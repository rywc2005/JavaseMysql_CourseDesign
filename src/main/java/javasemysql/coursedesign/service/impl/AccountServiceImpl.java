package javasemysql.coursedesign.service.impl;

import javasemysql.coursedesign.dao.AccountDao;
import javasemysql.coursedesign.dao.impl.AccountDaoImpl;
import javasemysql.coursedesign.dto.AccountQueryParam;
import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.service.AccountService;
import javasemysql.coursedesign.utils.DBUtils;
import javasemysql.coursedesign.utils.LogUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 账户服务实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());

    private final AccountDao accountDao;

    /**
     * 构造函数
     */
    public AccountServiceImpl() {
        this.accountDao = new AccountDaoImpl();
    }

    @Override
    public List<Account> getAccountsByUserId(int userId) {
        Connection conn = null;
        List<Account> accounts = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            accounts = accountDao.findByUserId(conn, userId);
        } catch (SQLException e) {
            LogUtils.error("获取用户账户列表失败，用户ID: " + userId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return accounts;
    }

    @Override
    public Account getAccountById(int accountId) {
        Connection conn = null;
        Account account = null;

        try {
            conn = DBUtils.getConnection();
            account = accountDao.findById(conn, accountId);
        } catch (SQLException e) {
            LogUtils.error("获取账户信息失败，账户ID: " + accountId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return account;
    }

    @Override
    public boolean addAccount(Account account) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 执行添加操作
            success = accountDao.insert(conn, account);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("添加账户失败", e);
            // 回滚事务
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                LogUtils.error("回滚事务失败", ex);
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                LogUtils.error("恢复自动提交失败", e);
            }
            DBUtils.closeConnection(conn);
        }

        return success;
    }

    @Override
    public boolean updateAccount(Account account) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 获取原始账户信息
            Account originalAccount = accountDao.findById(conn, account.getId());
            if (originalAccount == null) {
                LogUtils.error("更新账户失败，账户不存在: " + account.getId());
                return false;
            }

            // 执行更新操作
            success = accountDao.update(conn, account);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("更新账户失败", e);
            // 回滚事务
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                LogUtils.error("回滚事务失败", ex);
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                LogUtils.error("恢复自动提交失败", e);
            }
            DBUtils.closeConnection(conn);
        }

        return success;
    }

    @Override
    public boolean deleteAccount(int accountId) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 检查账户是否存在
            Account account = accountDao.findById(conn, accountId);
            if (account == null) {
                LogUtils.error("删除账户失败，账户不存在: " + accountId);
                return false;
            }

            // 执行删除操作
            success = accountDao.delete(conn, accountId);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("删除账户失败", e);
            // 回滚事务
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                LogUtils.error("回滚事务失败", ex);
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                LogUtils.error("恢复自动提交失败", e);
            }
            DBUtils.closeConnection(conn);
        }

        return success;
    }

    @Override
    public double getTotalBalance(int userId) {
        Connection conn = null;
        double totalBalance = 0.0;

        try {
            conn = DBUtils.getConnection();
            List<Account> accounts = accountDao.findByUserId(conn, userId);

            // 计算总余额
            for (Account account : accounts) {
                totalBalance += account.getBalance();
            }
        } catch (SQLException e) {
            LogUtils.error("获取用户总资产失败，用户ID: " + userId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return totalBalance;
    }

    @Override
    public List<Account> queryAccounts(AccountQueryParam param) {
        Connection conn = null;
        List<Account> accounts = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            accounts = accountDao.findByParam(conn, param);
        } catch (SQLException e) {
            LogUtils.error("查询账户失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return accounts;
    }

    @Override
    public boolean updateAccountBalance(int accountId, double amount) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 获取原始账户信息
            Account account = accountDao.findById(conn, accountId);
            if (account == null) {
                LogUtils.error("更新账户余额失败，账户不存在: " + accountId);
                return false;
            }

            // 计算新余额
            double newBalance = account.getBalance() + amount;

            // 更新余额
            account.setBalance(newBalance);
            success = accountDao.update(conn, account);

            // 提交事务
            if (success) {
                conn.commit();
                LogUtils.info("账户余额已更新：" + accountId + "，变动金额：" + amount + "，新余额：" + newBalance);
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("更新账户余额失败", e);
            // 回滚事务
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                LogUtils.error("回滚事务失败", ex);
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                LogUtils.error("恢复自动提交失败", e);
            }
            DBUtils.closeConnection(conn);
        }

        return success;
    }

    @Override
    public boolean accountExists(int accountId) {
        Connection conn = null;
        boolean exists = false;

        try {
            conn = DBUtils.getConnection();
            Account account = accountDao.findById(conn, accountId);
            exists = (account != null);
        } catch (SQLException e) {
            LogUtils.error("检查账户是否存在失败，账户ID: " + accountId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return exists;
    }

    @Override
    public Account getDefaultAccount(int userId) {
        Connection conn = null;
        Account defaultAccount = null;

        try {
            conn = DBUtils.getConnection();
            List<Account> accounts = accountDao.findByUserId(conn, userId);

            if (!accounts.isEmpty()) {
                // 返回第一个账户作为默认账户
                defaultAccount = accounts.get(0);
            }
        } catch (SQLException e) {
            LogUtils.error("获取默认账户失败，用户ID: " + userId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return defaultAccount;
    }
}