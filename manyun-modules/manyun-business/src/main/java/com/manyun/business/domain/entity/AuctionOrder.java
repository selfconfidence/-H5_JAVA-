package com.manyun.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 拍卖订单表
 * </p>
 *
 * @author yanwei
 * @since 2022-06-17
 */
@TableName("cnt_auction_order")
@ApiModel(value = "AuctionOrder对象", description = "拍卖订单表")
@Data
public class AuctionOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("创建人")
    private String createdBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty("更新人")
    private String updatedBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedTime;

    @ApiModelProperty("商品类型;1藏品，2盲盒")
    private Integer goodsType;

    @ApiModelProperty("商品id")
    private String goodsId;

    @ApiModelProperty("商品名")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImg;

    @ApiModelProperty("送拍id")
    private String sendAuctionid;

    @ApiModelProperty("当前价")
    private BigDecimal nowPrice;

    @ApiModelProperty("一口价")
    private BigDecimal soldPrice;

    @ApiModelProperty("起拍价")
    private BigDecimal startPrice;

    @ApiModelProperty("保证金")
    private BigDecimal margin;

    @ApiModelProperty("佣金")
    private BigDecimal commission;

    @ApiModelProperty("拍卖状态;1竞拍中，2待支付，3已支付，4已违约")
    private Integer auctionStatus;

}
