package pl.droidsonroids.gif;

import android.os.SystemClock;
import java.util.concurrent.TimeUnit;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class RenderTask extends SafeRunnable {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RenderTask(GifDrawable gifDrawable) {
        super(gifDrawable);
    }

    @Override // pl.droidsonroids.gif.SafeRunnable
    public void doWork() {
        long invalidationDelay = this.mGifDrawable.mNativeInfoHandle.renderFrame(this.mGifDrawable.mBuffer);
        if (invalidationDelay >= 0) {
            this.mGifDrawable.mNextFrameRenderTime = SystemClock.uptimeMillis() + invalidationDelay;
            if (this.mGifDrawable.isVisible() && this.mGifDrawable.mIsRunning && !this.mGifDrawable.mIsRenderingTriggeredOnDraw) {
                this.mGifDrawable.mExecutor.remove(this);
                this.mGifDrawable.mRenderTaskSchedule = this.mGifDrawable.mExecutor.schedule(this, invalidationDelay, TimeUnit.MILLISECONDS);
            }
            if (!this.mGifDrawable.mListeners.isEmpty() && this.mGifDrawable.getCurrentFrameIndex() == this.mGifDrawable.mNativeInfoHandle.getNumberOfFrames() - 1) {
                this.mGifDrawable.mInvalidationHandler.sendEmptyMessageAtTime(this.mGifDrawable.getCurrentLoop(), this.mGifDrawable.mNextFrameRenderTime);
            }
        } else {
            this.mGifDrawable.mNextFrameRenderTime = Long.MIN_VALUE;
            this.mGifDrawable.mIsRunning = false;
        }
        if (this.mGifDrawable.isVisible() && !this.mGifDrawable.mInvalidationHandler.hasMessages(-1)) {
            this.mGifDrawable.mInvalidationHandler.sendEmptyMessageAtTime(-1, 0L);
        }
    }
}
