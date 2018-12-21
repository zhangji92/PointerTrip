package yc.pointer.trip.view;

/**
 * Created by moyan on 2017/8/31.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import yc.pointer.trip.R;

/**
 * 自定义的圆角矩形ImageView，可以直接当组件在布局中使用。
 * @author caizhiming
 *
 */
public class RoundImageView  extends ImageView{
    private float xRadius = 30;
    private float yRadius = 30;
    private Paint paint = new Paint();

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getxRadius() {
        return xRadius;
    }

    public void setxRadius(float xRadius) {
        this.xRadius = xRadius;
    }

    public float getyRadius() {
        return yRadius;
    }

    public void setyRadius(float yRadius) {
        this.yRadius = yRadius;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDraw(Canvas canvas) {
        // java.lang.ClassCastException: android.graphics.drawable.TransitionDrawable cannot be cast
        // to android.graphics.drawable.BitmapDrawable
        BitmapShader shader;
        if (getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable)getDrawable();
            // clip
            shader = new BitmapShader(bitmapDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            RectF rect = new RectF(0.0f, 0.0f, getWidth(), getHeight());
            int width = bitmapDrawable.getBitmap().getWidth();
            int height = bitmapDrawable.getBitmap().getHeight();
            RectF src = null;
            if (((float)width) / height > 1) {
                src = new RectF(0.0f, 0.0f, height, height);
            } else {
                src = new RectF(0.0f, 0.0f, width, width);
            }
            Matrix matrix = canvas.getMatrix();
            matrix.setRectToRect(src, rect, Matrix.ScaleToFit.CENTER);
            shader.setLocalMatrix(matrix);

            // 抗锯齿
            paint.setAntiAlias(true);
            paint.setShader(shader);
            // draw round circle for HeadImage or other
//            canvas.drawRoundRect(rect, this.getWidth() / 2, this.getHeight() / 2, paint);
             canvas.drawRoundRect(rect, xRadius, yRadius , paint);
        }
    }
}
