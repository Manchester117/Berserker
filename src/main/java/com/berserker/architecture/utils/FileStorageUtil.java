package com.berserker.architecture.utils;

import com.berserker.architecture.domain.entity.ParamFileInfo;
import com.berserker.architecture.domain.entity.ScriptFileInfo;
import org.apache.commons.io.FileUtils;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Component
@PropertySource(value = {"classpath:application.yml"}, encoding="UTF-8")
public class FileStorageUtil {
    @Value("${fileStorage.script-folder-path}")
    private String scriptStoragePath;

    /**
     * @description         上传文件的基础方法,用于脚本和参数文件的上传
     * @param uploadFile    从上传脚本和上传参数文件传过来的文件对象
     * @param storagePath   上传路径,如果上传的脚本,则路径从配置文件读取.如果上传的是参数文件,则从上传参数文件的方法获取路径.
     * @param fileType      上传的文件类型(脚本,参数文件)
     * @return              Map类型的文件信息
     */
    private Map<String, Object> saveUploadFile(MultipartFile uploadFile, String storagePath, String fileType) {
        String fileName = uploadFile.getOriginalFilename();
        Path saveFolderPath = null;
        if (Objects.equals(fileType, "scriptFile")) {
            String folderName = UUID.randomUUID().toString().replace("-", "");
            saveFolderPath = Paths.get(storagePath, folderName);                                 // 脚本存放的文件夹
        } else if (Objects.equals(fileType, "paramFile")){
            saveFolderPath = Paths.get(storagePath);                                             // 参数化文件的文件夹
        } else {
            return null;                                                                         // 如果上传的类型是其他的则返回空,用于上层方法的逻辑判断
        }
        String saveFolderAbsPath = saveFolderPath.toAbsolutePath().toString();
        Path fileFullPath = Paths.get(saveFolderAbsPath, fileName);

        File fileObject = null;
        try {
            // 如果文件夹没有创建则创建文件夹
            if (Files.notExists(saveFolderPath))
                Files.createDirectories(saveFolderPath);
            // Path转成文件对象
            fileObject = fileFullPath.toFile();
            // 将上传的文件写到目的文件
            uploadFile.transferTo(fileObject);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("status", "Success");
        fileInfo.put("fileName", fileName);
        fileInfo.put("filePath", saveFolderPath.toUri().toString());
        fileInfo.put("fileObject", fileObject);
        fileInfo.put("uploadTime", new Date());
        return fileInfo;
    }

    /**
     * @description         上传脚本的方法
     * @param scriptFile    上传的脚本
     * @return              返回值可能是脚本的实体对象,也可能是null.
     */
    public ScriptFileInfo scriptFileUpload(MultipartFile scriptFile) {
        Map<String, Object> fileInfo = this.saveUploadFile(scriptFile, scriptStoragePath, "scriptFile");
        ScriptFileInfo scriptFileInfo = null;
        if (Objects.nonNull(fileInfo)) {
            scriptFileInfo = new ScriptFileInfo();
            scriptFileInfo.setScriptFileName(fileInfo.get("fileName").toString());
            scriptFileInfo.setScriptFilePath(fileInfo.get("filePath").toString());
            scriptFileInfo.setUploadTime((Date) fileInfo.get("uploadTime"));
        }
        return scriptFileInfo;
    }

    /**
     * @description                 上传参数文件的方法
     * @param paramFile             上传的参数文件
     * @param scriptStorageFolder   参数文件将要存放的路径
     * @return                      返回值可能是参数文件的实体对象,也可能是null.
     */
    public ParamFileInfo paramFileUpload(MultipartFile paramFile, String scriptStorageFolder) {
        Map<String, Object> fileInfo = this.saveUploadFile(paramFile, scriptStorageFolder, "paramFile");
        ParamFileInfo paramFileInfo = null;
        if (Objects.nonNull(fileInfo)) {
            paramFileInfo = new ParamFileInfo();
            paramFileInfo.setParamFileName(fileInfo.get("fileName").toString());
            paramFileInfo.setParamFilePath(fileInfo.get("filePath").toString());
            paramFileInfo.setUploadTime((Date) fileInfo.get("uploadTime"));
        }
        return paramFileInfo;
    }

    /**
     * @description                 在HashTree关联参数文件后,将HashTree转成JMX脚本
     * @param scriptAbsolutePath    脚本存放的绝对路径
     * @param testPlanTree          要写出的HashTree
     * @return                      返回是否写出成功
     */
    public Boolean scriptBackWrite(String scriptAbsolutePath, HashTree testPlanTree) {
        File scriptFile = new File(scriptAbsolutePath);
        OutputStream out = null;
        try {
            out = new FileOutputStream(scriptFile);
            SaveService.saveTree(testPlanTree, out);
            out.close();
            return Boolean.TRUE;
        } catch (IOException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    /**
     * @description                 文件的删除方法
     * @param delFilePath           文件路径
     * @return                      返回是否删除的Boolean
     */
    private Boolean deleteFile(Path delFilePath) {
        try {
            Files.delete(delFilePath);
            return Boolean.TRUE;
        } catch (IOException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    /**
     * @description                 删除文件夹的方法
     * @param scriptFolderUri       文件的URI地址
     * @return                      返回是否删除的Boolean
     */
    public Boolean deleteScriptFolder(Path scriptFolderUri) {
        try {
            FileUtils.deleteDirectory(scriptFolderUri.toFile());
            return Boolean.TRUE;
        } catch (IOException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    /**
     * @description                 删除脚本的方法
     * @param scriptFileInfo        脚本的实体对象
     * @return                      返回是否删除的Boolean
     */
    public Boolean deleteStorageScript(ScriptFileInfo scriptFileInfo) {
        String scriptName = scriptFileInfo.getScriptFileName();
        String scriptStorageUri = scriptFileInfo.getScriptFilePath();
        String scriptStorageFolder = Paths.get(URI.create(scriptStorageUri)).toFile().getAbsolutePath();
        Path scriptUriPath = Paths.get(scriptStorageFolder, scriptName);
        return !Files.notExists(scriptUriPath) && this.deleteFile(scriptUriPath);
    }

    /**
     * @description                 删除参数文件的方法
     * @param paramFileInfo         参数文件的实体对象
     * @return                      返回是否删除的Boolean
     */
    public Boolean deleteStorageParamFile(ParamFileInfo paramFileInfo) {
        String paramFileName = paramFileInfo.getParamFileName();
        String paramStorageUri = paramFileInfo.getParamFilePath();
        String paramStorageFolder = Paths.get(URI.create(paramStorageUri)).toFile().getAbsolutePath();
        Path paramFileUri = Paths.get(paramStorageFolder, paramFileName);
        return !Files.notExists(paramFileUri) && this.deleteFile(paramFileUri);
    }
}
