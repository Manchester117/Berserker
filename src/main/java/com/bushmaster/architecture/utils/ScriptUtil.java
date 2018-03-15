package com.bushmaster.architecture.utils;

import com.bushmaster.architecture.domain.entity.ScriptFileInfo;

import com.bushmaster.architecture.mapper.ScriptFileInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

@Component
public class ScriptUtil {
    @Autowired
    private ScriptFileInfoMapper scriptMapper;

    /**
     * @description             通过场景ID获取脚本文件的方法
     * @param scenarioId        场景ID
     * @return                  脚本对象
     */
    public File getScriptFileByScenarioId(Integer scenarioId) {
        ScriptFileInfo scriptInfo = scriptMapper.getScriptFileInfoByScenarioId(scenarioId);
        String scriptFileFolder = scriptInfo.getScriptFilePath();
        String scriptFileName = scriptInfo.getScriptFileName();

        String scriptFullPath = StringUtils.join(scriptFileFolder, scriptFileName);
        URI scriptFullURI = URI.create(scriptFullPath);
        return Paths.get(scriptFullURI).toFile();
    }
}
