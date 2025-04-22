package com.cardoso.shareyourtime.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarms")
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int hour;
    private int minute;
    private boolean isEnabled;
    private String label;
    private boolean vibrate;
    private String soundUri;

    public Alarm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.isEnabled = true;
        this.vibrate = true;
    }

    // Getters
    public int getId() { return id; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public boolean isEnabled() { return isEnabled; }

    public String getLabel() { return label; }
    public boolean isVibrate() { return vibrate; }
    public String getSoundUri() { return soundUri; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setHour(int hour) { this.hour = hour; }
    public void setMinute(int minute) { this.minute = minute; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public void setLabel(String label) { this.label = label; }
    public void setVibrate(boolean vibrate) { this.vibrate = vibrate; }
    public void setSoundUri(String soundUri) { this.soundUri = soundUri; }
} 