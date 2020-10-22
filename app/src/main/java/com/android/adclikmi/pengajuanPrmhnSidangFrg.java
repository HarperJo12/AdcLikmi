package com.android.adclikmi;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

import com.android.adclikmi.Model.tiketSidang;
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
public class pengajuanPrmhnSidangFrg extends Fragment implements EasyPermissions.PermissionCallbacks {
    public String title="Pengajuan Permohonan Sidang";
    private static final int REQUEST_FILE_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private Spinner spnSemester, spnPembimbing, spnKoPembimbing;
    private EditText ttl, alamat, kodePos, noTelp,  noHP, email, judulTA, namaPrs, alamatPrs, lampiranTA,lampiranKwitansi;
    private TextView tglTrans, noTiket, npm, nama, jenjang, jurusan, bidang, fileNamekwi;
    private ImageView imgViewkwi;
    private Button next, browsekwi, uploadkwi;
    Uri fileUri;
    private File filekwi, file, filedst;
    private uploadHelper hUpload;
    private String lmpkwi=null;
    private int modeLmp=0;
    private tiketSidang tkt;
    private ArrayList<String> nik;
    private List<String> list1,list2,list3;
    private int kelas=0;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");

    public pengajuanPrmhnSidangFrg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.pengajuan_prmhn_sidang, parent, false);
        Calendar c = Calendar.getInstance();

        AndroidNetworking.initialize(view.getContext());

        ((MainActivity) getActivity()).setTitle(title);
        tglTrans=(TextView)view.findViewById(R.id.frm_tgl_trans);
        noTiket=(TextView) view.findViewById(R.id.frm_no_tiket);
        npm=(TextView) view.findViewById(R.id.frm_npm);
        nama=(TextView) view.findViewById(R.id.frm_nama_lengkap);
        ttl=(EditText) view.findViewById(R.id.frm_edit_ttl);
        alamat=(EditText) view.findViewById(R.id.frm_edit_alamat);
        kodePos=(EditText) view.findViewById(R.id.frm_edit_kdps);
        noTelp=(EditText) view.findViewById(R.id.frm_edit_notel);
        noHP=(EditText) view.findViewById(R.id.frm_edit_hp);
        email=(EditText) view.findViewById(R.id.frm_edit_eml);
        jenjang=(TextView) view.findViewById(R.id.frm_program);
        jurusan=(TextView) view.findViewById(R.id.frm_jurusan);
        bidang=(TextView) view.findViewById(R.id.frm_bidang_minat);
        judulTA=(EditText) view.findViewById(R.id.frm_edit_jdta);
        namaPrs=(EditText) view.findViewById(R.id.frm_edit_nmprs);
        alamatPrs=(EditText) view.findViewById(R.id.frm_edit_almprs);
        spnSemester = (Spinner) view.findViewById(R.id.frm_semester_spinner);
        spnPembimbing= (Spinner) view.findViewById(R.id.frm_pembimbing_spinner);
        spnKoPembimbing= (Spinner) view.findViewById(R.id.frm_kopembimbing_spinner);

        next=(Button) view.findViewById(R.id.frm_next);
        browsekwi=(Button) view.findViewById(R.id.frm_browsekwi);
        uploadkwi=(Button) view.findViewById(R.id.frm_uploadkwi);

        imgViewkwi=(ImageView) view.findViewById(R.id.frm_imgViewkwi);
        fileNamekwi=(TextView)view.findViewById(R.id.frm_nameFilekwi);

        hUpload=new uploadHelper(getContext());

        tglTrans.setText(sdf2.format(c.getTime()));

        loadMhs();
        loadDsb(view);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getValidation(true, false,judulTA,"Judul Tugas Akhir")
                && getValidation(true, false,ttl,"Tempat, Tanggal Lahir")
                && getValidation(true, false,alamat,"Alamat")
                && getValidation(true, false,kodePos,"Kode Pos")
                && getValidation(true, false,noHP,"Nomor Handphone")
                && getValidation(true, false,email,"Email")
                && getValidationLmp(lmpkwi, "bukti kwitansi pelunasan biaya sidang")){
                Fragment fragment= new pengajuanKetSidangFrg();
                Bundle bundle = new Bundle();
                setTiket();
                bundle.putSerializable("tiket", tkt);
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).changeLayout(fragment);
                }
            }
        });

        ttl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getValidation(false, hasFocus,ttl,"Tempat, Tanggal Lahir");
            }
        });

        alamat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getValidation(false, hasFocus,alamat,"Alamat");
            }
        });

        kodePos.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getValidation(false, hasFocus,kodePos,"Kode Pos");
            }
        });

        noHP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getValidation(false, hasFocus,noHP,"Nomor Handphone");
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getValidation(false, hasFocus,email,"Email");
            }
        });

        judulTA.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getValidation(false, hasFocus,judulTA,"Judul Tugas Akhir");
            }
        });

        setHasOptionsMenu(true);

        list1 = new ArrayList<String>();
        list1.add("Semester Ganjil Tahun 2018/2019");

        addData(view, list1, spnSemester);

        imgViewkwi.setVisibility(View.GONE);
        fileNamekwi.setVisibility(View.GONE);

        browsekwi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if app has permission to access the external storage.
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if(EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                        showFileChooserIntent( "image/*");
                    }else {
                        EasyPermissions.requestPermissions(getActivity(), getString(R.string.write_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                } else {
                    EasyPermissions.requestPermissions(getActivity(), getString(R.string.read_file), REQUEST_FILE_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        uploadkwi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filekwi != null) {
                    if (lmpkwi ==null) {
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
        return  view;
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
            imgViewkwi.setVisibility(View.VISIBLE);
            fileNamekwi.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, getActivity());
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        showFileChooserIntent("image/*");
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

        fileNamekwi.setText(file.getName());

        /*
         * this checks to see if there are any previous test photo files
         * if there are any photos, they are deleted for the sake of
         * memory
         */
        if (filedst.exists()) {
            File[] dirFiles = filedst.listFiles();
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

            filekwi = filedst;
            imgViewkwi.setImageBitmap(bitmap);
        } else {
            filekwi = file;
            imgViewkwi.setImageResource(R.drawable.ic_action_file);
        }
    }

    /**
     * Shows an intent which has options from which user can choose the file like File manager, Gallery etc
     */
    public void showFileChooserIntent( String type) {
        Intent fileManagerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileManagerIntent.setType(type);
        startActivityForResult(fileManagerIntent, REQUEST_FILE_CODE);
    }

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

    public int transKelas(String kls){
        int i=0;
        if(kls.equalsIgnoreCase("Reguler")) i=1;
        else if(kls.equalsIgnoreCase("Malam")) i=2;
        else if(kls.equalsIgnoreCase("Eksekutif")) i=3;
        return i;
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
                            kelas=transKelas(response.getString("Kelas"));
                            npm.setText(response.getString("NPM"));
                            nama.setText(response.getString("Nama_Lkp"));
                            jenjang.setText(response.getString("Jenjang"));
                            jurusan.setText(response.getString("Jurusan"));
                            bidang.setText(response.getString("Bidang"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    public void loadDsb(final View vw){
        nik = new ArrayList<String>();
        list2 = new ArrayList<String>();
        list3 = new ArrayList<String>();
        list3.add("");
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"dsn")
                .addHeaders("Authorization", "Bearer "+((MainActivity)getActivity()).getUs().getToken().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ((MainActivity)getActivity()).dismissDProgressDialog();
                        for(int i=0; i<response.length();i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);

                                nik.add(obj.getString("NIK"));
                                list2.add(obj.getString("Nama_Lkp"));
                                list3.add(obj.getString("Nama_Lkp"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        addData(vw, list2, spnPembimbing);
                        addData(vw, list3, spnKoPembimbing);
                    }
                    @Override
                    public void onError(ANError error) {
                        ((MainActivity)getActivity()).dismissDProgressDialog();
                        Toast.makeText(getContext(), "Pastikan koneksi internet berjalan!", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).changeLayoutMain();
                    }
                });
    }

    public void uploadData() {
        File file = null;
        file = filekwi;
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
                                lmpkwi = response.getString("name");
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

    public void setTiket(){
        try {
            tkt=new tiketSidang("0",sdf.parse(tglTrans.getText().toString()),npm.getText().toString(), nama.getText().toString(), nik.get(spnPembimbing.getSelectedItemPosition()),
                    kelas,4,0, ttl.getText().toString(),alamat.getText().toString(), kodePos.getText().toString(), noTelp.getText().toString(), noHP.getText().toString(),
                    email.getText().toString(), jenjang.getText().toString(),jurusan.getText().toString(),bidang.getText().toString(), judulTA.getText().toString(),
                    namaPrs.getText().toString(), alamatPrs.getText().toString(),spnPembimbing.getSelectedItem().toString(),spnKoPembimbing.getSelectedItem().toString(),
                    spnSemester.getSelectedItem().toString(),lmpkwi);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void addData(View view, List lst, Spinner spn){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_item, lst);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(dataAdapter);
    }

}
