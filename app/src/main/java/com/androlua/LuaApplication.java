package com.androlua;

public class LuaApplication
{
    private static LuaApplication app;
    private LuaApplication(){

    }
    public static LuaApplication getInstance(){
        if(app==null){
            app=new LuaApplication();
        }
        return app;
    }
    public String getLuaExtDir(String v){
        return "";
    }
    public String getLuaExtPath(String v){return "";}

    public String getOdexDir(){
        return "";
    }
}