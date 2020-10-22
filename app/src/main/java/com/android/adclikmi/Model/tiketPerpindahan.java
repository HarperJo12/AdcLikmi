package com.android.adclikmi.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Marcel 2019 *
 **/

public class tiketPerpindahan implements Serializable {
    private String noTiket;
    private Date tglTrans;
    private String NPM;
    private String nama;
    private String nik_kjr;
    private int kelas;
    private int jnsTiket;
    private int stat;
    private String klsAwal;
    private String klsTujuan;
    private String jjgAwal;
    private String jjgTujuan;
    private String jrsAwal;
    private String jrsTujuan;
    private String bdgAwal;
    private String bdgTujuan;
    private String thnSem;
    private String keterangan;
    private String lampiran1;
    private String lampiran2;

    public tiketPerpindahan(String noTiket, Date tglTrans, String NPM, String nama, String nik_kjr, int kelas, int jnsTiket, int stat, String klsAwal, String klsTujuan, String jjgAwal, String jjgTujuan, String jrsAwal, String jrsTujuan, String bdgAwal, String bdgTujuan, String thnSem, String keterangan, String lampiran1, String lampiran2) {
        this.noTiket = noTiket;
        this.tglTrans = tglTrans;
        this.NPM = NPM;
        this.nama = nama;
        this.nik_kjr = nik_kjr;
        this.kelas = kelas;
        this.jnsTiket = jnsTiket;
        this.stat = stat;
        this.klsAwal = klsAwal;
        this.klsTujuan = klsTujuan;
        this.jjgAwal = jjgAwal;
        this.jjgTujuan = jjgTujuan;
        this.jrsAwal = jrsAwal;
        this.jrsTujuan = jrsTujuan;
        this.bdgAwal = bdgAwal;
        this.bdgTujuan = bdgTujuan;
        this.thnSem = thnSem;
        this.keterangan = keterangan;
        this.lampiran1 = lampiran1;
        this.lampiran2 = lampiran2;
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

    public String getNik_kjr() {
        return nik_kjr;
    }

    public void setNik_kjr(String nik_kjr) {
        this.nik_kjr = nik_kjr;
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

    public String getKlsAwal() {
        return klsAwal;
    }

    public void setKlsAwal(String klsAwal) {
        this.klsAwal = klsAwal;
    }

    public String getKlsTujuan() {
        return klsTujuan;
    }

    public void setKlsTujuan(String klsTujuan) {
        this.klsTujuan = klsTujuan;
    }

    public String getJjgAwal() {
        return jjgAwal;
    }

    public void setJjgAwal(String jjgAwal) {
        this.jjgAwal = jjgAwal;
    }

    public String getJjgTujuan() {
        return jjgTujuan;
    }

    public void setJjgTujuan(String jjgTujuan) {
        this.jjgTujuan = jjgTujuan;
    }

    public String getJrsAwal() {
        return jrsAwal;
    }

    public void setJrsAwal(String jrsAwal) {
        this.jrsAwal = jrsAwal;
    }

    public String getJrsTujuan() {
        return jrsTujuan;
    }

    public void setJrsTujuan(String jrsTujuan) {
        this.jrsTujuan = jrsTujuan;
    }

    public String getBdgAwal() {
        return bdgAwal;
    }

    public void setBdgAwal(String bdgAwal) {
        this.bdgAwal = bdgAwal;
    }

    public String getBdgTujuan() {
        return bdgTujuan;
    }

    public void setBdgTujuan(String bdgTujuan) {
        this.bdgTujuan = bdgTujuan;
    }

    public String getThnSem() {
        return thnSem;
    }

    public void setThnSem(String thnSem) {
        this.thnSem = thnSem;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getLampiran1() {
        return lampiran1;
    }

    public void setLampiran1(String lampiran1) {
        this.lampiran1 = lampiran1;
    }

    public String getLampiran2() {
        return lampiran2;
    }

    public void setLampiran2(String lampiran2) {
        this.lampiran2 = lampiran2;
    }
}
