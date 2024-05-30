package com.example.firebasedemo;

import java.util.Date;

public class VE {
    private String maVe; // Khóa chính
    private String gioRa;
    private String gioVao;
    private double tongTien;
    private String bienSo; // Khóa ngoại
    private String cccd; // Khóa ngoại
    private String maCho; // Khóa ngoại

    public VE(String maVe, String gioRa, String gioVao, double tongTien, String bienSo, String cccd, String maCho) {
        this.maVe = maVe;
        this.gioRa = gioRa;
        this.gioVao = gioVao;
        this.tongTien = tongTien;
        this.bienSo = bienSo;
        this.cccd = cccd;
        this.maCho = maCho;
    }


    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public String getGioRa() {
        return gioRa;
    }

    public void setGioRa(Date gioRa) {
        this.gioRa = String.valueOf(gioRa);
    }

    public String getGioVao() {
        return gioVao;
    }

    public void setGioVao(Date gioVao) {
        this.gioVao = String.valueOf(gioVao);
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        this.bienSo = bienSo;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getMaCho() {
        return maCho;
    }

    public void setMaCho(String maCho) {
        this.maCho = maCho;
    }
    @Override
    public String toString() {
        return "maVE{" +
                "maVe='" + maVe + '\'' +
                ", gioVao='" + gioVao + '\'' +
                ", gioRa='" + gioRa + '\'' +
                ", bienSoXe='" + bienSo + '\'' +
                ", cccd='" + cccd + '\'' +
                ", tongTien=" + tongTien +
                '}';
    }

}
