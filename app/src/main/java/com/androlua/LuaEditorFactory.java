package com.androlua;

import android.content.Context;
import android.support.v4.app.Utils.FileUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import formatfa.reflectmaster.MainActivity;

public class LuaEditorFactory {
    private static LuaEditor luaEditor;

    public static LuaEditor getInstance(Context context) {
        if (luaEditor == null) {
            luaEditor = new LuaEditor(context);
//            new Thread(LuaEditorFactory::initHightLight).start();
        }
        return luaEditor;
    }

//    private static void initHightLight() {
//        String classes = FileUtils.getString(MainActivity.BASE_PATH + "lua/android.lua");
//        if(classes == null)
//            return;
//        Pattern pattern = Pattern.compile("^\"(.*?)\"$", Pattern.MULTILINE);
//        Matcher matcher = pattern.matcher(classes);
//        while (matcher.find()) {
//            luaEditor.addNames(new String[]{matcher.group(1)});
//        }
//    }

}
