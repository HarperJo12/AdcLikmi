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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.adclikmi.Model.mataKuliah;
import com.android.adclikmi.Model.tiketMahasiswa;
import com.android.adclikmi.Model.tiketPerpindahan;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class detailTiketPerpindahanFrg extends Fragment implements EasyPermissions.PermissionCallbacks {
    public String title="Detail Tiket Perpindahan";
    private static final int REQUEST_FILE_CODE = 200;
    private TextView tglTiket, noTiket, npm, nama, klsAwal, klsAkhir,  jjgAwal, jjgAkhir,  jrsAwal, jrsAkhir,  bgdAwal, bdgAkhir, lampiran1, lampiran2, semester, ket, status;
    private Button downloadskot, downloadskk;
    private ImageView imgViewskot, imgViewskk;
    private File fileskot, fileskk, filedst = null;
    private tiketMahasiswa tkt;
    private String dstPath, lmp1, lmp2;
    public int modeLmp=0;
    private boolean browsed=false;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public detailTiketPerpindahanFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.detail_tiket_perpindahan, parent, false);
        AndroidNetworking.initialize(view.getContext());

        tglTiket=(TextView) view.findViewById(R.id.frm_tgl_trans);
        noTiket=(TextView) view.findViewById(R.id.frm_no_tiket);
        npm=(TextView) view.findViewById(R.id.frm_npm);
        nama=(TextView) view.findViewById(R.id.frm_nama_lengkap);
        klsAwal=(TextView) view.findViewById(R.id.frm_kelas_awal);
        klsAkhir=(TextView) view.findViewById(R.id.frm_kelas_akhir);
        jjgAwal=(TextView) view.findViewById(R.id.frm_jenjang_awal);
        jjgAkhir=(TextView) view.findViewById(R.id.frm_jenjang_akhir);
        jrsAwal=(TextView) view.findViewById(R.id.frm_jurusan_awal);
        jrsAkhir=(TextView) view.findViewById(R.id.frm_jurusan_akhir);
        bgdAwal=(TextView) view.findViewById(R.id.frm_bidang_awal);
        bdgAkhir=(TextView) view.findViewById(R.id.frm_bidang_akhir);
        lampiran1=(TextView) view.findViewById(R.id.frm_lampiran1);
        lampiran2=(TextView) view.findViewById(R.id.frm_lampiran2);
        semester=(TextView) view.findViewById(R.id.frm_semester);
        ket=(TextView) view.findViewById(R.id.frm_keterangan);
        status=(TextView) view.findViewById(R.id.frm_status);
        imgViewskot = (ImageView) view.findViewById(R.id.frm_imgViewSKOT);
        imgViewskk = (ImageView) view.findViewById(R.id.frm_imgViewSKK);
        downloadskot = (Button) view.findViewById((R.id.frm_downloadSKOT));
        downloadskk = (Button) view.findViewById((R.id.frm_downloadSKK));
        loadDat();

        ((MainActivity) getActivity()).setTitle(title);
        ((MainActivity) getActivity()).setSearchState(0);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        lampiran1.setPaintFlags(lampiran1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        lampiran2.setPaintFlags(lampiran2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        lampiran1.setVisibility(View.GONE);

        lampiran2.setVisibility(View.GONE);

        imgViewskot.setVisibility(View.GONE);
        imgViewskk.setVisibility(View.GONE);
        lampiran1.setPaintFlags(lampiran1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        downloadskot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lmp1=lampiran1.getText().toString();
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if(!lmp1.equalsIgnoreCase("null")) {
                            modeLmp=0;
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

        downloadskk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lmp2=lampiran2.getText().toString();
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if(!lmp2.equalsIgnoreCase("null")) {
                            modeLmp=1;
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

        imgViewskot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile(fileskot);
            }
        });

        imgViewskk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile(fileskk);
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

    protected void openFile(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < 24) {
            Uri uri = Uri.fromFile(file);
            Log.d("uri", uri.getPath());
            intent.setDataAndType(uri, getMimeType(file.getAbsolutePath()));
        } else {
            Log.d("file open path", file.getAbsolutePath());
            Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", file);
            intent.setDataAndType(uri, getMimeType(file.getAbsolutePath()));
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
        if(modeLmp==0){
            fileskot = new File(dstPath, lmp1.trim());
            dstPath += lmp1.trim();
            filedst=fileskot;
        }else{
            fileskk = new File(dstPath, lmp2.trim());
            dstPath += lmp2.trim();
            filedst=fileskk;
        }

        Log.d("filepath", dstPath);
        mime = getMimeType(dstPath);
        Log.d("mime", mime);
        //Show preview if the uploaded file is an image.
        if (mime != null && mime.contains("image")) {

            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 1;

            final Bitmap bitmap = BitmapFactory.decodeFile(dstPath, options);

            if(modeLmp==0){
                imgViewskot.setVisibility(View.VISIBLE);
                downloadskot.setVisibility(View.GONE);
                imgViewskot.setImageBitmap(bitmap);
            }else {
                imgViewskk.setVisibility(View.VISIBLE);
                downloadskk.setVisibility(View.GONE);
                imgViewskk.setImageBitmap(bitmap);
            }
        } else {
            if(modeLmp==0) {
                imgViewskot.setVisibility(View.VISIBLE);
                downloadskot.setVisibility(View.GONE);
                imgViewskot.setImageResource(R.drawable.ic_action_file);
            }else{
                imgViewskk.setVisibility(View.VISIBLE);
                downloadskk.setVisibility(View.GONE);
                imgViewskk.setImageResource(R.drawable.ic_action_file);
            }
        }
        openFile(filedst);
    }

    public void setLampiran(String lmp, Button down){
        if(TextUtils.isEmpty(lmp) || lmp.equalsIgnoreCase("null"))
            down.setVisibility(View.GONE);
    }

    public void downloadData() {
        String INTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");
        dstPath = INTERNAL_STORAGE + "/likmimedia/download/";
        Log.d("INTERNAL STORAGE", INTERNAL_STORAGE);
        filedst = new File(dstPath);

        //Log.d("filepath", dstPath);
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

        String fileName="";
        if(modeLmp==0)
            fileName=lmp1;
        else
            fileName=lmp2;

        AndroidNetworking.download(((MainActivity) getActivity()).getBaseUrl() + "download/{file}", dstPath, fileName)
                .addHeaders("Authorization", "Bearer " + ((MainActivity) getActivity()).getUs().getToken().trim())
                .addPathParameter("file", fileName)
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

    public void loadDat(){
        tkt=(tiketMahasiswa) getArguments().getSerializable("tiket");
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"tper/{tper}")
                .addPathParameter("tper", tkt.getNoTiket())
                .addHeaders("Authorization", "Bearer "+((MainActivity)getActivity()).getUs().getToken().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ((MainActivity)getActivity()).dismissDProgressDialog();
                            JSONObject obj=response.getJSONObject(0);

                            tglTiket.setText(obj.getString("Tgl_Transaksi"));
                            noTiket.setText(obj.getString("No_Tiket"));
                            npm.setText(obj.getString("NPM"));
                            nama.setText(obj.getString("Nama_Lkp"));
                            klsAwal.setText(obj.getString("Kls_Awal"));
                            klsAkhir.setText(obj.getString("Kls_Akhir"));
                            jjgAwal.setText(obj.getString("Jjg_Awal"));
                            jjgAkhir.setText(obj.getString("Jjg_Akhir"));
                            jrsAwal.setText(obj.getString("Jrs_Awal"));
                            jrsAkhir.setText(obj.getString("Jrs_Akhir"));
                            bgdAwal.setText(obj.getString("Bdg_Awal"));
                            bdgAkhir.setText(obj.getString("Bdg_Akhir"));
                            lampiran1.setText(obj.getString("Lampiran1"));
                            lampiran2.setText(obj.getString("Lampiran2"));
                            semester.setText(obj.getString("Tahun_Sem"));
                            ket.setText(obj.getString("Keterangan"));
                            status.setText(getStatus(obj.getInt("Stat")));

                            setLampiran(obj.getString("Lampiran1"), downloadskot);
                            setLampiran(obj.getString("Lampiran2"), downloadskk);
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

    public String getStatus(int stat){
        String tmp;
        switch (stat){
            case 0:tmp="Pengajuan Belum Diproses!";break;
            case 1:tmp="Pengajuan Diterima!";break;
            case 2:tmp="Pengajuan Ditolak!";break;
            case 3:tmp="Harap Menghadap Ketua Jurusan!";break;
            default:tmp="";
        }
        return tmp;
    }

}
