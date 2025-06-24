package com.PFM.CD.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 临时文件管理工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class TempFileManager {

    private static final String TEMP_DIR_PREFIX = "pfm_temp_";
    private static final long DEFAULT_CLEANUP_DELAY_HOURS = 24;
    private static final ConcurrentHashMap<String, Long> MANAGED_FILES = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService CLEANUP_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private static Path tempDirPath;

    static {
        try {
            // 创建临时目录
            tempDirPath = Files.createTempDirectory(TEMP_DIR_PREFIX);

            // 启动清理线程，每小时检查一次过期文件
            CLEANUP_EXECUTOR.scheduleAtFixedRate(
                    TempFileManager::cleanupExpiredFiles,
                    1, 1, TimeUnit.HOURS);

            // 添加JVM关闭钩子，在程序退出时清理临时文件
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                CLEANUP_EXECUTOR.shutdown();
                cleanupAllFiles();
            }));
        } catch (IOException e) {
            System.err.println("无法创建临时目录: " + e.getMessage());
            // 使用系统临时目录作为备选
            tempDirPath = Paths.get(System.getProperty("java.io.tmpdir"));
        }
    }

    /**
     * 私有构造函数，防止实例化
     */
    private TempFileManager() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 创建临时文件
     *
     * @param prefix 文件名前缀
     * @param suffix 文件名后缀（例如.txt）
     * @return 临时文件路径
     * @throws IOException 如果创建过程中发生IO错误
     */
    public static Path createTempFile(String prefix, String suffix) throws IOException {
        return createTempFile(prefix, suffix, DEFAULT_CLEANUP_DELAY_HOURS);
    }

    /**
     * 创建临时文件（指定过期时间）
     *
     * @param prefix 文件名前缀
     * @param suffix 文件名后缀（例如.txt）
     * @param expirationHours 过期时间（小时）
     * @return 临时文件路径
     * @throws IOException 如果创建过程中发生IO错误
     */
    public static Path createTempFile(String prefix, String suffix, long expirationHours) throws IOException {
        Path tempFile = Files.createTempFile(tempDirPath, prefix, suffix);

        // 记录文件及其过期时间
        long expirationTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(expirationHours);
        MANAGED_FILES.put(tempFile.toString(), expirationTime);

        return tempFile;
    }

    /**
     * 创建临时目录
     *
     * @param prefix 目录名前缀
     * @return 临时目录路径
     * @throws IOException 如果创建过程中发生IO错误
     */
    public static Path createTempDirectory(String prefix) throws IOException {
        return createTempDirectory(prefix, DEFAULT_CLEANUP_DELAY_HOURS);
    }

    /**
     * 创建临时目录（指定过期时间）
     *
     * @param prefix 目录名前缀
     * @param expirationHours 过期时间（小时）
     * @return 临时目录路径
     * @throws IOException 如果创建过程中发生IO错误
     */
    public static Path createTempDirectory(String prefix, long expirationHours) throws IOException {
        Path tempDir = Files.createTempDirectory(tempDirPath, prefix);

        // 记录目录及其过期时间
        long expirationTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(expirationHours);
        MANAGED_FILES.put(tempDir.toString(), expirationTime);

        return tempDir;
    }

    /**
     * 主动删除临时文件或目录
     *
     * @param path 文件或目录路径
     * @return 如果成功删除返回true，否则返回false
     */
    public static boolean deleteTempFile(Path path) {
        if (path == null) {
            return false;
        }

        try {
            if (Files.isDirectory(path)) {
                FileUtils.deleteDirectory(path.toString());
            } else {
                Files.deleteIfExists(path);
            }

            // 从管理列表中移除
            MANAGED_FILES.remove(path.toString());

            return true;
        } catch (IOException e) {
            System.err.println("删除临时文件失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 清理过期的临时文件
     */
    private static void cleanupExpiredFiles() {
        long currentTime = System.currentTimeMillis();
        List<String> expiredFiles = new ArrayList<>();

        // 找出过期的文件
        MANAGED_FILES.forEach((path, expirationTime) -> {
            if (expirationTime <= currentTime) {
                expiredFiles.add(path);
            }
        });

        // 删除过期文件
        for (String path : expiredFiles) {
            try {
                File file = new File(path);
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(path);
                } else {
                    file.delete();
                }

                // 从管理列表中移除
                MANAGED_FILES.remove(path);
            } catch (Exception e) {
                System.err.println("清理过期文件失败: " + e.getMessage());
            }
        }
    }

    /**
     * 清理所有临时文件
     */
    private static void cleanupAllFiles() {
        MANAGED_FILES.forEach((path, expirationTime) -> {
            try {
                File file = new File(path);
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(path);
                } else {
                    file.delete();
                }
            } catch (Exception e) {
                System.err.println("清理临时文件失败: " + e.getMessage());
            }
        });

        MANAGED_FILES.clear();

        // 尝试删除临时目录
        try {
            if (tempDirPath != null && Files.exists(tempDirPath)) {
                FileUtils.deleteDirectory(tempDirPath.toString());
            }
        } catch (Exception e) {
            System.err.println("清理临时目录失败: " + e.getMessage());
        }
    }

    /**
     * 获取临时目录路径
     *
     * @return 临时目录路径
     */
    public static Path getTempDirPath() {
        return tempDirPath;
    }

    /**
     * 获取当前管理的临时文件数量
     *
     * @return 临时文件数量
     */
    public static int getManagedFileCount() {
        return MANAGED_FILES.size();
    }
}