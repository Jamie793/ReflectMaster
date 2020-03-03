package formatfa.reflectmaster.j.widget;

import android.view.View;

import java.util.List;

public interface ViewLineClickListener {

    public void onClick(ViewLineView obj, List<View> views);

    public void onLongClick(ViewLineView obj);
}
