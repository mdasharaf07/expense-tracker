package com.tracker.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import com.tracker.model.Expense;
import com.tracker.model.Category;
import com.tracker.dao.ExpenseTrackerAppDAO;
import com.tracker.dao.CategoryAppDAO;

public class ExpenseAppGUI extends JFrame {
    private JButton expenseTrackerButton;
    private JButton categoryButton;
    private JButton exitButton;
    private JPanel panel;

    public ExpenseAppGUI() {

        initializeComponents();
        setupComponents();
        setupEventListeners();
    }

    private void initializeComponents() {
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void setupComponents() {
        setLayout(new BorderLayout());

        // Panel for buttons (horizontal arrangement)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 40));
        expenseTrackerButton = new JButton("Expense List");
        categoryButton = new JButton("Category List");
        exitButton = new JButton("Exit");

        Dimension buttonSize = new Dimension(200, 200);
        expenseTrackerButton.setPreferredSize(buttonSize);
        categoryButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);

        buttonPanel.add(expenseTrackerButton);
        buttonPanel.add(categoryButton);
        buttonPanel.add(exitButton);

        // Wrapper panel with GridBagLayout for vertical & horizontal centering
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(buttonPanel, new GridBagConstraints());

        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        expenseTrackerButton.addActionListener(e -> expenseTrigger());
        categoryButton.addActionListener(e -> categoryTrigger());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void expenseTrigger() {
        new ExpenseTrackerAppGUI().setVisible(true);
    }

    private void categoryTrigger() {
        new CategoryAppGUI().setVisible(true);
    }
}

class ExpenseTrackerAppGUI extends JFrame {
    private CategoryAppDAO categoryAppDAO;
    private ExpenseTrackerAppDAO expenseTrackerAppDAO;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckBox;
    private JButton addButton, deleteButton, editButton, refreshButton, closeButton;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> categoryInputComboBox;
    private JComboBox<String> filterComboBox;

    public ExpenseTrackerAppGUI() {
        categoryAppDAO = new CategoryAppDAO();
        expenseTrackerAppDAO = new ExpenseTrackerAppDAO();
        expenseTable = new JTable();
        tableModel = new DefaultTableModel();
        initializeComponents();
        setupComponents();
        setupEventListeners();
        loadExpenses();
    }

