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
import android.widget.TextView;
import android.widget.Toast;

import com.android.adclikmi.Model.tiketPerpindahan;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

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
public class penangananPerpindahanFrg extends Fragment implements EasyPermissions.PermissionCallbacks {
    public String title="Penanganan Perpindahan";
    private static final int REQUEST_FILE_CODE = 200;
    private TextView tglTiket, noTiket, npm, nama, stat, klsAwal, klsAkhir,  jjgAwal, jjgAkhir,  jrsAwal, jrsAkhir,  bgdAwal, bdgAkhir, lampiran1, lampiran2, semester, ket;
    private Spinner aksi;
    private Button submit, cancel, downloadskot, downloadskk;
    private ImageView imgViewskot, imgViewskk;
    private File fileskot, fileskk, filedst = null;
    private tiketPerpindahan tkt;
    private String dstPath;
    public int status, modeLmp=0;
    private boolean browsed=false;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public penangananPerpindahanFrg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.penanganan_perpindahan, parent, false);

        tglTiket=(TextView) view.findViewById(R.id.frm_tgl_trans);
        noTiket=(TextView) view.findViewById(R.id.frm_no_tiket);
        npm=(TextView) view.findViewById(R.id.frm_npm);
        nama=(TextView) view.findViewById(R.id.frm_nama_lengkap);
        stat=(TextView) view.findViewById(R.id.frm_status);
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
        imgViewskot = (ImageView) view.findViewById(R.id.frm_imgViewSKOT);
        imgViewskk = (ImageView) view.findViewById(R.id.frm_imgViewSKK);
        aksi=(Spinner)view.findViewById(R.id.frm_aksi_spinner);
        submit=(Button) view.findViewById(R.id.frm_submit);
        cancel=(Button) view.findViewById((R.id.frm_cancel));
        downloadskot = (Button) view.findViewById((R.id.frm_downloadSKOT));
        downloadskk = (Button) view.findViewById((R.id.frm_downloadSKK));

        setHasOptionsMenu(true);
        addData(view);
        ((MainActivity) getActivity()).setTitle(title);
        ((MainActivity) getActivity()).setSearchState(0);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        tkt=(tiketPerpindahan) getArguments().getSerializable("tiket");
        tglTiket.setText(sdf.format(tkt.getTglTrans()));
        noTiket.setText(tkt.getNoTiket());
        npm.setText(tkt.getNPM());
        nama.setText(tkt.getNama());
        stat.setText(getStatus(tkt.getStat()));
        klsAwal.setText(tkt.getKlsAwal());
        klsAkhir.setText(tkt.getKlsTujuan());
        jjgAwal.setText(tkt.getJjgAwal());
        jjgAkhir.setText(tkt.getJjgTujuan());
        jrsAwal.setText(tkt.getJrsAwal());
        jrsAkhir.setText(tkt.getJrsTujuan());
        bgdAwal.setText(tkt.getBdgAwal());
        bdgAkhir.setText(tkt.getBdgTujuan());
        lampiran1.setText(tkt.getLampiran1());
        lampiran2.setText(tkt.getLampiran2());
        semester.setText(tkt.getThnSem());
        ket.setText(tkt.getKeterangan());

        lampiran1.setClickable(true);
        lampiran2.setClickable(true);
        lampiran1.setPaintFlags(lampiran1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        lampiran2.setPaintFlags(lampiran2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if(tkt.getStat()!=0 && tkt.getStat()!=3) {
            submit.setEnabled(false);
            aksi.setEnabled(false);
        }else {
            submit.setEnabled(true);
            aksi.setEnabled(true);
        }
        if(TextUtils.isEmpty(tkt.getLampiran1()) || tkt.getLampiran1().equalsIgnoreCase("null"))
            downloadskot.setVisibility(View.GONE);
        lampiran1.setVisibility(View.GONE);

        if(TextUtils.isEmpty(tkt.getLampiran2()) || tkt.getLampiran2().equalsIgnoreCase("null"))
            downloadskk.setVisibility(View.GONE);
        lampiran2.setVisibility(View.GONE);

        imgViewskot.setVisibility(View.GONE);
        imgViewskk.setVisibility(View.GONE);
        lampiran1.setPaintFlags(lampiran1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        lampiran2.setPaintFlags(lampiran2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        downloadskot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if(!tkt.getLampiran1().equalsIgnoreCase("null")) {
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
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if(!tkt.getLampiran2().equalsIgnoreCase("null")) {
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status=aksi.getSelectedItemPosition();
                ((MainActivity)getActivity()).hideKeyboard();
                if(status!=0){
                    ((MainActivity) getActivity()).showDProgressDialog();
                    postData();
                }else{
                    Toast.makeText(getContext(), "Harap memilih aksi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).hideKeyboard();
                ((MainActivity)getActivity()).changeLayoutMain();
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

    public String getStatus(int stat) {
        String st="";
        switch (stat){
            case 0:st="Belum diproses";break;
            case 1:st="Diterima";break;
            case 2:st="Ditolak";break;
            case 3:st="Harap Menghadap Ketua Jurusan";break;
            default:st="Belum diproses";
        }
        return st;
    }

    public void addData(View view){
        List<String> list = new ArrayList<String>();
        list.add("");
        list.add("Diterima");
        list.add("Ditolak");
        list.add("Harap Menghadap Ketua Jurusan");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aksi.setAdapter(dataAdapter);
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
            fileskot = new File(dstPath, tkt.getLampiran1().trim());
            dstPath += tkt.getLampiran1().trim();
            filedst=fileskot;
        }else{
            fileskk = new File(dstPath, tkt.getLampiran2().trim());
            dstPath += tkt.getLampiran2().trim();
            filedst=fileskk;
        }

        Log.d("filepath", dstPath);
        mime = getMimeType(dstPath);
        Log.d("mime", mime);
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
            fileName=tkt.getLampiran1();
        else
            fileName=tkt.getLampiran2();

        AndroidNetworking.download(((MainActivity) getActivity()).getBaseUrl() + "download/{file}", dstPath, fileName.trim())
                .addHeaders("Authorization", "Bearer " + ((MainActivity) getActivity()).getUs().getToken().trim())
                .addPathParameter("file", fileName.trim())
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

            obj.put("Tgl_Transaksi",sdf.format(tkt.getTglTrans()));
            obj.put("Jenis_Tiket",tkt.getJnsTiket());
            obj.put("Stat",status);
            obj.put("Kelas", tkt.getKelas());
            obj.put("NPM",tkt.getNPM());
            obj.put("NIK_Cs",null);
            obj.put("NIK_Dosen",tkt.getNik_kjr());
            obj.put("Kls_Awal",tkt.getKlsAwal());
            obj.put("Kls_Akhir",tkt.getKlsTujuan());
            obj.put("Jjg_Awal",tkt.getJjgAwal());
            obj.put("Jjg_Akhir",tkt.getJjgTujuan());
            obj.put("Jrs_Awal",tkt.getJrsAwal());
            obj.put("Jrs_Akhir",tkt.getJrsTujuan());
            obj.put("Bdg_Awal",tkt.getBdgAwal());
            obj.put("Bdg_Akhir",tkt.getBdgTujuan());
            obj.put("Tahun_Sem",tkt.getThnSem());
            obj.put("Keterangan",tkt.getKeterangan());
            obj.put("Lampiran1",tkt.getLampiran1());
            obj.put("Lampiran2",tkt.getLampiran2());

            AndroidNetworking.put(((MainActivity)getActivity()).getBaseUrl()+"tiket/{tiket}")
                    .addPathParameter("tiket", tkt.getNoTiket())
                    .addJSONObjectBody(obj) // posting json
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
                                    Toast.makeText(getContext(), "Data berhasil diupdate!", Toast.LENGTH_SHORT).show();
                                    ((MainActivity)getActivity()).changeLayoutMain();
                                }else
                                    Toast.makeText(getContext(),"Data gagal diupdate! "+ response.getString("eror"),Toast.LENGTH_SHORT).show();
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
        }
    }

}
