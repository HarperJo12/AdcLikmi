package com.android.adclikmi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
 * Marcel 2019 *
 **/

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private Button login;
    private DelayedProgressDialog progressDialog;
    private Boolean close=false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.frm_edit_username);
        password = (EditText) findViewById(R.id.frm_edit_password);
        login = (Button) findViewById(R.id.frm_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validationAll()) {
                    showDProgressDialog();
                    hideKeyboard();
                    postData();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && getSupportFragmentManager().getBackStackEntryCount() == 0)
        {
            if(!close){
                Toast.makeText(getApplicationContext(), "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
                close=true;
            }else if(close){
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean validationAll() {
        boolean tmp = true;
        if (!getValidation(true, false, this.username, "Username")) {
            tmp = false;
        }
        if (getValidation(true, false, this.password, "Password")) {
            if (getValidLength(true, false, this.password, "Password", 6)) {
                return tmp;
            }
        }
        return false;
    }

    public void hideKeyboard(){
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void showDProgressDialog() {
        progressDialog = new DelayedProgressDialog();
        progressDialog.show(getSupportFragmentManager(), "tag");
    }

    public void dismissDProgressDialog() {
        progressDialog.dismiss();
    }

    public boolean getValidLength(boolean submit, boolean hasFocus, EditText text, String alert, int length){
        boolean bool=false;
        if(!hasFocus){
            String tmp=text.getText().toString().trim();
            if(tmp.length()<length) {
                text.setError(alert + " tidak boleh kurang dari " + length + " digit!");
                if(submit){
                    requestFocus(text);}
                bool=false;
            }else{
                text.setError(null);
                bool= true;
            }
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
                    requestFocus(text);}
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
            obj.put("username", username.getText().toString().trim());
            obj.put("password", password.getText().toString());

            AndroidNetworking.post("http://DOMAINNYAHARUSDIISI/api/login")
                    .addJSONObjectBody(obj) // posting json
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                dismissDProgressDialog();
                                SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                                SharedPreferences.Editor Edit = sp.edit();
                                Edit.putString("token", response.getString("token"));
                                Edit.putString("username", response.getString("username"));
                                Edit.putInt("level", response.getInt("level"));
                                Edit.commit();
                                Toast.makeText(getApplicationContext(), "Berhasil login!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            dismissDProgressDialog();
                            Toast.makeText(getApplicationContext(), "Gagal login!", Toast.LENGTH_SHORT).show();
                            username.setError("Username atau password salah!");
                            password.setError("Username atau password salah!");
                            // handle error
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
