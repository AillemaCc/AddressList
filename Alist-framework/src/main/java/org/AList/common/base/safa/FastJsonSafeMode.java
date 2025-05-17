package org.AList.common.base.safa;

import org.springframework.beans.factory.InitializingBean;

/**
 * FastJson安全模式，开启后关闭类型隐式传递
 */
public class FastJsonSafeMode implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        System.setProperty("fastjson2.parser.safeMode", "true");
    }
}