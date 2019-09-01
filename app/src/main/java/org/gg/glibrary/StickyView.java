package org.gg.glibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class StickyView extends View {

    private static final String TAG = "StickyView";
    private PointF pointA;
    private PointF pointB;
    private int r1;
    private int r2;
    private PointF center;
    private Paint paint;
    private Path path;

    public StickyView(Context context) {
        this(context, null);
    }

    public StickyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        path = new Path();

        pointA = new PointF();
        pointB = new PointF();
        center = new PointF();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        r1 = getMeasuredWidth() / 2;
        r2 = getMeasuredWidth() * 3 / 4;
        pointB.set(left, top);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(pointA.x, pointA.y, r1, paint);
        canvas.drawCircle(pointB.x, pointB.y, r2, paint);

        center.set((pointB.x + pointA.x) / 2, (pointB.y + pointA.y) / 2);
        double full = Math.sqrt(Math.pow(pointB.x - pointA.x, 2) + Math.pow(pointB.y - pointA.y, 2));
        double cosAngle = (pointB.x - pointA.x) / full;
        double sinAngle = (pointB.y - pointA.y) / full;
        double half = full / 2;
        double lengthR1 = Math.sqrt(half * half - r1 * r1);
        float x1 = (float) (r1 * r1 * cosAngle / half - lengthR1 * r1 * sinAngle / half);
        float y1 = (float) (lengthR1 * r1 * cosAngle / half + r1 * r1 * sinAngle / half);

        float x2 = (float) (r1 * r1 * cosAngle / half + lengthR1 * r1 * sinAngle / half);
        float y2 = (float) (lengthR1 * r1 * cosAngle / half - r1 * r1 * sinAngle / half);

        double lengthR2 = Math.sqrt(half * half - r2 * r2);
        float x3 = (float) (lengthR2 * r2 * sinAngle / half + r2 * r2 * cosAngle / half);
        float y3 = (float) (r2 * r2 * sinAngle / half - lengthR2 * r2 * cosAngle / half);

        float x4 = (float) (lengthR2 * r2 * sinAngle / half - r2 * r2 * cosAngle / half);
        float y4 = (float) (r2 * r2 * sinAngle / half + lengthR2 * r2 * cosAngle / half);

        path.reset();
        path.moveTo(pointA.x + x2, pointA.y - y2);
        path.quadTo(center.x, center.y, pointB.x + x4, pointB.y - y4);
        path.lineTo(pointB.x - x3, pointB.y - y3);
        path.quadTo(center.x, center.y, pointA.x + x1, pointA.y + y1);
        path.lineTo(pointA.x + x2, pointA.y - y2);

        canvas.drawPath(path, paint);
    }

    public void setRadius(int radius) {
        this.r1 = radius;
        this.r2 = (int) (radius * 0.75);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float rawX = event.getRawX();
                float rawY = event.getRawY();
                pointB.x = rawX;
                pointB.y = rawY;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                pointB.x = pointA.x;
                pointB.y = pointA.y;
                postInvalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
}
