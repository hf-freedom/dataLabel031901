package com.todo.service;

import com.todo.entity.Todo;
import com.todo.entity.User;
import com.todo.repository.TodoRepository;
import com.todo.repository.UserRepository;
import com.todo.util.ISendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ISendUtil sendUtil;

    public Todo createTodo(Todo todo, Long creatorId) {
        User creator = userRepository.findById(creatorId);
        if (creator == null) {
            throw new RuntimeException("创建人不存在");
        }

        User assignee = userRepository.findById(todo.getAssigneeId());
        if (assignee == null) {
            throw new RuntimeException("执行人不存在");
        }

        todo.setCreatorId(creatorId);
        todo.setCreatorName(creator.getName());
        todo.setAssigneeName(assignee.getName());
        todo.setCreateTime(LocalDateTime.now());
        todo.setStatus(Todo.STATUS_PENDING);
        todo.setNotified(false);

        return todoRepository.save(todo);
    }

    public Todo adminCreateTodo(Todo todo, Long adminId) {
        User admin = userRepository.findById(adminId);
        if (admin == null || !admin.isAdmin()) {
            throw new RuntimeException("只有管理员可以推送待办消息");
        }

        User assignee = userRepository.findById(todo.getAssigneeId());
        if (assignee == null) {
            throw new RuntimeException("执行人不存在");
        }

        todo.setCreatorId(adminId);
        todo.setCreatorName(admin.getName());
        todo.setAssigneeName(assignee.getName());
        todo.setCreateTime(LocalDateTime.now());
        todo.setStatus(Todo.STATUS_PENDING);
        todo.setNotified(false);

        return todoRepository.save(todo);
    }

    public Todo updateTodo(Todo todo, Long userId) {
        Todo existingTodo = todoRepository.findById(todo.getId());
        if (existingTodo == null) {
            throw new RuntimeException("待办不存在");
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!existingTodo.getCreatorId().equals(userId) && !user.isAdmin()) {
            throw new RuntimeException("没有权限修改此待办");
        }

        if (todo.getTitle() != null) {
            existingTodo.setTitle(todo.getTitle());
        }
        if (todo.getContent() != null) {
            existingTodo.setContent(todo.getContent());
        }
        if (todo.getPriority() != null) {
            existingTodo.setPriority(todo.getPriority());
        }
        if (todo.getDueTime() != null) {
            existingTodo.setDueTime(todo.getDueTime());
        }
        if (todo.getStatus() != null) {
            existingTodo.setStatus(todo.getStatus());
        }

        return todoRepository.save(existingTodo);
    }

    public Todo updatePriority(Long todoId, Integer priority, Long userId) {
        Todo todo = todoRepository.findById(todoId);
        if (todo == null) {
            throw new RuntimeException("待办不存在");
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!todo.getCreatorId().equals(userId) && !user.isAdmin()) {
            throw new RuntimeException("没有权限修改此待办优先级");
        }

        todo.setPriority(priority);
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long todoId, Long userId) {
        Todo todo = todoRepository.findById(todoId);
        if (todo == null) {
            throw new RuntimeException("待办不存在");
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!todo.getCreatorId().equals(userId) && !user.isAdmin()) {
            throw new RuntimeException("没有权限删除此待办");
        }

        todoRepository.deleteById(todoId);
    }

    public Todo getTodoById(Long todoId) {
        return todoRepository.findById(todoId);
    }

    public List<Todo> getMyTodos(Long userId, Integer status) {
        return todoRepository.findByAssigneeIdAndStatus(userId, status);
    }

    public List<Todo> getCreatedTodos(Long userId) {
        return todoRepository.findByCreatorId(userId);
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    @Scheduled(fixedRate = 60000)
    public void checkDueTodos() {
        LocalDateTime oneHourLater = LocalDateTime.now().plusHours(1);
        List<Todo> dueTodos = todoRepository.findByDueTimeBeforeAndNotNotified(oneHourLater);

        for (Todo todo : dueTodos) {
            if (todo.isDueWithinOneHour()) {
                sendUtil.sendTodoReminder(todo);
                todo.setNotified(true);
                todoRepository.save(todo);
            }
        }
    }
}
