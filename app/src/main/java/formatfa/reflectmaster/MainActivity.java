package formatfa.reflectmaster;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.MasterUtils;
import android.support.v4.app.Utils.FileUtils;
import android.support.v4.app.reflectmaster.CoreInstall;
import android.support.v4.app.reflectmaster.Utils.Utils;
import android.support.v4.app.widget.ReflectView2;
import android.support.v4.app.widget.WindowDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.luajava.LuaException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import dalvik.system.DexClassLoader;


public class MainActivity extends AppCompatActivity implements OnItemClickListener {
    private LuaDexLoaders luaDexLoader;
    public static final String BASE_PATH = Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/";
    //    public static final String APP_INFO = "反射大师1.1\nauthor:FormatFa and JamieXu";
    public static final HashSet<String> SELECTED_APK_LIST = new HashSet<>();
    public static SharedPreferences sharedPreferences;
    private final String PACKAGE_NAME = "packages";
    private ListView list;
    private EditText edit_search;
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
        setContentView(R.layout.apklist);
        initView();
        requestPermission();
        refreshApkList();
    }

    private void initData() {
        sharedPreferences = getSharedPreferences("package", MODE_WORLD_WRITEABLE);
        MasterUtils.windowSize = sharedPreferences.getInt("width", 700);
        MasterUtils.rotate = sharedPreferences.getBoolean("rotate", true);

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
        this.list = findViewById(R.id.listview);
        this.list.setTextFilterEnabled(true);
        this.list.setOnItemClickListener(this);
        this.edit_search = findViewById(R.id.edite_filter);
        this.swipeRefreshLayout = findViewById(R.id.swipeLayout);
        this.swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        this.swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshApkList();
            this.swipeRefreshLayout.setRefreshing(false);
        });
        this.edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    list.clearTextFilter();
                } else {
                    list.setFilterText(s.toString());
                }
            }
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
    public void onItemClick(AdapterView<?> p1, View v, final int position, long id) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater in = getMenuInflater();
        in.inflate(R.menu.main, menu);

        return true;
    }


    public void joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
        }
    }

