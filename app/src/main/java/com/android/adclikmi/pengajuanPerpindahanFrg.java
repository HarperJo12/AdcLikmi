package com.android.adclikmi;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class pengajuanPerpindahanFrg extends Fragment implements EasyPermissions.PermissionCallbacks {
    public String title="Pengajuan Perpindahan";
    private static final int REQUEST_FILE_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private Spinner spnSemester, spnKelas, spnJenjang, spnJurusan, spnBidang;
    private EditText keterangan;
    private ImageView imgViewskot, imgViewskk;
    private Button submit, cancel, browseskot, uploadskot, browseskk, uploadskk;
    Uri fileUri;
    private File fileskot, fileskk, file, filedst;
    private uploadHelper hUpload;
    private TextView tglTrans,klsAwal,jjgAwal,jrsAwal,bdgAwal,fileNameskot, fileNameskk;
    private String lmpskot=null, lmpskk=null, kjrs="";
    private int modeLmp=0;
    private boolean browsed=false;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");

    public pengajuanPerpindahanFrg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.pengajuan_perpindahan, parent, false);
        Calendar c = Calendar.getInstance();

        AndroidNetworking.initialize(view.getContext());

        ((MainActivity) getActivity()).setTitle(title);
        tglTrans=(TextView)view.findViewById(R.id.frm_tgl_trans);
        klsAwal=(TextView)view.findViewById(R.id.frm_kelas_awal);
        jjgAwal=(TextView)view.findViewById(R.id.frm_jenjang_awal);
        jrsAwal=(TextView)view.findViewById(R.id.frm_jurusan_awal);
        bdgAwal=(TextView)view.findViewById(R.id.frm_bidang_awal);
        fileNameskot=(TextView)view.findViewById(R.id.frm_nameFileSKOT);
        fileNameskk=(TextView)view.findViewById(R.id.frm_nameFileSKK);

        loadMhs();
        loadKjr();

        spnSemester = (Spinner) view.findViewById(R.id.frm_semester_spinner);
        spnKelas= (Spinner) view.findViewById(R.id.frm_kelas_akhir_spinner);
        spnJenjang= (Spinner) view.findViewById(R.id.frm_jenjang_akhir_spinner);
        spnJurusan= (Spinner) view.findViewById(R.id.frm_jurusan_akhir_spinner);
        spnBidang= (Spinner) view.findViewById(R.id.frm_bidang_akhir_spinner);
        submit=(Button) view.findViewById(R.id.frm_submit);
        cancel=(Button) view.findViewById(R.id.frm_cancel);
        browseskot=(Button) view.findViewById(R.id.frm_browseSKOT);
        uploadskot=(Button) view.findViewById(R.id.frm_uploadSKOT);
        browseskk=(Button) view.findViewById(R.id.frm_browseSKK);
        uploadskk=(Button) view.findViewById(R.id.frm_uploadSKK);

        imgViewskot=(ImageView) view.findViewById(R.id.frm_imgViewSKOT);
        imgViewskk=(ImageView) view.findViewById(R.id.frm_imgViewSKK);

        keterangan=(EditText) view.findViewById(R.id.frm_edit_ket);
        keterangan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getValidation(false, hasFocus,keterangan,"Keterangan");
            }
        });

        hUpload=new uploadHelper(getContext());

        tglTrans.setText(sdf2.format(c.getTime()));
        setHasOptionsMenu(true);

        List<String> list1 = new ArrayList<String>();
        list1.add("Semester Ganjil Tahun 2018/2019");
        list1.add("Semester Genap Tahun 2018/2019");

        List<String> list2 = new ArrayList<String>();
        list2.add("Reguler");
        list2.add("Malam");
        list2.add("Eksekutif");

        List<String> list3 = new ArrayList<String>();
        list3.add("Diploma III (D3)");
        list3.add("Sarjana (S1)");
        list3.add("Magister (S2)");

        List<String> list4 = new ArrayList<String>();
        list4.add("Manajemen Informatika");
        list4.add("Teknik Informatika");

        List<String> list5 = new ArrayList<String>();
        list5.add("Manajemen Informatika (D3/S1)");
        list5.add("Komputerisasi Akuntansi (D3/S1)");
        list5.add("Manajemen Bisnis (D3/S1)");
        list5.add("ITPreneurship (D3/S1)");
        list5.add("Rekayasa Perangkat Lunak (S1)");
        list5.add("Grafis dan Multimedia (S1)");
        list5.add("Perangkat Lunak Sistem & Jaringan (S1)");
        list5.add("Information Technology (RMIT) (S1)");
        list5.add("Sistem Informasi Bisnis (S2)");
        list5.add("Rekayasa Sistem Informasi (S2)");

        addData(view, list1, spnSemester);
        addData(view, list2, spnKelas);
        addData(view, list3, spnJenjang);
        addData(view, list4, spnJurusan);
        addData(view, list5, spnBidang);

        imgViewskot.setVisibility(View.GONE);
        fileNameskot.setVisibility(View.GONE);
        imgViewskk.setVisibility(View.GONE);
        fileNameskk.setVisibility(View.GONE);

        browseskot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if app has permission to access the external storage.
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if(EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                        modeLmp=0;
                        showFileChooserIntent("application/pdf");
                    }else {
                        EasyPermissions.requestPermissions(getActivity(), getString(R.string.write_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(getActivity(), getString(R.string.read_file), REQUEST_FILE_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        uploadskot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileskot != null) {
                    if (lmpskot ==null) {
                        modeLmp=0;
                        uploadData();
                        ((MainActivity) getActivity()).showPercentProgressBar();
                    }else {
                        Toast.makeText(getContext(),"File/image has been uploaded", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(),"Please select a file first", Toast.LENGTH_LONG).show();
                }
            }
        });

        browseskk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if app has permission to access the external storage.
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if(EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                        modeLmp=1;
                        showFileChooserIntent("application/pdf");
                    }else {
                        EasyPermissions.requestPermissions(getActivity(), getString(R.string.write_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(getActivity(), getString(R.string.read_file), REQUEST_FILE_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        uploadskk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileskk != null) {
                    if (lmpskk ==null) {
                        modeLmp=1;
                        uploadData();
                        ((MainActivity) getActivity()).showPercentProgressBar();
                    }else {
                        Toast.makeText(getContext(),"File/image has been uploaded", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(),"Please select a file first", Toast.LENGTH_LONG).show();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getValidation(true, false,keterangan,"Keterangan")){
                    if(klsAwal.getText().toString().trim().equalsIgnoreCase("Reguler")
                            && spnKelas.getSelectedItem().toString().trim().equalsIgnoreCase("Malam")){
                        if(getValidationLmp(lmpskot, "surat keterangan orang tua")
                                && getValidationLmp(lmpskk, "surat keterangan kerja")) {
                            ((MainActivity) getActivity()).hideKeyboard();
                            ((MainActivity) getActivity()).showDProgressDialog();
                            postData();
                        }
                    }else{
                        ((MainActivity) getActivity()).hideKeyboard();
                        ((MainActivity) getActivity()).showDProgressDialog();
                        postData();
                    }
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
        ((MainActivity)getActivity()).dismissDProgressDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK) {
            fileUri = data.getData();
            previewFile(fileUri);
            if (modeLmp == 0) {
                imgViewskot.setVisibility(View.VISIBLE);
                fileNameskot.setVisibility(View.VISIBLE);
            }else{
                imgViewskk.setVisibility(View.VISIBLE);
                fileNameskk.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, getActivity());
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        showFileChooserIntent("application/pdf");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getContext(),"Permission has been denied!",Toast.LENGTH_SHORT).show();
    }

    /**
     * Show the file name and preview once the file is chosen
     * @param uri
     */
    private void previewFile(Uri uri) {
        String filePath = hUpload.getRealPathFromURIPath(uri, getActivity());
        String INTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");
        String dstPath = INTERNAL_STORAGE + "/likmimedia/";
        Log.d("INTERNAL STORAGE", INTERNAL_STORAGE);
        file = new File(filePath);
        filedst = new File(dstPath);

        if(modeLmp==0){
            fileNameskot.setText(file.getName());
        }else{
            fileNameskk.setText(file.getName());
        }

        /*
         * this checks to see if there are any previous test photo files
         * if there are any photos, they are deleted for the sake of
         * memory
         */
        if (filedst.exists() && !browsed) {
            File[] dirFiles = filedst.listFiles();
            browsed=true;
            if (dirFiles.length != 0) {
                for (int i = 0; i < dirFiles.length; i++) {
                    dirFiles[i].delete();
                }
            }
        }
        // if no directory exists, create new directory
        if (!filedst.exists()) {
            filedst.mkdir();
        }

        filedst= new File(dstPath, file.getName());
        try {
            filedst.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Filepath ",filePath);
        Log.d("Filedst ",dstPath);

        ContentResolver cR = getContext().getContentResolver();
        String mime = cR.getType(uri);

        if (mime != null && mime.contains("image")) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 2;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            OutputStream os;
            try {
                os = new BufferedOutputStream(new FileOutputStream(filedst));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }
            if (modeLmp == 0) {
                fileskot=filedst;
                imgViewskot.setImageBitmap(bitmap);
            }else{
                fileskk=filedst;
                imgViewskk.setImageBitmap(bitmap);
            }
        } else {
            if(modeLmp==0){
                fileskot=file;
                imgViewskot.setImageResource(R.drawable.ic_action_file);
            }else{
                fileskk=file;
                imgViewskk.setImageResource(R.drawable.ic_action_file);
            }
        }
    }

    /**
     * Shows an intent which has options from which user can choose the file like File manager, Gallery etc
     */
    public void showFileChooserIntent(String type) {
        Intent fileManagerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        String [] mimeTypes = {"application/pdf","application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        fileManagerIntent.setType("*/*");
        fileManagerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(fileManagerIntent, REQUEST_FILE_CODE);
    }

    /**
     * Returns the actual path of the file in the file system
     *
     */

    public boolean getValidationLmp(String lamp, String obj){
        boolean bool=false;
        if(lamp==null) {
            Toast.makeText(getContext(),"Harap upload " +obj+" pendukung",Toast.LENGTH_SHORT).show();
            bool=false;
        }else{
            bool= true;
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

    public void addData(View view, List lst, Spinner spn){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_item, lst);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(dataAdapter);
    }

    public void loadKjr(){
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"kjrs/{kjrs}")
                .addPathParameter("kjrs", ((MainActivity)getActivity()).getUs().getUsername())
                .addHeaders("Authorization", "Bearer "+((MainActivity)getActivity()).getUs().getToken().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ((MainActivity)getActivity()).dismissDProgressDialog();
                        try {
                            JSONObject obj = response.getJSONObject(0);
                            kjrs = obj.getString("NIK");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        ((MainActivity)getActivity()).dismissDProgressDialog();
                        Toast.makeText(getContext(), "Pastikan koneksi internet berjalan!"+error.getMessage(), Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).changeLayoutMain();
                        // handle error
                    }
                });
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
                            klsAwal.setText(response.getString("Kelas"));
                            jjgAwal.setText(response.getString("Jenjang"));
                            jrsAwal.setText(response.getString("Jurusan"));
                            bdgAwal.setText(response.getString("Bidang"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        ((MainActivity)getActivity()).dismissDProgressDialog();
                        Toast.makeText(getContext(), "Pastikan koneksi internet berjalan!", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).changeLayoutMain();
                        // handle error
                    }
                });
    }

    public int transKelas(String kls){
        int i=0;
        if(kls.equalsIgnoreCase("Reguler")) i=1;
        else if(kls.equalsIgnoreCase("Malam")) i=2;
        else if(kls.equalsIgnoreCase("Eksekutif")) i=3;
        return i;
    }

    public void uploadData() {
        File file = null;
        if(modeLmp==0) file=fileskot;
        else file = fileskk;
        AndroidNetworking.upload(((MainActivity)getActivity()).getBaseUrl()+"upload/{npm}")
                .addHeaders("Authorization", "Bearer "+((MainActivity)getActivity()).getUs().getToken().trim())
                .addMultipartFile("data",file)
                .addPathParameter("npm", ((MainActivity)getActivity()).getUs().getUsername())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        if(bytesUploaded>=0) {
                            ((MainActivity)getActivity()).setPercentProgressBar(bytesUploaded,totalBytes);
                        }
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((MainActivity)getActivity()).dismissPercentProgressBar();
                        try {
                            if(response.getBoolean("sukses")) {
                                Toast.makeText(getContext(), "Data berhasil diupload!", Toast.LENGTH_SHORT).show();
                                if(modeLmp==0)
                                    lmpskot=response.getString("name");
                                else
                                    lmpskk=response.getString("name");
                            }else
                                Toast.makeText(getContext(),"Data gagal diupload!",Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        ((MainActivity)getActivity()).dismissPercentProgressBar();
                        // handle error
                    }
                });
    }

    public void postData() {
        try {
            JSONObject obj = new JSONObject();

            obj.put("Tgl_Transaksi",sdf.format(sdf2.parse(tglTrans.getText().toString())));
            obj.put("Jenis_Tiket",3);
            obj.put("Stat",0);
            obj.put("Kelas", transKelas(klsAwal.getText().toString()));
            obj.put("NPM",((MainActivity)getActivity()).getUs().getUsername());
            obj.put("NIK_Cs",null);
            obj.put("NIK_Dosen",kjrs);
            obj.put("Kls_Awal",klsAwal.getText().toString());
            obj.put("Kls_Akhir",spnKelas.getSelectedItem().toString());
            obj.put("Jjg_Awal",jjgAwal.getText().toString());
            obj.put("Jjg_Akhir",spnJenjang.getSelectedItem().toString());
            obj.put("Jrs_Awal",jrsAwal.getText().toString());
            obj.put("Jrs_Akhir",spnJurusan.getSelectedItem().toString());
            obj.put("Bdg_Awal",bdgAwal.getText().toString());
            obj.put("Bdg_Akhir",spnBidang.getSelectedItem().toString());
            obj.put("Tahun_Sem",spnSemester.getSelectedItem().toString());
            obj.put("Keterangan",keterangan.getText().toString());
            obj.put("Lampiran1",lmpskot);
            obj.put("Lampiran2",lmpskk);

            AndroidNetworking.post(((MainActivity)getActivity()).getBaseUrl()+"tper")
                    .addJSONObjectBody(obj)
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
                                    ((MainActivity)getActivity()).changeLayoutMain();
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
