package com.lwk.familycontact.utils.event;

import com.lwk.familycontact.base.BuildParams;

import org.greenrobot.eventbus.EventBus;

/**
 * Function:eventbus帮助类
 * Created by LWK
 * 2016/3/22
 */
public class EventBusHelper
{
    private EventBusHelper()
    {
        initOptions();
    }

    private static final class EventBusHelperHolder
    {
        private static EventBusHelper instance = new EventBusHelper();
    }

    public static EventBusHelper getInstance()
    {
        return EventBusHelperHolder.instance;
    }

    private EventBus mEventBus;

    private void initOptions()
    {
        mEventBus = EventBus.builder()
                //当调用事件处理函数异常时是否打印异常信息
                .logSubscriberExceptions(BuildParams.IS_DEBUG)
                //当没有订阅者订阅该事件时是否打印日志
                .logNoSubscriberMessages(BuildParams.IS_DEBUG)
                //当没有事件处理函数时是否发送事件
                .sendNoSubscriberEvent(false)
                //是否要抛出异常，建议debug开启
                .throwSubscriberException(BuildParams.IS_DEBUG)
                .build();
    }

    /**
     * 注册订阅者
     */
    public void regist(Object subscriber)
    {
        mEventBus.register(subscriber);
    }

    /**
     * 解绑订阅者
     */
    public void unregist(Object subscriber)
    {
        mEventBus.unregister(subscriber);
    }

    /**
     * 发布消息
     */
    public void post(Object o)
    {
        mEventBus.post(o);
    }

    /**
     * 发布粘性消息
     * 【注】订阅者接收粘性消息的注解里需要指定模式
     * 示例：@Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
     */
    public void postSticky(Object o)
    {
        mEventBus.postSticky(o);
    }

    /**
     * 移除某个粘性消息
     */
    public void removeStickyEvent(Object o)
    {
        mEventBus.removeStickyEvent(o);
    }

    /**
     * 根据类型移除某种类型所有粘性消息
     */
    public void removeStickyEventByType(Class clz)
    {
        mEventBus.removeStickyEvent(clz);
    }

    /**
     * 移除所有粘性消息
     */
    public void removeAllStickyEvent()
    {
        mEventBus.removeAllStickyEvents();
    }
}
