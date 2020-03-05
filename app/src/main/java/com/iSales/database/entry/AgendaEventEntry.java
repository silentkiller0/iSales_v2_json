package com.iSales.database.entry;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.iSales.remote.model.AgendaEvents;

import java.util.ArrayList;

@Entity(tableName = "agenda_events")
public class AgendaEventEntry {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String table_rowid;
    private String ref;
    private String type_id;
    private String type_code;
    private String type;
    private String type_color;
    private String code;
    private String label;
    private Long datec;
    private Long datem;
    private String authorid;
    private String usermodid;
    private Long datep;
    private Long datep2;
    private String durationp;
    private String fulldayevent;
    private String punctual;
    private String percentage;
    private String location;
    private String transparency;
    private String priority;
    private String userownerid;
    private String socid;
    private String note;



    public AgendaEventEntry(){
    }

    public AgendaEventEntry(AgendaEvents mAgendaEvents){
        this.id = mAgendaEvents.getId();
        this.table_rowid = mAgendaEvents.getTable_rowid();
        this.ref = mAgendaEvents.getRef();
        this.type_id = mAgendaEvents.getType_id();
        this.type_code = mAgendaEvents.getType_code();
        this.type = mAgendaEvents.getType();
        this.type_color = mAgendaEvents.getType_color();
        this.code = mAgendaEvents.getCode();
        this.label = mAgendaEvents.getLabel();
        this.datec = mAgendaEvents.getDatec();
        this.datem = mAgendaEvents.getDatem();
        this.authorid = mAgendaEvents.getAuthorid();
        this.usermodid = mAgendaEvents.getUsermodid();
        this.datep = mAgendaEvents.getDatep();
        this.datep2 = mAgendaEvents.getDatef();
        this.durationp = mAgendaEvents.getDurationp();
        this.fulldayevent = mAgendaEvents.getFulldayevent();
        this.punctual = mAgendaEvents.getPunctual();
        this.percentage = mAgendaEvents.getPercentage();
        this.location = mAgendaEvents.getLocation();
        this.transparency = mAgendaEvents.getTransparency();
        this.priority = mAgendaEvents.getPriority();
        this.userownerid = mAgendaEvents.getUserownerid();
        this.socid = mAgendaEvents.getSocid();
        this.note = mAgendaEvents.getNote();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTable_rowid() {
        return table_rowid;
    }

    public void setTable_rowid(String table_rowid) {
        this.table_rowid = table_rowid;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType_color() {
        return type_color;
    }

    public void setType_color(String type_color) {
        this.type_color = type_color;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getDatec() {
        return datec;
    }

    public void setDatec(Long datec) {
        this.datec = datec;
    }

    public Long getDatem() {
        return datem;
    }

    public void setDatem(Long datem) {
        this.datem = datem;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getUsermodid() {
        return usermodid;
    }

    public void setUsermodid(String usermodid) {
        this.usermodid = usermodid;
    }

    public Long getDatep() {
        return datep;
    }

    public void setDatep(Long datep) {
        this.datep = datep;
    }

    public Long getDatep2() {
        return datep2;
    }

    public void setDatep2(Long datep2) {
        this.datep2 = datep2;
    }

    public String getDurationp() {
        return durationp;
    }

    public void setDurationp(String durationp) {
        this.durationp = durationp;
    }

    public String getFulldayevent() {
        return fulldayevent;
    }

    public void setFulldayevent(String fulldayevent) {
        this.fulldayevent = fulldayevent;
    }

    public String getPunctual() {
        return punctual;
    }

    public void setPunctual(String punctual) {
        this.punctual = punctual;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTransparency() {
        return transparency;
    }

    public void setTransparency(String transparency) {
        this.transparency = transparency;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUserownerid() {
        return userownerid;
    }

    public void setUserownerid(String userownerid) {
        this.userownerid = userownerid;
    }

    public String getSocid() {
        return socid;
    }

    public void setSocid(String socid) {
        this.socid = socid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
