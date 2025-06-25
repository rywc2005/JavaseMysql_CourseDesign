package com.PFM.CD.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.PFM.CD.entity.User;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.factory.ServiceFactory;
import com.PFM.CD.service.interfaces.UserService;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-25
 * @Description:
 * @Version: 17.0
 */


public class LoginPanel extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginPanel() {

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("用户名:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("密码:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        JLabel emailLabel = new JLabel("邮箱:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passwordLabel, gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(passwordField, gbc);

        loginButton = new JButton("登录");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        registerButton = new JButton("注册");
        gbc.gridy = 3;
        add(registerButton, gbc);

        String email=emailLabel.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        UserService userService= ServiceFactory.getInstance().getUserService();

        // 添加登录和注册按钮的事件监听器
        loginButton.addActionListener(e -> {
            try {
                User user=userService.login(username, password);
                MainFrame mainframe=new MainFrame(user);

            } catch (ServiceException ex) {
                throw new RuntimeException(ex);
            }
        });

        registerButton.addActionListener(e -> {
            try {
                userService.register(username, password, email);
            } catch (ServiceException ex) {
                throw new RuntimeException(ex);
            }
        });

    }
}