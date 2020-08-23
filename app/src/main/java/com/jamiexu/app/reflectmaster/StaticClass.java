package com.jamiexu.app.reflectmaster;

import com.androlua.LuaEditor;

public class StaticClass {
    public LuaEditor getLuaEditor() {
        return luaEditor;
    }

    public void setLuaEditor(LuaEditor luaEditor) {
        this.luaEditor = luaEditor;
    }

    private LuaEditor luaEditor;
}
