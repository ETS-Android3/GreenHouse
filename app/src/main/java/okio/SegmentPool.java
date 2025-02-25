package okio;

import android.support.v4.media.session.PlaybackStateCompat;
import javax.annotation.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class SegmentPool {
    static final long MAX_SIZE = 65536;
    static long byteCount;
    @Nullable
    static Segment next;

    private SegmentPool() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Segment take() {
        synchronized (SegmentPool.class) {
            if (next == null) {
                return new Segment();
            }
            Segment result = next;
            next = result.next;
            result.next = null;
            byteCount -= PlaybackStateCompat.ACTION_PLAY_FROM_URI;
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void recycle(Segment segment) {
        if (segment.next != null || segment.prev != null) {
            throw new IllegalArgumentException();
        } else if (!segment.shared) {
            synchronized (SegmentPool.class) {
                if (byteCount + PlaybackStateCompat.ACTION_PLAY_FROM_URI <= 65536) {
                    byteCount += PlaybackStateCompat.ACTION_PLAY_FROM_URI;
                    segment.next = next;
                    segment.limit = 0;
                    segment.pos = 0;
                    next = segment;
                }
            }
        }
    }
}
