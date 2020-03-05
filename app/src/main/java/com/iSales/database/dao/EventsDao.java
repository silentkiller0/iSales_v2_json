package com.iSales.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.provider.CalendarContract;

import com.iSales.database.entry.AgendaEventEntry;
import com.iSales.database.entry.EventsEntry;
import com.iSales.pages.calendar.Events;

import java.util.List;

@Dao
public interface EventsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNewEvent(EventsEntry events);

    @Query("SELECT * FROM events")
    List<EventsEntry> getAllEvents();

    @Query("SELECT * FROM events WHERE id = :id")
    List<EventsEntry> getEventsById(Long id);

    @Query("SELECT * FROM events WHERE DATE = :date")
    List<EventsEntry> getEventsByDate(String date);

    @Query("SELECT * FROM events WHERE MONTH = :month and YEAR = :year")
    List<EventsEntry> getEventsByMonth(String month, String year);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEvent(EventsEntry events);

    @Delete
    void deleteEvent(EventsEntry events);

    @Query("DELETE FROM events WHERE id = :id")
    void deleteEvent(long id);

    @Query("DELETE FROM events")
    void deleteAllEvent();
}
