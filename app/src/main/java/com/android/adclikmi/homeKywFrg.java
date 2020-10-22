package com.android.adclikmi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.adclikmi.Model.user;

/**
 * Marcel 2019 *
 **/
public class homeKywFrg extends Fragment {
    private CardView izin,ujian,perpindahan,sidang;
    public String title="Adc LIKMI Karyawan";
    private GridLayout gridLayout;
    Fragment fragment = null;

    public homeKywFrg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_kyw, parent, false);
        ((MainActivity) getActivity()).setTitle(title);//untuk menset title pada toolbar
        ((MainActivity) getActivity()).setSearchState(0);//mengubah state search pada toolbar
        ((MainActivity) getActivity()).invalidateOptionsMenu();//memanggil method oncreatemenuoption pada mainactivity
        izin=(CardView)view.findViewById(R.id.mn_izin);
        ujian=(CardView)view.findViewById(R.id.mn_ujian);
        perpindahan=(CardView)view.findViewById(R.id.mn_perpindahan);
        sidang=(CardView)view.findViewById(R.id.mn_sidang);
        setHasOptionsMenu(true);
        gridLayout=(GridLayout)view.findViewById(R.id.mainGrid);
        setRole();
        setEvent(gridLayout);
        // Layout tampilan untuk fragment ini
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    //to manage on click listener
    private void setEvent(GridLayout gridLayout) {
        final int lvl = ((MainActivity)getActivity()).getUs().getLevel();
        for(int i = 0; i<gridLayout.getChildCount();i++){
            CardView cardView=(CardView)gridLayout.getChildAt(i);
            final int finalI= i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(lvl == 0){
                        switch (finalI){
                            case 0 :
                                fragment= new lstTiketIzinFrg();break;
                            case 1 :
                                fragment= new lstTiketUjianFrg();break;
                            case 2:
                                fragment= new lstTiketPerpindahanFrg();break;
                            case 3 :
                                fragment= new lstTiketSidangFrg();break;
                        }
                    }else if(lvl==2){
                        switch (finalI){
                            case 0 :
                                fragment= new lstTiketPerpindahanFrg();break;
                            case 1 :
                                fragment= new lstTiketSidangFrg();break;
                        }
                    }else if(lvl==3){
                        switch (finalI){
                            case 0 :
                                fragment= new lstTiketSidangFrg();break;
                        }
                    }else if(lvl==4){
                        switch (finalI){
                            case 0 :
                                fragment= new lstTiketIzinFrg();break;
                            case 1 :
                                fragment= new lstTiketUjianFrg();break;
                        }
                    }

                    if (fragment != null) {
                        ((MainActivity)getActivity()).setClose(false);
                        ((MainActivity)getActivity()).showDProgressDialog();
                        ((MainActivity)getActivity()).changeLayout(fragment);
                    }
                }
            });
        }
    }

    public void setRole(){
        final int lvl = ((MainActivity)getActivity()).getUs().getLevel();
        switch (lvl){
            case 2: ((LinearLayout.LayoutParams) gridLayout.getLayoutParams()).weight = 3.5f;
                gridLayout.setColumnCount(2);
                gridLayout.setRowCount(1);
                gridLayout.removeView(izin);
                gridLayout.removeView(ujian);break;
            case 3:((LinearLayout.LayoutParams) gridLayout.getLayoutParams()).weight = 3.5f;
                gridLayout.setColumnCount(2);
                gridLayout.setRowCount(1);
                gridLayout.removeView(izin);
                gridLayout.removeView(ujian);
                gridLayout.removeView(perpindahan);
                gridLayout.addView(perpindahan);
                perpindahan.setVisibility(View.INVISIBLE);break;
            case 4: ((LinearLayout.LayoutParams) gridLayout.getLayoutParams()).weight = 3.5f;
                gridLayout.setColumnCount(2);
                gridLayout.setRowCount(1);
                gridLayout.removeView(perpindahan);
                gridLayout.removeView(sidang); break;

        }
    }

}
