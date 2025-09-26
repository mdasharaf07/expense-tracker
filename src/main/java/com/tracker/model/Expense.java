package com.tracker.model;

import java.sql.Timestamp;

public class Expense {
    private int id;
    private int amount;
    private String description;
    private Timestamp created_at;
    private Timestamp updated_at;
    private int category_id;

    public Expense() {
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.updated_at = new Timestamp(System.currentTimeMillis());
    }

    public Expense(int amount, String description, int category_id) {
        this.amount = amount;
        this.description = description;
        this.category_id = category_id;
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.updated_at = new Timestamp(System.currentTimeMillis());
    }

    public Expense(int id, int amount, String description, int category_id) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category_id = category_id;
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.updated_at = new Timestamp(System.currentTimeMillis());
    }

    public Expense(int id, int amount, String description, int category_id, Timestamp created_at, Timestamp updated_at) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category_id = category_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    // Getters and Setters
    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }
    
    public int getAmount() { 
        return amount; 
    }
    public void setAmount(int amount) { 
        this.amount = amount;
    }
    
    public String getDescription() { 
        return description; 
    }
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public Timestamp getCreated_at() { 
        return created_at; 
    }
    public void setCreated_at(Timestamp created_at) { 
        this.created_at = created_at; 
    }
    
    public Timestamp getUpdated_at() { 
        return updated_at; 
    }
    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }
    
    public int getCategory_id() { 
        return category_id; 
    }
    public void setCategory_id(int category_id) {
        this.category_id = category_id; 
    }
}