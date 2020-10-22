package com.android.adclikmi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.adclikmi.Model.tiketSidang;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class penangananKetSidangFrg extends Fragment {
    public String title="Penanganan Keterangan Akhir Sidang";
    private TextView tglTiket, noTiket, stat, npm, nama,  jenjang, jurusan, bidang, judulTA, semester, tahun, tglSurat;
    private Spinner aksi;
    private Button submit, cancel;
    private tiketSidang tkt;
    public int status;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public penangananKetSidangFrg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.penanganan_ket_sidang, parent, false);

        tglTiket=(TextView) view.findViewById(R.id.frm_tgl_trans);
        noTiket=(TextView) view.findViewById(R.id.frm_no_tiket);
        stat=(TextView) view.findViewById(R.id.frm_status);
        npm=(TextView) view.findViewById(R.id.frm_npm);
        nama=(TextView) view.findViewById(R.id.frm_nama_lengkap);
        jenjang=(TextView) view.findViewById(R.id.frm_jenjang_pendidikan);
        jurusan=(TextView) view.findViewById(R.id.frm_jurusan);
        bidang=(TextView) view.findViewById(R.id.frm_bidang_minat);
        judulTA=(TextView) view.findViewById(R.id.frm_judul_TA);
        aksi=(Spinner)view.findViewById(R.id.frm_aksi_spinner);
        semester=(TextView) view.findViewById(R.id.frm_semester);
        tahun=(TextView) view.findViewById(R.id.frm_thn_akademik);
        tglSurat=(TextView) view.findViewById(R.id.frm_tanggal);
        submit=(Button) view.findViewById(R.id.frm_submit);
        cancel=(Button) view.findViewById((R.id.frm_cancel));

        tkt=(tiketSidang) getArguments().getSerializable("tiket");
        tglTiket.setText(sdf.format(tkt.getTglTrans()));
        noTiket.setText(tkt.getNoTiket());
        stat.setText(getStatus(tkt.getStat()));
        npm.setText(tkt.getNPM());
        nama.setText(tkt.getNama());
        jenjang.setText(tkt.getJenjang());
        jurusan.setText(tkt.getJurusan());
        bidang.setText(tkt.getBidang());
        judulTA.setText(tkt.getJudulTA());
        semester.setText(tkt.getTahunSem().substring(9,tkt.getTahunSem().length()-16));
        tahun.setText(tkt.getTahunSem().substring(tkt.getTahunSem().length()-9));
        tglSurat.setText(sdf.format(tkt.getTglTrans()));

        setHasOptionsMenu(true);
        addData(view);
        ((MainActivity) getActivity()).setTitle(title);
        ((MainActivity) getActivity()).setSearchState(0);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

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
                status=aksi.getSelectedItemPosition();
                ((MainActivity)getActivity()).hideKeyboard();
                if(status!=0){
                    ((MainActivity) getActivity()).showDProgressDialog();
                    postData();
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

            obj.put("Tgl_Transaksi",sdf.format(tkt.getTglTrans()));
            obj.put("Jenis_Tiket",tkt.getJnsTiket());
            obj.put("Stat",status);
            obj.put("Kelas", tkt.getKelas());
            obj.put("NPM",tkt.getNPM());
            obj.put("NIK_Cs",null);
            obj.put("NIK_Dosen",tkt.getNik_dsb());
            obj.put("Ttl",tkt.getTtl());
            obj.put("Alamat",tkt.getAlamat());
            obj.put("Kd_Pos",tkt.getKodePos());
            obj.put("No_Telp",tkt.getNoTelp());
            obj.put("No_HP",tkt.getNoHp());
            obj.put("Email",tkt.getEmail());
            obj.put("Jenjang",tkt.getJenjang());
            obj.put("Jurusan",tkt.getJurusan());
            obj.put("Bidang",tkt.getBidang());
            obj.put("Judul_TA",tkt.getJudulTA());
            obj.put("Nama_Prs",tkt.getNamaPerusahaan());
            obj.put("Alamat_Prs",tkt.getAlamatPerusahaan());
            obj.put("Pembimbing",tkt.getPembimbing());
            obj.put("Ko_Pembimbing",tkt.getKoPembimbing());
            obj.put("Tahun_Sem",tkt.getTahunSem());
            obj.put("Lmp_Kwitansi",tkt.getLampiranKwitansi());

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
