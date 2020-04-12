package helpers;

import android.util.Log;

public class Coordinates {

    private double d = 20.0; //20km radius

    public double getMinimumLatitude(double latitude) {
        latitude = Math.toRadians(latitude);
        double r = d / 6371;
        return Math.toDegrees(latitude - r);
    }

    public double getMaximumLatitude(double latitude) {
        latitude = Math.toRadians(latitude);
        double r = d / 6371;
        return Math.toDegrees(latitude + r);
    }

    public double getMinimumLongitude(double latitude, double longitude) {
        latitude = Math.toRadians(latitude);
        longitude = Math.toRadians(longitude);
        double r = d / 6371;
        double latT = Math.asin(Math.sin(latitude) / Math.cos(r));

        double dlon = Math.asin(Math.sin(r) / Math.cos(latitude));
        return Math.toDegrees(longitude - dlon);
    }

    public double getMaximumLongitude(double latitude, double longitude) {
        latitude = Math.toRadians(latitude);
        longitude = Math.toRadians(longitude);
        double r = d / 6371;
        double latT = Math.asin(Math.sin(latitude) / Math.cos(r));

        double dlon = Math.asin(Math.sin(r) / Math.cos(latitude));
        return Math.toDegrees(longitude + dlon);
    }

    public boolean checkRange(double ownLat, double ownLon, double latitude, double longitude) {

        if (latitude >= getMinimumLatitude(ownLat)
                && latitude <= getMaximumLatitude(ownLat)
                && longitude >= getMinimumLongitude(ownLat, ownLon)
                && longitude <= getMaximumLongitude(ownLat, ownLon)) {
            return true;
        }

        return false;
    }

}
