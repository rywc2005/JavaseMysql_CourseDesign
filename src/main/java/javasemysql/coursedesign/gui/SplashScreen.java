package javasemysql.coursedesign.gui;

import javax.swing.*;
import java.awt.*;

/**
 * 启动闪屏
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class SplashScreen extends JWindow {

    private int duration;

    /**
     * 构造函数
     *
     * @param duration 显示时间（毫秒）
     */
    public SplashScreen(int duration) {
        this.duration = duration;
    }

    /**
     * 显示闪屏
     */
    public void showSplash() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);

        // 创建标题面板
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        // 添加标题
        JLabel titleLabel = new JLabel("个人财务管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(66, 139, 202));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // 创建进度条
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("正在加载系统资源...");
        progressBar.setBorderPainted(false);
        progressBar.setForeground(new Color(66, 139, 202));

        // 创建底部版权面板
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        // 添加版权信息
        JLabel copyrightLabel = new JLabel("© 2025 个人财务管理系统 v1.0");
        copyrightLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        copyrightLabel.setForeground(new Color(120, 120, 120));
        footerPanel.add(copyrightLabel);

        // 添加组件到内容面板
        content.add(titlePanel, BorderLayout.NORTH);

        // 添加系统图标
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/splash_logo.png"));
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            content.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            // 如果加载图标失败，显示替代文本
            JLabel placeholderLabel = new JLabel("个人财务管理系统正在启动...");
            placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
            placeholderLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            content.add(placeholderLabel, BorderLayout.CENTER);
        }

        content.add(progressBar, BorderLayout.SOUTH);
        content.add(footerPanel, BorderLayout.SOUTH);

        // 添加边框
        content.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // 设置窗口内容
        setContentPane(content);

        // 设置窗口大小
        setSize(500, 300);

        // 居中显示
        setLocationRelativeTo(null);

        // 显示窗口
        setVisible(true);

        // 启动进度条线程
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(duration / 100);
                    progressBar.setValue(i);

                    // 更新加载状态文本
                    if (i < 30) {
                        progressBar.setString("正在加载系统资源...");
                    } else if (i < 60) {
                        progressBar.setString("正在初始化系统组件...");
                    } else if (i < 90) {
                        progressBar.setString("正在连接数据库...");
                    } else {
                        progressBar.setString("即将进入系统...");
                    }
                }

                // 关闭闪屏
                dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}