package com.iSales.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.iSales.database.entry.UserEntry;
import com.iSales.database.entry.VirtualProductEntry;
import com.iSales.remote.model.ProductVirtual;

import java.util.List;

/**
 * Created by JDevs on 16/02/2020.
 */

@Dao
public interface VirtualProductDao {
    @Query("SELECT * FROM virtual_product")
    List<ProductVirtual> getAllVirtualProduct();

    @Query("SELECT * FROM virtual_product WHERE fk_product_fils = :id")
    List<ProductVirtual> getVirtualProductByChildId(Long id);

    @Query("SELECT * FROM virtual_product WHERE fk_product_fils = :id")
    List<ProductVirtual> getVirtualProductByParentId(String id);

    @Query("SELECT * FROM virtual_product WHERE fk_product_fils = :id AND rowid = (rowid + 1)")
    List<ProductVirtual> getVirtualProductByChildAndParentId(Long id);

    @Insert
    void insertVirtualProduct(ProductVirtual mProductVirtual);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateVirtualProduct(ProductVirtual mProductVirtual);

    @Delete
    void deleteVirtualProduct(ProductVirtual mProductVirtual);

    @Query("DELETE FROM virtual_product")
    void deleteAllVirtualProduct();
}
