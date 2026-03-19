package com.example.todo.task;

import com.example.todo.entity.TodoMessage;
import com.example.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 代办提醒定时任务
 */
@Component
public class TodoReminderTask {

    private static final Logger logger = LoggerFactory.getLogger(TodoReminderTask.class);

    @Autowired
    private TodoService todoService;

    /**
     * 定时检查一小时内到期的代办并发送提醒
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void checkTodoDueInOneHour() {
        logger.debug("开始检查一小时内到期的代办消息...");

        try {
            List<TodoMessage> todos = todoService.getTodosDueInOneHour();
            if (!todos.isEmpty()) {
                todos.forEach(todo -> {
                logger.info("发现即将到期的代办：{}，执行人：{}，到期时间：{}",
                        todo.getTitle(),
                        todo.getExecutorName(),
                        todo.getDueTime());

                // 发送提醒消息
                todoService.sendReminder(todo);
            });
            }
        } catch (Exception e) {
            logger.error("检查代办提醒时发生异常", e);
        }

        logger.debug("代办提醒检查完成");
    }
}
