package com.android.adclikmi;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.ParseException;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class reportFrg extends Fragment implements EasyPermissions.PermissionCallbacks {
    public String title="Report";
    private static final int REQUEST_FILE_CODE = 200;
    private TextView fileName;
    private Spinner jenis, bln, thn;
    private Button submit;
    private ImageView imgView;
    private File filedst = null;
    private String dstPath, lmp;

    public reportFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.report, parent, false);
        AndroidNetworking.initialize(view.getContext());

        fileName=(TextView) view.findViewById(R.id.frm_nameFile);
        imgView = (ImageView) view.findViewById(R.id.frm_imgView);
        jenis = (Spinner) view.findViewById(R.id.frm_jenis_spinner);
        bln = (Spinner) view.findViewById(R.id.frm_bln_spinner);
        thn = (Spinner) view.findViewById(R.id.frm_thn_spinner);
        submit = (Button) view.findViewById((R.id.frm_submit));

        ((MainActivity) getActivity()).setTitle(title);//untuk menset title pada toolbar
        ((MainActivity) getActivity()).setSearchState(0);//mengubah state search pada toolbar
        ((MainActivity) getActivity()).invalidateOptionsMenu();//memanggil method oncreatemenuoption pada mainactivity

        fileName.setVisibility(View.GONE);
        imgView.setVisibility(View.GONE);

        List<String> list1 = new ArrayList<String>();//menambahkan data untuk spinner
        list1.add("Izin");
        list1.add("Ujian");
        list1.add("Perpindahan");
        list1.add("Sidang");

        List<String> list2 = new ArrayList<String>();
        list2.add("Januari");
        list2.add("Februari");
        list2.add("Maret");
        list2.add("April");
        list2.add("Mei");
        list2.add("Juni");
        list2.add("Juli");
        list2.add("Agustus");
        list2.add("September");
        list2.add("Oktober");
        list2.add("November");
        list2.add("Desember");

        List<String> list3 = new ArrayList<String>();
        list3.add("2019");
        list3.add("2020");

        addData(view, list1, jenis);
        addData(view, list2, bln);
        addData(view, list3, thn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        ((MainActivity) getActivity()).showDProgressDialog();
                        requestReport();
                    } else {
                        EasyPermissions.requestPermissions(getActivity(), getString(R.string.write_file), REQUEST_FILE_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                } else {
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
        if (mime != null && mime.contains("image")) {

            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 1;

            final Bitmap bitmap = BitmapFactory.decodeFile(dstPath, options);

            imgView.setVisibility(View.VISIBLE);
            imgView.setImageBitmap(bitmap);
        } else {
            imgView.setVisibility(View.VISIBLE);
            fileName.setVisibility(View.VISIBLE);
            imgView.setImageResource(R.drawable.ic_action_file);
            fileName.setText(lmp);
        }

        openFile();
    }

    public void addData(View view, List lst, Spinner spn) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, lst);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(dataAdapter);
    }

    public void requestReport(){
        try {
            int slc=jenis.getSelectedItemPosition()+1;
            JSONObject objk = new JSONObject();
            objk.put("jenis", slc);
            objk.put("bulan", bln.getSelectedItemPosition()+1);
            objk.put("tahun", thn.getSelectedItem().toString());
            AndroidNetworking.post(((MainActivity) getActivity()).getBaseUrl() + "report")
                    .addJSONObjectBody(objk) // posting json
                    .addHeaders("Authorization", "Bearer " + ((MainActivity) getActivity()).getUs().getToken().trim())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ((MainActivity) getActivity()).dismissDProgressDialog();
                            try {
                                if (!response.getBoolean("error")) {
                                    Toast.makeText(getContext(), "Laporan dibuat!", Toast.LENGTH_SHORT).show();
                                    lmp = response.getString("name");
                                    ((MainActivity) getActivity()).showPercentProgressBar();
                                    downloadData();
                                } else
                                    Toast.makeText(getContext(), response.getString("pesan"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            ((MainActivity) getActivity()).dismissDProgressDialog();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void downloadData() {
        String INTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");
        dstPath = INTERNAL_STORAGE + "/likmimedia/download/report/";
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

        AndroidNetworking.download(((MainActivity) getActivity()).getBaseUrl() + "reportDown/{file}", dstPath, lmp)
                .addHeaders("Authorization", "Bearer " + ((MainActivity) getActivity()).getUs().getToken().trim())
                .addPathParameter("file", lmp)
                .setTag("downloadTest")
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
                        Toast.makeText(getContext(), "Laporan berhasil disimpan pada "+dstPath+lmp, Toast.LENGTH_LONG).show();
                        loadImage();
                    }

                    @Override
                    public void onError(ANError error) {
                        ((MainActivity) getActivity()).dismissPercentProgressBar();
                        Toast.makeText(getContext(), "Laporan gagal di download!", Toast.LENGTH_SHORT).show();
                        filedst = null;
                        // handle error
                    }
                });
    }

}
