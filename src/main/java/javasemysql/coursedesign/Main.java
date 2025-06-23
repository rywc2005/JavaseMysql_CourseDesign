package javasemysql.coursedesign;

import javasemysql.coursedesign.gui.MainFrame;
import javasemysql.coursedesign.gui.component.SplashScreen;
import javasemysql.coursedesign.service.*;
import javasemysql.coursedesign.service.impl.*;
import javasemysql.coursedesign.utils.ConfigUtils;
import javasemysql.coursedesign.utils.DBUtils;
import javasemysql.coursedesign.utils.LogUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 应用程序入口类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * 程序入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 初始化日志
        initLogger();

        // 初始化配置
        initConfig();

        // 显示闪屏
        SplashScreen splashScreen = new SplashScreen(3000); // 显示3秒
        splashScreen.showSplash();

        // 设置应用程序外观
        setLookAndFeel();

        // 测试数据库连接
        testDatabaseConnection();

        // 启动主程序
        startApplication();
    }

    /**
     * 初始化日志
     */
    private static void initLogger() {
        try {
            // 确保日志目录存在
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdir();
            }

            // 初始化日志
            LogUtils.setup();
            logger.info("日志系统初始化成功");
        } catch (Exception e) {
            System.err.println("初始化日志系统失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 初始化配置
     */
    private static void initConfig() {
        try {
            // 加载配置文件
            ConfigUtils.loadConfig();
            logger.info("配置加载成功");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "加载配置文件失败", e);
            JOptionPane.showMessageDialog(null,
                    "加载配置文件失败: " + e.getMessage() + "\n应用程序将使用默认配置继续运行。",
                    "配置加载错误",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 设置应用程序外观
     */
    private static void setLookAndFeel() {
        try {
            // 尝试使用系统外观
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // 设置全局字体
            setGlobalFont(new Font("微软雅黑", Font.PLAIN, 12));

            logger.info("设置应用程序外观成功");
        } catch (Exception e) {
            logger.log(Level.WARNING, "设置应用程序外观失败，将使用默认外观", e);
        }
    }

    /**
     * 设置全局字体
     *
     * @param font 字体
     */
    private static void setGlobalFont(Font font) {
        UIManager.put("Button.font", font);
        UIManager.put("ToggleButton.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("ColorChooser.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("List.font", font);
        UIManager.put("MenuBar.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("RadioButtonMenuItem.font", font);
        UIManager.put("CheckBoxMenuItem.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("PopupMenu.font", font);
        UIManager.put("OptionPane.font", font);
        UIManager.put("Panel.font", font);
        UIManager.put("ProgressBar.font", font);
        UIManager.put("ScrollPane.font", font);
        UIManager.put("Viewport.font", font);
        UIManager.put("TabbedPane.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
        UIManager.put("TitledBorder.font", font);
        UIManager.put("ToolBar.font", font);
        UIManager.put("ToolTip.font", font);
        UIManager.put("Tree.font", font);
    }

    /**
     * 测试数据库连接
     */
    private static void testDatabaseConnection() {
        try {
            // 获取数据库连接
            DBUtils.getConnection().close();
            logger.info("数据库连接测试成功");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "数据库连接失败", e);
            JOptionPane.showMessageDialog(null,
                    "数据库连接失败: " + e.getMessage() + "\n请检查数据库配置后重试。",
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1); // 终止程序
        }
    }

    /**
     * 启动主程序
     */
    private static void startApplication() {
        SwingUtilities.invokeLater(() -> {
            try {
                // 初始化服务
                logger.info("正在初始化服务...");
                // 创建服务实例
                logger.info("创建服务实例...");
                BudgetService budgetService = new BudgetServiceImpl();
                ExpenseService expenseService = new ExpenseServiceImpl();
                AccountService accountService = new AccountServiceImpl();
                BackupService backupService = new BackupServiceImpl();
                IncomeService incomeService = new IncomeServiceImpl();
                UserService userService = new UserServiceImpl();
                BillService billService = new BillServiceImpl();

                // 初始化服务
                logger.info("服务初始化成功");
                // 创建主窗口
                MainFrame mainFrame = new MainFrame(
                        accountService,backupService,billService,budgetService,expenseService,incomeService,userService);
                mainFrame.setVisible(true);
                logger.info("主窗口创建成功, 应用程序启动中...");
                logger.info("应用程序启动成功");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "应用程序启动失败", e);
                JOptionPane.showMessageDialog(null,
                        "应用程序启动失败: " + e.getMessage(),
                        "启动错误",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1); // 终止程序
            }
        });
    }
}