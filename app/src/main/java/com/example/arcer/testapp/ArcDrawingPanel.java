package com.example.arcer.testapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class ArcDrawingPanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int POINTS_ON_CIRCLE = 50;
    public static final int ANIMATION_DELAY_STEP_MILLISECONDS = 20;
    public static final double PERCENTAGE_CHANGE_PER_ANIMATION_CHANGE = 1.5;
    public static final int MIN_POINTS_ON_ARC = 5;
    public static final float STEP_IN_DEGREES = 1F;
    private Data data;

    ArrayList<Pair<Path, Paint>> paths = new ArrayList<Pair<Path, Paint>>();

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
        paths.clear();

        float total = 0;
        for(Pair<Float, Paint> pair : data.arcParts){
            total += pair.first;
        }

        float totalDegrees = 360 - data.emptyDegrees;
        float startingAngle = data.startAngleDegrees;
        float outerRadius = data.radius + data.strokeWidth;
        for(Pair<Float, Paint> pair : data.arcParts){

            float sweepAngleDegrees = pair.first / total * totalDegrees;
            float endAngle = startingAngle + sweepAngleDegrees;

            for(float i=0; i < sweepAngleDegrees; i = i + STEP_IN_DEGREES){

                float stepStartingAngle = startingAngle + i;
                float stepEndAngle = stepStartingAngle + STEP_IN_DEGREES;
                stepEndAngle = Math.min(endAngle, stepEndAngle);
                float stepSweepingDegrees = stepEndAngle - stepStartingAngle;

                PointF stepOuterArcStartPoint = ArcUtils.pointFromAngleDegrees(data.center, outerRadius, stepStartingAngle);
                PointF stepInnerArcEndPoint = ArcUtils.pointFromAngleDegrees(data.center, data.radius, stepEndAngle);

                Log.d("stepSweepingDegrees " + stepSweepingDegrees + " stepOuterArcStartPoint " + stepOuterArcStartPoint.toString(), " stepInnerArcEndPoint " + stepInnerArcEndPoint);

                addSingleArc(POINTS_ON_CIRCLE, stepStartingAngle, outerRadius, pair.second, stepSweepingDegrees, stepEndAngle, stepOuterArcStartPoint, stepInnerArcEndPoint);
            }

            startingAngle += sweepAngleDegrees;

        }

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

        float percent = Math.min(1.0F , data.maxDrawPercentage / 100.0F);
        int maxDrawPath = (int)Math.ceil(percent * paths.size());

        for(int i = 0; i < maxDrawPath; i++){
            Pair<Path, Paint> path = paths.get(i);
            canvas.drawPath(path.first, path.second);
        }

        scheduleNextAnimationPhase();
    }

    private void addSingleArc(int pointsOnCircle, float startingAngle, float outerRadius, Paint paint, float sweepAngleDegrees, float endAngle, PointF outerArcStartPoint, PointF innerArcEndPoint) {
        Path path = new Path();
        path.moveTo(outerArcStartPoint.x, outerArcStartPoint.y);
        float pointsOnArc = Math.min(MIN_POINTS_ON_ARC, pointsOnCircle * (sweepAngleDegrees / 360F));

//        addArc(path, startingAngle, sweepAngleDegrees, outerRadius);
        path = ArcUtils.createBezierArcDegrees(data.center, outerRadius, startingAngle, sweepAngleDegrees, (int) pointsOnArc, true, path);

        path.lineTo(innerArcEndPoint.x, innerArcEndPoint.y);

//        addArc(path, endAngle, -1.0F * sweepAngleDegrees, data.radius);
        path = ArcUtils.createBezierArcDegrees(data.center, data.radius, endAngle, -1 * sweepAngleDegrees, (int)pointsOnArc, true, path);

        path.lineTo(outerArcStartPoint.x, outerArcStartPoint.y);
        paths.add(new Pair<Path, Paint>(path, paint));
    }

    private void addArc(Path path, float startingAngle, float sweepAngleDegrees, float radius){
        RectF rectF = new RectF(data.center.x - radius, data.center.y - radius, data.center.x + radius, data.center.y + radius);
        path.addArc(rectF, startingAngle, sweepAngleDegrees);
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
