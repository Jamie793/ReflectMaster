package android.support.v4.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EditFieldWindow extends Window {

    @SuppressLint("SetTextI18n")
    @Override
    public void show(final WindowManager manager, WindowManager.LayoutParams lp) {
        final EditText value = new EditText(act);
        value.setTextColor(Color.RED);
        final LinearLayout layout = new LinearLayout(act);
        layout.setBackgroundColor(Color.BLACK);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout buttonLayout = new LinearLayout(act);

        Button close = new Button(act);
        close.setText("关闭");
        close.setOnClickListener(p1 -> manager.removeView(layout));
        buttonLayout.addView(close);


        Button ok;

        if (type == TYPE_EDIT) {
            ok = new Button(act);
            ok.setText("修改");
            ok.setOnClickListener(p1 -> {
                Object result = MasterUtils.baseTypeParse(field.getType().getCanonicalName(), value.getText().toString());
                try {
                    field.set(object, result);
                } catch (IllegalAccessException e) {
                    Toast.makeText(act, "set value err:" + e.toString(), Toast.LENGTH_SHORT).show();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(act, "set value err:" + e.toString(), Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(act, "修改完成", Toast.LENGTH_SHORT).show();
                manager.removeView(layout);
            });

        } else {
            ok = new Button(act);
            ok.setText("持久修改");
            ok.setOnClickListener(p1 -> {

                Toast.makeText(act, "修改完成", Toast.LENGTH_SHORT).show();
                manager.removeView(layout);
            });

        }

        buttonLayout.addView(ok);

        layout.addView(buttonLayout);


        TextView msg = new TextView(act);
        msg.setTextColor(Color.BLUE);
        msg.setText("名字:" + field.getName());
        try {
            value.setText(String.valueOf(field.get(object)));
        } catch (IllegalAccessException ignored) {
        } catch (IllegalArgumentException ignored) {
        }

        value.setHint(field.getType().getCanonicalName());

        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layout.addView(msg);
        layout.addView(value);
        manager.addView(layout, lp);

    }

    private Field field;


    private int type;
    static int TYPE_EDIT = 0;

    EditFieldWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param, Context act, Object object, Field thiz, int type) {
        super(lpparam, param, act, object);
        field = thiz;
        this.type = type;
//        XSharedPreferences sp = new XSharedPreferences(Entry.PACKAGENAME, "");
    }

}
