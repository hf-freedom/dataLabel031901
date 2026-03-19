package com.example.todo.service;

import com.example.todo.cache.LocalDataCache;
import com.example.todo.entity.TodoMessage;
import com.example.todo.entity.User;
import com.example.todo.util.ISendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代办消息服务类
 */
@Service
public class TodoService {

    @Autowired
    private LocalDataCache localDataCache;

    @Autowired
    private UserService userService;

    @Autowired
    private ISendUtil sendUtil;

    /**
     * 用户创建自己的代办
     */
    public TodoMessage createTodo(TodoMessage todoMessage, Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        todoMessage.setCreatorId(userId);
        todoMessage.setCreatorName(user.getUsername());
        // 如果没有指定执行人，默认为创建人自己
        if (todoMessage.getExecutorId() == null) {
            todoMessage.setExecutorId(userId);
            todoMessage.setExecutorName(user.getUsername());
        } else {
            User executor = userService.getUserById(todoMessage.getExecutorId());
            if (executor != null) {
                todoMessage.setExecutorName(executor.getUsername());
            }
        }
        if (todoMessage.getStatus() == null) {
            todoMessage.setStatus(TodoMessage.TodoStatus.PENDING);
        }
        if (todoMessage.getPriority() == null) {
            todoMessage.setPriority(TodoMessage.Priority.MEDIUM);
        }

        return localDataCache.addTodoMessage(todoMessage);
    }

    /**
     * 管理员推送代办消息给指定用户
     */
    public TodoMessage pushTodoByAdmin(TodoMessage todoMessage, Long adminId, Long executorId) {
        // 验证是否是管理员
        if (!userService.isAdmin(adminId)) {
            throw new SecurityException("只有管理员可以推送代办消息");
        }

        User admin = userService.getUserById(adminId);
        User executor = userService.getUserById(executorId);
        if (admin == null || executor == null) {
            throw new IllegalArgumentException("管理员或执行人不存在");
        }

        todoMessage.setCreatorId(adminId);
        todoMessage.setCreatorName(admin.getUsername());
        todoMessage.setExecutorId(executorId);
        todoMessage.setExecutorName(executor.getUsername());
        if (todoMessage.getStatus() == null) {
            todoMessage.setStatus(TodoMessage.TodoStatus.PENDING);
        }
        if (todoMessage.getPriority() == null) {
            todoMessage.setPriority(TodoMessage.Priority.MEDIUM);
        }

        return localDataCache.addTodoMessage(todoMessage);
    }

    /**
     * 用户更新自己的代办
     */
    public TodoMessage updateTodo(TodoMessage todoMessage, Long userId) {
        TodoMessage existingTodo = localDataCache.getTodoMessageById(todoMessage.getId());
        if (existingTodo == null) {
            throw new IllegalArgumentException("代办消息不存在");
        }

        // 验证权限：只能更新自己创建或执行的代办
        if (!userId.equals(existingTodo.getCreatorId()) && !userId.equals(existingTodo.getExecutorId())) {
            throw new SecurityException("没有权限更新此代办消息");
        }

        // 保留不可修改字段
        todoMessage.setCreatorId(existingTodo.getCreatorId());
        todoMessage.setCreatorName(existingTodo.getCreatorName());
        todoMessage.setCreateTime(existingTodo.getCreateTime());

        // 如果执行人变更，更新执行人名称
        if (todoMessage.getExecutorId() != null && !todoMessage.getExecutorId().equals(existingTodo.getExecutorId())) {
            User executor = userService.getUserById(todoMessage.getExecutorId());
            if (executor != null) {
                todoMessage.setExecutorName(executor.getUsername());
            }
        } else {
            todoMessage.setExecutorId(existingTodo.getExecutorId());
            todoMessage.setExecutorName(existingTodo.getExecutorName());
        }

        return localDataCache.updateTodoMessage(todoMessage);
    }

    /**
     * 修改代办优先级
     */
    public TodoMessage updatePriority(Long todoId, TodoMessage.Priority priority, Long userId) {
        TodoMessage existingTodo = localDataCache.getTodoMessageById(todoId);
        if (existingTodo == null) {
            throw new IllegalArgumentException("代办消息不存在");
        }

        // 验证权限：只能修改自己相关的代办优先级
        if (!userId.equals(existingTodo.getCreatorId()) && !userId.equals(existingTodo.getExecutorId())) {
            throw new SecurityException("没有权限修改此代办的优先级");
        }

        existingTodo.setPriority(priority);
        return localDataCache.updateTodoMessage(existingTodo);
    }

    /**
     * 用户删除自己的代办
     */
    public boolean deleteTodo(Long todoId, Long userId) {
        TodoMessage existingTodo = localDataCache.getTodoMessageById(todoId);
        if (existingTodo == null) {
            return false;
        }

        // 验证权限：只能删除自己创建的代办
        if (!userId.equals(existingTodo.getCreatorId())) {
            throw new SecurityException("没有权限删除此代办消息");
        }

        return localDataCache.deleteTodoMessage(todoId);
    }

    /**
     * 根据ID获取代办详情
     */
    public TodoMessage getTodoById(Long id) {
        return localDataCache.getTodoMessageById(id);
    }

    /**
     * 获取用户的代办列表（作为执行人）
     */
    public List<TodoMessage> getMyTodoList(Long executorId) {
        return localDataCache.getTodoMessagesByExecutorId(executorId);
    }

    /**
     * 获取用户创建的代办列表
     */
    public List<TodoMessage> getCreatedTodoList(Long creatorId) {
        return localDataCache.getTodoMessagesByCreatorId(creatorId);
    }

    /**
     * 获取所有代办列表（管理员用）
     */
    public List<TodoMessage> getAllTodoList(Long adminId) {
        if (!userService.isAdmin(adminId)) {
            throw new SecurityException("只有管理员可以查看所有代办消息");
        }
        return localDataCache.getAllTodoMessages();
    }

    /**
     * 获取一小时内到期且未提醒的代办列表
     */
    public List<TodoMessage> getTodosDueInOneHour() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        return localDataCache.getAllTodoMessages().stream()
                .filter(todo -> !todo.isReminded())
                .filter(todo -> todo.getDueTime() != null)
                .filter(todo -> todo.getStatus() == null ||
                        TodoMessage.TodoStatus.PENDING.equals(todo.getStatus()) ||
                        TodoMessage.TodoStatus.IN_PROGRESS.equals(todo.getStatus()))
                .filter(todo -> {
                    LocalDateTime dueTime = todo.getDueTime();
                    return dueTime.isAfter(now) && dueTime.isBefore(oneHourLater);
                })
                .collect(Collectors.toList());
    }

    /**
     * 标记代办已提醒
     */
    public void markAsReminded(Long todoId) {
        TodoMessage todo = localDataCache.getTodoMessageById(todoId);
        if (todo != null) {
            todo.setReminded(true);
            localDataCache.updateTodoMessage(todo);
        }
    }

    /**
     * 发送到期提醒并标记
     */
    public void sendReminder(TodoMessage todo) {
        sendUtil.send(todo);
        markAsReminded(todo.getId());
    }
}
