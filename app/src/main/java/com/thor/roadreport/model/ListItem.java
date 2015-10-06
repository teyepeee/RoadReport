package com.thor.roadreport.model;

public class ListItem {
    private String judul;
    private String gambar;
    private String isi_laporan;
    private Double latitude;
    private Double longitude;

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
        return gambar;
    }

    public void setImage(String gambar) {
        this.gambar = gambar;
    }

    public String getIsi() {
        return isi_laporan;
    }

    public void setIsi(String isi_laporan) {
        this.isi_laporan = isi_laporan;
    }

    public Double getLat() {
        return latitude;
    }

    public void setLat(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLng() {
        return longitude;
    }

    public void setLng(Double longitude) {
        this.longitude = longitude;
    }
}