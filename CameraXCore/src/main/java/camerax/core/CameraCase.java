package camerax.core;


import android.content.Context;
import android.content.res.Resources;
import android.database.Observable;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;

import java.util.ArrayList;
import java.util.List;

/**
 * by JFZ
 * 2025/4/27
 * desc：
 **/
public abstract class CameraCase<V extends ViewGroup> {
    private final String TAG = getClass().getSimpleName();

    private final Handler handler = new Handler(Looper.getMainLooper());

    protected Context mContext;

    protected Resources mResources;

    protected Resources.Theme mTheme;

    protected V mCameraView;

    protected int mWidth;

    protected int mHeight;

    protected PreviewView mPreviewView;

    protected CameraCaseView<V> mCameraCaseView;

    private final CameraCase.CaseObservable mObservable = new CaseObservable();

    private final List<CameraCase<V>> groupCase = new ArrayList<>();

    public <Case extends CameraCase<V>> void onCaseAttach(@NonNull Context context, @NonNull V cameraView, List<Case> caseList) {
        this.mContext = context;
        this.mResources = context.getResources();
        this.mTheme = context.getTheme();
        this.mCameraView = cameraView;
        if (caseList != null) {
            for (CameraCase<V> controllerCase : caseList) {
                if (controllerCase.getCaseId() != getCaseId()) {
                    //排除自己,添加其他
                    groupCase.add(controllerCase);
                }
            }
        }
    }


    /**
     * case初始化并且创建完成
     */
    public void onCaseCreated() {
    }

    /**
     * 所有case 创建完成
     */
    public void onAllCaseCreated() {

    }

    /**
     * case 的 ui 绘制
     *
     * @param canvas 画布
     */
    public void onDraw(Canvas canvas) {
    }

    /**
     * case 的touch事件
     *
     * @param event 事件
     * @return 是否拦截touch
     */
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }


    /**
     * 添加view
     */
    protected final void addView(View child, ViewGroup.LayoutParams layoutParams) {
        if (mCameraView != null) {
            mCameraView.addView(child, layoutParams);
        }
    }

    /**
     * 移除view
     */
    protected final void removeView(View child) {
        if (mCameraView != null) {
            mCameraView.removeView(child);
        }
    }

    protected final void invalidateCase() {
        if (mCameraCaseView != null) {
            mCameraCaseView.invalidate();
        }
    }

    protected final void postInvalidateCase() {
        if (mCameraCaseView != null) {
            mCameraCaseView.postInvalidate();
        }
    }


    /**
     * 发送事件
     *
     * @param data 数据
     */
    public final void postData(int action, @NonNull Object data) {
        mObservable.onChanged(action, data);
    }

    public final void postData(int action) {
        mObservable.onChanged(action, "");
    }


    /**
     * 注册监听
     */
    public final void registerCaseObserver(@NonNull CaseDataObserver observer) {
        mObservable.registerObserver(observer);
    }

    /**
     * 取消监听
     */
    public final void unregisterCaseObserver(@NonNull CaseDataObserver observer) {
        if (mObservable.hasObservers()) {
            mObservable.unregisterObserver(observer);
        }
    }

    static class CaseObservable extends Observable<CameraCase.CaseDataObserver> {

        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        public void onChanged(int action, @NonNull Object data) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged(action, data);
            }
        }
    }

    public static class CaseDataObserver {
        public void onChanged(int action, @NonNull Object data) {
            // Do nothing
        }
    }

    protected final void post(Runnable runnable) {
        handler.post(runnable);
    }

    protected final void postDelayed(Runnable runnable, long duration) {
        handler.postDelayed(runnable, duration);
    }

    /**
     * 销毁释放
     */
    protected void onDestroy() {
        this.mContext = null;
        this.mCameraView = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.groupCase.clear();
        handler.removeCallbacksAndMessages(null);
    }


    /**
     * @return 每一个case 都需要一个唯一的id
     */
    public abstract int getCaseId();


    protected final void shake() {
        if (mCameraCaseView != null) {
            mCameraCaseView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
    }

    /**
     * 根据caseId 获取某一个case
     * 每一个case是单独的，case 与case之间不存在关联性，
     * 如果想与某一个case进行交互可通过此函数进行实现交互
     *
     * @param caseId 创建的case id
     * @return UserCase
     */
    @Nullable
    protected final CameraCase<V> getCase(int caseId) {
        for (CameraCase<V> useCase : groupCase) {
            if (useCase.getCaseId() == caseId) {
                return useCase;
            }
        }
        return null;
    }


    /**
     * @return 已添加的case
     */
    @NonNull
    public final List<CameraCase<V>> getOtherGroupCase() {
        return groupCase;
    }


    protected final void log(String m) {
        Log.e(TAG, m);
    }

}
