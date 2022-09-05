package com.manyun.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manyun.admin.domain.query.OrderListQuery;
import com.manyun.admin.domain.vo.CntOrderVo;
import com.manyun.admin.domain.vo.OrderAmountsAddStatisticsVo;
import com.manyun.admin.domain.vo.OrderTypePercentageVo;
import com.manyun.admin.domain.vo.ValidOrderAddStatisticsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.manyun.admin.mapper.CntOrderMapper;
import com.manyun.admin.domain.CntOrder;
import com.manyun.admin.service.ICntOrderService;

import java.util.List;

/**
 * 订单Service业务层处理
 *
 * @author yanwei
 * @date 2022-07-26
 */
@Service
public class CntOrderServiceImpl extends ServiceImpl<CntOrderMapper,CntOrder> implements ICntOrderService
{
    @Autowired
    private CntOrderMapper cntOrderMapper;

    /**
     * 我的订单
     */
    @Override
    public List<CntOrderVo> myOrderList(String userId) {
        return cntOrderMapper.myOrderList(userId);
    }

    /**
     *查询近七日每日新增有效订单数
     */
    @Override
    public List<ValidOrderAddStatisticsVo> validOrderAddStatistics() {
        return cntOrderMapper.validOrderAddStatistics();
    }

    /**
     *查询近期日每日新增销售金额
     */
    @Override
    public List<OrderAmountsAddStatisticsVo> orderAmountsAddStatistics() {
        return cntOrderMapper.orderAmountsAddStatistics();
    }

    /**
     *查询订单各种状态占订单总量比例
     */
    @Override
    public List<OrderTypePercentageVo> orderTypePercentageList() {
        return cntOrderMapper.orderTypePercentageList();
    }

    /**
     * 订单列表
     * @return
     */
    @Override
    public List<CntOrderVo> orderList(OrderListQuery orderListQuery) {
        return cntOrderMapper.orderList(orderListQuery);
    }

}
