#include <android/log.h>
#include <assert.h>
#include <jni.h>
#include <malloc.h>
#include <iostream>
#include <stdio.h>

using namespace std;

#define TAG "leopard_JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
#define NELM(methds) ((int)(sizeof(methds)/sizeof((methds)[0])))



void get_data_array_from_list(JNIEnv *env, jobject object_list, double **arr_point) {
    jclass cls_list = env->GetObjectClass(object_list);
    jmethodID list_get = env->GetMethodID(cls_list, "get", "(I)Ljava/lang/Object;");
    jmethodID list_size = env->GetMethodID(cls_list, "size", "()I");
    jint len = env->CallIntMethod(object_list, list_size);
    for (int i = 0; i < len; i++) {
        jobject depth_data = env->CallObjectMethod(object_list, list_get, i);
        jdouble *arr = env->GetDoubleArrayElements((jdoubleArray) depth_data, JNI_FALSE);
        LOGI("p和c分别为 %.2f ,%.2f", arr[0], arr[1]);
        *(*(arr_point + i)) = arr[0];
        *(*(arr_point + i) + 1) = arr[1];
    }
}

extern "C"
JNIEXPORT jint JNICALL
calculate_tendency(JNIEnv *env, jclass type,
                   jobject depth) {
    LOGI("jni calculate_tendency");
    jclass depth_clazz = env->GetObjectClass(depth);
    if (depth_clazz == NULL) {
        return 0;
    }

    jmethodID depth_getask_id = env->GetMethodID(depth_clazz, "getAsks", "()Ljava/util/List;");
    jobject object_ask_arr = env->CallObjectMethod(depth, depth_getask_id);
    if (object_ask_arr == NULL) {
        return 0;
    }
    jclass cls_list = env->GetObjectClass(object_ask_arr);
    jmethodID list_get = env->GetMethodID(cls_list, "get", "(I)Ljava/lang/Object;");
    jmethodID list_size = env->GetMethodID(cls_list, "size", "()I");
    jint len = env->CallIntMethod(object_ask_arr, list_size);
    double c_depth_data[len][2];
    for (int i = 0; i < len; i++) {
        jobject depth_data = env->CallObjectMethod(object_ask_arr, list_get, i);
        jdouble *arr = env->GetDoubleArrayElements((jdoubleArray) depth_data, JNI_FALSE);
        LOGI("p和c分别为 %.2f ,%.2f", arr[0], arr[1]);
        c_depth_data[i][0] = arr[0];
        c_depth_data[i][1] = arr[1];
    }

    return 1;
}


/*需要注册的函数列表，放在JNINativeMethod 类型的数组中，
以后如果需要增加函数，只需在这里添加就行了
参数：
1.java代码中用native关键字声明的函数名字符串
2.签名（传进来参数类型和返回值类型的说明）
3.C/C++中对应函数的函数名（地址）
*/
static JNINativeMethod getMethods[] = {

        {"getDepthTendency", "(Lcom/leapord/supercoin/entity/Depth;)I", (void *) calculate_tendency},

};

//此函数通过调用JNI中 RegisterNatives 方法来注册我们的函数
static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *getMethods,
                                 int methodsNum) {
    jclass clazz;
    //找到声明native方法的类
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    //注册函数 参数：java类 所要注册的函数数组 注册函数的个数
    if (env->RegisterNatives(clazz, getMethods, methodsNum) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static int registerNatives(JNIEnv *env) {
    //指定类的路径，通过FindClass 方法来找到对应的类
    const char *className = "com/leapord/supercoin/util/KlineUtil";
    return registerNativeMethods(env, className, getMethods,
                                 sizeof(getMethods) / sizeof(getMethods[0]));
}
//回调函数 在这里面注册函数
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    //判断虚拟机状态是否有问题
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);
    //开始注册函数 registerNatives -》registerNativeMethods -》env->RegisterNatives
    if (!registerNatives(env)) {
        return -1;
    }
    //返回jni 的版本
    return JNI_VERSION_1_6;
}