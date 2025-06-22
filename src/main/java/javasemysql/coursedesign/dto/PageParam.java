package javasemysql.coursedesign.dto;

/**
 * 分页参数数据传输对象
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class PageParam {
    private int pageNum;    // 当前页码
    private int pageSize;   // 每页记录数
    private int totalPage;  // 总页数
    private int totalCount; // 总记录数

    /**
     * 默认构造函数，初始化为第1页，每页10条记录
     */
    public PageParam() {
        this.pageNum = 1;
        this.pageSize = 10;
    }

    /**
     * 带参数的构造函数
     *
     * @param pageNum 页码
     * @param pageSize 每页记录数
     */
    public PageParam(int pageNum, int pageSize) {
        this.pageNum = pageNum > 0 ? pageNum : 1;
        this.pageSize = pageSize > 0 ? pageSize : 10;
    }

    /**
     * 获取当前页码
     *
     * @return 当前页码
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * 设置当前页码
     *
     * @param pageNum 当前页码
     */
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum > 0 ? pageNum : 1;
    }

    /**
     * 获取每页记录数
     *
     * @return 每页记录数
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页记录数
     *
     * @param pageSize 每页记录数
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize > 0 ? pageSize : 10;
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public int getTotalPage() {
        return totalPage;
    }

    /**
     * 设置总页数
     *
     * @param totalPage 总页数
     */
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    /**
     * 获取总记录数
     *
     * @return 总记录数
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 设置总记录数并自动计算总页数
     *
     * @param totalCount 总记录数
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        // 计算总页数
        this.totalPage = (totalCount + pageSize - 1) / pageSize;
    }

    /**
     * 获取数据库查询的起始位置
     *
     * @return 起始位置
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 获取数据库查询的记录数
     *
     * @return 查询记录数
     */
    public int getLimit() {
        return pageSize;
    }

    /**
     * 是否有上一页
     *
     * @return 是否有上一页
     */
    public boolean hasPrevious() {
        return pageNum > 1;
    }

    /**
     * 是否有下一页
     *
     * @return 是否有下一页
     */
    public boolean hasNext() {
        return pageNum < totalPage;
    }

    /**
     * 获取上一页页码
     *
     * @return 上一页页码
     */
    public int getPreviousPage() {
        return hasPrevious() ? pageNum - 1 : pageNum;
    }

    /**
     * 获取下一页页码
     *
     * @return 下一页页码
     */
    public int getNextPage() {
        return hasNext() ? pageNum + 1 : pageNum;
    }

    @Override
    public String toString() {
        return "PageParam{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", totalPage=" + totalPage +
                ", totalCount=" + totalCount +
                '}';
    }
}