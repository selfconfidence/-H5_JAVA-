package com.manyun.business.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("反馈举报提交表单")
public class FeedbackForm {

    @ApiModelProperty(value = "区块链地址")
    @NotBlank(message = "区块链地址不能为空")
    private String linkAddr;

    @ApiModelProperty(value = "反馈类型")
    @NotBlank(message = "反馈类型不能为空")
    private String feedbackType;

    @ApiModelProperty(value = "建议内容")
    @NotBlank(message = "建议内容不能为空")
    private String opinionContent;

    @ApiModelProperty(value = "反馈图片")
    @NotBlank(message = "反馈图片不能为空")
    private String feedbackImg;

}
