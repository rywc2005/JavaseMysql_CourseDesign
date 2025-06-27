package com.PFM.CD.gui.panel;

import com.PFM.CD.entity.enums.AccountStatus;
import com.PFM.CD.service.dto.AccountDto;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.AccountService;

import com.PFM.CD.gui.controller.AccountController;
import com.PFM.CD.gui.controller.AccountControllerImpl;
import com.PFM.CD.gui.dialog.AccountDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
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
        // 桥接Service,控制器调用服务层方法完成业务操作，转发请求给服务层处理业务细节

        setLayout(new BorderLayout(0, 0));// 无边框布局·
        setBackground(new Color(247, 249, 254));
        setBorder(new EmptyBorder(12, 18, 12, 18));

        // 顶部操作面板
        JPanel topPanelOuter = new JPanel();
        topPanelOuter.setLayout(new BoxLayout(topPanelOuter, BoxLayout.Y_AXIS));
        topPanelOuter.setOpaque(false);

        // 第一行：检索输入和按钮
        JPanel topPanelRow1 = new JPanel(new GridBagLayout());
        topPanelRow1.setOpaque(false);// 透明背景
        GridBagConstraints c = new GridBagConstraints();//
        c.insets = new Insets(4, 12, 4, 0);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        int gridx = 0;

        c.gridx = gridx++;
        JLabel searchLbl = new JLabel("账户查询:");
        searchLbl.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        topPanelRow1.add(searchLbl, c);

        c.gridx = gridx++;
        searchField = new JTextField(15);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        searchField.setPreferredSize(new Dimension(170, 36));
        topPanelRow1.add(searchField, c);

        c.gridx = gridx++;
        JButton searchButton = createFlatButton("查询", new Color(51, 102, 255), new Color(80, 130, 255));
        searchButton.setPreferredSize(new Dimension(120, 40));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> doSearch());
        topPanelRow1.add(searchButton, c);

        c.gridx = gridx++;
        refreshButton = createFlatButton("刷新", new Color(80, 170, 230), new Color(105, 200, 255));
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadAccounts(null));
        topPanelRow1.add(refreshButton, c);

        // 第二行：功能按钮（新建、编辑、删除）
        JPanel topPanelRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        topPanelRow2.setOpaque(false);

        addButton = createFlatButton("新建账户", new Color(0, 123, 255), new Color(30, 150, 255));
        addButton.setPreferredSize(new Dimension(150, 40));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addAccount());

        editButton = createFlatButton("编辑账户", new Color(70, 180, 100), new Color(110, 220, 140));
        editButton.setPreferredSize(new Dimension(150, 40));
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(e -> editAccount());

        deleteButton = createFlatButton("删除账户", new Color(230, 70, 70), new Color(240, 100, 100));
        deleteButton.setPreferredSize(new Dimension(150, 40));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteAccount());

        topPanelRow2.add(addButton);
        topPanelRow2.add(Box.createHorizontalStrut(12));// 增加间距
        topPanelRow2.add(editButton);
        topPanelRow2.add(Box.createHorizontalStrut(12));
        topPanelRow2.add(deleteButton);

        topPanelOuter.add(topPanelRow1);//
        topPanelOuter.add(Box.createVerticalStrut(8));
        topPanelOuter.add(topPanelRow2);

        add(topPanelOuter, BorderLayout.NORTH);// 顶部面板

        // 表格
        String[] columns = {"序号", "账户名称", "余额", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);// 表格美化

        JScrollPane scrollPane = new JScrollPane(table);// 表格滚动
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
        int rowNum = 1;
        for (AccountDto acc : displayList) {
            tableModel.addRow(new Object[]{
                    rowNum++,
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
        // 因为序号列是第0列，实际ID需要重新查找
        String name = (String) tableModel.getValueAt(row, 1);
        BigDecimal balance = (BigDecimal) tableModel.getValueAt(row, 2);
        String statusStr = (String) tableModel.getValueAt(row, 3);
        // 下面通过controller查找真实ID
        List<AccountDto> allList = controller.queryAccounts(userId, null);
        for (AccountDto acc : allList) {
            if (acc.getAccountName().equals(name) &&
                    acc.getBalance().compareTo(balance) == 0 &&
                    getStatusDisplay(acc.getStatus()).equals(statusStr)) {
                return acc;
            }
        }
        return null;
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

    /**
     * FlatLaf 风格扁平按钮
     */
    private JButton createFlatButton(String text, Color color, Color hover) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 20));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 34, 10, 34));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    /**
     * 表格美化
     */
    private void styleTable(JTable table) {
        table.setFont(new Font("微软雅黑", Font.PLAIN, 22));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 20));
        table.getTableHeader().setBackground(new Color(238, 242, 255));
        table.getTableHeader().setForeground(new Color(55, 80, 150));
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setGridColor(new Color(220, 222, 230));
    }
}