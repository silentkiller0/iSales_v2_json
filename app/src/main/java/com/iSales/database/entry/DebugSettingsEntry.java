package com.iSales.database.entry;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "debug_settings")
public class DebugSettingsEntry {

    @PrimaryKey(autoGenerate = false)
    private int id;
    private int checkDebug;

    public DebugSettingsEntry() {
    }

    public DebugSettingsEntry(int id, int checkDebug) {
        this.id = id;
        this.checkDebug = checkDebug;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCheckDebug() {
        return checkDebug;
    }

    public void setCheckDebug(int checkDebug) {
        this.checkDebug = checkDebug;
    }
}
