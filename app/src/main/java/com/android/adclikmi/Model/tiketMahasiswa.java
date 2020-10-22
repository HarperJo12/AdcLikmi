package com.android.adclikmi.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Marcel 2019 *
 **/

public class tiketMahasiswa implements Serializable {
    private String noTiket;
    private Date tglTrans;
    private String NPM;
    private int kelas;
    private int jnsTiket;
    private int stat;

    public tiketMahasiswa(String noTiket, Date tglTrans, String NPM, int kelas, int jnsTiket, int stat) {
        this.noTiket = noTiket;
        this.tglTrans = tglTrans;
        this.NPM = NPM;
        this.kelas = kelas;
        this.jnsTiket = jnsTiket;
        this.stat = stat;
    }

    public String getNoTiket() {
        return noTiket;
    }

    public void setNoTiket(String noTiket) {
        this.noTiket = noTiket;
    }

    public Date getTglTrans() {
        return tglTrans;
    }

    public void setTglTrans(Date tglTrans) {
        this.tglTrans = tglTrans;
    }

    public String getNPM() {
        return NPM;
    }

    public void setNPM(String NPM) {
        this.NPM = NPM;
    }

    public int getKelas() {
        return kelas;
    }

    public void setKelas(int kelas) {
        this.kelas = kelas;
    }

    public int getJnsTiket() {
        return jnsTiket;
    }

    public void setJnsTiket(int jnsTiket) {
        this.jnsTiket = jnsTiket;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }
}
