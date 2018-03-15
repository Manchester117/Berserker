package com.bushmaster.architecture.engine;

import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EngineParamLoader {
    public static void setEngineParam(String jmeterHomePath, String jmeterPropertiesPath) {
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
