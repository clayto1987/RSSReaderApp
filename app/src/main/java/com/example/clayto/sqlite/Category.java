package com.example.clayto.sqlite;

/**
 * Created by Clayto on 14-11-19.
 */
public class Category {

    long id;
    String createdAt;
    String name;


    // constructors
    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(long id, String name) {
        this.id = id;
        this.name = name;
    }

    // setters
    public void setId(long id) {
        this.id = id;
    }

    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }


    // getters
    public long getId() {
        return this.id;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public String getName() {
        return this.name;
    }

}
