package com.bushmaster.architecture.service.impl;

import com.bushmaster.architecture.domain.entity.ScenarioInfo;
import com.bushmaster.architecture.domain.entity.ScriptFileInfo;
import com.bushmaster.architecture.mapper.ScenarioInfoMapper;
import com.bushmaster.architecture.mapper.ScriptFileInfoMapper;
import com.bushmaster.architecture.service.ParamFileService;
import com.bushmaster.architecture.service.ScenarioInfoService;
import com.bushmaster.architecture.service.ScenarioResultService;
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
    @Autowired
    private ScenarioResultService resultService;

    /**
     * @description             通过ID获取场景实体信息
     * @param id                场景ID
     * @return                  如果DB中存在场景则返回场景信息,否则返回null
     */
    @Override
    public ScenarioInfo getScenarioInfo(Integer id) {
        ScenarioInfo scenarioInfo = scenarioMapper.getScenarioInfo(id);
        if (Objects.nonNull(scenarioInfo))
            return scenarioInfo;
        else
            return null;
    }

    /**
     * @description             获取场景信息分页列表
     * @param offset            页码
     * @param limit             单页信息个数
     * @param scenarioName      搜索条件: 场景名称
     * @return                  返回场景列表的分页信息
     */
    @Override
    public Map<String, Object> getScenarioInfoByPageList(Integer offset, Integer limit, String scenarioName) {
        Map<String, Object> result = new HashMap<>();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<ScenarioInfo> scenarioInfoList = scenarioMapper.getScenarioInfoList(scenarioName);
        result.put("total", page.getTotal());
        result.put("rows", scenarioInfoList);
        return result;
    }

    /**
     * @description             通过场景ID获取脚本实体对象
     * @param scenarioId        场景ID
     * @return                  要返回的脚本实体对象
     */
    @Override
    public ScriptFileInfo getScriptFileInfoByScenarioId(Integer scenarioId) {
        return scriptInfoService.getScriptFileInfoByScenarioId(scenarioId);
    }

    /**
     * @description             通过场景ID获取脚本中的CSV Data Set信息列表(包含: CSV Data Set名称, 文件名称, 变量名称)
     * @param scenarioId        场景ID
     * @return                  返回的CSV Data Set列表
     */
    @Override
    public List<Map<String, Object>> getCsvDataSetSlotList(Integer scenarioId) {
        return scriptInfoService.getCsvDataSetSlotList(scenarioId);
    }

    /**
     * @description             通过场景ID获取脚本的数据结构
     * @param scenarioId        场景ID
     * @return                  数据结构以Map形式返回,用于前端显示
     */
    @Override
    public Map<String, Object> getScriptDataStructure(Integer scenarioId) {
        return scriptInfoService.getScriptDataStructure(scenarioId);
    }

    /**
     * @description             添加场景实体信息
     * @param scenarioInfo      要添加场景实体对象
     * @param scriptFile        上传的测试计划文件
     * @return                  返回前端所需要的信息
     */
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

    /**
     * @description             给脚本添加参数文件
     * @param scenarioId        场景ID
     * @param csvParamFileList  参数文件的信息列表(包含: CSV Data Set的名称, 上传的参数文件)
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> addScenarioParamFiles(Integer scenarioId, List<Map<String, Object>> csvParamFileList) {
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(csvParamFileList)) {
            result = paramFileService.addParamFileInfo(scenarioId, csvParamFileList);
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

    /**
     * @description             修改场景实体信息
     * @param scenarioInfo      场景ID
     * @param scriptFile        测试计划文件
     * @return                  返回前端所需要的信息
     */
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

    /**
     * @description             删除场景实体信息
     * @param scenarioId        场景ID
     * @return                  返回前端所需要的信息
     */
    @Override
    @Transactional
    public Map<String, Object> delScenarioInfo(Integer scenarioId) {
        Map<String, Object> result = new HashMap<>();

        // 删除场景的所有结果集
        Map<String, Object> delResultInfo = resultService.delScenarioResultInfoByScenarioId(scenarioId);
        result.put("delResultInfo", delResultInfo);

        // 根据场景ID获取脚本文件
        ScriptFileInfo scriptInfo = scriptMapper.getScriptFileInfoByScenarioId(scenarioId);

        // 删除参数化文件
        List<Map<String, Object>> delParamFileInfo = paramFileService.delParamFileInfoByScenarioId(scenarioId);
        result.put("delParamFileInfo", delParamFileInfo);

        // 删除脚本记录
        Map<String, Object> delScriptInfo = scriptInfoService.delScriptInfoByScenarioId(scenarioId);
        result.put("delScriptInfo", delScriptInfo);

        // 删除场景记录
        Map<String, String> delScenarioInfo = new HashMap<>();
        Path scriptFolderUri = Paths.get(URI.create(scriptInfo.getScriptFilePath()));
        Integer deleteScenarioFlag = scenarioMapper.deleteScenarioInfo(scenarioId);
        Boolean scriptFolderDelFlag = fileUtil.deleteScriptFolder(scriptFolderUri);
        if (deleteScenarioFlag <= 0) {
            delScenarioInfo.put("status", "Info");
            delScenarioInfo.put("message", "未找到待删除场景");
        } else if (!scriptFolderDelFlag) {
            delScenarioInfo.put("status", "Fail");
            delScenarioInfo.put("message", "未能删除脚本文件夹");
        } else {
            delScenarioInfo.put("status", "Success");
            delScenarioInfo.put("message", "场景删除成功");
        }
        result.put("delScenarioInfo", delScenarioInfo);

        return result;
    }
}
