package com.todo.service;

import com.todo.entity.User;
import com.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllNormalUsers() {
        return userRepository.findByRole(User.ROLE_USER);
    }

    public boolean isAdmin(Long userId) {
        User user = userRepository.findById(userId);
        return user != null && user.isAdmin();
    }
}
