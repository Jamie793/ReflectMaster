package com.androlua;

import android.content.Context;

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
