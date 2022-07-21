package com.manyun.admin.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.manyun.common.log.annotation.Log;
import com.manyun.common.log.enums.BusinessType;
import com.manyun.common.security.annotation.RequiresPermissions;
import com.manyun.admin.domain.CntBanner;
import com.manyun.admin.service.ICntBannerService;
import com.manyun.common.core.web.controller.BaseController;
import com.manyun.common.core.web.domain.AjaxResult;
import com.manyun.common.core.utils.poi.ExcelUtil;
import com.manyun.common.core.web.page.TableDataInfo;

@RestController
@RequestMapping("/banner")
@Api(tags = "轮播图apis")
public class CntBannerController extends BaseController
{
    @Autowired
    private ICntBannerService cntBannerService;

    //@RequiresPermissions("admin:banner:list")
    @GetMapping("/list")
    @ApiOperation("查询轮播列表")
    public TableDataInfo list(CntBanner cntBanner)
    {
        startPage();
        List<CntBanner> list = cntBannerService.selectCntBannerList(cntBanner);
        return getDataTable(list);
    }

    //@RequiresPermissions("admin:banner:query")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取轮播详细信息")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(cntBannerService.selectCntBannerById(id));
    }

    //@RequiresPermissions("admin:banner:add")
    @Log(title = "轮播", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增轮播")
    public AjaxResult add(@RequestBody CntBanner cntBanner)
    {
        return toAjax(cntBannerService.insertCntBanner(cntBanner));
    }

    //@RequiresPermissions("admin:banner:edit")
    @Log(title = "轮播", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改轮播")
    public AjaxResult edit(@RequestBody CntBanner cntBanner)
    {
        return toAjax(cntBannerService.updateCntBanner(cntBanner));
    }

    //@RequiresPermissions("admin:banner:remove")
    @Log(title = "轮播", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除轮播")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(cntBannerService.deleteCntBannerByIds(ids));
    }
}
