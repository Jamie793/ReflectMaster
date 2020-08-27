//
// Created by Jamiexu on 2020/8/27.
//

# include "J.h"
const char *PACKAGENAME = "com.jamiexu.app.reflectmaster";

extern "C" JNIEXPORT void JNICALL Java_com_jamiexu_app_J_init
        (JNIEnv *env, jclass jclas, jobject context){
    jclass clas = env->GetObjectClass(context);
    jmethodID methodId = env->GetMethodID(clas,"getPackageName", "()Ljava/lang/String;");
    jmethodID equals = env->GetMethodID(jclas,"equals", "(Ljava/lang/Object;)Z");
    jstring packName = static_cast<jstring>(env->CallObjectMethod(context, methodId));
    jstring packageNames = env->NewStringUTF(PACKAGENAME);
    if(env->CallBooleanMethod(packName,equals,packageNames)){
        methodId = env->GetMethodID(clas,"c","()V");
        env->CallVoidMethod(context,methodId);

        methodId = env->GetMethodID(clas,"a","()V");
        env->CallVoidMethod(context,methodId);

        methodId = env->GetMethodID(clas,"b", "()V");
        env->CallVoidMethod(context,methodId);
    }
}