package com.google.firebase.database.core.view;

import com.google.firebase.database.core.Context;
import com.google.firebase.database.core.EventTarget;
import com.google.firebase.database.logging.LogWrapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* compiled from: com.google.firebase:firebase-database@@19.3.0 */
/* loaded from: classes.dex */
public class EventRaiser {
    private final EventTarget eventTarget;
    private final LogWrapper logger;

    public EventRaiser(Context ctx) {
        this.eventTarget = ctx.getEventTarget();
        this.logger = ctx.getLogger("EventRaiser");
    }

    public void raiseEvents(List<? extends Event> events) {
        if (this.logger.logsDebug()) {
            LogWrapper logWrapper = this.logger;
            logWrapper.debug("Raising " + events.size() + " event(s)", new Object[0]);
        }
        final ArrayList<Event> eventsClone = new ArrayList<>(events);
        this.eventTarget.postEvent(new Runnable() { // from class: com.google.firebase.database.core.view.EventRaiser.1
            @Override // java.lang.Runnable
            public void run() {
                Iterator it = eventsClone.iterator();
                while (it.hasNext()) {
                    Event event = (Event) it.next();
                    if (EventRaiser.this.logger.logsDebug()) {
                        LogWrapper logWrapper2 = EventRaiser.this.logger;
                        logWrapper2.debug("Raising " + event.toString(), new Object[0]);
                    }
                    event.fire();
                }
            }
        });
    }
}
