package viewl.dq.com.viewl.draw1;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.LinearInterpolator;

import viewl.dq.com.viewl.R;

/**
 * Created by liqing on 17-10-25.
 */

public class WaterRipple extends View{
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap bitmap;
    private Camera camera = new Camera();
    int degree;
    int degreeZ;
    ObjectAnimator animator = ObjectAnimator.ofInt(this, "degreeY", 0, 45);
    ObjectAnimator animator1 = ObjectAnimator.ofInt(this, "degreeZ", -380, 380);
    AnimatorSet set = new AnimatorSet();
    public WaterRipple(Context context) {
        super(context);
    }

    public WaterRipple(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterRipple(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float newZ = -displayMetrics.density * 20;
        camera.setLocation(0, 0, newZ);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maps);

        animator.setDuration(1500);
        animator.setInterpolator(new LinearInterpolator());
        //animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);

        animator1.setDuration(1500);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatMode(ValueAnimator.REVERSE);

        set.playTogether(animator,animator1);
        set.start();
    }

    @SuppressWarnings("unused")
    public void setDegreeY(int degree) {
        this.degree = degree;
        invalidate();
    }

    @SuppressWarnings("unused")
    public void setDegreeZ(int degree) {
        this.degreeZ = degree;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int x = centerX - bitmapWidth / 2;
        int y = centerY - bitmapHeight / 2;

        // 第一遍绘制：上半部分
        canvas.save();
        canvas.translate(centerX, centerY);
        canvas.translate(0,27); // 旋转45度后，平面位置需要移动的距离，可以自己写程序计算下，懒的写了
        canvas.translate(0,degreeZ);
        canvas.clipRect(-centerX, -centerY, centerX, -100);
        canvas.translate(0,-degreeZ);
        canvas.translate(-centerX, -centerY);
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();

        canvas.save();
        camera.save();
        canvas.translate(centerX, centerY-100);
        canvas.translate(0,27); // 旋转45度后，平面位置需要移动的距离
        canvas.translate(0,degreeZ);
        camera.rotateX(45);
        camera.applyToCanvas(canvas);
        camera.restore();
        canvas.clipRect(-centerX, 0, centerX, 100);
        canvas.translate(0,-degreeZ);
        canvas.translate(-centerX, -centerY+100);
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();

        // 第二遍绘制：下半部分
        canvas.save();
        canvas.translate(centerX, centerY);
        canvas.translate(0,-30); // 旋转45度后，平面位置需要移动的距离
        canvas.translate(0,degreeZ);
        canvas.clipRect(-centerX, 100, centerX, centerY);
        canvas.translate(0,-degreeZ);
        canvas.translate(-centerX, -centerY);
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();

        canvas.save();
        camera.save();
        canvas.translate(centerX, centerY+100);
        canvas.translate(0,-30); // 旋转45度后，平面位置需要移动的距离
        canvas.translate(0,degreeZ);
        camera.rotateX(-45);
        camera.applyToCanvas(canvas);
        camera.restore();
        canvas.clipRect(-centerX, -100, centerX, 0);
        canvas.translate(0,-degreeZ);
        canvas.translate(-centerX, -centerY-100);
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();
    }
}
