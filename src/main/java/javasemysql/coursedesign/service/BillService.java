package javasemysql.coursedesign.service;

import javasemysql.coursedesign.dto.BillQueryParam;
import javasemysql.coursedesign.model.Bill;

import java.util.Date;
import java.util.List;

/**
 * 账单服务接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface BillService {

    /**
     * 根据用户ID获取账单列表
     *
     * @param userId 用户ID
     * @return 账单列表
     */
    List<Bill> getBillsByUserId(int userId);

    /**
     * 根据账单ID获取账单信息
     *
     * @param billId 账单ID
     * @return 账单对象，如果不存在则返回null
     */
    Bill getBillById(int billId);

    /**
     * 添加账单
     *
     * @param bill 账单对象
     * @return 是否添加成功
     */
    boolean addBill(Bill bill);

    /**
     * 更新账单
     *
     * @param bill 账单对象
     * @return 是否更新成功
     */
    boolean updateBill(Bill bill);

    /**
     * 删除账单
     *
     * @param billId 账单ID
     * @return 是否删除成功
     */
    boolean deleteBill(int billId);

    /**
     * 根据条件查询账单
     *
     * @param param 查询参数
     * @return 账单列表
     */
    List<Bill> queryBills(BillQueryParam param);

    /**
     * 获取即将到期的账单（未付款）
     *
     * @param userId 用户ID
     * @param days 天数（如7天内到期）
     * @return 账单列表
     */
    List<Bill> getUpcomingBills(int userId, int days);

    /**
     * 获取逾期账单（未付款且已过期）
     *
     * @param userId 用户ID
     * @return 账单列表
     */
    List<Bill> getOverdueBills(int userId);

    /**
     * 支付账单
     *
     * @param billId 账单ID
     * @param paymentDate 支付日期
     * @return 是否支付成功
     */
    boolean payBill(int billId, Date paymentDate);

    /**
     * 获取账单统计数据
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据，键为状态（已付、未付、已逾期），值为金额
     */
    java.util.Map<String, Double> getBillStatistics(int userId, Date startDate, Date endDate);
}