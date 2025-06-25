package com.PFM.CD.gui;

import com.PFM.CD.entity.enums.CategoryType;
import com.PFM.CD.entity.Category;
import com.PFM.CD.service.dto.CategoryDto;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.CategoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类管理面板（MVC模式）
 * 支持分类的新增、编辑、删除、查询
 * 显示CategoryDto，操作Category实体，通过CategoryService
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

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 顶部工具栏
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        topPanel.setBackground(Color.WHITE);

        searchField = new JTextField(14);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        typeCombo = new JComboBox<>(CategoryType.values());
        typeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        typeCombo.insertItemAt(null, 0);
        typeCombo.setSelectedIndex(0);

        JButton searchButton = new JButton("查询");
        searchButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchButton.addActionListener(e -> doSearch());

        refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> loadCategories(null, null));

        addButton = new JButton("新建分类");
        addButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        addButton.setBackground(new Color(0, 123, 255));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addCategory());

        editButton = new JButton("编辑分类");
        editButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        editButton.addActionListener(e -> editCategory());

        deleteButton = new JButton("删除分类");
        deleteButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        deleteButton.setForeground(Color.RED.darker());
        deleteButton.addActionListener(e -> deleteCategory());

        topPanel.add(new JLabel("名称:"));
        topPanel.add(searchField);
        topPanel.add(new JLabel("类型:"));
        topPanel.add(typeCombo);
        topPanel.add(searchButton);
        topPanel.add(refreshButton);
        topPanel.add(Box.createHorizontalStrut(28));
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);

        // 表格
        String[] columns = {"ID", "名称", "类型", "交易数"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
        for (CategoryDto dto : list) {
            tableModel.addRow(new Object[]{
                    dto.getCategoryId(),
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
        int categoryId = (int) tableModel.getValueAt(row, 0);
        return controller.getCategoryById(categoryId);
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
            nameLabel.setBounds(30, 24, 80, 26);
            nameField = new JTextField();
            nameField.setBounds(110, 24, 180, 26);
            form.add(nameLabel); form.add(nameField);

            JLabel typeLabel = new JLabel("分类类型：");
            typeLabel.setBounds(30, 62, 80, 26);
            typeCombo = new JComboBox<>(CategoryType.values());
            typeCombo.setBounds(110, 62, 180, 26);
            form.add(typeLabel); form.add(typeCombo);

            JButton okBtn = new JButton("确定");
            okBtn.setBounds(60, 110, 90, 28);
            okBtn.setBackground(new Color(0, 123, 255));
            okBtn.setForeground(Color.WHITE);
            JButton cancelBtn = new JButton("取消");
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
}