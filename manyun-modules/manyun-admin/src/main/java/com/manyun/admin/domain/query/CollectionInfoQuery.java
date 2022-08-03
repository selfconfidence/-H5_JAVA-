package com.manyun.admin.domain.query;

import com.manyun.common.core.web.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("条件查询对象")
@Data
public class CollectionInfoQuery extends PageQuery {

    @ApiModelProperty("发行方")
    private String publishOther;

}