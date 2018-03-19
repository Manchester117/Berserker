package com.bushmaster.architecture.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.domain.entity.ScenarioInfo;
import com.bushmaster.architecture.domain.entity.ScriptFileInfo;
import com.bushmaster.architecture.domain.param.ScenarioInfoAddParams;
import com.bushmaster.architecture.domain.param.ScenarioInfoListParams;
import com.bushmaster.architecture.domain.param.ScenarioInfoModParams;
import com.bushmaster.architecture.engine.core.EngineController;
import com.bushmaster.architecture.interceptor.PreventRepeatSubmit;
import com.bushmaster.architecture.service.ScenarioInfoService;
import com.google.common.base.Strings;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ScenarioInfoController {
    @Autowired
    private ScenarioInfoService scenarioInfoService;

    private Map<String, List<String>> paramsValidator(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errorResult = new HashMap<>();
            List<String> errorMsgList = new ArrayList<>();
            for (ObjectError objectError: bindingResult.getAllErrors())
                errorMsgList.add(objectError.getDefaultMessage());
            errorResult.put("paramErrors", errorMsgList);
            return errorResult;
        } else {
            return null;
        }
    }

    private Date getDateTimeValue(String dateTime) {
        Date dateTimeValue = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateTimeValue = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeValue;
    }

    private Boolean getBooleanValue(String status) {
        Boolean statusValue = null;
        if (Objects.nonNull(status) && StringUtils.isNumeric(status)) {
            if (Objects.equals(status, "1"))
                statusValue = Boolean.TRUE;
            else
                statusValue = Boolean.FALSE;
        } else {
            statusValue = null;
        }
        return statusValue;
    }

    /** 场景列表部分 **/

    @GetMapping(path = "/")
    public String index() {
        return "scenarioInfoList";
    }

    @PostMapping(path = "/scenarioInfoList")
    public @ResponseBody JSONObject scenarioInfoList(@Validated @RequestBody ScenarioInfoListParams requestParams, BindingResult bindingResult) {
        // 参数验证
        Map<String, List<String>> errorResultInfo = this.paramsValidator(bindingResult);
        if (Objects.nonNull(errorResultInfo))
            return JSONObject.parseObject(JSON.toJSONString(errorResultInfo));

        String offset = requestParams.getOffset();
        String limit = requestParams.getLimit();
        String scenarioName = requestParams.getScenarioName();
        String status = requestParams.getStatus();

        Integer offsetInt = null;
        Integer limitInt = null;
        if (StringUtils.isNumeric(offset) && StringUtils.isNumeric(limit)) {
            offsetInt = Integer.parseInt(offset);
            limitInt = Integer.parseInt(limit);
        } else {
            offsetInt = 0;
            limitInt = 15;
        }
        Boolean statusValue = this.getBooleanValue(status);
        Map<String, Object> resultMap = scenarioInfoService.getScenarioInfoByPageList(offsetInt, limitInt, scenarioName, statusValue);

        return JSONObject.parseObject(JSON.toJSONString(resultMap));
    }

    /** 添加场景部分 **/

    @GetMapping(path = "/scenarioInfoAdd")
    public String scenarioInfoAdd() {
        return "scenarioInfoAdd";
    }

    @PreventRepeatSubmit
    @PostMapping(path = "/addScenarioInfo")
    public @ResponseBody JSONObject addScenarioInfo(@Validated ScenarioInfoAddParams requestParams, BindingResult bindingResult,
                                                    @RequestParam(value = "scriptFile", required = false) MultipartFile scriptFile) {
        // 参数验证部分
        Map<String, List<String>> errorResultInfo = new HashMap<>();
        Map<String, List<String>> paramsError = this.paramsValidator(bindingResult);
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
        // 默认场景停用
        scenarioInfo.setStatus(false);
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
        return "scenarioInfoModify";
    }

    @PreventRepeatSubmit
    @PostMapping(path = "/modScenarioInfo")
    public @ResponseBody JSONObject modScenarioInfo(@Validated ScenarioInfoModParams requestParams, BindingResult bindingResult,
                                                    @RequestParam(value = "scriptFile", required = false) MultipartFile scriptFile) {
        Map<String, List<String>> errorResultInfo = this.paramsValidator(bindingResult);
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

        scenarioInfo.setStatus(false);

        Map<String, Object> result = scenarioInfoService.modScenarioInfo(scenarioInfo, scriptFile);
        result.put("scenarioId", scenarioId);
        return JSONObject.parseObject(JSON.toJSONString(result));
    }

    /** 删除场景部分 **/

    @PostMapping(path = "/delScenarioInfo")
    public @ResponseBody JSONObject delScenarioInfo(@RequestParam("id") String id) {
        JSONObject resultJson = new JSONObject();
        if (Objects.isNull(id)) {
            resultJson.put("status", "Error");
            resultJson.put("message", "删除场景的ID不能为空");
            return resultJson;
        }

        if (StringUtils.isNumeric(id) && Integer.parseInt(id) > 0) {
            Map<String, Object> result = scenarioInfoService.delScenarioInfo(Integer.parseInt(id));
            return JSONObject.parseObject(JSON.toJSONString(result));
        } else {
            resultJson.put("status", "Error");
            resultJson.put("message", "删除场景ID必须是非零正整数");
            return resultJson;
        }
    }
}
