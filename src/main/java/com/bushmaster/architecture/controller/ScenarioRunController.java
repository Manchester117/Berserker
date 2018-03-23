package com.bushmaster.architecture.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.service.ScenarioRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class ScenarioRunController {
    @Autowired
    private ScenarioRunService runService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @GetMapping(path = "/scenarioStartRun")
    public String scenarioStartRun(Model model, @RequestParam("scenarioId") String scenarioId) {
        runService.scenarioStartRun(Integer.parseInt(scenarioId));
        return "scenarioInfoChart";
    }

    @GetMapping(path = "/scenarioStopRun")
    public @ResponseBody JSONObject scenarioStopRun() {
        Map<String, Object> stopRunMessage = runService.scenarioStopRun();
        return JSONObject.parseObject(JSON.toJSONString(stopRunMessage));
    }

//    @SendTo(value = "/topic/notice")
//    public String greeting() {
//        SamplerQueue queue = SamplerQueue.getInstance();
//        SampleResultInfo samplerInfo = queue.pop();
//
//        System.out.println(samplerInfo);
//        return samplerInfo.toString();
//    }

//    @GetMapping(path = "/index")
//    public String index() {
//        return "index";
//    }

//    // 简化的WebSocket接收发送方法
//    @MessageMapping("/change-notice")
//    @SendTo("/topic/notice")
//    public String greeting(String value) {
//        System.out.println(value);
//        return value;
//    }

//    @GetMapping(path = "receive")
//    public String receive() {
//        return "receive";
//    }

    // 原始的WebSocket接收发送方法
//    @MessageMapping("/change-notice")
//    public void greeting(String value) {
//        System.out.println(value);
//        this.simpMessagingTemplate.convertAndSend("/topic/notice", value);
//    }
}
