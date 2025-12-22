package mariusbinary.cursorr.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;

import java.util.ArrayList;
import java.util.List;

import mariusbinary.cursorr.R;
import mariusbinary.cursorr.network.NetworkManager;

public class Touchpad extends View implements   GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private boolean mGestureRecognition = false;
    private boolean mTouchpadTrail = false;
    private boolean mTouchpadEnabled = false;

    private final int mouseTouchpadBg;
    private final int mouseTouchpadScrollThumb;
    private final int mouseTouchpadScrollInactive;
    private final int mouseTouchpadScrollActive;
    private final int mouseTouchpadTouchBr;
    private boolean mTouchAnimation;

    private Handler handler;
    private Runnable movePlayer0Runnable;

    public Touchpad(Context context) {
        this(context, null);
    }

    public Touchpad(Context context, AttributeSet attrs) {
        super(context, attrs);

        mouseTouchpadBg = getResources().getColor(R.color.mouseTouchpadBg);
        mouseTouchpadScrollThumb = getResources().getColor(R.color.mouseTouchpadScrollThumb);
        mouseTouchpadScrollInactive = getResources().getColor(R.color.mouseTouchpadScrollInactive);
        mouseTouchpadScrollActive = getResources().getColor(R.color.mouseTouchpadScrollActive);
        mouseTouchpadTouchBr = getResources().getColor(R.color.mouseTouchpadTouchBr);

        mDetector = new GestureDetectorCompat(context, this);
        mDetector.setOnDoubleTapListener(this);

        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        sendMouseButtonData("left");
        sendMouseButtonData("left");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        sendMouseButtonData("left");
        return true;
    }

    private void sendMouseButtonData(final String data) {
        new Thread(() -> {
            if (NetworkManager.sConnectionAlive) {
                NetworkManager.mClientIOThread.sendData("Mouse_Button", data);
            }
        }).start();
    }

    public void setGestureRecognition(boolean enabled) {
        mGestureRecognition = enabled;
    }

    public void setTouchpadTrail(boolean enabled) {
        mTouchpadTrail = enabled;

        if (enabled) {
            mTouchAnimation = true;
            movePlayer0Runnable = new Runnable(){
                public void run() {
                    if (mTouchpadEnabled) {
                        invalidate();
                    }
                    if (mTouchAnimation) {
                        handler.postDelayed(this, 10);
                    }
                }
            };
            movePlayer0Runnable.run();
        } else {
            mTouchAnimation = false;
        }
    }

    public void setTouchpadEnabled(boolean enabled) {
        mTouchpadEnabled = enabled;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return true;
    }

    private void drawCircle(Canvas canvas, float x, float y, float r, boolean b) {

        float alpha;
        float scale;

        Paint paint = new Paint();

        if (b) {
            alpha = 1f;
            scale = 1f;
        } else {
            alpha = r / 2f;
            scale = r;
        }
        paint.setColor(mouseTouchpadScrollActive);

        paint.setAlpha((int)(alpha * 255));
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        canvas.drawCircle(x, y, scale * 50,  paint);
    }

    boolean scrollMode = false;
    int motionTrailLength = 15;
    List<Member> positions = new ArrayList<>();
    class Member
    {
        public int x;
        public int y;

        public Member(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    boolean findPoint(float x1, float y1, float x2, float y2, float x, float y)
    {
        return (x > x1 && x < x2 && y > y1 && y < y2);
    }

    public void storeLastPosition(int xPos, int yPos) {
        positions.add(new Member(xPos, yPos));

        if (positions.size() > motionTrailLength) {
            positions.remove(0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.setBackgroundColor(mouseTouchpadBg);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        int contentHeight1 = getHeight();

        Paint paint = new Paint();

        if (scrollMode) {
            paint.setColor(mouseTouchpadScrollActive);
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        }else {
            paint.setColor(mouseTouchpadScrollInactive);
        }

        if (isTouchEnabled) {

            if (scrollMode) {
                int trackWidth = 25;

                float thumbTop = mouseY - 50;
                float thumbBottom = contentHeight1  - (contentHeight1 - (mouseY + 100));

                if(thumbTop < 24) {
                    thumbTop = 24;
                    thumbBottom = thumbTop + 150;
                } else if (thumbBottom > contentHeight1 - 24) {
                    thumbTop = contentHeight1 - 24 - 150;
                    thumbBottom = contentHeight1 - 24;
                }

                RectF rectF = new RectF(
                        contentWidth - trackWidth,
                        thumbTop,
                        contentWidth + trackWidth,
                        thumbBottom);

                canvas.drawRoundRect(rectF, 26, 26, paint);
            } else {

                if (mTouchpadEnabled) {
                    if (mTouchpadTrail) {
                        for (int i = 0; i < positions.size(); i++) {
                            if ((stopMouseX != -1 && positions.get(i).x == (int) stopMouseX) &&
                                    (stopMouseY != -1 && positions.get(i).y == (int) stopMouseY)) {
                            } else {
                                float r = (float) i / (float) positions.size();
                                drawCircle(canvas, positions.get(i).x, positions.get(i).y, r, false);
                            }
                        }

                        if (stopMouseX == -1 && stopMouseY == -1) {
                            drawCircle(canvas, (int) mouseX, (int) mouseY, 0, true);
                        }

                        storeLastPosition((int) mouseX, (int) mouseY);
                    }
                    drawCircle(canvas, (int) mouseX, (int) mouseY, 0, true);
                }
            }
        }

        // Scrollbar
        paint.setAlpha(80);
        paint.setStyle(Paint.Style.STROKE);
        int trackWidth = 18;
        RectF rectF = new RectF(contentWidth - trackWidth, 24, contentWidth + trackWidth, contentHeight1 - 24);
        canvas.drawRoundRect(rectF, 18, 18, paint);
    }

    float startX = 0;
    private void touchStart(float x, float y) {
        stopMouseX = -1;
        stopMouseY = -1;
        isTouchEnabled = true;
        startX = x;
        mouseX = x;
        mouseY = y;

        positions.clear();

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        int contentHeight1 = getHeight();
        int contentWidth1 = getWidth();

        if (findPoint(contentWidth - 50, 24, contentWidth1, contentHeight1 - 24, x,y)) {
            scrollMode = true;
        }
    }

    boolean isTouchEnabled = false;
    float mouseX = 0;
    float mouseY = 0;

    float stopMouseX = -1;
    float stopMouseY = -1;

    private void touchMove(float x, float y) {
        mouseX = x;
        mouseY = y;
    }

    private void touchUp(float x, float y) {
        stopMouseX = x;
        stopMouseY = y;
        isTouchEnabled = false;
        scrollMode = false;
    }

    private GestureDetectorCompat mDetector;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        this.mDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onSingleClick(event, true);
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                onSingleClick(event, false);
                touchUp(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                if (scrollMode) {
                    onScroll(event, true);
                } else if (event.getPointerCount() >= 2 && mGestureRecognition) {
                    onScroll(event, false);
                } else {
                    onMove(event);
                }
                break;
        }
        return true;
    }

    private void send2dMouseData(final int x, final int y) {
        new Thread(() -> {
            if (NetworkManager.sConnectionAlive) {
                NetworkManager.mClientIOThread.send2dMouseData(x, y);
            }
        }).start();
    }

    private void sendScrollData(final int x, final int y) {
        new Thread(() -> {
            if (NetworkManager.sConnectionAlive) {
                NetworkManager.mClientIOThread.sendScrollData(x, y);
            }
        }).start();
    }

    private float mLastMoveX = Float.MAX_VALUE;
    private float mLastMoveY = Float.MAX_VALUE;

    private void onMove(MotionEvent event) {

        if (!mTouchpadEnabled) return;

        float curMoveX = event.getX();
        float curMoveY = event.getY();
        int distanceX = 0, distanceY = 0;

        if (mLastMoveX != Float.MAX_VALUE && mLastMoveY != Float.MAX_VALUE) {
            distanceX = (int) (curMoveX - mLastMoveX);
            distanceY = (int) (curMoveY - mLastMoveY);
        }

        send2dMouseData(distanceX, distanceY);
        mLastMoveX = curMoveX;
        mLastMoveY = curMoveY;
    }

    private void onScroll(MotionEvent event, boolean invert) {
        float curMoveX = event.getX();
        float curMoveY = event.getY();
        int distanceX = 0, distanceY = 0;

        if (mLastMoveX != Float.MAX_VALUE && mLastMoveY != Float.MAX_VALUE) {
            distanceX = (int) (curMoveX - mLastMoveX) * (invert ? -1 : 1);
            distanceY = (int) (curMoveY - mLastMoveY) * (invert ? -1 : 1);
        }

        sendScrollData(distanceX, distanceY);
        mLastMoveX = curMoveX;
        mLastMoveY = curMoveY;
    }

    private void onSingleClick(MotionEvent event, boolean down) {
        if (down) {
            mLastMoveX = event.getX();
            mLastMoveY = event.getY();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mTouchAnimation = false;
    }
}