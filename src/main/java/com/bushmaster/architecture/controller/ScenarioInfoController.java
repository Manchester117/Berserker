package com.bushmaster.architecture.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.domain.entity.ScenarioInfo;
import com.bushmaster.architecture.domain.entity.ScriptFileInfo;
import com.bushmaster.architecture.domain.param.ScenarioInfoAddParams;
import com.bushmaster.architecture.domain.param.ScenarioInfoListParams;
import com.bushmaster.architecture.domain.param.ScenarioInfoModParams;
import com.bushmaster.architecture.interceptor.PreventRepeatSubmit;
import com.bushmaster.architecture.service.ScenarioInfoService;
import com.bushmaster.architecture.utils.CommonUtil;
import com.google.common.base.Strings;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
public class ScenarioInfoController {
    @Autowired
    private ScenarioInfoService scenarioInfoService;

    /** 场景列表部分 **/

    @GetMapping(path = "/")
    public String index() {
        return "scenarioList";
    }

    @PostMapping(path = "/scenarioInfoList")
    public @ResponseBody JSONObject scenarioInfoList(@Validated @RequestBody ScenarioInfoListParams requestParams, BindingResult bindingResult) {
        // 参数验证
        Map<String, List<String>> errorResultInfo = CommonUtil.paramsValidator(bindingResult);
        if (Objects.nonNull(errorResultInfo))
            return JSONObject.parseObject(JSON.toJSONString(errorResultInfo));

        Integer offset = Integer.parseInt(requestParams.getOffset());
        Integer limit = Integer.parseInt(requestParams.getLimit());
        String scenarioName = requestParams.getScenarioName();

        Map<String, Object> result = scenarioInfoService.getScenarioInfoByPageList(offset, limit, scenarioName);

        return JSONObject.parseObject(JSON.toJSONString(result));
    }

    /** 添加场景部分 **/

    @GetMapping(path = "/scenarioInfoAdd")
    public String scenarioInfoAdd() {
        return "scenarioAdd";
    }

    @PreventRepeatSubmit
    @PostMapping(path = "/addScenarioInfo")
    public @ResponseBody JSONObject addScenarioInfo(@Validated ScenarioInfoAddParams requestParams, BindingResult bindingResult,
                                                    @RequestParam(value = "scriptFile", required = false) MultipartFile scriptFile) {
        // 参数验证部分
        Map<String, List<String>> errorResultInfo = new HashMap<>();
        Map<String, List<String>> paramsError = CommonUtil.paramsValidator(bindingResult);
        if (Objects.nonNull(paramsError))
            errorResultInfo.put("errorMessages", paramsError.get("paramErrors"));

        if (Objects.isNull(scriptFile)) {
            if (errorResultInfo.size() == 0) {
                List<String> scriptNullErrors = new ArrayList<>();
                scriptNullErrors.add("测试场景脚本不能为空");
                errorResultInfo.put("errorMessages", scriptNullErrors);
            } else {
                errorResultInfo.get("errorMessages").add("测试场景脚本不能为空");
            }
        }

        if (errorResultInfo.size() > 0)
            return JSONObject.parseObject(JSON.toJSONString(errorResultInfo));

        ScenarioInfo scenarioInfo = new ScenarioInfo();
        scenarioInfo.setScenarioName(requestParams.getScenarioName());

        Date createDate = new Date();
        scenarioInfo.setCreateTime(createDate);

        // 分别对并发数量/用户攀升时长/测试持续时间进行判空判断
        String numThreads = requestParams.getNumThreads();
        if (!Strings.isNullOrEmpty(numThreads) && StringUtils.isNumeric(numThreads))
            scenarioInfo.setNumThreads(Integer.parseInt(numThreads));
        else
            scenarioInfo.setNumThreads(null);

        String rampUp = requestParams.getRampUp();
        if (!Strings.isNullOrEmpty(rampUp) && StringUtils.isNumeric(rampUp))
            scenarioInfo.setRampUp(Integer.parseInt(requestParams.getRampUp()));
        else
            scenarioInfo.setRampUp(null);

        String duration = requestParams.getDuration();
        if (!Strings.isNullOrEmpty(duration) && StringUtils.isNumeric(duration))
            scenarioInfo.setDuration(Integer.parseInt(requestParams.getDuration()));
        else
            scenarioInfo.setDuration(null);

        scenarioInfo.setScenarioDescription(requestParams.getScenarioDescription());

        // 获取场景添加状态和解析脚本CSV的Json
        Map<String, Object> result = scenarioInfoService.addScenarioInfo(scenarioInfo, scriptFile);

        return JSONObject.parseObject(JSON.toJSONString(result));
    }

