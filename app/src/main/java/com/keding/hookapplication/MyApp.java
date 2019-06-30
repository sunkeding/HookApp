package com.keding.hookapplication;

import android.app.Application;
import android.app.Instrumentation;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author: skd
 * @date 2019-06-30
 * @Desc MyApp
 */
public class MyApp extends Application {

    public static final String TAG = "MyApplication";
    public static final String ACTIVIT_THREAD = "android.app.ActivityThread";
    public static final String CURRENT_ACTIVITY_THREAD = "currentActivityThread";
    public static final String INSTRUMENTATION = "mInstrumentation";


    @Override
    public void onCreate() {
        super.onCreate();
        try {
            attachContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void attachContext() throws Exception{

        //获取当前的ActivityThread对象
        Class<?> activityThreadClass = Class.forName(ACTIVIT_THREAD);
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod(CURRENT_ACTIVITY_THREAD);
        currentActivityThreadMethod.setAccessible(true);
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);
        //拿到在ActivityThread类里面的原始mInstrumentation对象
        Field mInstrumentationField = activityThreadClass.getDeclaredField(INSTRUMENTATION);
        mInstrumentationField.setAccessible(true);
        Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
        //构建我们的代理对象
        Instrumentation evilInstrumentation = new InstrumentationProxy(mInstrumentation);

        //通过反射，换掉字段，注意，这里是反射的代码，不是Instrumentation里面的方法
        mInstrumentationField.set(currentActivityThread, evilInstrumentation);

        //做个标记，方便后面查看
        Log.i("skd", "has go in MyApplication attachContext method");
    }


}
