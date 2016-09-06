package edu.dmitry.geoserver.LocationGroup;

public class GeoPoint
{
    private int lon;
    private int lat;

    public GeoPoint(int ln, int lt) {
        lon = ln;
        lat = lt;
    }

    public GeoPoint(double ln, double lt) {
        lon = GeoUtls.FromDegree(ln);
        lat = GeoUtls.FromDegree(lt);
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }


}