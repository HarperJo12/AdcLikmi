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
import com.android.adclikmi.Model.user;

/**
 * Marcel 2019 *
 **/

public class homeMhsFrg extends Fragment {
    public String title="Adc LIKMI";
    private GridLayout gridLayout;
    Fragment fragment = null;
    Button change;


    public homeMhsFrg() {
        // Required empty public constructor
    }

    // Method onCreateView dipanggil saat Fragment harus menampilkan layoutnya dengan membuat layout tersebut secara manual lewat objek View atau dengan membaca file XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_mhs, parent, false);
        ((MainActivity) getActivity()).setTitle(title);
        ((MainActivity) getActivity()).setSearchState(0);
        ((MainActivity) getActivity()).invalidateOptionsMenu();
        setHasOptionsMenu(true);
        gridLayout=(GridLayout)view.findViewById(R.id.mainGrid);
        setEvent(gridLayout);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //to manage on click listener
    private void setEvent(GridLayout gridLayout) {
        for(int i = 0; i<gridLayout.getChildCount();i++){
            CardView cardView=(CardView)gridLayout.getChildAt(i);
            final int finalI= i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (finalI){
                        case 0 :
                            fragment= new pengajuanIzinFrg();break;
                        case 1 :
                            fragment= new pengajuanUjianFrg();break;
                        case 2 :
                            ((MainActivity)getActivity()).showDProgressDialog();
                            fragment= new pengajuanPerpindahanFrg();break;
                        case 3 :
                            ((MainActivity)getActivity()).showDProgressDialog();
                            fragment= new pengajuanPrmhnSidangFrg();break;

                    }

                    if (fragment != null) {
                        ((MainActivity)getActivity()).setClose(false);
                        ((MainActivity)getActivity()).changeLayout(fragment);
                    }
                }
            });
        }
    }
}
