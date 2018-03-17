package com.bushmaster.architecture.engine;

import com.rometools.utils.Strings;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
@PropertySource(value = {"classpath:application.yml"}, encoding="UTF-8")
public class EngineScriptParser {
    @Value("${jmeterSetting.jmeter-home}")
    private String jmeterHomePath;
    @Value("${jmeterSetting.jmeter-properties}")
    private String jmeterPropertiesPath;


    public HashTree loadTestPlan(File scriptFile) {
        // 参数加载和启用SaveService
        EngineParamLoader.setEngineParam(jmeterHomePath, jmeterPropertiesPath);
        HashTree testPlanTree = null;
        try {
            testPlanTree = SaveService.loadTree(scriptFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testPlanTree;
    }

    public List<Map<String, Object>> parseScriptToParamFileSlots(File scriptFile) {
        HashTree testPlanTree = this.loadTestPlan(scriptFile);
        // 定义CSV Data Slot的列表
        List<Map<String, Object>> csvDataSetSlotList = new ArrayList<>();
        // 遍历测试用例找出CSV Data File Setting
        SearchByClass<TestElement> testElementVisitor = new SearchByClass<>(TestElement.class);
        testPlanTree.traverse(testElementVisitor);
        Collection<TestElement> testElementCollection = testElementVisitor.getSearchResults();
        for (TestElement testElement : testElementCollection) {
            if (testElement instanceof CSVDataSet) {
                CSVDataSet csvDataSet = (CSVDataSet) testElement;
                String csvIsEnable = csvDataSet.getProperty("TestElement.enabled").getStringValue();
                String testElementName = csvDataSet.getProperty("TestElement.name").getStringValue();
                String filename = csvDataSet.getProperty("filename").getStringValue();
                String variableNames = csvDataSet.getProperty("variableNames").getStringValue();
                // 当CSV Data Set设置为启用状态,并且文件路径和变量名不为空的情况下才返回
                if (Objects.equals(csvIsEnable, "true") && Strings.isNotEmpty(testElementName) && Strings.isNotEmpty(filename) && Strings.isNotEmpty(variableNames)) {
                    Map<String, Object> csvDataSetSlot = new HashMap<>();
                    csvDataSetSlot.put("testElementName", testElementName);
                    csvDataSetSlot.put("fileName", filename);
                    csvDataSetSlot.put("variableNames", variableNames);
                    csvDataSetSlotList.add(csvDataSetSlot);
                }
            }
        }
        return csvDataSetSlotList;
    }

    public HashTree correlationScriptToSetParamFile(File scriptFile, List<Map<String, String>> correlationParamFileList) {
        HashTree testPlanTree = this.loadTestPlan(scriptFile);
        // 定义CSV data set的列表
        List<CSVDataSet> csvDataSetList = new ArrayList<>();
        // 对脚本进行遍历
        SearchByClass<TestElement> testElementVisitor = new SearchByClass<>(TestElement.class);
        testPlanTree.traverse(testElementVisitor);
        Collection<TestElement> testElementCollection = testElementVisitor.getSearchResults();
        for (TestElement testElement: testElementCollection) {
            if (testElement instanceof CSVDataSet) {
                CSVDataSet csvDataSet = (CSVDataSet) testElement;
                String csvIsEnable = csvDataSet.getProperty("TestElement.enabled").getStringValue();
                String testElementName = csvDataSet.getProperty("TestElement.name").getStringValue();
                String filename = csvDataSet.getProperty("filename").getStringValue();
                String variableNames = csvDataSet.getProperty("variableNames").getStringValue();
                // 如果当前的CSV Data Set是启用状态
                if (Objects.equals(csvIsEnable, "true") && Strings.isNotEmpty(testElementName) && Strings.isNotEmpty(filename) && Strings.isNotEmpty(variableNames))
                    csvDataSetList.add(csvDataSet);                                 // 将当前的CSV Data Set放置到列表中
                else
                    csvDataSet.setProperty("TestElement.enabled", "false");         // 如果脚本中的CSV Data Set存在信息缺失的情况,则将当前的CSV Data Set置为禁用状态
            }
        }
        // 通过列表的遍历修改脚本中CSV Data Set的filename(CSV和脚本关联)
        for (CSVDataSet csvDataSetElement: csvDataSetList) {
            String csvDataSetSlotName = csvDataSetElement.getProperty("TestElement.name").getStringValue();
            for (Map<String, String> correlationParamFile: correlationParamFileList) {
                if (Objects.equals(csvDataSetSlotName, correlationParamFile.get("csvDataSetName")))
                    csvDataSetElement.setProperty("filename", correlationParamFile.get("paramFileAbsolutePath"));
            }
        }
        return testPlanTree;
    }

    public List<Map<String, Object>> parseScriptToDataStructure(File scriptFile) {
        HashTree testPlanTree = this.loadTestPlan(scriptFile);
        List<Map<String, Object>> scriptStructureList = new ArrayList<>();
        Map<String, Object> scriptDataStructure = null;
        // 将测试计划的HashTree转换成Java的Map结构
        scriptDataStructure = new HashMap<>();
        TestElement testPlanElement = (TestElement)testPlanTree.getArray()[0];
        createScriptDataStructure(testPlanElement, testPlanTree, scriptDataStructure);

        // 将获取到的Map类型的数据结构放入List
        scriptStructureList.add(scriptDataStructure);
        return scriptStructureList;
    }

    /**
     * @description 对测试计划进行递归遍历,生成测试计划的数据结构.
     * @param testElement               测试计划中的根节点
     * @param testPlanTree              根节点下元素
     * @param scriptStructureMap        测试计划的数据结构
     */
    private void createScriptDataStructure(TestElement testElement, HashTree testPlanTree, Map<String, Object> scriptStructureMap) {
        HashTree subTestPlanTree = testPlanTree.get(testElement);
        scriptStructureMap.put("text", testElement.getName());

        if (Objects.nonNull(subTestPlanTree)) {
            Object [] subTestElementArray = subTestPlanTree.getArray();             // 获取当前元素下的所有TestElement
            List<Map<String, Object>> subTestElementList = null;

            if (subTestElementArray.length > 0) {
                subTestElementList = new ArrayList<>();
                scriptStructureMap.put("nodes", subTestElementList);
            }
            for (Object subTestElementObj: subTestElementArray) {
                TestElement subTestElement = (TestElement) subTestElementObj;

                Map<String, Object> subTestElementMap = new HashMap<>();            // 将测试计划中的子元素添加到Map当中
                subTestElementMap.put("text", subTestElement.getName());
                subTestElementList.add(subTestElementMap);

                createScriptDataStructure(subTestElement, subTestPlanTree, subTestElementMap);
            }
        }
    }
}