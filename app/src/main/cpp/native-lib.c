#include <android/log.h>
#include <assert.h>
#include <jni.h>
#include <malloc.h>

#define TAG "leopard_JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)





static const JNINativeMethod gMethods[] = {
        {
//                "diff", "(Ljava/lang/String;Ljava/lang/String;I)V", (void *) native_diff
                //方法名, 方法参数 方法指针
        }
};

//static int registerNatives(JNIEnv *env) {
//    LOGI("register begin");
//    jclass clazz;
//    clazz = (*env)->FindClass(env, "com/leapord/dn_ls9/FileUtils");
//    if (clazz == NULL) {
//        LOGI("clazz is null");
//        return JNI_FALSE;
//    }
//    if ((*env)->RegisterNatives(env, clazz, gMethods, NELEM(gMethods)) < 0) {
//        LOGI("register fail");
//        return JNI_FALSE;
//    }
//    LOGI("register success");
//    return JNI_TRUE;
//}
//JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
//    LOGI("jni onload begin");
//    JNIEnv *env = NULL;
//    jint result = -1;
//    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
//        LOGI("ERROR: get env failed");
//        return -1;
//    }
//    assert(env != NULL);
//    registerNatives(env);
//    LOGI("jni onload success");
//    return JNI_VERSION_1_4;
//}
