package com.iSales.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.iSales.database.entry.UserEntry;

import java.util.List;

/**
 * Created by netserve on 07/10/2018.
 */

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<UserEntry> getUser();

    @Query("SELECT * FROM user")
    LiveData<List<UserEntry>> loadUser();

    @Insert
    void insertUser(UserEntry userEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUser(UserEntry userEntry);

    @Delete
    void deleteUser(UserEntry userEntry);

    @Query("DELETE FROM user")
    void deleteAllUser();
}
