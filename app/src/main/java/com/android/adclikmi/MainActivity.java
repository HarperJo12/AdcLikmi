package com.android.adclikmi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.adclikmi.Model.user;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Marcel 2019 *
 **/

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener{
    private ProgressBar progressBar;
    private int kls=0;
    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private int searchState;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private TextView mUsername, tvText;
    private AlertDialog dialog;
    private Toolbar toolbar;
    private SearchView searchView;
    private DelayedProgressDialog progressDialog;
    private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    private String baseUrl = "https://HARUSDIISIDOMAINNYA/api/";
    private user us;
    private Boolean close=false ;

    public int getSearchState() {
        return searchState;
    }

    public void setSearchState(int searchState) {
        this.searchState = searchState;
    }

    public String getBaseUrl() {return baseUrl;}

    public void setBaseUrl(String baseUrl) {this.baseUrl = baseUrl;}

    public user getUs() {
        return us;
    }

    public void setUs(user us) {
        this.us = us;
    }

    public Boolean getClose() {
        return this.close;
    }

    public void setClose(Boolean close2) {
        this.close = close2;
    }

    public DelayedProgressDialog getProgressDialog() {
        return progressDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set first view fragment
        searchState=0;

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        mUsername = (TextView) findViewById(R.id.main_username);
        checkLogin();
        setHome();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        toolbar = findViewById(R.id.toolbarHom);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addDataNavDrawer(int type){
        //0=mahasiswa 1=karyawan
        if(!mNavItems.isEmpty()) mNavItems.clear();
        kls=type;
        if(kls==0) {
            mNavItems.add(new NavItem("Home", R.drawable.ic_action_home));
            mNavItems.add(new NavItem("Progress", R.drawable.ic_action_list));
            mNavItems.add(new NavItem("About", R.drawable.ic_action_about));
            mNavItems.add(new NavItem("Change Password", R.drawable.ic_action_chg));
            mNavItems.add(new NavItem("Logout", R.drawable.ic_action_logout));
        }else if(kls==1){
            mNavItems.add(new NavItem("Home", R.drawable.ic_action_home));
            mNavItems.add(new NavItem("About", R.drawable.ic_action_about));
            mNavItems.add(new NavItem("Change Password", R.drawable.ic_action_chg));
            mNavItems.add(new NavItem("Report", R.drawable.ic_action_list));
            mNavItems.add(new NavItem("Logout", R.drawable.ic_action_logout));
        }

        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawerLayout,R.string.drawer_open,  R.string.drawer_close);
    }

    //change icon between hamburger and back button
    @Override
    public void onBackStackChanged() {
        Log.d("back", "back stack berubah jml "+ getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }else{
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        mDrawerToggle.syncState();
    }

    public void changeLayout(Fragment frg){
        FragmentTransaction fragmentMan = getSupportFragmentManager().beginTransaction();
        hideKeyboard();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fragment previous = getSupportFragmentManager().findFragmentById(R.id.mainContent);
            Slide out = new Slide();
            out.setDuration(200);
            previous.setExitTransition(out);

            Explode enter = new Explode();
            enter.setStartDelay(201);
            enter.setDuration(200);
            frg.setEnterTransition(enter);

            fragmentMan.replace(R.id.mainContent, frg).addToBackStack(null).commitAllowingStateLoss();
        }else{
            fragmentMan.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentMan.replace(R.id.mainContent, frg).addToBackStack(null).commit();
        }

        Log.d("back","back stack num "+getSupportFragmentManager().getBackStackEntryCount());
    }

    public void changeLayoutMain() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            hideKeyboard();
            getSupportFragmentManager().popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        Log.d("back","back stack num "+getSupportFragmentManager().getBackStackEntryCount());
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
    public void setUntouchable(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void setTouchable(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void initPercentProgressBar(){
        DrawerLayout layout=(DrawerLayout) findViewById(R.id.drawerLayout);
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(0);

        LinearLayout ll= new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER);
        ll.setPadding(30, 30, 30, 30);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
        params.gravity = Gravity.CENTER;
        ll.setLayoutParams(params);

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        tvText = new TextView(this);
        tvText.setText("0%");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(params);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
    }

    public void setPercentProgressBar(long curr, long maxx){
        int cur = Long.valueOf(curr).intValue();
        int max = Long.valueOf(maxx).intValue();
        progressBar.setProgress(cur);
        progressBar.setMax(max);
        int tmp= (cur*100)/max;
        tvText.setText(tmp+"%");
    }

    public void showPercentProgressBar(){
        initPercentProgressBar();
        setUntouchable();
        dialog.show();
    }

    public void dismissPercentProgressBar(){
        setTouchable();
        if(dialog.isShowing())
        dialog.dismiss();
    }

    public void showDProgressDialog() {
        progressDialog = new DelayedProgressDialog();
        //progressDialog.setMode(0);
        progressDialog.show(getSupportFragmentManager(), "tag");
        setUntouchable();
    }

    public void dismissDProgressDialog() {
        progressDialog.dismiss();
        setTouchable();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        Log.d("pressed", "onOptionsItemSelected: " + item.getItemId()+" "+R.id.home);
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //handle key back button at navigation
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

    //handle back pressed if backstack not zero, then execute
    @Override
    public void onBackPressed() {
        Log.d("back", "onBackPressed: ");
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            hideKeyboard();
            setTouchable();
        } else if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else if(!mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem search=menu.findItem(R.id.action_search);

        // 0 = tanpa search icon 1=izin 2=ujian 3=perpindahan 4=sidang
        if(searchState==0){
            search.setVisible(false);
        }else{
            search.setVisible(true);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                selectSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                selectSearch(query);
                return false;
            }
        });
        return true;
    }

    private void selectSearch(String query){
        final FragmentManager fm = getSupportFragmentManager();
        switch (searchState){
            case 1:lstTiketIzinFrg frg = (lstTiketIzinFrg)fm.findFragmentById(R.id.mainContent);
                tiketIzinAdapter adapter=frg.getAdapter();
                frg.setQuery(query);
                adapter.getFilter().filter(query);break;
            case 2:lstTiketUjianFrg frg2 = (lstTiketUjianFrg)fm.findFragmentById(R.id.mainContent);
                tiketUjianAdapter adapter2=frg2.getAdapter();
                frg2.setQuery(query);
                adapter2.getFilter().filter(query);break;
            case 3:lstTiketPerpindahanFrg frg3 = (lstTiketPerpindahanFrg)fm.findFragmentById(R.id.mainContent);
                tiketPerpindahanAdapter adapter3=frg3.getAdapter();
                frg3.setQuery(query);
                adapter3.getFilter().filter(query);break;
            case 4:lstTiketSidangFrg frg4 = (lstTiketSidangFrg)fm.findFragmentById(R.id.mainContent);
                tiketSidangAdapter adapter4=frg4.getAdapter();
                frg4.setQuery(query);
                adapter4.getFilter().filter(query);break;
            default:
        }
    }

    public void checkLogin() {
        SharedPreferences sp = getSharedPreferences("user", 0);
        String token = sp.getString("token", null);
        String username = sp.getString("username", null);
        int level = sp.getInt("level", 0);
        mUsername.setText(username);
        us = new user(username, level, token);
    }

    public void setHome() {
        Fragment fragment;
        if (us.getLevel() == 1) {
            fragment = new homeMhsFrg();
            addDataNavDrawer(0);
        } else {
            fragment = new homeKywFrg();
            addDataNavDrawer(1);
        }
        FragmentTransaction fragmentMan = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        fragmentMan.add(R.id.mainContent, fragment);
        fragmentMan.isAddToBackStackAllowed();
        fragmentMan.commit();
    }

    public void logout() {
        getSharedPreferences("user", 0).edit().clear().commit();
        postData();
    }

    public void postData() {
        AndroidNetworking.get(getBaseUrl()+"logout")
                .addHeaders("Authorization", "Bearer "+us.getToken().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getApplicationContext(), "Gagal Logout!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void selectItemFromDrawer(int position) {
        Fragment fragment = null;
        boolean psw=true;

        if(kls==0) {
            switch (position) {
                case 0:
                    fragment = new homeMhsFrg();
                    break;
                case 1:
                    showDProgressDialog();
                    fragment = new lstTiketMahasiswaFrg();
                    break;
                case 2:
                    fragment = new aboutFrg();
                    break;
                case 3:
                    fragment = new chgPasswordFrg();
                    psw=false;
                    mDrawerList.setItemChecked(position, true);
                    mDrawerLayout.closeDrawer(mDrawerPane);
                    changeLayout(fragment);
                    break;
                case 4:
                    logout();
                    break;

                default:
                    break;
            }
        }else if(kls==1) {
            switch (position) {
                case 0:
                    fragment = new homeKywFrg();
                    break;
                case 1:
                    fragment = new aboutFrg();
                    break;
                case 2:
                    fragment = new chgPasswordFrg();
                    psw=false;
                    mDrawerList.setItemChecked(position, true);
                    mDrawerLayout.closeDrawer(mDrawerPane);
                    changeLayout(fragment);
                    break;
                case 3:
                    fragment = new reportFrg();
                    break;
                case 4:
                    logout();
                    break;

                default:
                    break;
            }
        }

        if (fragment != null && psw) {
            FragmentTransaction fragmentMan = getSupportFragmentManager().beginTransaction();
            fragmentMan.replace(R.id.mainContent, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mDrawerPane);
        }else{
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
}

class NavItem {
    String mTitle;
    int mIcon;

    public NavItem(String title, int icon) {
        mTitle = title;
        mIcon = icon;
    }
}

class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItem> mNavItems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_list_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        titleView.setText( mNavItems.get(position).mTitle );
        iconView.setImageResource(mNavItems.get(position).mIcon);

        return view;
    }
}