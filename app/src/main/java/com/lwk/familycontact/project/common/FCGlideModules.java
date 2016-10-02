package com.lwk.familycontact.project.common;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;
import com.lib.base.utils.SdUtils;

/**
 * GlideModule:指定Glide的缓存参数
 */
public class FCGlideModules implements GlideModule
{
    //glide系统缓存基础倍率
    private static final float MEMORY_CACHE_COUNT = 1.2f;
    //磁盘缓存容量最大值
    private static final int MAX_DISK_CACHE_SIZE = 314572800;//300M

    @Override
    public void applyOptions(Context context, GlideBuilder builder)
    {
        //修改内存容量和位图缓存池大小
        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        int customMemoryCacheSize = (int) (MEMORY_CACHE_COUNT * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (MEMORY_CACHE_COUNT * defaultBitmapPoolSize);
        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));
        //设置磁盘缓存
        String diskCachePath = FCCache.getInstance().getImageCachePath();
        int cacheSize = 0;
        long availableSize = SdUtils.getAvailableExternalMemorySize();
        if (availableSize < MAX_DISK_CACHE_SIZE)
            cacheSize = (int) availableSize;
        else
            cacheSize = MAX_DISK_CACHE_SIZE;
        builder.setDiskCache(new DiskLruCacheFactory(diskCachePath, cacheSize));
    }

    @Override
    public void registerComponents(Context context, Glide glide)
    {

    }
}
