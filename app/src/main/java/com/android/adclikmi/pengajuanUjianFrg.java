package com.android.adclikmi;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.adclikmi.Model.mataKuliah;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class pengajuanUjianFrg extends Fragment implements DatePickerDialog.OnDateSetListener{
    public String title="Pengajuan Ujian Susulan";
    private Spinner spnJenis, spnSemester;
    private TextView dtpAwal, dtpAkhir, tglTrans;
    private EditText keterangan;
    private Button submit, cancel;
    private TableLayout mTableLayout;
    private TableRow head;
    private mataKuliahTable mt;
    private ArrayList<mataKuliah> matkul;
    private ArrayList<mataKuliah> sMatkul;
    private int dateAct=0, kelas=0;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdf4 = new SimpleDateFormat("HH:mm:ss");

    public pengajuanUjianFrg() {
        // Required empty public constructor
    }

    public String getTitle() {
        return title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.pengajuan_ujian, parent, false);
        Calendar c = Calendar.getInstance();

        AndroidNetworking.initialize(view.getContext());

        ((MainActivity) getActivity()).setTitle(title);
        tglTrans=(TextView)view.findViewById(R.id.frm_tgl_trans);
        dtpAwal=(TextView)view.findViewById(R.id.dtpAwal);
        dtpAkhir=(TextView)view.findViewById(R.id.dtpAkhir);

        submit=(Button) view.findViewById(R.id.frm_submit);
        cancel=(Button) view.findViewById(R.id.frm_cancel);
        keterangan=(EditText) view.findViewById(R.id.frm_edit_ket);

        spnJenis = (Spinner) view.findViewById(R.id.frm_jenis_spinner);
        spnSemester = (Spinner) view.findViewById(R.id.frm_semester_spinner);

        loadMhs();
        setHasOptionsMenu(true);

        dtpAwal.setText(sdf2.format(c.getTime()));
        dtpAkhir.setText(sdf2.format(c.getTime()));
        tglTrans.setText(sdf2.format(c.getTime()));

        dtpAwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date=dtpAwal.getText().toString();
                int dtpy,dtpm,dtpd;
                dtpy = Integer.parseInt(date.substring(6, 10));
                dtpm = Integer.parseInt(date.substring(3, 5)) - 1;
                dtpd = Integer.parseInt(date.substring(0, 2));
                DatePickerDialog datePickerDialog= new DatePickerDialog(view.getContext(),pengajuanUjianFrg.this, dtpy, dtpm, dtpd);
                datePickerDialog.setTitle("Tanggal Awal");
                dateAct=0;
                datePickerDialog.show();
            }
        });

        dtpAkhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date=dtpAkhir.getText().toString();
                int dtpy,dtpm,dtpd;
                dtpy = Integer.parseInt(date.substring(6,10));
                dtpm = Integer.parseInt(date.substring(3,5))-1;
                dtpd = Integer.parseInt(date.substring(0,2));
                DatePickerDialog datePickerDialog= new DatePickerDialog(view.getContext(),pengajuanUjianFrg.this, dtpy, dtpm, dtpd);
                datePickerDialog.setTitle("Tanggal Akhir");
                dateAct=1;
                datePickerDialog.show();
            }
        });

        keterangan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getValidation(false, hasFocus,keterangan,"Keterangan");
            }
        });


        List<String> list1 = new ArrayList<String>();
        list1.add("Ujian Tengah Semester (UTS)");
        list1.add("Ujian Akhir Semester (UAS)");


        List<String> list2 = new ArrayList<String>();
        list2.add("Semester Ganjil Tahun 2018/2019");
        list2.add("Semester Genap Tahun 2018/2019");

        addData(view, list1, spnJenis);
        addData(view, list2, spnSemester);

        head=(TableRow)view.findViewById(R.id.frm_header_matkul);
        mTableLayout=(TableLayout)view.findViewById(R.id.frm_data_matkul);

        matkul=new ArrayList<>();

        mt=new mataKuliahTable(getContext(),mTableLayout,head,matkul,1);
        mt.loadTable();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMatkul = new ArrayList<>();
                sMatkul = mt.getMatkulSelected();
                if(getValidation(true, false,keterangan,"Keterangan")
                        && getValidationMk(sMatkul)){
                    ((MainActivity)getActivity()).hideKeyboard();
                    ((MainActivity)getActivity()).showDProgressDialog();
                    postData();
                }
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

    public boolean getValidationDate(){
        boolean bool=false;
        try {
            if(sdf2.parse(dtpAkhir.getText().toString()).compareTo(sdf2.parse(dtpAwal.getText().toString()))>=0) {
                bool= true;
            }else{
                Toast.makeText(getContext(),"Tanggal awal tidak boleh lebih besar dari tanggal akhir!",Toast.LENGTH_SHORT).show();
                bool=false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bool;
    }

    public boolean getValidation(boolean submit, boolean hasFocus, EditText text, String alert){
        boolean bool=false;
        if(!hasFocus){
            String tmp=text.getText().toString();
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day){
        try {
            int mon=month+1;
            switch (dateAct){
                case 0 : dtpAwal.setText(sdf2.format(sdf2.parse(day + "-" + mon + "-" + year ))); break;
                case 1 : dtpAkhir.setText(sdf2.format(sdf2.parse(day + "-" + mon + "-" + year )));
                    if (getValidationDate()) {
                        ((MainActivity) getActivity()).showDProgressDialog();
                        loadMatkul();
                    }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int transKelas(String kls){
        int i=0;
        if(kls.equalsIgnoreCase("Reguler")) i=1;
        else if(kls.equalsIgnoreCase("Malam")) i=2;
        else if(kls.equalsIgnoreCase("Eksekutif")) i=3;
        return i;
    }

    public void loadMhs(){
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"mhs/{mhs}")
                .addPathParameter("mhs", ((MainActivity)getActivity()).getUs().getUsername())
                .addHeaders("Authorization", "Bearer "+((MainActivity)getActivity()).getUs().getToken().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            kelas=transKelas(response.getString("Kelas"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getContext(), "Pastikan koneksi internet berjalan!", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).changeLayoutMain();
                    }
                });
    }

    public void loadMatkul() {
        matkul = new ArrayList<>();
        matkul.clear();
        try {
            JSONObject objk = new JSONObject();
            objk.put("Tgl_Awal", sdf.format(sdf2.parse(dtpAwal.getText().toString())));
            objk.put("Tgl_Akhir", sdf.format(sdf2.parse(dtpAkhir.getText().toString())));
            AndroidNetworking.post(((MainActivity)getActivity()).getBaseUrl()+"jkd/m/{jkd}")
                    .addPathParameter("jkd", ((MainActivity)getActivity()).getUs().getUsername())
                    .addJSONObjectBody(objk)
                    .addHeaders("Authorization", "Bearer "+((MainActivity)getActivity()).getUs().getToken().trim())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            ((MainActivity)getActivity()).dismissDProgressDialog();
                            if(!response.isNull(0)) {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject obj = response.getJSONObject(i);
                                        String mulai=sdf3.format(sdf4.parse(obj.getString("mulai")));
                                        String selesai=sdf3.format(sdf4.parse(obj.getString("selesai")));
                                        String jam=mulai + " - " +selesai;
                                        matkul.add(new mataKuliah("1",obj.getString("kodemk"),
                                                obj.getString("namamatkul"),
                                                sdf.parse(obj.getString("tanggal")),
                                                jam,
                                                obj.getString("kelas"),
                                                0));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            mt=new mataKuliahTable(getContext(),mTableLayout,head,matkul,1);
                            mt.loadTable();
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

    public void addData(View view, List lst, Spinner spn){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_item, lst);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(dataAdapter);
    }

    public String getJenis(int jenis) {
        String jns="";
        switch (jenis){
            case 0:jns="UTS";break;
            case 1:jns="UAS";break;
            default:jns="";
        }
        return jns;
    }

    public void postData() {
        try {
            JSONObject obj = new JSONObject();
            JSONObject mk;
            JSONArray mkAr = new JSONArray();
            for (mataKuliah m : sMatkul) {
                mk = new JSONObject();
                mk.put("Kode_Mk", m.getKodeMk());
                mk.put("Nama_Mk", m.getNamaMk());
                mk.put("Tgl_Kuliah", sdf.format(m.getTanggal()));
                mk.put("Jam", m.getJam());
                mk.put("Kelas", m.getKelas());
                mk.put("Stat",m.getStat());
                mkAr.put(mk);
            }

            obj.put("Tgl_Transaksi",sdf.format(sdf2.parse(tglTrans.getText().toString())));
            obj.put("Jenis_Tiket",2);
            obj.put("Stat",0);
            obj.put("Kelas", kelas);
            obj.put("NPM",((MainActivity)getActivity()).getUs().getUsername());
            obj.put("NIK_Cs",null);
            obj.put("NIK_Dosen",null);
            obj.put("Jenis",getJenis(spnJenis.getSelectedItemPosition()));
            obj.put("Tahun_Sem",spnSemester.getSelectedItem().toString());
            obj.put("Tgl_Awal",sdf.format(sdf2.parse(dtpAwal.getText().toString())));
            obj.put("Tgl_Akhir",sdf.format(sdf2.parse(dtpAkhir.getText().toString())));
            obj.put("Keterangan",keterangan.getText().toString());
            obj.put("Matkul",mkAr);

            AndroidNetworking.post(((MainActivity)getActivity()).getBaseUrl()+"tpuj")
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
                                    Toast.makeText(getContext(), "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                                    ((MainActivity) getActivity()).changeLayoutMain();
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
