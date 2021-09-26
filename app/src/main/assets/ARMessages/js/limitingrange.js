const MAX_SHOW_DISTANCE = 30; //  max distance set to around 100m of visible markers

/* Implementation of AR-Experience (aka "World"). */
var World = {

    /*
        User's latest known location, accessible via userLocation.latitude, userLocation.longitude,
         userLocation.altitude.
     */
    userLocation: null,

    /* Different POI-Marker assets. */
    markerDrawableIdle: null,
    markerDrawableSelected: null,
    markerDrawableDirectionIndicator: null,

    /* List of AR.GeoObjects that are currently shown in the scene / World. */
    markerList: [],

    /* the last selected marker. */
    currentMarker: null,

    locationUpdateCounter: 0,
    updatePlacemarkDistancesEveryXLocationUpdates: 10,

    /* Called to inject new POI data. */
    loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {

        /* Show radar. */
        PoiRadar.show();

        /* Empty list of visible markers. */
        World.markerList = [];

        World.markerDrawableDirectionIndicator = new AR.ImageResource("assets/indi.png", {
            onError: World.onError
        });

        /* Loop through POI-information and create an AR.GeoObject (=Marker) per POI. */
        for (var currentPlaceNr = 0; currentPlaceNr < poiData.length; currentPlaceNr++) {
            var singlePoi = {
                "id": poiData[currentPlaceNr].id,
                "latitude": parseFloat(poiData[currentPlaceNr].latitude),
                "longitude": parseFloat(poiData[currentPlaceNr].longitude),
                "altitude": parseFloat(poiData[currentPlaceNr].altitude),
            };

            World.markerList.push(new Marker(singlePoi));
        }
    

        /* Updates distance information of all placemarks. */
        World.updateDistanceToUserValues();

        World.renderToMaxDistance();

    },

    /*
        Sets/updates distances of all makers so they are available way faster than calling (time-consuming)
        distanceToUser() method all the time.
     */
    updateDistanceToUserValues: function updateDistanceToUserValuesFn() {
        for (var i = 0; i < World.markerList.length; i++) {
            World.markerList[i].distanceToUser = World.markerList[i].markerObject.locations[0].distanceToUser();
        }
    },

    /* Location updates, fired every time you call architectView.setLocation() in native environment. */
    locationChanged: function locationChangedFn(lat, lon, alt, acc) {

        /* Store user's current location in World.userLocation, so you always know where user is. */
        World.userLocation = {
            'latitude': lat,
            'longitude': lon,
            'altitude': alt,
            'accuracy': acc
        };


        /* Request data if not already present. */
        if (World.locationUpdateCounter === 0) {
            /*
                Update placemark distance information frequently, you max also update distances only every 10m with
                some more effort.
             */
            World.updateDistanceToUserValues();
        }

        /* Helper used to update placemark information every now and then (e.g. every 10 location upadtes fired). */
        World.locationUpdateCounter =
            (++World.locationUpdateCounter % World.updatePlacemarkDistancesEveryXLocationUpdates);
    },

    /*
        POIs usually have a name and sometimes a quite long description.
        Depending on your content type you may e.g. display a marker with its name and cropped description but
        allow the user to get more information after selecting it.
    */

    /* Fired when user pressed maker in cam. */
    onMarkerSelected: function onMarkerSelectedFn(marker) {
        World.closePanel();

        World.currentMarker = marker;

        /*
            In this sample a POI detail panel appears when pressing a cam-marker (the blue box with title &
            description), compare index.html in the sample's directory.
        */
        /* Update panel values. */
        document.getElementById("poiDetailTitle").innerHTML = marker.poiData.title;
        document.getElementById("poiDetailDescription").innerHTML = marker.poiData.description;

        /*
            It's ok for AR.Location subclass objects to return a distance of `undefined`. In case such a distance
            was calculated when all distances were queried in `updateDistanceToUserValues`, we recalculate this
            specific distance before we update the UI.
         */
        if (undefined === marker.distanceToUser) {
            marker.distanceToUser = marker.markerObject.locations[0].distanceToUser();
        }

        /*
            Distance and altitude are measured in meters by the SDK. You may convert them to miles / feet if
            required.
        */
        var distanceToUserValue = (marker.distanceToUser > 999) ?
            ((marker.distanceToUser / 1000).toFixed(2) + " km") :
            (Math.round(marker.distanceToUser) + " m");

        document.getElementById("poiDetailDistance").innerHTML = distanceToUserValue;

        /* Show panel. */
        document.getElementById("panelPoiDetail").style.visibility = "visible";
    },

    closePanel: function closePanel() {
        /* Hide panels. */
        document.getElementById("panelPoiDetail").style.visibility = "hidden";
        document.getElementById("panelRange").style.visibility = "hidden";

        if (World.currentMarker != null) {
            /* Deselect AR-marker when user exits detail screen div. */
            World.currentMarker.setDeselected(World.currentMarker);
            World.currentMarker = null;
        }
    },

    /* Screen was clicked but no geo-object was hit. */
    onScreenClick: function onScreenClickFn() {
        /* You may handle clicks on empty AR space too. */
        World.closePanel();
    },

    /* Returns distance in meters of placemark with maxdistance * 1.1. */
    getMaxDistance: function getMaxDistanceFn() {

        /* Sort places by distance so the first entry is the one with the maximum distance. */
        World.markerList.sort(World.sortByDistanceSortingDescending);

        /* Use distanceToUser to get max-distance. */
        var maxDistanceMeters = World.markerList[0].distanceToUser;

        /*
            Return maximum distance times some factor >1.0 so ther is some room left and small movements of user
            don't cause places far away to disappear.
         */
        return maxDistanceMeters * 1.1;
    },

    /* Updates values show in "range panel". */
    renderToMaxDistance: function renderToMaxDistance() {
        /* Max range relative to the maximum distance of all visible places. */
        var maxRangeMeters = Math.round(World.getMaxDistance() * (MAX_SHOW_DISTANCE / 100));

        /* Update culling distance, so only places within given range are rendered. */
        AR.context.scene.cullingDistance = Math.max(maxRangeMeters, 1);

        /* Update radar's maxDistance so radius of radar is updated too. */
        PoiRadar.setMaxDistance(Math.max(maxRangeMeters, 1));
    },

    /* Returns number of places with same or lower distance than given range. */
    getNumberOfVisiblePlacesInRange: function getNumberOfVisiblePlacesInRangeFn(maxRangeMeters) {

        /* Sort markers by distance. */
        World.markerList.sort(World.sortByDistanceSorting);

        /* Loop through list and stop once a placemark is out of range ( -> very basic implementation ). */
        for (var i = 0; i < World.markerList.length; i++) {
            if (World.markerList[i].distanceToUser > maxRangeMeters) {
                return i;
            }
        }

        /* In case no placemark is out of range -> all are visible. */
        return World.markerList.length;
    },

    handlePanelMovements: function handlePanelMovementsFn() {
        PoiRadar.updatePosition();
    },


    /* Helper to sort places by distance. */
    sortByDistanceSorting: function sortByDistanceSortingFn(a, b) {
        return a.distanceToUser - b.distanceToUser;
    },

    /* Helper to sort places by distance, descending. */
    sortByDistanceSortingDescending: function sortByDistanceSortingDescendingFn(a, b) {
        return b.distanceToUser - a.distanceToUser;
    },

    onError: function onErrorFn(error) {
        alert(error);
    }
};


/* Forward locationChanges to custom function. */
AR.context.onLocationChanged = World.locationChanged;

/* Forward clicks in empty area to World. */
AR.context.onScreenClick = World.onScreenClick;