package com.example.firebasedemo;

public class CHO_DO {
    private String maCho; // Khóa chính
    private String trangThai;

    // Constructor mặc định không tham số
    public CHO_DO() {
    }

    // Constructor có tham số
    public CHO_DO(String maCho, String trangThai) {
        this.maCho = maCho;
        this.trangThai = trangThai;
    }

    public String getMaCho() {
        return maCho;
    }

    public void setMaCho(String maCho) {
        this.maCho = maCho;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
