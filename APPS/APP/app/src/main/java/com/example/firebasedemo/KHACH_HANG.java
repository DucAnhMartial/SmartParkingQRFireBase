package com.example.firebasedemo;



public class KHACH_HANG {
    private String cccd;
    private String hoTen;
    private String sdt;
    private String email;

    public KHACH_HANG(String cccd, String hoTen, String sdt, String email) {
        this.cccd = cccd;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.email = email;
    }

    public String getCccd() {
        return cccd;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getSdt() {
        return sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}