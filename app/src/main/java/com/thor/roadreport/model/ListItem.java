package com.thor.roadreport.model;

public class ListItem {
    private String judul;
    private String image;
    private String isi_laporan;

    public ListItem() {
    }

    public ListItem(String judul, String isi_laporan) {
        this.judul = judul;
        this.isi_laporan = isi_laporan;
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

    public String getIsi() {
        return isi_laporan;
    }

    public void setIsi(String isi_laporan) {
        this.isi_laporan = isi_laporan;
    }
}