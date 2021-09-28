package postpc2021.android.socialar;

import android.app.Application;

import java.io.Serializable;

public class FirebaseWrapper  extends Application implements Serializable {

    private FireBaseManager fireBaseManager;
    private static FirebaseWrapper instance = null;

    public FireBaseManager getFireBaseManager(){
        return this.fireBaseManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        fireBaseManager = new FireBaseManager(this);
    }

    public static FirebaseWrapper getInstance() {
        return instance;
    }
}
