package com.android.adclikmi.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Marcel 2019 *
 **/

public class tiketIzin implements Serializable {
    private String noTiket;
    private Date tglTrans;
    private String NPM;
    private String nama;
    private int kelas;
    private int jnsTiket;
    private int stat;
    private String jenis;
    private String lampiran;
    private Date tglAwal;
    private Date tglAkhir;
    private String keterangan;
    private ArrayList<mataKuliah> matkul;

    public tiketIzin(String noTiket, Date tglTrans, String NPM, String nama, int kelas, int jnsTiket, int stat, String jenis, String lampiran, Date tglAwal, Date tglAkhir, String keterangan, ArrayList<mataKuliah> matkul) {
        this.noTiket = noTiket;
        this.tglTrans = tglTrans;
        this.NPM = NPM;
        this.nama = nama;
        this.kelas = kelas;
        this.jnsTiket = jnsTiket;
        this.stat = stat;
        this.jenis = jenis;
        this.lampiran = lampiran;
        this.tglAwal = tglAwal;
        this.tglAkhir = tglAkhir;
        this.keterangan = keterangan;
        this.matkul = matkul;
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

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getLampiran() {
        return lampiran;
    }

    public void setLampiran(String lampiran) {
        this.lampiran = lampiran;
    }

    public Date getTglAwal() {
        return tglAwal;
    }

    public void setTglAwal(Date tglAwal) {
        this.tglAwal = tglAwal;
    }

    public Date getTglAkhir() {
        return tglAkhir;
    }

    public void setTglAkhir(Date tglAkhir) {
        this.tglAkhir = tglAkhir;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public ArrayList<mataKuliah> getMatkul() {
        return matkul;
    }

    public void setMatkul(ArrayList<mataKuliah> matkul) {
        this.matkul = matkul;
    }
}