package com.baibutao.app.waibao.yun.android.tasks.taskgroup;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.baibutao.app.waibao.yun.android.util.CollectionUtil;

/**
 * @author lsb
 *
 * @date 2012-5-30 上午10:27:09
 */
public class TaskGroup implements Runnable {
	
	private Collection<Future<?>> mustFutures = CollectionUtil.newArrayList();
	
	private Collection<Future<?>> mayFutures = CollectionUtil.newArrayList();
	
	private Collection<Runnable> mustTasks = CollectionUtil.newArrayList();
	
	private Collection<Runnable> mayTasks = CollectionUtil.newArrayList();
	
	private ExecutorService executorService = Executors.newCachedThreadPool();

	
	public void addMust(Runnable mustTask) {
		mustTasks.add(mustTask);
	}
	
	public void addMay(Runnable mayTask) {
		mayTasks.add(mayTask);
	}
	
	@Override
	public void run() {
		for(Runnable mustTask : mustTasks) {
			mustFutures.add(executorService.submit(mustTask));
		}
		
		for(Runnable mayTask : mayTasks) {
			mayFutures.add(executorService.submit(mayTask));
		}
	}
	
	public void shutdown() {
		// 关闭线程池
		executorService.shutdown();
	}
	

}

