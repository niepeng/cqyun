package com.baibutao.app.waibao.yun.android.as;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.baibutao.app.waibao.yun.android.biz.LoadImgDO;
import com.baibutao.app.waibao.yun.android.biz.consumer.LoadImageConsumer;
import com.baibutao.app.waibao.yun.android.util.MyQueue;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:01:45
 */
public class AsynchronizedInvoke {
private ExecutorService executorService;
	
	private MyQueue<LoadImgDO> loadImageQueue;
	
	public void init() {
		executorService = Executors.newCachedThreadPool();
		loadImageQueue = new MyQueue<LoadImgDO>();
		startloadImage(2);
	}
	
	private void startloadImage(int number) {
		for (int i = 0; i < number; i++) {
			Runnable runnbale = new LoadImageConsumer(loadImageQueue);
			executorService.submit(runnbale);
		}
	}
	
	public <V> Future<V> invoke(Callable<V> callable) {
		return executorService.submit(callable);
	}
	
	public void call(Runnable runnable) {
		executorService.submit(runnable);
	}
	
	public void addLoadImage(LoadImgDO loadImgDO) {
		 loadImageQueue.add(loadImgDO);
	}
	

	public void cleanup() {
		if (executorService != null) {
			executorService.shutdown();
		}
	}
	
	
}

