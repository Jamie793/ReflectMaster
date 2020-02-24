package formatfa.reflectmaster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.reflectmaster.Utils.Utils;

/**
 * Created by Jamie793 on 18-4-28.
 */

public class LuaScriptManager extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Version.isTestMode = true;
        setTitle("Lua脚本");
        setContentView(R.layout.scriptmanager);
        listView = findViewById(R.id.list);
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("flag", 0);
                intent.setClass(LuaScriptManager.this, LuaScriptEditor.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(LuaScriptManager.this, LuaScriptEditor.class);
                if(checkCompiled(items.get(i).getData()))
                intent.putExtra("flag", 2);
                else
                    intent.putExtra("flag", 1);
                intent.putExtra("path", items.get(i).getPath());
                intent.putExtra("code", items.get(i).getData());
                startActivity(intent);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(LuaScriptManager.this).setTitle("提示：").setMessage("是否删除此脚本？").setPositiveButton("取消", null).setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new File(items.get(position).getPath()).delete();
                        items.remove(position);
                        loadscript();
                    }
                }).show();
                return false;
            }
        });

        loadscript();


    }

    public boolean checkCompiled(byte[] data){
        if(data.length<4)return false;
        String s = new String(data, 0, 4);
        if(s.toUpperCase().indexOf("LUAJ")!=-1)return true;
        return false;
    }

    public byte[] readCode(String path) {
        return Utils.readFile(path);
    }




    //    private List<ScriptItem> items;
    private List<LuaScriptItem> items = new ArrayList<>();
    private List<String> names = new ArrayList<>();

    private void loadscript() {
        names.clear();
        File file = new File(Utils.BASEPATH + "/script");
        if (file.exists()) {
        File[] files = file.listFiles();
            for (File fi : files) {
                String name = fi.getName();
                if (fi.isFile() && (name.endsWith(".lua")||name.endsWith(".luaj"))) {
                    if(name.endsWith(".lua"))
                    names.add(name.replace(".lua", ""));
                    else
                        names.add("*"+name.replace(".luaj", ""));
                    items.add(new LuaScriptItem(fi.toString(), readCode(fi.toString())));
                }
            }
        } else {
            file.mkdirs();
            String code = "require \"import\"\n" +
                    "import \"java.io.*\"\n" +
                    "import \"dalvik.system.DexClassLoader\"\n" +
                    "a=Class.forName(\"com.android.dex.Dex\")\n" +
                    "b=a.getDeclaredMethod(\"getBytes\",Class[0])\n" +
                    "a=Class.getClass()\n" +
                    "c=a.getDeclaredMethod(\"getDex\",Class[0])\n" +
                    "d=b.invoke(c.invoke(activity.getClass(),Object[0]),Object[0])\n" +
                    "e=FileOutputStream(\"/sdcard/1xmi/1.dex\")\n" +
                    "e.write(d)\n" +
                    "e.flush()\n" +
                    "e.close()\n" +
                    "print(\"写出成功\")\n";
            names.add("DumpDex");
            items.add(new LuaScriptItem(file.toString() + "/DexDump.lua", code.getBytes()));
            save();
        }

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names));


    }




//    public void edit(final int p, final String name, final String code, final String pkg) {
//        //    CodeDialog dialog = new CodeDialog(this);
//
//        AlertDialog.Builder ab = new AlertDialog.Builder(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.scriptedit, null);
//        final EditText editname = view.findViewById(R.id.name);
//        final EditText editcode = view.findViewById(R.id.code);
//
//        editname.setHint("脚本名字");
//        editcode.setHint("代码");
//        if (name != null) editname.setText(name);
//        if (code != null) editcode.setText(code);
//        ab.setView(view).setPositiveButton("保存", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                try {
//                    set(p, editname.getText().toString(), editcode.getText().toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).setNegativeButton("取消", null)
////                .setNeutralButton("删除", new DialogInterface.OnClickListener() {
////
////            @Override
////            public void onClick(DialogInterface dialogInterface, int i) {
////                items.remove(p);
////                try {
////                    save();
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
////            }
////        })
//
//                .setNeutralButton("编译", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        View view = LayoutInflater.from(LuaScriptManager.this).inflate(R.layout.scriptedit, null);
//                        final EditText compilepath = view.findViewById(R.id.name);
//                        final EditText compilepass = view.findViewById(R.id.code);
//
//                        compilepath.setHint("路径");
//                        compilepass.setHint("密钥");
//                        new AlertDialog.Builder(LuaScriptManager.this).setTitle("编译").setView(view).setPositiveButton("编译", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                File file = new File(compilepath.getText().toString());
//                                if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
//                                Utils.writeFile(file.toString(), compile(code, compilepass.getText().toString()));
//                                Utils.showToast(LuaScriptManager.this, "编译成功 ：" + compilepath.getText().toString(), Toast.LENGTH_LONG);
//                            }
//                        }).show();
//                    }
//                }).show().setCancelable(false);
//
//
//    }


    private void save() {
        if (!items.isEmpty()) {
            for (LuaScriptItem item : items) {
                Utils.writeFile(item.getPath(), item.getData());
            }
        }
        loadscript();
    }


}
