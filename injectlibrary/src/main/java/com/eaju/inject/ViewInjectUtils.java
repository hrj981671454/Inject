package com.eaju.inject;

import android.content.Context;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : Android
 * Author     : 关羽
 * Date       : 2018-07-17 14:40
 */
public class ViewInjectUtils {

    public static void inject(Context context) {
        injectContentView(context);
        injectView(context);
        injectOnClick(context);
        injectOnLongClick(context);
    }


    /**
     * 通过反射反射到当前引用Activity或者其他UI操作的Context对象找到 setContentView方法
     * 然后通过注解传入要加载的布局ID
     * @param context
     */
    private static void injectContentView(Context context) {
        //获取当前context下的class
        Class<? extends Context> clazz = context.getClass();
        //找到当前class中的注解对象
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        //判断是否有值
        if (null != contentView) {
            //找到注解下传入的布局对象
            int value = contentView.value();
            try {
                //找到当前context下的setContentView方法
                Method method = clazz.getMethod("setContentView", int.class);
                //设置此方法可访问，无论权限是private还是public
                method.setAccessible(true);
                //反射调用setContentView，并传入当前context对象和布局对象
                method.invoke(context, value);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过反射找到所有在当前context对象下使用注解的对象
     * @param context
     */
    private static void injectView(Context context) {
        //找到当前context下Class
        Class<?> clazz = context.getClass();

        //找到当前Class下所有的private或者public变量（全局变量）
        Field[] declaredFields = clazz.getDeclaredFields();

        //变量所有变量
        for (Field field : declaredFields) {
            //找到使用注解的变量
            ViewInject annotation = field.getAnnotation(ViewInject.class);

            //判空，如为空，则代表未使用注解去findViewById，所以跳出循环
            if (null == annotation) {
                continue;
            }

            //如不为空，找到设置的布局组件ID
            int value = annotation.value();

            try {
                if (value != -1) {
                    //找到当前Class中的findViewById方法
                    Method findViewById = clazz.getMethod("findViewById", int.class);
                    //设置要findViewById的值，返回View对象
                    View view = (View) findViewById.invoke(context, value);
                    //设置private权限下可执行
                    field.setAccessible(true);
                    //设置值
                    field.set(context, view);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过反射设置OnClick事件
     * @param context
     */
    private static void injectOnClick(Context context) {
        //找到context下的Class
        Class<? extends Context> clazz = context.getClass();
        //找到Class下所有的方法
        Method[] methods = clazz.getMethods();
        //便利所有方法
        for (Method method : methods) {

            //找到所有方法中使用注解
            Annotation[] annotations = method.getAnnotations();

            //遍历所有注解
            for (Annotation annotation : annotations) {

                //找到注解类型所属的Class
                Class<? extends Annotation> aClass = annotation.annotationType();

                //找到使用EventBus的对象
                OnClickEventBus onClickEventBus = aClass.getAnnotation(OnClickEventBus.class);

                //判空，如为空，跳出循环
                if (null == onClickEventBus) {
                    continue;
                }

                //将EventBus所设置的监听事件的方法
                String listenerSetter = onClickEventBus.listenerSetter();

                //将EventBus所设置的监听事件类型的Class，设置的是View.OnCLickListener,此方法也只支持此点击事件
                Class<?> listenerTypeClazz = onClickEventBus.listenerType();

                //点击事件的方法 设置的是OnClick
                String callBackMethod = onClickEventBus.callBackMethod();

                //创建集合
                HashMap<String, Method> methodHashMap = new HashMap<>();

                //将OnClick的callBackMethod放入集合
                methodHashMap.put(callBackMethod, method);

                try {
                    //找到方法下所有的注解值
                    Method value = aClass.getDeclaredMethod("value");
                    //因为可能会给多个对象设置监听，所以是个数组
                    int[] viewIds = (int[]) value.invoke(annotation);

                    //便利所有要设置的监听
                    for (int ids : viewIds) {

                        //调用findViewById方法
                        Method findViewById = clazz.getMethod("findViewById", int.class);
                        findViewById.setAccessible(true);
                        View view = (View) findViewById.invoke(context, ids);
                        if (null == view) {
                            continue;
                        }
                        //找到要设置OnClick的View的设置监听的方法和Class对象
                        Method setListener = view.getClass().getMethod(listenerSetter, listenerTypeClazz);

                        //通过动态代理，去代理View.OnClickListener,当前Activity去引用
                        ListenerInvocationHandler handler = new ListenerInvocationHandler(context, methodHashMap);

                        Object proxy = Proxy.newProxyInstance(listenerTypeClazz.getClassLoader(), new Class[]{listenerTypeClazz}, handler);

                        setListener.invoke(view, proxy);

                    }

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过反射设置OnClick事件
     * @param context
     */
    private static void injectOnLongClick(Context context) {
        //找到context下的Class
        Class<? extends Context> clazz = context.getClass();
        //找到Class下所有的方法
        Method[] methods = clazz.getMethods();
        //便利所有方法
        for (Method method : methods) {

            //找到所有方法中使用注解
            Annotation[] annotations = method.getAnnotations();

            //遍历所有注解
            for (Annotation annotation : annotations) {

                //找到注解类型所属的Class
                Class<? extends Annotation> aClass = annotation.annotationType();

                //找到使用EventBus的对象
                OnLongClickEventBus onClickEventBus = aClass.getAnnotation(OnLongClickEventBus.class);

                //判空，如为空，跳出循环
                if (null == onClickEventBus) {
                    continue;
                }

                //将EventBus所设置的监听事件的方法
                String listenerSetter = onClickEventBus.listenerSetter();

                //将EventBus所设置的监听事件类型的Class，设置的是View.OnLongCLickListener,此方法也只支持此点击事件
                Class<?> listenerTypeClazz = onClickEventBus.listenerType();

                //点击事件的方法 设置的是OnClick
                String callBackMethod = onClickEventBus.callBackMethod();

                //创建集合
                HashMap<String, Method> methodHashMap = new HashMap<>();

                //将OnClick的callBackMethod放入集合
                methodHashMap.put(callBackMethod, method);

                try {
                    //找到方法下所有的注解值
                    Method value = aClass.getDeclaredMethod("value");
                    //因为可能会给多个对象设置监听，所以是个数组
                    int[] viewIds = (int[]) value.invoke(annotation);

                    //便利所有要设置的监听
                    for (int ids : viewIds) {

                        //调用findViewById方法
                        Method findViewById = clazz.getMethod("findViewById", int.class);
                        findViewById.setAccessible(true);
                        View view = (View) findViewById.invoke(context, ids);
                        if (null == view) {
                            continue;
                        }
                        //找到要设置OnClick的View的设置监听的方法和Class对象
                        Method setListener = view.getClass().getMethod(listenerSetter, listenerTypeClazz);

                        setListener.setAccessible(true);

                        //通过动态代理，去代理View.OnClickListener,当前Activity去引用
                        ListenerInvocationHandler handler = new ListenerInvocationHandler(context, methodHashMap);

                        Object proxy = Proxy.newProxyInstance(listenerTypeClazz.getClassLoader(), new Class[]{listenerTypeClazz}, handler);

                        setListener.invoke(view, proxy);
                    }

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
