package com.android.adclikmi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.adclikmi.Model.tiketSidang;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class detailTiketKetSidangFrg extends Fragment {
    public String title="Detail Tiket Keterangan Akhir Sidang";
    private TextView tglTiket, noTiket, npm, nama, jenjang, jurusan, bidang, judulTA, semester, tahun, tglSurat;
    private tiketSidang tkt;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public detailTiketKetSidangFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.detail_tiket_ket_sidang, parent, false);

        tglTiket=(TextView) view.findViewById(R.id.frm_tgl_trans);
        noTiket=(TextView) view.findViewById(R.id.frm_no_tiket);
        npm=(TextView) view.findViewById(R.id.frm_npm);
        nama=(TextView) view.findViewById(R.id.frm_nama_lengkap);
        jenjang=(TextView) view.findViewById(R.id.frm_jenjang_pendidikan);
        jurusan=(TextView) view.findViewById(R.id.frm_jurusan);
        bidang=(TextView) view.findViewById(R.id.frm_bidang_minat);
        judulTA=(TextView) view.findViewById(R.id.frm_judul_TA);
        semester=(TextView) view.findViewById(R.id.frm_semester);
        tahun=(TextView) view.findViewById(R.id.frm_thn_akademik);
        tglSurat=(TextView) view.findViewById(R.id.frm_tanggal);

        tkt=(tiketSidang) getArguments().getSerializable("tiket");
        tglTiket.setText(sdf.format(tkt.getTglTrans()));
        noTiket.setText(tkt.getNoTiket());
        npm.setText(tkt.getNPM());
        nama.setText(tkt.getNama());
        jenjang.setText(tkt.getJenjang());
        jurusan.setText(tkt.getJurusan());
        bidang.setText(tkt.getBidang());
        judulTA.setText(tkt.getJudulTA());
        semester.setText(tkt.getTahunSem().substring(9,tkt.getTahunSem().length()-16));
        tahun.setText(tkt.getTahunSem().substring(tkt.getTahunSem().length()-9));
        tglSurat.setText(sdf.format(tkt.getTglTrans()));

        ((MainActivity) getActivity()).setTitle(title);
        ((MainActivity) getActivity()).setSearchState(0);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        return view;
    }

}
