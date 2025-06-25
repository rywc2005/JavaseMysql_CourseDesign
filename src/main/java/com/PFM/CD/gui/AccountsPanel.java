package com.PFM.CD.gui;

import com.PFM.CD.entity.enums.AccountStatus;
import com.PFM.CD.service.dto.AccountDto;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.AccountService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 账户面板：账户的创建、编辑、删除和查询
 * 使用 AccountDto 保证与 Service/Entity/DTO 规范对齐
 */
public class AccountsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private final AccountController controller;
    private final int userId; // 登录用户ID

    public AccountsPanel(int userId, AccountService accountService) {
        this.userId = userId;
        this.controller = new AccountControllerImpl(accountService);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        topPanel.setBackground(Color.WHITE);

        searchField = new JTextField(18);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JButton searchButton = new JButton("查询");
        searchButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchButton.addActionListener(e -> doSearch());

        refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> loadAccounts(null));

        addButton = new JButton("新建账户");
        addButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        addButton.setBackground(new Color(0, 123, 255));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addAccount());

        editButton = new JButton("编辑账户");
        editButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        editButton.addActionListener(e -> editAccount());

        deleteButton = new JButton("删除账户");
        deleteButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        deleteButton.setForeground(Color.RED.darker());
        deleteButton.addActionListener(e -> deleteAccount());

        topPanel.add(new JLabel("账户查询:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(refreshButton);
        topPanel.add(Box.createHorizontalStrut(28));
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);

        // 表格
        String[] columns = {"ID", "账户名称", "余额", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 初始化数据
        loadAccounts(null);

        // 双击编辑
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    editAccount();
                }
            }
        });
    }

    private void loadAccounts(String keyword) {
        tableModel.setRowCount(0);
        List<AccountDto> displayList = controller.queryAccounts(userId, keyword);

        for (AccountDto acc : displayList) {
            tableModel.addRow(new Object[]{
                    acc.getAccountId(),
                    acc.getAccountName(),
                    acc.getBalance(),
                    getStatusDisplay(acc.getStatus())
            });
        }
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        loadAccounts(keyword);
    }

    private AccountDto getSelectedAccount() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        int accountId = (int) tableModel.getValueAt(row, 0);
        return controller.getAccountById(accountId);
    }

    private void addAccount() {
        AccountDialog dialog = new AccountDialog(null);
        dialog.setVisible(true);
        AccountDto newAcc = dialog.getAccount();
        if (newAcc != null) {
            try {
                controller.addAccount(userId, newAcc.getAccountName(), newAcc.getBalance());
                loadAccounts(null);
            } catch (ServiceException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void editAccount() {
        AccountDto acc = getSelectedAccount();
        if (acc == null) {
            showWarn("请选择要编辑的账户。");
            return;
        }
        AccountDialog dialog = new AccountDialog(acc);
        dialog.setVisible(true);
        AccountDto updated = dialog.getAccount();
        if (updated != null) {
            try {
                acc.setAccountName(updated.getAccountName());
                // 余额编辑建议特殊处理，这里只允许改名，余额调整引导到专门功能
                controller.updateAccount(acc);
                loadAccounts(null);
            } catch (ServiceException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void deleteAccount() {
        AccountDto acc = getSelectedAccount();
        if (acc == null) {
            showWarn("请选择要删除的账户。");
            return;
        }
        if (acc.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            showWarn("账户余额不为零，无法直接删除。请先转移资金或设置转移账户功能。");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this,
                "确认要删除账户【" + acc.getAccountName() + "】吗？", "删除确认",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            try {
                controller.deleteAccount(acc.getAccountId(), null);
                loadAccounts(null);
            } catch (ServiceException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "错误", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getStatusDisplay(AccountStatus status) {
        switch (status) {
            case ACTIVE: return "活跃";
            case INACTIVE: return "停用";
            default: return "未知";
        }
    }

    // ===== Controller层（Service桥接） =====

    public interface AccountController {
        List<AccountDto> queryAccounts(int userId, String keyword);
        void addAccount(int userId, String accountName, BigDecimal initialBalance) throws ServiceException;
        void updateAccount(AccountDto account) throws ServiceException;
        void deleteAccount(int accountId, Integer transferAccountId) throws ServiceException;
        AccountDto getAccountById(int accountId);
    }

    public static class AccountControllerImpl implements AccountController {
        private final AccountService accountService;

        public AccountControllerImpl(AccountService accountService) {
            this.accountService = accountService;
        }

        @Override
        public List<AccountDto> queryAccounts(int userId, String keyword) {
            try {
                List<AccountDto> all = accountService.getUserAccounts(userId)
                        .stream()
                        .map(acc -> new AccountDto(
                                acc.getAccountId(),
                                acc.getUserId(),
                                acc.getAccountName(),
                                acc.getBalance(),
                                acc.getStatus()
                        ))
                        .toList();
                if (keyword == null || keyword.isEmpty()) return all;
                return all.stream().filter(a ->
                        (a.getAccountName() != null && a.getAccountName().contains(keyword))
                ).toList();
            } catch (ServiceException e) {
                JOptionPane.showMessageDialog(null, "加载账户失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return java.util.Collections.emptyList();
            }
        }

        @Override
        public void addAccount(int userId, String accountName, BigDecimal initialBalance) throws ServiceException {
            accountService.createAccount(userId, accountName, initialBalance);
        }

        @Override
        public void updateAccount(AccountDto account) throws ServiceException {
            // 这里只允许更新账户名，状态和余额等不在本面板更新
            com.PFM.CD.entity.Account entity = accountService.getAccountById(account.getAccountId());
            entity.setAccountName(account.getAccountName());
            accountService.updateAccount(entity);
        }

        @Override
        public void deleteAccount(int accountId, Integer transferAccountId) throws ServiceException {
            accountService.deleteAccount(accountId, transferAccountId);
        }

        @Override
        public AccountDto getAccountById(int accountId) {
            try {
                com.PFM.CD.entity.Account acc = accountService.getAccountById(accountId);
                return acc == null ? null : new AccountDto(
                        acc.getAccountId(),
                        acc.getUserId(),
                        acc.getAccountName(),
                        acc.getBalance(),
                        acc.getStatus()
                );
            } catch (ServiceException e) {
                return null;
            }
        }
    }

    // ====== AccountDialog 账户新增/编辑对话框 ======
    private static class AccountDialog extends JDialog {
        private JTextField nameField;
        private JTextField balanceField;
        private AccountDto account;
        private boolean confirmed = false;

        public AccountDialog(AccountDto acc) {
            setTitle(acc == null ? "新建账户" : "编辑账户");
            setModal(true);
            setSize(360, 200);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel form = new JPanel(null);
            form.setBackground(Color.WHITE);

            JLabel nameLabel = new JLabel("账户名称：");
            nameLabel.setBounds(30, 24, 80, 26);
            nameField = new JTextField();
            nameField.setBounds(110, 24, 200, 26);
            form.add(nameLabel); form.add(nameField);

            JLabel balanceLabel = new JLabel("初始余额：");
            balanceLabel.setBounds(30, 62, 80, 26);
            balanceField = new JTextField();
            balanceField.setBounds(110, 62, 200, 26);
            form.add(balanceLabel); form.add(balanceField);

            JButton okBtn = new JButton("确定");
            okBtn.setBounds(70, 110, 90, 28);
            okBtn.setBackground(new Color(0, 123, 255));
            okBtn.setForeground(Color.WHITE);
            JButton cancelBtn = new JButton("取消");
            cancelBtn.setBounds(190, 110, 90, 28);

            form.add(okBtn); form.add(cancelBtn);

            add(form, BorderLayout.CENTER);

            // 填充数据
            if (acc != null) {
                nameField.setText(acc.getAccountName());
                balanceField.setText(acc.getBalance().toString());
                balanceField.setEditable(false); // 编辑时余额不可改
            } else {
                balanceField.setText("0.00");
            }

            okBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                String balanceStr = balanceField.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "请输入账户名称", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                BigDecimal balance = BigDecimal.ZERO;
                try {
                    balance = new BigDecimal(balanceStr);
                    if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        JOptionPane.showMessageDialog(this, "余额不能为负数", "警告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "请输入正确的余额数字", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                confirmed = true;
                if (acc == null)
                    account = new AccountDto(0, 0, name, balance, AccountStatus.ACTIVE);
                else
                    account = new AccountDto(acc.getAccountId(), acc.getUserId(), name, acc.getBalance(), acc.getStatus());
                dispose();
            });
            cancelBtn.addActionListener(e -> dispose());
        }

        public AccountDto getAccount() {
            return confirmed ? account : null;
        }
    }
}