package com.bushmaster.architecture.service.impl;

import com.bushmaster.architecture.domain.entity.ScenarioInfo;
import com.bushmaster.architecture.domain.entity.ScriptFileInfo;
import com.bushmaster.architecture.mapper.ScenarioInfoMapper;
import com.bushmaster.architecture.mapper.ScriptFileInfoMapper;
import com.bushmaster.architecture.service.ParamFileService;
import com.bushmaster.architecture.service.ScenarioInfoService;
import com.bushmaster.architecture.service.ScriptInfoService;
import com.bushmaster.architecture.utils.FileStorageUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ScenarioInfoServiceImpl implements ScenarioInfoService{
    @Autowired
    private ScenarioInfoMapper scenarioMapper;
    @Autowired
    private ScriptFileInfoMapper scriptMapper;
    @Autowired
    private ScriptInfoService scriptInfoService;
    @Autowired
    private ParamFileService paramFileService;
    @Autowired
    private FileStorageUtil fileUtil;

    @Override
    public ScenarioInfo getScenarioInfo(Integer id) {
        ScenarioInfo scenarioInfo = scenarioMapper.getScenarioInfo(id);
        if (Objects.nonNull(scenarioInfo))
            return scenarioInfo;
        else
            return null;
    }

    @Override
    public Map<String, Object> getScenarioInfoByPageList(Integer offset, Integer limit, String scenarioName, Boolean status) {
        Map<String, Object> result = new HashMap<>();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<ScenarioInfo> scenarioInfoList = scenarioMapper.getScenarioInfoList(scenarioName, status);
        result.put("total", page.getTotal());
        result.put("rows", scenarioInfoList);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> addScenarioInfo(ScenarioInfo scenarioInfo, MultipartFile scriptFile) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> insertScenarioResult = new HashMap<>();

        int insertFlag = scenarioMapper.insertScenarioInfo(scenarioInfo);
        // 获取插入的场景ID
        Integer addScenarioId = null;
        if (insertFlag > 0) {
            addScenarioId = scenarioInfo.getId();
            Map<String, Object> insertAndParseScriptResult = scriptInfoService.addScriptInfo(scriptFile, addScenarioId);
            result.put("insertScriptResult", insertAndParseScriptResult.get("insertScriptResult"));
            result.put("csvDataSetSlotList", insertAndParseScriptResult.get("csvDataSetSlotList"));
            result.put("scenarioId", addScenarioId);

            insertScenarioResult.put("status", "Success");
            insertScenarioResult.put("message", "场景添加成功");
        } else {
            insertScenarioResult.put("status", "Fail");
            insertScenarioResult.put("message", "场景添加失败");
        }
        result.put("insertScenarioResult", insertScenarioResult);

        return result;
    }

    @Override
    public ScriptFileInfo getScriptFileInfoByScenarioId(Integer scenarioId) {
        return scriptInfoService.getScriptFileInfoByScenarioId(scenarioId);
    }

    @Override
    public Map<String, Object> addScenarioParamFiles(Integer scenarioId, List<Map<String, Object>> csvDataParamFileList) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(csvDataParamFileList)) {
            result = paramFileService.addParamFileInfo(scenarioId, csvDataParamFileList);
            String scriptAbsolutePath = result.get("scriptPath").toString();
            HashTree testPlanTree = (HashTree) result.get("testPlanTree");
            // 进行脚本重写
            Boolean backWriteFlag = fileUtil.scriptBackWrite(scriptAbsolutePath, testPlanTree);
            if (backWriteFlag) {
                result.put("status", "Success");
                result.put("message", "脚本重写成功");
            } else {
                result.put("status", "Fail");
                result.put("message", "脚本重写失败");
            }
            result.remove("scriptPath");            // 删除脚步的绝对路径
            result.remove("testPlanTree");          // 必须把testPlanTree删除,否则FastJson解析会出错.
        } else {
            result.put("status", "Success");
            result.put("message", "场景存储成功");
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getCsvDataSetSlotList(Integer scenarioId) {
        return scriptInfoService.getCsvDataSetSlotList(scenarioId);
    }

    @Override
    public Map<String, Object> getScriptDataStructure(Integer scenarioId) {
        return scriptInfoService.getScriptDataStructure(scenarioId);
    }

    @Override
    @Transactional
    public Map<String, Object> modScenarioInfo(ScenarioInfo scenarioInfo, MultipartFile scriptFile) {
        Map<String, Object> result = new HashMap<>();
        // 这里分成两部分
        // 1.进行场景数据写入
        Map<String, Object> modifyScenarioResult = new HashMap<>();
        int updateFlag = scenarioMapper.updateScenarioInfo(scenarioInfo);
        if (updateFlag > 0) {
            modifyScenarioResult.put("status", "Success");
            modifyScenarioResult.put("message", "修改场景成功");
        } else {
            modifyScenarioResult.put("status", "Fail");
            modifyScenarioResult.put("message", "修改场景失败");
        }
        result.put("modifyScenarioResult", modifyScenarioResult);
        // 2.对上传脚本进行判断
        Map<String, Object> modifyScriptResult = null;
        if (Objects.nonNull(scriptFile)) {
            // 对脚本进行更新
            modifyScriptResult = scriptInfoService.modScriptInfo(scriptFile, scenarioInfo.getId());
            result.put("modifyScriptResult", modifyScriptResult);
            List<Map<String, Object>> modifyParamResult = this.getCsvDataSetSlotList(scenarioInfo.getId());
            result.put("modifyParamResult", modifyParamResult);
        }
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> delScenarioInfo(Integer scenarioId) {
        Map<String, Object> result = new HashMap<>();
        // 根据场景ID获取脚本文件
        ScriptFileInfo scriptInfo = scriptMapper.getScriptFileInfoByScenarioId(scenarioId);

        // 删除参数化文件
        List<Map<String, Object>> delParamFileResult = paramFileService.delParamFileInfoByScenarioId(scenarioId);
        result.put("delParamFileResult", delParamFileResult);

        // 删除脚本记录
        Map<String, Object> delScriptResult = scriptInfoService.delScriptInfoByScenarioId(scenarioId);
        result.put("delScriptResult", delScriptResult);

        // 删除场景记录
        Map<String, String> delScenarioResult = new HashMap<>();
        Path scriptFolderUri = Paths.get(URI.create(scriptInfo.getScriptFilePath()));
        Integer deleteScenarioFlag = scenarioMapper.deleteScenarioInfo(scenarioId);
        Boolean scriptFolderDelFlag = fileUtil.deleteScriptFolder(scriptFolderUri);
        if (deleteScenarioFlag <= 0) {
            delScenarioResult.put("status", "Info");
            delScenarioResult.put("message", "未找到待删除场景");
        } else if (!scriptFolderDelFlag) {
            delScenarioResult.put("status", "Fail");
            delScenarioResult.put("message", "未能删除脚本文件夹");
        } else {
            delScenarioResult.put("status", "Success");
            delScenarioResult.put("message", "场景删除成功");
        }
        result.put("delScenarioResult", delScenarioResult);

        return result;
    }
}
