package formatfa.reflectmaster;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import android.support.v4.app.reflectmaster.Utils.Utils;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private TextView statu;
    private TextView id;
    private Button button;

    private String ii;
    private EditText input;
    TelephonyManager telephonyManager;
    public static String[] status = {"未注册", "已注册版本"};
    SharedPreferences sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("注册");
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
//        XmlPullParser xmlPullParser = Xml.newPullParser();
//        try {
//            xmlPullParser.setInput(new FileInputStream("121213"), "utf-8");
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        LayoutInflater.from(this).inflate(xmlPullParser, null);
        try {
            sharedPreference = getSharedPreferences("package", MODE_WORLD_READABLE);
        } catch (SecurityException e) {
            sharedPreference = getSharedPreferences("package", MODE_PRIVATE);
            e.printStackTrace();
        }
        statu = findViewById(R.id.statu);
        id = findViewById(R.id.id);
        button = findViewById(R.id.register);
        input = findViewById(R.id.input);
        button.setOnClickListener(this);
        findViewById(R.id.buy).setOnClickListener(this);
        findViewById(R.id.copy).setOnClickListener(this);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            id.setText("获取权限失败，请赋给反射大师读取手机状态权限");
        } else {
            int[] r = new int[4];
            String i = idToString("设备ID:" + telephonyManager.getDeviceId()).hashCode() + "";
            String ii = getRandomString().hashCode() + "";
            String iii = getRandomString().hashCode() + "";
            String iiii = getRandomString().hashCode() + "";
            r[0] = Integer.parseInt(i.substring(0, 3));
            r[1] = Integer.parseInt(ii.substring(ii.length() - 3, ii.length()));
            r[2] = Integer.parseInt(iii.substring(0, 3));
            r[3] = Integer.parseInt(iiii.substring(ii.length() - 3, iiii.length()));
            id.setText(idToString(r) + "");
        }

        check();
    }

    private void check() {
        int sta = sharedPreference.getInt("statu", 0);
        if (sta == 1) button.setEnabled(false);
        statu.setTextColor(Color.RED);
        statu.setText(status[sta]);
    }

    public static String getRandomString() {
        int[] i = new int[26];
        for (int j = 0; j < i.length; j++) {
            i[j] = 'a' + j;
        }
        StringBuilder stringBuilder = new StringBuilder(5);
        for (int k = 0; k <= 4; k++) {
            char c = (char) i[new Random().nextInt(i.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private int idToString(int[] byt) {
        return (byt[0] << 24)
                + (byt[1] << 16)
                + (byt[2] << 8)
                + byt[3];
    }


    private String idToString(String id) {
        StringBuilder ab = new StringBuilder();
        for (char c : id.toCharArray()) {
            ab.append((int) c);
        }
        return ab.toString();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register) {

            String i = id.getText().toString();

            String in = input.getText().toString();
            if (in.length() == 0 || i.length() == 0) return;
            if ((Utils.$(i) + "").equals(in)) {
                sharedPreference.edit().putInt("statu", 1).commit();
                sharedPreference.edit().putString("register", in).commit();
                sharedPreference.edit().putString("fid", i).commit();
                Toast.makeText(this, "注册成功!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "注册码错误!", Toast.LENGTH_LONG).show();
            }
            check();
        } else if (view.getId() == R.id.copy) {
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            //    cm.setText();
            cm.setPrimaryClip(ClipData.newPlainText("register", id.getText().toString()));
            Toast.makeText(this, "复制成功!", Toast.LENGTH_LONG).show();
        } else if (view.getId() == R.id.buy) {

            try {
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=2049896440";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "似乎还没有安装手机QQ...", Toast.LENGTH_LONG).show();

            }
        }else if (view.getId() == R.id.buy) {

            try {
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=3510088586";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "似乎还没有安装手机QQ...", Toast.LENGTH_LONG).show();

            }
        }
    }
}
