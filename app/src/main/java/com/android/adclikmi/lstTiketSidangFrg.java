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

import com.android.adclikmi.Model.mataKuliah;
import com.android.adclikmi.Model.tiketIzin;
import com.android.adclikmi.Model.tiketSidang;
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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class lstTiketSidangFrg extends Fragment {
    private Spinner kelas, semester;
    public String title="Daftar Pengajuan Sidang", query="";
    private RecyclerView recyclerView;
    private tiketSidangAdapter adapter;
    private ArrayList<tiketSidang> arrayList;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public lstTiketSidangFrg() {
        // Required empty public constructor
    }

    public tiketSidangAdapter getAdapter() {
        return adapter;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.lst_tiket_sidang, container, false);

        AndroidNetworking.initialize(view.getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new tiketSidangAdapter(arrayList, getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        if(((MainActivity)getActivity()).getUs().getLevel()==0){
            loadData();
        }else if(((MainActivity)getActivity()).getUs().getLevel()==3 || ((MainActivity)getActivity()).getUs().getLevel()==2){
            loadDataDsb();
        }

        ((MainActivity) getActivity()).setTitle(title);
        ((MainActivity) getActivity()).setSearchState(4);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        kelas=(Spinner)view.findViewById(R.id.frm_kelas_spinner);
        semester=(Spinner)view.findViewById(R.id.frm_semester_spinner);

        List<String> list = new ArrayList<String>();
        list.add(" ");
        list.add("Regular");
        list.add("Malam");

        List<String> list2 = new ArrayList<String>();
        list2.add("");
        list2.add("Semester Ganjil Tahun 2018/2019");

        addDataSpinner(view,list,kelas);
        addDataSpinner(view,list2,semester);

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

        semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSemester(semester.getSelectedItem().toString());
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
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"tpsi")
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
                                    arrayList.add(new tiketSidang(obj.getString("No_Tiket"),
                                            sdf.parse(obj.getString("Tgl_Transaksi")),
                                            obj.getString("NPM"),
                                            obj.getString("Nama_Lkp"),
                                            obj.getString("NIK_Dosen"),
                                            obj.getInt("Kelas"),
                                            obj.getInt("Jenis_Tiket"),
                                            obj.getInt("Stat"),
                                            obj.getString("Ttl"),
                                            obj.getString("Alamat"),
                                            obj.getString("Kd_Pos"),
                                            obj.getString("No_Telp"),
                                            obj.getString("No_HP"),
                                            obj.getString("Email"),
                                            obj.getString("Jenjang"),
                                            obj.getString("Jurusan"),
                                            obj.getString("Bidang"),
                                            obj.getString("Judul_TA"),
                                            obj.getString("Nama_Prs"),
                                            obj.getString("Alamat_Prs"),
                                            obj.getString("Pembimbing"),
                                            obj.getString("Ko_Pembimbing"),
                                            obj.getString("Tahun_Sem"),
                                            obj.getString("Lmp_Kwitansi")
                                    ));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        adapter = new tiketSidangAdapter(arrayList,getContext());
                        recyclerView.setAdapter(adapter);
                    }
                    @Override
                    public void onError(ANError error) {
                        ((MainActivity)getActivity()).dismissDProgressDialog();
                        // handle error
                    }
                });
    }

    public void loadDataDsb(){
        arrayList = new ArrayList<>();
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"tpsi/d/{tpsi}")
                .addPathParameter("tpsi", ((MainActivity)getActivity()).getUs().getUsername())
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
                                    arrayList.add(new tiketSidang(obj.getString("No_Tiket"),
                                            sdf.parse(obj.getString("Tgl_Transaksi")),
                                            obj.getString("NPM"),
                                            obj.getString("Nama_Lkp"),
                                            obj.getString("NIK_Dosen"),
                                            obj.getInt("Kelas"),
                                            obj.getInt("Jenis_Tiket"),
                                            obj.getInt("Stat"),
                                            obj.getString("Ttl"),
                                            obj.getString("Alamat"),
                                            obj.getString("Kd_Pos"),
                                            obj.getString("No_Telp"),
                                            obj.getString("No_HP"),
                                            obj.getString("Email"),
                                            obj.getString("Jenjang"),
                                            obj.getString("Jurusan"),
                                            obj.getString("Bidang"),
                                            obj.getString("Judul_TA"),
                                            obj.getString("Nama_Prs"),
                                            obj.getString("Alamat_Prs"),
                                            obj.getString("Pembimbing"),
                                            obj.getString("Ko_Pembimbing"),
                                            obj.getString("Tahun_Sem"),
                                            obj.getString("Lmp_Kwitansi")
                                    ));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        adapter = new tiketSidangAdapter(arrayList,getContext());
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

