package com.iSales.remote.model;


/**
 *      Created by Jean-Laurent
 */
public class AgendaUserassigned {
    private String id;
    private String transparency;

    public AgendaUserassigned() {
    }

    public AgendaUserassigned(String id, String transparency) {
        this.id = id;
        this.transparency = transparency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransparency() {
        return transparency;
    }

    public void setTransparency(String transparency) {
        this.transparency = transparency;
    }
}
