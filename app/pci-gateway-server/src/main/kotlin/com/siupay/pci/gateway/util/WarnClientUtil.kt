//package com.siupay.pci.gateway.uitl
//
//import com.siupay.common.warn.enums.AlertLevel
//import com.siupay.common.warn.service.SystemWarnClient
//import lombok.Data
//import lombok.NoArgsConstructor
//import lombok.ToString
//import org.apache.commons.lang3.RandomUtils
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Component
//import java.util.concurrent.ArrayBlockingQueue
//import javax.annotation.PostConstruct
//import javax.annotation.Resource
//
//
//@Component
//class WarnClientUtil {
//    @Resource
//    private val systemWarnClient: SystemWarnClient? = null
//
//    private val log = LoggerFactory.getLogger(WarnClientUtil::class.java)
//
//    /**
//     * 警告日志入队
//     * @param title
//     * @param message
//     */
//    fun putWarnMessage(title: String, message: String, application: String) {
//        val warnPojo = WarnRequest(title, message, application)
//        log.info("DelayExecutorUtil putWarnMessage warnPojo:{}", warnPojo)
//        try {
//            WARN_QUEUE.put(warnPojo)
//        } catch (e: Exception) {
//            log.warn("DelayExecutorUtil putWarnMessage error:{},warnPojo:{}", e.message, warnPojo)
//        }
//    }
//
//    @PostConstruct
//    fun scanWarnQueue() {
//        Thread({
//            try {
//                var flag = true
//                while (flag) {
//                    if (WARN_QUEUE.isEmpty()) {
//                        log.info("WARN_QUEUE is empty")
//                    } else {
//                        val warnPojo: WarnRequest = WARN_QUEUE.poll()
//                        systemWarnClient?.send(
//                            warnPojo.title,
//                            warnPojo.message,
//                            warnPojo.application,
//                            AlertLevel.WARN
//                        )
//                    }
//                    //等待5s+不定长时间
//                    Thread.sleep(RandomUtils.nextInt(5000, 5500).toLong())
//                    if (WARN_QUEUE.size == MAX_QUEUE_SIZE) {
//                        flag = false
//                    }
//                }
//            } catch (e: Exception) {
//                log.warn("DelayExecutorUtil scanWarnQueue error:{}", e.message)
//            }
//        }, "ScanWarnQueue").start()
//    }
//
//    companion object {
//        private const val MAX_QUEUE_SIZE = 1000
//        private val WARN_QUEUE: ArrayBlockingQueue<WarnRequest> = ArrayBlockingQueue<WarnRequest>(MAX_QUEUE_SIZE)
//    }
//}
//
//@Data
//@NoArgsConstructor
//@ToString
//data class WarnRequest(val title: String, val message: String, val application: String? = null)