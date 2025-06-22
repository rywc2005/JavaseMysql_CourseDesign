package javasemysql.coursedesign.service.impl;

import javasemysql.coursedesign.dao.BackupDao;
import javasemysql.coursedesign.dao.impl.BackupDaoImpl;
import javasemysql.coursedesign.dto.BackupQueryParam;
import javasemysql.coursedesign.model.Backup;
import javasemysql.coursedesign.service.BackupService;
import javasemysql.coursedesign.utils.DBUtils;
import javasemysql.coursedesign.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 * 备份服务实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BackupServiceImpl implements BackupService {

    private static final Logger logger = Logger.getLogger(BackupServiceImpl.class.getName());

    private final BackupDao backupDao;

    // MySQL数据库备份和恢复命令的路径
    private static final String MYSQL_DUMP_PATH = "mysqldump";
    private static final String MYSQL_PATH = "mysql";

    // 备份文件存储目录
    private static final String BACKUP_DIR = "backups";

    /**
     * 构造函数
     */
    public BackupServiceImpl() {
        this.backupDao = new BackupDaoImpl();

        // 确保备份目录存在
        createBackupDirectory();
    }

    /**
     * 创建备份目录
     */
    private void createBackupDirectory() {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            boolean created = backupDir.mkdirs();
            if (!created) {
                LogUtils.error("无法创建备份目录: " + BACKUP_DIR);
            }
        }
    }

    @Override
    public Backup createBackup(int userId, String description, boolean compress) {
        Connection conn = null;
        Backup backup = null;

        try {
            // 创建备份对象
            backup = new Backup();
            backup.setUserId(userId);
            backup.setCreatedAt((Timestamp) new Date());

            // 生成备份文件名
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "backup_" + userId + "_" + timestamp + (compress ? ".sql.gz" : ".sql");
            String filePath = BACKUP_DIR + File.separator + fileName;
            backup.setFilePath(new File(filePath).getAbsolutePath());

            // 执行数据库备份
            boolean backupSuccess = backupDatabase(filePath, compress);

            if (!backupSuccess) {
                LogUtils.error("数据库备份失败");
                return null;
            }

            // 将备份记录保存到数据库
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false);

            boolean inserted = backupDao.insert(conn, backup);

            if (inserted) {
                conn.commit();
                LogUtils.info("备份创建成功: " + backup.getFilePath());
                return backup;
            } else {
                conn.rollback();
                // 备份记录保存失败，删除备份文件
                new File(filePath).delete();
                LogUtils.error("备份记录保存失败");
                return null;
            }
        } catch (Exception e) {
            LogUtils.error("创建备份失败", e);

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                LogUtils.error("回滚事务失败", ex);
            }

            // 出现异常，删除备份文件
            if (backup != null && backup.getFilePath() != null) {
                new File(backup.getFilePath()).delete();
            }

            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                LogUtils.error("关闭数据库连接失败", e);
            }
        }
    }

    @Override
    public boolean restoreBackup(int backupId) {
        Connection conn = null;

        try {
            // 获取备份信息
            conn = DBUtils.getConnection();
            Backup backup = backupDao.findById(conn, backupId);

            if (backup == null) {
                LogUtils.error("备份不存在: " + backupId);
                return false;
            }

            // 检查备份文件是否存在
            File backupFile = new File(backup.getFilePath());
            if (!backupFile.exists()) {
                LogUtils.error("备份文件不存在: " + backup.getFilePath());
                return false;
            }

            // 执行数据库恢复
            boolean restoreSuccess = restoreDatabase(backup.getFilePath());

            if (restoreSuccess) {
                LogUtils.info("备份恢复成功: " + backup.getFilePath());
                return true;
            } else {
                LogUtils.error("备份恢复失败: " + backup.getFilePath());
                return false;
            }
        } catch (Exception e) {
            LogUtils.error("恢复备份失败", e);
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                LogUtils.error("关闭数据库连接失败", e);
            }
        }
    }

    @Override
    public List<Backup> getBackupsByUserId(int userId) {
        Connection conn = null;
        List<Backup> backups = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            backups = backupDao.findByUserId(conn, userId);
        } catch (SQLException e) {
            LogUtils.error("获取用户备份列表失败", e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                LogUtils.error("关闭数据库连接失败", e);
            }
        }

        return backups;
    }

    @Override
    public Backup getBackupById(int backupId) {
        Connection conn = null;
        Backup backup = null;

        try {
            conn = DBUtils.getConnection();
            backup = backupDao.findById(conn, backupId);
        } catch (SQLException e) {
            LogUtils.error("获取备份信息失败", e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                LogUtils.error("关闭数据库连接失败", e);
            }
        }

        return backup;
    }

    @Override
    public boolean deleteBackup(int backupId) {
        Connection conn = null;

        try {
            // 获取备份信息
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false);

            Backup backup = backupDao.findById(conn, backupId);

            if (backup == null) {
                LogUtils.error("备份不存在: " + backupId);
                return false;
            }

            // 删除备份记录
            boolean deleted = backupDao.delete(conn, backupId);

            if (deleted) {
                // 删除备份文件
                File backupFile = new File(backup.getFilePath());
                if (backupFile.exists()) {
                    boolean fileDeleted = backupFile.delete();
                    if (!fileDeleted) {
                        LogUtils.warn("备份文件删除失败: " + backup.getFilePath());
                    }
                }

                conn.commit();
                LogUtils.info("备份删除成功: " + backupId);
                return true;
            } else {
                conn.rollback();
                LogUtils.error("备份记录删除失败: " + backupId);
                return false;
            }
        } catch (Exception e) {
            LogUtils.error("删除备份失败", e);

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                LogUtils.error("回滚事务失败", ex);
            }

            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                LogUtils.error("关闭数据库连接失败", e);
            }
        }
    }

    @Override
    public List<Backup> queryBackups(BackupQueryParam param) {
        Connection conn = null;
        List<Backup> backups = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            backups = backupDao.findByParam(conn, param);
        } catch (SQLException e) {
            LogUtils.error("查询备份失败", e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                LogUtils.error("关闭数据库连接失败", e);
            }
        }

        return backups;
    }

    /**
     * 执行数据库备份
     *
     * @param filePath 备份文件路径
     * @param compress 是否压缩
     * @return 是否备份成功
     */
    private boolean backupDatabase(String filePath, boolean compress) {
        Process process = null;

        try {
            // 获取数据库连接信息
            Properties dbProps = DBUtils.getDBProperties();
            String dbName = dbProps.getProperty("db.name");
            String dbUser = dbProps.getProperty("db.user");
            String dbPassword = dbProps.getProperty("db.password");
            String dbHost = dbProps.getProperty("db.host", "localhost");
            String dbPort = dbProps.getProperty("db.port", "3306");

            // 构建备份命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    MYSQL_DUMP_PATH,
                    "--host=" + dbHost,
                    "--port=" + dbPort,
                    "--user=" + dbUser,
                    "--password=" + dbPassword,
                    "--add-drop-database",
                    "--add-drop-table",
                    "--databases",
                    dbName
            );

            // 启动进程
            process = processBuilder.start();

            // 获取输入流
            InputStream is = process.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            // 创建输出文件
            OutputStream os;
            if (compress) {
                // 压缩备份
                os = new GZIPOutputStream(new FileOutputStream(filePath));
            } else {
                os = new FileOutputStream(filePath);
            }
            BufferedOutputStream bos = new BufferedOutputStream(os);

            // 将数据写入文件
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }

            bos.flush();
            bos.close();
            bis.close();

            // 等待进程结束
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                LogUtils.info("数据库备份成功: " + filePath);
                return true;
            } else {
                LogUtils.error("数据库备份失败，退出代码: " + exitCode);

                // 如果备份失败，删除备份文件
                new File(filePath).delete();
                return false;
            }
        } catch (Exception e) {
            LogUtils.error("执行数据库备份失败", e);

            // 出现异常，删除备份文件
            new File(filePath).delete();

            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 执行数据库恢复
     *
     * @param filePath 备份文件路径
     * @return 是否恢复成功
     */
    private boolean restoreDatabase(String filePath) {
        Process process = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            // 获取数据库连接信息
            Properties dbProps = DBUtils.getDBProperties();
            String dbName = dbProps.getProperty("db.name");
            String dbUser = dbProps.getProperty("db.user");
            String dbPassword = dbProps.getProperty("db.password");
            String dbHost = dbProps.getProperty("db.host", "localhost");
            String dbPort = dbProps.getProperty("db.port", "3306");

            // 构建恢复命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    MYSQL_PATH,
                    "--host=" + dbHost,
                    "--port=" + dbPort,
                    "--user=" + dbUser,
                    "--password=" + dbPassword
            );

            // 启动进程
            process = processBuilder.start();

            // 获取输出流（发送到mysql进程）
            os = process.getOutputStream();

            // 读取备份文件
            if (filePath.endsWith(".gz")) {
                // 解压缩并发送到mysql进程
                Process decompressProcess = new ProcessBuilder("gunzip", "-c", filePath).start();
                is = decompressProcess.getInputStream();
            } else {
                // 直接读取备份文件
                is = new FileInputStream(filePath);
            }

            // 将备份文件数据发送到mysql进程
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            os.flush();
            os.close();
            is.close();

            // 等待进程结束
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                LogUtils.info("数据库恢复成功: " + filePath);
                return true;
            } else {
                LogUtils.error("数据库恢复失败，退出代码: " + exitCode);
                return false;
            }
        } catch (Exception e) {
            LogUtils.error("执行数据库恢复失败", e);
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                LogUtils.error("关闭流失败", e);
            }

            if (process != null) {
                process.destroy();
            }
        }
    }
}