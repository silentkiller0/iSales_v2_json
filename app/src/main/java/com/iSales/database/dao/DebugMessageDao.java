package com.iSales.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.iSales.database.entry.DebugItemEntry;

import java.util.List;

@Dao
public interface DebugMessageDao {

    //@Query("SELECT * FROM debug_message order by datetimeLong ASC, ClassName ASC")
    @Query("SELECT * FROM debug_message ORDER BY datetimeLong ASC")
    List<DebugItemEntry> getAllDebugMessages();

    @Query("DELETE FROM debug_message WHERE :millisecondsInOneDate > datetimeLong")
    void
    deleteAllDebugMessagesOver24Hrs(long millisecondsInOneDate);

    @Query("DELETE FROM debug_message")
    void deleteAllDebugMessages();

    @Insert
    void insertDebugMessage(DebugItemEntry debugItemEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDebugMessage(DebugItemEntry debugItemEntry);

    @Delete
    void deleteDebugMessage(DebugItemEntry debugItemEntry);
}
