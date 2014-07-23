package com.example.arcer.testapp;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        drawArc();

        Button reloadButton = (Button)findViewById(R.id.reload_button);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawArc();
            }
        });
    }

    private void drawArc() {
        ArcDrawingPanel arcDrawingPanel = (ArcDrawingPanel)findViewById(R.id.arcDrawer);
        arcDrawingPanel.setData(getExampleData());
    }

    private ArcDrawingPanel.Data getExampleData() {
        ArcDrawingPanel.Data data = new ArcDrawingPanel.Data();

        data.center = new PointF(150, 150);
        data.radius = 100;

        data.arcParts = new ArrayList<Pair<Float, Paint>>();

        Paint red = new Paint(Paint.ANTI_ALIAS_FLAG);
        red.setColor(Color.RED);

        Pair<Float, Paint> first = new Pair<Float, Paint>(1.5F, red);

        Paint blue = new Paint(Paint.ANTI_ALIAS_FLAG);
        blue.setColor(Color.BLUE);

        Pair<Float, Paint> second = new Pair<Float, Paint>(10.5F, blue);

        Paint green = new Paint(Paint.ANTI_ALIAS_FLAG);
        green.setColor(Color.GREEN);

        Pair<Float, Paint> third = new Pair<Float, Paint>(3.5F, green);

        data.arcParts.add(first);
        data.arcParts.add(second);
        data.arcParts.add(third);

        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.BLACK);
        data.backgroundPaint = backgroundPaint;
        data.emptyDegrees = 60;
        data.startAngleDegrees = 90 + (data.emptyDegrees / 2);
        data.strokeWidth = 50;

        data.maxDrawPercentage = 0;
        return data;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
