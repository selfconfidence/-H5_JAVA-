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
 * 拍卖表
 * </p>
 *
 * @author yanwei
 * @since 2022-06-17
 */
@TableName("cnt_auction_send")
@ApiModel(value = "AuctionSend对象", description = "拍卖表")
@Data
public class AuctionSend implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("创建人")
    private String createdBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty("更新人")
    private String updatedBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedTime;

    @ApiModelProperty("送拍人id")
    private String userId;

    @ApiModelProperty("类型（藏品，盲盒）;1藏品，2盲盒")
    private Integer goodsType;

    @ApiModelProperty("商品id")
    private String goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("起拍价")
    private BigDecimal startPrice;

    @ApiModelProperty("一口价")
    private BigDecimal soldPrice;

    @ApiModelProperty("拍卖状态;1待开始，2竞拍中，3待支付，4已完成，5已违约，6已流拍")
    private Integer auctionStatus;

}
