package com.iSales.database.entry;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "agenda_userassigned",
        foreignKeys = {
                @ForeignKey(entity = AgendaEventEntry.class, parentColumns = "id", childColumns = "id", onDelete = ForeignKey.CASCADE)})
public class AgendaUserassignedEntry {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String mandatory;
    private String answer_status;
    private String transparency;

    public AgendaUserassignedEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getAnswer_status() {
        return answer_status;
    }

    public void setAnswer_status(String answer_status) {
        this.answer_status = answer_status;
    }

    public String getTransparency() {
        return transparency;
    }

    public void setTransparency(String transparency) {
        this.transparency = transparency;
    }
}
