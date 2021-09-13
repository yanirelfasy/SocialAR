package postpc2021.android.socialar.arComponents;

import android.app.Activity;

import com.wikitude.architect.ArchitectView;

public abstract class ArchitectViewExtension {

    protected final Activity activity;
    protected final ArchitectView architectView;

    public ArchitectViewExtension(Activity activity, ArchitectView architectView) {
        this.activity = activity;
        this.architectView = architectView;
    }

    public void onCreate(){}

    public void onPostCreate(){}

    public void onResume(){}

    public void onPause(){}

    public void onDestroy(){}
}
