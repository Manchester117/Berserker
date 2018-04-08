package com.bushmaster.architecture.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.domain.entity.ScenarioInfo;
import com.bushmaster.architecture.service.ScenarioInfoService;
import com.bushmaster.architecture.service.ScenarioRunService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class ScenarioRunController {
    @Autowired
    private ScenarioRunService runService;
    @Autowired
    private ScenarioInfoService infoService;

    @PostMapping(path = "/scenarioStartRunCheck")
    public @ResponseBody JSONObject scenarioStartRunCheck(@RequestParam("scenarioId") String scenarioId) {
        Map<String, Object> startRunInfo = new HashMap<>();
        ScenarioInfo scenarioInfo = infoService.getScenarioInfo(Integer.parseInt(scenarioId));
        // 如果并发数量|攀升时间|运行时间为空则提示用于先设定这些参数
        List<String> message = new ArrayList<>();
        if (Objects.isNull(scenarioInfo.getNumThreads())) {
            startRunInfo.put("enableRun", "False");
            message.add("请先设置并发数量");
        }
        if (Objects.isNull(scenarioInfo.getRampUp())) {
            startRunInfo.put("enableRun", "False");
            message.add("请先设置攀升时间");
        }
        if (Objects.isNull(scenarioInfo.getDuration())) {
            startRunInfo.put("enableRun", "False");
            message.add("请先设置运行时长");
        }
        // 如果有场景正在运行
        Boolean engineIsActive = runService.getEngineIsActive();
        if (engineIsActive) {
            // 如果场景正在运行,则返回正在运行的场景名称
            String runningScenarioName = runService.getRunningScenarioName();
            startRunInfo.put("enableRun", "False");
            message.add(StringUtils.join("场景[", runningScenarioName, "]正在运行,请等待测试完成后再试"));
        }

        // 如果填写项正确填写,并且没有场景运行,则返回可以运行测试
        if (Objects.equals(startRunInfo.get("enableRun"), "False")) {
            startRunInfo.put("message", message);
        } else {
            // 如果场景没有运行,则返回要运行场景的ID
            startRunInfo.put("isActive", "True");
            startRunInfo.put("scenarioId", scenarioId);
        }

        return JSONObject.parseObject(JSON.toJSONString(startRunInfo));
    }

    @GetMapping(path = "/scenarioStartRun")
    public String scenarioStartRun(Model model, @RequestParam("scenarioId") String scenarioId) {
        // 启动场景
        runService.scenarioStartRun(Integer.parseInt(scenarioId));
        model.addAttribute("scenarioId", scenarioId);
        return "scenarioRealTimeChartGrid";
    }

    @GetMapping(path = "/scenarioStopRun")
    public @ResponseBody JSONObject scenarioStopRun() {
        Map<String, Object> stopRunMessage = runService.scenarioStopRun();
        return JSONObject.parseObject(JSON.toJSONString(stopRunMessage));
    }

    @GetMapping(path = "/scenarioRunningCheck")
    public String scenarioRunningCheck(Model model) {
        Boolean engineIsActive = runService.getEngineIsActive();
        Integer scenarioId = null;
        if (engineIsActive) {
            scenarioId = runService.getRunningScenarioId();
            model.addAttribute("scenarioId", scenarioId);
            return "scenarioRealTimeChartGrid";
        } else {
            model.addAttribute("isActive", "False");
            return "scenarioList";
        }
    }

    @GetMapping(path = "/scenarioRealTimeChartDetail")
    public String scenarioRealTimeChartDetail(Model model, @RequestParam("scenarioId") String scenarioId, @RequestParam("dataType") String dataType) {
        runService.scenarioSampleResultRealOuter();
        model.addAttribute("scenarioId", scenarioId);
        model.addAttribute("dataType", dataType);

        if (Objects.equals(dataType, "meanTime")) {
            model.addAttribute("chartTitle", "平均响应时间-运行状态");
            model.addAttribute("unit", "毫秒");
//            return "scenarioResponseTimeChart";
        }
        if (Objects.equals(dataType, "requestRate")) {
            model.addAttribute("chartTitle", "每秒请求处理能力-运行状态");
            model.addAttribute("unit", "个");
//            return "scenarioRequestRateChart";
        }
        if (Objects.equals(dataType, "errorPercentage")) {
            model.addAttribute("chartTitle", "错误百分比-运行状态");
            model.addAttribute("unit", "百分比");
//            return "scenarioErrorByPercentChart";
        }
        if (Objects.equals(dataType, "threadCount")) {
            model.addAttribute("chartTitle", "并发数量趋势-运行状态");
            model.addAttribute("unit", "个");
//            return "scenarioThreadCountChart";
        }
        if (Objects.equals(dataType, "sentKBPerSecond")) {
            model.addAttribute("chartTitle", "发送数据量趋势-运行状态");
            model.addAttribute("unit", "KB");
//            return "scenarioSendKBPerSecChart";
        }
        if (Objects.equals(dataType, "receiveKBPerSecond")) {
            model.addAttribute("chartTitle", "接收数据量趋势-运行状态");
            model.addAttribute("unit", "KB");
//            return "scenarioReceiveKBPerSecChart";
        }
        return "scenarioRealTimeChart";
    }
}
