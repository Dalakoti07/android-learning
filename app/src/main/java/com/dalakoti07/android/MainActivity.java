package com.dalakoti07.android;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*RoundRectShape roundRectShape = new RoundRectShape(new float[]{
                10, 10, 10, 10,
                10, 10, 10, 10}, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(Color.parseColor("#FFFFFF"));
        ImageView myImageView = findViewById(R.id.iv_java);
        myImageView.setBackground(shapeDrawable);*/
        // or you can use myImageView.setImageDrawable(shapeDrawable);
    }
}