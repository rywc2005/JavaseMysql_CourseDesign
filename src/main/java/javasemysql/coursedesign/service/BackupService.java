package javasemysql.coursedesign.service;

import javasemysql.coursedesign.dto.BackupQueryParam;
import javasemysql.coursedesign.model.Backup;

import java.util.List;

/**
 * 备份服务接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface BackupService {

    /**
     * 创建备份
     *
     * @param userId 用户ID
     * @param description 备份描述
     * @param compress 是否压缩
     * @return 备份对象
     */
    Backup createBackup(int userId, String description, boolean compress);

    /**
     * 恢复备份
     *
     * @param backupId 备份ID
     * @return 是否恢复成功
     */
    boolean restoreBackup(int backupId);

    /**
     * 获取用户的所有备份
     *
     * @param userId 用户ID
     * @return 备份列表
     */
    List<Backup> getBackupsByUserId(int userId);

    /**
     * 根据ID获取备份
     *
     * @param backupId 备份ID
     * @return 备份对象
     */
    Backup getBackupById(int backupId);

    /**
     * 删除备份
     *
     * @param backupId 备份ID
     * @return 是否删除成功
     */
    boolean deleteBackup(int backupId);

    /**
     * 查询备份
     *
     * @param param 查询参数
     * @return 备份列表
     */
    List<Backup> queryBackups(BackupQueryParam param);
}