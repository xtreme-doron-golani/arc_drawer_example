package com.example.arcer.testapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class ArcDrawingPanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int POINTS_ON_CIRCLE = 50;
    public static final int ANIMATION_DELAY_STEP_MILLISECONDS = 20;
    public static final double PERCENTAGE_CHANGE_PER_ANIMATION_CHANGE = 1.5;
    public static final int MIN_POINTS_ON_ARC = 1;
    private Data data;

    public ArcDrawingPanel(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public ArcDrawingPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArcDrawingPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setData(Data data) {
        this.data = data;
        invalidate();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {}

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}

    @Override
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (data == null){
            return;
        }

        float total = 0;
        for(Pair<Float, Paint> pair : data.arcParts){
            total += pair.first;
        }

        float totalDegrees = 360 - data.emptyDegrees;
        float startingAngle = data.startAngleDegrees;
        float outerRadius = data.radius + data.strokeWidth;
        float maxDrawAngle = data.maxDrawPercentage / 100 * totalDegrees;
        boolean maxDrawAngelExceeded = false;
        for(Pair<Float, Paint> pair : data.arcParts){
            if(!maxDrawAngelExceeded) {
                float sweepAngleDegrees = pair.first / total * totalDegrees;
                maxDrawAngelExceeded = startingAngle + sweepAngleDegrees - data.startAngleDegrees > maxDrawAngle;
                if (maxDrawAngelExceeded) {
                    sweepAngleDegrees = maxDrawAngle + data.startAngleDegrees - startingAngle;
                }

                float endAngle = startingAngle + sweepAngleDegrees;
                PointF outerArcStartPoint = ArcUtils.pointFromAngleDegrees(data.center, outerRadius, startingAngle);
                PointF innerArcEndPoint = ArcUtils.pointFromAngleDegrees(data.center, data.radius, endAngle);

                drawSingleArc(canvas, POINTS_ON_CIRCLE, startingAngle, outerRadius, pair, sweepAngleDegrees, endAngle, outerArcStartPoint, innerArcEndPoint);

                startingAngle += sweepAngleDegrees;
            }
        }
        scheduleNextAnimationPhase();
    }

    private void drawSingleArc(Canvas canvas, int pointsOnCircle, float startingAngle, float outerRadius, Pair<Float, Paint> pair, float sweepAngleDegrees, float endAngle, PointF outerArcStartPoint, PointF innerArcEndPoint) {
        Path path = new Path();
        path.moveTo(outerArcStartPoint.x, outerArcStartPoint.y);
        float pointsOnArc = Math.min(MIN_POINTS_ON_ARC, pointsOnCircle * (sweepAngleDegrees / 360F));
        path = ArcUtils.createBezierArcDegrees(data.center, outerRadius, startingAngle, sweepAngleDegrees, (int) pointsOnArc, true, path);
        path.lineTo(innerArcEndPoint.x, innerArcEndPoint.y);
        path = ArcUtils.createBezierArcDegrees(data.center, data.radius, endAngle, -1 * sweepAngleDegrees, (int)pointsOnArc, true, path);
        path.lineTo(outerArcStartPoint.x, outerArcStartPoint.y);
        canvas.drawPath(path, pair.second);
    }

    private void scheduleNextAnimationPhase() {
        if(data.maxDrawPercentage < 100) {
            data.maxDrawPercentage+= PERCENTAGE_CHANGE_PER_ANIMATION_CHANGE;
            postInvalidateDelayed(ANIMATION_DELAY_STEP_MILLISECONDS);
        }
    }

    public static class Data{
        ArrayList<Pair<Float, Paint>> arcParts;
        float startAngleDegrees;
        float emptyDegrees;
        Paint backgroundPaint;
        float radius;
        float strokeWidth;
        PointF center;
        float maxDrawPercentage;
    }
}
