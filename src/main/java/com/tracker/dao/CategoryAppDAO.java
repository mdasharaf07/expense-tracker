package com.tracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.tracker.model.Category;
import com.tracker.util.DatabaseConnection;

public class CategoryAppDAO {
    
    // SQL Statements
    private static final String GET_ALL_CATEGORIES = "SELECT * FROM category";
    private static final String ADD_CATEGORY = "INSERT INTO category (category_name) VALUES (?)";
    private static final String UPDATE_CATEGORY = "UPDATE category SET category_name = ? WHERE id = ?";
    private static final String DELETE_CATEGORY = "DELETE FROM category WHERE id = ?";
    
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getDBConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ALL_CATEGORIES);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt("id"));
                category.setCategory_name(resultSet.getString("category_name"));
                categories.add(category);
            }
        }
        return categories;
    }
    
    public void addCategory(Category category) throws SQLException {
        try (Connection connection = DatabaseConnection.getDBConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_CATEGORY)) {
            statement.setString(1, category.getCategory_name());
            statement.executeUpdate();
        }
    }   
    
    public void updateCategory(Category category) throws SQLException {
        try (Connection connection = DatabaseConnection.getDBConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_CATEGORY)) {
            statement.setString(1, category.getCategory_name());
            statement.setInt(2, category.getId());
            statement.executeUpdate();
        }
    }
    
    public void deleteCategory(int categoryId) throws SQLException {
        try (Connection connection = DatabaseConnection.getDBConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY)) {
            statement.setInt(1, categoryId);
            statement.executeUpdate();
        }
    }
}