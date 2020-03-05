package com.iSales.model;

import java.util.ArrayList;

public class ISalesIncidentTable {
    private String name;
    private String priority;
    private ArrayList<ISalesIncidentTable> incidentTable;

    public ISalesIncidentTable() {
    }

    public ISalesIncidentTable(String name, String priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public ArrayList<ISalesIncidentTable> getIncidentTable() {
        return incidentTable;
    }

    public void setIncidentTable(ArrayList<ISalesIncidentTable> incidentTable) {
        this.incidentTable = incidentTable;
    }
}
