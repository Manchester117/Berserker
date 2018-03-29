package com.bushmaster.architecture.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.service.ScenarioResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class ScenarioResultController {
    @Autowired
    private ScenarioResultService resultService;

    @PostMapping(path = "/scenarioResultList")
    public @ResponseBody JSONObject scenarioResultList(@RequestParam("scenarioId") Integer scenarioId) {
        Map<String, Object> result = resultService.getScenarioResultInfoByScenarioId(0, 15, scenarioId);
        return JSONObject.parseObject(JSON.toJSONString(result));
    }
}
