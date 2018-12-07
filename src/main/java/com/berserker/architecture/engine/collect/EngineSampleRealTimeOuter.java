package com.berserker.architecture.engine.collect;

import com.berserker.architecture.engine.core.EngineController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EngineSampleRealTimeOuter {
    private static final Logger log = LoggerFactory.getLogger(EngineSampleRealTimeOuter.class);
    @Autowired
    private EngineController controller;
    @Autowired
    private SimpMessagingTemplate template;

    private BoundListOperations<String, String> runningSampleResultList;

    /**
     * @description                         获取Redis的队列列表
     * @param runningSampleResultList       Redis队列列表
     */
    public void setRunningSampleResultList(BoundListOperations<String, String> runningSampleResultList) {
        this.runningSampleResultList = runningSampleResultList;
    }

    /**
     * @description 通过WebSocket向前端发送数据(控制时间间隔发送数据,避免浏览器崩溃)
     */
    @Async
    public void sampleRealTimeOuter() {
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
                    nextSendTime = currentTime + 1000L;
                    template.convertAndSend("/sampleResult/data", sampleResult);
                    isSendSamplerResult = Boolean.FALSE;
                }
                // 如果当前时间大于等于上次发送信息时定义的nextSendTime,则将是否发送的标志位置为TRUE
                if (currentTime >= nextSendTime)
                    isSendSamplerResult = Boolean.TRUE;
                // 列表下标自加
                cursor++;
            }
            // 此时cursor的值和sampleResultLength相同,进行下一次循环
        }
    }
}
