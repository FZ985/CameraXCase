package camerax.usecase;

import android.content.Context;
import android.content.res.Resources;
import android.database.Observable;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.view.PreviewView;

import java.util.ArrayList;
import java.util.List;

/**
 * author : JFZ
 * date : 2023/7/15 17:36
 * description : UseCase功能开发类
 */
public abstract class UseCase {

    private final String TAG = getClass().getSimpleName();

    Context context;

    Resources resources;

    Resources.Theme theme;

    int width;

    int height;

    CameraXView cameraView;

    PreviewView previewView;

    CaseView caseView;

    Camera camera;

    List<UseCase> groupCase = new ArrayList<>();

    private final UseCase.UseCaseObservable mObservable = new UseCaseObservable();

    /**
     * case 的创建
     *
     * @param context     上下文
     * @param cameraView  自定义的组件
     * @param previewView 相机预览view
     * @param caseView    与当前case绑定的caseView
     * @param camera      相机对象
     * @param width       当前case宽度
     * @param height      当前case高度
     * @param cases       其他绑定的case
     */
    protected final void onCreate(Context context,
                                  CameraXView cameraView, PreviewView previewView, CaseView caseView, Camera camera,
                                  int width, int height, List<UseCase> cases) {
        this.context = context.getApplicationContext();
        this.resources = context.getResources();
        this.theme = context.getTheme();
        this.width = width;
        this.height = height;
        this.cameraView = cameraView;
        this.previewView = previewView;
        this.caseView = caseView;
        this.camera = camera;
        if (cases != null) {
            for (UseCase usecase : cases) {
                if (usecase.getCaseId() != getCaseId()) {
                    //排除自己,添加其他
                    groupCase.add(usecase);
                }
            }
        }
        onCaseCreated();
    }

    /**
     * case初始化并且创建完成
     */
    public void onCaseCreated() {
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
     * 发送事件
     *
     * @param data 数据
     */
    protected final void postData(Object data) {
        mObservable.onChanged(data);
    }

    protected final Context getContext() {
        return context;
    }

    protected final Resources getResources() {
        return resources;
    }

    protected final Resources.Theme getTheme() {
        return theme;
    }

    protected final int getWidth() {
        return width;
    }

    protected final int getHeight() {
        return height;
    }

    protected final CameraXView getCameraView() {
        return cameraView;
    }

    protected final PreviewView getPreviewView() {
        return previewView;
    }

    protected final CaseView getCaseView() {
        return caseView;
    }

    protected final Camera getCamera() {
        return camera;
    }

    /**
     * 刷新case
     */
    protected final void invalidate() {
        if (caseView != null) {
            caseView.invalidate();
        }
    }

    /**
     * 刷新case
     */
    protected final void postInvalidate() {
        if (caseView != null) {
            caseView.postInvalidate();
        }
    }

    /**
     * 添加view
     */
    protected final void addView(View child, RelativeLayout.LayoutParams layoutParams) {
        if (cameraView != null) {
            cameraView.addView(child, layoutParams);
        }
    }

    /**
     * 移除view
     */
    protected final void removeView(View child) {
        if (cameraView != null) {
            cameraView.removeView(child);
        }
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

    static class UseCaseObservable extends Observable<UseCase.CaseDataObserver> {

        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        public void onChanged(Object data) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged(data);
            }
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
    protected final UseCase getCase(int caseId) {
        for (UseCase useCase : groupCase) {
            if (useCase.getCaseId() == caseId) {
                return useCase;
            }
        }
        return null;
    }

    /**
     * @return 每一个case 都需要一个唯一的id
     */
    public abstract int getCaseId();

    //谷歌的usecase
    public androidx.camera.core.UseCase getCameraUseCase() {
        return null;
    }

    public static class CaseDataObserver {
        public void onChanged(Object data) {
            // Do nothing
        }
    }

    /**
     * 销毁释放
     */
    protected void onDestroy() {
        this.context = null;
        this.cameraView = null;
        this.caseView = null;
        this.width = 0;
        this.height = 0;
    }

    protected final void log(String m) {
        Log.e(TAG, m);
    }

}
