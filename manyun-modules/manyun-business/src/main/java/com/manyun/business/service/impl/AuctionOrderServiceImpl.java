package com.manyun.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.manyun.business.domain.dto.AuctionOrderCreateDto;
import com.manyun.business.domain.dto.LogInfoDto;
import com.manyun.business.domain.entity.AuctionOrder;
import com.manyun.business.domain.entity.AuctionPrice;
import com.manyun.business.domain.entity.AuctionSend;
import com.manyun.business.domain.entity.Money;
import com.manyun.business.domain.query.AuctionOrderQuery;
import com.manyun.business.domain.vo.AuctionOrderVo;
import com.manyun.business.mapper.AuctionOrderMapper;
import com.manyun.business.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manyun.common.core.constant.BusinessConstants;
import com.manyun.common.core.domain.Builder;
import com.manyun.common.core.enums.AuctionSendStatus;
import com.manyun.common.core.enums.AuctionStatus;
import com.manyun.common.core.web.page.TableDataInfo;
import com.manyun.common.core.web.page.TableDataInfoUtil;
import org.apache.commons.compress.utils.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.manyun.common.core.constant.BusinessConstants.LogsTypeConstant.POLL_SOURCE;
import static com.manyun.common.core.constant.BusinessConstants.LogsTypeConstant.PULL_SOURCE;
import static com.manyun.common.core.constant.BusinessConstants.ModelTypeConstant.MONEY_TYPE;

/**
 * <p>
 * 拍卖订单表 服务实现类
 * </p>
 *
 * @author yanwei
 * @since 2022-06-17
 */
@Service
public class AuctionOrderServiceImpl extends ServiceImpl<AuctionOrderMapper, AuctionOrder> implements IAuctionOrderService {

    @Autowired
    private ISystemService systemService;

    @Autowired
    private IAuctionSendService auctionSendService;

    @Autowired
    private IUserBoxService userBoxService;

    @Autowired
    private IUserCollectionService userCollectionService;

    @Autowired
    private IMoneyService moneyService;

    @Autowired
    @Lazy
    private IAuctionPriceService auctionPriceService;

    @Autowired
    private ILogsService logsService;

    @Override
    public TableDataInfo<AuctionOrderVo> myAuctionOrderList(AuctionOrderQuery orderQuery, String userId) {
        List<AuctionOrder> list = list(Wrappers.<AuctionOrder>lambdaQuery().eq(AuctionOrder::getToUserId, userId)
                .eq(orderQuery.getAuctionStatus() != null && orderQuery.getAuctionStatus() != 0, AuctionOrder::getAuctionStatus, orderQuery.getAuctionStatus())
                .orderByDesc(AuctionOrder::getCreatedTime));
        return TableDataInfoUtil.pageTableDataInfo(list.parallelStream().map(this::providerAuctionOrderVo).collect(Collectors.toList()), list);
    }

    private AuctionOrderVo providerAuctionOrderVo(AuctionOrder auctionOrder) {
        AuctionOrderVo auctionOrderVo = Builder.of(AuctionOrderVo::new).build();
        BeanUtil.copyProperties(auctionOrder, auctionOrderVo);
        return auctionOrderVo;
    }


    @Override
    public String createAuctionOrder(AuctionOrderCreateDto auctionOrderCreateDto, Consumer<String> consumer) {
        return createOrder(auctionOrderCreateDto, systemService.getVal(BusinessConstants.SystemTypeConstant.AUCTION_ORDER_TIME, Integer.class), consumer);
    }


    //创建订单
    private String createOrder(AuctionOrderCreateDto auctionOrderCreateDto, Integer serviceVal, Consumer<String> consumer) {

        AuctionSend auctionSend = auctionSendService.getById(auctionOrderCreateDto.getSendAuctionId());

        AuctionOrder auctionOrder = Builder.of(AuctionOrder::new).build();
        BeanUtil.copyProperties(auctionOrderCreateDto, auctionOrder);
        auctionOrder.createD(auctionOrderCreateDto.getToUserId());

        String idStr = IdUtil.getSnowflake().nextIdStr();
        auctionOrder.setId(idStr);

        String orderNo = IdUtil.objectId();
        auctionOrder.setOrderNo(orderNo);
        //修改订单为待支付状态
        auctionOrder.setBuiId(auctionSend.getMyGoodsId());
        BigDecimal commission = auctionSend.getNowPrice().multiply(systemService.getVal(BusinessConstants.SystemTypeConstant.COMMISSION_SCALE, BigDecimal.class)).setScale(2, RoundingMode.DOWN);
        auctionOrder.setCommission(commission);
        auctionOrder.setMargin(auctionSend.getMargin());
        auctionOrder.setAuctionStatus(AuctionStatus.WAIT_PAY.getCode());
        auctionOrder.setPayTime(LocalDateTime.now());
        auctionOrder.setEndTime(LocalDateTime.now().plusMinutes(serviceVal));
        save(auctionOrder);
        consumer.accept(idStr);
        return orderNo;
    }

