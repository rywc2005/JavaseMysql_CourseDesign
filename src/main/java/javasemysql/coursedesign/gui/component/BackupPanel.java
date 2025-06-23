package javasemysql.coursedesign.gui.component;

import javasemysql.coursedesign.dto.BackupQueryParam;
import javasemysql.coursedesign.gui.MainFrame;
import javasemysql.coursedesign.gui.component.dialog.BackupDialog;
import javasemysql.coursedesign.model.Backup;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.BackupService;
import javasemysql.coursedesign.service.impl.BackupServiceImpl;
import javasemysql.coursedesign.utils.DateUtils;
import javasemysql.coursedesign.utils.LogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * 备份管理面板
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BackupPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(BackupPanel.class.getName());

    private MainFrame mainFrame;
    private User currentUser;
    private BackupService backupService;

    // UI组件
    private JTextField searchField;
    private JComboBox<String> timeRangeComboBox;
    private JTable backupTable;
    private DefaultTableModel tableModel;
    private JButton createButton;
    private JButton restoreButton;
    private JButton deleteButton;
    private JButton refreshButton;

    /**
     * 构造函数
     *
     * @param mainFrame 主窗口引用
     */
    public BackupPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.backupService = new BackupServiceImpl();

        initComponents();
        setupListeners();
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 240, 245));

        // 创建顶部面板
        JPanel topPanel = new JPanel(new BorderLayout(20, 0));
        topPanel.setOpaque(false);

        // 创建标题标签
        JLabel titleLabel = new JLabel("备份管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));

        // 创建信息标签
        JLabel infoLabel = new JLabel("定期备份您的数据，以防数据丢失");
        infoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(100, 100, 100));

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(infoLabel, BorderLayout.EAST);

        // 创建工具栏面板
        JPanel toolbarPanel = new JPanel(new BorderLayout(10, 0));
        toolbarPanel.setOpaque(false);
        toolbarPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // 创建搜索面板
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        // 创建搜索框
        searchField = new JTextField(20);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // 创建搜索图标
        JLabel searchIcon = new JLabel(new ImageIcon(getClass().getResource("/resources/images/search_icon.png")));
        searchIcon.setBorder(new EmptyBorder(0, 5, 0, 5));

        // 创建时间范围下拉框
        timeRangeComboBox = new JComboBox<>(new String[]{"所有时间", "最近一周", "最近一个月", "最近三个月", "最近半年", "最近一年"});
        timeRangeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 组装搜索面板
        JPanel searchFieldPanel = new JPanel(new BorderLayout());
        searchFieldPanel.setOpaque(false);
        searchFieldPanel.add(searchIcon, BorderLayout.WEST);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);

        searchPanel.add(searchFieldPanel, BorderLayout.CENTER);
        searchPanel.add(timeRangeComboBox, BorderLayout.EAST);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // 创建按钮
        createButton = new JButton("创建备份");
        createButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        createButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/backup_create_icon.png")));
        createButton.setFocusPainted(false);

        restoreButton = new JButton("恢复");
        restoreButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        restoreButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/backup_restore_icon.png")));
        restoreButton.setFocusPainted(false);
        restoreButton.setEnabled(false);

        deleteButton = new JButton("删除");
        deleteButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        deleteButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/delete_icon.png")));
        deleteButton.setFocusPainted(false);
        deleteButton.setEnabled(false);

        refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        refreshButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/refresh_icon.png")));
        refreshButton.setFocusPainted(false);

        buttonPanel.add(createButton);
        buttonPanel.add(restoreButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // 组装工具栏面板
        toolbarPanel.add(searchPanel, BorderLayout.WEST);
        toolbarPanel.add(buttonPanel, BorderLayout.EAST);

        // 创建表格
        createBackupTable();

        // 创建表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "备份列表",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14)
        ));
        tablePanel.setBackground(Color.WHITE);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(backupTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // 创建底部信息面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel tipLabel = new JLabel("<html><body>提示：备份文件保存在应用程序的 'backups' 目录下。为确保数据安全，建议定期创建备份并保存到其他位置。</body></html>");
        tipLabel.setFont(new Font("微软雅黑", Font.ITALIC, 12));
        tipLabel.setForeground(new Color(100, 100, 100));

        bottomPanel.add(tipLabel, BorderLayout.CENTER);

        // 添加组件到主面板
        add(topPanel, BorderLayout.NORTH);
        add(toolbarPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建备份表格
     */
    private void createBackupTable() {
        // 定义表格列名
        String[] columnNames = {"ID", "文件名", "创建时间", "描述", "文件路径"};

        // 创建表格模型
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置表格不可编辑
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Date.class; // 设置日期列为Date类型，以便正确排序
                }
                return String.class;
            }
        };

        // 创建表格
        backupTable = new JTable(tableModel);
        backupTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        backupTable.setRowHeight(30);
        backupTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        backupTable.setAutoCreateRowSorter(true);

        // 设置表格样式
        backupTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        backupTable.getTableHeader().setReorderingAllowed(false);
        backupTable.getTableHeader().setResizingAllowed(true);

        // 设置列宽
        backupTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        backupTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        backupTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        backupTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        backupTable.getColumnModel().getColumn(4).setPreferredWidth(300);

        // 隐藏ID列和文件路径列
        backupTable.getColumnModel().getColumn(0).setMinWidth(0);
        backupTable.getColumnModel().getColumn(0).setMaxWidth(0);
        backupTable.getColumnModel().getColumn(0).setWidth(0);

        backupTable.getColumnModel().getColumn(4).setMinWidth(0);
        backupTable.getColumnModel().getColumn(4).setMaxWidth(0);
        backupTable.getColumnModel().getColumn(4).setWidth(0);

        // 设置日期列的单元格渲染器，以便格式化显示
        DefaultTableCellRenderer dateRenderer = new DefaultTableCellRenderer() {
            private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Date) {
                    value = sdf.format((Date) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        // 应用日期列的渲染器
        backupTable.getColumnModel().getColumn(2).setCellRenderer(dateRenderer);

        // 创建表格排序器
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        backupTable.setRowSorter(sorter);
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 创建备份按钮点击事件
        createButton.addActionListener(e -> createBackup());

        // 恢复备份按钮点击事件
        restoreButton.addActionListener(e -> restoreSelectedBackup());

        // 删除备份按钮点击事件
        deleteButton.addActionListener(e -> deleteSelectedBackup());

        // 刷新按钮点击事件
        refreshButton.addActionListener(e -> refreshData());

        // 搜索框回车事件
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterBackups();
                }
            }
        });

        // 时间范围下拉框选择事件
        timeRangeComboBox.addActionListener(e -> filterBackups());

        // 表格选择事件
        backupTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = backupTable.getSelectedRow() != -1;
                restoreButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });

        // 表格双击事件
        backupTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && backupTable.getSelectedRow() != -1) {
                    showBackupDetails();
                }
            }
        });
    }

    /**
     * 创建备份
     */
    private void createBackup() {
        if (currentUser == null) {
            mainFrame.showErrorMessage("请先登录");
            return;
        }

        // 创建备份对话框
        BackupDialog dialog = new BackupDialog(mainFrame, currentUser);
        dialog.setVisible(true);

        // 如果备份创建成功，刷新数据
        if (dialog.isBackupCreated()) {
            refreshData();
        }
    }

    /**
     * 恢复选中的备份
     */
    private void restoreSelectedBackup() {
        int selectedRow = backupTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = backupTable.convertRowIndexToModel(selectedRow);

        // 获取备份ID和文件名
        int backupId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
        String fileName = tableModel.getValueAt(modelRow, 1).toString();
        String filePath = tableModel.getValueAt(modelRow, 4).toString();

        // 确认恢复
        boolean confirmed = mainFrame.showConfirmDialog("确定要恢复备份 \"" + fileName + "\" 吗？\n恢复操作将覆盖当前所有数据，此操作不可逆！");
        if (!confirmed) {
            return;
        }

        // 检查文件是否存在
        File backupFile = new File(filePath);
        if (!backupFile.exists()) {
            mainFrame.showErrorMessage("备份文件不存在，无法恢复");
            return;
        }

        // 执行恢复操作
        try {
            // 显示进度对话框
            ProgressDialog progressDialog = new ProgressDialog(mainFrame, "正在恢复备份...");
            progressDialog.setVisible(true);

            // 异步执行恢复操作
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return backupService.restoreBackup(backupId);
                }

                @Override
                protected void done() {
                    progressDialog.dispose();
                    try {
                        boolean success = get();
                        if (success) {
                            mainFrame.showInfoMessage("备份恢复成功，系统将重新启动以应用更改");

                            // 退出登录并重新加载系统
                            mainFrame.logout();
                        } else {
                            mainFrame.showErrorMessage("备份恢复失败");
                        }
                    } catch (Exception e) {
                        LogUtils.error("恢复备份失败", e);
                        mainFrame.showErrorMessage("恢复备份过程中发生错误: " + e.getMessage());
                    }
                }
            };

            worker.execute();
        } catch (Exception e) {
            LogUtils.error("恢复备份失败", e);
            mainFrame.showErrorMessage("恢复备份失败: " + e.getMessage());
        }
    }

    /**
     * 删除选中的备份
     */
    private void deleteSelectedBackup() {
        int selectedRow = backupTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = backupTable.convertRowIndexToModel(selectedRow);

        // 获取备份ID和文件名
        int backupId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
        String fileName = tableModel.getValueAt(modelRow, 1).toString();

        // 确认删除
        boolean confirmed = mainFrame.showConfirmDialog("确定要删除备份 \"" + fileName + "\" 吗？\n删除后将无法恢复。");
        if (!confirmed) {
            return;
        }

        // 执行删除操作
        boolean success = backupService.deleteBackup(backupId);
        if (success) {
            mainFrame.showInfoMessage("备份删除成功");
            refreshData();
        } else {
            mainFrame.showErrorMessage("备份删除失败");
        }
    }

    /**
     * 显示备份详情
     */
    private void showBackupDetails() {
        int selectedRow = backupTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = backupTable.convertRowIndexToModel(selectedRow);

        // 获取备份信息
        String fileName = tableModel.getValueAt(modelRow, 1).toString();
        Date createdAt = (Date) tableModel.getValueAt(modelRow, 2);
        String description = tableModel.getValueAt(modelRow, 3).toString();
        String filePath = tableModel.getValueAt(modelRow, 4).toString();

        // 创建详情对话框
        JDialog dialog = new JDialog(mainFrame, "备份详情", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(mainFrame);

        // 创建内容面板
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 创建信息面板
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        // 添加备份信息
        JLabel fileNameLabel = new JLabel("文件名:");
        fileNameLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        JLabel fileNameValue = new JLabel(fileName);
        fileNameValue.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JLabel createdAtLabel = new JLabel("创建时间:");
        createdAtLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        JLabel createdAtValue = new JLabel(DateUtils.formatDate(createdAt, DateUtils.DATETIME_FORMAT));
        createdAtValue.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JLabel filePathLabel = new JLabel("文件路径:");
        filePathLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        JLabel filePathValue = new JLabel(filePath);
        filePathValue.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JLabel descriptionLabel = new JLabel("描述:");
        descriptionLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        JLabel descriptionValue = new JLabel(description);
        descriptionValue.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 添加到信息面板
        infoPanel.add(fileNameLabel);
        infoPanel.add(fileNameValue);
        infoPanel.add(createdAtLabel);
        infoPanel.add(createdAtValue);
        infoPanel.add(filePathLabel);
        infoPanel.add(filePathValue);
        infoPanel.add(descriptionLabel);
        infoPanel.add(descriptionValue);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton restoreButton = new JButton("恢复此备份");
        restoreButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        restoreButton.addActionListener(e -> {
            dialog.dispose();
            restoreSelectedBackup();
        });

        JButton closeButton = new JButton("关闭");
        closeButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(restoreButton);
        buttonPanel.add(closeButton);

        // 添加到内容面板
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 设置对话框内容
        dialog.setContentPane(contentPanel);
        dialog.setVisible(true);
    }

    /**
     * 过滤备份
     */
    private void filterBackups() {
        if (currentUser == null) {
            return;
        }

        // 获取搜索关键字
        String keyword = searchField.getText().trim();

        // 获取选中的时间范围
        String selectedRange = (String) timeRangeComboBox.getSelectedItem();

        // 创建查询参数
        BackupQueryParam param = new BackupQueryParam(currentUser.getId());
        param.setDescription(keyword.isEmpty() ? null : keyword);

        // 设置时间范围
        Date[] dateRange = getDateRangeBySelection(selectedRange);
        if (dateRange != null) {
            param.setStartDate(dateRange[0]);
            param.setEndDate(dateRange[1]);
        }

        // 异步加载数据
        SwingWorker<List<Backup>, Void> worker = new SwingWorker<List<Backup>, Void>() {
            @Override
            protected List<Backup> doInBackground() throws Exception {
                return backupService.queryBackups(param);
            }

            @Override
            protected void done() {
                try {
                    List<Backup> backups = get();
                    updateBackupTable(backups);
                } catch (Exception e) {
                    LogUtils.error("过滤备份失败", e);
                    mainFrame.showErrorMessage("加载备份数据失败");
                }
            }
        };

        worker.execute();
    }

    /**
     * 根据选择获取日期范围
     *
     * @param selection 选择的时间范围
     * @return 日期范围数组[开始日期, 结束日期]，如果选择"所有时间"则返回null
     */
    private Date[] getDateRangeBySelection(String selection) {
        Date endDate = new Date(); // 当前时间
        Date startDate = null;

        switch (selection) {
            case "最近一周":
                startDate = DateUtils.addDays(endDate, -7);
                break;
            case "最近一个月":
                startDate = DateUtils.addMonths(endDate, -1);
                break;
            case "最近三个月":
                startDate = DateUtils.addMonths(endDate, -3);
                break;
            case "最近半年":
                startDate = DateUtils.addMonths(endDate, -6);
                break;
            case "最近一年":
                startDate = DateUtils.addMonths(endDate, -12);
                break;
            default: // "所有时间"
                return null;
        }

        return new Date[]{startDate, endDate};
    }

    /**
     * 更新备份表格
     *
     * @param backups 备份列表
     */
    private void updateBackupTable(List<Backup> backups) {
        // 清空表格
        tableModel.setRowCount(0);

        if (backups == null || backups.isEmpty()) {
            return;
        }

        // 添加数据到表格
        for (Backup backup : backups) {
            Vector<Object> row = new Vector<>();
            row.add(backup.getId());

            // 从文件路径中提取文件名
            String filePath = backup.getFilePath();
            String fileName = new File(filePath).getName();

            row.add(fileName);
            row.add(backup.getCreatedAt());
            row.add(filePath);

            tableModel.addRow(row);
        }
    }

    /**
     * 更新用户数据
     *
     * @param user 用户对象
     */
    public void updateUserData(User user) {
        this.currentUser = user;

        // 如果没有用户登录，清空表格
        if (user == null) {
            tableModel.setRowCount(0);
            return;
        }

        // 加载备份数据
        refreshData();
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        if (currentUser == null) {
            return;
        }

        // 异步加载数据
        SwingWorker<List<Backup>, Void> worker = new SwingWorker<List<Backup>, Void>() {
            @Override
            protected List<Backup> doInBackground() throws Exception {
                return backupService.getBackupsByUserId(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Backup> backups = get();
                    updateBackupTable(backups);
                } catch (Exception e) {
                    LogUtils.error("加载备份数据失败", e);
                    mainFrame.showErrorMessage("加载备份数据失败");
                }
            }
        };

        worker.execute();
    }

    /**
     * 进度对话框内部类
     */
    private class ProgressDialog extends JDialog {

        /**
         * 构造函数
         *
         * @param parent 父窗口
         * @param title 标题
         */
        public ProgressDialog(JFrame parent, String title) {
            super(parent, title, true);

            // 创建内容面板
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));

            // 创建进度条
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setPreferredSize(new Dimension(300, 20));

            // 创建标签
            JLabel label = new JLabel("请稍候，操作正在进行中...");
            label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            label.setHorizontalAlignment(JLabel.CENTER);

            // 添加组件
            panel.add(label, BorderLayout.NORTH);
            panel.add(progressBar, BorderLayout.CENTER);

            // 设置对话框属性
            setContentPane(panel);
            setSize(350, 150);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            setResizable(false);
        }
    }
}