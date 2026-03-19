package com.example.todo.util;

import com.example.todo.entity.TodoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 发送消息工具实现类
 */
@Component
public class SendUtilImpl implements ISendUtil {

    private static final Logger logger = LoggerFactory.getLogger(SendUtilImpl.class);

    @Override
    public void send(TodoMessage todoMessage) {
        // 方法可以为空，这里简单记录日志
        logger.info("发送到期提醒消息：代办【{}】，执行人：{}，到期时间：{}",
                todoMessage.getTitle(),
                todoMessage.getExecutorName(),
                todoMessage.getDueTime());

        // 实际业务中这里可以实现具体的发送逻辑，如：短信、邮件、推送等
    }
}
