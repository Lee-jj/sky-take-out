package com.sky.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {

        // 1. 处理各种业务异常情况（地址薄为空、购物车为空）
        AddressBook addressBook = AddressBook.builder().id(ordersSubmitDTO.getAddressBookId()).build();
        List<AddressBook> addressBookList = addressBookMapper.list(addressBook);
        if (addressBookList == null || addressBookList.size() == 0) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        
        // 2. 向订单表中插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBookList.get(0).getPhone());
        orders.setConsignee(addressBookList.get(0).getConsignee());
        orders.setUserId(userId);
        // 获取地址数据，添加到订单表中
        String address = getAddress(addressBookList.get(0));
        orders.setAddress(address);
        
        orderMapper.insert(orders);

        List<OrderDetail> orderDetailsList = new ArrayList<>();
        // 3. 向订单明细表中插入多条数据
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailsList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailsList);

        // 4. 清空当前用户的购物车数据
        shoppingCartMapper.clean(userId);

        // 5. 封装VO返回值
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
    }

    /**
     * 获取地址字符串
     * @param addressBook
     * @return
     */
    private String getAddress(AddressBook addressBook) {
        return addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        
        JSONObject jsonObject = new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        // 支付成功，修改订单状态
        paySuccess(ordersPaymentDTO.getOrderNumber());

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    @Override
    public void paySuccess(String outTradeNo) {
        // 根据订单id查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);
        // 更新订单状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
               .id(ordersDB.getId())
               .status(Orders.TO_BE_CONFIRMED)
               .payStatus(Orders.PAID)
               .checkoutTime(LocalDateTime.now())
               .build();
        orderMapper.update(orders);
    }

    /**
     * 查询用户历史订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult page(Integer page, Integer pageSize, Integer status) {
        PageHelper.startPage(page, pageSize);
        OrdersPageQueryDTO orderPageQueryDTO = new OrdersPageQueryDTO();
        orderPageQueryDTO.setStatus(status);
        orderPageQueryDTO.setUserId(BaseContext.getCurrentId());
        // 分页查询订单
        Page<Orders> p = orderMapper.page(orderPageQueryDTO);

        // 封装查询结果
        List<OrderVO> list = new ArrayList<>();
        if (p != null && p.size() > 0) {
            for (Orders orders: p) {
                Long orderId = orders.getId();
                // 根据订单id查找订单详细表
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }

        return new PageResult(p.getTotal(), list);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO detail(Long id) {
        // 根据订单id查询订单
        Orders orders = orderMapper.getById(id);

        // 根据订单id查询订单详情
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @Override
    public void cancel(Long id) {
        //         业务规则：
        // - 待支付和待接单状态下，用户可直接取消订单
        // - 商家已接单状态下，用户取消订单需电话沟通商家
        // - 派送中状态下，用户取消订单需电话沟通商家
        // - 如果在待接单状态下取消订单，需要给用户退款
        // - 取消订单后需要将订单状态修改为“已取消”

        Orders ordersDB = orderMapper.getById(id);

        if (ordersDB == null) 
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        if (ordersDB.getStatus() > 2) 
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        Orders orders = new Orders();
        orders.setId(id);
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            // 待接单状态，需要退款
            // 不能调用微信支付退款接口，直接修改支付状态为退款
            orders.setPayStatus(Orders.REFUND);
        }

        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason("用户取消");
        orderMapper.update(orders);
    }

    /**
     * 再来一单
     * @param id
     * @return
     */
    @Override
    public void repetition(Long id) {
        // 再来一单就是将原订单中的商品重新加入到购物车中
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        List<ShoppingCart> shoppingCarts = orderDetails.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartMapper.insertBatch(shoppingCarts);
    }

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> p = orderMapper.page(ordersPageQueryDTO);
        List<OrderVO> list = new ArrayList<>();
        if (p != null && p.size() > 0) {
            for (Orders orders: p) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishes(orders);
                orderVO.setOrderDishes(orderDishes);
                list.add(orderVO);
            }
        }
        return new PageResult(p.getTotal(), list);
    }

    /**
     * 根据订单id获取菜品信息的字符串
     * @param orders
     * @return
     */
    private String getOrderDishes(Orders orders) {
        // 获取订单中的菜品列表
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getId());
        List<String> orderDetailList = orderDetails.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());
        return String.join("", orderDetailList);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistic() {
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        return orderStatisticsVO;
    }

    /**
     * 确认订单
     * @param ordersConfirmDTO
     * @return
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder().id(ordersConfirmDTO.getId()).status(Orders.CONFIRMED).build();
        orderMapper.update(orders);
    }

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     * @return
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // 根据id查询订单，只有状态2才可以拒单
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());
        if (ordersDB == null || ! ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) 
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        
        Orders orders = new Orders();
        if (ordersDB.getPayStatus().equals(Orders.PAID))
            orders.setPayStatus(Orders.REFUND);
        
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setId(ordersRejectionDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 商家取消订单
     * @return
     */
    @Override
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) {
        // 商家取消订单权限高，何种情况都可以直接取消，用户付钱了旧退款后取消
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Orders orders = new Orders();
        if (ordersDB.getPayStatus().equals(Orders.PAID)) {
            // 用户已经付钱，执行退款
            // 无法调用小程序接口，直接修改数据库状态
            orders.setPayStatus(Orders.REFUND);
        }

        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @Override
    public void delivery(Long id) {
        Orders ordersDB = orderMapper.getById(id);
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED))
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }
    
}
