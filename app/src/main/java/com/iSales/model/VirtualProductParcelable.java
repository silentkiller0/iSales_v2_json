package com.iSales.model;

// ###########################################################################################
// LAST EDIT : nouvelle classe
// ###########################################################################################
public class VirtualProductParcelable {
    private String Rowid_Colis;
    private String Ref_Colis;
    private String Price_TTC_Colis;
    private String TVA_Colis;
    private String Stock_Colis;
    private String Name_Colis;
    private String Quantite_Colis;
    private String Price_Colis;
    private String Rowid_Palette;
    private String Ref_Palette;
    private String Price_TTC_Palette;
    private String Price_Palette;
    private String Name_Palette;
    private String Quantite_Palette;
    private String TVA_Palette;
    private String Stock_Palette;

    public VirtualProductParcelable() {
    }

    public VirtualProductParcelable(String rowid_Colis, String ref_Colis, String price_TTC_Colis, String TVA_Colis, String stock_Colis, String name_Colis, String quantite_Colis, String price_Colis, String rowid_Palette, String ref_Palette, String price_TTC_Palette, String price_Palette, String name_Palette, String quantite_Palette, String TVA_Palette, String stock_Palette) {
        Rowid_Colis = rowid_Colis;
        Ref_Colis = ref_Colis;
        Price_TTC_Colis = price_TTC_Colis;
        this.TVA_Colis = TVA_Colis;
        Stock_Colis = stock_Colis;
        Name_Colis = name_Colis;
        Quantite_Colis = quantite_Colis;
        Price_Colis = price_Colis;
        Rowid_Palette = rowid_Palette;
        Ref_Palette = ref_Palette;
        Price_TTC_Palette = price_TTC_Palette;
        Price_Palette = price_Palette;
        Name_Palette = name_Palette;
        Quantite_Palette = quantite_Palette;
        this.TVA_Palette = TVA_Palette;
        Stock_Palette = stock_Palette;
    }

    public String getRowid_Colis() {
        return Rowid_Colis;
    }

    public void setRowid_Colis(String rowid_Colis) {
        Rowid_Colis = rowid_Colis;
    }

    public String getRef_Colis() {
        return Ref_Colis;
    }

    public void setRef_Colis(String ref_Colis) {
        Ref_Colis = ref_Colis;
    }

    public String getPrice_TTC_Colis() {
        return Price_TTC_Colis;
    }

    public void setPrice_TTC_Colis(String price_TTC_Colis) {
        Price_TTC_Colis = price_TTC_Colis;
    }

    public String getTVA_Colis() {
        return TVA_Colis;
    }

    public void setTVA_Colis(String TVA_Colis) {
        this.TVA_Colis = TVA_Colis;
    }

    public String getStock_Colis() {
        return Stock_Colis;
    }

    public void setStock_Colis(String stock_Colis) {
        Stock_Colis = stock_Colis;
    }

    public String getName_Colis() {
        return Name_Colis;
    }

    public void setName_Colis(String name_Colis) {
        Name_Colis = name_Colis;
    }

    public String getQuantite_Colis() {
        return Quantite_Colis;
    }

    public void setQuantite_Colis(String quantite_Colis) {
        Quantite_Colis = quantite_Colis;
    }

    public String getPrice_Colis() {
        return Price_Colis;
    }

    public void setPrice_Colis(String price_Colis) {
        Price_Colis = price_Colis;
    }

    public String getRowid_Palette() {
        return Rowid_Palette;
    }

    public void setRowid_Palette(String rowid_Palette) {
        Rowid_Palette = rowid_Palette;
    }

    public String getRef_Palette() {
        return Ref_Palette;
    }

    public void setRef_Palette(String ref_Palette) {
        Ref_Palette = ref_Palette;
    }

    public String getPrice_TTC_Palette() {
        return Price_TTC_Palette;
    }

    public void setPrice_TTC_Palette(String price_TTC_Palette) {
        Price_TTC_Palette = price_TTC_Palette;
    }

    public String getPrice_Palette() {
        return Price_Palette;
    }

    public void setPrice_Palette(String price_Palette) {
        Price_Palette = price_Palette;
    }

    public String getName_Palette() {
        return Name_Palette;
    }

    public void setName_Palette(String name_Palette) {
        Name_Palette = name_Palette;
    }

    public String getQuantite_Palette() {
        return Quantite_Palette;
    }

    public void setQuantite_Palette(String quantite_Palette) {
        Quantite_Palette = quantite_Palette;
    }

    public String getTVA_Palette() {
        return TVA_Palette;
    }

    public void setTVA_Palette(String TVA_Palette) {
        this.TVA_Palette = TVA_Palette;
    }

    public String getStock_Palette() {
        return Stock_Palette;
    }

    public void setStock_Palette(String stock_Palette) {
        Stock_Palette = stock_Palette;
    }
}
