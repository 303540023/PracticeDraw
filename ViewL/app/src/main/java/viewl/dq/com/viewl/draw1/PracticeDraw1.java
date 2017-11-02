package viewl.dq.com.viewl.draw1;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;


/**
 * Created by liqing on 17-10-25.
 */

public class PracticeDraw1 extends View{

    private static final float REDIUS = 300; // 饼状图半径
    private static final float START_ANGLE = 0; // 画第一个扇形图的起始水平夹角,正方向0度
    private float OFFSET = 30;  // 指定的扇形图偏移距离
    private float CX0 = 700;    // 饼状图X坐标
    private float CY0 = 1000;    // 饼状图Y坐标

    private Camera camera = new Camera();
    private Path path = new Path();
    private int degree;
    private int degreeZ;
    ObjectAnimator animator = ObjectAnimator.ofInt(this, "degreeX", 0, 180);
    ObjectAnimator animator1 = ObjectAnimator.ofInt(this, "degreeZ", 180);
    AnimatorSet set = new AnimatorSet();

    private static float[] DATA = {88.2f,52.0f,50f,88.0f,88.0f,65.5f} ;
    private static int[] PAINT_COLOR = {Color.RED,Color.BLACK,Color.BLUE,Color.GREEN,Color.CYAN,Color.YELLOW};

    public PracticeDraw1(Context context) {
        super(context);
    }

    public PracticeDraw1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PracticeDraw1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        path.addArc(-CX0,-CY0,CX0,CY0,0,200);

        animator.setDuration(2500);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);

        animator1.setDuration(1500);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatMode(ValueAnimator.REVERSE);
        animator1.setStartDelay(2500);

        set.playTogether(animator,animator1);
        set.start();
    }

    @SuppressWarnings("unused")
    private void setDegreeX(int degree) {
        this.degree = degree;
        invalidate();
    }

    @SuppressWarnings("unused")
    private void setDegreeZ(int degreeZ) {
        this.degreeZ = degreeZ;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        OFFSET = degree;

        float[] dataAngle = getDataAngle(DATA);
        float startAngle = START_ANGLE;

        // 先画下半圆
        canvas.save();
        canvas.translate(CX0,CY0);
        canvas.rotate(degreeZ);
        canvas.clipRect(-CX0,-CY0,0,CY0);
        canvas.rotate(-degreeZ);

        canvas.translate(-CX0,-CY0);
        for (int i = 0;i<dataAngle.length;i++){
            if (i == 0){
                Paint paint1 = new Paint();
                paint1.setColor(PAINT_COLOR[i]);
                float[] XY = getXYOffset(OFFSET,dataAngle[i], START_ANGLE);
                float X = XY[0];
                float Y = XY[1];
                canvas.drawArc(CX0 + X - REDIUS,CY0 + Y - REDIUS,CX0 + X + REDIUS,CY0 + Y + REDIUS,startAngle,dataAngle[i],true,paint1);
            } else {
                Paint paint4 = new Paint();
                if (i < PAINT_COLOR.length){
                    paint4.setColor(PAINT_COLOR[i]);
                }else {
                    paint4.setColor(PAINT_COLOR[i - PAINT_COLOR.length + 1]);
                }

                startAngle = (startAngle + dataAngle[i-1]) % 360;
                float[] XY = getXYOffset(OFFSET,dataAngle[i], startAngle);
                float X = XY[0];
                float Y = XY[1];
                canvas.drawArc(CX0 + X - REDIUS,CY0 +Y - REDIUS,CX0 +X + REDIUS,CY0 +Y+ REDIUS,startAngle,dataAngle[i],true,paint4);
            }
        }
        canvas.restore();
    }

    private float[] getDataAngle(float[] datas){
        float total = 0;
        float copy = 0;
        for (int i = 0;i<datas.length;i++){
            total += datas[i];
        }

        float[] dataPercent = new float[datas.length];
        for (int i = 0;i<datas.length;i++){
            dataPercent[i] = datas[i] * (360/total);
            if (dataPercent[i] > dataPercent[0]){
                copy = dataPercent[0];
                dataPercent[0] = dataPercent[i];
                dataPercent[i] = copy;
            }
        }
        return dataPercent;
    }

    private float[] getXYOffset(float offset, float dataAngle,float startAngle){
        float rightAngle = 90f;
        float useAngle = Math.round(dataAngle + startAngle);
        float[] XY = new float[2];
        double X = 0;
        double Y = 0;
        if (useAngle <= rightAngle){
            if (startAngle >0){
                Y = Math.sin((useAngle - dataAngle/2)*Math.PI/180) * offset;
                X = Math.sqrt(offset*offset - Y*Y);
                XY[0] = (float) X;
                XY[1] = (float) Y;
            } else {
                Y = Math.sin(useAngle/2*Math.PI/180) * offset;
                X = Math.sqrt(offset*offset - X*X);
                XY[0] = (float) X;
                XY[1] = (float) Y;
            }
        } else if (rightAngle < useAngle && useAngle <= 2*rightAngle){
            if (startAngle >0){
                X = Math.sin((useAngle - rightAngle - dataAngle/2)*Math.PI/180) * offset;
                Y = Math.sqrt(offset*offset - X*X);
                XY[0] = -(float) X;
                XY[1] = (float) Y;
            } else {
                float d = useAngle - rightAngle;
                X = Math.sin((useAngle/2 - d)*Math.PI/180) * offset;
                Y = Math.sqrt(Math.abs(offset*offset - X*X));
                XY[0] = (float) X;
                XY[1] = (float) Y;
            }
        } else if (2*rightAngle < useAngle && useAngle <= 3*rightAngle){
            if (startAngle >0){
                Y = Math.sin((useAngle - 2*rightAngle - dataAngle/2)*Math.PI/180) * offset;
                X = Math.sqrt(offset*offset - Y*Y);
                XY[0] = -(float) X;
                XY[1] = -(float) Y;
            } else {
                X = Math.sin((useAngle/2-rightAngle)*Math.PI/180) * offset;
                Y = Math.sqrt(offset*offset - X*X);
                XY[0] = -(float) X;
                XY[1] = (float) Y;
            }
        } else if (3*rightAngle < useAngle && useAngle <= 4*rightAngle){
            if (startAngle >0){
                X = Math.sin((useAngle - 3*rightAngle - dataAngle/2)*Math.PI/180) * offset;
                Y = Math.sqrt(offset*offset - X*X);
                XY[0] = (float) X;
                XY[1] = -(float) Y;
            } else {
                Y = Math.sin((2*rightAngle - useAngle/2)*Math.PI/180) * offset;
                X = Math.sqrt(offset*offset - Y*Y);
                XY[0] = -(float) X;
                XY[1] = (float) Y;
            }
        }
        return XY;
    }
}
