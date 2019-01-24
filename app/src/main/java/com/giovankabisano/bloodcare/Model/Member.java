package com.giovankabisano.bloodcare.Model;

public class Member {
    public String nama;
    public String email;
    public String password;
    public int tinggi;
    public int berat;
    public Double umur;
    public boolean rokok;

    public Member() {
    }

    public Member(String nama, String email, String password, int tinggi, int berat, Double umur, boolean rokok) {
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.tinggi = tinggi;
        this.berat = berat;
        this.umur = umur;
        this.rokok = rokok;
    }
}
