package com.android.adclikmi;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.adclikmi.Model.mataKuliah;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class pengajuanIzinFrg extends Fragment implements EasyPermissions.PermissionCallbacks, DatePickerDialog.OnDateSetListener {
    public String title = "Pengajuan Izin";
    private static final int REQUEST_FILE_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private Spinner spinner1;
    private EditText keterangan;
    private TextView dtpAwal, dtpAkhir, tglTrans, fileName;
    private ImageView imgView;
    private Button submit, cancel, browse, upload;
    Uri fileUri;
    private File file, filedst;
    private uploadHelper hUpload;
    private TableLayout mTableLayout;
    private TableRow head;
    private mataKuliahTable mt;
    private ArrayList<mataKuliah> matkul;
    private ArrayList<mataKuliah> sMatkul;
    private String lmp = null;
    private int dateAct = 0, kelas = 0;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdf4 = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat sdf5 = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");

    public pengajuanIzinFrg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.pengajuan_izin, parent, false);
        Calendar c = Calendar.getInstance();

        AndroidNetworking.initialize(view.getContext());

        ((MainActivity) getActivity()).setTitle(title);
        tglTrans = (TextView) view.findViewById(R.id.frm_tgl_trans);
        dtpAwal = (TextView) view.findViewById(R.id.dtpAwal);
        dtpAkhir = (TextView) view.findViewById(R.id.dtpAkhir);
        fileName = (TextView) view.findViewById(R.id.frm_nameFile);

        submit = (Button) view.findViewById(R.id.frm_submit);
        cancel = (Button) view.findViewById(R.id.frm_cancel);
        browse = (Button) view.findViewById(R.id.frm_browse);
        upload = (Button) view.findViewById(R.id.frm_upload);

        imgView = (ImageView) view.findViewById(R.id.frm_imgView);

        head = (TableRow) view.findViewById(R.id.frm_header_matkul);
        mTableLayout = (TableLayout) view.findViewById(R.id.frm_data_matkul);

        spinner1 = (Spinner) view.findViewById(R.id.frm_jenis_spinner);

        hUpload = new uploadHelper(getContext());
        loadMhs();
        setHasOptionsMenu(true);

        keterangan = (EditText) view.findViewById(R.id.frm_edit_ket);

        dtpAwal.setText(sdf2.format(c.getTime()));
        dtpAkhir.setText(sdf2.format(c.getTime()));
        tglTrans.setText(sdf2.format(c.getTime()));

        dtpAwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = dtpAwal.getText().toString();
                int dtpy, dtpm, dtpd;
                dtpy = Integer.parseInt(date.substring(6, 10));
                dtpm = Integer.parseInt(date.substring(3, 5)) - 1;
                dtpd = Integer.parseInt(date.substring(0, 2));
                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), pengajuanIzinFrg.this, dtpy, dtpm, dtpd);
                datePickerDialog.setTitle("Tanggal Awal");
                dateAct = 0;
                datePickerDialog.show();
            }
        });

        dtpAkhir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String date = dtpAkhir.getText().toString();
                int dtpy, dtpm, dtpd;
                dtpy = Integer.parseInt(date.substring(6, 10));
                dtpm = Integer.parseInt(date.substring(3, 5)) - 1;
                dtpd = Integer.parseInt(date.substring(0, 2));
                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), pengajuanIzinFrg.this, dtpy, dtpm, dtpd);
                datePickerDialog.setTitle("Tanggal Akhir");
                dateAct = 1;
                datePickerDialog.show();
            }
        });

        keterangan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getValidation(false, hasFocus, keterangan, "Keterangan");
            }
        });

        addData(view);

        imgView.setVisibility(View.GONE);
        fileName.setVisibility(View.GONE);

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if app has permission to access the external storage.
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showFileChooserIntent("image/*");
                    } else {
                        EasyPermissions.requestPermissions(getActivity(), getString(R.string.write_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                } else {
                    EasyPermissions.requestPermissions(getActivity(), getString(R.string.read_file), REQUEST_FILE_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file != null) {
                    if (lmp == null) {
                        uploadData();
                        ((MainActivity) getActivity()).showPercentProgressBar();
                    } else {
                        Toast.makeText(getContext(), "File/image has been uploaded", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please select a file first", Toast.LENGTH_LONG).show();
                }
            }
        });


        matkul = new ArrayList<>();

        mt = new mataKuliahTable(getContext(), mTableLayout, head, matkul, 1);
        mt.loadTable();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMatkul = new ArrayList<>();
                sMatkul = mt.getMatkulSelected();
                if (getValidation(true, false, keterangan, "Keterangan")
                        && getValidationMk(sMatkul)
                        && getValidationLmp(lmp, "file/foto")) {
                    ((MainActivity) getActivity()).hideKeyboard();
                    ((MainActivity) getActivity()).showDProgressDialog();
                    postData();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeLayoutMain();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AndroidNetworking.cancelAll();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK && data.getData() != null) {
            fileUri = data.getData();
            previewFile(fileUri);
            imgView.setVisibility(View.VISIBLE);
            fileName.setVisibility(View.VISIBLE);
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
        Toast.makeText(getContext(), "Permission has been denied!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Show the file name and preview once the file is chosen
     *
     * @param uri
     */
    private void previewFile(Uri uri) {
        String filePath = hUpload.getRealPathFromURIPath(uri, getActivity()).trim();
        String INTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");
        String dstPath = INTERNAL_STORAGE + "/likmimedia/";
        Log.d("INTERNAL STORAGE", INTERNAL_STORAGE);
        file = new File(filePath);
        filedst = new File(dstPath);

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

        filedst = new File(dstPath, file.getName());
        try {
            filedst.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Filepath ", filePath);
        Log.d("Filedst ", dstPath);

        fileName.setText(file.getName());

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
            imgView.setImageBitmap(bitmap);
        } else {
            imgView.setImageResource(R.drawable.ic_action_file);
        }
    }

    /**
     * Shows an intent which has options from which user can choose the file like File manager, Gallery etc
     */
    public void showFileChooserIntent(String type) {
        Intent fileManagerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileManagerIntent.setType(type);
        startActivityForResult(fileManagerIntent, REQUEST_FILE_CODE);
    }

    public boolean getValidationLmp(String lamp, String obj) {
        boolean bool = false;
        if (lamp == null) {
            Toast.makeText(getContext(), "Harap upload " + obj + " pendukung", Toast.LENGTH_SHORT).show();
            bool = false;
        } else {
            bool = true;
        }
        return bool;
    }

    public boolean getValidationDate() {
        boolean bool = false;
        try {
            if (sdf2.parse(dtpAkhir.getText().toString()).compareTo(sdf2.parse(dtpAwal.getText().toString())) >= 0) {
                bool = true;
            } else {
                Toast.makeText(getContext(), "Tanggal awal tidak boleh lebih besar dari tanggal akhir!", Toast.LENGTH_SHORT).show();
                bool = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bool;
    }

    public boolean getValidation(boolean submit, boolean hasFocus, EditText text, String alert) {
        boolean bool = false;
        if (!hasFocus) {
            String tmp = text.getText().toString();
            if (tmp.isEmpty()) {
                text.setError(alert + " Tidak Boleh Kosong!");
                if (submit) {
                    ((MainActivity) getActivity()).requestFocus(text);
                }
                bool = false;
            } else {
                text.setError(null);
                bool = true;
            }
        }
        return bool;
    }

    public boolean getValidationMk(ArrayList<mataKuliah> mk) {
        boolean bool = false;
        if (mk.isEmpty()) {
            Toast.makeText(getContext(), "Mata kuliah harus dipilih!", Toast.LENGTH_SHORT).show();
            bool = false;
        } else {
            bool = true;
        }
        return bool;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        try {
            int mon = month + 1;
            switch (dateAct) {
                case 0:
                    dtpAwal.setText(sdf2.format(sdf2.parse(day + "-" + mon + "-" + year)));
                    break;
                case 1:
                    dtpAkhir.setText(sdf2.format(sdf2.parse(day + "-" + mon + "-" + year)));
                    if (getValidationDate()) {
                        ((MainActivity) getActivity()).showDProgressDialog();
                        loadMatkul();
                    }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int transKelas(String kls) {
        int i = 0;
        if (kls.equalsIgnoreCase("Reguler")) i = 1;
        else if (kls.equalsIgnoreCase("Malam")) i = 2;
        else if (kls.equalsIgnoreCase("Eksekutif")) i = 3;
        return i;
    }

    public void loadMhs() {
        AndroidNetworking.get(((MainActivity) getActivity()).getBaseUrl() + "mhs/{mhs}")
                .addPathParameter("mhs", ((MainActivity) getActivity()).getUs().getUsername())
                .addHeaders("Authorization", "Bearer " + ((MainActivity) getActivity()).getUs().getToken().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            kelas = transKelas(response.getString("Kelas"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getContext(), "Pastikan koneksi internet berjalan!", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).changeLayoutMain();
                    }
                });
    }

    public void addData(View view) {
        List<String> list = new ArrayList<String>();//menambahkan data untuk spinner
        list.add("Sakit");
        list.add("Izin");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
    }

    public void uploadData() {
        AndroidNetworking.upload(((MainActivity) getActivity()).getBaseUrl() + "upload/{npm}")
                .addHeaders("Authorization", "Bearer " + ((MainActivity) getActivity()).getUs().getToken().trim())
                .addMultipartFile("data", filedst)
                .addPathParameter("npm", ((MainActivity) getActivity()).getUs().getUsername())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        if (bytesUploaded >= 0) {
                            ((MainActivity) getActivity()).setPercentProgressBar(bytesUploaded, totalBytes);
                        }
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((MainActivity) getActivity()).dismissPercentProgressBar();
                        try {
                            if (response.getBoolean("sukses")) {
                                Toast.makeText(getContext(), "Data berhasil diupload!", Toast.LENGTH_SHORT).show();
                                lmp = response.getString("name");
                            } else
                                Toast.makeText(getContext(), "Data gagal diupload!", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        ((MainActivity) getActivity()).dismissPercentProgressBar();
                    }
                });
    }

    public void loadMatkul() {
        matkul = new ArrayList<>();
        matkul.clear();
        try {
            JSONObject objk = new JSONObject();
            objk.put("Tgl_Awal", sdf.format(sdf2.parse(dtpAwal.getText().toString())));
            objk.put("Tgl_Akhir", sdf.format(sdf2.parse(dtpAkhir.getText().toString())));
            AndroidNetworking.post(((MainActivity) getActivity()).getBaseUrl() + "jkd/m/{jkd}")
                    .addPathParameter("jkd", ((MainActivity) getActivity()).getUs().getUsername())
                    .addJSONObjectBody(objk)
                    .addHeaders("Authorization", "Bearer " + ((MainActivity) getActivity()).getUs().getToken().trim())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            ((MainActivity) getActivity()).dismissDProgressDialog();
                            if (!response.isNull(0)) {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject obj = response.getJSONObject(i);
                                        String mulai = sdf3.format(sdf4.parse(obj.getString("mulai")));
                                        String selesai = sdf3.format(sdf4.parse(obj.getString("selesai")));
                                        String jam = mulai + " - " + selesai;
                                        matkul.add(new mataKuliah("1", obj.getString("kodemk"),
                                                obj.getString("namamatkul"),
                                                sdf.parse(obj.getString("tanggal")),
                                                jam,
                                                obj.getString("kelas"),
                                                0));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            mt = new mataKuliahTable(getContext(), mTableLayout, head, matkul, 1);
                            mt.loadTable();
                        }

                        @Override
                        public void onError(ANError error) {
                            ((MainActivity) getActivity()).dismissDProgressDialog();
                            // handle error
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void postData() {
        try {
            JSONObject obj = new JSONObject();
            JSONObject mk;
            JSONArray mkAr = new JSONArray();
            for (mataKuliah m : sMatkul) {
                mk = new JSONObject();
                mk.put("Kode_Mk", m.getKodeMk());
                mk.put("Nama_Mk", m.getNamaMk());
                mk.put("Tgl_Kuliah", sdf.format(m.getTanggal()));
                mk.put("Jam", m.getJam());
                mk.put("Kelas", m.getKelas());
                mk.put("Stat", m.getStat());
                mkAr.put(mk);
            }

            obj.put("Tgl_Transaksi", sdf.format(sdf2.parse(tglTrans.getText().toString())));
            obj.put("Jenis_Tiket", 1);
            obj.put("Stat", 0);
            obj.put("Kelas", kelas);
            obj.put("NPM", ((MainActivity) getActivity()).getUs().getUsername());
            obj.put("NIK_Cs", null);
            obj.put("NIK_Dosen", null);
            obj.put("Jenis", spinner1.getSelectedItem().toString());
            obj.put("Lampiran", lmp);
            obj.put("Tgl_Awal", sdf.format(sdf2.parse(dtpAwal.getText().toString())));
            obj.put("Tgl_Akhir", sdf.format(sdf2.parse(dtpAkhir.getText().toString())));
            obj.put("Keterangan", keterangan.getText().toString());
            obj.put("Matkul", mkAr);

            AndroidNetworking.post(((MainActivity) getActivity()).getBaseUrl() + "tpiz")
                    .addJSONObjectBody(obj) // posting json
                    .addHeaders("Authorization", "Bearer " + ((MainActivity) getActivity()).getUs().getToken().trim())
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ((MainActivity) getActivity()).dismissDProgressDialog();
                            try {
                                if (response.getBoolean("sukses")) {
                                    Toast.makeText(getContext(), "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                                    ((MainActivity) getActivity()).changeLayoutMain();
                                } else
                                    Toast.makeText(getContext(), "Data gagal ditambahkan!", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            ((MainActivity) getActivity()).dismissDProgressDialog();
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
