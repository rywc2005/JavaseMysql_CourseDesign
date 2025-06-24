package com.PFM.CD.example;

import com.PFM.CD.dao.UserDao;
import com.PFM.CD.dao.impl.UserDaoImpl;
import com.PFM.CD.entity.User;

import java.sql.SQLException;

public class UserExample {
    public static void main(String[] args) {
        try {
            // 使用接口引用实现类
            UserDao userDao = new UserDaoImpl();

            // 获取当前用户
            User user = userDao.findByUsername("rywc2005");

            if (user != null) {
                System.out.println("找到用户: " + user.getUsername());
                System.out.println("用户ID: " + user.getUserId());
                System.out.println("邮箱: " + user.getEmail());
                System.out.println("创建时间: " + user.getCreatedAt());
            } else {
                System.out.println("用户不存在");
            }

        } catch (SQLException e) {
            System.err.println("数据库操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}