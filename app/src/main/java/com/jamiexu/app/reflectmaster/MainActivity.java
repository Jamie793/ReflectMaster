package com.jamiexu.app.reflectmaster;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.jamiexu.app.reflectmaster.j.MasterUtils;

import com.jamiexu.app.reflectmaster.j.reflectmaster.CoreInstall;
import com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jamiexu.utils.file.ZipUtils;
import com.luajava.LuaException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import dalvik.system.DexClassLoader;


public class MainActivity extends AppCompatActivity {
    private LuaDexLoaders luaDexLoader;
    public static final String BASE_PATH = Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/";
    //    public static final String APP_INFO = "反射大师1.1\nauthor:FormatFa and JamieXu";
    public static final HashSet<String> SELECTED_APK_LIST = new HashSet<>();
    public static SharedPreferences sharedPreferences;
    private final String PACKAGE_NAME = "packages";
    private ListView list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final String[] permission = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MasterUtils.nowAct = this;
        luaDexLoader = new LuaDexLoaders(this);
//        FWindow(this, null);
        initData();
        firstOpen();
        initView();
        requestPermission();
        refreshApkList();
    }

    private void initData() {
        try {
            sharedPreferences = getSharedPreferences("package", MODE_WORLD_READABLE);
        } catch (Exception e) {
            e.printStackTrace();
            sharedPreferences = getSharedPreferences("package", MODE_PRIVATE);
        }
        String[] packages = sharedPreferences.getString("packages", "").split(";");
        SELECTED_APK_LIST.addAll(Arrays.asList(packages));
    }

    public static void saveSelectedApk() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String p : SELECTED_APK_LIST)
            stringBuilder.append(p).append(";");
        sharedPreferences.edit().putString("packages", stringBuilder.toString()).apply();
    }

    private void initView() {
        setContentView(R.layout.apklist);
        this.list = findViewById(R.id.listview);
        this.list.setTextFilterEnabled(true);
        this.swipeRefreshLayout = findViewById(R.id.swipeLayout);
        this.swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        this.swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshApkList();
            this.swipeRefreshLayout.setRefreshing(false);
        });
    }


    private void refreshApkList() {
        new ApkLoadAsync(this, this.list).execute();
    }

    public void requestPermission() {
        int i = 1;
        for (String s : permission) {
            if (ContextCompat.checkSelfPermission(this, s) != 0) {
                ActivityCompat.requestPermissions(this, permission, i++);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater in = getMenuInflater();
        in.inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.isEmpty()) {
                    MainActivity.this.list.clearTextFilter();
                } else {
                    MainActivity.this.list.setFilterText(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) {
                    MainActivity.this.list.clearTextFilter();
                } else {
                    MainActivity.this.list.setFilterText(s);
                }
                return false;
            }
        });
        return true;
    }


    public void joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2" +
                "Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
        }
    }

    private void showHelp() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);

        TextView title = new TextView(this);
        title.setText("用户协议：");
        title.setTextSize(18);
        title.setTextColor(getResources().getColor(R.color.colorAccent));

        TextView textView = new TextView(this);
        textView.setText(getResources().getText(R.string.about));
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
        textView.setPadding(30, 20, 0, 0);

        linearLayout.addView(title);
        linearLayout.addView(textView);

        AlertDialog ab = new AlertDialog.Builder(this)
                .setView(linearLayout)
                .setPositiveButton("同意", (p1, p2) -> sharedPreferences.edit().putBoolean(
                        "first", false).apply())
                .setNegativeButton("不同意", (p1, p2) -> finish())
                .show();
        ab.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(0xFFFF4081);
        ab.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(0xFFFF4081);
    }


    private void firstOpen() {

        //使用协议
        if (sharedPreferences.getBoolean("first", true)) {
            sharedPreferences.edit().putBoolean("float", true)
                    .putBoolean("newthread", true).apply();
            showHelp();
        }

//        Log.i(getClass().getCanonicalName(), getApplicationInfo().sourceDir);

        //创建文件夹复制文件等
        final File file = new File(BASE_PATH + "lua");
        final File file2 = new File(BASE_PATH + "lib");
        final File file3 = new File(BASE_PATH + "icon.png");

        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Utils.showToast(MainActivity.this, msg.obj.toString(), 0);
                super.handleMessage(msg);
            }
        };

        if (!file3.exists())
            ZipUtils.extraceFile(getApplicationInfo().sourceDir, file3.getAbsolutePath(),
                    "res/drawable/ic_launcher.png");


        if (!file.isDirectory()) {
            file.mkdirs();
            Utils.showToast(this, "解压Lua资源中...", 0);

            new Thread(() -> {
                boolean status = com.jamiexu.utils.file.ZipUtils.extraceDir(
                        getApplicationInfo().sourceDir, file.toString(), "assets/lua/");
                if (status) {
                    handler.obtainMessage(0, "解压Lua资源完成");
                } else {
                    handler.obtainMessage(0, "解压Lua资源失败");
                }
            }).start();

        }

        if (!file2.exists()) {
            Utils.showToast(this, "解压So资源中...", 0);
            new Thread(() -> {
                boolean status = ZipUtils.extraceDirs(getApplicationInfo().sourceDir,
                        file2.getParent(), "lib");
                if (status) {
                    handler.obtainMessage(0, "解压So资源完成");
                } else {
                    handler.obtainMessage(0, "解压So资源失败");
                }
            }).start();
        }
    }


    @SuppressLint("ResourceType")
    private void about() {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pay));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.main_about, null,
                        false))
                .setNegativeButton("打赏作者", (v, e) -> {
                    new AlertDialog.Builder(this).setView(imageView).show();
                })
                .setPositiveButton("了解", null)
                .show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().
                getColor(R.color.colorAccent));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().
                getColor(R.color.colorAccent));

    }


    @SuppressLint("CommitPrefEdits")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exit) {
            finish();
        } else if (item.getItemId() == R.id.about) {
            about();

        } else if (item.getItemId() == R.id.group) {

            joinQQGroup("SBkakwduo86-IUNuyD3URdiinFc7fwTc");

        } else if (item.getItemId() == R.id.core) {

            Intent i = new Intent(MainActivity.this, CoreInstall.class);
            startActivity(i);

        } else if (item.getItemId() == R.id.lua_script) {
            if (checkAppInstalled(this)) {
                Intent intent = this.getPackageManager()
                        .getLaunchIntentForPackage("com.androlua");
                startActivity(intent);
            } else {
                Utils.showToast(this, "请先安装Androlua", 0);
            }


        } else if (item.getItemId() == R.id.setting) {

            @SuppressLint("InflateParams")
            View view = getLayoutInflater().inflate(R.layout.setting, null);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .setPositiveButton("确定", null)
                    .show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
            alertDialog.setCanceledOnTouchOutside(false);

            CheckBox chb_sysapp, chb_thread, chb_float;
            chb_sysapp = view.findViewById(R.id.setting_sysapp);
            chb_thread = view.findViewById(R.id.setting_thread);
            chb_float = view.findViewById(R.id.setting_float);
            boolean sysapp, thread, floatt;

            sysapp = sharedPreferences.getBoolean("sysapp", false);
            thread = sharedPreferences.getBoolean("newthread", false);
            floatt = sharedPreferences.getBoolean("float", false);

            chb_sysapp.setChecked(sysapp);
            chb_thread.setChecked(thread);
            chb_float.setChecked(floatt);


            chb_sysapp.setOnClickListener((v) -> {
                sharedPreferences.edit().putBoolean("sysapp", chb_sysapp.isChecked()).apply();
            });


            chb_thread.setOnClickListener((v) -> {
                sharedPreferences.edit().putBoolean("newthread", chb_thread.isChecked()).apply();
            });


            chb_float.setOnClickListener((v) -> {
                sharedPreferences.edit().putBoolean("float", chb_float.isChecked()).apply();
            });

        }
        return true;
    }


    private boolean checkAppInstalled(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("com.androlua", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
            case 2:
            case 3:
                if (grantResults.length > 0)
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "获取权限失败", Toast.LENGTH_LONG).show();
                    }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public ArrayList<ClassLoader> getClassLoaders() {
        // TODO: Implement this method
        return luaDexLoader.getClassLoaders();
    }


    public HashMap<String, String> getLibrarys() {
        return luaDexLoader.getLibrarys();
    }


    public DexClassLoader loadDex(String path) throws LuaException {
        return luaDexLoader.loadDex(path);
    }


    public static boolean isRoot() {
        try {
            Runtime.getRuntime().exec("su");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void copyFile(String from, String to) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            OutputStream outputStream = process.getOutputStream();
            String p = new File(to).getParent();
            outputStream.write(("mkdir -p " + p + " && cp " + from + " " + to + " && chmod 777 " + to).getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}