//    private void save() {
//        StringBuilder sb = new StringBuilder();
//        for (String s : this.packs) {
//            sb.append(s);
//            sb.append(";");
//        }
//        sharedPreferences.edit().putString(this.PACKAGE_NAME, sb.toString()).apply();
//    }
//
//    private void refresh() {
//        String s = sharedPreferences.getString(this.PACKAGE_NAME, null);
//        if (s == null) return;
//        String[] all = s.split(",");
//        packs.addAll(Arrays.asList(all));
//    }

    private void showHelp() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("协议");
        ab.setMessage(R.string.about);
        ab.setPositiveButton("同意", (p1, p2) -> sharedPreferences.edit().putBoolean("first", false).apply());
        ab.setNegativeButton("不同意", (p1, p2) -> finish());
        ab.show().setCancelable(false);
    }


    private void firstOpen() {

        //使用协议
        if (sharedPreferences.getBoolean("first", true)) {
            showHelp();
        }

        //创建文件夹复制文件等
        final File file = new File(BASE_PATH + "lua");
        final File file2 = new File(BASE_PATH + "lib");

        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Utils.showToast(MainActivity.this, msg.obj.toString(), 0);
                super.handleMessage(msg);
            }
        };


        if (!file.isDirectory()) {
            file.mkdirs();
            Utils.showToast(this, "解压Lua资源中...", 0);

            new Thread(() -> {
                boolean status = FileUtils.deZip(getApplicationInfo().sourceDir, file.toString(), "assets/lua/");
                if (status) {
                    handler.obtainMessage(22, "解压Lua完成").sendToTarget();
                } else {
                    handler.obtainMessage(22, "解压Lua出现错误").sendToTarget();
                }
            }).start();

        }

        if (!file2.exists()) {
            Utils.showToast(this, "解压So资源中...", 0);
            new Thread(() -> {
                boolean status = FileUtils.deZip(getApplicationInfo().sourceDir, file2.toString(), "lib");
                if (status) {
                    handler.obtainMessage(22, "解压So完成").sendToTarget();
                } else {
                    handler.obtainMessage(22, "解压So出现错误").sendToTarget();
                }
            }).start();
        }
    }


    private void about() {
        WindowDialog wd = new WindowDialog(this);
        WebView web = new WebView(this);
        web.loadUrl("file:///android_asset/about.html");
        web.getSettings().setSupportZoom(true);
        wd.setView(web);
        wd.show(500, 600);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exit) {

            finish();
        }
        if (item.getItemId() == R.id.about) {

            about();

        } else if (item.getItemId() == R.id.register) {
            Intent i = new Intent();
            i.setClass(this, Register.class);
            startActivity(i);
        }
        if (item.getItemId() == R.id.group) {

            joinQQGroup("jKcW_juMAQbVCtIXsZwTf7DsFBSbri9b");

        } else if (item.getItemId() == R.id.jgroup) {

            joinQQGroup("q33iBRmq-koN9JiTbMKM7AS4W0B49oEi");

        } else if (item.getItemId() == R.id.core) {

            Intent i = new Intent(MainActivity.this, CoreInstall.class);
            startActivity(i);

        } else if (item.getItemId() == R.id.lua_script) {
            if (checkAppInstalled(this)) {
                Intent intent = this.getPackageManager().getLaunchIntentForPackage("com.androlua");
                startActivity(intent);
            } else {
                Utils.showToast(this, "请先安装Androlua", 0);
            }


        } else if (item.getItemId() == R.id.setting) {

            @SuppressLint("InflateParams")
            View view = getLayoutInflater().inflate(R.layout.setting, null);

            view.setBackgroundColor(Color.BLACK);
            WindowDialog wd = new WindowDialog(this);
            final EditText se = view.findViewById(R.id.setting_windowsize);

            se.setTextColor(Color.WHITE);
            se.setText(String.valueOf(sharedPreferences.getInt("width", 700)));
            view.findViewById(R.id.preview).setOnClickListener(p1 -> {
                int i = Integer.parseInt(se.getText().toString());

                WindowDialog wd1 = new WindowDialog(MainActivity.this);
                ReflectView2 re = new ReflectView2(MainActivity.this, sharedPreferences.getBoolean("rotate", true));

                wd1.setTitle("预览");
                wd1.setView(re);
                wd1.show(i, i);


            });

            view.findViewById(R.id.ok).setOnClickListener(p1 -> {
                int i = Integer.parseInt(se.getText().toString());
                sharedPreferences.edit().putInt("width", i).apply();
                Toast.makeText(MainActivity.this, "ojbk", Toast.LENGTH_SHORT).show();
            });

            CheckBox box = view.findViewById(R.id.setting_windowsearch);
            box.setChecked(sharedPreferences.getBoolean("windowsearch", false));
            box.setOnCheckedChangeListener((compoundButton, b) -> {
                sharedPreferences.edit().putBoolean("windowsearch", b).apply();
                MasterUtils.isUseWindowSearch = b;

            });

            CheckBox showfloat = view.findViewById(R.id.setting_float);
            showfloat.setChecked(sharedPreferences.getBoolean("float", true));
            showfloat.setOnCheckedChangeListener((compoundButton, b) -> sharedPreferences.edit().putBoolean("float", b).apply());

            CheckBox rotate = view.findViewById(R.id.setting_rotate);
            rotate.setChecked(sharedPreferences.getBoolean("rotate", true));
            rotate.setOnCheckedChangeListener((compoundButton, b) -> sharedPreferences.edit().putBoolean("rotate", b).apply());

            CheckBox sysapp = view.findViewById(R.id.setting_sysapp);
            sysapp.setChecked(sharedPreferences.getBoolean("sysapp", false));
            sysapp.setOnCheckedChangeListener((compoundButton, b) -> sharedPreferences.edit().putBoolean("sysapp", b).apply());
            CheckBox newThread = view.findViewById(R.id.setting_thread);
            newThread.setChecked(sharedPreferences.getBoolean("newthread", false));
            newThread.setOnCheckedChangeListener((compoundButton, b) -> {
                MasterUtils.newThread = b;
                sharedPreferences.edit().putBoolean("newthread", b).apply();
            });

            wd.setTitle("确认配置");
            wd.setView(view);
            wd.show(800, 800);
        }
//        else if (item.getItemId() == R.id.update) {
//            //更新
//        }

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
                if (grantResults.length > 0)
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "获取权限失败", Toast.LENGTH_LONG).show();
                    }
                break;
            case 2:
                if (grantResults.length > 0)
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "获取权限失败", Toast.LENGTH_LONG).show();
                    }
                break;
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

}