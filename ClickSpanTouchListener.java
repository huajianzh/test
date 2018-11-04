package com.xyy.view;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * 从LinkMovementMethod中将onTouchEvent部分移到该监听中处理当前评论TextView和ListView
 * 焦点冲突的问题(ListView不能触发OnItemClick)
 */
public class ClickSpanTouchListener implements View.OnTouchListener {
    private static ClickSpanTouchListener instance;

    private ClickSpanTouchListener() {
    }

    public static ClickSpanTouchListener getInstance() {
        if (null == instance) {
            instance = new ClickSpanTouchListener();
        }
        return instance;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        boolean result = false;
        TextView widget = (TextView) v;
        //基于文本框的内容创建Spannable对象
        Spannable buffer = Spannable.Factory.getInstance().newSpannable(widget.getText());

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }
                result = true;
            }
        }
        return result;
    }
}
