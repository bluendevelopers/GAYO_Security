package bluen.homein.gayo_security.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class RoundFrameLayout extends FrameLayout {
    private final Path clipPath = new Path();
    private float radius;

    public RoundFrameLayout(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        // 16dp → px
        radius = getResources().getDisplayMetrics().density * 15;
        setWillNotDraw(false);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        clipPath.reset();

        float[] radii = {
                radius, radius,   // top-left
                0f,     0f,       // top-right
                0f,     0f,       // bottom-right
                radius, radius    // bottom-left
        };

        RectF rect = new RectF(0, 0, getWidth(), getHeight());
        clipPath.addRoundRect(rect, radii, Path.Direction.CW);
        canvas.clipPath(clipPath);

        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
    }
}