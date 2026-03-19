package com.todo.controller;

import com.todo.dto.Result;
import com.todo.dto.TodoRequest;
import com.todo.entity.Todo;
import com.todo.service.TodoService;
import com.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Result<Todo> createTodo(@Validated @RequestBody TodoRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(Result.UNAUTHORIZED, "未登录");
        }

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setContent(request.getContent());
        todo.setAssigneeId(request.getAssigneeId());
        todo.setPriority(request.getPriority());
        todo.setDueTime(request.getDueTime());

        Todo createdTodo = todoService.createTodo(todo, userId);
        return Result.success(createdTodo);
    }

    @PostMapping("/admin-push")
    public Result<Todo> adminPushTodo(@Validated @RequestBody TodoRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(Result.UNAUTHORIZED, "未登录");
        }

        if (!userService.isAdmin(userId)) {
            return Result.error(Result.FORBIDDEN, "只有管理员可以推送待办消息");
        }

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setContent(request.getContent());
        todo.setAssigneeId(request.getAssigneeId());
        todo.setPriority(request.getPriority());
        todo.setDueTime(request.getDueTime());

        Todo createdTodo = todoService.adminCreateTodo(todo, userId);
        return Result.success(createdTodo);
    }

    @PostMapping("/update")
    public Result<Todo> updateTodo(@Validated @RequestBody TodoRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(Result.UNAUTHORIZED, "未登录");
        }

        if (request.getId() == null) {
            return Result.error("待办ID不能为空");
        }

        Todo todo = new Todo();
        todo.setId(request.getId());
        todo.setTitle(request.getTitle());
        todo.setContent(request.getContent());
        todo.setPriority(request.getPriority());
        todo.setDueTime(request.getDueTime());
        todo.setStatus(request.getStatus());

        Todo updatedTodo = todoService.updateTodo(todo, userId);
        return Result.success(updatedTodo);
    }

    @PostMapping("/update-priority/{todoId}")
    public Result<Todo> updatePriority(@PathVariable Long todoId, @RequestParam Integer priority, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(Result.UNAUTHORIZED, "未登录");
        }

        Todo updatedTodo = todoService.updatePriority(todoId, priority, userId);
        return Result.success(updatedTodo);
    }

    @PostMapping("/delete/{todoId}")
    public Result<Void> deleteTodo(@PathVariable Long todoId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(Result.UNAUTHORIZED, "未登录");
        }

        todoService.deleteTodo(todoId, userId);
        return Result.success();
    }

    @GetMapping("/detail/{todoId}")
    public Result<Todo> getTodoDetail(@PathVariable Long todoId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(Result.UNAUTHORIZED, "未登录");
        }

        Todo todo = todoService.getTodoById(todoId);
        if (todo == null) {
            return Result.error("待办不存在");
        }
        return Result.success(todo);
    }

    @GetMapping("/my-list")
    public Result<List<Todo>> getMyTodos(@RequestParam(required = false) Integer status, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(Result.UNAUTHORIZED, "未登录");
        }

        List<Todo> todos = todoService.getMyTodos(userId, status);
        return Result.success(todos);
    }

    @GetMapping("/created-list")
    public Result<List<Todo>> getCreatedTodos(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(Result.UNAUTHORIZED, "未登录");
        }

        List<Todo> todos = todoService.getCreatedTodos(userId);
        return Result.success(todos);
    }

    @GetMapping("/all-list")
    public Result<List<Todo>> getAllTodos(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(Result.UNAUTHORIZED, "未登录");
        }

        if (!userService.isAdmin(userId)) {
            return Result.error(Result.FORBIDDEN, "只有管理员可以查看所有待办");
        }

        List<Todo> todos = todoService.getAllTodos();
        return Result.success(todos);
    }
}
