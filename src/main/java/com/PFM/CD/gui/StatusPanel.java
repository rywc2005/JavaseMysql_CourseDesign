package com.PFM.CD.gui;

import com.PFM.CD.entity.User;
import com.PFM.CD.service.factory.ServiceFactory;
import com.PFM.CD.service.interfaces.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 状态面板/个人信息面板
 * - 普通用户：可修改用户名、密码、邮箱
 * - 管理员：显示系统总用户数
 */
public class StatusPanel extends JPanel {
    // for both
    private JLabel userLabel;
    private JLabel roleLabel;
    // for admin
    private JLabel totalUserLabel;
    // for user edit
    private JTextField usernameEdit;
    private JPasswordField passwordEdit;
    private JTextField emailEdit;
    private JButton saveBtn;
    private JLabel stateLabel;

    private User user;

    public StatusPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(20, 0));
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(16, 32, 16, 32));

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        userLabel = new JLabel("用户：" + (user != null ? user.getUsername() : "未知"));
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        userLabel.setForeground(new Color(51, 102, 255));
        leftPanel.add(userLabel);

        roleLabel = new JLabel("角色：" + getRoleDisplay(user));
        roleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        roleLabel.setForeground(new Color(80, 80, 80));
        leftPanel.add(roleLabel);

        add(leftPanel, BorderLayout.WEST);

        if (isAdmin(user)) {
            // 管理员：显示总用户数
            totalUserLabel = new JLabel();
            totalUserLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
            totalUserLabel.setForeground(new Color(200, 120, 60));
            updateTotalUserCount();
            JPanel centerPanel = new JPanel();
            centerPanel.setOpaque(false);
            centerPanel.add(totalUserLabel);
            add(centerPanel, BorderLayout.CENTER);
        } else {
            // 普通用户：显示编辑面板
            JPanel editPanel = new JPanel();
            editPanel.setOpaque(false);
            editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
            editPanel.setBorder(BorderFactory.createTitledBorder("个人信息管理"));

            usernameEdit = new JTextField(user != null ? user.getUsername() : "", 18);
            usernameEdit.setFont(new Font("微软雅黑", Font.PLAIN, 15));
            usernameEdit.setMaximumSize(new Dimension(220, 32));
            addLabeledField(editPanel, "用户名：", usernameEdit);

            passwordEdit = new JPasswordField("", 18);
            passwordEdit.setFont(new Font("微软雅黑", Font.PLAIN, 15));
            passwordEdit.setMaximumSize(new Dimension(220, 32));
            addLabeledField(editPanel, "新密码：", passwordEdit);

            emailEdit = new JTextField(user != null ? user.getEmail() : "", 18);
            emailEdit.setFont(new Font("微软雅黑", Font.PLAIN, 15));
            emailEdit.setMaximumSize(new Dimension(220, 32));
            addLabeledField(editPanel, "邮  箱：", emailEdit);

            saveBtn = createFlatButton("保存更改", new Color(51, 102, 255), new Color(80, 130, 255));
            saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            saveBtn.addActionListener(this::onSave);
            editPanel.add(Box.createVerticalStrut(10));
            editPanel.add(saveBtn);

            stateLabel = new JLabel(" ");
            stateLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            stateLabel.setForeground(new Color(40, 120, 90));
            editPanel.add(Box.createVerticalStrut(8));
            editPanel.add(stateLabel);

            add(editPanel, BorderLayout.CENTER);
        }
    }

    private void addLabeledField(JPanel parent, String label, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        panel.add(l);
        panel.add(field);
        parent.add(panel);
        parent.add(Box.createVerticalStrut(6));
    }

    private JButton createFlatButton(String text, Color color, Color hover) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 15));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 22, 7, 22));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    private boolean isAdmin(User user) {
        if (user == null) return false;
        String role = user.getRole();
        return role != null && "admin".equalsIgnoreCase(role);
    }

    private String getRoleDisplay(User user) {
        if (user == null) return "未知";
        if (user.getRole() != null) {
            if ("admin".equalsIgnoreCase(user.getRole())) return "管理员";
            if ("user".equalsIgnoreCase(user.getRole())) return "普通用户";
            return user.getRole();
        }
        return "普通用户";
    }

    private void updateTotalUserCount() {
        try {
            UserService userService = ServiceFactory.getInstance().getUserService();
            int total = userService.getTotalUserCount();
            totalUserLabel.setText("系统总用户数：" + total);
        } catch (Exception e) {
            totalUserLabel.setText("系统总用户数：获取失败");
        }
    }

    private void onSave(ActionEvent evt) {
        String newUsername = usernameEdit.getText().trim();
        String newPassword = new String(passwordEdit.getPassword());
        String newEmail = emailEdit.getText().trim();

        if (newUsername.isEmpty()) {
            stateLabel.setText("用户名不能为空");
            return;
        }
        if (newEmail.isEmpty() || !newEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            stateLabel.setText("请输入有效的邮箱地址");
            return;
        }
        try {
            UserService userService = ServiceFactory.getInstance().getUserService();
            // 仅更改填写的字段，密码可选
            boolean changed = false;
            if (!newUsername.equals(user.getUsername())) {
                User newuser = new User( newUsername, user.getPasswordHash(), user.getEmail());
                userService.updateUserInfo(newuser);
                user.setUsername(newUsername);
                changed = true;
            }
            if (!newEmail.equals(user.getEmail())) {
                User newuser = new User( newUsername, user.getPasswordHash(), newEmail);
                userService.updateUserInfo(newuser);
                user.setEmail(newEmail);
                changed = true;
            }
            if (!newPassword.isEmpty()) {
                userService.changePassword(user.getUserId(), user.getPasswordHash(), newPassword);
                changed = true;
            }
            if (changed) {
                stateLabel.setText("保存成功！");
                userLabel.setText("用户：" + user.getUsername());
            } else {
                stateLabel.setText("没有更改内容");
            }
        } catch (Exception e) {
            stateLabel.setText("保存失败：" + e.getMessage());
        }
    }

    // 外部可调用更新用户信息
    public void setUser(User user) {
        this.user = user;
        userLabel.setText("用户：" + (user != null ? user.getUsername() : "未知"));
        roleLabel.setText("角色：" + getRoleDisplay(user));
        if (!isAdmin(user)) {
            if (usernameEdit != null) usernameEdit.setText(user != null ? user.getUsername() : "");
            if (emailEdit != null) emailEdit.setText(user != null ? user.getEmail() : "");
            if (passwordEdit != null) passwordEdit.setText("");
            if (stateLabel != null) stateLabel.setText(" ");
        } else {
            updateTotalUserCount();
        }
    }

}