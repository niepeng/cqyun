package com.baibutao.app.waibao.yun.android.common;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.baibutao.app.waibao.yun.android.localcache.ImageCache;
import com.baibutao.app.waibao.yun.android.util.IoUtil;

/**
 * @author lsb
 *
 * @date 2012-6-4 下午02:45:59
 */
public class ImageUtil {
	// 角度
	public static final int ScaleAngle = 90;

	// 圆角
	public static final float ROUNDPX = 15.0f;
	
	public static final int IMG_WIDTH = 480;
	
    public static final int IMG_HEIGHT = 480;
    
	
	
	public static Bitmap change(Drawable drawable) {
		if(drawable == null) {
			return null;
		}
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}
	
	public static Drawable change(Bitmap bitmap) {
		if(bitmap == null) {
			return null;
		}
		return new BitmapDrawable(bitmap); 
	}
	
	public static byte[] change2Byte(Drawable drawable) {
		Bitmap bitmap = change(drawable);
		if (bitmap == null) {
			return null;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		return stream.toByteArray();
	}

	/**
	 * bitmap旋转一定的角度
	 * 
	 * @param bitmap
	 * @param scaleAngle
	 * @return
	 */
	public static Bitmap rotation(Bitmap bitmap, int scaleAngle) {
		try {
			final int widthOrig = bitmap.getWidth();
			final int heightOrig = bitmap.getHeight();
			/* 维持1:1的宽高比例, 否则可以 乘以某个系数 */
			int newWidth = widthOrig;
			int newHeight = heightOrig;
			/* 计算旋转的Matrix比例 */
			float scaleWidth = ((float) newWidth) / widthOrig;
			float scaleHeight = ((float) newHeight) / heightOrig;
			Matrix matrix = new Matrix();
			/* 使用Matrix.postScale设定维度 */
			matrix.postScale(scaleWidth, scaleHeight);
			/* 使用Matrix.postRotate方法旋转Bitmap */
			matrix.setRotate(scaleAngle);
			/* 建立新的Bitmap对象 */
			return Bitmap.createBitmap(bitmap, 0, 0, widthOrig, heightOrig, matrix, true);
		} catch (Exception e) {
			Log.e("ImageUtil", "旋转图片出现异常，不转换返回", e);
			return bitmap;
		}
	}
	
	 public static Bitmap cuteImage(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
     	// 矩阵缩放
     	Matrix matrix = new Matrix();
     	int oldWidth = bitmap.getWidth();
     	int oldHeight = bitmap.getHeight();
     	float scaleWidth = 0.0f;
     	float scaleHeight = 0.0f;
     	if(oldWidth > oldHeight) {
     		scaleHeight = (float) IMG_HEIGHT / oldHeight;
     		matrix.postScale(scaleHeight, scaleHeight);
     	} else {
     		scaleWidth = (float)  IMG_WIDTH / oldWidth;
     		matrix.postScale(scaleWidth, scaleWidth);
     	}
         // 缩放图片,按照小的变来缩小
     	Bitmap newBitmap =  Bitmap.createBitmap(bitmap, 0, 0, oldWidth, oldHeight, matrix, true);
     	// 裁剪图片
     	return  Bitmap.createBitmap(newBitmap, (newBitmap.getWidth() - IMG_WIDTH)/2,  (newBitmap.getHeight() - IMG_HEIGHT )/2 , IMG_WIDTH, IMG_HEIGHT);
     }

	/**
	 * 图片圆角处理
	 * 
	 * @param bitmap
	 * @param roundPX
	 * @return
	 */
	public static Bitmap getRCB(Bitmap bitmap, float roundPX) // RCB means
	// Rounded
	// Corner Bitmap
	{
		if (bitmap == null) {
			return null;
		}
		Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(dstbmp);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return dstbmp;
	}

	public static Bitmap getBitmap(String srcName, String picUrl) {
		String name = ImageCache.makeKeyName(srcName);
		Bitmap bmp = ImageCache.getBitmap(srcName);
		if (bmp != null) {
			return bmp;
		}
		try {
			URL url = new URL(picUrl);
			byte[] data = IoUtil.readFromUrlByHttpClient(url);
			if (data == null) {
				return null;
			}
			ImageCache.setBitmapData(name, data);
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//private static final int MAX_NUM_PIXELS = 480 * 480;

	public static Bitmap asBitmap(byte[] imageData, int size) {
		if (imageData == null) {
			return null;
		}
		final int maxNumPixels = size * size;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(imageData, 0, imageData.length, opts);
		
		opts.inSampleSize = computeSampleSize(opts, size, maxNumPixels);
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(imageData, 0, imageData.length, opts);
	}
	
	/*public static Bitmap ajustSizeImage(Bitmap bitmap, int width, int height) {
		if (bitmap == null || width < 1 || height < 1) {
			return null;
		}
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(width, height);
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}*/
	
	private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 480 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

}
