package com.example.todo.config;

import com.example.todo.entity.User;
import com.example.todo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化类，项目启动时初始化默认用户数据
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始初始化默认用户数据...");

        // 检查是否已有用户数据
        if (userService.getAllUsers().isEmpty()) {
            // 创建管理员用户
            User admin = new User();
            admin.setUsername("admin");
            admin.setRole(User.UserRole.ADMIN);
            userService.createUser(admin);
            logger.info("创建管理员用户：{} (ID: {})", admin.getUsername(), admin.getId());

            // 创建3个普通用户
            User user1 = new User();
            user1.setUsername("zhangsan");
            user1.setRole(User.UserRole.USER);
            userService.createUser(user1);
            logger.info("创建普通用户：{} (ID: {})", user1.getUsername(), user1.getId());

            User user2 = new User();
            user2.setUsername("lisi");
            user2.setRole(User.UserRole.USER);
            userService.createUser(user2);
            logger.info("创建普通用户：{} (ID: {})", user2.getUsername(), user2.getId());

            User user3 = new User();
            user3.setUsername("wangwu");
            user3.setRole(User.UserRole.USER);
            userService.createUser(user3);
            logger.info("创建普通用户：{} (ID: {})", user3.getUsername(), user3.getId());

            logger.info("默认用户数据初始化完成！共创建1个管理员，3个普通用户。");
        } else {
            logger.info("已存在用户数据，跳过初始化");
        }
    }
}
