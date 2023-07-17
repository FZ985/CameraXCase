package camerax.usecase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * author : JFZ
 * date : 2023/7/15 17:33
 * description : 将 UseCase 绑定到 CaseView中
 */
@SuppressLint("ViewConstructor")
final class CaseView extends View {
    private UseCase useCase;

    public CaseView(Context context, UseCase useCase) {
        super(context);
        this.useCase = useCase;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (useCase != null) {
            useCase.onDraw(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (useCase != null) {
            return useCase.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            if (useCase != null) {
                useCase.onDestroy();
            }
            useCase = null;
        } catch (Exception e) {
            Log.e("CaseView", "onDetachedFromWindow:" + e.getMessage());
        }
        super.onDetachedFromWindow();
    }
}
