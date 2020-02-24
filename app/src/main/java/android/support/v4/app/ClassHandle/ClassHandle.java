package android.support.v4.app.ClassHandle;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class ClassHandle {
    Context context;
    Object obj;

    public ClassHandle(Context context, Object obj) {
        this.context = context;
        this.obj = obj;
    }

    public abstract void handle(LinearLayout layout);
}
