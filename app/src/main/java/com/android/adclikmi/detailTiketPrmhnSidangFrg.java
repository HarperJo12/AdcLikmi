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

import com.android.adclikmi.Model.tiketMahasiswa;
import com.android.adclikmi.Model.tiketSidang;
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
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class detailTiketPrmhnSidangFrg extends Fragment implements EasyPermissions.PermissionCallbacks {
    public String title="Detail Tiket Permohonan Sidang";
    private static final int REQUEST_FILE_CODE = 200;
    private TextView tglTiket, noTiket, npm, nama, ttl, alamat, kodePos, noTelp,  noHP, email, jenjang, jurusan, bidang, judulTA, namaPrs, alamatPrs, pembimbing, koPembimbing, thnSem, lampiranKwitansi, status;
    private Button next, downloadkwi;
    private ImageView imgViewkwi;
    private File filekwi, filedst = null;;
    private tiketMahasiswa tktMhs;
    private tiketSidang tkt;
    private String dstPath;
    public int modeLmp=0;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public detailTiketPrmhnSidangFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.detail_tiket_prmhn_sidang, parent, false);

        tglTiket=(TextView) view.findViewById(R.id.frm_tgl_trans);
        noTiket=(TextView) view.findViewById(R.id.frm_no_tiket);
        npm=(TextView) view.findViewById(R.id.frm_npm);
        nama=(TextView) view.findViewById(R.id.frm_nama_lengkap);
        ttl=(TextView) view.findViewById(R.id.frm_ttl);
        alamat=(TextView) view.findViewById(R.id.frm_alamat);
        kodePos=(TextView) view.findViewById(R.id.frm_kode_pos);
        noTelp=(TextView) view.findViewById(R.id.frm_no_telp);
        noHP=(TextView) view.findViewById(R.id.frm_no_handphone);
        email=(TextView) view.findViewById(R.id.frm_email);
        jenjang=(TextView) view.findViewById(R.id.frm_program);
        jurusan=(TextView) view.findViewById(R.id.frm_jurusan);
        bidang=(TextView) view.findViewById(R.id.frm_bidang_minat);
        judulTA=(TextView) view.findViewById(R.id.frm_judul_TA);
        namaPrs=(TextView) view.findViewById(R.id.frm_nama_prs);
        alamatPrs=(TextView) view.findViewById(R.id.frm_alamat_prs);
        pembimbing=(TextView) view.findViewById(R.id.frm_pembimbing);
        koPembimbing=(TextView) view.findViewById(R.id.frm_kopembimbing);
        lampiranKwitansi=(TextView) view.findViewById(R.id.frm_lmp_kwitansi);
        thnSem=(TextView) view.findViewById(R.id.frm_semester);
        status=(TextView) view.findViewById(R.id.frm_status);
        imgViewkwi = (ImageView) view.findViewById(R.id.frm_imgViewkwi);
        next=(Button) view.findViewById(R.id.frm_next);
        downloadkwi = (Button) view.findViewById((R.id.frm_downloadkwi));
        loadDat();

        ((MainActivity) getActivity()).setTitle(title);
        ((MainActivity) getActivity()).setSearchState(0);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment= new detailTiketKetSidangFrg();
                Bundle bundle = new Bundle();
                bundle.putSerializable("tiket", tkt);
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).changeLayout(fragment);
            }
        });

        if(TextUtils.isEmpty(lampiranKwitansi.getText().toString()) || lampiranKwitansi.getText().toString().equalsIgnoreCase("null"))
            downloadkwi.setVisibility(View.GONE);
        lampiranKwitansi.setVisibility(View.GONE);

        imgViewkwi.setVisibility(View.GONE);
        lampiranKwitansi.setPaintFlags(lampiranKwitansi.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        downloadkwi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if(!tkt.getLampiranKwitansi().equalsIgnoreCase("null")) {
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

        imgViewkwi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile(filekwi);
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
        filekwi = new File(dstPath, tkt.getLampiranKwitansi().trim());
        dstPath += tkt.getLampiranKwitansi().trim();
        filedst = filekwi;

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


            imgViewkwi.setVisibility(View.VISIBLE);
            downloadkwi.setVisibility(View.GONE);
            imgViewkwi.setImageBitmap(bitmap);
        } else {
            imgViewkwi.setVisibility(View.VISIBLE);
            downloadkwi.setVisibility(View.GONE);
            imgViewkwi.setImageResource(R.drawable.ic_action_file);
        }
        openFile(filedst);
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

        String fileName="";
        fileName=tkt.getLampiranKwitansi();

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

    public void loadDat(){
        tktMhs=(tiketMahasiswa) getArguments().getSerializable("tiket");//mengambil objek custom yang telah dilampirkan
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"tpsi/{tpsi}")
                .addPathParameter("tpsi", tktMhs.getNoTiket())
                .addHeaders("Authorization", "Bearer "+((MainActivity)getActivity()).getUs().getToken().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ((MainActivity)getActivity()).dismissDProgressDialog();
                            JSONObject obj=response.getJSONObject(0);
                            tkt=new tiketSidang(obj.getString("No_Tiket"),
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
                                    obj.getString("Lmp_Kwitansi"));

                            tglTiket.setText(sdf.format(tkt.getTglTrans()));
                            noTiket.setText(tkt.getNoTiket());
                            npm.setText(tkt.getNPM());
                            nama.setText(tkt.getNama());
                            ttl.setText(tkt.getTtl());
                            alamat.setText(tkt.getAlamat());
                            kodePos.setText(tkt.getKodePos());
                            noTelp.setText(tkt.getNoTelp());
                            noHP.setText(tkt.getNoHp());
                            email.setText(tkt.getEmail());
                            jenjang.setText(tkt.getJenjang());
                            jurusan.setText(tkt.getJurusan());
                            bidang.setText(tkt.getBidang());
                            judulTA.setText(tkt.getJudulTA());
                            namaPrs.setText(tkt.getNamaPerusahaan());
                            alamatPrs.setText(tkt.getAlamatPerusahaan());
                            pembimbing.setText(tkt.getPembimbing());
                            koPembimbing.setText(tkt.getKoPembimbing());
                            lampiranKwitansi.setText(tkt.getLampiranKwitansi());
                            thnSem.setText(tkt.getTahunSem());
                            status.setText(getStatus(tkt.getStat()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
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
