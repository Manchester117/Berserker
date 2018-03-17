package com.bushmaster.architecture.service.impl;

import com.bushmaster.architecture.domain.entity.ParamFileInfo;
import com.bushmaster.architecture.engine.core.EngineScriptParser;
import com.bushmaster.architecture.mapper.ParamFileInfoMapper;
import com.bushmaster.architecture.service.ParamFileService;
import com.bushmaster.architecture.utils.FileStorageUtil;
import com.bushmaster.architecture.utils.ScriptUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ParamFileServiceImpl implements ParamFileService{
    @Autowired
    private ParamFileInfoMapper paramMapper;
    @Autowired
    private FileStorageUtil fileUtil;
    @Autowired
    private ScriptUtil scriptUtil;
    @Autowired
    private EngineScriptParser parser;

    @Override
    public ParamFileInfo getParamFileInfo(Integer id) {
        ParamFileInfo paramFileInfo = paramMapper.getParamFileInfo(id);
        if (Objects.nonNull(paramFileInfo))
            return paramFileInfo;
        else
            return null;
    }

    @Override
    public List<ParamFileInfo> getParamFileInfoListByScenarioId(Integer scenarioId) {
        List<ParamFileInfo> paramFileInfoList = paramMapper.getParamFileInfoListByScenarioId(scenarioId);
        if (Objects.nonNull(paramFileInfoList))
            return paramFileInfoList;
        else
            return null;
    }

    @Override
    public Map<String, Object> addParamFileInfo(Integer scenarioId, List<Map<String, Object>> csvDataParamFileList) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> insertParamFileResultList = new ArrayList<>();
        // 获得脚本的绝对存放路径
        File scriptFile = scriptUtil.getScriptFileByScenarioId(scenarioId);
        String scriptStorageFolderPath = scriptFile.getParentFile().getAbsolutePath();
        String scriptFileAbsolutePath = scriptFile.getAbsolutePath();

        // 删除以前的参数文件
        this.delParamFileInfoByScenarioId(scenarioId);

        // 参数文件上传
        List<Map<String, String>> correlationParamFileList = new ArrayList<>();
        for (Map<String, Object> csvDataParamFile: csvDataParamFileList) {
            String csvDataSetName = csvDataParamFile.get("csvDataSetName").toString();
            MultipartFile paramFile = (MultipartFile) csvDataParamFile.get("paramFile");
            Map<String, Object> singleParamFileUploadResult = new HashMap<>();
            // 参数文件写入到磁盘
            ParamFileInfo paramFileInfo = fileUtil.paramFileUpload(paramFile, scriptStorageFolderPath);
            if (Objects.nonNull(paramFileInfo)) {
                paramFileInfo.setScenarioId(scenarioId);
                // 将参数文件的信息写入到DB
                int insertFlag = paramMapper.insertParamFileInfo(paramFileInfo);
                if (insertFlag > 0) {
                    singleParamFileUploadResult.put("status", "Success");
                    singleParamFileUploadResult.put("message", "参数文件添加成功");
                    singleParamFileUploadResult.put("object", paramFileInfo.getParamFileName());
                } else {
                    singleParamFileUploadResult.put("status", "Fail");
                    singleParamFileUploadResult.put("message", "参数文件入库失败");
                    singleParamFileUploadResult.put("object", paramFileInfo.getParamFileName());
                }
                insertParamFileResultList.add(singleParamFileUploadResult);
                // 获取参数文件的绝对路径
                String paramFileFolderPath = Paths.get(URI.create(paramFileInfo.getParamFilePath())).toFile().getAbsolutePath();
                String paramFileName = paramFileInfo.getParamFileName();
                String paramFileAbsolutePath = StringUtils.join(paramFileFolderPath, File.separator, paramFileName);
                // 进行参数结构的构成
                Map<String, String> paramFileCorrelation = new HashMap<>();
                paramFileCorrelation.put("csvDataSetName", csvDataSetName);
                paramFileCorrelation.put("paramFileAbsolutePath", paramFileAbsolutePath);
                correlationParamFileList.add(paramFileCorrelation);
            } else {
                singleParamFileUploadResult.put("status", "Fail");
                singleParamFileUploadResult.put("message", "参数文件写入磁盘失败");
                insertParamFileResultList.add(singleParamFileUploadResult);
            }
            result.put("insertParamFileResultList", insertParamFileResultList);
        }
        // 获取测试计划
        HashTree testPlanTree = parser.correlationScriptToSetParamFile(scriptFile, correlationParamFileList);
        result.put("testPlanTree", testPlanTree);
        result.put("scriptPath", scriptFileAbsolutePath);

        return result;
    }

    @Override
    public Map<String, Object> delParamFileInfo(ParamFileInfo paramFileInfo) {
        Map<String, Object> result = new HashMap<>();
        int deleteFlag = paramMapper.deleteParamFileInfo(paramFileInfo.getId());
        if (deleteFlag > 0) {
            boolean deleteFileFlag = fileUtil.deleteStorageParamFile(paramFileInfo);
            if (deleteFileFlag) {
                result.put("status", "Success");
                result.put("message", "参数文件删除成功");
            } else {
                result.put("status", "Fail");
                result.put("message", "参数文件删除失败");
            }
            result.put("object", paramFileInfo.getParamFileName());
        } else {
            result.put("status", "Fail");
            result.put("message", "没有待删除的参数化文件");
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> delParamFileInfoByScenarioId(Integer scenarioId) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<ParamFileInfo> paramFileInfoList = paramMapper.getParamFileInfoListByScenarioId(scenarioId);
        for (ParamFileInfo paramFileInfo: paramFileInfoList) {
            Map<String, Object> delSingleParamFileResult = this.delParamFileInfo(paramFileInfo);
            result.add(delSingleParamFileResult);
        }
        return result;
    }
}
