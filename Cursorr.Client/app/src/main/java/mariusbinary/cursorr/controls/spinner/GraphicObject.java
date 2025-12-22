package mariusbinary.cursorr.controls.spinner;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class GraphicObject {
  protected Paint paint;

  public GraphicObject() {
    paint = new Paint();
    paint.setAntiAlias(true);
  }

  public void setColor(int color) {
    paint.setColor(color);
  }

  public void setAlpha(int alpha) {
    paint.setAlpha(alpha);
  }

  public void setWidth(float width) {
    paint.setStrokeWidth(width);
  }

  public abstract void draw(Canvas canvas);
}