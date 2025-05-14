package com.cardoso.shareyourtime.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timezones")
public class TimeZoneEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private int offset;
    private boolean isSelected;

    public TimeZoneEntity(String name, int offset) {
        this.name = name;
        this.offset = offset;
        this.isSelected = true;
    }

    // Getters
    public long getId() { return id; }
    public String getName() { return name; }
    public int getOffset() { return offset; }
    public boolean isSelected() { return isSelected; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setOffset(int offset) { this.offset = offset; }
    public void setSelected(boolean selected) { isSelected = selected; }
} 