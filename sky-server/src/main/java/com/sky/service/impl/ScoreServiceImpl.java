package com.sky.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.context.BaseContext;
import com.sky.entity.TScore;
import com.sky.entity.TUser;
import com.sky.mapper.DishMapper;
import com.sky.mapper.ScoreMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ScoreService;

@Service
public class ScoreServiceImpl implements ScoreService{

    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户打分
     * @param dishId
     * @param score
     * @return
     */
    @Override
    public void score(Long dishId, Integer score) {
        // 根据用户id和dishid查询是否有打分记录
        Long userId = BaseContext.getCurrentId();
        TScore tScore = scoreMapper.getScoreByUserIdAndDishId(userId, dishId);

        if (tScore != null) {
            // 如果有则修改
            scoreMapper.updateByUserIdAndDishId(userId, dishId, score);
        } else {
            // 如果没有则添加
            scoreMapper.insert(userId, dishId, score);
        }
    }

    /**
     * 获取推荐菜品列表
     * @return
     */
    @Override
    public List<Long> getRecommendation() {
        // 获取用户id
        Long userId = BaseContext.getCurrentId();
        // 获取该用户对所有菜品的打分情况，未打分的评分设置为0
        long dishCount = dishMapper.getTotalNum();
        Long[] scoreList1 = new Long[(int)dishCount + 1];
        for (long dishId = 0L; dishId <= dishCount; ++dishId) {
            TScore tScore = scoreMapper.getScoreByUserIdAndDishId(userId, dishId);
            scoreList1[(int)dishId] = tScore == null ? 0L : tScore.getScore();
        }

        Map<Double, Long> distances = new TreeMap<>();

        // 获取剩余用户id列表
        // TODO 目前先测试前10个用户，用户数量太多需要改进接口，否则响应速度太慢
        List<TUser> userList = userMapper.getWithout(userId);
        // 获取该用户对所有菜品的打分情况
        Long[] scoreList2 = new Long[(int)dishCount + 1];
        for (TUser user : userList) {
            Arrays.fill(scoreList2, 0L);
            for (long dishId = 0L; dishId <= dishCount; ++dishId) {
                TScore tScore = scoreMapper.getScoreByUserIdAndDishId(user.getId(), dishId);
                scoreList2[(int)dishId] += tScore == null ? 0L : tScore.getScore();
            }
            double distance = pearson_dis(scoreList1, scoreList2);
            distances.put(distance, user.getId());
        }

        // 获取最近邻
        Long nearestId = distances.values().iterator().next();

        // 找到前5个最近邻吃过而当前用户没吃过的菜品进行推荐
        List<Long> dishIdList = new ArrayList<>();
        for (long dishId = 0L; dishId <= dishCount; ++dishId) {
            if (dishIdList.size() >= 5) {
                break;
            }
            
            TScore tScore = scoreMapper.getScoreByUserIdAndDishId(nearestId, dishId);
            if (tScore != null && scoreList1[(int)dishId] == 0L) {
                dishIdList.add(dishId);
            }
        }
        // 如果没有合适的推荐菜品，则推荐最新的菜品
        if (dishIdList.isEmpty()) {
            dishIdList.add(dishCount);
        }
        return dishIdList;
    }

    /**
     * 计算2个打分序列间的pearson距离
     * @param rating1
     * @param rating2
     * @return
     */
    private double pearson_dis(Long[] rating1, Long[] rating2) {
        int n = rating1.length;
        List<Long> rating1ScoreCollect = Arrays.stream(rating1).collect(Collectors.toList());
        List<Long> rating2ScoreCollect = Arrays.stream(rating2).collect(Collectors.toList());

        double Ex= rating1ScoreCollect.stream().mapToDouble(x->x).sum();
        double Ey= rating2ScoreCollect.stream().mapToDouble(y->y).sum();
        double Ex2=rating1ScoreCollect.stream().mapToDouble(x->Math.pow(x,2)).sum();
        double Ey2=rating2ScoreCollect.stream().mapToDouble(y->Math.pow(y,2)).sum();
        double Exy= IntStream.range(0,n).mapToDouble(i->rating1ScoreCollect.get(i)*rating2ScoreCollect.get(i)).sum();
        double numerator=Exy-Ex*Ey/n;
        double denominator=Math.sqrt((Ex2-Math.pow(Ex,2)/n)*(Ey2-Math.pow(Ey,2)/n));
        if (denominator==0) return 0.0;
        return numerator/denominator;
    }

    
}
