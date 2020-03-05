package com.iSales.database.entry;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.util.Log;
import android.widget.Spinner;

import com.iSales.database.AppDatabase;
import com.iSales.remote.model.DebugItem;

@Entity(tableName = "debug_message")
public class DebugItemEntry {

    @PrimaryKey(autoGenerate = true)
    private int rowId;
    private long datetimeLong;
    private String mask;
    private String className;
    private String methodName;
    private String message;
    private String stackTrace;

    public DebugItemEntry() {
    }

    public DebugItemEntry(Context context, long timeStamp, String mask, String className, String methodName, String message, String stackTrace){
        this.datetimeLong = timeStamp;
        this.mask = mask + AppDatabase.getInstance(context).debugMessageDao().getAllDebugMessages().size();
        this.className =className;
        this.methodName =methodName;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public long getDatetimeLong() {
        return datetimeLong;
    }

    public void setDatetimeLong(long datetimeLong) {
        this.datetimeLong = datetimeLong;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
