package com.jamiexu.app.reflectmaster.j;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jamiexu.app.reflectmaster.MainActivity;
import com.jamiexu.app.reflectmaster.j.widget.SaveFileDialog;

import java.io.FileOutputStream;
import java.io.IOException;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ImageWindow extends Window {

//  Update by Jamiexu 2020-08-25

    private Bitmap bmp = null;
    private Context context;

    public ImageWindow(Context context, XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param, Context act, Object object) {
        super(lpparam, param, act, object);
        this.context = context;
    }

    @Override
    public void show(final WindowManager manager, WindowManager.LayoutParams lp) {
        if (object.getClass().getCanonicalName().equals(""))
            return;

        if (object instanceof Bitmap)
            bmp = (Bitmap) object;
        else if (object instanceof Drawable) {
            bmp = drawableToBitmap((Drawable) object);
        }
        final LinearLayout root = new LinearLayout(act);
        root.setOrientation(LinearLayout.VERTICAL);
        ActionWindow ac = new ActionWindow(act, WindowUtils.getWm(act), WindowUtils.getLp(), root);

        Button save = new Button(act);
        save.setText("保存");
        save.setBackgroundColor(0xFF2196F3);
        save.setTextColor(Color.WHITE);
        save.setOnClickListener(v -> {
            try {
                String path = MainActivity.BASE_PATH + System.currentTimeMillis() + ".png";
                bmp.compress(Bitmap.CompressFormat.PNG, 10, new FileOutputStream(path));
                Toast.makeText(act, "图片已保存到目录：" + path, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        save.setOnLongClickListener(p1 -> {
            EditText editText = new EditText(context);
            editText.setHint("保存的路径");
            new SaveFileDialog(this.act, "保存图片：", "输入路径", (path, fileOutputStream) -> {
                bmp.compress(Bitmap.CompressFormat.PNG, 10, fileOutputStream);
                Toast.makeText(act, "图片已保存到目录：" + path, Toast.LENGTH_LONG).show();
            }).show();

            return true;
        });

        root.addView(ac.getActionBar());
        root.addView(save);


        ImageView image = new ImageView(act);
        root.addView(image);
        lp.width = -1;


        lp.height = -1;
        if (bmp != null) {
            image.setImageBitmap(bmp);
        } else if (object instanceof Drawable) {
            image.setImageDrawable((Drawable) object);
        } else
            Toast.makeText(act, "null.....", Toast.LENGTH_SHORT).show();
        manager.addView(root, lp);
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽   
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        if (w < 0 || h < 0) return null;
        // 取 drawable 的颜色格式   
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap   
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布   
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中   
        drawable.draw(canvas);
        return bitmap;
    }


}
