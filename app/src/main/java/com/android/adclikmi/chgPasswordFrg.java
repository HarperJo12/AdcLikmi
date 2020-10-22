package com.android.adclikmi;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Marcel 2019 *
 */
public class chgPasswordFrg extends Fragment {
    public String title = "Change Password";
    private EditText pswLama, nPsw, cnPsw;
    private Button save, cancel;


    public chgPasswordFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chg_password, container, false);
        ((MainActivity) getActivity()).setTitle(title);
        pswLama = (EditText) view.findViewById(R.id.frm_edit_password_lama);
        nPsw = (EditText) view.findViewById(R.id.frm_edit_password);
        cnPsw = (EditText) view.findViewById(R.id.frm_edit_password_conf);
        save = (Button) view.findViewById(R.id.frm_save);
        cancel = (Button) view.findViewById(R.id.frm_cancel);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getValidation(true, false, pswLama, "Current Password ")
                        && getValidation(true, false, nPsw, "New Password ")
                        && getValidation(true, false, cnPsw, "Retype New Password ")
                        && getValidLength(true, false, nPsw, "New Password", 6)
                        && getValidLength(true, false, cnPsw, "Retype New Password", 6)
                        && getValidationMatch(nPsw, cnPsw)) {
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

    public boolean getValidationMatch(EditText text, EditText text2) {
        boolean bool = false;
        String tmp = text.getText().toString();
        String tmp2 = text2.getText().toString();
        if (!tmp.equals(tmp2)) {
            text.setError("Kedua field password harus sama!");
            text2.setError("Kedua field password harus sama!");
            ((MainActivity) getActivity()).requestFocus(text);
            bool = false;
        } else {
            text.setError(null);
            text2.setError(null);
            bool = true;
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

    public boolean getValidLength(boolean submit, boolean hasFocus, EditText text, String alert, int length){
        boolean bool=false;
        if(!hasFocus){
            String tmp=text.getText().toString().trim();
            if(tmp.length()<length) {
                text.setError(alert + " tidak boleh kurang dari " + length + " digit!");
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

    public void postData() {
        try {
            JSONObject obj = new JSONObject();

            obj.put("Username", ((MainActivity) getActivity()).getUs().getUsername());
            obj.put("Old_Password", pswLama.getText().toString());
            obj.put("Password", nPsw.getText().toString());

            AndroidNetworking.post(((MainActivity) getActivity()).getBaseUrl() + "rstpassword")
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
                                if (!response.getBoolean("error")) {
                                    Toast.makeText(getContext(), response.getString("response"), Toast.LENGTH_SHORT).show();
                                    ((MainActivity) getActivity()).getSharedPreferences("user", 0).edit().clear().commit();
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivity(intent);
                                } else
                                    Toast.makeText(getContext(), response.getString("response"), Toast.LENGTH_SHORT).show();
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
