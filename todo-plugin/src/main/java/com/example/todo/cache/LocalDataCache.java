package com.example.todo.cache;

import com.example.todo.entity.TodoMessage;
import com.example.todo.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 本地缓存数据存储类，模拟数据库功能
 */
@Component
public class LocalDataCache {

    // 用户数据存储
    private final Map<Long, User> userMap = new ConcurrentHashMap<>();
    private final AtomicLong userIdGenerator = new AtomicLong(1);

    // 代办消息数据存储
    private final Map<Long, TodoMessage> todoMessageMap = new ConcurrentHashMap<>();
    private final AtomicLong todoIdGenerator = new AtomicLong(1);

    // ==================== 用户相关操作 ====================

    /**
     * 添加用户
     */
    public User addUser(User user) {
        if (user.getId() == null) {
            user.setId(userIdGenerator.getAndIncrement());
        }
        userMap.put(user.getId(), user);
        return user;
    }

    /**
     * 根据ID获取用户
     */
    public User getUserById(Long id) {
        return userMap.get(id);
    }

    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        return userMap.values().stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    /**
     * 更新用户
     */
    public User updateUser(User user) {
        if (user.getId() == null || !userMap.containsKey(user.getId())) {
            return null;
        }
        userMap.put(user.getId(), user);
        return user;
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(Long id) {
        return userMap.remove(id) != null;
    }

    // ==================== 代办消息相关操作 ====================

    /**
     * 添加代办消息
     */
    public TodoMessage addTodoMessage(TodoMessage todoMessage) {
        if (todoMessage.getId() == null) {
            todoMessage.setId(todoIdGenerator.getAndIncrement());
        }
        todoMessageMap.put(todoMessage.getId(), todoMessage);
        return todoMessage;
    }

    /**
     * 根据ID获取代办消息
     */
    public TodoMessage getTodoMessageById(Long id) {
        return todoMessageMap.get(id);
    }

    /**
     * 获取所有代办消息
     */
    public List<TodoMessage> getAllTodoMessages() {
        return new ArrayList<>(todoMessageMap.values());
    }

    /**
     * 根据执行人ID获取代办消息列表
     */
    public List<TodoMessage> getTodoMessagesByExecutorId(Long executorId) {
        List<TodoMessage> result = new ArrayList<>();
        for (TodoMessage todo : todoMessageMap.values()) {
            if (executorId.equals(todo.getExecutorId())) {
                result.add(todo);
            }
        }
        return result;
    }

    /**
     * 根据创建人ID获取代办消息列表
     */
    public List<TodoMessage> getTodoMessagesByCreatorId(Long creatorId) {
        List<TodoMessage> result = new ArrayList<>();
        for (TodoMessage todo : todoMessageMap.values()) {
            if (creatorId.equals(todo.getCreatorId())) {
                result.add(todo);
            }
        }
        return result;
    }

    /**
     * 更新代办消息
     */
    public TodoMessage updateTodoMessage(TodoMessage todoMessage) {
        if (todoMessage.getId() == null || !todoMessageMap.containsKey(todoMessage.getId())) {
            return null;
        }
        todoMessageMap.put(todoMessage.getId(), todoMessage);
        return todoMessage;
    }

    /**
     * 删除代办消息
     */
    public boolean deleteTodoMessage(Long id) {
        return todoMessageMap.remove(id) != null;
    }

    /**
     * 清空所有数据（仅用于测试）
     */
    public void clearAll() {
        userMap.clear();
        todoMessageMap.clear();
        userIdGenerator.set(1);
        todoIdGenerator.set(1);
    }
}
