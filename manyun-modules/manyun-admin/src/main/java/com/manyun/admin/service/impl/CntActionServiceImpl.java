package com.manyun.admin.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import com.manyun.admin.domain.query.ActionQuery;
import com.manyun.admin.domain.vo.CntActionVo;
import com.manyun.admin.mapper.CntActionTarMapper;
import com.manyun.common.core.utils.DateUtils;
import com.manyun.common.core.utils.uuid.IdUtils;
import com.manyun.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.manyun.admin.mapper.CntActionMapper;
import com.manyun.admin.domain.CntAction;
import com.manyun.admin.service.ICntActionService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 活动Service业务层处理
 *
 * @author yanwei
 * @date 2022-07-21
 */
@Service
public class CntActionServiceImpl implements ICntActionService
{
    @Autowired
    private CntActionMapper cntActionMapper;

    @Autowired
    private CntActionTarMapper actionTarMapper;

    /**
     * 查询活动
     *
     * @param id 活动主键
     * @return 活动
     */
    @Override
    public CntAction selectCntActionById(String id)
    {
        return cntActionMapper.selectCntActionById(id);
    }

    /**
     * 查询活动列表
     *
     * @param actionQuery 活动
     * @return 活动
     */
    @Override
    public List<CntActionVo> selectCntActionList(ActionQuery actionQuery)
    {
        return cntActionMapper.selectSearchActionList(actionQuery).stream().map(m ->{
            CntActionVo cntActionVo=new CntActionVo();
            BeanUtil.copyProperties(m,cntActionVo);
            return cntActionVo;
        }).collect(Collectors.toList());
    }

    /**
     * 新增活动
     *
     * @param cntAction 活动
     * @return 结果
     */
    @Override
    public int insertCntAction(CntAction cntAction)
    {
        cntAction.setId(IdUtils.getSnowflakeNextIdStr());
        cntAction.setCreatedBy(SecurityUtils.getUsername());
        cntAction.setCreatedTime(DateUtils.getNowDate());
        return cntActionMapper.insertCntAction(cntAction);
    }

    /**
     * 修改活动
     *
     * @param cntAction 活动
     * @return 结果
     */
    @Override
    public int updateCntAction(CntAction cntAction)
    {
        cntAction.setUpdatedBy(SecurityUtils.getUsername());
        cntAction.setUpdatedTime(DateUtils.getNowDate());
        return cntActionMapper.updateCntAction(cntAction);
    }

    /**
     * 批量删除活动
     *
     * @param ids 需要删除的活动主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCntActionByIds(String[] ids)
    {
        cntActionMapper.deleteCntActionByIds(ids);
        actionTarMapper.deleteCntActionTarByActionIds(ids);
        return 1;
    }
}
