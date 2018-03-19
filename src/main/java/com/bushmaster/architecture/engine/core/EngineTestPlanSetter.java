package com.bushmaster.architecture.engine.core;

import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class EngineTestPlanSetter {
    public HashTree testPlanSetting(Map<String, Object> testPlanInfo, List<ResultCollector> collectorList) {
        // 脚本读取
        File scriptFileJMX = (File) testPlanInfo.get("scriptFile");
        Integer numThreads = (Integer) testPlanInfo.get("numThreads");
        Integer rampUp = (Integer) testPlanInfo.get("rampUp");
        Integer duration = (Integer) testPlanInfo.get("duration");

        // 定义测试计划树
        HashTree testPlanTree = null;
        try {
            // 读取测试用例
            testPlanTree = SaveService.loadTree(scriptFileJMX);

            // 加入自定义的结果收集元素
            for (ResultCollector collector: collectorList)
                testPlanTree.add(testPlanTree.getArray()[0], collector);

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
                    threadGroup.setNumThreads(numThreads);
                    threadGroup.setRampUp(rampUp);
                    threadGroup.setDuration(duration);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return testPlanTree;
    }
}
