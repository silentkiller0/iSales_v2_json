package com.iSales.database.entry;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "settings")
public class SettingsEntry {
    @PrimaryKey(autoGenerate = false)
    private int id;
    private boolean showDescripCataloge;
    private boolean enableVirtualProductSync;
    private String email;
    private String email_Pwd;
    private String emailReceiverList;

    public SettingsEntry() {

    }

    public SettingsEntry(int id, boolean showDescripCataloge, boolean enableVirtualProductSync) {
        this.id = id;
        this.showDescripCataloge = showDescripCataloge;
        this.enableVirtualProductSync = enableVirtualProductSync;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isShowDescripCataloge() {
        return showDescripCataloge;
    }

    public void setShowDescripCataloge(boolean showDescripCataloge) {
        this.showDescripCataloge = showDescripCataloge;
    }

    public boolean isEnableVirtualProductSync() {
        return enableVirtualProductSync;
    }

    public void setEnableVirtualProductSync(boolean enableVirtualProductSync) {
        this.enableVirtualProductSync = enableVirtualProductSync;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail_Pwd() {
        return email_Pwd;
    }

    public void setEmail_Pwd(String email_Pwd) {
        this.email_Pwd = email_Pwd;
    }

    public String getEmailReceiverList() {
        return emailReceiverList;
    }

    public void setEmailReceiverList(String emailReceiverList) {
        this.emailReceiverList = emailReceiverList;
    }
}
