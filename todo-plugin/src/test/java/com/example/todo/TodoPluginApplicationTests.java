package com.example.todo;

import com.example.todo.entity.TodoMessage;
import com.example.todo.entity.User;
import com.example.todo.service.TodoService;
import com.example.todo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TodoPluginApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private TodoService todoService;

    @Test
    void contextLoads() {
    }

    @Test
    void testDefaultUsers() {
        List<User> users = userService.getAllUsers();
        assertEquals(4, users.size()); // 1 admin + 3 users

        User admin = userService.getUserByUsername("admin");
        assertNotNull(admin);
        assertEquals(User.UserRole.ADMIN, admin.getRole());

        User zhangsan = userService.getUserByUsername("zhangsan");
        assertNotNull(zhangsan);
        assertEquals(User.UserRole.USER, zhangsan.getRole());
    }

    @Test
    void testCreateTodo() {
        User zhangsan = userService.getUserByUsername("zhangsan");

        TodoMessage todo = new TodoMessage();
        todo.setTitle("测试代办");
        todo.setContent("这是一个测试代办");
        todo.setDueTime(LocalDateTime.now().plusHours(2));
        todo.setPriority(TodoMessage.Priority.HIGH);

        TodoMessage created = todoService.createTodo(todo, zhangsan.getId());
        assertNotNull(created.getId());
        assertEquals(zhangsan.getId(), created.getCreatorId());
        assertEquals(zhangsan.getId(), created.getExecutorId());
        assertEquals(TodoMessage.Priority.HIGH, created.getPriority());
    }

    @Test
    void testUpdatePriority() {
        User lisi = userService.getUserByUsername("lisi");

        TodoMessage todo = new TodoMessage();
        todo.setTitle("测试修改优先级");
        todo.setDueTime(LocalDateTime.now().plusDays(1));
        TodoMessage created = todoService.createTodo(todo, lisi.getId());

        TodoMessage updated = todoService.updatePriority(created.getId(), TodoMessage.Priority.HIGH, lisi.getId());
        assertEquals(TodoMessage.Priority.HIGH, updated.getPriority());
    }

    @Test
    void testAdminPushTodo() {
        User admin = userService.getUserByUsername("admin");
        User wangwu = userService.getUserByUsername("wangwu");

        TodoMessage todo = new TodoMessage();
        todo.setTitle("管理员推送的代办");
        todo.setContent("请尽快完成");
        todo.setDueTime(LocalDateTime.now().plusHours(3));

        TodoMessage pushed = todoService.pushTodoByAdmin(todo, admin.getId(), wangwu.getId());
        assertNotNull(pushed.getId());
        assertEquals(admin.getId(), pushed.getCreatorId());
        assertEquals(wangwu.getId(), pushed.getExecutorId());
    }

    @Test
    void testGetMyTodoList() {
        User lisi = userService.getUserByUsername("lisi");

        TodoMessage todo = new TodoMessage();
        todo.setTitle("李四的代办");
        todo.setDueTime(LocalDateTime.now().plusDays(1));
        todoService.createTodo(todo, lisi.getId());

        List<TodoMessage> myTodos = todoService.getMyTodoList(lisi.getId());
        assertFalse(myTodos.isEmpty());
    }

    @Test
    void testDeleteTodo() {
        User zhangsan = userService.getUserByUsername("zhangsan");

        TodoMessage todo = new TodoMessage();
        todo.setTitle("待删除的代办");
        todo.setDueTime(LocalDateTime.now().plusDays(1));
        TodoMessage created = todoService.createTodo(todo, zhangsan.getId());

        boolean deleted = todoService.deleteTodo(created.getId(), zhangsan.getId());
        assertTrue(deleted);
        assertNull(todoService.getTodoById(created.getId()));
    }
}
