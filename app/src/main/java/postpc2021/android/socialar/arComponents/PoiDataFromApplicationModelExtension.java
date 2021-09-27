package postpc2021.android.socialar.arComponents;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Message;

import com.wikitude.architect.ArchitectView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import postpc2021.android.socialar.FireBaseManager;
import postpc2021.android.socialar.MessageData;

public class PoiDataFromApplicationModelExtension extends ArchitectViewExtension implements LocationListener {

    private FireBaseManager fireBaseManager = new FireBaseManager();


    public PoiDataFromApplicationModelExtension(Activity activity, ArchitectView architectView) {
        super(activity, architectView);
    }

    /** If the POIs were already generated and sent to JavaScript. */
    private boolean injectedPois = false;

    /**
     * When the first location was received the POIs are generated and sent to the JavaScript code,
     * by using architectView.callJavascript.
     */
    @Override
    public void onLocationChanged(Location location) {
        if (!injectedPois) {
            final JSONArray jsonArray = generatePoiInformation(location);
            architectView.callJavascript("World.loadPoisFromJsonData(" + jsonArray.toString() + ")"); // Triggers the loadPoisFromJsonData function
            injectedPois = true; // don't load pois again
        }
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    private JSONArray generatePoiInformation(final Location userLocation) {

        final JSONArray pois = new JSONArray();
//        MessageData data = new MessageData("WvjyS9iIN0c9Amn2KWFELcsMIPu1", 31.895120285543527, 35.00513086114198, "Testing Upload", new ArrayList(), new ArrayList());
//        this.fireBaseManager.uploadMessage(data, true);
        List<MessageData> poisData = this.fireBaseManager.getMessagesByPoIandRange(userLocation.getLatitude(), userLocation.getLongitude(), 300);


        // ensure these attributes are also used in JavaScript when extracting POI data
        final String ATTR_ID = "id";
        final String ATTR_NAME = "name";
        final String ATTR_DESCRIPTION = "description";
        final String ATTR_LATITUDE = "latitude";
        final String ATTR_LONGITUDE = "longitude";
        final String ATTR_ALTITUDE = "altitude";

        double[][] POI_COORDINATES_DEMO = {{31.895120285543527, 35.00513086114198}, {31.896131623870144, 35.00318713275174}, {31.893764052276612, 35.00793490652589}, {31.89639793721652, 35.008242712353805}};
        String[] DATA = {"0001", "0002", "0003", "0004"};
        for (int i = 0; i < POI_COORDINATES_DEMO.length; i++) {
            final HashMap<String, String> poiInformation = new HashMap<String, String>();
            poiInformation.put(ATTR_ID, DATA[i]);
            double[] poiLocationLatLon = {POI_COORDINATES_DEMO[i][0], POI_COORDINATES_DEMO[i][1]};
            poiInformation.put(ATTR_LATITUDE, String.valueOf(poiLocationLatLon[0]));
            poiInformation.put(ATTR_LONGITUDE, String.valueOf(poiLocationLatLon[1]));
            final float UNKNOWN_ALTITUDE = -32768f;  // equals "AR.CONST.UNKNOWN_ALTITUDE" in JavaScript (compare AR.GeoLocation specification)
            // Use "AR.CONST.UNKNOWN_ALTITUDE" to tell ARchitect that altitude of places should be on user level. Be aware to handle altitude properly in locationManager in case you use valid POI altitude value (e.g. pass altitude only if GPS accuracy is <7m).
            poiInformation.put(ATTR_ALTITUDE, String.valueOf(UNKNOWN_ALTITUDE));
            pois.put(new JSONObject(poiInformation));
        }
        return pois;
    }



}
