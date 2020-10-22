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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.adclikmi.Model.mataKuliah;
import com.android.adclikmi.Model.tiketMahasiswa;
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
public class detailTiketIzinFrg extends Fragment implements EasyPermissions.PermissionCallbacks {
    public String title="Detail Tiket Izin";
    private static final int REQUEST_FILE_CODE = 200;
    private TextView tglTiket, noTiket, npm, nama, jenis, lampiran, tglAwal, tglAkhir, ket, status;
    private tiketMahasiswa tkt;
    private TableLayout mTableLayout;
    private TableRow head;
    private Button download;
    private ImageView imgView;
    private File filedst = null;
    private mataKuliahTable mt;
    private ArrayList<mataKuliah> matkul;
    private String dstPath, lmp;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public detailTiketIzinFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.detail_tiket_izin, parent, false);
        AndroidNetworking.initialize(view.getContext());

        tglTiket=(TextView) view.findViewById(R.id.frm_tgl_trans);
        noTiket=(TextView) view.findViewById(R.id.frm_no_tiket);
        npm=(TextView) view.findViewById(R.id.frm_npm);
        nama=(TextView) view.findViewById(R.id.frm_nama_lengkap);
        jenis=(TextView) view.findViewById(R.id.frm_jenis);
        lampiran=(TextView) view.findViewById(R.id.frm_lampiran);
        tglAwal=(TextView) view.findViewById(R.id.frm_tgl_awal);
        tglAkhir=(TextView) view.findViewById(R.id.frm_tgl_akhir);
        ket=(TextView) view.findViewById(R.id.frm_keterangan);
        status=(TextView) view.findViewById(R.id.frm_status);
        imgView = (ImageView) view.findViewById(R.id.frm_imgView);
        download = (Button) view.findViewById((R.id.frm_download));
        head=(TableRow)view.findViewById(R.id.frm_header_matkul);
        mTableLayout=(TableLayout)view.findViewById(R.id.frm_data_matkul);
        loadDat();
        
        ((MainActivity) getActivity()).setTitle(title);//untuk menset title pada toolbar
        ((MainActivity) getActivity()).setSearchState(0);//mengubah state search pada toolbar
        ((MainActivity) getActivity()).invalidateOptionsMenu();//memanggil method oncreatemenuoption pada mainactivity

        if(TextUtils.isEmpty(lampiran.getText().toString()) || lampiran.getText().toString().equalsIgnoreCase("null"))
            download.setVisibility(View.GONE);
        lampiran.setVisibility(View.GONE);
        imgView.setVisibility(View.GONE);
        //lampiran.setClickable(true);
        lampiran.setPaintFlags(lampiran.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lmp=lampiran.getText().toString();
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if(!lmp.equalsIgnoreCase("null")) {
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
        filedst = new File(dstPath, lmp.trim());
        dstPath += lmp.trim();

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

            imgView.setVisibility(View.VISIBLE);
            download.setVisibility(View.GONE);
            imgView.setImageBitmap(bitmap);
        } else {
            imgView.setVisibility(View.VISIBLE);
            download.setVisibility(View.GONE);
            imgView.setImageResource(R.drawable.ic_action_file);
        }

        openFile();
    }

    public void downloadData() {
        //internal storagenya jadi pakai env external storage karna ext storage udah pake kaya storage/656-320
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

        AndroidNetworking.download(((MainActivity) getActivity()).getBaseUrl() + "download/{file}", dstPath, lmp)
                .addHeaders("Authorization", "Bearer " + ((MainActivity) getActivity()).getUs().getToken().trim())
                .addPathParameter("file", lmp)
                .setTag("uploadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        if (bytesDownloaded >= 0) {
                            ((MainActivity) getActivity()).setPercentProgressBar(bytesDownloaded, totalBytes);
                            //((MainActivity) getActivity()).getProgressDialog().setPercentage(getActivity().getSupportFragmentManager(), bytesUploaded, totalBytes);
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
        matkul = new ArrayList<>();
        tkt=(tiketMahasiswa) getArguments().getSerializable("tiket");//mengambil objek custom yang telah dilampirkan
        AndroidNetworking.get(((MainActivity)getActivity()).getBaseUrl()+"tpiz/{tpiz}")
                .addPathParameter("tpiz", tkt.getNoTiket())
                .addHeaders("Authorization", "Bearer "+((MainActivity)getActivity()).getUs().getToken().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ((MainActivity)getActivity()).dismissDProgressDialog();
                            JSONObject obj=response.getJSONObject(0);
                            JSONArray matkulAr = obj.getJSONArray("pmatkuldet");
                            for(int y=0; y<matkulAr.length();y++){
                                JSONObject objCh = matkulAr.getJSONObject(y);
                                matkul.add(new mataKuliah(objCh.getString("Id"),
                                        objCh.getString("Kode_Mk"),
                                        objCh.getString("Nama_Mk"),
                                        sdf.parse(objCh.getString("Tgl_Kuliah")),
                                        objCh.getString("Jam"),
                                        objCh.getString("Kelas"),
                                        objCh.getInt("Stat")));
                            }

                            tglTiket.setText(obj.getString("Tgl_Transaksi"));
                            noTiket.setText(obj.getString("No_Tiket"));
                            npm.setText(obj.getString("NPM"));
                            nama.setText(obj.getString("Nama_Lkp"));
                            jenis.setText(obj.getString("Jenis"));
                            lampiran.setText(obj.getString("Lampiran"));
                            tglAwal.setText(obj.getString("Tgl_Awal"));
                            tglAkhir.setText(obj.getString("Tgl_Akhir"));
                            ket.setText(obj.getString("Keterangan"));
                            status.setText(getStatus(obj.getInt("Stat")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mt=new mataKuliahTable(getContext(),mTableLayout,head,matkul,0);
                        mt.loadTable();
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
            case 0:tmp="Belum Diproses!";break;
            case 1:tmp="Pengajuan Diterima!";break;
            case 2:tmp="Pengajuan Ditolak!";break;
            case 3:tmp="Harap Menghadap Ketua Jurusan!";break;
            default:tmp="";
        }
        return tmp;
    }



}
