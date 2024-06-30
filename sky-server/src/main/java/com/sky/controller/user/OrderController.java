package com.sky.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.annotation.RateLimiter;
import com.sky.dto.OrderSubmitDTOv1;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.TOrderRush;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "用户订单管理相关接口")
@Slf4j
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户提交订单,{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("订单支付，{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单，{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 查询用户历史订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("查询用户历史订单")
    public Result<PageResult> page(Integer page, Integer pageSize, Integer status) {
        log.info("查询用户历史订单,page:{},pageSize:{},status:{}", page, pageSize, status);
        PageResult pageResult = orderService.page(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> detail(@PathVariable Long id) {
        log.info("查询订单详情，{}", id);
        OrderVO orderVO = orderService.detail(id);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancel(@PathVariable Long id) throws Exception {
        log.info("取消订单，{}", id);
        orderService.cancel(id);
        return Result.success();
    }

    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id) {
        log.info("再来一单，{}", id);
        orderService.repetition(id);
        return Result.success();
    }

    /**
     * 用户催单
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("用户催单")
    public Result reminder(@PathVariable Long id) {
        log.info("用户催单，{}", id);
        orderService.reminder(id);
        return Result.success();
    }

    /**
     * 下单
     * @param orderSubmitDTO
     * @return
     */
    @PostMapping("/submit/v1")
    @ApiOperation("用户提交订单v1")
    public Result<OrderSubmitVO> submitV1(@RequestBody OrderSubmitDTOv1 orderSubmitDTO) {
        log.info("用户提交订单{}", orderSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitv1(orderSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 分页查询当前可接订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/avaliableOrder")
    @ApiOperation("查询当前可接订单")
    public Result<PageResult> pageAvailableOrder(Integer page, Integer pageSize) {
        log.info("查询当前可接订单: page: {}, pageSize: {}", page, pageSize);
        PageResult pageResult = orderService.pageAvaliableOrder(page, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 用户抢单接口
     *      优化前5000并发qps：236 /sec，有超卖问题
     *      使用Redis缓存订单后5000并发qps：499 /sec
     *      使用Redis缓存订单+RabbiMQ异步更新数据库后5000并发qps：625 /sec
     * @param id
     * @return
     */
    @RateLimiter(value = 16, timeout = 100)
    @PostMapping("/seckill/{id}")
    @ApiOperation("用户抢单")
    public Result doSeckill(@PathVariable Long id) {
        log.info("用户抢单，id:{}", id);
        orderService.doSeckill(id);
        return Result.success();
    }

    /**
     * 获取用户抢单结果
     * @param orderId
     * @return seckillOrderId: 成功, -1 抢单失败, 0 抢单中
     */
    @GetMapping("/seckillOrder")
    @ApiOperation("获取抢单结果")
    public Result<Long> getSeckillResult(Long orderId) {
        log.info("获取抢单结果, orderId:{}", orderId);
        Long seckillOrderId = orderService.getSeckillResult(orderId);
        return Result.success(seckillOrderId);
    }
}
