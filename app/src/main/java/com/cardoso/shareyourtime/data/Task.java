package com.cardoso.shareyourtime.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private Date dateTime;
    private boolean completed;

    public Task(String title, Date dateTime) {
        this.title = title;
        this.dateTime = dateTime;
        this.completed = false;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Date getDateTime() { return dateTime; }
    public boolean isCompleted() { return completed; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }
    public void setCompleted(boolean completed) { this.completed = completed; }
} 