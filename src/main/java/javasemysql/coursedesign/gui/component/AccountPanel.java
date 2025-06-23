package javasemysql.coursedesign.gui.component;

import javasemysql.coursedesign.dto.AccountQueryParam;
import javasemysql.coursedesign.gui.MainFrame;
import javasemysql.coursedesign.gui.component.dialog.AccountDialog;
import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.AccountService;
import javasemysql.coursedesign.service.UserService;
import javasemysql.coursedesign.service.impl.AccountServiceImpl;
import javasemysql.coursedesign.service.impl.UserServiceImpl;
import javasemysql.coursedesign.utils.LogUtils;
import javasemysql.coursedesign.utils.StringUtils;

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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 账户管理面板
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class AccountPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(AccountPanel.class.getName());

    private MainFrame mainFrame;
    private User currentUser;
    private AccountService accountService;
    private UserService userService;

    // UI组件
    private JTextField searchField;
    private JComboBox<String> typeComboBox;
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private JLabel totalBalanceLabel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    /**
     * 构造函数
     *
     * @param mainFrame 主窗口引用
     */
    public AccountPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userService=new UserServiceImpl();
        this.accountService = new AccountServiceImpl();

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
        JLabel titleLabel = new JLabel("账户管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));

        // 创建总余额标签
        totalBalanceLabel = new JLabel("总余额: ¥0.00");
        totalBalanceLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        totalBalanceLabel.setForeground(new Color(66, 139, 202));

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(totalBalanceLabel, BorderLayout.EAST);

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
        logger.log(Level.INFO, "Search icon loaded successfully");

        // 创建类型下拉框
        typeComboBox = new JComboBox<>(new String[]{"所有类型", "现金", "银行卡", "信用卡", "支付宝", "微信", "其他"});
        typeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 组装搜索面板
        JPanel searchFieldPanel = new JPanel(new BorderLayout());
        searchFieldPanel.setOpaque(false);
        searchFieldPanel.add(searchIcon, BorderLayout.WEST);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);

        searchPanel.add(searchFieldPanel, BorderLayout.CENTER);
        searchPanel.add(typeComboBox, BorderLayout.EAST);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // 创建按钮
        addButton = new JButton("添加账户");
        addButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        addButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/add_icon.png")));
        addButton.setFocusPainted(false);

        editButton = new JButton("编辑");
        editButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        editButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/edit_icon.png")));
        editButton.setFocusPainted(false);
        editButton.setEnabled(false);

        deleteButton = new JButton("删除");
        deleteButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        deleteButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/delete_icon.png")));
        deleteButton.setFocusPainted(false);
        deleteButton.setEnabled(false);

        refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        refreshButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/refresh_icon.png")));
        refreshButton.setFocusPainted(false);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // 组装工具栏面板
        toolbarPanel.add(searchPanel, BorderLayout.WEST);
        toolbarPanel.add(buttonPanel, BorderLayout.EAST);

        // 创建表格
        createAccountTable();

        // 创建表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "账户列表",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14)
        ));
        tablePanel.setBackground(Color.WHITE);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(accountTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // 添加组件到主面板
        add(topPanel, BorderLayout.NORTH);
        add(toolbarPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);
    }

    /**
     * 创建账户表格
     */
    private void createAccountTable() {
        // 定义表格列名
        String[] columnNames = {"ID", "账户名称", "账户类型", "余额", "说明"};

        // 创建表格模型
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置表格不可编辑
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Double.class; // 设置余额列为Double类型，以便正确排序
                }
                return String.class;
            }
        };

        // 创建表格
        accountTable = new JTable(tableModel);
        accountTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        accountTable.setRowHeight(30);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.setAutoCreateRowSorter(true);

        // 设置表格样式
        accountTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        accountTable.getTableHeader().setReorderingAllowed(false);
        accountTable.getTableHeader().setResizingAllowed(true);

        // 设置列宽
        accountTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        accountTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        accountTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        accountTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        accountTable.getColumnModel().getColumn(4).setPreferredWidth(300);

        // 隐藏ID列
        accountTable.getColumnModel().getColumn(0).setMinWidth(0);
        accountTable.getColumnModel().getColumn(0).setMaxWidth(0);
        accountTable.getColumnModel().getColumn(0).setWidth(0);

        // 设置余额列的单元格渲染器，以便格式化显示
        DefaultTableCellRenderer balanceRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = (JLabel) comp;

                if (value != null) {
                    double balance = (Double) value;
                    label.setText(StringUtils.formatCurrency(balance));

                    // 根据余额设置颜色
                    if (balance < 0) {
                        label.setForeground(new Color(217, 83, 79)); // 红色
                    } else if (balance == 0) {
                        label.setForeground(new Color(0, 0, 0)); // 黑色
                    } else {
                        label.setForeground(new Color(92, 184, 92)); // 绿色
                    }
                }

                label.setHorizontalAlignment(JLabel.RIGHT);
                return label;
            }
        };

        // 应用余额列的渲染器
        accountTable.getColumnModel().getColumn(3).setCellRenderer(balanceRenderer);

        // 创建表格排序器
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        accountTable.setRowSorter(sorter);
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 添加账户按钮点击事件
        addButton.addActionListener(e -> addAccount());

        // 编辑账户按钮点击事件
        editButton.addActionListener(e -> editSelectedAccount());

        // 删除账户按钮点击事件
        deleteButton.addActionListener(e -> deleteSelectedAccount());

        // 刷新按钮点击事件
        refreshButton.addActionListener(e -> refreshData());

        // 搜索框回车事件
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterAccounts();
                }
            }
        });

        // 类型下拉框选择事件
        typeComboBox.addActionListener(e -> filterAccounts());

        // 表格选择事件
        accountTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = accountTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });

        // 表格双击事件
        accountTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && accountTable.getSelectedRow() != -1) {
                    editSelectedAccount();
                }
            }
        });
    }

    /**
     * 添加账户
     */
    private void addAccount() {
        if (currentUser == null) {
            mainFrame.showErrorMessage("请先登录");
            return;
        }

        // 创建账户对话框
        AccountDialog dialog = new AccountDialog(mainFrame, null, currentUser.getId());
        dialog.setVisible(true);

        // 如果账户添加成功，刷新数据
        if (dialog.isAccountSaved()) {
            refreshData();
        }
    }

    /**
     * 编辑选中的账户
     */
    private void editSelectedAccount() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = accountTable.convertRowIndexToModel(selectedRow);

        // 获取账户ID
        int accountId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());

        // 获取账户信息
        Account account = accountService.getAccountById(accountId);
        if (account == null) {
            mainFrame.showErrorMessage("获取账户信息失败");
            return;
        }

        // 创建账户对话框
        AccountDialog dialog = new AccountDialog(mainFrame, account, currentUser.getId());
        dialog.setVisible(true);

        // 如果账户编辑成功，刷新数据
        if (dialog.isAccountSaved()) {
            refreshData();
        }
    }

    /**
     * 删除选中的账户
     */
    private void deleteSelectedAccount() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取选中行的实际索引（考虑排序）
        int modelRow = accountTable.convertRowIndexToModel(selectedRow);

        // 获取账户ID和名称
        int accountId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
        String accountName = tableModel.getValueAt(modelRow, 1).toString();

        // 确认删除
        boolean confirmed = mainFrame.showConfirmDialog("确定要删除账户 \"" + accountName + "\" 吗？\n删除后将无法恢复，且相关收支记录也会被删除。");
        if (!confirmed) {
            return;
        }

        // 执行删除操作
        boolean success = accountService.deleteAccount(accountId);
        if (success) {
            mainFrame.showInfoMessage("账户删除成功");
            refreshData();
        } else {
            mainFrame.showErrorMessage("账户删除失败");
        }
    }

    /**
     * 过滤账户
     */
    private void filterAccounts() {
        if (currentUser == null) {
            return;
        }

        // 获取搜索关键字
        String keyword = searchField.getText().trim();

        // 获取选中的账户类型
        String selectedType = (String) typeComboBox.getSelectedItem();
        String type = "所有类型".equals(selectedType) ? null : selectedType;

        // 创建查询参数
        AccountQueryParam param = new AccountQueryParam(currentUser.getId());
        param.setName(keyword.isEmpty() ? null : keyword);
        param.setType(type);

        // 异步加载数据
        SwingWorker<List<Account>, Void> worker = new SwingWorker<List<Account>, Void>() {
            @Override
            protected List<Account> doInBackground() throws Exception {
                return accountService.queryAccounts(param);
            }

            @Override
            protected void done() {
                try {
                    List<Account> accounts = get();
                    updateAccountTable(accounts);
                } catch (Exception e) {
                    LogUtils.error("过滤账户失败", e);
                    mainFrame.showErrorMessage("加载账户数据失败");
                }
            }
        };

        worker.execute();
    }

    /**
     * 更新账户表格
     *
     * @param accounts 账户列表
     */
    private void updateAccountTable(List<Account> accounts) {
        // 清空表格
        tableModel.setRowCount(0);

        if (accounts == null || accounts.isEmpty()) {
            // 更新总余额
            totalBalanceLabel.setText("总余额: " + StringUtils.formatCurrency(0));
            return;
        }

        // 计算总余额
        double totalBalance = accounts.stream().mapToDouble(Account::getBalance).sum();

        // 更新总余额标签
        totalBalanceLabel.setText("总余额: " + StringUtils.formatCurrency(totalBalance));

        // 添加数据到表格
        for (Account account : accounts) {
            Vector<Object> row = new Vector<>();
            row.add(account.getId());
            row.add(account.getName());
            row.add(account.getBalance());
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
            totalBalanceLabel.setText("总余额: " + StringUtils.formatCurrency(0));
            return;
        }

        // 加载账户数据
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
        SwingWorker<List<Account>, Void> worker = new SwingWorker<List<Account>, Void>() {
            @Override
            protected List<Account> doInBackground() throws Exception {
                return accountService.getAccountsByUserId(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Account> accounts = get();
                    updateAccountTable(accounts);
                } catch (Exception e) {
                    LogUtils.error("加载账户数据失败", e);
                    mainFrame.showErrorMessage("加载账户数据失败");
                }
            }
        };

        worker.execute();
    }

    public void showAccountDetails(int id) {
        // 获取账户信息
        Account account = accountService.getAccountById(id);
        if (account == null) {
            mainFrame.showErrorMessage("获取账户信息失败");
            return;
        }

        // 创建账户对话框
        AccountDialog dialog = new AccountDialog(mainFrame, account, currentUser.getId());
        dialog.setVisible(true);

        // 如果账户编辑成功，刷新数据
        if (dialog.isAccountSaved()) {
            refreshData();
        }
    }
}