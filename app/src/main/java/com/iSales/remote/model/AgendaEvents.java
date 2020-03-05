package com.iSales.remote.model;

import com.iSales.database.entry.AgendaEventEntry;
import com.iSales.database.entry.EventsEntry;

import java.util.ArrayList;

/**
 *      Created by Jean-Laurent
 */

public class AgendaEvents {
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
    private Long datef;
    private String durationp;
    private String fulldayevent;
    private String punctual;
    private String percentage;
    private String location;
    private String transparency;
    private String priority;
    private AgendaUserassigned userassigned;
    private String userownerid;
    private String socid;
    private String contactid;
    private String elementid;
    private String elementtype;
    private String icalname;
    private String icalcolor;
    private ArrayList<String> actions;
    private String email_msgid;
    private String email_from;
    private String email_sender;
    private String email_to;
    private String email_tocc;
    private String email_tobcc;
    private String email_subject;
    private String errors_to;
    private String import_key;
    private ArrayList<String> array_options;
    private String linkedObjectsIds;
    private String fk_project;
    private String state;
    private String state_id;
    private String state_code;
    private String region;
    private String region_code;
    private String modelpdf;
    private String note_public;
    private String note_private;
    private String note;
    private String lines;
    private String type_picto;
    private String type_short;


    public AgendaEvents() {
    }

    public String getTable_rowid() {
        return table_rowid;
    }

    public void setTable_rowid(String table_rowid) {
        this.table_rowid = table_rowid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getDatef() {
        return datef;
    }

    public void setDatef(Long datef) {
        this.datef = datef;
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

    public AgendaUserassigned getUserassigned() {
        return userassigned;
    }

    public void setUserassigned(AgendaUserassigned userassigned) {
        this.userassigned = userassigned;
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

    public String getContactid() {
        return contactid;
    }

    public void setContactid(String contactid) {
        this.contactid = contactid;
    }

    public String getElementid() {
        return elementid;
    }

    public void setElementid(String elementid) {
        this.elementid = elementid;
    }

    public String getElementtype() {
        return elementtype;
    }

    public void setElementtype(String elementtype) {
        this.elementtype = elementtype;
    }

    public String getIcalname() {
        return icalname;
    }

    public void setIcalname(String icalname) {
        this.icalname = icalname;
    }

    public String getIcalcolor() {
        return icalcolor;
    }

    public void setIcalcolor(String icalcolor) {
        this.icalcolor = icalcolor;
    }

    public ArrayList<String> getActions() {
        return actions;
    }

    public void setActions(ArrayList<String> actions) {
        this.actions = actions;
    }

    public String getEmail_msgid() {
        return email_msgid;
    }

    public void setEmail_msgid(String email_msgid) {
        this.email_msgid = email_msgid;
    }

    public String getEmail_from() {
        return email_from;
    }

    public void setEmail_from(String email_from) {
        this.email_from = email_from;
    }

    public String getEmail_sender() {
        return email_sender;
    }

    public void setEmail_sender(String email_sender) {
        this.email_sender = email_sender;
    }

    public String getEmail_to() {
        return email_to;
    }

    public void setEmail_to(String email_to) {
        this.email_to = email_to;
    }

    public String getEmail_tocc() {
        return email_tocc;
    }

    public void setEmail_tocc(String email_tocc) {
        this.email_tocc = email_tocc;
    }

    public String getEmail_tobcc() {
        return email_tobcc;
    }

    public void setEmail_tobcc(String email_tobcc) {
        this.email_tobcc = email_tobcc;
    }

    public String getEmail_subject() {
        return email_subject;
    }

    public void setEmail_subject(String email_subject) {
        this.email_subject = email_subject;
    }

    public String getErrors_to() {
        return errors_to;
    }

    public void setErrors_to(String errors_to) {
        this.errors_to = errors_to;
    }

    public String getImport_key() {
        return import_key;
    }

    public void setImport_key(String import_key) {
        this.import_key = import_key;
    }

    public ArrayList<String> getArray_options() {
        return array_options;
    }

    public void setArray_options(ArrayList<String> array_options) {
        this.array_options = array_options;
    }

    public String getLinkedObjectsIds() {
        return linkedObjectsIds;
    }

    public void setLinkedObjectsIds(String linkedObjectsIds) {
        this.linkedObjectsIds = linkedObjectsIds;
    }

    public String getFk_project() {
        return fk_project;
    }

    public void setFk_project(String fk_project) {
        this.fk_project = fk_project;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion_code() {
        return region_code;
    }

    public void setRegion_code(String region_code) {
        this.region_code = region_code;
    }

    public String getModelpdf() {
        return modelpdf;
    }

    public void setModelpdf(String modelpdf) {
        this.modelpdf = modelpdf;
    }

    public String getNote_public() {
        return note_public;
    }

    public void setNote_public(String note_public) {
        this.note_public = note_public;
    }

    public String getNote_private() {
        return note_private;
    }

    public void setNote_private(String note_private) {
        this.note_private = note_private;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public String getType_picto() {
        return type_picto;
    }

    public void setType_picto(String type_picto) {
        this.type_picto = type_picto;
    }

    public String getType_short() {
        return type_short;
    }

    public void setType_short(String type_short) {
        this.type_short = type_short;
    }
}
