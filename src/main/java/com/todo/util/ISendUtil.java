package com.todo.util;

import com.todo.entity.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ISendUtil {

    private static final Logger logger = LoggerFactory.getLogger(ISendUtil.class);

    public void send(String msg) {
        logger.info("发送消息: {}", msg);
    }

    public void sendTodoReminder(Todo todo) {
        String message = String.format("待办提醒: [%s] 将在1小时内到期，执行人: %s，优先级: %s",
                todo.getTitle(),
                todo.getAssigneeName(),
                getPriorityText(todo.getPriority()));
        send(message);
    }

    private String getPriorityText(Integer priority) {
        if (priority == null) return "未知";
        switch (priority) {
            case Todo.PRIORITY_HIGH:
                return "高";
            case Todo.PRIORITY_MEDIUM:
                return "中";
            case Todo.PRIORITY_LOW:
                return "低";
            default:
                return "未知";
        }
    }
}
