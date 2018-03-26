package com.bushmaster.architecture.engine.collect;

import com.bushmaster.architecture.engine.core.EngineController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class EngineSampleRealOuter {
    private static final Logger log = LoggerFactory.getLogger(EngineSampleRealOuter.class);
    private BoundListOperations<String, String> runningSampleResultList;
    @Autowired
    private EngineController controller;
    @Autowired
    private SimpMessagingTemplate template;


    public BoundListOperations<String, String> getRunningSampleResultList() {
        return runningSampleResultList;
    }

    public void setRunningSampleResultList(BoundListOperations<String, String> runningSampleResultList) {
        this.runningSampleResultList = runningSampleResultList;
    }

//    public void sampleRealOuter() {
//        // 如果Engine还处于运行状态则保持输出状态
//        while (controller.getEngineStatus()) {
//            String sampleResult = runningSampleResultList.rightPop();
//            if (Objects.nonNull(sampleResult)) {
//                log.info(sampleResult);
//                template.convertAndSend("/sampleResult/data", sampleResult);
//            }
//        }
//    }

    public void sampleRealOuter() {
        // 初始化游标
        long cursor = 0;
        // 如果Engine还处于运行状态则保持输出状态
        while (controller.getEngineStatus()) {
            // 首先获取Redis中SampleResult列表的长度
            long sampleResultLength = runningSampleResultList.size();
            // 如果列表还没有数据,则continue直到有数据为止
            if (cursor >= sampleResultLength)
                continue;
            // 如果列表中有数据,则按照索引读取,通过WebSocket发送到前端
            while (cursor < sampleResultLength) {
                String sampleResult = runningSampleResultList.index(cursor);
                log.info(sampleResult);
                template.convertAndSend("/sampleResult/data", sampleResult);
                cursor++;
            }
            // 此时cursor的值和sampleResultLength相同,进行下一次循环
        }
    }
}
