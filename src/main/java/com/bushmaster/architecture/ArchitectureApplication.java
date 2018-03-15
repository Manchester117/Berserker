package com.bushmaster.architecture;

import com.github.pagehelper.PageHelper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Date;
import java.util.Properties;

//@SpringBootApplication
@SpringBootApplication(exclude = MongoAutoConfiguration.class)      // Springboot启动时不要默认加载MongoDriver
@EnableTransactionManagement
@MapperScan("com.bushmaster.architecture.mapper")
public class ArchitectureApplication {
    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("dialect", "mysql");                 // 使用MySQL的方言
        pageHelper.setProperties(properties);

        return pageHelper;
    }

    public static void main(String[] args) {
        SpringApplication.run(ArchitectureApplication.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println(this.mapper.getScenarioInfoList("个", 1).size());

//        ScenarioInfo info_1 = new ScenarioInfo();
//        info_1.setId(1);
//        info_1.setScenarioDescription("第一个场景描述");
//        info_1.setRampUpSeconds(30);
//        info_1.setDuration(1200);
//        info_1.setStatus(true);
//        System.out.println(this.mapper.modifyScenarioInfo(info_1));

//        ScenarioInfo info_2 = new ScenarioInfo();
//        info_2.setScenarioName("第四个场景");
//        info_2.setScenarioDescription("第四个场景描述");
//        info_2.setCreateTime(new Date());
//        info_2.setConcurrentNum(20);
//        info_2.setRampUpSeconds(20);
//        info_2.setDuration(1800);
//        info_2.setStatus(Boolean.TRUE);
//        System.out.println(this.mapper.insertScenarioInfo(info_2));

//        ScriptFileInfo scriptInfo = new ScriptFileInfo();
//        scriptInfo.setScriptFileName("第一个脚本");
//        scriptInfo.setScriptFilePath("D:\\Script\\第一个脚本");
//        scriptInfo.setUploadTime(new Date());
//        scriptInfo.setScenarioId(1);
//
//        scriptMapper.insertScriptFileInfo(scriptInfo);

//        ParamFileInfo paramInfo = new ParamFileInfo();
//        paramInfo.setParamFileName("第一个参数化文件");
//        paramInfo.setParamFilePath("D:\\Param\\第一个参数化文件");
//        paramInfo.setUploadTime(new Date());
//        paramInfo.setScenarioId(1);
//
//        paramMapper.insertParamFileInfo(paramInfo);
//    }
}
