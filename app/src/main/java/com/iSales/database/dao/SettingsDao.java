package com.iSales.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.iSales.database.entry.SettingsEntry;

import java.util.List;

@Dao
public interface SettingsDao {

    @Query("SELECT * FROM settings")
    List<SettingsEntry> getAllSettings();

    @Query("DELETE FROM settings")
    void deleteSettings();

    @Insert
    void insertSettings(SettingsEntry settingsEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSettings(SettingsEntry settingsEntry);
}
