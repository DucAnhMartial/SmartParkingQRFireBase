package com.example.firebasedemo;

public class XE {
    private String bienSo; // Khóa chính
    private String mauXe;

    public XE(String bienSo, String mauXe) {
        this.bienSo = bienSo;
        this.mauXe = mauXe;
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        this.bienSo = bienSo;
    }

    public String getMauXe() {
        return mauXe;
    }

    public void setMauXe(String mauXe) {
        this.mauXe = mauXe;
    }
}
