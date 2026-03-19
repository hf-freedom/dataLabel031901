package com.example.todo.controller;

import com.example.todo.entity.TodoMessage;
import com.example.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代办消息控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    /**
     * 用户创建自己的代办
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<TodoMessage> createTodo(
            @RequestBody TodoMessage todoMessage,
            @PathVariable Long userId) {
        try {
            TodoMessage created = todoService.createTodo(todoMessage, userId);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 管理员推送代办消息给指定用户
     */
    @PostMapping("/admin/{adminId}/push/{executorId}")
    public ResponseEntity<TodoMessage> pushTodoByAdmin(
            @RequestBody TodoMessage todoMessage,
            @PathVariable Long adminId,
            @PathVariable Long executorId) {
        try {
            TodoMessage pushed = todoService.pushTodoByAdmin(todoMessage, adminId, executorId);
            return ResponseEntity.ok(pushed);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 用户更新自己的代办
     */
    @PutMapping("/{todoId}/user/{userId}")
    public ResponseEntity<TodoMessage> updateTodo(
            @PathVariable Long todoId,
            @PathVariable Long userId,
            @RequestBody TodoMessage todoMessage) {
        try {
            todoMessage.setId(todoId);
            TodoMessage updated = todoService.updateTodo(todoMessage, userId);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 修改代办优先级
     */
    @PutMapping("/{todoId}/user/{userId}/priority")
    public ResponseEntity<TodoMessage> updatePriority(
            @PathVariable Long todoId,
            @PathVariable Long userId,
            @RequestParam TodoMessage.Priority priority) {
        try {
            TodoMessage updated = todoService.updatePriority(todoId, priority, userId);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 用户删除自己的代办
     */
    @DeleteMapping("/{todoId}/user/{userId}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable Long todoId,
            @PathVariable Long userId) {
        try {
            boolean deleted = todoService.deleteTodo(todoId, userId);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * 根据ID获取代办详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoMessage> getTodoById(@PathVariable Long id) {
        TodoMessage todo = todoService.getTodoById(id);
        if (todo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(todo);
    }

    /**
     * 获取用户的代办列表（作为执行人）
     */
    @GetMapping("/user/{executorId}/my")
    public ResponseEntity<List<TodoMessage>> getMyTodoList(@PathVariable Long executorId) {
        List<TodoMessage> todos = todoService.getMyTodoList(executorId);
        return ResponseEntity.ok(todos);
    }

    /**
     * 获取用户创建的代办列表
     */
    @GetMapping("/user/{creatorId}/created")
    public ResponseEntity<List<TodoMessage>> getCreatedTodoList(@PathVariable Long creatorId) {
        List<TodoMessage> todos = todoService.getCreatedTodoList(creatorId);
        return ResponseEntity.ok(todos);
    }

    /**
     * 获取所有代办列表（管理员用）
     */
    @GetMapping("/admin/{adminId}/all")
    public ResponseEntity<List<TodoMessage>> getAllTodoList(@PathVariable Long adminId) {
        try {
            List<TodoMessage> todos = todoService.getAllTodoList(adminId);
            return ResponseEntity.ok(todos);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }
}
