package com.android.adclikmi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.adclikmi.Model.mataKuliah;
import com.android.adclikmi.Model.tiketUjian;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class penangananUjianFrg extends Fragment {
    public String title="Penanganan Ujian Susulan";
    private TextView tglTiket, noTiket, npm, nama, stat, jenis, semester, tglAwal, tglAkhir, ket;
    private TableLayout mTableLayout;
    private TableRow head;
    private Spinner aksi;
    private Button submit, cancel;
    private tiketUjian tkt;
    private mataKuliahTable mt;
    private ArrayList<mataKuliah> matkul;
    private ArrayList<mataKuliah> sMatkul;
    public int status;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public penangananUjianFrg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.penanganan_ujian, parent, false);

        tglTiket=(TextView) view.findViewById(R.id.frm_tgl_trans);
        noTiket=(TextView) view.findViewById(R.id.frm_no_tiket);
        npm=(TextView) view.findViewById(R.id.frm_npm);
        nama=(TextView) view.findViewById(R.id.frm_nama_lengkap);
        stat=(TextView) view.findViewById(R.id.frm_status);
        jenis=(TextView) view.findViewById(R.id.frm_jenis);
        semester=(TextView) view.findViewById(R.id.frm_semester);
        tglAwal=(TextView) view.findViewById(R.id.frm_tgl_awal);
        tglAkhir=(TextView) view.findViewById(R.id.frm_tgl_akhir);
        ket=(TextView) view.findViewById(R.id.frm_keterangan);
        aksi=(Spinner)view.findViewById(R.id.frm_aksi_spinner);
        submit=(Button) view.findViewById(R.id.frm_submit);
        cancel=(Button) view.findViewById((R.id.frm_cancel));
        head=(TableRow)view.findViewById(R.id.frm_header_matkul);
        mTableLayout=(TableLayout)view.findViewById(R.id.frm_data_matkul);

        setHasOptionsMenu(true);
        addData(view);
        ((MainActivity) getActivity()).setTitle(title);
        ((MainActivity) getActivity()).setSearchState(0);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        tkt=(tiketUjian) getArguments().getSerializable("tiket");
        tglTiket.setText(sdf.format(tkt.getTglTrans()));
        noTiket.setText(tkt.getNoTiket());
        npm.setText(tkt.getNPM());
        stat.setText(getStatus(tkt.getStat()));
        nama.setText(tkt.getNama());
        jenis.setText(tkt.getJenis());
        semester.setText(tkt.getThnSem());
        tglAwal.setText(sdf.format(tkt.getTglAwal()));
        tglAkhir.setText(sdf.format(tkt.getTglAkhir()));
        ket.setText(tkt.getKeterangan());
        matkul=tkt.getMatkul();
        mt=new mataKuliahTable(getContext(),mTableLayout,head,matkul,2);
        mt.loadTable();

        if(tkt.getStat()!=0 && tkt.getStat()!=3) {
            submit.setEnabled(false);
            aksi.setEnabled(false);
        }else {
            submit.setEnabled(true);
            aksi.setEnabled(true);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMatkul=new ArrayList<>();
                sMatkul=mt.getMatkulSelected();
                status=aksi.getSelectedItemPosition();
                ((MainActivity)getActivity()).hideKeyboard();
                if(status!=0){
                    if(status==1 && getValidationMk(sMatkul)){
                        matkul=new ArrayList<>();
                        matkul.clear();
                        matkul=mt.getMatkul();
                        for(mataKuliah m : matkul){
                            if(m.getStat()==0) {
                                m.setStat(2);
                            }
                        }
                        ((MainActivity)getActivity()).showDProgressDialog();
                        postData();
                    }else if(status==2 || status==3) {
                        for(mataKuliah m : matkul){
                            if(status==2) {
                                m.setStat(2);
                            }else if(status==3){
                                m.setStat(0);
                            }
                        }
                        ((MainActivity)getActivity()).showDProgressDialog();
                        postData();
                    }
                }else{
                    Toast.makeText(getContext(), "Harap memilih aksi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).hideKeyboard();
                ((MainActivity)getActivity()).changeLayoutMain();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AndroidNetworking.cancelAll();
    }

    public boolean getValidationMk(ArrayList<mataKuliah> mk){
        boolean bool=false;
        if(mk.isEmpty()) {
            Toast.makeText(getContext(),"Mata kuliah harus dipilih!",Toast.LENGTH_SHORT).show();
            bool=false;
        }else{
            bool= true;
        }
        return bool;
    }

    public String getStatus(int stat) {
        String st="";
        switch (stat){
            case 0:st="Belum diproses";break;
            case 1:st="Diterima";break;
            case 2:st="Ditolak";break;
            case 3:st="Harap Menghadap Ketua Jurusan";break;
            default:st="Belum diproses";
        }
        return st;
    }

    public void addData(View view){
        List<String> list = new ArrayList<String>();
        list.add("");
        list.add("Diterima");
        list.add("Ditolak");
        list.add("Harap Menghadap Ketua Jurusan");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aksi.setAdapter(dataAdapter);
    }

    public void postData() {
        try {
            JSONObject obj = new JSONObject();
            JSONObject mk;
            JSONArray mkAr = new JSONArray();
            for (mataKuliah m : matkul) {
                mk = new JSONObject();
                mk.put("Id", m.getId());
                mk.put("Kode_Mk", m.getKodeMk());
                mk.put("Nama_Mk", m.getNamaMk());
                mk.put("Tgl_Kuliah", sdf.format(m.getTanggal()));
                mk.put("Jam", m.getJam());
                mk.put("Kelas", m.getKelas());
                mk.put("Stat",m.getStat());
                mkAr.put(mk);
            }

            obj.put("Tgl_Transaksi",sdf.format(tkt.getTglTrans()));
            obj.put("Jenis_Tiket",tkt.getJnsTiket());
            obj.put("Stat",status);
            obj.put("Kelas", tkt.getKelas());
            obj.put("NPM",tkt.getNPM());
            obj.put("NIK_Cs",null);
            obj.put("NIK_Dosen",null);
            obj.put("Jenis",tkt.getJenis());
            obj.put("Tahun_Sem",tkt.getThnSem());
            obj.put("Tgl_Awal",sdf.format(tkt.getTglAwal()));
            obj.put("Tgl_Akhir",sdf.format(tkt.getTglAkhir()));
            obj.put("Keterangan",tkt.getKeterangan());
            obj.put("Matkul",mkAr);

            AndroidNetworking.put(((MainActivity)getActivity()).getBaseUrl()+"tiket/{tiket}")
                    .addPathParameter("tiket", tkt.getNoTiket())
                    .addJSONObjectBody(obj) // posting json
                    .addHeaders("Authorization", "Bearer "+((MainActivity)getActivity()).getUs().getToken().trim())
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ((MainActivity)getActivity()).dismissDProgressDialog();
                            try {
                                if(response.getBoolean("sukses")) {
                                    Toast.makeText(getContext(), "Data berhasil diupdate!", Toast.LENGTH_SHORT).show();
                                    ((MainActivity)getActivity()).changeLayoutMain();
                                }else
                                    Toast.makeText(getContext(),"Data gagal diupdate! "+ response.getString("eror"),Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError error) {
                            ((MainActivity)getActivity()).dismissDProgressDialog();
                            // handle error
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
