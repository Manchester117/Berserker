package com.berserker.architecture.engine.core;

import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@PropertySource(value = {"classpath:application.yml"}, encoding="UTF-8")
public class EngineParamLoader {
    @Value("${jmeterSetting.jmeter-home}")
    private String jmeterHomePath;                      // Jmeter的路径
    @Value("${jmeterSetting.jmeter-properties}")
    private String jmeterPropertiesPath;                // Jmeter配置文件的路径

    /**
     * @description                     运行前设置JmeterEngine的参数
     */
    public void setEngineParam() {
        // 初始化参数,日志,地区等信息
        JMeterUtils.setJMeterHome(jmeterHomePath);
        JMeterUtils.loadJMeterProperties(jmeterPropertiesPath);
        JMeterUtils.initLocale();
        // 从Jmeter3.2之后使用log4j2,不需要使用initLogging()
        // JMeterUtils.initLogging();
        // 参数和插件的加载
        try {
            SaveService.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
