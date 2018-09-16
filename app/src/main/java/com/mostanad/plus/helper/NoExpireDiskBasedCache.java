package com.mostanad.plus.helper;

import java.io.File;

import ir.mono.monolyticsdk.Utils.volley.toolbox.DiskBasedCache;

/**
 * Created by Parsa on 16/12/2017.
 */

public  class NoExpireDiskBasedCache extends DiskBasedCache
{
    public NoExpireDiskBasedCache(File rootDirectory, int maxCacheSizeInBytes)
    {
        super(rootDirectory, maxCacheSizeInBytes);
    }
    public NoExpireDiskBasedCache(File rootDirectory)
    {
        super(rootDirectory);
    }
    @Override
    public synchronized void put(String key, Entry entry)
    {
        if (entry != null)
        {
            entry.etag = null;
            entry.softTtl = 0;
            entry.ttl = 0;
        }

        super.put(key, entry);
    }
}