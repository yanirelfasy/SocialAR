package postpc2021.android.socialar.arComponents;

import android.content.Intent;
import android.os.Bundle;

import com.wikitude.architect.ArchitectView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ArchitectViewExtensionActivity extends ARView {
    private PoiDataFromApplicationModelExtension poiExtension;
    private  GeoExtension geoExtension;
    private final Map<String, ArchitectViewExtension> extensions = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        poiExtension = new PoiDataFromApplicationModelExtension(this, this.getArchitectView());
        geoExtension = new GeoExtension(this, this.getArchitectView());
        geoExtension.setLocationListenerExtension(poiExtension);
        geoExtension.onCreate();
        poiExtension.onCreate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        geoExtension.onPostCreate();
        poiExtension.onPostCreate();

    }

    @Override
    protected void onResume() {
        super.onResume();
        geoExtension.onResume();
        poiExtension.onResume();
    }

    @Override
    protected void onPause() {
        geoExtension.onPause();
        poiExtension.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        geoExtension.onDestroy();
        poiExtension.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArchitectView.getPermissionManager().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
