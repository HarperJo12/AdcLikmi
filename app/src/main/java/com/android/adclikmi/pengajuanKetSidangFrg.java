package com.android.adclikmi;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.adclikmi.Model.tiketSidang;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class pengajuanKetSidangFrg extends Fragment implements DatePickerDialog.OnDateSetListener {
    public String title="Pengajuan Keterangan Akhir Sidang";
    private TextView npm,nama, jenjang, jurusan, bidang, dtpTgl, tglTrans;
    private EditText judulTA, semester, tahun;
    private tiketSidang tkt;
    private Button submit,cancel;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");


    public pengajuanKetSidangFrg() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.pengajuan_ket_sidang, parent, false);
        Calendar c = Calendar.getInstance();

        AndroidNetworking.initialize(view.getContext());

        ((MainActivity) getActivity()).setTitle(title);
        npm=(TextView) view.findViewById(R.id.frm_npm);
        nama=(TextView) view.findViewById(R.id.frm_nama_lengkap);
        jenjang=(TextView) view.findViewById(R.id.frm_jenjang_pendidikan);
        jurusan=(TextView) view.findViewById(R.id.frm_jurusan);
        bidang=(TextView) view.findViewById(R.id.frm_bidang_minat);
        tglTrans=(TextView)view.findViewById(R.id.frm_tgl_trans);
        dtpTgl=(TextView)view.findViewById(R.id.dtpTgl);
        judulTA=(EditText) view.findViewById(R.id.frm_judul_TA);
        semester=(EditText) view.findViewById(R.id.frm_semester);
        tahun=(EditText) view.findViewById(R.id.frm_thn_akademik);
        submit=(Button) view.findViewById(R.id.frm_submit);
        cancel=(Button) view.findViewById(R.id.frm_cancel);

        tkt=(tiketSidang)getArguments().getSerializable("tiket");
        npm.setText(tkt.getNPM());
        nama.setText(tkt.getNama());
        jenjang.setText(tkt.getJenjang());
        jurusan.setText(tkt.getJurusan());
        bidang.setText(tkt.getBidang());
        judulTA.setText(tkt.getJudulTA());
        semester.setText(tkt.getTahunSem().substring(9,tkt.getTahunSem().length()-16));
        tahun.setText(tkt.getTahunSem().substring(tkt.getTahunSem().length()-9));

        dtpTgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date=dtpTgl.getText().toString();
                int dtpy,dtpm,dtpd;
                dtpy = Integer.parseInt(date.substring(6, 10));
                dtpm = Integer.parseInt(date.substring(3, 5)) - 1;
                dtpd = Integer.parseInt(date.substring(0, 2));
                DatePickerDialog datePickerDialog= new DatePickerDialog(view.getContext(),pengajuanKetSidangFrg.this, dtpy, dtpm, dtpd);
                datePickerDialog.setTitle("Tanggal");
                datePickerDialog.show();
            }
        });

        dtpTgl.setText(sdf2.format(c.getTime()));
        tglTrans.setText(sdf2.format(c.getTime()));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).hideKeyboard();
                ((MainActivity)getActivity()).showDProgressDialog();
                postData();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day){
        int mon=month+1;
        dtpTgl.setText(day + "-" + mon + "-" + year );
    }

    public boolean getValidation(boolean submit, boolean hasFocus, TextInputLayout text, String alert){
        boolean bool=false;
        if(!hasFocus){
            String tmp=text.getEditText().getText().toString();
            if(tmp.isEmpty()) {
                text.setError(alert + " Tidak Boleh Kosong!");
                if(submit){
                    ((MainActivity)getActivity()).requestFocus(text);}
                bool=false;
            }else{
                text.setError(null);
                bool= true;
            }
        }
        return bool;
    }

    public void postData() {
        try {
            JSONObject obj = new JSONObject();

            obj.put("Tgl_Transaksi",sdf.format(sdf2.parse(tglTrans.getText().toString())));
            obj.put("Jenis_Tiket",4);
            obj.put("Stat",0);
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

            AndroidNetworking.post(((MainActivity)getActivity()).getBaseUrl()+"tpsi")
                    .addJSONObjectBody(obj)
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
                                    Toast.makeText(getContext(), "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                                    ((MainActivity)getActivity()).changeLayoutMain();
                                }else
                                    Toast.makeText(getContext(),"Data gagal ditambahkan!",Toast.LENGTH_SHORT).show();
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
