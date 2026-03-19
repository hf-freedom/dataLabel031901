package com.todo.repository;

import com.todo.entity.User;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {

    private Cache<Long, User> userCache;
    private Cache<String, User> usernameCache;
    private AtomicLong idGenerator;

    @PostConstruct
    public void init() {
        userCache = Caffeine.newBuilder()
                .expireAfterWrite(365, TimeUnit.DAYS)
                .maximumSize(1000)
                .build();

        usernameCache = Caffeine.newBuilder()
                .expireAfterWrite(365, TimeUnit.DAYS)
                .maximumSize(1000)
                .build();

        idGenerator = new AtomicLong(1);

        initData();
    }

    private void initData() {
        User admin = new User(idGenerator.getAndIncrement(), "admin", "admin123", User.ROLE_ADMIN, "管理员");
        User user1 = new User(idGenerator.getAndIncrement(), "user1", "user123", User.ROLE_USER, "张三");
        User user2 = new User(idGenerator.getAndIncrement(), "user2", "user123", User.ROLE_USER, "李四");
        User user3 = new User(idGenerator.getAndIncrement(), "user3", "user123", User.ROLE_USER, "王五");

        save(admin);
        save(user1);
        save(user2);
        save(user3);
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        userCache.put(user.getId(), user);
        usernameCache.put(user.getUsername(), user);
        return user;
    }

    public User findById(Long id) {
        return userCache.getIfPresent(id);
    }

    public User findByUsername(String username) {
        return usernameCache.getIfPresent(username);
    }

    public List<User> findAll() {
        return new ArrayList<>(userCache.asMap().values());
    }

    public List<User> findByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User user : userCache.asMap().values()) {
            if (role.equals(user.getRole())) {
                result.add(user);
            }
        }
        return result;
    }

    public void deleteById(Long id) {
        User user = userCache.getIfPresent(id);
        if (user != null) {
            userCache.invalidate(id);
            usernameCache.invalidate(user.getUsername());
        }
    }

    public boolean existsById(Long id) {
        return userCache.getIfPresent(id) != null;
    }
}
