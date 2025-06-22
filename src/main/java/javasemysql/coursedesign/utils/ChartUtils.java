package javasemysql.coursedesign.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * 图表工具类，用于生成各种统计图表
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class ChartUtils {

    // 图表标题字体
    private static final Font TITLE_FONT = new Font("微软雅黑", Font.BOLD, 16);

    // 图表轴标签字体
    private static final Font LABEL_FONT = new Font("微软雅黑", Font.PLAIN, 12);

    // 图表颜色
    private static final Color[] CHART_COLORS = {
            new Color(65, 105, 225),  // 蓝色
            new Color(46, 139, 87),   // 绿色
            new Color(178, 34, 34),   // 红色
            new Color(255, 165, 0),   // 橙色
            new Color(128, 0, 128),   // 紫色
            new Color(0, 128, 128),   // 青色
            new Color(255, 192, 203), // 粉色
            new Color(128, 128, 0),   // 橄榄色
            new Color(70, 130, 180),  // 钢蓝色
            new Color(210, 105, 30)   // 巧克力色
    };

    /**
     * 生成支出分类饼图
     *
     * @param categoryExpenses 类别-支出金额映射
     * @return 饼图对象
     */
    public static JFreeChart createExpensePieChart(Map<String, Double> categoryExpenses) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        // 添加数据
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // 创建饼图
        JFreeChart chart = ChartFactory.createPieChart(
                "支出类别分布",  // 图表标题
                dataset,         // 数据集
                true,            // 显示图例
                true,            // 生成工具提示
                false            // 不生成URL
        );

        // 设置饼图样式
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(LABEL_FONT);
        plot.setNoDataMessage("没有数据可显示");

        // 设置标题字体
        chart.getTitle().setFont(TITLE_FONT);

        // 设置图例字体
        chart.getLegend().setItemFont(LABEL_FONT);

        return chart;
    }

    /**
     * 生成收入分类饼图
     *
     * @param categoryIncomes 类别-收入金额映射
     * @return 饼图对象
     */
    public static JFreeChart createIncomePieChart(Map<String, Double> categoryIncomes) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        // 添加数据
        for (Map.Entry<String, Double> entry : categoryIncomes.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // 创建饼图
        JFreeChart chart = ChartFactory.createPieChart(
                "收入类别分布",  // 图表标题
                dataset,         // 数据集
                true,            // 显示图例
                true,            // 生成工具提示
                false            // 不生成URL
        );

        // 设置饼图样式
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(LABEL_FONT);
        plot.setNoDataMessage("没有数据可显示");

        // 设置标题字体
        chart.getTitle().setFont(TITLE_FONT);

        // 设置图例字体
        chart.getLegend().setItemFont(LABEL_FONT);

        return chart;
    }

    /**
     * 生成收支趋势柱状图
     *
     * @param incomeByDate 日期-收入金额映射
     * @param expenseByDate 日期-支出金额映射
     * @return 柱状图对象
     */
    public static JFreeChart createIncomeExpenseBarChart(Map<Date, Double> incomeByDate, Map<Date, Double> expenseByDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 合并日期并排序
        TreeMap<Date, Boolean> allDates = new TreeMap<>();
        for (Date date : incomeByDate.keySet()) {
            allDates.put(date, true);
        }
        for (Date date : expenseByDate.keySet()) {
            allDates.put(date, true);
        }

        // 填充数据集
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        for (Date date : allDates.keySet()) {
            String dateStr = sdf.format(date);
            Double income = incomeByDate.getOrDefault(date, 0.0);
            Double expense = expenseByDate.getOrDefault(date, 0.0);

            dataset.addValue(income, "收入", dateStr);
            dataset.addValue(expense, "支出", dateStr);
        }

        // 创建柱状图
        JFreeChart chart = ChartFactory.createBarChart(
                "收支趋势",               // 图表标题
                "日期",                   // X轴标签
                "金额",                   // Y轴标签
                dataset,                  // 数据集
                PlotOrientation.VERTICAL, // 图表方向
                true,                     // 显示图例
                true,                     // 生成工具提示
                false                     // 不生成URL
        );

        // 设置图表样式
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // 设置柱状图颜色
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, CHART_COLORS[0]);  // 收入柱颜色
        renderer.setSeriesPaint(1, CHART_COLORS[2]);  // 支出柱颜色

        // 设置字体
        chart.getTitle().setFont(TITLE_FONT);
        plot.getDomainAxis().setLabelFont(LABEL_FONT);
        plot.getRangeAxis().setLabelFont(LABEL_FONT);
        plot.getDomainAxis().setTickLabelFont(LABEL_FONT);
        plot.getRangeAxis().setTickLabelFont(LABEL_FONT);
        chart.getLegend().setItemFont(LABEL_FONT);

        return chart;
    }

    /**
     * 生成收支趋势折线图
     *
     * @param incomeByDate 日期-收入金额映射
     * @param expenseByDate 日期-支出金额映射
     * @return 折线图对象
     */
    public static JFreeChart createIncomeExpenseLineChart(Map<Date, Double> incomeByDate, Map<Date, Double> expenseByDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 合并日期并排序
        TreeMap<Date, Boolean> allDates = new TreeMap<>();
        for (Date date : incomeByDate.keySet()) {
            allDates.put(date, true);
        }
        for (Date date : expenseByDate.keySet()) {
            allDates.put(date, true);
        }

        // 填充数据集
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        for (Date date : allDates.keySet()) {
            String dateStr = sdf.format(date);
            Double income = incomeByDate.getOrDefault(date, 0.0);
            Double expense = expenseByDate.getOrDefault(date, 0.0);

            dataset.addValue(income, "收入", dateStr);
            dataset.addValue(expense, "支出", dateStr);
        }

        // 创建折线图
        JFreeChart chart = ChartFactory.createLineChart(
                "收支趋势",               // 图表标题
                "日期",                   // X轴标签
                "金额",                   // Y轴标签
                dataset,                  // 数据集
                PlotOrientation.VERTICAL, // 图表方向
                true,                     // 显示图例
                true,                     // 生成工具提示
                false                     // 不生成URL
        );

        // 设置图表样式
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // 设置线条样式
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, CHART_COLORS[0]);  // 收入线颜色
        renderer.setSeriesPaint(1, CHART_COLORS[2]);  // 支出线颜色
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);

        // 设置字体
        chart.getTitle().setFont(TITLE_FONT);
        plot.getDomainAxis().setLabelFont(LABEL_FONT);
        plot.getRangeAxis().setLabelFont(LABEL_FONT);
        plot.getDomainAxis().setTickLabelFont(LABEL_FONT);
        plot.getRangeAxis().setTickLabelFont(LABEL_FONT);
        chart.getLegend().setItemFont(LABEL_FONT);

        return chart;
    }

    /**
     * 生成预算执行情况条形图
     *
     * @param budgetAmounts 类别-预算金额映射
     * @param actualExpenses 类别-实际支出映射
     * @return 条形图对象
     */
    public static JFreeChart createBudgetExecutionChart(Map<String, Double> budgetAmounts, Map<String, Double> actualExpenses) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 添加数据
        for (String category : budgetAmounts.keySet()) {
            double budgetAmount = budgetAmounts.get(category);
            double actualExpense = actualExpenses.getOrDefault(category, 0.0);

            dataset.addValue(budgetAmount, "预算", category);
            dataset.addValue(actualExpense, "实际支出", category);
        }

        // 创建条形图
        JFreeChart chart = ChartFactory.createBarChart(
                "预算执行情况",             // 图表标题
                "类别",                    // X轴标签
                "金额",                    // Y轴标签
                dataset,                   // 数据集
                PlotOrientation.HORIZONTAL, // 图表方向
                true,                      // 显示图例
                true,                      // 生成工具提示
                false                      // 不生成URL
        );

        // 设置图表样式
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // 设置条形图颜色
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, CHART_COLORS[1]);  // 预算条颜色
        renderer.setSeriesPaint(1, CHART_COLORS[2]);  // 实际支出条颜色

        // 设置字体
        chart.getTitle().setFont(TITLE_FONT);
        plot.getDomainAxis().setLabelFont(LABEL_FONT);
        plot.getRangeAxis().setLabelFont(LABEL_FONT);
        plot.getDomainAxis().setTickLabelFont(LABEL_FONT);
        plot.getRangeAxis().setTickLabelFont(LABEL_FONT);
        chart.getLegend().setItemFont(LABEL_FONT);

        return chart;
    }

    /**
     * 生成账户余额饼图
     *
     * @param accountBalances 账户名称-余额映射
     * @return 饼图对象
     */
    public static JFreeChart createAccountBalancePieChart(Map<String, Double> accountBalances) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        // 添加数据
        for (Map.Entry<String, Double> entry : accountBalances.entrySet()) {
            if (entry.getValue() > 0) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        // 创建饼图
        JFreeChart chart = ChartFactory.createPieChart(
                "账户余额分布",  // 图表标题
                dataset,        // 数据集
                true,           // 显示图例
                true,           // 生成工具提示
                false           // 不生成URL
        );

        // 设置饼图样式
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(LABEL_FONT);
        plot.setNoDataMessage("没有数据可显示");

        // 设置标题字体
        chart.getTitle().setFont(TITLE_FONT);

        // 设置图例字体
        chart.getLegend().setItemFont(LABEL_FONT);

        return chart;
    }

    /**
     * 设置图表的字体和颜色（辅助方法）
     *
     * @param chart 图表对象
     */
    private static void setupChartStyle(JFreeChart chart) {
        // 设置标题字体
        chart.getTitle().setFont(TITLE_FONT);

        // 设置图例字体
        chart.getLegend().setItemFont(LABEL_FONT);

        // 根据图表类型设置轴字体
        if (chart.getPlot() instanceof CategoryPlot) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.getDomainAxis().setLabelFont(LABEL_FONT);
            plot.getRangeAxis().setLabelFont(LABEL_FONT);
            plot.getDomainAxis().setTickLabelFont(LABEL_FONT);
            plot.getRangeAxis().setTickLabelFont(LABEL_FONT);
        } else if (chart.getPlot() instanceof XYPlot) {
            XYPlot plot = (XYPlot) chart.getPlot();
            plot.getDomainAxis().setLabelFont(LABEL_FONT);
            plot.getRangeAxis().setLabelFont(LABEL_FONT);
            plot.getDomainAxis().setTickLabelFont(LABEL_FONT);
            plot.getRangeAxis().setTickLabelFont(LABEL_FONT);
        }
    }
}