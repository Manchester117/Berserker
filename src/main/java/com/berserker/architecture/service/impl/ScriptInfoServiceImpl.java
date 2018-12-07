package com.berserker.architecture.service.impl;

import com.berserker.architecture.domain.entity.ScriptFileInfo;
import com.berserker.architecture.engine.core.EngineScriptParser;
import com.berserker.architecture.mapper.ScriptFileInfoMapper;
import com.berserker.architecture.service.ScriptInfoService;
import com.berserker.architecture.utils.FileStorageUtil;
import com.berserker.architecture.utils.ScriptUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ScriptInfoServiceImpl implements ScriptInfoService {
    @Autowired
    private ScriptFileInfoMapper scriptMapper;
    @Autowired
    private FileStorageUtil fileUtil;
    @Autowired
    private ScriptUtil scriptUtil;
    @Autowired
    private EngineScriptParser parser;

    @Override
    public ScriptFileInfo getScriptFileInfo(Integer id) {
        ScriptFileInfo scriptInfo = scriptMapper.getScriptFileInfo(id);
        if (Objects.nonNull(scriptInfo))
            return scriptInfo;
        else
            return null;
    }

    @Override
    public ScriptFileInfo getScriptFileInfoByScenarioId(Integer scenarioId) {
        ScriptFileInfo scriptInfo = scriptMapper.getScriptFileInfoByScenarioId(scenarioId);
        if (Objects.nonNull(scriptInfo))
            return scriptInfo;
        else
            return null;
    }

    @Override
    public HashTree getTestPlanTreeByScenarioId(Integer scenarioId) {
        File scriptFile = scriptUtil.getScriptFileByScenarioId(scenarioId);
        return parser.loadTestPlan(scriptFile);
    }

    @Override
    public Map<String, Object> getScriptDataStructure(Integer scenarioId) {
        File scriptFile = scriptUtil.getScriptFileByScenarioId(scenarioId);
        // 获得脚本的数据结构
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> scriptDataStructure = parser.parseScriptToDataStructure(scriptFile);
        result.put("scriptDataStructure", scriptDataStructure);

        return result;
    }

    @Override
    public List<Map<String, Object>> getCsvDataSetSlotList(Integer scenarioId) {
        File scriptFile = scriptUtil.getScriptFileByScenarioId(scenarioId);
        // 获得脚本中的CSV Data Set
        return parser.parseScriptToParamFileSlots(scriptFile);
    }

    @Override
    public Map<String, Object> addScriptInfo(MultipartFile scriptFileUpload, Integer scenarioId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> insertScriptResult = new HashMap<>();

        ScriptFileInfo scriptInfo = fileUtil.scriptFileUpload(scriptFileUpload);
        if (Objects.nonNull(scriptInfo)) {
            String scriptFileName = scriptInfo.getScriptFileName();
            String scriptFileFolder = scriptInfo.getScriptFilePath();

            scriptInfo.setScenarioId(scenarioId);                           // 插入的脚本对象加上场景ID
            int insertFlag = scriptMapper.insertScriptFileInfo(scriptInfo);
            if (insertFlag > 0) {
                insertScriptResult.put("status", "Success");
                insertScriptResult.put("message", "脚本添加成功");
                // 脚本路径字符串拼接
                String scriptFullPath = StringUtils.join(scriptFileFolder, scriptFileName);
                URI scriptFullURI = URI.create(scriptFullPath);
                File scriptFile = Paths.get(scriptFullURI).toFile();
                // 获得脚本中的CSV Data Set的List
                List<Map<String, Object>> csvDataSetSlotList = parser.parseScriptToParamFileSlots(scriptFile);
                result.put("csvDataSetSlotList", csvDataSetSlotList);
            } else {
                insertScriptResult.put("status", "Fail");
                insertScriptResult.put("message", "脚本入库失败");
            }
            insertScriptResult.put("object", scriptFileName);
        } else {
            insertScriptResult.put("status", "Fail");
            insertScriptResult.put("message", "脚本写入磁盘失败");
        }

        result.put("insertScriptResult", insertScriptResult);
        return result;
    }

    @Override
    public Map<String, Object> modScriptInfo(MultipartFile scriptFileUpload, Integer modScenarioId) {
        Map<String, Object> result = new HashMap<>();
        // 获取要更新的脚本记录
        ScriptFileInfo oldScriptInfo = scriptMapper.getScriptFileInfoByScenarioId(modScenarioId);
        // 上传新的脚本
        ScriptFileInfo newScriptInfo = fileUtil.scriptFileUpload(scriptFileUpload);     // 进行脚本上传操作,获取新的脚本对象
        newScriptInfo.setId(oldScriptInfo.getId());
        newScriptInfo.setScenarioId(modScenarioId);
        // 更新DB中的记录
        int modScriptFlag = scriptMapper.modifyScriptFileInfo(newScriptInfo);
        Path oldScriptPath = Paths.get(URI.create(oldScriptInfo.getScriptFilePath()));
        // 删除旧的文件夹
        Boolean deleteOldFolderFlag = fileUtil.deleteScriptFolder(oldScriptPath);
        if (modScriptFlag > 0 && deleteOldFolderFlag) {
            result.put("status", "Success");
            result.put("message", "脚本更新成功");
        } else {
            result.put("status", "Fail");
            result.put("message", "脚本更新失败");
        }
        return result;
    }

    @Override
    public Map<String, Object> delScriptInfo(ScriptFileInfo scriptFileInfo) {
        Map<String, Object> result = new HashMap<>();
        int deleteResultVal = scriptMapper.deleteScriptFileInfo(scriptFileInfo.getId());
        if (deleteResultVal > 0) {
            boolean deleteFileFlag = fileUtil.deleteStorageScript(scriptFileInfo);
            if (deleteFileFlag) {
                result.put("status", "Success");
                result.put("message", "脚本删除成功");
            } else {
                result.put("status", "Fail");
                result.put("message", "脚本删除失败");
            }
        } else {
            result.put("status", "Info");
            result.put("message", "未在DB中找到脚本记录");
        }
        return result;
    }

    @Override
    public Map<String, Object> delScriptInfoByScenarioId(Integer scenarioId) {
        ScriptFileInfo scriptFileInfo = scriptMapper.getScriptFileInfoByScenarioId(scenarioId);
        return this.delScriptInfo(scriptFileInfo);
    }
}
