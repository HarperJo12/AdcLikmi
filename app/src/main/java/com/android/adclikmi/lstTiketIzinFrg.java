package com.android.adclikmi;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.android.adclikmi.Model.mataKuliah;
import com.android.adclikmi.Model.tiketIzin;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class lstTiketIzinFrg extends Fragment {
    private Spinner kelas,jenis;
    public String title="Daftar Pengajuan Izin", query="";
    private RecyclerView recyclerView;
    private tiketIzinAdapter adapter;
    private ArrayList<tiketIzin> arrayList;
    private ArrayList<mataKuliah> matkul;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public lstTiketIzinFrg() {
        // Required empty public constructor
    }

    public tiketIzinAdapter getAdapter() {
        return adapter;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.lst_tiket_izin, container, false);
        AndroidNetworking.initialize(view.getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new tiketIzinAdapter(arrayList,getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        loadData();
        ((MainActivity) getActivity()).setTitle(title);//untuk menset title pada toolbar
        ((MainActivity) getActivity()).setSearchState(1);//mengubah state search pada toolbar
        ((MainActivity) getActivity()).invalidateOptionsMenu();//memanggil method oncreatemenuoption pada mainactivity

        kelas=(Spinner)view.findViewById(R.id.frm_kelas_spinner);
        jenis=(Spinner)view.findViewById(R.id.frm_jenis_spinner);

        List<String> list = new ArrayList<String>();//menambahkan data untuk spinner
        list.add(" ");
        list.add("Regular");
        list.add("Malam");

        List<String> list2 = new ArrayList<String>();
        list2.add("");
        list2.add("Sakit");
        list2.add("Izin");

        addDataSpinner(view,list,kelas);
        addDataSpinner(view,list2,jenis);
        kelas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setKelas(kelas.getSelectedItemPosition());
                adapter.getFilter().filter(query);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        jenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setJenis(jenis.getSelectedItem().toString());
                adapter.getFilter().filter(query);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    public void addDataSpinner(View view, List lst, Spinner spn){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_item, lst);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(dataAdapter);
    }

    public void loadData(){
        arrayList = new ArrayList<>();
        matkul = new ArrayList<>();
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"tpiz")
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
                                    matkul = new ArrayList<>();
                                    matkul.clear();
                                    JSONObject obj = response.getJSONObject(i);
                                    JSONArray matkulAr = obj.getJSONArray("pmatkuldet");

                                    for (int y = 0; y < matkulAr.length(); y++) {
                                        JSONObject objCh = matkulAr.getJSONObject(y);
                                        matkul.add(new mataKuliah(objCh.getString("Id"),
                                                objCh.getString("Kode_Mk"),
                                                objCh.getString("Nama_Mk"),
                                                sdf.parse(objCh.getString("Tgl_Kuliah")),
                                                objCh.getString("Jam"),
                                                objCh.getString("Kelas"),
                                                objCh.getInt("Stat")));
                                    }

                                    arrayList.add(new tiketIzin(obj.getString("No_Tiket"),
                                            sdf.parse(obj.getString("Tgl_Transaksi")),
                                            obj.getString("NPM"),
                                            obj.getString("Nama_Lkp"),
                                            obj.getInt("Kelas"),
                                            obj.getInt("Jenis_Tiket"),
                                            obj.getInt("Stat"),
                                            obj.getString("Jenis"),
                                            obj.getString("Lampiran"),
                                            sdf.parse(obj.getString("Tgl_Awal")),
                                            sdf.parse(obj.getString("Tgl_Akhir")),
                                            obj.getString("Keterangan"),
                                            matkul));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        adapter = new tiketIzinAdapter(arrayList,getContext());
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

class tiketIzinAdapter extends RecyclerView.Adapter<tiketIzinAdapter.tiketIzinViewHolder> {
    private int kelas=0;
    private String jenis="";
    private ArrayList<tiketIzin> dataList;
    private ArrayList<tiketIzin> dataListFiltered;
    private Context context;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public tiketIzinAdapter(ArrayList<tiketIzin> dataList, Context context) {
        this.dataList = dataList;
        this.dataListFiltered=dataList;
        this.context=context;
    }

    public void setKelas(int kelas) {
        this.kelas = kelas;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    @Override
    public tiketIzinViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tiket_izin_item, parent, false);
        return new tiketIzinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(tiketIzinViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tiketIzin item = dataListFiltered.get(position);
                changeLayout(item);
            }
        });
        holder.noTiket.setText(dataListFiltered.get(position).getNoTiket());
        holder.tglTrans.setText(sdf.format(dataListFiltered.get(position).getTglTrans()));
        holder.NPM.setText(dataListFiltered.get(position).getNPM());
        holder.nama.setText(dataListFiltered.get(position).getNama());
        holder.jenis.setText(dataListFiltered.get(position).getJenis());
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();
                Boolean kls, que, jns;

                ArrayList<tiketIzin> filtered = new ArrayList<>();

                if (query.isEmpty() && kelas==0 && jenis.isEmpty()) {
                    filtered = dataList;
                } else {
                    for (tiketIzin tiket : dataList) {
                        if(kelas==0 || kelas==tiket.getKelas()) kls=true; else kls=false;

                        if(jenis.isEmpty() || jenis.equalsIgnoreCase(tiket.getJenis())) jns= true; else jns=false;

                        if (tiket.getNoTiket().toLowerCase().contains(query.toLowerCase())
                        || tiket.getNPM().toLowerCase().contains(query.toLowerCase())
                        || tiket.getNama().toLowerCase().contains(query.toLowerCase()))
                            que=true; else que=false;

                        if(kls && que && jns) filtered.add(tiket);
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                dataListFiltered = (ArrayList<tiketIzin>) results.values;
                notifyDataSetChanged();//memberi tahu bahwa data pada list berubah
            }
        };
    }

    public void changeLayout(tiketIzin item){
        Fragment fragment=new penangananIzinFrg();
        Bundle bundle = new Bundle();
        bundle.putSerializable("tiket", item);
        fragment.setArguments(bundle);
        ((MainActivity)context).changeLayout(fragment);
    }

    @Override
    public int getItemCount() {
        return (dataListFiltered != null) ? dataListFiltered.size() : 0;
    }

    public class tiketIzinViewHolder extends RecyclerView.ViewHolder{
        private TextView noTiket, tglTrans, NPM, nama, jenis;

        public tiketIzinViewHolder(View itemView) {
            super(itemView);
            noTiket = (TextView) itemView.findViewById(R.id.tkt_notiket);
            tglTrans = (TextView) itemView.findViewById(R.id.tkt_tgl_trans);
            NPM = (TextView) itemView.findViewById(R.id.tkt_npm);
            nama = (TextView) itemView.findViewById(R.id.tkt_nama);
            jenis = (TextView) itemView.findViewById(R.id.tkt_jenis);
        }
    }

}

