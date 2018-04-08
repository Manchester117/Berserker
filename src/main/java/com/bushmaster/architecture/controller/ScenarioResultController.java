package com.bushmaster.architecture.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.domain.param.ScenarioResultByIdParams;
import com.bushmaster.architecture.domain.param.ScenarioResultListParams;
import com.bushmaster.architecture.service.ScenarioResultService;
import com.bushmaster.architecture.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class ScenarioResultController {
    @Autowired
    private ScenarioResultService resultService;

    @GetMapping(path = "/scenarioNewResultList")
    public String scenarioNewResultList() {
        return "scenarioNewResultList";
    }

    @PostMapping(path = "/getScenarioNewResultList")
    public @ResponseBody JSONObject getScenarioNewResultList(@Validated @RequestBody ScenarioResultListParams requestParams, BindingResult bindingResult) {
        // 参数验证
        Map<String, List<String>> errorResultInfo = CommonUtil.paramsValidator(bindingResult);
        if (Objects.nonNull(errorResultInfo))
            return JSONObject.parseObject(JSON.toJSONString(errorResultInfo));

        Integer offset = Integer.parseInt(requestParams.getOffset());
        Integer limit = Integer.parseInt(requestParams.getLimit());

        Map<String, Object> result = resultService.getScenarioResultInfoList(offset, limit);
        return JSONObject.parseObject(JSON.toJSONString(result));
    }

    @GetMapping(path = "/scenarioResultListById")
    public String scenarioResultListById(Model model, @RequestParam("scenarioId") Integer scenarioId) {
        model.addAttribute("scenarioId", scenarioId);
        return "scenarioResultListById";
    }

    @PostMapping(path = "/getScenarioResultListByScenarioId")
    public @ResponseBody JSONObject getScenarioResultListByScenarioId(@Validated @RequestBody ScenarioResultByIdParams requestParams, BindingResult bindingResult) {
        // 参数验证
        Map<String, List<String>> errorResultInfo = CommonUtil.paramsValidator(bindingResult);
        if (Objects.nonNull(errorResultInfo))
            return JSONObject.parseObject(JSON.toJSONString(errorResultInfo));

        Integer offset = Integer.parseInt(requestParams.getOffset());
        Integer limit = Integer.parseInt(requestParams.getLimit());
        Integer scenarioId = Integer.parseInt(requestParams.getScenarioId());

        Map<String, Object> result = resultService.getScenarioResultInfoByScenarioId(offset, limit, scenarioId);
        return JSONObject.parseObject(JSON.toJSONString(result));
    }

    @PostMapping(path = "/delScenarioResult")
    public @ResponseBody JSONObject delScenarioResult(@RequestParam("resultId") Integer resultId) {
        Map<String, Object> result = resultService.delScenarioResultInfoByResultId(resultId);
        return JSONObject.parseObject(JSON.toJSONString(result));
    }
}