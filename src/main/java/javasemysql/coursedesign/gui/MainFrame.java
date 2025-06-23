package javasemysql.coursedesign.gui;

import javasemysql.coursedesign.gui.component.*;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.*;
import javasemysql.coursedesign.utils.LogUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 应用程序主窗口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class MainFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(MainFrame.class.getName());

    private User user;

    // 添加服务层字段
    private final AccountService accountService;
    private final BackupService backupService;
    private final BillService billService;
    private final BudgetService budgetService;
    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final UserService userService;
    private User currentUser; // 当前登录用户
    /**
     * 获取单例实例
     *
     * @param accountService 账户服务
     * @param backupService 备份服务
     * @param billService 账单服务
     * @param budgetService 预算服务
     * @param expenseService 支出服务
     * @param incomeService 收入服务
     * @param userService 用户服务
     * @return MainFrame实例
     */
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;

    // 功能面板
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;
    private AccountPanel accountPanel;
    private IncomePanel incomePanel;
    private ExpensePanel expensePanel;
    private BudgetPanel budgetPanel;
    private BillPanel billPanel;
    private StatisticsPanel statisticsPanel;
    private BackupPanel backupPanel;
    private SettingsPanel settingsPanel;

    /**
     * 私有构造函数（单例模式）
     */
    public MainFrame(AccountService accountService,BackupService backupService,BillService billService,
                     BudgetService budgetService, ExpenseService expenseService,
                     IncomeService incomeService, UserService userService) {
        this.accountService = accountService;
        this.backupService = backupService;
        this.billService = billService;
        this.budgetService = budgetService;
        this.expenseService = expenseService;
        this.incomeService = incomeService;
        this.userService = userService;

        initLookAndFeel();
        initComponents();
        setupListeners();
    }

    /**
     * 设置UI外观
     */
    private void initLookAndFeel() {
        try {
            // 尝试使用系统外观
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // 自定义UI属性
            UIManager.put("OptionPane.messageFont", new Font("微软雅黑", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("微软雅黑", Font.PLAIN, 14));
            UIManager.put("TextField.font", new Font("微软雅黑", Font.PLAIN, 14));
            UIManager.put("Label.font", new Font("微软雅黑", Font.PLAIN, 14));
            UIManager.put("Button.font", new Font("微软雅黑", Font.PLAIN, 14));
            UIManager.put("Table.font", new Font("微软雅黑", Font.PLAIN, 14));
            UIManager.put("TableHeader.font", new Font("微软雅黑", Font.BOLD, 14));
            UIManager.put("TabbedPane.font", new Font("微软雅黑", Font.PLAIN, 14));
            UIManager.put("ComboBox.font", new Font("微软雅黑", Font.PLAIN, 14));
            UIManager.put("Menu.font", new Font("微软雅黑", Font.PLAIN, 14));
            UIManager.put("MenuItem.font", new Font("微软雅黑", Font.PLAIN, 14));
            logger.log(Level.INFO, "系统外观设置成功");
        } catch (Exception e) {
            logger.log(Level.WARNING, "无法设置系统外观", e);
        }
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        // 设置窗口属性
        logger.info("初始化主窗口组件");
        setTitle("个人财务管理系统");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);  // 居中显示

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建内容面板（使用卡片布局）
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // 创建侧边栏面板
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.setBackground(new Color(40, 45, 51));
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));

        // 创建各个功能面板
        logger.info("创建各个功能面板");

        try{
        loginPanel = new LoginPanel(this);
        logger.info("LoginPanel创建成功");
        dashboardPanel = new DashboardPanel(this);
        logger.info("DashboardPanel创建成功");
        accountPanel = new AccountPanel(this);
        logger.info("AccountPanel创建成功");
        incomePanel = new IncomePanel(this);logger.info("IncomePanel创建成功");
        expensePanel = new ExpensePanel(this);logger.info("ExpensePanel创建成功");
        budgetPanel = new BudgetPanel(this);logger.info("BudgetPanel创建成功");
        billPanel = new BillPanel(this);logger.info("BillPanel创建成功");
        statisticsPanel = new StatisticsPanel(this);logger.info("StatisticsPanel创建成功");
        backupPanel = new BackupPanel(this);logger.info("BackupPanel创建成功");
        settingsPanel = new SettingsPanel(this);
        logger.info("new成功");

        // 添加面板到内容面板
        contentPanel.add(loginPanel, "login");
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(accountPanel, "account");
        contentPanel.add(incomePanel, "income");
        contentPanel.add(expensePanel, "expense");
        contentPanel.add(budgetPanel, "budget");
        contentPanel.add(billPanel, "bill");
        contentPanel.add(statisticsPanel, "statistics");
        contentPanel.add(backupPanel, "backup");
        contentPanel.add(settingsPanel, "settings");
        logger.info("各个功能面板创建完成");

        // 创建侧边栏菜单按钮
        createSidebarButtons();
        logger.info("侧边栏按钮创建完成");

        // 默认显示登录面板，隐藏侧边栏
        cardLayout.show(contentPanel, "login");
        sidebarPanel.setVisible(false);
        logger.info("默认显示登录面板，侧边栏隐藏");

        // 将组件添加到主面板
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        logger.info("主面板组件添加完成");

        // 设置主面板为内容面板
        setContentPane(mainPanel);

        logger.info("主窗口组件初始化完成");
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "初始化组件失败", e);
            JOptionPane.showMessageDialog(this, "初始化组件失败: "
                    + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            //终止
            System.exit(1);
        }
    }

    /**
     * 创建侧边栏按钮
     */
    private void createSidebarButtons() {
        // 创建按钮样式
        Font buttonFont = new Font("微软雅黑", Font.PLAIN, 14);
        Dimension buttonSize = new Dimension(200, 40);
        Color buttonTextColor = Color.WHITE;
        Color buttonBgColor = new Color(50, 55, 61);
        Color buttonHoverColor = new Color(70, 75, 81);

        // 创建用户信息面板
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(new Color(40, 45, 51));
        userPanel.setMaximumSize(new Dimension(200, 100));
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userIconLabel = new JLabel();
        userIconLabel.setIcon(new ImageIcon(getClass().getResource("/resources/images/user_icon.png")));
        userIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userNameLabel = new JLabel("未登录");
        userNameLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(userIconLabel);
        userPanel.add(Box.createVerticalStrut(5));
        userPanel.add(userNameLabel);
        userPanel.add(Box.createVerticalStrut(10));

        // 创建各个功能按钮
        JButton dashboardButton = createSidebarButton("仪表盘", buttonFont, buttonSize, buttonTextColor, buttonBgColor, buttonHoverColor);
        dashboardButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/dashboard_icon.png")));
        dashboardButton.addActionListener(e -> showPanel("dashboard"));

        JButton accountButton = createSidebarButton("账户管理", buttonFont, buttonSize, buttonTextColor, buttonBgColor, buttonHoverColor);
        accountButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/account_icon.png")));
        accountButton.addActionListener(e -> showPanel("account"));

        JButton incomeButton = createSidebarButton("收入记录", buttonFont, buttonSize, buttonTextColor, buttonBgColor, buttonHoverColor);
        incomeButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/income_icon.png")));
        incomeButton.addActionListener(e -> showPanel("income"));

        JButton expenseButton = createSidebarButton("支出记录", buttonFont, buttonSize, buttonTextColor, buttonBgColor, buttonHoverColor);
        expenseButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/expense_icon.png")));
        expenseButton.addActionListener(e -> showPanel("expense"));

        JButton budgetButton = createSidebarButton("预算管理", buttonFont, buttonSize, buttonTextColor, buttonBgColor, buttonHoverColor);
        budgetButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/budget_icon.png")));
        budgetButton.addActionListener(e -> showPanel("budget"));

        JButton billButton = createSidebarButton("账单管理", buttonFont, buttonSize, buttonTextColor, buttonBgColor, buttonHoverColor);
        billButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/bill_icon.png")));
        billButton.addActionListener(e -> showPanel("bill"));

        JButton statisticsButton = createSidebarButton("统计分析", buttonFont, buttonSize, buttonTextColor, buttonBgColor, buttonHoverColor);
        statisticsButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/statistics_icon.png")));
        statisticsButton.addActionListener(e -> showPanel("statistics"));

        JButton backupButton = createSidebarButton("备份管理", buttonFont, buttonSize, buttonTextColor, buttonBgColor, buttonHoverColor);
        backupButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/backup_icon.png")));
        backupButton.addActionListener(e -> showPanel("backup"));

        JButton settingsButton = createSidebarButton("系统设置", buttonFont, buttonSize, buttonTextColor, buttonBgColor, buttonHoverColor);
        settingsButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/settings_icon.png")));
        settingsButton.addActionListener(e -> showPanel("settings"));

        JButton logoutButton = createSidebarButton("退出登录", buttonFont, buttonSize, buttonTextColor, buttonBgColor, buttonHoverColor);
        logoutButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/logout_icon.png")));
        logoutButton.addActionListener(e -> logout());

        // 添加按钮到侧边栏
        sidebarPanel.add(userPanel);
        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(dashboardButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(accountButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(incomeButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(expenseButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(budgetButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(billButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(statisticsButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(backupButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(settingsButton);
        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalGlue());

        // 保存用户名标签引用，以便登录后更新
        this.userNameLabel = userNameLabel;
    }

    private JLabel userNameLabel;

    /**
     * 创建侧边栏按钮
     *
     * @param text 按钮文本
     * @param font 字体
     * @param size 尺寸
     * @param textColor 文本颜色
     * @param bgColor 背景颜色
     * @param hoverColor 悬停颜色
     * @return 按钮组件
     */
    private JButton createSidebarButton(String text, Font font, Dimension size, Color textColor, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 添加鼠标悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 窗口关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        // 窗口大小改变事件
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // 可以在这里处理窗口大小改变后的调整
            }
        });
    }

    /**
     * 显示指定的面板
     *
     * @param panelName 面板名称
     */
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        logger.log(Level.INFO, "Showing panel: " + panelName);

        // 更新面板数据
        if ("dashboard".equals(panelName)) {
            dashboardPanel.refreshData();
        } else if ("account".equals(panelName)) {
            accountPanel.refreshData();
        } else if ("income".equals(panelName)) {
            incomePanel.refreshData();
        } else if ("expense".equals(panelName)) {
            expensePanel.refreshData();
        } else if ("budget".equals(panelName)) {
            budgetPanel.refreshData();
        } else if ("bill".equals(panelName)) {
            billPanel.refreshData();
        } else if ("statistics".equals(panelName)) {
            statisticsPanel.refreshData();
        } else if ("backup".equals(panelName)) {
            backupPanel.refreshData();
        } else if ("settings".equals(panelName)) {
            settingsPanel.refreshData();
        }
    }

    /**
     * 用户登录成功后的处理
     *
     * @param user 用户对象
     */
    public void userLoggedIn(User user) {
        this.currentUser = user;

        // 更新用户信息
        if (userNameLabel != null) {
            userNameLabel.setText(user.getName());
        }

        // 显示侧边栏
        sidebarPanel.setVisible(true);

        // 更新各个面板的用户数据
        dashboardPanel.updateUserData(user);
        accountPanel.updateUserData(user);
        incomePanel.updateUserData(user);
        expensePanel.updateUserData(user);
        budgetPanel.updateUserData(user);
        billPanel.updateUserData(user);
        statisticsPanel.updateUserData(user);
        backupPanel.updateUserData(user);
        settingsPanel.updateUserData(user);

        // 显示仪表板
        showPanel("dashboard");

        LogUtils.info("用户登录成功: " + user.getName());
    }

    /**
     * 用户登出
     */
    public void logout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "确定要退出登录吗？",
                "退出登录",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            this.currentUser = null;

            // 更新用户信息
            if (userNameLabel != null) {
                userNameLabel.setText("未登录");
            }

            // 隐藏侧边栏
            sidebarPanel.setVisible(false);

            // 显示登录面板
            showPanel("login");

            LogUtils.info("用户已登出");
        }
    }

    /**
     * 退出应用程序
     */
    private void exitApplication() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "确定要退出系统吗？",
                "退出系统",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            // 关闭日志系统
            LogUtils.shutdown();

            // 退出程序
            dispose();
            System.exit(0);
        }
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户对象
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 显示信息提示
     *
     * @param message 信息内容
     */
    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "提示",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * 显示错误提示
     *
     * @param message 错误信息
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "错误",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * 显示警告提示
     *
     * @param message 警告信息
     */
    public void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "警告",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * 显示确认对话框
     *
     * @param message 确认信息
     * @return 用户选择（是/否）
     */
    public boolean showConfirmDialog(String message) {
        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "确认",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return option == JOptionPane.YES_OPTION;
    }

    public void showBillTab(int billId) {
        // 切换到账单管理面板
        showPanel("bill");

        // 在账单面板中显示指定的账单
        billPanel.showBillDetails(billId);
    }

    public void showAccountTab(int id) {
        // 切换到账户管理面板
        showPanel("account");

        // 在账户面板中显示指定的账户
        accountPanel.showAccountDetails(id);
    }

    public void showBudgetTab(int id) {
        // 切换到预算管理面板
        showPanel("budget");

        // 在预算面板中显示指定的预算
        budgetPanel.showBudgetDetails(id);
    }
}