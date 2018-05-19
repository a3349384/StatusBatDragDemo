package cn.zmy.statusbatdragdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ViewAnimator;

public class MainActivity extends AppCompatActivity {

    private WindowManager mWindowManager;
    WindowManager.LayoutParams mLayoutParams;
    private GestureDetector mGestureDetector;
    private View mView;
    private int mViewHeightWhenInVisible = 120;

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
                updateBackgroundDim();
                return true;
            }
        });
        mView = LayoutInflater.from(this).inflate(R.layout.layout_fuc, null);
        mView.setTranslationY(-mViewHeightWhenInVisible
        );
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!v.isEnabled()) {
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mLayoutParams.height != WindowManager.LayoutParams.WRAP_CONTENT) {
                        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mView.measure(mLayoutParams.width, mLayoutParams.height);
                        mWindowManager.updateViewLayout(mView, mLayoutParams);
                        mView.setTranslationY(-mView.getMeasuredHeight());
                        updateBackgroundDim();
                    }
                }
                mGestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (Math.abs(mView.getTranslationY()) <= mView.getMeasuredHeight() / 2) {
                        //下拉超过临界距离
                        mView.animate()
                                .translationY(0)
                                .setDuration(300)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        mView.setEnabled(false);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mView.setEnabled(true);
                                        updateBackgroundDim();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        mView.setEnabled(true);
                                        updateBackgroundDim();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        updateBackgroundDim();
                                    }
                                })
                                .start();
                        mLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                        mWindowManager.updateViewLayout(mView, mLayoutParams);
                    } else {
                        mView.animate()
                                .translationY(-mView.getMeasuredHeight())
                                .setDuration(300)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        mView.setEnabled(false);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mView.setEnabled(true);
                                        mLayoutParams.height = mViewHeightWhenInVisible;
                                        mView.setTranslationY(-mViewHeightWhenInVisible);
                                        mLayoutParams.dimAmount = 0f;
                                        mWindowManager.updateViewLayout(mView, mLayoutParams);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        mView.setEnabled(true);
                                        mLayoutParams.height = mViewHeightWhenInVisible;
                                        mView.setTranslationY(-mViewHeightWhenInVisible);
                                        mWindowManager.updateViewLayout(mView, mLayoutParams);
                                        updateBackgroundDim();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        updateBackgroundDim();
                                    }
                                })
                                .start();
                        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                        mWindowManager.updateViewLayout(mView, mLayoutParams);
                    }
                }
                return true;
            }
        });
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                mViewHeightWhenInVisible,
                // Allows the view to be on top of the StatusBar
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSPARENT);
        mLayoutParams.gravity = Gravity.TOP;
        mLayoutParams.dimAmount = 0f;
        mWindowManager.addView(mView, mLayoutParams);
    }

    private void updateBackgroundDim() {
        float currentTranslationY = mView.getTranslationY();
        //TranslationY 0 -> dim 0.5f
        //TranslationY -mView.height -> dim 0f
        float dim = currentTranslationY / mView.getMeasuredHeight() / 2 + 0.5f;
        mLayoutParams.dimAmount = dim;
        Log.d("dim", "" + dim);
        mWindowManager.updateViewLayout(mView, mLayoutParams);
    }
}
