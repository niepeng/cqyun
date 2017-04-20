package com.baibutao.app.waibao.yun.android.biz.consumer;

import android.graphics.Bitmap;

import com.baibutao.app.waibao.yun.android.biz.LoadImgDO;
import com.baibutao.app.waibao.yun.android.common.ImageUtil;
import com.baibutao.app.waibao.yun.android.util.MyQueue;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:04:48
 */
public class LoadImageConsumer implements Runnable {

	private MyQueue<LoadImgDO> myQueue;

	public LoadImageConsumer(MyQueue<LoadImgDO> queue) {
		this.myQueue = queue;
	}

	@Override
	public void run() {
		while (true) {
			final LoadImgDO loadImgDO = myQueue.take();
			final Bitmap bitmap = tryLoadImage(loadImgDO);
			if (bitmap == null) {
				continue;
			}
			loadImgDO.getHandler().post(new Runnable() {
				@Override
				public void run() {
					if(loadImgDO.getNewWidth() > 0) {
						// 设置图片容器大小
						loadImgDO.getImageView().getLayoutParams().width = loadImgDO.getNewWidth();
						loadImgDO.getImageView().getLayoutParams().height = (int)(loadImgDO.getNewWidth() * bitmap.getHeight() / Double.valueOf(bitmap.getWidth()).doubleValue()); 
					}
					loadImgDO.getImageView().setImageBitmap(bitmap);
				}
			});
		}
	}

	private Bitmap tryLoadImage(LoadImgDO loadImgDO) {
		int tryTimes = 50;
		for (int i = 0; i < tryTimes; ++i) {
			try {
				// 加载图片需要具体处理
				return ImageUtil.getBitmap(loadImgDO.getFileName(), loadImgDO.getPicUrl());
			} catch (Exception e) {
				// 如果失败再重新取几次
				try {
					Thread.sleep(300);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
				}
			}
		}
		return null;
	}

}

