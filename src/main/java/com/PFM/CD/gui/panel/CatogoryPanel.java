package com.PFM.CD.gui.panel;

import com.PFM.CD.entity.enums.CategoryType;
import com.PFM.CD.entity.Category;
import com.PFM.CD.service.dto.CategoryDto;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.CategoryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类管理面板（MVC模式）
 * 支持分类的新增、编辑、删除、查询
 * 显示CategoryDto，操作Category实体，通过CategoryService
 * 界面风格对齐 TransactionsPanel，ID列为“序号”
 */
public class CatogoryPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<CategoryType> typeCombo;
    private JButton addButton, editButton, deleteButton, refreshButton;

    private CategoryController controller;

    public CatogoryPanel(CategoryService categoryService) {
        this.controller = new CategoryControllerImpl(categoryService);

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(247, 249, 254));
        setBorder(new EmptyBorder(12, 18, 12, 18));

        // 顶部工具栏分为两行
        JPanel topPanelOuter = new JPanel();
        topPanelOuter.setLayout(new BoxLayout(topPanelOuter, BoxLayout.Y_AXIS));
        topPanelOuter.setOpaque(false);

        // 第一行：检索输入和按钮
        JPanel topPanelRow1 = new JPanel(new GridBagLayout());
        topPanelRow1.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 12, 4, 0);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        int gridx = 0;

        c.gridx = gridx++;
        JLabel nameLbl = new JLabel("名称:");
        nameLbl.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        topPanelRow1.add(nameLbl, c);

        c.gridx = gridx++;
        searchField = new JTextField(14);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        searchField.setPreferredSize(new Dimension(140, 36));
        topPanelRow1.add(searchField, c);

        c.gridx = gridx++;
        JLabel typeLbl = new JLabel("类型:");
        typeLbl.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        topPanelRow1.add(typeLbl, c);

        c.gridx = gridx++;
        typeCombo = new JComboBox<>(CategoryType.values());
        typeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        typeCombo.setPreferredSize(new Dimension(120, 36));
        typeCombo.insertItemAt(null, 0);
        typeCombo.setSelectedIndex(0);
        topPanelRow1.add(typeCombo, c);

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
        refreshButton.addActionListener(e -> loadCategories(null, null));
        topPanelRow1.add(refreshButton, c);

        // 第二行：功能按钮
        JPanel topPanelRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        topPanelRow2.setOpaque(false);

        addButton = createFlatButton("新建分类", new Color(0, 123, 255), new Color(30, 150, 255));
        addButton.setPreferredSize(new Dimension(150, 40));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addCategory());

        editButton = createFlatButton("编辑分类", new Color(70, 180, 100), new Color(110, 220, 140));
        editButton.setPreferredSize(new Dimension(150, 40));
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(e -> editCategory());

        deleteButton = createFlatButton("删除分类", new Color(230, 70, 70), new Color(240, 100, 100));
        deleteButton.setPreferredSize(new Dimension(150, 40));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteCategory());

        topPanelRow2.add(addButton);
        topPanelRow2.add(Box.createHorizontalStrut(12));
        topPanelRow2.add(editButton);
        topPanelRow2.add(Box.createHorizontalStrut(12));
        topPanelRow2.add(deleteButton);

        topPanelOuter.add(topPanelRow1);
        topPanelOuter.add(Box.createVerticalStrut(8));
        topPanelOuter.add(topPanelRow2);

        add(topPanelOuter, BorderLayout.NORTH);

        // 表格
        String[] columns = {"序号", "名称", "类型", "交易数"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 加载初始数据
        loadCategories(null, null);

        // 双击编辑
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    editCategory();
                }
            }
        });
    }

    private void loadCategories(String keyword, CategoryType type) {
        tableModel.setRowCount(0);
        List<CategoryDto> list = controller.queryCategories(keyword, type);
        int rowNum = 1;
        for (CategoryDto dto : list) {
            tableModel.addRow(new Object[]{
                    rowNum++,
                    dto.getCategoryName(),
                    dto.getCategoryType().getDisplayName(),
                    dto.getTransactionCount()
            });
        }
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        CategoryType type = (CategoryType) typeCombo.getSelectedItem();
        loadCategories(keyword, type);
    }

    private CategoryDto getSelectedCategory() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        String name = (String) tableModel.getValueAt(row, 1);
        String typeDisplay = (String) tableModel.getValueAt(row, 2);
        List<CategoryDto> all = controller.queryCategories(null, null);
        for (CategoryDto dto : all) {
            if (dto.getCategoryName().equals(name) && dto.getCategoryType().getDisplayName().equals(typeDisplay)) {
                return dto;
            }
        }
        return null;
    }

    private void addCategory() {
        CategoryDialog dialog = new CategoryDialog(null);
        dialog.setVisible(true);
        CategoryDto dto = dialog.getCategory();
        if (dto != null) {
            try {
                controller.addCategory(dto.getCategoryName(), dto.getCategoryType());
                loadCategories(null, null);
            } catch (ServiceException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void editCategory() {
        CategoryDto dto = getSelectedCategory();
        if (dto == null) {
            showWarn("请选择要编辑的分类。");
            return;
        }
        CategoryDialog dialog = new CategoryDialog(dto);
        dialog.setVisible(true);
        CategoryDto updated = dialog.getCategory();
        if (updated != null) {
            try {
                controller.updateCategory(updated);
                loadCategories(null, null);
            } catch (ServiceException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void deleteCategory() {
        CategoryDto dto = getSelectedCategory();
        if (dto == null) {
            showWarn("请选择要删除的分类。");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this,
                "确认要删除分类【" + dto.getCategoryName() + "】吗？", "删除确认",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            try {
                controller.deleteCategory(dto.getCategoryId());
                loadCategories(null, null);
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

    // ===== Controller层（Service桥接） =====

    public interface CategoryController {
        List<CategoryDto> queryCategories(String keyword, CategoryType type);
        void addCategory(String name, CategoryType type) throws ServiceException;
        void updateCategory(CategoryDto dto) throws ServiceException;
        void deleteCategory(int categoryId) throws ServiceException;
        CategoryDto getCategoryById(int categoryId);
    }

    public static class CategoryControllerImpl implements CategoryController {
        private final CategoryService categoryService;

        public CategoryControllerImpl(CategoryService categoryService) {
            this.categoryService = categoryService;
        }

        @Override
        public List<CategoryDto> queryCategories(String keyword, CategoryType type) {
            try {
                List<Category> list;
                if (type != null) {
                    list = categoryService.getCategoriesByType(type);
                } else {
                    list = categoryService.getAllCategories();
                }
                List<CategoryDto> dtoList = list.stream()
                        .map(c -> new CategoryDto(
                                c.getCategoryId(),
                                c.getCategoryName(),
                                c.getCategoryType()
                        ))
                        .collect(Collectors.toList());
                if (keyword == null || keyword.isEmpty()) return dtoList;
                return dtoList.stream().filter(a ->
                        (a.getCategoryName() != null && a.getCategoryName().contains(keyword))
                ).collect(Collectors.toList());
            } catch (ServiceException e) {
                JOptionPane.showMessageDialog(null, "加载分类失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return java.util.Collections.emptyList();
            }
        }

        @Override
        public void addCategory(String name, CategoryType type) throws ServiceException {
            categoryService.createCategory(name, type);
        }

        @Override
        public void updateCategory(CategoryDto dto) throws ServiceException {
            Category entity = categoryService.getCategoryById(dto.getCategoryId());
            entity.setCategoryName(dto.getCategoryName());
            entity.setCategoryType(dto.getCategoryType());
            categoryService.updateCategory(entity);
        }

        @Override
        public void deleteCategory(int categoryId) throws ServiceException {
            categoryService.deleteCategory(categoryId);
        }

        @Override
        public CategoryDto getCategoryById(int categoryId) {
            try {
                Category c = categoryService.getCategoryById(categoryId);
                return c == null ? null : new CategoryDto(
                        c.getCategoryId(),
                        c.getCategoryName(),
                        c.getCategoryType()
                );
            } catch (ServiceException e) {
                return null;
            }
        }
    }

    // ====== CategoryDialog 分类新增/编辑对话框 ======
    private static class CategoryDialog extends JDialog {
        private JTextField nameField;
        private JComboBox<CategoryType> typeCombo;
        private CategoryDto category;
        private boolean confirmed = false;

        public CategoryDialog(CategoryDto dto) {
            setTitle(dto == null ? "新建分类" : "编辑分类");
            setModal(true);
            setSize(350, 180);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel form = new JPanel(null);
            form.setBackground(Color.WHITE);

            JLabel nameLabel = new JLabel("分类名称：");
            nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            nameLabel.setBounds(30, 24, 80, 26);
            nameField = new JTextField();
            nameField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            nameField.setBounds(110, 24, 180, 26);
            form.add(nameLabel); form.add(nameField);

            JLabel typeLabel = new JLabel("分类类型：");
            typeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            typeLabel.setBounds(30, 62, 80, 26);
            typeCombo = new JComboBox<>(CategoryType.values());
            typeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            typeCombo.setBounds(110, 62, 180, 26);
            form.add(typeLabel); form.add(typeCombo);

            JButton okBtn = new JButton("确定");
            okBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
            okBtn.setBounds(60, 110, 90, 28);
            okBtn.setBackground(new Color(0, 123, 255));
            okBtn.setForeground(Color.WHITE);
            JButton cancelBtn = new JButton("取消");
            cancelBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
            cancelBtn.setBounds(170, 110, 90, 28);

            form.add(okBtn); form.add(cancelBtn);

            add(form, BorderLayout.CENTER);

            // 填充数据
            if (dto != null) {
                nameField.setText(dto.getCategoryName());
                typeCombo.setSelectedItem(dto.getCategoryType());
            }

            okBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                CategoryType type = (CategoryType) typeCombo.getSelectedItem();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "请输入分类名称", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (type == null) {
                    JOptionPane.showMessageDialog(this, "请选择分类类型", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                confirmed = true;
                if (dto == null)
                    category = new CategoryDto(0, name, type, 0);
                else
                    category = new CategoryDto(dto.getCategoryId(), name, type, dto.getTransactionCount());
                dispose();
            });
            cancelBtn.addActionListener(e -> dispose());
        }

        public CategoryDto getCategory() {
            return confirmed ? category : null;
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