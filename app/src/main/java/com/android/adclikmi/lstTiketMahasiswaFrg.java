package com.android.adclikmi;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
public class lstTiketMahasiswaFrg extends Fragment {
    public String title="Daftar Pengajuan Tiket";
    private RecyclerView recyclerView;
    private tiketMahasiswaAdapter adapter;
    private ArrayList<tiketMahasiswa> arrayList;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public lstTiketMahasiswaFrg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.lst_tiket_mahasiswa, container, false);

        AndroidNetworking.initialize(view.getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new tiketMahasiswaAdapter(arrayList, getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        loadData();
        ((MainActivity) getActivity()).setTitle(title);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AndroidNetworking.cancelAll();
        ((MainActivity) getActivity()).dismissDProgressDialog();
    }

    public void loadData(){
        arrayList = new ArrayList<>();
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"tiket/m/{tiket}")
                .addPathParameter("tiket", ((MainActivity)getActivity()).getUs().getUsername())
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
                                    //flt.setText(obj.getString("Nama_Lkp"));
                                    arrayList.add(new tiketMahasiswa(obj.getString("No_Tiket"),
                                            sdf.parse(obj.getString("Tgl_Transaksi")),
                                            obj.getString("NPM"),
                                            obj.getInt("Kelas"),
                                            obj.getInt("Jenis_Tiket"),
                                            obj.getInt("Stat")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        adapter = new tiketMahasiswaAdapter(arrayList,getContext());
                        recyclerView.setAdapter(adapter);
                    }
                    @Override
                    public void onError(ANError error) {
                        ((MainActivity)getActivity()).dismissDProgressDialog();
                        // handle error
                    }
                });
    }

}

class tiketMahasiswaAdapter extends RecyclerView.Adapter<tiketMahasiswaAdapter.tiketMahasiswaViewHolder> {
    private ArrayList<tiketMahasiswa> dataList;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Context context;

    public tiketMahasiswaAdapter(ArrayList<tiketMahasiswa> dataList, Context context) {
        this.dataList = dataList;
        this.context=context;
    }

    @Override
    public tiketMahasiswaViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tiket_mahasiswa_item, parent, false);
        return new tiketMahasiswaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(tiketMahasiswaViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tiketMahasiswa item=dataList.get(position);
                changeLayout(item);
            }
        });
        holder.noTiket.setText(dataList.get(position).getNoTiket());
        holder.tglTrans.setText(sdf.format(dataList.get(position).getTglTrans()));
        holder.NPM.setText(dataList.get(position).getNPM());
        holder.jenisTiket.setText(transJenisTiket(dataList.get(position).getJnsTiket()));
    }

    public String transJenisTiket(int jns){
        String tmp;
        switch (jns){
            case 1:tmp="Izin";break;
            case 2:tmp="Ujian Susulan";break;
            case 3:tmp="Perpindahan";break;
            case 4:tmp="Sidang";break;
            default:tmp="";
        }
        return tmp;
    }

    public void changeLayout(tiketMahasiswa item){
        Fragment fragment= new Fragment();
        Bundle bundle = new Bundle();
        int jns=item.getJnsTiket();
        switch (jns){
            case 1:fragment=new detailTiketIzinFrg();break;
            case 2:fragment=new detailTiketUjianFrg();break;
            case 3:fragment=new detailTiketPerpindahanFrg();break;
            case 4:fragment=new detailTiketPrmhnSidangFrg();break;
            default:
        }
        bundle.putSerializable("tiket", item);
        fragment.setArguments(bundle);
        ((MainActivity)context).showDProgressDialog();
        ((MainActivity)context).changeLayout(fragment);
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class tiketMahasiswaViewHolder extends RecyclerView.ViewHolder{
        private TextView noTiket, tglTrans, NPM, jenisTiket;

        public tiketMahasiswaViewHolder(View itemView) {
            super(itemView);
            noTiket = (TextView) itemView.findViewById(R.id.tkt_notiket);
            tglTrans = (TextView) itemView.findViewById(R.id.tkt_tgl_trans);
            NPM = (TextView) itemView.findViewById(R.id.tkt_npm);
            jenisTiket = (TextView) itemView.findViewById(R.id.tkt_jenis_tiket);
        }
    }

}
