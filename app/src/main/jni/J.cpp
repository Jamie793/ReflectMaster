//
// Created by Jamiexu on 2020/8/27.
//

# include "J.h"

extern "C" {

const char *PACKAGENAME = "com.jamiexu.app.reflectmaster";
const char *APPLIATION_NAME = "com.jamiexu.app.reflectmaster.MyApplication";
bool isOk;


jboolean getVer(JNIEnv *env, jobject context) {
    jclass contextClass = env->GetObjectClass(context);
    jmethodID methodId = env->GetMethodID(contextClass, "getPackageName", "()Ljava/lang/String;");
    jmethodID equals = env->GetMethodID(contextClass, "equals", "(Ljava/lang/Object;)Z");
    jstring packName = static_cast<jstring>(env->CallObjectMethod(context, methodId));
    jstring packageNames = env->NewStringUTF(PACKAGENAME);
    env->ReleaseStringChars(packageNames, nullptr);
    if (env->CallBooleanMethod(packName, equals, packageNames)) {
//
        jmethodID applicationInfoMethodID = env->GetMethodID(contextClass, "getApplicationInfo",
                                                             "()Landroid/content/pm/ApplicationInfo;");
        jobject applicatinInfo = env->CallObjectMethod(context, applicationInfoMethodID);

        jclass jclass1 = env->GetObjectClass(applicatinInfo);
        jfieldID jfieldId = env->GetFieldID(jclass1, "className", "Ljava/lang/String;");
//
        jstring jstring1 = static_cast<jstring>(env->GetObjectField(applicatinInfo, jfieldId));
        jstring jstring2 = env->NewStringUTF(APPLIATION_NAME);

        return env->CallBooleanMethod(jstring1, equals, jstring2);
    } else return false;
}

JNIEXPORT void JNICALL Java_com_jamiexu_app_J_init
        (JNIEnv *env, jclass jclas, jobject context) {
    if (getVer(env, context)) {
        isOk = true;
        jclass clas = env->GetObjectClass(context);
        jmethodID methodId = env->GetMethodID(clas, "c", "()V");
        env->CallVoidMethod(context, methodId);

        methodId = env->GetMethodID(clas, "a", "()V");
        env->CallVoidMethod(context, methodId);

        methodId = env->GetMethodID(clas, "b", "()V");
        env->CallVoidMethod(context, methodId);
    }
}


JNIEXPORT void JNICALL Java_com_jamiexu_app_J_cf(JNIEnv *env, jclass clazz, jstring f, jstring t) {
    // TODO: implement cf()
    if (!isOk)
        return;
    jclass clas = env->FindClass("com/jamiexu/app/reflectmaster/MainActivity");
    jmethodID methodid = env->GetStaticMethodID(clas, "cf",
                                                "(Ljava/lang/String;Ljava/lang/String;)V");
    env->CallStaticVoidMethod(clas, methodid, f, t);
}

JNIEXPORT void JNICALL
Java_com_jamiexu_app_J_i(JNIEnv *env, jclass clazz, jobject context) {
    // TODO: implement i()
    if (!getVer(env, context))
        return;
    jclass clas = env->FindClass("com/jamiexu/app/reflectmaster/MainActivity");
    jmethodID method = env->GetStaticMethodID(clas, "d", "()V");
    env->CallStaticVoidMethod(clas, method);
}

}