    private void initializeComponents() {
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        String[] columnNames = { "ID", "Amount", "Description", "Category", "Created At", "Updated At" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Inputs
        titleField = new JTextField(25);

        descriptionArea = new JTextArea(4, 25);
        descriptionArea.setEditable(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        String[] categoryOptions = loadCategoryOptions();
        categoryInputComboBox = new JComboBox<>(categoryOptions);

        // Buttons
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        editButton = new JButton("Update");
        refreshButton = new JButton("Refresh");
        closeButton = new JButton("Exit");

        // Filter dropdown (for filtering, separate from input dropdown)
        filterComboBox = new JComboBox<>(categoryOptions);
    }

    private void setupComponents() {
        setLayout(new BorderLayout());

        // Input panel for title, description, category dropdown
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JScrollPane(descriptionArea), gbc);

        // Category dropdown (instead of completed checkbox)
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(categoryInputComboBox, gbc);

        // Button panel for Add, Update, Delete, Refresh, Exit
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        // Filter panel for filter label and combo box
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterComboBox);

        // North panel to combine filter, input, and button panels
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(filterPanel, BorderLayout.NORTH);
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(expenseTable), BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Select an expense to edit or delete:"));
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        addButton.addActionListener(e -> addExpense());
        editButton.addActionListener(e -> updateExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        refreshButton.addActionListener(e -> refreshExpense());
        closeButton.addActionListener(e -> dispose());
        filterComboBox.addActionListener(e -> filterExpense());
        expenseTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedExpense();
            }
        });
    }

    private String[] loadCategoryOptions() {
        try {
            List<Category> categories = categoryAppDAO.getAllCategories();
            return categories.stream().map(Category::getCategory_name).toArray(String[]::new);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return new String[0];
        }
    }

    private void loadExpenses() {
        try {
            List<Expense> expenses = expenseTrackerAppDAO.getAllExpenses();
            updateTable(expenses);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addExpense() {
        try {
            String description = descriptionArea.getText().trim();
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a description", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String amountText = titleField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int amount = Integer.parseInt(amountText);
            int categoryId = getCategoryIdFromName((String) categoryInputComboBox.getSelectedItem());

            Expense expense = new Expense(amount, description, categoryId);
            expenseTrackerAppDAO.addExpense(expense);
            loadExpenses();
            clearFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding expense: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateExpense() {
        int row = expenseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to update", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String description = descriptionArea.getText().trim();
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a description", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String amountText = titleField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int amount = Integer.parseInt(amountText);
            int categoryId = getCategoryIdFromName((String) categoryInputComboBox.getSelectedItem());

            int expenseId = (Integer) tableModel.getValueAt(row, 0);
            Expense expense = new Expense(expenseId, amount, description, categoryId);
            expenseTrackerAppDAO.updateExpense(expense);
            loadExpenses();
            clearFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating expense: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteExpense() {
        int row = expenseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int expenseId = (Integer) tableModel.getValueAt(row, 0);
            Expense expense = new Expense();
            expense.setId(expenseId);
            expenseTrackerAppDAO.deleteExpense(expense);
            loadExpenses();
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting expense: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshExpense() {
        clearFields();
        loadExpenses();
        filterComboBox.setSelectedIndex(0);
        JOptionPane.showMessageDialog(this, "Fields cleared and expenses refreshed.", "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadSelectedExpense() {
        int row = expenseTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        try {
            String amount = tableModel.getValueAt(row, 1).toString();
            String description = tableModel.getValueAt(row, 2).toString();
            String category = tableModel.getValueAt(row, 3).toString();

            titleField.setText(amount);
            descriptionArea.setText(description);
            categoryInputComboBox.setSelectedItem(category);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterExpense() {
        String filter = (String) filterComboBox.getSelectedItem();
        try {
            List<Expense> expenses = expenseTrackerAppDAO.getExpensesByCategory(filter);
            updateTable(expenses);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering expenses: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateTable(List<Expense> expenses) {
        tableModel.setRowCount(0);
        for (Expense expense : expenses) {
            Object[] row = {
                    expense.getId(),
                    expense.getAmount(),
                    expense.getDescription(),
                    getCategoryNameFromId(expense.getCategory_id()),
                    expense.getCreated_at(),
                    expense.getUpdated_at()
            };
            tableModel.addRow(row);
        }
    }

    private String getCategoryNameFromId(int categoryId) {
        try {
            List<Category> categories = categoryAppDAO.getAllCategories();
            for (Category category : categories) {
                if (category.getId() == categoryId) {
                    return category.getCategory_name();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private int getCategoryIdFromName(String categoryName) {
        try {
            List<Category> categories = categoryAppDAO.getAllCategories();
            for (Category category : categories) {
                if (category.getCategory_name().equals(categoryName)) {
                    return category.getId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Default category ID
    }

    private void clearFields() {
        titleField.setText("");
        descriptionArea.setText("");
        categoryInputComboBox.setSelectedIndex(0);
        
    }

}

class CategoryAppGUI extends JFrame {
    private CategoryAppDAO categoryAppDAO;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField categoryNameField;
    private JButton addButton, deleteButton, editButton, refreshButton, closeButton;

    public CategoryAppGUI() {
        categoryAppDAO = new CategoryAppDAO();
        categoryTable = new JTable();
        tableModel = new DefaultTableModel();
        initializeComponents();
        setupComponents();
        setupEventListeners();
        loadCategories();
    }

    private void initializeComponents() {
        setTitle("Category Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 1000);
        setLocationRelativeTo(null);

        // Initialize table model and table
        String[] columnNames = { "ID", "Category Name" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Initialize input field
        categoryNameField = new JTextField(25);

        // Initialize buttons
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        editButton = new JButton("Update");
        refreshButton = new JButton("Refresh");
        closeButton = new JButton("Exit");
    }

    private void setupComponents() {
        setLayout(new BorderLayout());

        // Input panel for category name
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Category name input
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Category Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(categoryNameField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        // North panel to combine input and button panels
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(categoryTable), BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Select a category to edit or delete:"));
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        addButton.addActionListener(e -> addCategory());
        deleteButton.addActionListener(e -> deleteCategory());
        editButton.addActionListener(e -> updateCategory());
        refreshButton.addActionListener(e -> refreshCategory());
        closeButton.addActionListener(e -> dispose());
        categoryTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedCategory();
            }
        });
    }

    private void addCategory() {
        String categoryName = categoryNameField.getText().trim();
        if (categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a category name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Category category = new Category(categoryName);
            categoryAppDAO.addCategory(category);
            loadCategories();
            categoryNameField.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding category: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int categoryId = (Integer) tableModel.getValueAt(row, 0);
            categoryAppDAO.deleteCategory(categoryId);
            loadCategories();
            categoryNameField.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting category: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to update", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String categoryName = categoryNameField.getText().trim();
        if (categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a category name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int categoryId = (Integer) tableModel.getValueAt(row, 0);
            Category category = new Category(categoryId, categoryName);
            categoryAppDAO.updateCategory(category);
            loadCategories();
            categoryNameField.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating category: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshCategory() {

        loadCategories();
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryAppDAO.getAllCategories();
            updateTable(categories);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateTable(List<Category> categories) {
        tableModel.setRowCount(0);
        for (Category category : categories) {
            Object[] row = {
                    category.getId(),
                    category.getCategory_name()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        try {
            String categoryName = tableModel.getValueAt(row, 1).toString();
            categoryNameField.setText(categoryName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

}
