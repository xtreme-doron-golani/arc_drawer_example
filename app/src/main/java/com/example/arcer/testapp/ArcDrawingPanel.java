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
    public static final int ANIMATION_DELAY_STEP_MILLISECONDS = 10;
    public static final double PERCENTAGE_CHANGE_PER_ANIMATION_CHANGE = 1;
    public static final int MIN_POINTS_ON_ARC = 1;
    public static final int BREAK_INTO_X_PARTS = 3;
    private Data data;

    private ArrayList<Pair<Float, Paint>> internalArcParts = new ArrayList<Pair<Float, Paint>>();

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

        workAround180DegreeProblem(data.arcParts);

        invalidate();
    }

    private void workAround180DegreeProblem(ArrayList<Pair<Float, Paint>> originalArcParts) {
        internalArcParts.clear();
        float total = getTotalArcsWeight(originalArcParts);
        for(Pair<Float, Paint> pair : originalArcParts){
            if(pair.first / total >= 0.5){
                for(int i = 0; i < BREAK_INTO_X_PARTS; i++) {
                    internalArcParts.add(new Pair<Float, Paint>(pair.first / BREAK_INTO_X_PARTS, pair.second));
                }
            }
            else{
                internalArcParts.add(pair);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {}

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}


    private float getTotalArcsWeight(ArrayList<Pair<Float, Paint>> arcs){
        float total = 0;
        for(Pair<Float, Paint> pair : arcs){
            total += pair.first;
        }
        return total;
    }

    @Override
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (data == null){
            return;
        }

        float total = getTotalArcsWeight(internalArcParts);

        float totalDegrees = 360 - data.emptyDegrees;
        float startingAngle = data.startAngleDegrees;
        float outerRadius = data.radius + data.strokeWidth;
        float maxDrawAngle = data.maxDrawPercentage / 100 * totalDegrees;
        boolean maxDrawAngelExceeded = false;
        float overlapDrawingOffset = 0.0F;
        int index = 0;
        for(Pair<Float, Paint> pair : internalArcParts){
            if(!maxDrawAngelExceeded) {
                float sweepAngleDegrees = pair.first / total * totalDegrees;
                maxDrawAngelExceeded = startingAngle + sweepAngleDegrees - data.startAngleDegrees > maxDrawAngle;
                if (maxDrawAngelExceeded) {
                    sweepAngleDegrees = maxDrawAngle + data.startAngleDegrees - startingAngle;
                }

                float endAngle = startingAngle + sweepAngleDegrees + overlapDrawingOffset;
                float startingAngleForDrawing = startingAngle - overlapDrawingOffset;
                PointF outerArcStartPoint = ArcUtils.pointFromAngleDegrees(data.center, outerRadius, startingAngleForDrawing);
                PointF innerArcEndPoint = ArcUtils.pointFromAngleDegrees(data.center, data.radius, endAngle);

                drawSingleArc(canvas, POINTS_ON_CIRCLE, startingAngleForDrawing, outerRadius, pair, sweepAngleDegrees + overlapDrawingOffset, endAngle, outerArcStartPoint, innerArcEndPoint);

                startingAngle += sweepAngleDegrees;
            }

            overlapDrawingOffset = 1F ;
            index++;
        }
        scheduleNextAnimationPhase();
    }

    private void drawSingleArc(Canvas canvas, int pointsOnCircle, float startingAngle, float outerRadius, Pair<Float, Paint> pair, float sweepAngleDegrees, float endAngle, PointF outerArcStartPoint, PointF innerArcEndPoint) {
        Path path = new Path();
        path.moveTo(outerArcStartPoint.x, outerArcStartPoint.y);
        float pointsOnArc = Math.min(MIN_POINTS_ON_ARC, pointsOnCircle * (sweepAngleDegrees / 360F));
        path = ArcUtils.createBezierArcDegrees(data.center, outerRadius, startingAngle, sweepAngleDegrees, (int) pointsOnArc, false, path);
        path.lineTo(innerArcEndPoint.x, innerArcEndPoint.y);
        path = ArcUtils.createBezierArcDegrees(data.center, data.radius, endAngle, -1 * sweepAngleDegrees, (int)pointsOnArc, false, path);
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
