package com.tracker.model;

public class Category {
    private int id;
    private String category_name;

    public Category(){
        // Default constructor
    }

    public Category(int id, String category_name){
        this.id = id;
        this.category_name = category_name;
    }

    public Category(String category_name){
        this.category_name = category_name;
    }

    // Getters and Setters
    public int getId() 
    { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getCategory_name() { 
        return category_name; 
    }
    public void setCategory_name(String category_name) { 
        this.category_name = category_name; 
    }
}
