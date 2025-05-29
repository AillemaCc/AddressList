package org.AList.common.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EncryptProperties {
    @Autowired
    private Environment environment;

    public String getEncryptPrefix(){
        return environment.getProperty("encrypt.prefix");
    }
    public String getEncryptKey(){
        return environment.getProperty("encrypt.key");
    }
}
