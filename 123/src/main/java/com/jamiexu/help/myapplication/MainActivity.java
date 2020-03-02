package com.jamiexu.help.myapplication;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.jamiexu.utils.Resource.InstalledLoader;
import com.jamiexu.utils.Resource.ResourceFactory;
import com.jamiexu.utils.Resource.ResourceType;
import com.jamiexu.utils.Resource.UnInstallLoader;
import com.jamiexu.utils.Resource.XmlParser;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private static final String PATH_NAME = "/storage/emulated/0/ReflectMaster/base.apk";
    private static final String PACKAGE_NAME = "formatfa.reflectmaster";


    private final String TAG = this.getClass().getCanonicalName();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //加载没安装的apk
        UnInstallLoader unInstallLoader = (UnInstallLoader) ResourceFactory.getInstance(this,1);
        Resources resources = unInstallLoader.loadResource(PATH_NAME);
        View view = LayoutInflater.from(this).inflate(resources.getXml(resources.getIdentifier("float_0", ResourceType.layout, unInstallLoader.getPackageInfo(PATH_NAME).packageName)), null);
        setContentView(view);
        ImageView imageView = view.findViewById(0x7F07004A);
        imageView.setImageBitmap(BitmapFactory.decodeResource(resources, 0x7F060055));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(123123);
            }
        });


        //加载安装了的apk
//        InstalledLoader installedLoader = (InstalledLoader) ResourceFactory.getInstance(this,0);
//        Resources resources = installedLoader.getResource(PACKAGE_NAME);
//        Log.d(TAG,resources+"");
//        final XmlPullParser xmlPullParser = resources.getXml(resources.getIdentifier("float_0", ResourceType.layout, PACKAGE_NAME));
//        View view = LayoutInflater.from(this).inflate(xmlPullParser, null);
//        XmlParser.parse(xmlPullParser);
//
//        setContentView(view);
//        ImageView imageView = view.findViewById(0x7F07004A);
//        System.out.println(imageView.getId());
//        imageView.setImageBitmap(BitmapFactory.decodeResource(resources, 0x7F060055));
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println(123123);
//                XmlParser.parse(xmlPullParser);
//            }
//        });






    }
}
