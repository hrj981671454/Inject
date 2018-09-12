package com.eaju.inject;

import android.content.Context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : Android
 * Author     : 关羽
 * Date       : 2018-07-17 17:15
 */
public class ListenerInvocationHandler implements InvocationHandler {
    private Context                 context;
    private HashMap<String, Method> hashMap;

    public ListenerInvocationHandler(Context context, HashMap<String, Method> hashMap) {
        this.context = context;
        this.hashMap = hashMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //找到HashMap中的方法，并执行
        String name = method.getName();
        Method mtd = hashMap.get(name);
        if (null == mtd) {
            return method.invoke(proxy, args);
        } else {
            return mtd.invoke(context, args);
        }
    }
}
