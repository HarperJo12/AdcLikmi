package com.android.adclikmi.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Marcel 2019 *
 **/

public class tiketSidang implements Serializable {
    private String noTiket;
    private Date tglTrans;
    private String NPM;
    private String nama;
    private String nik_dsb;
    private int kelas;
    private int jnsTiket;
    private int stat;
    private String ttl;
    private String alamat;
    private String kodePos;
    private String noTelp;
    private String noHp;
    private String email;
    private String jenjang;
    private String jurusan;
    private String bidang;
    private String judulTA;
    private String namaPerusahaan;
    private String alamatPerusahaan;
    private String pembimbing;
    private String koPembimbing;
    private String tahunSem;
    private String lampiranKwitansi;

    public tiketSidang(String noTiket, Date tglTrans, String NPM, String nama, String nik_dsb, int kelas, int jnsTiket, int stat, String ttl, String alamat, String kodePos, String noTelp, String noHp, String email, String jenjang, String jurusan, String bidang, String judulTA, String namaPerusahaan, String alamatPerusahaan, String pembimbing, String koPembimbing, String tahunSem, String lampiranKwitansi) {
        this.noTiket = noTiket;
        this.tglTrans = tglTrans;
        this.NPM = NPM;
        this.nama = nama;
        this.nik_dsb = nik_dsb;
        this.kelas = kelas;
        this.jnsTiket = jnsTiket;
        this.stat = stat;
        this.ttl = ttl;
        this.alamat = alamat;
        this.kodePos = kodePos;
        this.noTelp = noTelp;
        this.noHp = noHp;
        this.email = email;
        this.jenjang = jenjang;
        this.jurusan = jurusan;
        this.bidang = bidang;
        this.judulTA = judulTA;
        this.namaPerusahaan = namaPerusahaan;
        this.alamatPerusahaan = alamatPerusahaan;
        this.pembimbing = pembimbing;
        this.koPembimbing = koPembimbing;
        this.tahunSem = tahunSem;
        this.lampiranKwitansi = lampiranKwitansi;
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

    public String getNik_dsb() {
        return nik_dsb;
    }

    public void setNik_dsb(String nik_dsb) {
        this.nik_dsb = nik_dsb;
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

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKodePos() {
        return kodePos;
    }

    public void setKodePos(String kodePos) {
        this.kodePos = kodePos;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJenjang() {
        return jenjang;
    }

    public void setJenjang(String jenjang) {
        this.jenjang = jenjang;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public String getBidang() {
        return bidang;
    }

    public void setBidang(String bidang) {
        this.bidang = bidang;
    }

    public String getJudulTA() {
        return judulTA;
    }

    public void setJudulTA(String judulTA) {
        this.judulTA = judulTA;
    }

    public String getNamaPerusahaan() {
        return namaPerusahaan;
    }

    public void setNamaPerusahaan(String namaPerusahaan) {
        this.namaPerusahaan = namaPerusahaan;
    }

    public String getAlamatPerusahaan() {
        return alamatPerusahaan;
    }

    public void setAlamatPerusahaan(String alamatPerusahaan) {
        this.alamatPerusahaan = alamatPerusahaan;
    }

    public String getPembimbing() {
        return pembimbing;
    }

    public void setPembimbing(String pembimbing) {
        this.pembimbing = pembimbing;
    }

    public String getKoPembimbing() {
        return koPembimbing;
    }

    public void setKoPembimbing(String koPembimbing) {
        this.koPembimbing = koPembimbing;
    }

    public String getTahunSem() {
        return tahunSem;
    }

    public void setTahunSem(String tahunSem) {
        this.tahunSem = tahunSem;
    }

    public String getLampiranKwitansi() {
        return lampiranKwitansi;
    }

    public void setLampiranKwitansi(String lampiranKwitansi) {
        this.lampiranKwitansi = lampiranKwitansi;
    }
}
