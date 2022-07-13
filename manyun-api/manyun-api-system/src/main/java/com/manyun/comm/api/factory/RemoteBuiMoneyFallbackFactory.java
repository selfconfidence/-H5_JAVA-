package com.manyun.comm.api.factory;

import com.manyun.comm.api.RemoteBuiMoneyService;
import com.manyun.comm.api.RemoteBuiUserService;
import com.manyun.comm.api.domain.dto.CntUserDto;
import com.manyun.comm.api.model.LoginPhoneCodeForm;
import com.manyun.comm.api.model.LoginPhoneForm;
import com.manyun.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 文件服务降级处理
 * 
 * @author ruoyi
 */
@Component
public class RemoteBuiMoneyFallbackFactory implements FallbackFactory<RemoteBuiMoneyService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteBuiMoneyFallbackFactory.class);

    @Override
    public RemoteBuiMoneyService create(Throwable throwable)
    {
        log.error("业务用户服务调用失败:{}", throwable.getMessage());
        return new RemoteBuiMoneyService() {
            @Override
            public R initUserMoney(String userId,String source) {
                return R.fail("操作失败:" + throwable.getMessage());
            }
        };
    }
}