package com.iSales.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.iSales.database.entry.DebugSettingsEntry;

import java.util.List;

@Dao
public interface DebugSettingsDao {

    @Query("SELECT * FROM debug_settings")
    List<DebugSettingsEntry> getAllDebugSettings();

    @Query("DELETE FROM debug_settings")
    void deleteDebugSettings();

    @Insert
    void insertDebugSettings(DebugSettingsEntry debugSettingsEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDebugSettings(DebugSettingsEntry debugSettingsEntry);
}
