package yc.pointer.trip.view;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;
import yc.pointer.trip.R;

/**
 * Created by Administrator on 2017/3/29.
 * 自定义原型头像
 */

public class CustomCircleImage extends AppCompatImageView {
    private Paint mPaintBitmap = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap mRawBitmap;
    private BitmapShader mShader;
    private Matrix mMatrix = new Matrix();

    public CustomCircleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap rawBitmap = getBitmap(getDrawable());
        if (rawBitmap != null) {
            int viewWidth = getWidth();//图片宽度
            int viewHeight = getHeight();//图片高度
            int viewMinSize = Math.min(viewWidth, viewHeight);//哪个小选哪个作为缩放比例
            float dstWidth = viewMinSize;
            float dstHeight = viewMinSize;
            if (mShader == null || !rawBitmap.equals(mRawBitmap)) {
                mRawBitmap = rawBitmap;
                mShader = new BitmapShader(mRawBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }
            if (mShader != null) {
                mMatrix.setScale(dstWidth / rawBitmap.getWidth(), dstHeight / rawBitmap.getHeight());
                mShader.setLocalMatrix(mMatrix);//缩放图片
            }
            float radius = viewMinSize / 2.0f;//圆心X,Y为图片大小的一半，这就是圆
            //画头像外面白色圆圈
            mPaint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawCircle(radius, radius, radius, mPaint);
            //画圆形头像
            mPaintBitmap.setShader(mShader);
            canvas.drawCircle(radius, radius, radius-5, mPaintBitmap);

        } else {
            super.onDraw(canvas);
        }
    }

    private Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof ColorDrawable) {
            Rect rect = drawable.getBounds();
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            int color = ((ColorDrawable) drawable).getColor();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            return bitmap;
        } else {
            return null;
        }
    }
}
