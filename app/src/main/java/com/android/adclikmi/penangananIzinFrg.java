package com.android.adclikmi;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.adclikmi.Model.mataKuliah;
import com.android.adclikmi.Model.tiketIzin;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class penangananIzinFrg extends Fragment implements EasyPermissions.PermissionCallbacks {
    public String title = "Penanganan Izin";
    private static final int REQUEST_FILE_CODE = 200;
    private TextView tglTiket, noTiket, npm, nama, stat, jenis, lampiran, tglAwal, tglAkhir, ket;
    private TableLayout mTableLayout;
    private TableRow head;
    private Spinner aksi;
    private Button submit, cancel, download;
    private ImageView imgView;
    private File filedst = null;
    private tiketIzin tkt;
    private mataKuliahTable mt;
    private ArrayList<mataKuliah> matkul;
    private ArrayList<mataKuliah> sMatkul;
    private String dstPath;
    public int status;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public penangananIzinFrg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.penanganan_izin, parent, false);

        tglTiket = (TextView) view.findViewById(R.id.frm_tgl_trans);
        noTiket = (TextView) view.findViewById(R.id.frm_no_tiket);
        npm = (TextView) view.findViewById(R.id.frm_npm);
        nama = (TextView) view.findViewById(R.id.frm_nama_lengkap);
        stat = (TextView) view.findViewById(R.id.frm_status);
        jenis = (TextView) view.findViewById(R.id.frm_jenis);
        lampiran = (TextView) view.findViewById(R.id.frm_lampiran);
        tglAwal = (TextView) view.findViewById(R.id.frm_tgl_awal);
        tglAkhir = (TextView) view.findViewById(R.id.frm_tgl_akhir);
        ket = (TextView) view.findViewById(R.id.frm_keterangan);
        imgView = (ImageView) view.findViewById(R.id.frm_imgView);
        aksi = (Spinner) view.findViewById(R.id.frm_aksi_spinner);
        submit = (Button) view.findViewById(R.id.frm_submit);
        cancel = (Button) view.findViewById((R.id.frm_cancel));
        download = (Button) view.findViewById((R.id.frm_download));
        head = (TableRow) view.findViewById(R.id.frm_header_matkul);
        mTableLayout = (TableLayout) view.findViewById(R.id.frm_data_matkul);
        //mTableLayout.setStretchAllColumns(true);

        setHasOptionsMenu(true);
        adddat(view);
        ((MainActivity) getActivity()).setTitle(title);//untuk menset title pada toolbar
        ((MainActivity) getActivity()).setSearchState(0);//mengubah state search pada toolbar
        ((MainActivity) getActivity()).invalidateOptionsMenu();//memanggil method oncreatemenuoption pada mainactivity

        tkt = (tiketIzin) getArguments().getSerializable("tiket");
        tglTiket.setText(sdf.format(tkt.getTglTrans()));
        noTiket.setText(tkt.getNoTiket());
        npm.setText(tkt.getNPM());
        stat.setText(getStatus(tkt.getStat()));
        nama.setText(tkt.getNama());
        jenis.setText(tkt.getJenis());
        lampiran.setText(tkt.getLampiran());
        tglAwal.setText(sdf.format(tkt.getTglAwal()));
        tglAkhir.setText(sdf.format(tkt.getTglAkhir()));
        ket.setText(tkt.getKeterangan());
        matkul = tkt.getMatkul();
        mt = new mataKuliahTable(getContext(), mTableLayout, head, matkul, 2);
        mt.loadTable();

        if (tkt.getStat() != 0 && tkt.getStat() != 3) {
            submit.setEnabled(false);
            aksi.setEnabled(false);
        } else {
            submit.setEnabled(true);
            aksi.setEnabled(true);
        }

        if(TextUtils.isEmpty(tkt.getLampiran()) || tkt.getLampiran().equalsIgnoreCase("null"))
            download.setVisibility(View.GONE);
        lampiran.setVisibility(View.GONE);
        imgView.setVisibility(View.GONE);
        lampiran.setPaintFlags(lampiran.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if(!tkt.getLampiran().equalsIgnoreCase("null")) {
                            ((MainActivity) getActivity()).showPercentProgressBar();
                            downloadData();
                        }
                    } else {
                        EasyPermissions.requestPermissions(getActivity(), getString(R.string.write_file), REQUEST_FILE_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(getActivity(), getString(R.string.read_file), REQUEST_FILE_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMatkul = new ArrayList<>();
                sMatkul = mt.getMatkulSelected();
                status = aksi.getSelectedItemPosition();
                ((MainActivity) getActivity()).hideKeyboard();
                if (status != 0) {
                    if (status == 1 && getValidationMk(sMatkul)) {
                        matkul = new ArrayList<>();
                        matkul.clear();
                        matkul = mt.getMatkul();
                        for (mataKuliah m : matkul) {
                            if (m.getStat() == 0) { //matkul yg belum kepilih diset tolak
                                m.setStat(2);
                            }
                        }
                        ((MainActivity) getActivity()).showDProgressDialog();
                        postData();
                    } else if (status == 2 || status == 3) {
                        for (mataKuliah m : matkul) {
                            if (status == 2) {
                                m.setStat(2);
                            } else if (status == 3) {
                                m.setStat(0);
                            }
                        }
                        ((MainActivity) getActivity()).showDProgressDialog();
                        postData();
                    }
                } else {
                    Toast.makeText(getContext(), "Harap memilih aksi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).hideKeyboard();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, getActivity());
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getContext(), "Permission has been denied!", Toast.LENGTH_SHORT).show();
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

    public String getStatus(int stat) {
        String st = "";
        switch (stat) {
            case 0:
                st = "Belum diproses";
                break;
            case 1:
                st = "Diterima";
                break;
            case 2:
                st = "Ditolak";
                break;
            case 3:
                st = "Harap Menghadap Ketua Jurusan";
                break;
            default:
                st = "Belum diproses";
        }
        return st;
    }

    public void adddat(View view) {
        List<String> list = new ArrayList<String>();//menambahkan dat pada spinner
        list.add("");
        list.add("Diterima");
        list.add("Ditolak");
        list.add("Harap Menghadap Ketua Jurusan");
        ArrayAdapter<String> datAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, list);
        datAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aksi.setAdapter(datAdapter);
    }

    protected void openFile() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < 24) {
            Uri uri = Uri.fromFile(filedst);
            Log.d("uri", uri.getPath());
            intent.setDataAndType(uri, getMimeType(dstPath));
        } else {
            Log.d("file open path", filedst.getPath());
            Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", filedst);
            intent.setDataAndType(FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", filedst), getMimeType(dstPath));
            Log.d("uri", uri.getPath());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivity(intent);
    }


    public String getMimeType(String filePath) {
        String mime = null;
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        return mime;
    }

    public void loadImage() {
        String mime = null;
        filedst = new File(dstPath, tkt.getLampiran().trim());
        dstPath += tkt.getLampiran().trim();

        Log.d("filepath", dstPath);
        mime = getMimeType(dstPath);
        Log.d("mime", mime);
        if (mime != null && mime.contains("image")) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 1;

            final Bitmap bitmap = BitmapFactory.decodeFile(dstPath, options);

            imgView.setVisibility(View.VISIBLE);
            download.setVisibility(View.GONE);
            imgView.setImageBitmap(bitmap);
        } else {
            imgView.setImageResource(R.drawable.ic_action_file);
        }

        openFile();
    }

    public void downloadData() {
        String INTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");
        dstPath = INTERNAL_STORAGE + "/likmimedia/download/";
        Log.d("INTERNAL STORAGE", INTERNAL_STORAGE);
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

        AndroidNetworking.download(((MainActivity) getActivity()).getBaseUrl() + "download/{file}", dstPath, tkt.getLampiran().trim())
                .addHeaders("Authorization", "Bearer " + ((MainActivity) getActivity()).getUs().getToken().trim())
                .addPathParameter("file", tkt.getLampiran())
                .setTag("uploadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        if (bytesDownloaded >= 0) {
                            ((MainActivity) getActivity()).setPercentProgressBar(bytesDownloaded, totalBytes);
                        }
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d("download", "Selesai download!");
                        ((MainActivity) getActivity()).dismissPercentProgressBar();
                        Toast.makeText(getContext(), "Data berhasil didownload!", Toast.LENGTH_SHORT).show();
                        loadImage();
                    }

                    @Override
                    public void onError(ANError error) {
                        ((MainActivity) getActivity()).dismissPercentProgressBar();
                        Toast.makeText(getContext(), "Data gagal didownload!", Toast.LENGTH_SHORT).show();
                        filedst = null;
                        // handle error
                    }
                });
    }


    public void postData() {
        try {
            JSONObject obj = new JSONObject();
            JSONObject mk;
            JSONArray mkAr = new JSONArray();
            for (mataKuliah m : matkul) {
                mk = new JSONObject();
                mk.put("Id", m.getId());
                mk.put("Kode_Mk", m.getKodeMk());
                mk.put("Nama_Mk", m.getNamaMk());
                mk.put("Tgl_Kuliah", sdf.format(m.getTanggal()));
                mk.put("Jam", m.getJam());
                mk.put("Kelas", m.getKelas());
                mk.put("Stat", m.getStat());
                mkAr.put(mk);
            }

            obj.put("Tgl_Transaksi", sdf.format(tkt.getTglTrans()));
            obj.put("Jenis_Tiket", tkt.getJnsTiket());
            obj.put("Stat", status);
            obj.put("Kelas", tkt.getKelas());
            obj.put("NPM", tkt.getNPM());
            obj.put("NIK_Cs", null);
            obj.put("NIK_Dosen", null);
            obj.put("Jenis", tkt.getJenis());
            obj.put("Lampiran", null);
            obj.put("Tgl_Awal", sdf.format(tkt.getTglAwal()));
            obj.put("Tgl_Akhir", sdf.format(tkt.getTglAkhir()));
            obj.put("Keterangan", tkt.getKeterangan());
            obj.put("Matkul", mkAr);

            AndroidNetworking.put(((MainActivity) getActivity()).getBaseUrl() + "tiket/{tiket}")
                    .addPathParameter("tiket", tkt.getNoTiket())
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
                                    Toast.makeText(getContext(), "Data berhasil diupdate!", Toast.LENGTH_SHORT).show();
                                    ((MainActivity) getActivity()).changeLayoutMain();
                                } else
                                    Toast.makeText(getContext(), "Data gagal diupdate! " + response.getString("eror"), Toast.LENGTH_SHORT).show();
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
        }
    }

}
