package com.android.adclikmi;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.adclikmi.Model.mataKuliah;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Marcel 2019 *
 **/

public class mataKuliahTable {
    private Context context;
    private TableLayout mTableLayout;
    private TableRow head;
    private ArrayList<mataKuliah> matkul;
    private ArrayList<mataKuliah> matkulSelected;
    private int tgl=25,jam=30, kdmk=20,nmmk=40,kls=20, pilih=15, stat=15, state=0, screenWidth;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public mataKuliahTable(Context context, TableLayout mTableLayout, TableRow head, ArrayList<mataKuliah> matkul, int state) {
        this.context = context;
        this.mTableLayout = mTableLayout;
        this.head = head;
        this.matkul = matkul;
        this.state = state;
        matkulSelected=new ArrayList<>();
    }

    public ArrayList<mataKuliah> getMatkulSelected() {
        return matkulSelected;
    }

    public ArrayList<mataKuliah> getMatkul() {
        return matkul;
    }

    public TextView setRowText(String text, int percentWidth, boolean head){
        final TextView tv = new TextView(context);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText(text);
        if(!head){
            tv.setTextColor(Color.parseColor("#000000"));
        }else{
            tv.setPadding(5,5,5,5);
            tv.setBackgroundColor(Color.parseColor("#008577"));
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }
        tv.setWidth(percentWidth * screenWidth / 100);
        tv.setTextAppearance(context,android.R.style.TextAppearance_Small);
        return tv;
    }

    public TextView setRowChk(int percentWidth, final int i){
        final CheckBox ch = new CheckBox(context);
        ch.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        ch.setScaleX(ch.getScaleX()*0.85f);
        ch.setScaleY(ch.getScaleY()*0.85f);
        ch.setGravity(Gravity.CENTER_HORIZONTAL);
        ch.setTextColor(Color.parseColor("#000000"));
        ch.setWidth(percentWidth * screenWidth / 100);
        ch.setTextAppearance(context,android.R.style.TextAppearance_Small);
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ch.isChecked()){
                    matkulSelected.add(matkul.get(i));
                    if(state==2){
                        matkul.get(i).setStat(1);
                    }
                }else{
                    matkulSelected.remove(matkul.get(i));
                    if(state==2){
                        matkul.get(i).setStat(2);
                    }
                }
            }
        });
        return ch;
    }

    public String getStat(int i){
        String tmp;
        switch (i){
            case 0:tmp="";break;
            case 1:tmp="Diterima!";break;
            case 2:tmp="Ditolak!";break;
            default:tmp="";
        }
        return tmp;
    }

    public void loadTable(){
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        head.removeAllViews();

        head.addView(setRowText("Tanggal",tgl,true));
        head.addView(setRowText("Jam",jam,true));
        head.addView(setRowText("Kode MK",kdmk,true));
        head.addView(setRowText("Nama MK",nmmk,true));
        head.addView(setRowText("Kelas",kls,true));
        if(state==1 || state == 2){
            head.addView(setRowText("Pilih",pilih,true));
        }else{

            head.addView(setRowText("Status",stat,true));
        }

        mTableLayout.removeAllViews();
        for(int i=0;i<matkul.size();i++){

            final TableRow tr = new TableRow(context);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(trParams);
            tr.addView(setRowText(sdf.format(matkul.get(i).getTanggal()),tgl,false));
            tr.addView(setRowText(matkul.get(i).getJam(),jam,false));
            tr.addView(setRowText(matkul.get(i).getKodeMk(),kdmk,false));
            tr.addView(setRowText(matkul.get(i).getNamaMk(),nmmk,false));
            tr.addView(setRowText(matkul.get(i).getKelas(),kls,false));
            if(state==1 || state==2){
                tr.addView(setRowChk(pilih,i));
            }else{
                tr.addView(setRowText(getStat(matkul.get(i).getStat()),stat,false));
            }
            mTableLayout.addView(tr);
        }
    }
}
