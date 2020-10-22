package com.android.adclikmi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.adclikmi.Model.mataKuliah;
import com.android.adclikmi.Model.tiketMahasiswa;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class detailTiketUjianFrg extends Fragment {
    public String title="Detail Tiket Ujian";
    private TextView tglTiket, noTiket, npm, nama, jenis, semester, tglAwal, tglAkhir, ket, status;
    private tiketMahasiswa tkt;
    private TableLayout mTableLayout;
    private TableRow head;
    private mataKuliahTable mt;
    private ArrayList<mataKuliah> matkul;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public detailTiketUjianFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.detail_tiket_ujian, parent, false);
        AndroidNetworking.initialize(view.getContext());

        tglTiket=(TextView) view.findViewById(R.id.frm_tgl_trans);
        noTiket=(TextView) view.findViewById(R.id.frm_no_tiket);
        npm=(TextView) view.findViewById(R.id.frm_npm);
        nama=(TextView) view.findViewById(R.id.frm_nama_lengkap);
        jenis=(TextView) view.findViewById(R.id.frm_jenis);
        semester=(TextView) view.findViewById(R.id.frm_semester);
        tglAwal=(TextView) view.findViewById(R.id.frm_tgl_awal);
        tglAkhir=(TextView) view.findViewById(R.id.frm_tgl_akhir);
        ket=(TextView) view.findViewById(R.id.frm_keterangan);
        status=(TextView) view.findViewById(R.id.frm_status);
        head=(TableRow)view.findViewById(R.id.frm_header_matkul);
        mTableLayout=(TableLayout)view.findViewById(R.id.frm_data_matkul);
        loadDat();

        ((MainActivity) getActivity()).setTitle(title);//untuk menset title pada toolbar
        ((MainActivity) getActivity()).setSearchState(0);//mengubah state search pada toolbar
        ((MainActivity) getActivity()).invalidateOptionsMenu();//memanggil method oncreatemenuoption pada mainactivity

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AndroidNetworking.cancelAll();
        ((MainActivity)getActivity()).dismissDProgressDialog();
    }

    public void loadDat(){
        matkul = new ArrayList<>();
        tkt=(tiketMahasiswa) getArguments().getSerializable("tiket");//mengambil objek custom yang telah dilampirkan
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"tpuj/{tpuj}")
                .addPathParameter("tpuj", tkt.getNoTiket())
                .addHeaders("Authorization", "Bearer "+((MainActivity)getActivity()).getUs().getToken().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ((MainActivity)getActivity()).dismissDProgressDialog();
                            JSONObject obj=response.getJSONObject(0);
                            JSONArray matkulAr = obj.getJSONArray("pmatkuldet");
                            for(int y=0; y<matkulAr.length();y++){
                                JSONObject objCh = matkulAr.getJSONObject(y);
                                matkul.add(new mataKuliah(objCh.getString("Id"),
                                        objCh.getString("Kode_Mk"),
                                        objCh.getString("Nama_Mk"),
                                        sdf.parse(objCh.getString("Tgl_Kuliah")),
                                        objCh.getString("Jam"),
                                        objCh.getString("Kelas"),
                                        objCh.getInt("Stat")));
                            }

                            tglTiket.setText(obj.getString("Tgl_Transaksi"));
                            noTiket.setText(obj.getString("No_Tiket"));
                            npm.setText(obj.getString("NPM"));
                            nama.setText(obj.getString("Nama_Lkp"));
                            jenis.setText(obj.getString("Jenis"));
                            semester.setText(obj.getString("Tahun_Sem"));
                            tglAwal.setText(obj.getString("Tgl_Awal"));
                            tglAkhir.setText(obj.getString("Tgl_Akhir"));
                            ket.setText(obj.getString("Keterangan"));
                            status.setText(getStatus(obj.getInt("Stat")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mt=new mataKuliahTable(getContext(),mTableLayout,head,matkul,0);
                        mt.loadTable();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    public String getStatus(int stat){
        String tmp;
        switch (stat){
            case 0:tmp="Pengajuan Belum Diproses!";break;
            case 1:tmp="Pengajuan Diterima!";break;
            case 2:tmp="Pengajuan Ditolak!";break;
            case 3:tmp="Harap Menghadap Ketua Jurusan!";break;
            default:tmp="";
        }
        return tmp;
    }

}
