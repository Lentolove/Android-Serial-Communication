package com.android.library;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池工具
 */
public class ExecutorUtils {

    public final static ExecutorService executors = Executors.newCachedThreadPool();
}
