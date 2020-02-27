package android.support.v4.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.reflectmaster.Utils.Utils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.luajava.LuaException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import formatfa.reflectmaster.LuaDexLoaders;


public class ScriptWindow extends Window {

    private LuaDexLoaders luaDexLoader;
    private static int screenW, screenH;
    private LuaExecutor luaExecutor;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1:
                    getLog().append("" + msg.obj);
                    getLog().append("\n");
                    appendsize += 1;
                    if (appendsize > 200) {
                        appendsize = 0;
                        getLog().setText("");
                    }
                    break;
                case 2:
                    Object obj = msg.obj;
                    FieldWindow w = new FieldWindow(getLpparam(), getParam(), getAct(), obj);
                    w.show(null, null);
                    break;
                case 3:
                    Toast.makeText(getLog().getContext(), "" + msg.obj, Toast.LENGTH_SHORT).show();

                    break;
                case 4:
                    ClipboardManager manager = (ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
                    manager.setPrimaryClip(ClipData.newPlainText("ReflectMaster", "" + msg.obj));
                    Toast.makeText(getLog().getContext(), "复制obj成功:" + msg.obj, Toast.LENGTH_SHORT).show();

                    break;

            }
        }
    };
    private LinearLayout layout;

    private WindowManager.LayoutParams layoutParams;
    private ActionWindow actionWindow;
    private Button execute;
    private EditText script;
    private TextView log;

    public TextView getLog() {
        return log;
    }

    public Context getAct() {
        return act;
    }


    private int appendsize = 0;

    public static class rf {
        public static Object thiz;
        public static Context act;
        public static Handler handler;

        public static Object getThis() {
            return thiz;
        }

        public static Context getAct() {
            return act;
        }

        public static void window(Object obj) {
            FieldWindow w = new FieldWindow(null, null, getAct(), obj);
            w.show(null, null);
        }


        public static void copy(Object string) {

            ClipboardManager cm = (ClipboardManager) act.getSystemService(act.CLIPBOARD_SERVICE);
            if (cm != null)
                cm.setPrimaryClip(ClipData.newPlainText("test", "" + string));

        }

        public static void print(Object obj) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = obj;
            handler.sendMessage(msg);
        }

        public static Object getTempVar(int position) {
            if (position < MasterUtils.objects.size())
                return MasterUtils.objects.get(position);
            else
                return null;
        }


    }

    public static class io {
        public static String sleep(int second) {
            try {
                Thread.sleep(second);
            } catch (InterruptedException e) {
                return e.toString();
            }

            return null;
        }

        public static void xplog(Object obj) {
            XposedBridge.log(obj + "");
        }

        public static byte[] readbytes(String path) {
            try {
                InputStream stream = new FileInputStream(path);
                byte[] buff = new byte[stream.available()];
                stream.read(buff);
                return buff;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String readstring(String path) {
            String result = null;
            byte[] buff = null;
            if ((buff = readbytes(path)) != null) {
                result = new String(buff);
            }
            return result;
        }

        public static int exists(Object path) {
            File f = null;
            if (path instanceof String) {
                f = new File((String) path);
            } else if (path instanceof File) {
                f = (File) path;
            } else return 0;
            return f.exists() ? 1 : 0;
        }

        public static boolean writefile(String path, Object obj) {
            OutputStream os = null;
            if (obj == null) return false;
            try {
                os = new FileOutputStream(path);

                if (obj instanceof String) {
                    os.write(((String) obj).getBytes());
                } else if (obj instanceof byte[]) {
                    os.write((byte[]) obj);
                } else {
                    os.write(("" + obj).getBytes());
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void show(final WindowManager manager, WindowManager.LayoutParams lpl) {
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.width = MasterUtils.windowSize;
        layoutParams.height = MasterUtils.windowSize;
        layout = new LinearLayout(act);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        actionWindow = new ActionWindow(act, manager, layoutParams, layout);
        LinearLayout buttonLayout = new LinearLayout(act);


        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        execute = new Button(act);
        execute.setText("运行");
        execute.setWidth(screenW);
        execute.setOnClickListener(p1 -> {
            log.setText("");
            if (MasterUtils.newThread) {
                new Thread(()->{
                    luaExecutor.executeLua(getAct(),this.script.toString());
                }).start();
            }
            luaExecutor.executeLua(getAct(),this.script.getText().toString());
        });

//        Button copy = new Button(act);
//        copy.setText("粘贴");
//        copy.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ClipboardManager manager1 = (ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
//                if (manager1.getPrimaryClip() != null)
//                    script.setText(manager1.getPrimaryClip().getItemAt(0).getText());
//            }
//        });

        Button copylog = new Button(act);
        copylog.setText("粘贴");
        copylog.setOnClickListener(view -> {
            ClipboardManager cm = (ClipboardManager) act.getSystemService(act.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("test", log.getText().toString()));
            Toast.makeText(act, "复制Log成功", Toast.LENGTH_SHORT).show();
        });
        buttonLayout.addView(execute);
        // buttonLayout.addView(copy);
        script = new EditText(act);
        script.setHeight(this.screenW / 4);
        script.setTextColor(Color.BLACK);
        log = new TextView(act);

        log.setTextColor(Color.RED);
        script.setMaxHeight(this.screenH / 2);
        layout.addView(actionWindow.getActionBar());
        layout.addView(buttonLayout);
        layout.addView(script);

        ScrollView scrollView = new ScrollView(act);
        scrollView.addView(log);
        layout.addView(scrollView);

        manager.addView(layout, layoutParams);
        log.setOnLongClickListener(v -> {
            {
                ClipboardManager cm = (ClipboardManager) act.getSystemService(act.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("test", log.getText().toString()));
                Toast.makeText(act, "复制Log成功", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        if (code == null) {
            script.setText("require \"import\"\n" +
                    "import \"java.lang.*\"\n" +
                    "import \"java.io.*\"\n" +
                    "import \"dalvik.system.DexClassLoader\"");
        } else {
            script.setText(code);
        }
//
//            if (checkCompiled(dataCode)) {
//                final EditText editText = new EditText(act);
//                editText.setHint("输入密钥");
//                new AlertDialog.Builder(act).setTitle("密钥：").setView(editText).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String k = editText.getText().toString().trim();
//                        if (!k.equals("")) {
//                            byte[] byt = deCompile(dataCode, k);
//                            if (byt != null) {
//                                script.setText(new String(byt));
//                            } else {
//                                Utils.showToast(act, "密钥错误", 0);
//                            }
//                        }
//
//                    }
//                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).show().setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//
//                    }
//                });
//            } else {
//                script.setText(new String(dataCode));
//            }
//        }

    }


    private byte[] deCompile(byte[] data, String key) {
        try {
            byte[] newbyte = new byte[data.length - 4];
            System.arraycopy(data, 4, newbyte, 0, newbyte.length);
            return Utils.Encryption(newbyte, key.getBytes("utf-8"), 2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkCompiled(byte[] data) {
        if (data.length < 4) return false;
        String s = new String(data, 0, 4);
        if (s.toUpperCase().indexOf("LUAJ") != -1) return true;
        return false;
    }



    public void loadFile(String file, String key) {
        luaExecutor.doFile(file, new Object[0]);
    }


    public Object dofile(String s) {
        return luaExecutor.doFile(s, new Object[0]);
    }








    private XC_LoadPackage.LoadPackageParam lpparam;

    private XC_MethodHook.MethodHookParam param;
    private Context act;

    public XC_LoadPackage.LoadPackageParam getLpparam() {
        return lpparam;
    }

    public XC_MethodHook.MethodHookParam getParam() {
        return param;
    }

    //
    private Object object;
    String code;
//    private byte[] dataCode;

    public ScriptWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param, Context act, Object object, String code) {
        super(lpparam, param, act, object);
        this.lpparam = lpparam;
        this.param = param;
        this.object = object;
        this.act = act;
        this.code = code;
//        this.dataCode = dataCode;l
        Activity activity = (Activity) act;
        activity.getWindow().setSoftInputMode(0x10);
        WindowManager windowManager = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenW = displayMetrics.widthPixels;
        screenH = displayMetrics.heightPixels;
        this.luaExecutor = new LuaExecutor(getAct(),this);
        luaDexLoader = new LuaDexLoaders(getAct());

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







