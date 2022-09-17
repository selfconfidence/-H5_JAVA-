package com.manyun.admin.controller;


import com.manyun.admin.domain.dto.UpdateBalanceDto;
import com.manyun.admin.domain.dto.UpdateWithDrawDto;
import com.manyun.admin.domain.query.SystemWithdrawQuery;
import com.manyun.admin.domain.vo.SystemWithdrawVo;
import com.manyun.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.manyun.common.security.annotation.RequiresPermissions;
import com.manyun.admin.domain.CntSystemWithdraw;
import com.manyun.admin.service.ICntSystemWithdrawService;
import com.manyun.common.core.web.controller.BaseController;
import com.manyun.common.core.web.page.TableDataInfo;

/**
 * 系统余额提现Controller
 *
 * @author yanwei
 * @date 2022-09-17
 */
@RestController
@RequestMapping("/withdraw")
@Api(tags = "系统余额提现apis")
public class CntSystemWithdrawController extends BaseController
{
    @Autowired
    private ICntSystemWithdrawService cntSystemWithdrawService;

    /**
     * 查询系统余额提现列表
     */
    //@RequiresPermissions("admin:withdraw:list")
    @GetMapping("/list")
    @ApiOperation("查询系统余额提现列表")
    public TableDataInfo<SystemWithdrawVo> list(SystemWithdrawQuery systemWithdrawQuery)
    {
        return cntSystemWithdrawService.selectCntSystemWithdrawList(systemWithdrawQuery);
    }

    @PostMapping("/updateWithdrawStatus")
    @ApiOperation("修改打款状态")
    public R updateWithdrawStatus(@RequestBody UpdateWithDrawDto withDrawDto)
    {
        return toResult(cntSystemWithdrawService.updateWithdrawStatus(withDrawDto));
    }

}
