package mariusbinary.cursorr.controls.spinner;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import mariusbinary.cursorr.R;

public class LoadingSpinner extends View implements InvalidateListener {
  private LoaderView loaderView;

  public LoadingSpinner(Context context) {
    super(context);
    initialize(context, null, 0);
  }

  public LoadingSpinner(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize(context, attrs, 0);
  }

  public LoadingSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(context, attrs, defStyleAttr);
  }

  private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
    loaderView = new ClassicSpinner();
    loaderView.setColor(context.getResources().getColor(R.color.appHighlight));
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int measuredWidth = resolveSize(loaderView.getDesiredWidth(), widthMeasureSpec);
    final int measuredHeight = resolveSize(loaderView.getDesiredHeight(), heightMeasureSpec);

    setMeasuredDimension(measuredWidth, measuredHeight);
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    loaderView.setSize(getWidth(), getHeight());
    loaderView.initializeObjects();
    loaderView.setUpAnimation();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    loaderView.draw(canvas);
  }

  @Override public void reDraw() {
    invalidate();
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (loaderView != null && loaderView.isDetached()) {
      loaderView.setInvalidateListener(this);
    }
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (loaderView != null) {
      loaderView.onDetach();
    }
  }
}