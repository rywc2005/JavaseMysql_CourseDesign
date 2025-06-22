package javasemysql.coursedesign.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * 日志工具类，提供日志记录功能
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class LogUtils {

    private static final String LOG_FOLDER = "logs";
    private static final String LOG_FILE_PREFIX = "finance_";
    private static final String LOG_FILE_SUFFIX = ".log";
    private static final int LOG_FILE_SIZE_LIMIT = 10 * 1024 * 1024; // 10MB
    private static final int LOG_FILE_COUNT = 10;

    private static Logger logger;

    static {
        try {
            init();
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    /**
     * 初始化日志系统
     *
     * @throws IOException 如果创建日志文件失败
     */
    private static void init() throws IOException {
        // 创建日志目录
        File logDir = new File(LOG_FOLDER);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // 创建日志文件处理器
        String logFileName = LOG_FOLDER + File.separator +
                LOG_FILE_PREFIX +
                new SimpleDateFormat("yyyyMMdd").format(new Date()) +
                LOG_FILE_SUFFIX;

        FileHandler fileHandler = new FileHandler(logFileName, LOG_FILE_SIZE_LIMIT, LOG_FILE_COUNT, true);
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.ALL);

        // 创建控制台处理器
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord record) {
                return String.format(format,
                        new Date(record.getMillis()),
                        record.getLevel().getLocalizedName(),
                        record.getMessage()
                );
            }
        });
        consoleHandler.setLevel(Level.INFO);

        // 配置根日志记录器
        logger = Logger.getLogger("javasemysql.coursedesign");
        logger.setUseParentHandlers(false);
        logger.addHandler(fileHandler);
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
    }

    /**
     * 记录信息级别日志
     *
     * @param message 日志消息
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * 记录警告级别日志
     *
     * @param message 日志消息
     */
    public static void warning(String message) {
        logger.warning(message);
    }

    /**
     * 记录警告级别日志
     *
     * @param message 日志消息
     * @param thrown 异常对象
     */
    public static void warning(String message, Throwable thrown) {
        logger.log(Level.WARNING, message, thrown);
    }

    /**
     * 记录错误级别日志
     *
     * @param message 日志消息
     */
    public static void error(String message) {
        logger.severe(message);
    }

    /**
     * 记录错误级别日志
     *
     * @param message 日志消息
     * @param thrown 异常对象
     */
    public static void error(String message, Throwable thrown) {
        logger.log(Level.SEVERE, message, thrown);
    }

    /**
     * 记录调试级别日志
     *
     * @param message 日志消息
     */
    public static void debug(String message) {
        logger.fine(message);
    }

    /**
     * 记录跟踪级别日志
     *
     * @param message 日志消息
     */
    public static void trace(String message) {
        logger.finer(message);
    }

    /**
     * 获取日志记录器
     *
     * @return 日志记录器对象
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * 为指定类获取日志记录器
     *
     * @param clazz 类对象
     * @return 日志记录器对象
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

    /**
     * 关闭日志系统
     */
    public static void shutdown() {
        for (Handler handler : logger.getHandlers()) {
            handler.close();
        }
    }

    public static void warn(String s) {
        logger.warning(s);
    }

    public static void setup() {
        try {
            init();
        } catch (IOException e) {
            System.err.println("Failed to setup logger: " + e.getMessage());
            e.printStackTrace();
        }
    }
}