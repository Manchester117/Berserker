package com.bushmaster.architecture.engine.collect;

import com.bushmaster.architecture.engine.core.EngineController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

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

    /**
     * @description 通过WebSocket向前端发送数据(控制时间间隔发送数据,避免浏览器崩溃)
     */
    public void sampleRealOuter() {
        // 初始化游标
        long cursor = 0;
        // 定时器开关
        Boolean isSendSamplerResult = Boolean.TRUE;
        // 下一次发送时间
        Long nextSendTime = 0L;
        Long currentTime = 0L;
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
//                log.info(sampleResult);
                // 做一个定时器,每隔1秒向前端发送一个SamplerResult
                currentTime = System.currentTimeMillis();
                if (isSendSamplerResult) {
                    nextSendTime = currentTime + 750L;
                    template.convertAndSend("/sampleResult/data", sampleResult);
                    isSendSamplerResult = Boolean.FALSE;
                }
                if (currentTime >= nextSendTime)
                    isSendSamplerResult = Boolean.TRUE;
                // 列表下标自加
                cursor++;
            }
            // 此时cursor的值和sampleResultLength相同,进行下一次循环
        }
    }
}