class tiketSidangAdapter extends RecyclerView.Adapter<tiketSidangAdapter.tiketSidangViewHolder> {
    private int kelas=0;
    private String semester="";
    private ArrayList<tiketSidang> dataList;
    private ArrayList<tiketSidang> dataListFiltered;
    private Context context;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public tiketSidangAdapter(ArrayList<tiketSidang> dataList, Context context) {
        this.dataList = dataList;
        this.dataListFiltered=dataList;
        this.context=context;
    }

    public void setKelas(int kelas) {
        this.kelas = kelas;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Override
    public tiketSidangViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tiket_sidang_item, parent, false);
        return new tiketSidangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(tiketSidangViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tiketSidang item = dataListFiltered.get(position);
                changeLayout(item);
            }
        });
        holder.noTiket.setText(dataListFiltered.get(position).getNoTiket());
        holder.tglTrans.setText(sdf.format(dataListFiltered.get(position).getTglTrans()));
        holder.NPM.setText(dataListFiltered.get(position).getNPM());
        holder.nama.setText(dataListFiltered.get(position).getNama());
        holder.judulTA.setText(dataListFiltered.get(position).getJudulTA());
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();
                Boolean kls, que, sme;

                ArrayList<tiketSidang> filtered = new ArrayList<>();

                if (query.isEmpty() && kelas==0 && semester.isEmpty()) {
                    filtered = dataList;
                } else {
                    for (tiketSidang tiket : dataList) {
                        if(kelas==0 || kelas==tiket.getKelas()) kls=true; else kls=false;

                        if(semester.isEmpty() || semester.substring(9,semester.length()-16).equalsIgnoreCase(tiket.getTahunSem().substring(9,tiket.getTahunSem().length()-16))
                                && semester.substring(semester.length()-9).equalsIgnoreCase(tiket.getTahunSem().substring(tiket.getTahunSem().length()-9))) sme=true; else sme=false;

                        if (tiket.getNoTiket().toLowerCase().contains(query.toLowerCase())
                                || tiket.getNPM().toLowerCase().contains(query.toLowerCase())
                                || tiket.getNama().toLowerCase().contains(query.toLowerCase()))
                            que=true; else que=false;

                        if(kls && que && sme) filtered.add(tiket);
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                dataListFiltered = (ArrayList<tiketSidang>) results.values;
                notifyDataSetChanged();//memberi tahu bahwa data pada list berubah
            }
        };
    }

    public void changeLayout(tiketSidang item){
        Fragment fragment=new penangananPrmhnSidangFrg();
        Bundle bundle = new Bundle();
        bundle.putSerializable("tiket", item);
        fragment.setArguments(bundle);
        ((MainActivity)context).changeLayout(fragment);
    }

    @Override
    public int getItemCount() {
        return (dataListFiltered != null) ? dataListFiltered.size() : 0;
    }

    public class tiketSidangViewHolder extends RecyclerView.ViewHolder{
        private TextView noTiket, tglTrans, NPM, nama, judulTA;

        public tiketSidangViewHolder(View itemView) {
            super(itemView);
            noTiket = (TextView) itemView.findViewById(R.id.tkt_notiket);
            tglTrans = (TextView) itemView.findViewById(R.id.tkt_tgl_trans);
            NPM = (TextView) itemView.findViewById(R.id.tkt_npm);
            nama = (TextView) itemView.findViewById(R.id.tkt_nama);
            judulTA = (TextView) itemView.findViewById(R.id.tkt_judul_TA);
        }
    }

}



