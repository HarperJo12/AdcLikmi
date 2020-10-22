package com.android.adclikmi.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Marcel 2019 *
 **/

public class mataKuliah implements Serializable {
    private String Id;
    private String kodeMk;
    private String namaMk;
    private Date tanggal;
    private String jam;
    private String kelas;
    private int stat;

    public mataKuliah(String id, String kodeMk, String namaMk, Date tanggal, String jam, String kelas, int stat) {
        Id = id;
        this.kodeMk = kodeMk;
        this.namaMk = namaMk;
        this.tanggal = tanggal;
        this.jam = jam;
        this.kelas = kelas;
        this.stat = stat;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getKodeMk() {
        return kodeMk;
    }

    public void setKodeMk(String kodeMk) {
        this.kodeMk = kodeMk;
    }

    public String getNamaMk() {
        return namaMk;
    }

    public void setNamaMk(String namaMk) {
        this.namaMk = namaMk;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }
}
