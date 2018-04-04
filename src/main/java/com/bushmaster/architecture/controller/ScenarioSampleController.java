package com.bushmaster.architecture.controller;

import com.bushmaster.architecture.service.SampleResultService;
import com.google.common.base.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class ScenarioSampleController {
    @Autowired
    private SampleResultService resultService;

    @GetMapping(path = "/scenarioSampleDetailChartGrid")
    public String scenarioSampleDetailChartGrid(Model model, @RequestParam("resultId") Integer resultId) {
        model.addAttribute("resultId", resultId);
        return "scenarioSampleDetailChartGrid";
    }

    @GetMapping(path = "/scenarioSampleResultChart")
    public String scenarioSampleResultChart(Model model, @RequestParam("resultId") Integer resultId, @RequestParam("dataType") String dataType) {
        model.addAttribute("resultId", resultId);
        model.addAttribute("dataType", dataType);
        if (Objects.equal(dataType, "meanTime")) {
            model.addAttribute("chartTitle", "平均响应时间趋势");
            model.addAttribute("unit", "毫秒");
        }
        if (Objects.equal(dataType, "requestRate")) {
            model.addAttribute("chartTitle", "请求处理能力");
            model.addAttribute("unit", "个");
        }
        if (Objects.equal(dataType, "errorPercentage")) {
            model.addAttribute("chartTitle", "请求错误率");
            model.addAttribute("unit", "百分比");
        }
        if (Objects.equal(dataType, "threadCount")) {
            model.addAttribute("chartTitle", "并发数量趋势");
            model.addAttribute("unit", "个");
        }
        if (Objects.equal(dataType, "sentKBPerSecond")) {
            model.addAttribute("chartTitle", "发送数据量趋势");
            model.addAttribute("unit", "KB");
        }
        if (Objects.equal(dataType, "receiveKBPerSecond")) {
            model.addAttribute("chartTitle", "接收数据量趋势");
            model.addAttribute("unit", "KB");
        }
        return "scenarioSampleDetailResultChart";
    }

    // Springboot 1.5.X 直接返回JSON时解析会报错.避免此问题需要直接按照Java的数据结构返回.
//    @GetMapping(path = "/getSampleResultDetailData")
//    public @ResponseBody JSONObject getSampleResultDetailData(@RequestParam("resultId") Integer resultId,
//                                                                                             @RequestParam("dataType") String dataType) {
//        Map<String, List<Map<Timestamp, Object>>> sampleResultContainer = resultService.getSampleResultDataListByResultId(resultId, dataType);
//        return JSONObject.parseObject(JSON.toJSONString(sampleResultContainer));
//    }

    // 修改后的方法,直接使用Java的数据结构返回.
    @PostMapping(path = "/getSampleResultDetailData")
    public @ResponseBody Map<String, List<List<Number>>> getSampleResultDetailData(@RequestParam("resultId") Integer resultId,
                                                                                   @RequestParam("dataType") String dataType) {
        // 如果给HighCharts传时间,需要使用Long类型.
        return resultService.getSampleResultDataListByResultId(resultId, dataType);
    }
}
