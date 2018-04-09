package com.bushmaster.architecture.engine.core;

import com.bushmaster.architecture.utils.FileStorageUtil;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class EngineTestPlanSetter {
    @Autowired
    private FileStorageUtil fileUtil;

    public HashTree testPlanSetting(Map<String, Object> testPlanInfo, List<ResultCollector> collectorList) {
        // 脚本读取
        File scriptFile = (File) testPlanInfo.get("scriptFile");
        Integer numThreads = (Integer) testPlanInfo.get("numThreads");
        Integer rampUp = (Integer) testPlanInfo.get("rampUp");
        Integer duration = (Integer) testPlanInfo.get("duration");

        // 定义测试计划树
        HashTree testPlanTree = null;
        try {
            // 读取测试用例
            testPlanTree = SaveService.loadTree(scriptFile);

            // 遍历测试用例
            SearchByClass<TestElement> testElementVisitor = new SearchByClass<>(TestElement.class);
            testPlanTree.traverse(testElementVisitor);
            Collection<TestElement> testElementCollection = testElementVisitor.getSearchResults();
            for (TestElement testElement : testElementCollection) {
                // 输出元素名称
                // System.out.println(testElement.getName());
                if (testElement instanceof ThreadGroup) {
                    // 定义线程组参数
                    ThreadGroup threadGroup = (ThreadGroup) testElement;
                    if (threadGroup.isEnabled()) {
                        threadGroup.setNumThreads(numThreads);
                        threadGroup.setRampUp(rampUp);
                        threadGroup.setScheduler(Boolean.TRUE);
                        threadGroup.setDuration(duration);
                        // 获取测试计划运行逻辑(启用调度器,确定运行时间)
                        LoopController loopController = (LoopController)threadGroup.getProperty("ThreadGroup.main_controller").getObjectValue();
                        loopController.setLoops(-1);                        // 不按照Loop次数来运行测试
                        loopController.setContinueForever(Boolean.FALSE);
                        System.out.println();
                    }
                }
            }

            // 在加入自定义结果收集之前对测试计划进行回写,避免由于添加自定义结果收集而导致测试计划结构出错.
            fileUtil.scriptBackWrite(scriptFile.getAbsolutePath(), testPlanTree);

            // 加入自定义的结果收集元素
            for (ResultCollector collector: collectorList)
                testPlanTree.add(testPlanTree.getArray()[0], collector);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return testPlanTree;
    }
}
