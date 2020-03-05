package com.iSales.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.iSales.database.entry.AgendaUserassignedEntry;

import java.util.List;

@Dao
public interface AgendaUserassignedEntryDao {

    @Query("SELECT * FROM agenda_userassigned WHERE id = :id")
    List<AgendaUserassignedEntry> getAllAgendaUserAssignId(Long id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAgendaUserAssign(AgendaUserassignedEntry agendaUserassignedEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAgendaUserAssign(AgendaUserassignedEntry agendaUserassignedEntry);

    @Delete
    void deleteAgendaUserAssign(AgendaUserassignedEntry agendaUserassignedEntry);

    @Query("DELETE FROM agenda_userassigned")
    void deleteAllAgendaUserAssign();
}
