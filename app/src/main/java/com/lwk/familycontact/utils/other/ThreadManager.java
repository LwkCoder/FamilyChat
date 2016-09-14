package com.lwk.familycontact.utils.other;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by LWK
 * TODO 线程控制器
 * 2016/9/14
 */
public class ThreadManager
{
    private ThreadManager()
    {
    }

    private static final class ThreadManagerHolder
    {
        private static ThreadManager instance = new ThreadManager();
    }

    public static ThreadManager getInstance()
    {
        return ThreadManagerHolder.instance;
    }

    private ExecutorService mCachedThreadService = Executors.newCachedThreadPool();

    public void addNewRunnable(Runnable runnable)
    {
        mCachedThreadService.execute(runnable);
    }
}
