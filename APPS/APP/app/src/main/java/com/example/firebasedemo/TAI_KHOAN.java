package com.example.firebasedemo;

public class TAI_KHOAN {
    private String tenDn;
    private String matKhau;
    private String cccd;


    public TAI_KHOAN(String tenDn, String matKhau, String cccd) {
        this.tenDn = tenDn;
        this.matKhau = matKhau;
        this.cccd = cccd;
    }

    public String getTenDn() {
        return tenDn;
    }

    public void setTenDn(String tenDn) {
        this.tenDn = tenDn;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }
}
