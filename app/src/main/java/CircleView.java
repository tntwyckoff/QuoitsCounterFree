import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.view.View;

public class CircleView extends OvalShape {

    private ShapeDrawable mDrawable;

    public CircleView() {
        super();
    }

    @Override
    protected void onResize(float width, float height) {
        super.onResize(width, height);
    }

    public void draw(Canvas canvas, Paint paint) {

        boolean useHeight = canvas.getHeight() >= canvas.getWidth() ? false : true;

        int max = useHeight ? canvas.getHeight() : canvas.getWidth();

        int buffer = useHeight ? canvas.getWidth() - max : canvas.getHeight() - max;

        float x = useHeight ? buffer/2 : 0;
        float y = useHeight ? 0 : buffer/2;

        canvas.translate(x, y);

        super.draw(canvas, paint);

//
//        mDrawable.setBounds();
//        mDrawable.draw(canvas);
    }
}
