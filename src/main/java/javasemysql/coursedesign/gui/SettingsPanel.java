package javasemysql.coursedesign.gui;

import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.UserService;
import javasemysql.coursedesign.service.impl.UserServiceImpl;
import javasemysql.coursedesign.utils.LogUtils;
import javasemysql.coursedesign.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 系统设置面板
 */
public class SettingsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserService userService;
    private User currentUser;

    // UI组件
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> themeComboBox;
    private JComboBox<String> languageComboBox;

    /**
     * 构造方法
     *
     * @param mainFrame 主窗口
     */
    public SettingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userService = new UserServiceImpl();
        initComponents();
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("系统设置");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // 创建设置面板
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 个人信息设置
        JPanel profilePanel = createSectionPanel("个人信息设置");
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        addLabelAndField(profilePanel, "姓名:", nameField, 0);
        addLabelAndField(profilePanel, "邮箱:", emailField, 1);
        JButton updateProfileButton = new JButton("更新个人信息");
        updateProfileButton.addActionListener(this::updateProfile);
        profilePanel.add(updateProfileButton, createGBC(0, 2, 2));

        // 密码修改
        JPanel passwordPanel = createSectionPanel("密码修改");
        oldPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        addLabelAndField(passwordPanel, "原密码:", oldPasswordField, 0);
        addLabelAndField(passwordPanel, "新密码:", newPasswordField, 1);
        addLabelAndField(passwordPanel, "确认密码:", confirmPasswordField, 2);
        JButton updatePasswordButton = new JButton("修改密码");
        updatePasswordButton.addActionListener(this::updatePassword);
        passwordPanel.add(updatePasswordButton, createGBC(0, 3, 2));

        // 界面设置
        JPanel appearancePanel = createSectionPanel("界面设置");
        themeComboBox = new JComboBox<>(new String[]{"默认主题", "暗色主题", "浅色主题"});
        languageComboBox = new JComboBox<>(new String[]{"简体中文", "English"});
        addLabelAndField(appearancePanel, "主题:", themeComboBox, 0);
        addLabelAndField(appearancePanel, "语言:", languageComboBox, 1);
        JButton applyButton = new JButton("应用设置");
        applyButton.addActionListener(this::applySettings);
        appearancePanel.add(applyButton, createGBC(0, 2, 2));

        // 添加所有面板
        settingsPanel.add(profilePanel, gbc);
        gbc.gridy++;
        settingsPanel.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy++;
        settingsPanel.add(passwordPanel, gbc);
        gbc.gridy++;
        settingsPanel.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy++;
        settingsPanel.add(appearancePanel, gbc);

        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(settingsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 添加到主面板
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 创建分区面板
     */
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    /**
     * 添加标签和输入框
     */
    private void addLabelAndField(JPanel panel, String labelText, JComponent field, int row) {
        panel.add(new JLabel(labelText), createGBC(0, row));
        panel.add(field, createGBC(1, row));
    }

    /**
     * 创建网格约束
     */
    private GridBagConstraints createGBC(int x, int y) {
        return createGBC(x, y, 1);
    }

    private GridBagConstraints createGBC(int x, int y, int width) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    /**
     * 更新个人信息
     */
    private void updateProfile(ActionEvent e) {
        if (currentUser == null) {
            mainFrame.showErrorMessage("请先登录");
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        // 验证输入
        if (name.isEmpty() || email.isEmpty()) {
            mainFrame.showWarningMessage("姓名和邮箱不能为空");
            return;
        }

        if (!StringUtils.isValidEmail(email)) {
            mainFrame.showWarningMessage("邮箱格式不正确");
            return;
        }

        // 更新用户信息
        currentUser.setName(name);
        currentUser.setEmail(email);

        try {
            if (userService.updateUser(currentUser)) {
                mainFrame.showInfoMessage("个人信息更新成功");
                refreshData();
            } else {
                mainFrame.showErrorMessage("个人信息更新失败");
            }
        } catch (Exception ex) {
            LogUtils.error("更新用户信息失败", ex);
            mainFrame.showErrorMessage("更新失败：" + ex.getMessage());
        }
    }

    /**
     * 更新密码
     */
    private void updatePassword(ActionEvent e) {
        if (currentUser == null) {
            mainFrame.showErrorMessage("请先登录");
            return;
        }

        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // 验证输入
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            mainFrame.showWarningMessage("请填写所有密码字段");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            mainFrame.showWarningMessage("两次输入的新密码不一致");
            return;
        }

        // 验证旧密码
        if (!userService.verifyPassword(currentUser.getId(), oldPassword)) {
            mainFrame.showWarningMessage("原密码不正确");
            return;
        }

        try {
            if (userService.updatePassword(currentUser.getId(), newPassword)) {
                mainFrame.showInfoMessage("密码修改成功");
                clearPasswordFields();
            } else {
                mainFrame.showErrorMessage("密码修改失败");
            }
        } catch (Exception ex) {
            LogUtils.error("修改密码失败", ex);
            mainFrame.showErrorMessage("修改失败：" + ex.getMessage());
        }
    }

    /**
     * 应用界面设置
     */
    private void applySettings(ActionEvent e) {
        String theme = (String) themeComboBox.getSelectedItem();
        String language = (String) languageComboBox.getSelectedItem();

        // TODO: 实现主题和语言切换功能
        mainFrame.showInfoMessage("界面设置已保存，部分设置将在重启后生效");
    }

    /**
     * 清除密码字段
     */
    private void clearPasswordFields() {
        oldPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    /**
     * 更新用户数据
     */
    public void updateUserData(User user) {
        this.currentUser = user;
        refreshData();
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        if (currentUser == null) {
            nameField.setText("");
            emailField.setText("");
            clearPasswordFields();
            return;
        }

        nameField.setText(currentUser.getName());
        emailField.setText(currentUser.getEmail());
        clearPasswordFields();
    }
}