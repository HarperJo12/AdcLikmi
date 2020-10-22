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
import com.android.adclikmi.Model.tiketUjian;
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
public class lstTiketUjianFrg extends Fragment {
    private Spinner kelas, jenis, semester;
    public String title="Daftar Pengajuan Ujian Susulan", query="";
    private RecyclerView recyclerView;
    private tiketUjianAdapter adapter;
    private ArrayList<tiketUjian> arrayList;
    private ArrayList<mataKuliah> matkul;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public lstTiketUjianFrg() {
        // Required empty public constructor
    }

    public tiketUjianAdapter getAdapter() {
        return adapter;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.lst_tiket_ujian, container, false);
        AndroidNetworking.initialize(view.getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new tiketUjianAdapter(arrayList,getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        loadData();

        ((MainActivity) getActivity()).setTitle(title);
        ((MainActivity) getActivity()).setSearchState(2);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        kelas=(Spinner)view.findViewById(R.id.frm_kelas_spinner);
        jenis=(Spinner)view.findViewById(R.id.frm_jenis_spinner);
        semester=(Spinner)view.findViewById(R.id.frm_semester_spinner);

        List<String> list = new ArrayList<String>();//menambahkan data untuk spinner
        list.add(" ");
        list.add("Regular");
        list.add("Malam");

        List<String> list2 = new ArrayList<String>();
        list2.add("");
        list2.add("UTS");
        list2.add("UAS");

        List<String> list3 = new ArrayList<String>();
        list3.add("");
        list3.add("Semester Ganjil Tahun 2018/2019");
        list3.add("Semester Genap Tahun 2018/2019");

        addDataSpinner(view,list,kelas);
        addDataSpinner(view,list2,jenis);
        addDataSpinner(view,list3,semester);

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
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"tpuj")
                .addHeaders("Authorization", "Bearer "+((MainActivity)getActivity()).getUs().getToken().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ((MainActivity) getActivity()).dismissDProgressDialog();
                        if (!response.isNull(0)) {
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

                                    arrayList.add(new tiketUjian(obj.getString("No_Tiket"),
                                            sdf.parse(obj.getString("Tgl_Transaksi")),
                                            obj.getString("NPM"),
                                            obj.getString("Nama_Lkp"),
                                            obj.getInt("Kelas"),
                                            obj.getInt("Jenis_Tiket"),
                                            obj.getInt("Stat"),
                                            obj.getString("Jenis"),
                                            obj.getString("Tahun_Sem"),
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
                        adapter = new tiketUjianAdapter(arrayList, getContext());
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

class tiketUjianAdapter extends RecyclerView.Adapter<tiketUjianAdapter.tiketUjianViewHolder> {
    private int kelas=0;
    private String jenis="", semester="";
    private ArrayList<tiketUjian> dataList;
    private ArrayList<tiketUjian> dataListFiltered;
    private Context context;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public tiketUjianAdapter(ArrayList<tiketUjian> dataList, Context context) {
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

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Override
    public tiketUjianViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tiket_ujian_item, parent, false);
        return new tiketUjianViewHolder(view);
    }

    @Override
    public void onBindViewHolder(tiketUjianViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tiketUjian item = dataListFiltered.get(position);
                changeLayout(item);
            }
        });
        holder.noTiket.setText(dataListFiltered.get(position).getNoTiket());
        holder.tglTrans.setText(sdf.format(dataListFiltered.get(position).getTglTrans()));
        holder.NPM.setText(dataListFiltered.get(position).getNPM());
        holder.nama.setText(dataListFiltered.get(position).getNama());
        holder.jenis.setText(dataListFiltered.get(position).getJenis());
        holder.thnSem.setText(dataListFiltered.get(position).getThnSem());
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();
                Boolean kls=true, que=true, jns=true, sme=true;

                ArrayList<tiketUjian> filtered = new ArrayList<>();

                if (query.isEmpty() && kelas==0 && jenis.isEmpty() && semester.isEmpty()) {
                    filtered = dataList;
                } else {
                    for (tiketUjian tiket : dataList) {
                        if(kelas==0 || kelas==tiket.getKelas()) kls=true; else kls=false;

                        if(jenis.isEmpty() || jenis.equalsIgnoreCase(tiket.getJenis())) jns= true; else jns=false;

                        if(semester.isEmpty() || semester.substring(9,semester.length()-16).equalsIgnoreCase(tiket.getThnSem().substring(9,tiket.getThnSem().length()-16))
                                && semester.substring(semester.length()-9).equalsIgnoreCase(tiket.getThnSem().substring(tiket.getThnSem().length()-9))) sme=true; else sme=false;

                        if (tiket.getNoTiket().toLowerCase().contains(query.toLowerCase())
                                || tiket.getNPM().toLowerCase().contains(query.toLowerCase())
                                || tiket.getNama().toLowerCase().contains(query.toLowerCase()))
                            que=true; else que=false;

                        if(kls && que && jns && sme) filtered.add(tiket);
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                dataListFiltered = (ArrayList<tiketUjian>) results.values;
                notifyDataSetChanged();//memberi tahu bahwa data pada list berubah
            }
        };
    }

    public void changeLayout(tiketUjian item){
        Fragment fragment=new penangananUjianFrg();
        Bundle bundle = new Bundle();
        bundle.putSerializable("tiket", item);
        fragment.setArguments(bundle);
        ((MainActivity)context).changeLayout(fragment);
    }

    @Override
    public int getItemCount() {
        return (dataListFiltered != null) ? dataListFiltered.size() : 0;
    }

    public class tiketUjianViewHolder extends RecyclerView.ViewHolder{
        private TextView noTiket, tglTrans, NPM, nama, jenis, thnSem;

        public tiketUjianViewHolder(View itemView) {
            super(itemView);
            noTiket = (TextView) itemView.findViewById(R.id.tkt_notiket);
            tglTrans = (TextView) itemView.findViewById(R.id.tkt_tgl_trans);
            NPM = (TextView) itemView.findViewById(R.id.tkt_npm);
            nama = (TextView) itemView.findViewById(R.id.tkt_nama);
            jenis = (TextView) itemView.findViewById(R.id.tkt_jenis);
            thnSem = (TextView) itemView.findViewById(R.id.tkt_thn_sem);
        }
    }

}
