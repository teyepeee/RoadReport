package com.thor.roadreport.model;

public class ListItem {
    private String judul;
    private String image;
    private String keterangan;

    public ListItem() {
    }

    public ListItem(String judul, String keterangan) {
        this.judul = judul;
        this.keterangan = keterangan;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}