    @PreventRepeatSubmit
    @PostMapping(path = "/saveScenarioParamFiles")
    public @ResponseBody JSONObject saveScenarioParamFiles(@RequestParam("scenarioId") Integer scenarioId,
                                                           @RequestParam(value = "csvDataSetName", required = false) List<String> csvDataSetNameList,
                                                           @RequestParam(value = "paramFiles", required = false) List<MultipartFile> paramFileList) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.isNull(scenarioId)) {
            result.put("status", "Fail");
            result.put("message", "请先上传脚本建立场景");
            return JSONObject.parseObject(JSON.toJSONString(result));
        }

        if (Objects.isNull(csvDataSetNameList)) {
            if (paramFileList.size() == 0) {
                result = scenarioInfoService.addScenarioParamFiles(scenarioId, null);
                return JSONObject.parseObject(JSON.toJSONString(result));
            } else {
                result.put("status", "Fail");
                result.put("message", "上传参数文件逻辑不正确");
                return JSONObject.parseObject(JSON.toJSONString(result));
            }
        } else {
            if (paramFileList.size() > 0) {
                if (csvDataSetNameList.size() != paramFileList.size()) {
                    result.put("status", "Fail");
                    result.put("message", "实际上传的参数文件数量少于脚本中定义的参数文件数量");
                    return JSONObject.parseObject(JSON.toJSONString(result));
                } else {
                    // 将CSVDataSet与ParamFileList进行合并
                    List<Map<String, Object>> csvDataParamFileList = new ArrayList<>();
                    for (int index = 0; index < csvDataSetNameList.size(); ++index) {
                        Map<String, Object> csvDataParamFile = new HashMap<>();
                        csvDataParamFile.put("csvDataSetName", csvDataSetNameList.get(index));
                        csvDataParamFile.put("paramFile", paramFileList.get(index));
                        csvDataParamFileList.add(csvDataParamFile);
                    }
                    result = scenarioInfoService.addScenarioParamFiles(scenarioId, csvDataParamFileList);
                    return JSONObject.parseObject(JSON.toJSONString(result));
                }
            } else {
                result.put("status", "Fail");
                result.put("message", "没有任何参数文件被上传");
                return JSONObject.parseObject(JSON.toJSONString(result));
            }
        }
    }

    @PostMapping(path = "/getScenarioCsvDataSetSlotList")
    public @ResponseBody JSONArray getScenarioCsvDataSetSlotList(@RequestParam(value = "scenarioId") Integer scenarioId) {
        List<Map<String, Object>> csvDataSetSlotList = scenarioInfoService.getCsvDataSetSlotList(scenarioId);
        return JSONArray.parseArray(JSONArray.toJSONString(csvDataSetSlotList));
    }

    @PostMapping(path = "/getScenarioScriptDataStructure")
    public @ResponseBody JSONObject getScenarioScriptDataStructure(@RequestParam(value = "scenarioId") Integer scenarioId) {
        Map<String, Object> scriptDataStructure = scenarioInfoService.getScriptDataStructure(scenarioId);
        return JSONObject.parseObject(JSON.toJSONString(scriptDataStructure));
    }

    /** 修改场景部分 **/

    @GetMapping(path = "/scenarioInfoMod")
    public String scenarioInfoMod(Model model, @RequestParam(value = "scenarioId") Integer scenarioId) {
        ScenarioInfo scenarioInfo = scenarioInfoService.getScenarioInfo(scenarioId);
        ScriptFileInfo scriptFileInfo = scenarioInfoService.getScriptFileInfoByScenarioId(scenarioId);
        List<Map<String, Object>> csvDataSetSlotList = scenarioInfoService.getCsvDataSetSlotList(scenarioId);
        // 从脚本存放的全路径中取出文件名用于前端显示
        for (Map<String, Object> csvDataSetSlot: csvDataSetSlotList) {
            String paramFileName = FilenameUtils.getName(csvDataSetSlot.get("fileName").toString());
            csvDataSetSlot.put("fileName", paramFileName);
        }
        model.addAttribute("scenarioInfo", scenarioInfo);
        model.addAttribute("scriptFileInfo", scriptFileInfo);
        model.addAttribute("csvDataSetSlotList", csvDataSetSlotList);
        return "scenarioModify";
    }

    @PreventRepeatSubmit
    @PostMapping(path = "/modScenarioInfo")
    public @ResponseBody JSONObject modScenarioInfo(@Validated ScenarioInfoModParams requestParams, BindingResult bindingResult,
                                                    @RequestParam(value = "scriptFile", required = false) MultipartFile scriptFile) {
        Map<String, List<String>> errorResultInfo = CommonUtil.paramsValidator(bindingResult);
        if (Objects.nonNull(errorResultInfo))
            return JSONObject.parseObject(JSON.toJSONString(errorResultInfo));

        ScenarioInfo scenarioInfo = new ScenarioInfo();
        Integer scenarioId = Integer.parseInt(requestParams.getScenarioId());
        scenarioInfo.setId(scenarioId);
        scenarioInfo.setScenarioName(requestParams.getScenarioName());

        Date createDate = new Date();
        scenarioInfo.setCreateTime(createDate);

        // 分别对并发数量/用户攀升时长/测试持续时间进行判空判断
        String numThreads = requestParams.getNumThreads();
        if (!Strings.isNullOrEmpty(numThreads) && StringUtils.isNumeric(numThreads))
            scenarioInfo.setNumThreads(Integer.parseInt(numThreads));
        else
            scenarioInfo.setNumThreads(null);

        String rampUp = requestParams.getRampUp();
        if (!Strings.isNullOrEmpty(rampUp) && StringUtils.isNumeric(rampUp))
            scenarioInfo.setRampUp(Integer.parseInt(requestParams.getRampUp()));
        else
            scenarioInfo.setRampUp(null);

        String duration = requestParams.getDuration();
        if (!Strings.isNullOrEmpty(duration) && StringUtils.isNumeric(duration))
            scenarioInfo.setDuration(Integer.parseInt(requestParams.getDuration()));
        else
            scenarioInfo.setDuration(null);

        scenarioInfo.setScenarioDescription(requestParams.getScenarioDescription());

        Map<String, Object> result = scenarioInfoService.modScenarioInfo(scenarioInfo, scriptFile);
        result.put("scenarioId", scenarioId);
        return JSONObject.parseObject(JSON.toJSONString(result));
    }

    /** 删除场景部分 **/

    @PostMapping(path = "/delScenarioInfo")
    public @ResponseBody JSONObject delScenarioInfo(@RequestParam("scenarioId") String scenarioId) {
        JSONObject resultJson = new JSONObject();
        if (Objects.isNull(scenarioId)) {
            resultJson.put("status", "Error");
            resultJson.put("message", "删除场景的ID不能为空");
            return resultJson;
        }

        if (StringUtils.isNumeric(scenarioId) && Integer.parseInt(scenarioId) > 0) {
            Map<String, Object> result = scenarioInfoService.delScenarioInfo(Integer.parseInt(scenarioId));
            return JSONObject.parseObject(JSON.toJSONString(result));
        } else {
            resultJson.put("status", "Error");
            resultJson.put("message", "删除场景ID必须是非零正整数");
            return resultJson;
        }
    }
}