    @Override
    public List<AuctionOrder> checkUnpaidOrder(String payUserId) {
        return this.list(Wrappers.<AuctionOrder>lambdaQuery().eq(AuctionOrder::getToUserId, payUserId)
                .eq(AuctionOrder::getAuctionStatus, AuctionStatus.WAIT_PAY.getCode()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void notifyPaySuccess(String outHost, String userId) {
        AuctionOrder auctionOrder = getOne(Wrappers.<AuctionOrder>lambdaQuery().eq(AuctionOrder::getOrderNo, outHost));
        String info = StrUtil.format("购买成功,本次消费{}，来源为拍卖市场", auctionOrder.getOrderAmount().toString());
        Assert.isTrue(Objects.nonNull(auctionOrder),"找不到对应订单编号!");
        Assert.isTrue(AuctionStatus.WAIT_PAY.getCode().equals(auctionOrder.getAuctionStatus()),"订单状态有误,请核实!");
        //更改订单状态

        auctionOrder.setAuctionStatus(AuctionStatus.PAY_SUCCESS.getCode());
        auctionOrder.updateD(auctionOrder.getToUserId());
        updateById(auctionOrder);

        //更改送拍状态
        AuctionSend auctionSend = auctionSendService.getById(auctionOrder.getSendAuctionId());
        auctionSend.setAuctionSendStatus(AuctionSendStatus.BID_SUCCESS.getCode());
        auctionSendService.updateById(auctionSend);

        //更改出价状态
        List<AuctionPrice> list = auctionPriceService.list(Wrappers.<AuctionPrice>lambdaQuery()
                .eq(AuctionPrice::getAuctionSendId, auctionSend.getId()).orderByDesc(AuctionPrice::getBidPrice));
        AuctionPrice auctionPrice = list.get(0);
        auctionPrice.setAuctionStatus(AuctionStatus.PAY_SUCCESS.getCode());
        auctionPriceService.updateById(auctionPrice);

        //退还买方保证金
        Money buyerMoney = moneyService.getOne(Wrappers.<Money>lambdaQuery().eq(Money::getUserId, userId));
        buyerMoney.setMoneyBalance(buyerMoney.getMoneyBalance().add(auctionOrder.getMargin()));
        moneyService.updateById(buyerMoney);
        logsService.saveLogs(LogInfoDto.builder().buiId(userId).jsonTxt("退还保证金").formInfo(auctionSend.getMargin().toString()).isType(PULL_SOURCE).modelType(MONEY_TYPE).build());

        //扣除佣金,剩余钱加给卖方   需要后台审核
        Money sellerMoney = moneyService.getOne(Wrappers.<Money>lambdaQuery().eq(Money::getUserId, auctionSend.getUserId()));
        BigDecimal subtract = auctionOrder.getNowPrice().subtract(auctionOrder.getCommission());
        sellerMoney.setMoneyBalance(sellerMoney.getMoneyBalance().add(subtract));
        moneyService.updateById(sellerMoney);
        logsService.saveLogs(LogInfoDto.builder().buiId(userId).jsonTxt("拍卖成功").formInfo(auctionOrder.getNowPrice().toString()).isType(PULL_SOURCE).modelType(MONEY_TYPE).build());
        logsService.saveLogs(LogInfoDto.builder().buiId(userId).jsonTxt("扣除佣金").formInfo(auctionOrder.getCommission().toString()).isType(POLL_SOURCE).modelType(MONEY_TYPE).build());

        Integer goodsType = auctionOrder.getGoodsType();

        //绑定藏品关系
        if (goodsType == 1) {
            // 藏品
            //userCollectionService.bindCollection(auctionOrder.getToUserId(),auctionOrder.getGoodsName(),auctionOrder.getGoodsId(),info,1);
            userCollectionService.tranCollection(auctionOrder.getFromUserId(),auctionOrder.getToUserId(),auctionOrder.getBuiId());
            return;
        }

        if (goodsType == 2) {
            // 盲盒
            userBoxService.bindBox(auctionOrder.getToUserId(),auctionOrder.getBuiId(),info,1);
            return;
        }

        throw new IllegalStateException("not fount order good_type = " + goodsType);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void timeCancelAuction() {
        List<AuctionOrder> auctionOrderList = list(Wrappers.<AuctionOrder>lambdaQuery().eq(AuctionOrder::getAuctionStatus, AuctionStatus.WAIT_PAY.getCode()).lt(AuctionOrder::getEndTime, LocalDateTime.now()));
        if (auctionOrderList.isEmpty()) return;
        List<AuctionSend> auctionSendList = auctionSendService.list(Wrappers.<AuctionSend>lambdaQuery().in(AuctionSend::getAuctionOrderId, auctionOrderList.parallelStream().map(item -> item.getId()).collect(Collectors.toSet()))
                .eq(AuctionSend::getAuctionSendStatus, AuctionSendStatus.WAIT_PAY.getCode()));
        Set<String> auctionSendOrderIds = Sets.newHashSet();
        if (!auctionSendList.isEmpty()) {
            auctionSendOrderIds.addAll(auctionSendList.parallelStream().map(item -> item.getAuctionOrderId()).collect(Collectors.toSet()));
            auctionSendService.reloadAuctionSend(auctionSendList);
        }

        List<AuctionOrder> updateOrder = auctionOrderList.parallelStream().map(item -> {
            AuctionPrice auctionPrice = auctionPriceService.getById(item.getAuctionPriceId());
            auctionPrice.setAuctionStatus(AuctionStatus.BID_BREAK.getCode());
            auctionPriceService.updateById(auctionPrice);
            item.setAuctionStatus(AuctionStatus.BID_BREAK.getCode());
            item.updateD(item.getToUserId());
            return item;
        }).collect(Collectors.toList());
        updateBatchById(updateOrder);

    }

}
