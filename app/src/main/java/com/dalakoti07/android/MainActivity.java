package com.dalakoti07.android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private LinearLayout llTop,llBottom;
    private View dragView;
    private Pair<Integer,Integer> xAndYValues;
    int interMediateX,interMediateY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llTop=findViewById(R.id.ll_top);
        llBottom=findViewById(R.id.ll_bottom);
        dragView=findViewById(R.id.drag_view);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Log.d(TAG, "onCreate: width " +width+" and height "+height);

        llTop.setOnDragListener(dragListener);
        llBottom.setOnDragListener(dragListener);

        dragView.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onLongClick(View view) {
                String clipText="This is our clip data";
                ClipData.Item item=new ClipData.Item(clipText);
//                ArrayList<String> mimeTypes= new ArrayList<>(Arrays.asList(ClipDescription.MIMETYPE_TEXT_PLAIN));
                ClipData clipData= new ClipData(clipText,new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},item);
                View.DragShadowBuilder dragShadowBuilder= new View.DragShadowBuilder(view);
                view.startDragAndDrop(clipData,dragShadowBuilder,view,0);
                view.setVisibility(View.INVISIBLE);
                return true;
            }
        });

    }

    private View.OnDragListener dragListener= new View.OnDragListener() {
        @Override
        public boolean onDrag(View listenerView, DragEvent dragEvent) {
            switch (dragEvent.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d(TAG, "onDrag started ");
                    dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d(TAG, "onDrag: entered");
                    // the clippedView has entered our area redraw it
                    listenerView.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    Log.d(TAG, "onDrag: location change");
                    Log.d(TAG, "onDrag: x value: "+dragEvent.getX()+" and y value: "+dragEvent.getY());
                    interMediateX=(int) dragEvent.getX();
                    interMediateY=(int) dragEvent.getY();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:{
                    listenerView.invalidate();
                    return true;
                }
                case DragEvent.ACTION_DROP:{
                    Log.d(TAG, "onDrag: dropped");
                    ClipData.Item item=dragEvent.getClipData().getItemAt(0);
                    String itemData =item.getText().toString();
                    Toast.makeText(MainActivity.this, itemData, Toast.LENGTH_SHORT).show();

                    listenerView.invalidate();

                    View v=(View) dragEvent.getLocalState();
                    ViewGroup owner= (ViewGroup) v.getParent();
                    owner.removeView(v);
                    LinearLayout destination= (LinearLayout) listenerView;
                    destination.addView(v);
                    v.setVisibility(View.VISIBLE);
                    return true;
                }
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d(TAG, "onDrag: ended");
                    //re-create the view
                    addADotToTheView(listenerView);
                    listenerView.invalidate();
                    return true;
                default:
                    Log.d(TAG, "onDrag: default ");
                    return false;
            }
        }
    };

    private void addADotToTheView(View parentView) {

    }

}