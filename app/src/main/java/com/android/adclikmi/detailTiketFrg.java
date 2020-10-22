package com.android.adclikmi;


import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.adclikmi.Model.tiketMahasiswa;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class detailTiketFrg extends Fragment {
    public String title="Detail Tiket";
    private TextView tglTiket, noTiket, npm, jenisTiket, lampiran, ket,status;
    private tiketMahasiswa tkt;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public detailTiketFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.detail_tiket, parent, false);

        tglTiket=(TextView) view.findViewById(R.id.frm_tgl_trans);
        noTiket=(TextView) view.findViewById(R.id.frm_no_tiket);
        npm=(TextView) view.findViewById(R.id.frm_npm);
        jenisTiket=(TextView) view.findViewById(R.id.frm_jenis_tiket);
        lampiran=(TextView) view.findViewById(R.id.frm_lampiran);
        ket=(TextView) view.findViewById(R.id.frm_keterangan);
        status=(TextView) view.findViewById(R.id.frm_status);

        ((MainActivity) getActivity()).setTitle(title);

        tkt=(tiketMahasiswa) getArguments().getSerializable("tiket");//mengambil objek custom yang telah dilampirkan
        tglTiket.setText(sdf.format(tkt.getTglTrans()));
        noTiket.setText(tkt.getNoTiket());
        npm.setText(tkt.getNPM());
        jenisTiket.setText(tkt.getJnsTiket());
        lampiran.setText("");
        ket.setText("");

        lampiran.setClickable(true);
        lampiran.setPaintFlags(lampiran.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }

}
