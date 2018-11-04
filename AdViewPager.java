package com.xyy.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 广告轮播视图
 */

public class AdViewPager extends ViewPager {

    public AdViewPager(Context context) {
        super(context);
    }

    public AdViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopLoop();
                break;
            case MotionEvent.ACTION_UP:
                startLoop();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private Thread mThread;
    //更新方向
    private int offset=1;

    public void startLoop() {
        if (null != mThread && mThread.isAlive()) {
            return;
        }
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    int count = getAdapter().getCount();
                    while (true) {
                        Thread.sleep(3000);
                        int index = getCurrentItem();
                        int nextIndex = index+offset;
                        if (nextIndex >= count - 1) {
                            offset=-1;
                            nextIndex=count-1;
                        } else if(nextIndex<=0) {
                            offset = 1;
                            nextIndex = 0;
                        }
                        mHandler.obtainMessage(1, nextIndex).sendToTarget();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }

    public void stopLoop() {
        if (null != mThread) {
            mThread.interrupt();
            mThread = null;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int index = (Integer) msg.obj;
            setCurrentItem(index);
        }
    };
}
