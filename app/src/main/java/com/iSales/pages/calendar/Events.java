package com.iSales.pages.calendar;

import java.io.Serializable;

public class Events implements Serializable {
    private Long ID;
    private String LABEL;
    private String LIEU;
    private String PERCENTAGE;
    private String FULLDAYEVENT;
    private String TRANSPARENCY;
    private String TIME;
    private String DATE;
    private String MONTH;
    private String YEAR;
    private Long START_EVENT;
    private Long END_EVENT;
    private String DESCRIPTION;

    public Events(Long ID, String LABEL, String LIEU, String PERCENTAGE, String FULLDAYEVENT, String TRANSPARENCY, String TIME, String DATE, String MONTH, String YEAR, Long START_EVENT, Long END_EVENT, String DESCRIPTION) {
        this.ID = ID;
        this.LABEL = LABEL;
        this.LIEU = LIEU;
        this.PERCENTAGE = PERCENTAGE;
        this.FULLDAYEVENT = FULLDAYEVENT;
        this.TRANSPARENCY = TRANSPARENCY;
        this.TIME = TIME;
        this.DATE = DATE;
        this.MONTH = MONTH;
        this.YEAR = YEAR;
        this.START_EVENT = START_EVENT;
        this.END_EVENT = END_EVENT;
        this.DESCRIPTION = DESCRIPTION;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getLABEL() {
        return LABEL;
    }

    public void setLABEL(String LABEL) {
        this.LABEL = LABEL;
    }

    public String getLIEU() {
        return LIEU;
    }

    public void setLIEU(String LIEU) {
        this.LIEU = LIEU;
    }

    public String getPERCENTAGE() {
        return PERCENTAGE;
    }

    public void setPERCENTAGE(String PERCENTAGE) {
        this.PERCENTAGE = PERCENTAGE;
    }

    public String getFULLDAYEVENT() {
        return FULLDAYEVENT;
    }

    public void setFULLDAYEVENT(String FULLDAYEVENT) {
        this.FULLDAYEVENT = FULLDAYEVENT;
    }

    public String getTRANSPARENCY() {
        return TRANSPARENCY;
    }

    public void setTRANSPARENCY(String TRANSPARENCY) {
        this.TRANSPARENCY = TRANSPARENCY;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getMONTH() {
        return MONTH;
    }

    public void setMONTH(String MONTH) {
        this.MONTH = MONTH;
    }

    public String getYEAR() {
        return YEAR;
    }

    public void setYEAR(String YEAR) {
        this.YEAR = YEAR;
    }

    public Long getSTART_EVENT() {
        return START_EVENT;
    }

    public void setSTART_EVENT(Long START_EVENT) {
        this.START_EVENT = START_EVENT;
    }

    public Long getEND_EVENT() {
        return END_EVENT;
    }

    public void setEND_EVENT(Long END_EVENT) {
        this.END_EVENT = END_EVENT;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }
}
