package javasemysql.coursedesign.gui.dialog;

import javasemysql.coursedesign.model.Backup;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.BackupService;
import javasemysql.coursedesign.service.impl.BackupServiceImpl;
import javasemysql.coursedesign.utils.LogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * 备份管理对话框
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BackupDialog extends JDialog {

    private static final Logger logger = Logger.getLogger(BackupDialog.class.getName());

    private User currentUser;
    private BackupService backupService;

    // UI组件
    private JTextField descriptionField;
    private JCheckBox compressCheckBox;
    private JButton createButton;
    private JButton restoreButton;
    private JButton deleteButton;
    private JButton closeButton;
    private JTable backupTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    /**
     * 构造函数
     *
     * @param parent 父窗口
     * @param user 当前用户
     */
    public BackupDialog(JFrame parent, User user) {
        super(parent, "数据库备份与恢复", true);
        this.currentUser = user;
        this.backupService = new BackupServiceImpl();

        initComponents();
        setupListeners();
        loadBackups();

        setSize(800, 500);
        setLocationRelativeTo(parent);
        setResizable(true);
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 创建顶部面板（表单区域）
        JPanel formPanel = new JPanel(new BorderLayout(10, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder("创建新备份"));

        // 创建输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 描述标签和输入框
        JLabel descriptionLabel = new JLabel("备份描述:");
        descriptionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        inputPanel.add(descriptionLabel, gbc);

        descriptionField = new JTextField(20);
        descriptionField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        inputPanel.add(descriptionField, gbc);

        // 压缩选项
        compressCheckBox = new JCheckBox("压缩备份文件");
        compressCheckBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        compressCheckBox.setSelected(true);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(compressCheckBox, gbc);

        // 创建按钮
        createButton = new JButton("创建备份");
        createButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        createButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/backup_icon.png")));
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        inputPanel.add(createButton, gbc);

        formPanel.add(inputPanel, BorderLayout.CENTER);

        // 创建备份表格
        createBackupTable();

        // 创建表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("备份列表"));

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(backupTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // 创建底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        restoreButton = new JButton("恢复选中备份");
        restoreButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        restoreButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/restore_icon.png")));
        restoreButton.setEnabled(false);

        deleteButton = new JButton("删除选中备份");
        deleteButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        deleteButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/delete_icon.png")));
        deleteButton.setEnabled(false);

        closeButton = new JButton("关闭");
        closeButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        buttonPanel.add(restoreButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        // 创建状态面板
        JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
        statusPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        statusLabel = new JLabel("就绪");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.CENTER);

        // 添加到主面板
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        contentPanel.add(statusPanel, BorderLayout.SOUTH);

        // 设置内容面板
        setContentPane(contentPanel);
    }

    /**
     * 创建备份表格
     */
    private void createBackupTable() {
        // 定义表格列名
        String[] columnNames = {"ID", "备份描述", "文件路径", "创建时间", "文件大小"};

        // 创建表格模型
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置表格不可编辑
            }
        };

        // 创建表格
        backupTable = new JTable(tableModel);
        backupTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        backupTable.setRowHeight(30);
        backupTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 设置表格样式
        backupTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        backupTable.getTableHeader().setReorderingAllowed(false);
        backupTable.getTableHeader().setResizingAllowed(true);

        // 设置列宽
        backupTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        backupTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        backupTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        backupTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        backupTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        // 隐藏ID列
        backupTable.getColumnModel().getColumn(0).setMinWidth(0);
        backupTable.getColumnModel().getColumn(0).setMaxWidth(0);
        backupTable.getColumnModel().getColumn(0).setWidth(0);

        // 设置日期列的单元格渲染器
        DefaultTableCellRenderer dateRenderer = new DefaultTableCellRenderer() {
            private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof java.util.Date) {
                    value = sdf.format((java.util.Date) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        // 设置文件大小列的单元格渲染器
        DefaultTableCellRenderer fileSizeRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Long) {
                    value = formatFileSize((Long) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        fileSizeRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // 应用渲染器
        backupTable.getColumnModel().getColumn(3).setCellRenderer(dateRenderer);
        backupTable.getColumnModel().getColumn(4).setCellRenderer(fileSizeRenderer);
    }

    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 创建备份按钮点击事件
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createBackup();
            }
        });

        // 恢复备份按钮点击事件
        restoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restoreBackup();
            }
        });

        // 删除备份按钮点击事件
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBackup();
            }
        });

        // 关闭按钮点击事件
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

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
                    restoreBackup();
                }
            }
        });
    }

    /**
     * 加载备份列表
     */
    private void loadBackups() {
        if (currentUser == null) {
            return;
        }

        // 清空表格
        tableModel.setRowCount(0);

        // 显示加载状态
        statusLabel.setText("正在加载备份列表...");
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);

        // 使用SwingWorker异步加载数据
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
                    statusLabel.setText("就绪");
                } catch (Exception e) {
                    LogUtils.error("加载备份列表失败", e);
                    statusLabel.setText("加载备份列表失败: " + e.getMessage());
                } finally {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                }
            }
        };

        worker.execute();
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
            row.add(backup.getFilePath());
            row.add(backup.getCreatedAt());

            // 获取文件大小
            File file = new File(backup.getFilePath());
            long fileSize = file.exists() ? file.length() : 0;
            row.add(fileSize);

            tableModel.addRow(row);
        }
    }

    /**
     * 创建备份
     */
    private void createBackup() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 获取输入信息
        String description = descriptionField.getText().trim();
        boolean compress = compressCheckBox.isSelected();

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入备份描述", "错误", JOptionPane.ERROR_MESSAGE);
            descriptionField.requestFocus();
            return;
        }

        // 禁用按钮和显示进度状态
        setControlsEnabled(false);
        statusLabel.setText("正在创建备份...");
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);

        // 使用SwingWorker异步执行备份操作
        SwingWorker<Backup, Void> worker = new SwingWorker<Backup, Void>() {
            @Override
            protected Backup doInBackground() throws Exception {
                return backupService.createBackup(currentUser.getId(), description, compress);
            }

            @Override
            protected void done() {
                try {
                    Backup backup = get();

                    if (backup != null) {
                        // 备份成功
                        JOptionPane.showMessageDialog(
                                BackupDialog.this,
                                "备份创建成功",
                                "成功",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        // 清空输入
                        descriptionField.setText("");

                        // 刷新备份列表
                        loadBackups();

                        statusLabel.setText("备份创建成功");
                    } else {
                        // 备份失败
                        JOptionPane.showMessageDialog(
                                BackupDialog.this,
                                "备份创建失败",
                                "错误",
                                JOptionPane.ERROR_MESSAGE
                        );

                        statusLabel.setText("备份创建失败");
                    }
                } catch (Exception e) {
                    LogUtils.error("创建备份失败", e);
                    JOptionPane.showMessageDialog(
                            BackupDialog.this,
                            "创建备份失败: " + e.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE
                    );

                    statusLabel.setText("创建备份失败: " + e.getMessage());
                } finally {
                    // 恢复按钮状态和隐藏进度条
                    setControlsEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                }
            }
        };

        worker.execute();
    }

    /**
     * 恢复备份
     */
    private void restoreBackup() {
        int selectedRow = backupTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取备份ID
        int backupId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String description = tableModel.getValueAt(selectedRow, 1).toString();

        // 确认恢复
        int result = JOptionPane.showConfirmDialog(
                this,
                "确定要恢复备份 \"" + description + "\" 吗？\n注意：这将覆盖当前所有数据！",
                "确认恢复",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        // 再次确认
        result = JOptionPane.showConfirmDialog(
                this,
                "恢复备份将会丢失自备份创建以来的所有更改，确定要继续吗？",
                "再次确认",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        // 禁用按钮和显示进度状态
        setControlsEnabled(false);
        statusLabel.setText("正在恢复备份...");
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);

        // 使用SwingWorker异步执行恢复操作
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return backupService.restoreBackup(backupId);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();

                    if (success) {
                        // 恢复成功
                        JOptionPane.showMessageDialog(
                                BackupDialog.this,
                                "备份恢复成功，请重新启动应用程序以使更改生效。",
                                "成功",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        statusLabel.setText("备份恢复成功");
                    } else {
                        // 恢复失败
                        JOptionPane.showMessageDialog(
                                BackupDialog.this,
                                "备份恢复失败",
                                "错误",
                                JOptionPane.ERROR_MESSAGE
                        );

                        statusLabel.setText("备份恢复失败");
                    }
                } catch (Exception e) {
                    LogUtils.error("恢复备份失败", e);
                    JOptionPane.showMessageDialog(
                            BackupDialog.this,
                            "恢复备份失败: " + e.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE
                    );

                    statusLabel.setText("恢复备份失败: " + e.getMessage());
                } finally {
                    // 恢复按钮状态和隐藏进度条
                    setControlsEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                }
            }
        };

        worker.execute();
    }

    /**
     * 删除备份
     */
    private void deleteBackup() {
        int selectedRow = backupTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // 获取备份ID
        int backupId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String description = tableModel.getValueAt(selectedRow, 1).toString();

        // 确认删除
        int result = JOptionPane.showConfirmDialog(
                this,
                "确定要删除备份 \"" + description + "\" 吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        // 禁用按钮和显示进度状态
        setControlsEnabled(false);
        statusLabel.setText("正在删除备份...");
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);

        // 使用SwingWorker异步执行删除操作
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return backupService.deleteBackup(backupId);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();

                    if (success) {
                        // 删除成功
                        statusLabel.setText("备份删除成功");

                        // 刷新备份列表
                        loadBackups();
                    } else {
                        // 删除失败
                        JOptionPane.showMessageDialog(
                                BackupDialog.this,
                                "备份删除失败",
                                "错误",
                                JOptionPane.ERROR_MESSAGE
                        );

                        statusLabel.setText("备份删除失败");
                    }
                } catch (Exception e) {
                    LogUtils.error("删除备份失败", e);
                    JOptionPane.showMessageDialog(
                            BackupDialog.this,
                            "删除备份失败: " + e.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE
                    );

                    statusLabel.setText("删除备份失败: " + e.getMessage());
                } finally {
                    // 恢复按钮状态和隐藏进度条
                    setControlsEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                }
            }
        };

        worker.execute();
    }

    /**
     * 设置控件启用状态
     *
     * @param enabled 是否启用
     */
    private void setControlsEnabled(boolean enabled) {
        descriptionField.setEnabled(enabled);
        compressCheckBox.setEnabled(enabled);
        createButton.setEnabled(enabled);

        boolean hasSelection = backupTable.getSelectedRow() != -1;
        restoreButton.setEnabled(enabled && hasSelection);
        deleteButton.setEnabled(enabled && hasSelection);

        closeButton.setEnabled(enabled);
        backupTable.setEnabled(enabled);
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小（字节）
     * @return 格式化后的文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    public boolean isBackupCreated() {
        // 检查是否有备份被创建
        return tableModel.getRowCount() > 0;
    }
}