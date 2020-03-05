package com.iSales.remote.model;

public class DebugItem {
    private int rowId;
    private long datetimeLong;
    private String mask;
    private String errorMessage;

    public DebugItem() {
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
