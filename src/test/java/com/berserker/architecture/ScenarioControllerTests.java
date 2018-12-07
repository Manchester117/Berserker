package com.berserker.architecture;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArchitectureApplication.class)
@WebAppConfiguration
public class ScenarioControllerTests {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mock;

    @Before
    public void before() {
        mock = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testScenarioIndex() throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("offset", "0");
        paramMap.put("limit", "15");
        paramMap.put("scenarioName", "");
        paramMap.put("status", "");
        String requestParamJson = JSONObject.toJSONString(paramMap);

        String responseData = mock.perform(MockMvcRequestBuilders.post("/scenarioInfoList")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestParamJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("total")))
                .andReturn().getResponse().getContentAsString();

        // 格式化输出结果,这里使用Jackson
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        // 可以直接将FastJson的JSON对象用Jackson格式化输出
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
    }

    @Test
    public void testAddScenario() throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("scenarioName", "第16个测试场景");
        paramMap.put("scenarioDescription", "第16个测试场景描述");
        paramMap.put("createTime", "2018-02-22 17:44:30");
        paramMap.put("numThreads", "150");
        paramMap.put("rampUp", "100");
        paramMap.put("duration", "1800");
        paramMap.put("status", "1");
        String requestParamJson = JSONObject.toJSONString(paramMap);

        String responseData = mock.perform(MockMvcRequestBuilders.post("/addScenarioInfo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestParamJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Success")))
                .andReturn().getResponse().getContentAsString();

        // 格式化输出结果,这里使用Jackson
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        // 可以直接将FastJson的JSON对象用Jackson格式化输出
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
    }

    @Test
    public void testModScenario() throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("id", "5");
        paramMap.put("scenarioName", "第5个测试场景");
        paramMap.put("scenarioDescription", "第5个测试场景描述");
        paramMap.put("createTime", "2018-02-23 17:44:30");
        paramMap.put("numThreads", "150");
        paramMap.put("rampUp", "100");
        paramMap.put("duration", "2400");
        paramMap.put("status", "0");
        String requestParamJson = JSONObject.toJSONString(paramMap);


        String responseData = mock.perform(MockMvcRequestBuilders.post("/modScenarioInfo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestParamJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Success")))
                .andReturn().getResponse().getContentAsString();

        // 格式化输出结果,这里使用Jackson
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        // 可以直接将FastJson的JSON对象用Jackson格式化输出
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
    }

    @Test
    public void testDelScenario() throws Exception {
        String responseData = mock.perform(MockMvcRequestBuilders.post("/delScenarioInfo")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "16"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Success")))
                .andReturn().getResponse().getContentAsString();

        // 格式化输出结果,这里使用Jackson
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        // 可以直接将FastJson的JSON对象用Jackson格式化输出
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
    }

    @After
    public void after() {}
}
