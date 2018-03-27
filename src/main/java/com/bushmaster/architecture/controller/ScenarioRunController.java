package com.bushmaster.architecture.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.service.ScenarioRunService;
import com.google.common.base.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class ScenarioRunController {
    @Autowired
    private ScenarioRunService scenarioRunService;

    @GetMapping(path = "/scenarioStartRun")
    public String scenarioStartRun(Model model, @RequestParam("scenarioId") String scenarioId) {
        scenarioRunService.scenarioStartRun(Integer.parseInt(scenarioId));
        model.addAttribute("scenarioId", scenarioId);
        return "scenarioChartGrid";
    }

    @GetMapping(path = "/scenarioStopRun")
    public @ResponseBody JSONObject scenarioStopRun() {
        Map<String, Object> stopRunMessage = scenarioRunService.scenarioStopRun();
        return JSONObject.parseObject(JSON.toJSONString(stopRunMessage));
    }

    @GetMapping(path = "/scenarioChartDetail")
    public String scenarioChartDetail(Model model, @RequestParam("scenarioId") String scenarioId, @RequestParam("dataType") String dataType) {
        scenarioRunService.scenarioSampleResultRealOuter();
        model.addAttribute("scenarioId", scenarioId);
        model.addAttribute("dataType", dataType);

        if (Objects.equal(dataType, "meanTime"))
            return "scenarioResponseTimeChart";
        else if (Objects.equal(dataType, "requestRate"))
            return "scenarioRequestRateChart";
        else if (Objects.equal(dataType, "errorPercentage"))
            return "scenarioErrorByPercentChart";
        else if (Objects.equal(dataType, "threadCount"))
            return "scenarioThreadCountChart";
        else if (Objects.equal(dataType, "sentKBPerSecond"))
            return "scenarioSendKBPerSecChart";
        else if (Objects.equal(dataType, "receiveKBPerSecond"))
            return "scenarioReceiveKBPerSecChart";
        else
            return "";
    }
}
