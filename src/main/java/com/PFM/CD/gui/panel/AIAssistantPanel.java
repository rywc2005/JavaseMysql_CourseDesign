package com.PFM.CD.gui.panel;

import com.PFM.CD.service.OpenRouterService;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI助手面板 - 提供智能问答功能
 * 豪华现代风格的聊天界面，支持左右对话气泡和Markdown格式
 *
 * @Author: down812
 * @CreateTime: 2025-06-29
 * @Version: 3.0 Premium
 */
public class AIAssistantPanel extends JPanel {
    // 高端配色方案
    private static final Color BG_COLOR = new Color(245, 247, 250);
    private static final Color PANEL_BG_COLOR = new Color(255, 255, 255);
    private static final Color ACCENT_COLOR = new Color(66, 99, 235);
    private static final Color ACCENT_LIGHT = new Color(108, 132, 233);
    private static final Color ACCENT_DARK = new Color(50, 76, 187);
    private static final Color SECONDARY_COLOR = new Color(80, 200, 120);
    private static final Color USER_BUBBLE_COLOR = new Color(235, 242, 252);
    private static final Color USER_BUBBLE_BORDER = new Color(211, 226, 248);
    private static final Color AI_BUBBLE_COLOR = new Color(255, 255, 255);
    private static final Color AI_BUBBLE_BORDER = new Color(230, 230, 230);
    private static final Color TEXT_PRIMARY = new Color(50, 50, 50);
    private static final Color TEXT_SECONDARY = new Color(120, 120, 120);
    private static final Color TEXT_LIGHT = new Color(150, 150, 150);
    private static final Color BORDER_COLOR = new Color(230, 230, 230);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 15);

    // 豪华字体
    private static final Font TITLE_FONT = new Font("微软雅黑", Font.BOLD, 18);
    private static final Font CONTENT_FONT = new Font("微软雅黑", Font.PLAIN, 15);
    private static final Font BUTTON_FONT = new Font("微软雅黑", Font.BOLD, 14);
    private static final Font SMALL_FONT = new Font("微软雅黑", Font.PLAIN, 12);
    private static final Font TIME_FONT = new Font("微软雅黑", Font.PLAIN, 11);

    // 圆角和阴影
    private static final int CORNER_RADIUS = 12;
    private static final int BUBBLE_RADIUS = 18;

    // 服务和线程池
    private final OpenRouterService aiService;
    private final ExecutorService executorService;

    // 组件
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;
    private JLabel statusLabel;
    private JPanel statusBarPanel;
    private String currentDateTime;
    private String username;

    // 底部按钮区
    private JButton clearButton;
    private JButton exportButton;

    // 思考计时相关
    private Timer thinkingTimer;
    private long startThinkingTime;
    private JLabel thinkingTimeLabel;
    private int thinkingSeconds = 0;

    // Markdown 解析器
    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;

    public AIAssistantPanel(String apiKey, String username) {
        // 初始化服务和工具
        this.aiService = new OpenRouterService(apiKey);
        this.executorService = Executors.newSingleThreadExecutor();
        this.username = username;
        this.currentDateTime = "2025-06-29 14:49:56"; // 使用提供的时间
        // 初始化Markdown解析器
        this.markdownParser = Parser.builder().build();
        this.htmlRenderer = HtmlRenderer.builder()
                .escapeHtml(false)
                .attributeProviderFactory(context -> (node, tagName, attributes) -> {
                    if (tagName.equals("code")) {
                        attributes.put("style", "background-color: #f5f7fa; padding: 2px 6px; border-radius: 4px; font-family: 'Consolas', 'Courier New', monospace; color: #333;");
                    } else if (tagName.equals("pre")) {
                        attributes.put("style", "background-color: #f5f7fa; padding: 12px; border-radius: 8px; overflow-x: auto; font-family: 'Consolas', 'Courier New', monospace; border: 1px solid #e8e8e8;");
                    } else if (tagName.equals("a")) {
                        attributes.put("style", "color: #4263eb; text-decoration: none; border-bottom: 1px solid #4263eb;");
                    } else if (tagName.equals("h1")) {
                        attributes.put("style", "font-size: 1.5em; margin: 0.8em 0 0.3em 0; color: #333; font-weight: 600;");
                    } else if (tagName.equals("h2")) {
                        attributes.put("style", "font-size: 1.3em; margin: 0.7em 0 0.3em 0; color: #333; font-weight: 600;");
                    } else if (tagName.equals("h3")) {
                        attributes.put("style", "font-size: 1.1em; margin: 0.6em 0 0.3em 0; color: #333; font-weight: 600;");
                    } else if (tagName.equals("ul") || tagName.equals("ol")) {
                        attributes.put("style", "margin: 0.5em 0; padding-left: 2em;");
                    } else if (tagName.equals("li")) {
                        attributes.put("style", "margin: 0.3em 0;");
                    } else if (tagName.equals("blockquote")) {
                        attributes.put("style", "border-left: 4px solid #e0e0e0; margin: 0.7em 0; padding: 0.5em 0 0.5em 1em; color: #666; background-color: #fafafa; border-radius: 0 4px 4px 0;");
                    } else if (tagName.equals("table")) {
                        attributes.put("style", "border-collapse: collapse; width: 100%; margin: 1em 0;");
                    } else if (tagName.equals("th")) {
                        attributes.put("style", "border: 1px solid #ddd; padding: 8px; background-color: #f5f5f5; text-align: left;");
                    } else if (tagName.equals("td")) {
                        attributes.put("style", "border: 1px solid #ddd; padding: 8px;");
                    }
                })
                .build();
        // 设置面板基础样式
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 初始化组件
        initComponents();
    }

    //！！！！！！！
    private void sendMessage() {
        String userMessage = inputField.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }

        // 禁用发送按钮，防止重复发送
        sendButton.setEnabled(false);
        inputField.setEnabled(false);
        statusLabel.setText("正在发送消息...");

        // 显示用户消息
        addUserMessage(userMessage);
        inputField.setText("");

        // 显示"思考中"气泡
        JPanel thinkingPanel = addThinkingMessage();

        // 使用线程池进行API调用，防止UI冻结
        executorService.submit(() -> {
            try {
                // 使用OpenRouterService获取AI响应
                String aiResponse = aiService.getAIResponse(userMessage);

                // 停止思考计时器
                if (thinkingTimer != null) {
                    thinkingTimer.stop();
                }

                // 更新UI响应
                SwingUtilities.invokeLater(() -> {
                    // 移除"思考中"气泡
                    chatPanel.remove(thinkingPanel);

                    // 显示AI响应
                    addAIMessage(aiResponse);

                    // 更新状态栏
                    statusLabel.setText(String.format("回复完成 (耗时: %02d:%02d)", thinkingSeconds / 60, thinkingSeconds % 60));

                    // 重新启用发送按钮
                    sendButton.setEnabled(true);
                    inputField.setEnabled(true);
                    inputField.requestFocus();
                });
            } catch (Exception e) {
                // 停止思考计时器
                if (thinkingTimer != null) {
                    thinkingTimer.stop();
                }

                SwingUtilities.invokeLater(() -> {
                    // 移除"思考中"气泡
                    chatPanel.remove(thinkingPanel);

                    // 显示错误信息
                    addAIMessage("抱歉，遇到了错误: " + e.getMessage() + "\n\n请检查网络连接或API密钥是否正确。");

                    // 更新状态栏
                    statusLabel.setText("错误: " + e.getMessage());

                    // 重新启用发送按钮
                    sendButton.setEnabled(true);
                    inputField.setEnabled(true);
                    inputField.requestFocus();
                });
                e.printStackTrace();
            }
        });
    }

    // 更新面板上的状态信息
    public void updateStatusInfo(String dateTime, String user) {
        this.currentDateTime = dateTime;
        this.username = user;
    }

    // 关闭面板时清理资源
    public void cleanup() {
        if (thinkingTimer != null) {
            thinkingTimer.stop();
        }
        executorService.shutdown();
    }
    private void initComponents() {
        // 创建内容面板（带阴影和圆角）
        JPanel contentPanel = createShadowPanel();
        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        // 创建聊天面板
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(PANEL_BG_COLOR);

        // 自定义滚动面板
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(PANEL_BG_COLOR);
        scrollPane.getViewport().setBackground(PANEL_BG_COLOR);

        // 自定义滚动条样式
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        // 底部输入区域
        JPanel inputContainerPanel = new JPanel(new BorderLayout(0, 10));
        inputContainerPanel.setOpaque(true);
        inputContainerPanel.setBackground(PANEL_BG_COLOR);
        inputContainerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // 输入框
        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.setOpaque(false);

        inputField = new JTextField();
        inputField.setFont(CONTENT_FONT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(BORDER_COLOR, 1, 8),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        inputField.setBackground(AI_BUBBLE_COLOR);

        // 添加输入框聚焦时的视觉反馈
        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                inputField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(ACCENT_COLOR, 1, 8),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                inputField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(BORDER_COLOR, 1, 8),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }
        });

        // 发送按钮
        sendButton = createGradientButton("发送", ACCENT_COLOR, ACCENT_LIGHT);
        sendButton.setFont(BUTTON_FONT);
        sendButton.setForeground(Color.WHITE);
        sendButton.setPreferredSize(new Dimension(100, 45));

        inputWrapper.add(inputField, BorderLayout.CENTER);
        inputWrapper.add(sendButton, BorderLayout.EAST);

        // 底部按钮区
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomButtonPanel.setOpaque(false);

        clearButton = createGradientButton("清空对话", new Color(240, 240, 240), new Color(230, 230, 230));
        clearButton.setFont(BUTTON_FONT);
        clearButton.setForeground(TEXT_SECONDARY);

        exportButton = createGradientButton("导出对话", SECONDARY_COLOR, new Color(100, 210, 140));
        exportButton.setFont(BUTTON_FONT);
        exportButton.setForeground(Color.WHITE);

        bottomButtonPanel.add(exportButton);
        bottomButtonPanel.add(clearButton);

        // 添加状态栏
        statusBarPanel = new JPanel(new BorderLayout());
        statusBarPanel.setOpaque(false);
        statusBarPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        statusLabel = new JLabel("");
        statusLabel.setFont(SMALL_FONT);
        statusLabel.setForeground(TEXT_LIGHT);

        statusBarPanel.add(statusLabel, BorderLayout.WEST);

        // 组装输入区域
        inputContainerPanel.add(inputWrapper, BorderLayout.NORTH);
        inputContainerPanel.add(bottomButtonPanel, BorderLayout.CENTER);
        inputContainerPanel.add(statusBarPanel, BorderLayout.SOUTH);

        // 将所有组件添加到内容面板
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(inputContainerPanel, BorderLayout.SOUTH);

        // 添加内容面板到主面板
        add(contentPanel, BorderLayout.CENTER);

        // 添加动作监听器
        sendButton.addActionListener(e -> sendMessage());

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        // 清空按钮事件
        clearButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "确定要清空所有对话记录吗？",
                    "确认清空",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                chatPanel.removeAll();
                chatPanel.revalidate();
                chatPanel.repaint();

                // 添加欢迎消息
                addAIMessage("您好！我是您的财务智能助手，有什么可以帮您的吗？");
                statusLabel.setText("对话已清空");
            }
        });

        // 导出按钮事件
        exportButton.addActionListener(e -> exportConversation());

        // 添加欢迎消息
        addAIMessage("您好！我是您的财务智能助手，有什么可以帮您的吗？");
        statusLabel.setText("系统就绪，请输入您的问题");
    }

    /**
     * 创建带阴影的面板
     */
    private JPanel createShadowPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制阴影
                g2.setColor(SHADOW_COLOR);
                for (int i = 0; i < 5; i++) {
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, CORNER_RADIUS, CORNER_RADIUS);
                }

                // 绘制面板背景
                g2.setColor(PANEL_BG_COLOR);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, CORNER_RADIUS, CORNER_RADIUS);

                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    /**
     * 创建渐变按钮
     */
    private JButton createGradientButton(String text, Color startColor, Color endColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                // 绘制渐变背景
                GradientPaint gradient = new GradientPaint(
                        0, 0, startColor,
                        0, height, endColor);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, width, height, 8, 8);

                // 设置文本颜色
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2.drawString(getText(), (width - textWidth) / 2,
                        (height + textHeight / 2) / 2 - fm.getDescent());

                g2.dispose();
            }
        };

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * 添加用户消息气泡
     */
    private void addUserMessage(String message) {
        JPanel bubblePanel = createMessageBubble(message, true);
        chatPanel.add(bubblePanel);
        chatPanel.add(Box.createVerticalStrut(15)); // 增加间距
        chatPanel.revalidate();
        chatPanel.repaint();

        // 滚动到底部
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    /**
     * 添加AI消息气泡
     */
    private void addAIMessage(String message) {
        JPanel bubblePanel = createMessageBubble(message, false);
        chatPanel.add(bubblePanel);
        chatPanel.add(Box.createVerticalStrut(15)); // 增加间距
        chatPanel.revalidate();
        chatPanel.repaint();

        // 滚动到底部
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    /**
     * 添加思考中提示气泡
     */
    private JPanel addThinkingMessage() {
        JPanel thinkingPanel = createThinkingBubble();
        chatPanel.add(thinkingPanel);
        chatPanel.add(Box.createVerticalStrut(15)); // 增加间距
        chatPanel.revalidate();
        chatPanel.repaint();

        // 滚动到底部
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });

        // 启动思考计时器
        startThinkingTime = System.currentTimeMillis();
        thinkingSeconds = 0;

        // 更新思考时间的计时器
        thinkingTimer = new Timer(1000, e -> {
            thinkingSeconds++;
            statusLabel.setText(String.format("AI思考中... (%02d:%02d)", thinkingSeconds / 60, thinkingSeconds % 60));
        });
        thinkingTimer.start();

        return thinkingPanel;
    }

    /**
     * 创建对话气泡
     */
    private JPanel createMessageBubble(String message, boolean isUser) {
        JPanel outerPanel = new JPanel(new BorderLayout(15, 0));
        outerPanel.setOpaque(false);

        // 创建头像面板
        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setOpaque(false);

        JLabel avatarLabel = new JLabel(isUser ? "👤" : "🤖");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setVerticalAlignment(SwingConstants.TOP);
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        avatarPanel.add(avatarLabel, BorderLayout.CENTER);

        // 创建气泡容器
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.Y_AXIS));
        bubblePanel.setOpaque(false);

        // 添加发送者标签
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 5, 8));

        JLabel senderLabel = new JLabel(isUser ? username : "AI助手");
        senderLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
        senderLabel.setForeground(isUser ? ACCENT_COLOR : SECONDARY_COLOR);
        headerPanel.add(senderLabel, BorderLayout.WEST);

        // 添加时间标签
        JLabel timeLabel = new JLabel(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        timeLabel.setFont(TIME_FONT);
        timeLabel.setForeground(TEXT_LIGHT);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        // 创建Markdown/HTML渲染的文本面板
        JEditorPane textPane = new JEditorPane();
        textPane.setEditorKit(new HTMLEditorKit());
        textPane.setDocument(new HTMLDocument());
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        textPane.setFont(CONTENT_FONT);

        // 应用Markdown格式并设置HTML内容
        String htmlContent = renderMarkdown(message);
        textPane.setText(htmlContent);

        // 创建自定义气泡边框
        JPanel bubbleContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制气泡背景
                RoundRectangle2D.Float bubble = new RoundRectangle2D.Float(
                        0, 0, getWidth() - 1, getHeight() - 1, BUBBLE_RADIUS, BUBBLE_RADIUS);

                // 填充气泡
                g2.setColor(isUser ? USER_BUBBLE_COLOR : AI_BUBBLE_COLOR);
                g2.fill(bubble);

                // 绘制气泡边框
                g2.setColor(isUser ? USER_BUBBLE_BORDER : AI_BUBBLE_BORDER);
                g2.draw(bubble);

                g2.dispose();
            }
        };

        // 设置气泡布局
        bubbleContainer.setLayout(new BorderLayout());
        bubbleContainer.setOpaque(false);
        bubbleContainer.add(textPane, BorderLayout.CENTER);
        bubbleContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 根据内容调整文本面板大小
        textPane.setSize(new Dimension(500, Short.MAX_VALUE));
        int preferredHeight = textPane.getPreferredSize().height;
        bubbleContainer.setPreferredSize(new Dimension(500, preferredHeight + 30));

        // 组装气泡面板
        bubblePanel.add(headerPanel);
        bubblePanel.add(bubbleContainer);

        // 设置对齐和添加组件
        if (isUser) {
            outerPanel.add(bubblePanel, BorderLayout.CENTER);
            outerPanel.add(avatarPanel, BorderLayout.EAST);
            outerPanel.setBorder(BorderFactory.createEmptyBorder(5, 60, 5, 15));
        } else {
            outerPanel.add(avatarPanel, BorderLayout.WEST);
            outerPanel.add(bubblePanel, BorderLayout.CENTER);
            outerPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 60));
        }

        return outerPanel;
    }

    /**
     * 创建"思考中"气泡
     */
    private JPanel createThinkingBubble() {
        JPanel outerPanel = new JPanel(new BorderLayout(15, 0));
        outerPanel.setOpaque(false);

        // 创建头像面板
        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setOpaque(false);

        JLabel avatarLabel = new JLabel("🤖");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setVerticalAlignment(SwingConstants.TOP);
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        avatarPanel.add(avatarLabel, BorderLayout.CENTER);

        // 思考气泡容器
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.Y_AXIS));
        bubblePanel.setOpaque(false);

        // 添加发送者标签
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 5, 8));

        JLabel senderLabel = new JLabel("AI助手");
        senderLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
        senderLabel.setForeground(SECONDARY_COLOR);
        headerPanel.add(senderLabel, BorderLayout.WEST);

        // 添加时间标签
        JLabel timeLabel = new JLabel(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        timeLabel.setFont(TIME_FONT);
        timeLabel.setForeground(TEXT_LIGHT);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        // 思考中容器
        JPanel thinkingContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制气泡背景
                RoundRectangle2D.Float bubble = new RoundRectangle2D.Float(
                        0, 0, getWidth() - 1, getHeight() - 1, BUBBLE_RADIUS, BUBBLE_RADIUS);

                // 填充气泡
                g2.setColor(AI_BUBBLE_COLOR);
                g2.fill(bubble);

                // 绘制气泡边框
                g2.setColor(AI_BUBBLE_BORDER);
                g2.draw(bubble);

                g2.dispose();
            }
        };

        thinkingContainer.setLayout(new BorderLayout(10, 5));
        thinkingContainer.setOpaque(false);

        // 添加动画效果
        JPanel animationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        animationPanel.setOpaque(false);

        JLabel thinkingIcon = new JLabel("🧠");
        thinkingIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JLabel thinkingLabel = new JLabel("AI助手正在思考");
        thinkingLabel.setFont(CONTENT_FONT);
        thinkingLabel.setForeground(TEXT_SECONDARY);

        animationPanel.add(thinkingIcon);
        animationPanel.add(thinkingLabel);

        // 添加进度条
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(ACCENT_COLOR);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(200, 5));

        // 动画定时器
        Timer timer = new Timer(500, new ActionListener() {
            private int dots = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                dots = (dots + 1) % 4;
                StringBuilder dotsText = new StringBuilder();
                for (int i = 0; i < dots; i++) {
                    dotsText.append(".");
                }
                thinkingLabel.setText("AI助手正在思考" + dotsText);
            }
        });
        timer.start();

        thinkingContainer.add(animationPanel, BorderLayout.NORTH);
        thinkingContainer.add(progressBar, BorderLayout.CENTER);
        thinkingContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 组装思考气泡
        bubblePanel.add(headerPanel);
        bubblePanel.add(thinkingContainer);

        // 设置整体布局
        outerPanel.add(avatarPanel, BorderLayout.WEST);
        outerPanel.add(bubblePanel, BorderLayout.CENTER);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 60));

        // 清理资源
        outerPanel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0) {
                if (e.getChanged().getParent() == null) {
                    timer.stop();
                    if (thinkingTimer != null) {
                        thinkingTimer.stop();
                    }
                }
            }
        });

        return outerPanel;
    }

    /**
     * 渲染Markdown为HTML
     */
    private String renderMarkdown(String markdown) {
        Node document = markdownParser.parse(markdown);
        String html = htmlRenderer.render(document);

        // 添加HTML样式
        return "<html><head><style>" +
                "body { font-family: '微软雅黑', sans-serif; font-size: 15px; line-height: 1.5; color: #333; }" +
                "p { margin: 0.5em 0; }" +
                "code { background-color: #f5f7fa; padding: 2px 6px; border-radius: 4px; font-family: Consolas, 'Courier New', monospace; color: #333; }" +
                "pre { background-color: #f5f7fa; padding: 12px; border-radius: 8px; overflow-x: auto; font-family: Consolas, 'Courier New', monospace; border: 1px solid #e8e8e8; }" +
                "a { color: #4263eb; text-decoration: none; border-bottom: 1px solid #4263eb; }" +
                "h1, h2, h3, h4, h5, h6 { margin-top: 0.8em; margin-bottom: 0.3em; color: #333; font-weight: 600; }" +
                "h1 { font-size: 1.5em; }" +
                "h2 { font-size: 1.3em; }" +
                "h3 { font-size: 1.1em; }" +
                "ul, ol { margin: 0.5em 0; padding-left: 2em; }" +
                "li { margin: 0.3em 0; }" +
                "blockquote { border-left: 4px solid #e0e0e0; margin: 0.7em 0; padding: 0.5em 0 0.5em 1em; color: #666; background-color: #fafafa; border-radius: 0 4px 4px 0; }" +
                "table { border-collapse: collapse; width: 100%; margin: 1em 0; }" +
                "th { border: 1px solid #ddd; padding: 8px; background-color: #f5f5f5; text-align: left; }" +
                "td { border: 1px solid #ddd; padding: 8px; }" +
                "</style></head><body>" + html + "</body></html>";
    }

    /**
     * 导出对话
     */
    private void exportConversation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存对话");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("财务助手对话记录_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".html"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                writer.println("<!DOCTYPE html>");
                writer.println("<html>");
                writer.println("<head>");
                writer.println("<meta charset=\"UTF-8\">");
                writer.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
                writer.println("<title>PFM财务助手对话记录</title>");
                writer.println("<style>");
                writer.println("* { box-sizing: border-box; margin: 0; padding: 0; }");
                writer.println("body { font-family: '微软雅黑', 'Microsoft YaHei', sans-serif; line-height: 1.6; color: #333; max-width: 900px; margin: 0 auto; padding: 30px; background-color: #f5f7fa; }");
                writer.println("header { text-align: center; margin-bottom: 40px; }");
                writer.println("h1 { color: #4263eb; font-size: 28px; margin-bottom: 10px; }");
                writer.println("h2 { color: #666; font-size: 16px; font-weight: normal; margin-bottom: 30px; }");
                writer.println(".conversation { background-color: white; border-radius: 16px; box-shadow: 0 5px 25px rgba(0,0,0,0.07); padding: 30px; }");
                writer.println(".message { display: flex; margin-bottom: 25px; }");
                writer.println(".message.user { flex-direction: row-reverse; }");
                writer.println(".avatar { width: 50px; height: 50px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24px; flex-shrink: 0; }");
                writer.println(".user .avatar { background-color: #edf2ff; }");
                writer.println(".ai .avatar { background-color: #f0f8ff; }");
                writer.println(".bubble-container { flex-grow: 1; max-width: 80%; }");
                writer.println(".bubble-header { display: flex; justify-content: space-between; margin-bottom: 5px; padding: 0 10px; }");
                writer.println(".user .bubble-header { flex-direction: row-reverse; }");
                writer.println(".sender { font-weight: bold; color: #4263eb; font-size: 14px; }");
                writer.println(".ai .sender { color: #50c878; }");
                writer.println(".time { color: #999; font-size: 12px; }");
                writer.println(".bubble { padding: 15px 20px; border-radius: 16px; overflow-wrap: break-word; }");
                writer.println(".user .bubble { background-color: #edf2ff; border: 1px solid #d3e2f8; margin-left: 20px; }");
                writer.println(".ai .bubble { background-color: white; border: 1px solid #e6e6e6; margin-right: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.03); }");
                writer.println("code { background-color: #f5f7fa; padding: 2px 6px; border-radius: 4px; font-family: Consolas, 'Courier New', monospace; }");
                writer.println("pre { background-color: #f5f7fa; padding: 15px; border-radius: 8px; overflow-x: auto; font-family: Consolas, 'Courier New', monospace; border: 1px solid #e8e8e8; margin: 10px 0; }");
                writer.println("footer { text-align: center; margin-top: 30px; color: #999; font-size: 14px; }");
                writer.println("</style>");
                writer.println("</head>");
                writer.println("<body>");
                writer.println("<header>");
                writer.println("<h1>PFM财务助手对话记录</h1>");
                writer.println("<h2>导出时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " | 用户: " + username + "</h2>");
                writer.println("</header>");
                writer.println("<div class=\"conversation\">");

                // 获取所有消息组件并导出
                Component[] components = chatPanel.getComponents();
                for (Component component : components) {
                    if (component instanceof JPanel) {
                        JPanel panel = (JPanel) component;
                        if (panel.getComponentCount() > 0) {
                            // 排除间距组件
                            if (panel.getLayout() instanceof BorderLayout) {
                                BorderLayout layout = (BorderLayout) panel.getLayout();
                                Component eastComponent = layout.getLayoutComponent(BorderLayout.EAST);
                                boolean isUser = eastComponent instanceof JPanel && ((JPanel) eastComponent).getComponentCount() > 0;

                                writer.println("<div class=\"message " + (isUser ? "user" : "ai") + "\">");
                                writer.println("<div class=\"avatar\">" + (isUser ? "👤" : "🤖") + "</div>");
                                writer.println("<div class=\"bubble-container\">");

                                // 查找消息内容
                                Component centerComponent = layout.getLayoutComponent(isUser ? BorderLayout.CENTER : BorderLayout.CENTER);
                                if (centerComponent instanceof JPanel) {
                                    JPanel bubblePanel = (JPanel) centerComponent;

                                    // 查找标题和时间
                                    if (bubblePanel.getComponentCount() > 0 && bubblePanel.getComponent(0) instanceof JPanel) {
                                        JPanel headerPanel = (JPanel) bubblePanel.getComponent(0);
                                        if (headerPanel.getLayout() instanceof BorderLayout) {
                                            BorderLayout headerLayout = (BorderLayout) headerPanel.getLayout();
                                            Component senderComponent = headerLayout.getLayoutComponent(BorderLayout.WEST);
                                            Component timeComponent = headerLayout.getLayoutComponent(BorderLayout.EAST);

                                            writer.println("<div class=\"bubble-header\">");
                                            if (senderComponent instanceof JLabel) {
                                                JLabel senderLabel = (JLabel) senderComponent;
                                                writer.println("<div class=\"sender\">" + senderLabel.getText() + "</div>");
                                            }
                                            if (timeComponent instanceof JLabel) {
                                                JLabel timeLabel = (JLabel) timeComponent;
                                                writer.println("<div class=\"time\">" + timeLabel.getText() + "</div>");
                                            }
                                            writer.println("</div>");
                                        }
                                    }

                                    // 查找消息内容
                                    if (bubblePanel.getComponentCount() > 1) {
                                        Component bubbleComponent = bubblePanel.getComponent(1);
                                        if (bubbleComponent instanceof JPanel) {
                                            JPanel container = (JPanel) bubbleComponent;
                                            if (container.getLayout() instanceof BorderLayout) {
                                                BorderLayout containerLayout = (BorderLayout) container.getLayout();
                                                Component contentComponent = containerLayout.getLayoutComponent(BorderLayout.CENTER);
                                                if (contentComponent instanceof JEditorPane) {
                                                    JEditorPane editorPane = (JEditorPane) contentComponent;
                                                    writer.println("<div class=\"bubble\">");
                                                    writer.println(editorPane.getText());
                                                    writer.println("</div>");
                                                }
                                            }
                                        }
                                    }
                                }

                                writer.println("</div>"); // bubble-container
                                writer.println("</div>"); // message
                            }
                        }
                    }
                }

                writer.println("</div>"); // conversation
                writer.println("<footer>");
                writer.println("© 2025 PFM财务管家 —— 高端·专业·安全");
                writer.println("</footer>");
                writer.println("</body>");
                writer.println("</html>");

                statusLabel.setText("对话记录已成功导出到: " + file.getAbsolutePath());

                JOptionPane.showMessageDialog(this,
                        "对话记录已成功导出到: " + file.getAbsolutePath(),
                        "导出成功",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                statusLabel.setText("导出失败: " + e.getMessage());

                JOptionPane.showMessageDialog(this,
                        "导出失败: " + e.getMessage(),
                        "导出错误",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * 自定义圆角边框
     */
    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = thickness;
            return insets;
        }
    }

    /**
     * 现代滚动条UI
     */
    private class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = ACCENT_COLOR;
            this.thumbDarkShadowColor = ACCENT_COLOR;
            this.thumbHighlightColor = ACCENT_COLOR;
            this.thumbLightShadowColor = ACCENT_COLOR;
            this.trackColor = new Color(240, 240, 240);
            this.trackHighlightColor = new Color(240, 240, 240);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createEmptyButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createEmptyButton();
        }

        private JButton createEmptyButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制滚动条滑块
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 8, 8);

            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }
}