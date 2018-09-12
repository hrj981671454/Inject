package com.eaju.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : Android
 * Author     : 关羽
 * Date       : 2018-07-17 15:49
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnClickEventBus {

    /**
     * 监听事件的方法
     * @return
     */
    String listenerSetter();

    /**
     * 事件监听类型
     * @return
     */
    Class<?> listenerType();


    /**
     * 事件被触发后执行
     */
    String callBackMethod();
}
