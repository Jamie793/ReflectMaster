package formatfa.reflectmaster;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.reflectmaster.Utils.Utils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.androlua.LuaEditor;
import com.luajava.JavaFunction;
import com.luajava.LuaException;
import com.luajava.LuaState;
import com.luajava.LuaStateFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dalvik.system.DexClassLoader;
import formatfa.reflectmaster.Utils.FileUtils;

public class LuaScriptEditor extends AppCompatActivity {
    private LuaEditor luaEditor;
    private LuaState L;
    final StringBuilder output = new StringBuilder();
    private String code;
    private String path;
    private LuaDexLoaders luaDexLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("脚本编辑器");

        setContentView(R.layout.lua_script_editor);


        Intent intent = getIntent();
        int flag = 0;
        flag = intent.getIntExtra("flag", 0);
        this.path = intent.getStringExtra("path");
        this.code = intent.getStringExtra("code");

        switch (flag) {
            case 0:
                EditText editText = new EditText(this);
                new AlertDialog.Builder(this).setTitle("新建文件：").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
            case 1:

                break;
        }

        LinearLayout linearLayout = findViewById(R.id.lua_script_editor_editor);

        if (RFApplication.staticClass.getLuaEditor() == null) {
            RFApplication.staticClass.setLuaEditor(new LuaEditor(this));
            this.luaEditor = RFApplication.staticClass.getLuaEditor();
            this.luaDexLoader = new LuaDexLoaders(this);

            new Thread(() -> {
                initHightLight();
                loadLuaScriptAndAddJavaMethod();
            }).start();
            linearLayout.addView(this.luaEditor);

            this.luaEditor.setText("require \"import\"\n" +
                    "import \"java.lang.*\"\n" +
                    "import \"java.io.*\"\n" +
                    "import \"dalvik.system.DexClassLoader\"");
            if (RFApplication.staticClass.getLuaEditor() != this.luaEditor)
                RFApplication.staticClass.setLuaEditor(this.luaEditor);
        } else {
            this.luaEditor = RFApplication.staticClass.getLuaEditor();
        }
    }


    public static String[] addArray(String[] strings, String str) {
        String[] newstrings = new String[strings.length + 1];
        System.arraycopy(strings, 0, newstrings, 0, strings.length);
        newstrings[newstrings.length - 1] = str;
        return newstrings;
    }


    public String loadClasses() {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream open = null;
        BufferedReader bufferedReader = null;
        try {
            open = getAssets().open("javaapi/android.lua");
            bufferedReader = new BufferedReader(new InputStreamReader(open));
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (open != null)
                    open.close();
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return stringBuilder.toString().replace("\"", "").replace(",", "");
    }

    public void initHightLight() {
        if (this.luaEditor == null) return;
        String[] keyWord = new String[0];
        Pattern pattern = Pattern.compile("\\w+$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(loadClasses());
        while (matcher.find()) {
            keyWord = addArray(keyWord, matcher.group());
        }
        this.luaEditor.addNames(keyWord);
    }

    private String exeLua(String src) throws LuaException {
        L.setTop(0);
        int ok = L.LloadString(src);
        if (ok == 0) {
            L.getGlobal("debug");
            L.getField(-1, "traceback");
            L.remove(-2);
            L.insert(-2);
            ok = L.pcall(0, 0, -2);
            if (ok == 0) {
                String res = output.toString();
                return res;
            }
        }
        throw new LuaException(errorReason(ok) + ": " + L.toString(-1));
        //return null;

    }

    private String errorReason(int error) {
        switch (error) {
            case 4:
                return "Out of memory";
            case 3:
                return "Syntax error";
            case 2:
                return "Runtime error";
            case 1:
                return "Yield error";
        }
        return "Unknown error " + error;
    }

    public void loadLuaScriptAndAddJavaMethod() {
        L = LuaStateFactory.newLuaState();
        L.openLibs();
        L.pushJavaObject(this);
        L.setGlobal("this");
        L.pushJavaObject(this);
        L.setGlobal("activity");
        L.getGlobal("package");
        L.pushString(Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/lua/?.lua");
        L.setField(-2, "path");
//        L.pushString("456");
//        L.setField(-2, "cpath");
        L.pop(1);
        JavaFunction print = new JavaFunction(L) {
            @Override
            public int execute() {
                for (int i = 2; i <= L.getTop(); i++) {
                    int type = L.type(i);
                    String stype = L.typeName(type);
                    String val = null;
                    if (stype.equals("userdata")) {
                        Object obj = L.toJavaObject(i);
                        if (obj != null)
                            val = obj.toString();
                    } else if (stype.equals("boolean")) {
                        val = L.toBoolean(i) ? "true" : "false";
                    } else {
                        val = L.toString(i);
                    }
                    if (val == null)
                        val = stype;
                    output.insert(0, val);
                    output.insert(0, "\t");
                }

                output.insert(0, "\n");
                return 0;
            }
        };
        print.register("print");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.luaeditor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void isSave() {
        if (!this.code.equals(this.luaEditor.getText().toString())) {
            Utils.showDialog(this, "提示：", "是否保存？", "保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FileUtils.putString(path, code);
                            finish();
                        }
                    }, "取消", null,
                    "不保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }, null, true, true);

        } else finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.run:
                try {
                    exeLua(this.luaEditor.getText().toString());
                } catch (LuaException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.undo:
                this.luaEditor.undo();
                break;
            case R.id.redo:
                this.luaEditor.redo();
                break;
            case R.id.compile:

                break;
            case R.id.save:

                break;

            case R.id.close:
                isSave();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            isSave();
        }
        return false;
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
