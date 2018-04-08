package com.bushmaster.architecture;

import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.service.ScenarioInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArchitectureApplication.class)
@WebAppConfiguration
public class ScenarioServiceTests {
    @Autowired
    private ScenarioInfoService scenarioInfoService;

    @Before
    public void before() {
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testGetScenarioInfoByPageList() {
        Map<String, Object> result = scenarioInfoService.getScenarioInfoByPageList(0, 15, "");
        String jsonResult = JSONObject.toJSONString(result);
        System.out.println(jsonResult);
    }

//    @Test
//    public void testAddScenarioInfo() throws Exception {
//        for (int i = 0; i < 50; ++i) {
//            ScenarioInfo scenarioInfo = new ScenarioInfo();
//            scenarioInfo.setScenarioName("第" + i + "个测试场景");
//            scenarioInfo.setScenarioDescription("第" + i + "个测试场景描述");
//            scenarioInfo.setCreateTime(new Date());
//            scenarioInfo.setNumThreads(200);
//            scenarioInfo.setRampUp(180);
//            scenarioInfo.setDuration(5600);
//            scenarioInfo.setStatus(true);
//
//            Map<String, Object> result = scenarioInfoService.addScenarioInfo(scenarioInfo);
//            // 格式化输出结果,这里使用Jackson
//            ObjectMapper mapper = new ObjectMapper();
//            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
//        }
//    }

//    @Test
//    public void testModScenarioInfo() throws Exception {
//        ScenarioInfo scenarioInfo = new ScenarioInfo();
//        scenarioInfo.setId(5);
//        scenarioInfo.setScenarioName("第5个测试场景-修改");
//        scenarioInfo.setScenarioDescription("第5个测试场景描述-修改");
//        scenarioInfo.setCreateTime(new Date());
//        scenarioInfo.setNumThreads(200);
//        scenarioInfo.setRampUp(600);
//        scenarioInfo.setDuration(4800);
//        scenarioInfo.setStatus(true);
//
//        Map<String, Object> result = scenarioInfoService.modScenarioInfo(scenarioInfo);
//        // 格式化输出结果,这里使用Jackson
//        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
//    }

    @Test
    public void testDelScenarioInfo() throws Exception {
        Map<String, Object> result = scenarioInfoService.delScenarioInfo(16);
        // 格式化输出结果,这里使用Jackson
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
    }

    @After
    public void after() {
    }
}
