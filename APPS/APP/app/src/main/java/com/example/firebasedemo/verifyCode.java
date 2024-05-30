package com.example.firebasedemo;


public class verifyCode {
   private String code;
    private String emai;
    private String tgianhethan;

    public verifyCode(String code, String emai, String tgianhethan) {
        this.code = code;
        this.emai = emai;
        this.tgianhethan = tgianhethan;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmai() {
        return emai;
    }

    public void setEmai(String emai) {
        this.emai = emai;
    }

    public String getTgianhethan() {
        return tgianhethan;
    }

    public void setTgianhethan(String tgianhethan) {
        this.tgianhethan = tgianhethan;
    }
}
