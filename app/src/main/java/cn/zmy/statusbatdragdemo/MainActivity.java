package cn.zmy.statusbatdragdemo;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private WindowManager mWindowManager;
    private GestureDetector mGestureDetector;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (mWindowManager == null) {
            return;
        }
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d("SCROLL", distanceY + "");
                float targetTranslationY = Math.min(0, mView.getTranslationY() - distanceY);
                mView.setTranslationY(targetTranslationY);
                return true;
            }
        });
        mView = LayoutInflater.from(this).inflate(R.layout.layout_fuc, null);
        mView.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mView.setTranslationY(-mView.getMeasuredHeight());
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return false;
            }
        });
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                // Allows the view to be on top of the StatusBar
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSPARENT);
        layoutParams.gravity = Gravity.TOP;
        layoutParams.dimAmount = 0.1f;
        mWindowManager.addView(mView, layoutParams);
    }
}
