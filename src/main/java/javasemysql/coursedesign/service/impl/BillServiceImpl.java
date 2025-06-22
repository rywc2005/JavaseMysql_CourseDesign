package javasemysql.coursedesign.service.impl;

import javasemysql.coursedesign.dao.BillDao;
import javasemysql.coursedesign.dao.impl.BillDaoImpl;
import javasemysql.coursedesign.dto.BillQueryParam;
import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.model.Bill;
import javasemysql.coursedesign.service.AccountService;
import javasemysql.coursedesign.service.BillService;
import javasemysql.coursedesign.utils.DBUtils;
import javasemysql.coursedesign.utils.LogUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 账单服务实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BillServiceImpl implements BillService {

    private static final Logger logger = Logger.getLogger(BillServiceImpl.class.getName());

    private BillDao billDao;
    private AccountService accountService;

    /**
     * 构造函数
     */
    public BillServiceImpl() {
        this.billDao = new BillDaoImpl();
        this.accountService = new AccountServiceImpl();
    }

    @Override
    public List<Bill> getBillsByUserId(int userId) {
        Connection conn = null;
        List<Bill> bills = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            bills = billDao.findByUserId(conn, userId);
        } catch (SQLException e) {
            LogUtils.error("获取用户账单列表失败，用户ID: " + userId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return bills;
    }

    @Override
    public Bill getBillById(int billId) {
        Connection conn = null;
        Bill bill = null;

        try {
            conn = DBUtils.getConnection();
            bill = billDao.findById(conn, billId);
        } catch (SQLException e) {
            LogUtils.error("获取账单信息失败，账单ID: " + billId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return bill;
    }

    @Override
    public boolean addBill(Bill bill) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 检查账户是否存在
            if (bill.getAccountId() > 0) {
                Account account = accountService.getAccountById(bill.getAccountId());
                if (account == null) {
                    LogUtils.error("添加账单失败，账户不存在: " + bill.getAccountId());
                    return false;
                }

                // 设置账户名称
                bill.setAccountName(account.getName());
            }

            // 执行添加操作
            success = billDao.insert(conn, bill);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("添加账单失败", e);
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
    public boolean updateBill(Bill bill) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 获取原始账单信息
            Bill originalBill = billDao.findById(conn, bill.getId());
            if (originalBill == null) {
                LogUtils.error("更新账单失败，账单不存在: " + bill.getId());
                return false;
            }

            // 检查账户是否已更改
            if (bill.getAccountId() != originalBill.getAccountId()) {
                // 检查新账户是否存在
                Account account = accountService.getAccountById(bill.getAccountId());
                if (account == null) {
                    LogUtils.error("更新账单失败，账户不存在: " + bill.getAccountId());
                    return false;
                }

                // 更新账户名称
                bill.setAccountName(account.getName());
            }

            // 执行更新操作
            success = billDao.update(conn, bill);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("更新账单失败", e);
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
    public boolean deleteBill(int billId) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 检查账单是否存在
            Bill bill = billDao.findById(conn, billId);
            if (bill == null) {
                LogUtils.error("删除账单失败，账单不存在: " + billId);
                return false;
            }

            // 执行删除操作
            success = billDao.delete(conn, billId);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("删除账单失败", e);
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
    public List<Bill> queryBills(BillQueryParam param) {
        Connection conn = null;
        List<Bill> bills = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            bills = billDao.findByParam(conn, param);
        } catch (SQLException e) {
            LogUtils.error("查询账单失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return bills;
    }

    @Override
    public List<Bill> getUpcomingBills(int userId, int days) {
        Connection conn = null;
        List<Bill> bills = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();

            // 计算日期范围
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();

            calendar.add(Calendar.DAY_OF_MONTH, days);
            Date futureDate = calendar.getTime();

            // 获取即将到期的账单
            bills = billDao.findUpcomingBills(conn, userId, today, futureDate);
        } catch (SQLException e) {
            LogUtils.error("获取即将到期账单失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return bills;
    }

    @Override
    public List<Bill> getOverdueBills(int userId) {
        Connection conn = null;
        List<Bill> bills = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();

            // 获取当前日期
            Date today = new Date();

            // 获取逾期账单
            bills = billDao.findOverdueBills(conn, userId, today);
        } catch (SQLException e) {
            LogUtils.error("获取逾期账单失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return bills;
    }

    @Override
    public boolean payBill(int billId, Date paymentDate) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 获取账单信息
            Bill bill = billDao.findById(conn, billId);
            if (bill == null) {
                LogUtils.error("支付账单失败，账单不存在: " + billId);
                return false;
            }

            // 检查账单是否已支付
            if (bill.isPaid()) {
                LogUtils.error("支付账单失败，账单已支付: " + billId);
                return false;
            }

            // 更新账单状态
            bill.setPaid(true);
            bill.setPaymentDate(paymentDate);

            // 如果账单关联了账户，则更新账户余额
            if (bill.getAccountId() > 0) {
                // 检查账户是否存在
                boolean accountExists = accountService.accountExists(bill.getAccountId());
                if (!accountExists) {
                    LogUtils.error("支付账单失败，账户不存在: " + bill.getAccountId());
                    return false;
                }

                // 更新账户余额（支出）
                boolean balanceUpdated = accountService.updateAccountBalance(bill.getAccountId(), -bill.getAmount());
                if (!balanceUpdated) {
                    LogUtils.error("支付账单失败，更新账户余额失败: " + bill.getAccountId());
                    return false;
                }
            }

            // 更新账单
            success = billDao.update(conn, bill);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("支付账单失败", e);
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
    public Map<String, Double> getBillStatistics(int userId, Date startDate, Date endDate) {
        Connection conn = null;
        Map<String, Double> statistics = new HashMap<>();

        try {
            conn = DBUtils.getConnection();

            // 获取当前日期
            Date today = new Date();

            // 初始化统计数据
            statistics.put("paid", 0.0);
            statistics.put("unpaid", 0.0);
            statistics.put("overdue", 0.0);

            // 创建查询参数
            BillQueryParam param = new BillQueryParam(userId);
            param.setStartDate(startDate);
            param.setEndDate(endDate);

            // 获取所有账单
            List<Bill> bills = billDao.findByParam(conn, param);

            // 计算统计数据
            for (Bill bill : bills) {
                if (bill.isPaid()) {
                    // 已付款
                    double paidAmount = statistics.get("paid");
                    statistics.put("paid", paidAmount + bill.getAmount());
                } else {
                    // 未付款
                    if (bill.getDueDate().before(today)) {
                        // 已逾期
                        double overdueAmount = statistics.get("overdue");
                        statistics.put("overdue", overdueAmount + bill.getAmount());
                    } else {
                        // 未逾期
                        double unpaidAmount = statistics.get("unpaid");
                        statistics.put("unpaid", unpaidAmount + bill.getAmount());
                    }
                }
            }
        } catch (SQLException e) {
            LogUtils.error("获取账单统计数据失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return statistics;
    }
}