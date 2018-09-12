package com.eaju.inject;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : Android
 * Author     : 关羽
 * Date       : 2018-07-17 15:44
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@OnLongClickEventBus(
        listenerSetter = "setOnLongClickListener",
        listenerType = View.OnLongClickListener.class,
        callBackMethod = "onLongClick")
public @interface OnLongClick {
    int[] value();
}
