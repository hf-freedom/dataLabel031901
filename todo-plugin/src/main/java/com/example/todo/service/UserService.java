package com.example.todo.service;

import com.example.todo.cache.LocalDataCache;
import com.example.todo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务类
 */
@Service
public class UserService {

    @Autowired
    private LocalDataCache localDataCache;

    /**
     * 创建用户
     */
    public User createUser(User user) {
        return localDataCache.addUser(user);
    }

    /**
     * 根据ID获取用户
     */
    public User getUserById(Long id) {
        return localDataCache.getUserById(id);
    }

    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        return localDataCache.getUserByUsername(username);
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return localDataCache.getAllUsers();
    }

    /**
     * 更新用户
     */
    public User updateUser(User user) {
        return localDataCache.updateUser(user);
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(Long id) {
        return localDataCache.deleteUser(id);
    }

    /**
     * 检查是否是管理员
     */
    public boolean isAdmin(Long userId) {
        User user = getUserById(userId);
        return user != null && User.UserRole.ADMIN.equals(user.getRole());
    }
}
