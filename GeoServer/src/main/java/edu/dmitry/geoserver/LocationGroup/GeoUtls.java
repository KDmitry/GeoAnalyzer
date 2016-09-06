package edu.dmitry.geoserver.LocationGroup;

public class GeoUtls
{
    public static Cos100 cos100 = new Cos100();

    public static int Cos100(int degree) {
        return cos100.Guess(degree);
    }

    public static int Sqr(int i) {
        return i*i;
    }

    public static double Sqr(double d) {
        return d*d;
    }

    public static int Sin100(int degree) {
        return cos100.Guess(degree - (1 << (Consts.GPWIDTH - 2)));
    }

    public static int int_sqrt(int input) {
        if (input < 0)
            return 0;

        int nv, v = input >> 1, c = 0;

        if (v == 0)
            return input;
        do
        {
            nv = (v + input / v) >> 1;
            if (Math.abs(v - nv) <= 1)
                return nv;
            v = nv;
        }
        while (c++ < 25);

        return nv;
    }

    public static double ToDegree(int degree) {
        return ((double)degree) * (360.0 / (1 << Consts.GPWIDTH));
    }

    public static int FromDegree(double degree) {
        return (int)(degree / (360.0 / (1 << Consts.GPWIDTH)));
    }

    public static int intDistance(GeoPoint gp1, GeoPoint gp2) {
        int iLonScale100 = Cos100((gp1.getLat() + gp2.getLat()) / 2);
        int x = Math.abs((int)(iLonScale100 * (long)(gp1.getLon() - gp2.getLon()) / 100));
        int y = Math.abs(gp1.getLat() - gp2.getLat());
        int c = 1;

        while (x > (1 << 15) || y > (1 << 15))
        {
            x /= 4;
            y /= 4;
            c *= 4;
        }

        int res = int_sqrt(Sqr(x) + Sqr(y));
        res *= 40000000 / (1 << 10);
        res /= 1 << (Consts.GPWIDTH - 10);
        res *= c;

        return res;
    }

    public static int IntAzimuth(GeoPoint llPointFrom, GeoPoint llPointTo) {
        int iLonScale100 = Cos100((llPointFrom.getLat() + llPointTo.getLat()) / 2);

        double dLat = ToDegree(llPointTo.getLat()) - ToDegree(llPointFrom.getLat());
        double dLon = ToDegree(llPointTo.getLon()) - ToDegree(llPointFrom.getLon());
        dLon = dLon * iLonScale100 / 100;
        double res = 0;

        if (dLat == 0.0) {
            if (dLon > 0)
                res = 90;
            else if (dLon< 0)
                res = -90;
        } else {
            res = Math.atan(dLon / dLat) / Consts.pi* 180.0;
            if (dLat< 0)
                res += 180.0;
        }

        if (res< 0)
            res += 360;

        return (int)res;
    }

    public static double AzimuthRadian(GeoPoint llPointFrom, GeoPoint llPointTo) {
        double lat1 = ToDegree(llPointFrom.getLat()) * Consts.pi / 180;
        double lat2 = ToDegree(llPointTo.getLat()) * Consts.pi / 180;
        double deltaLon = ToDegree(llPointFrom.getLon() - llPointTo.getLon()) * Consts.pi / 180;

        double y = Math.sin(deltaLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLon);
        return Math.atan2(y, x);
    }

    public static double CrossTrackDoubleDistance(GeoPoint gp1, GeoPoint gp2, GeoPoint gp) {
        double d13 = DoubleDistance(gp1, gp);
        double az13 = AzimuthRadian(gp1, gp);
        double az12 = AzimuthRadian(gp1, gp2);
        return Math.abs(Math.asin(Math.sin(d13 / Consts.EARTH_RAD) * Math.sin(az13 - az12)) * Consts.EARTH_RAD);
    }

    public static double DoubleDistance(GeoPoint llPoint1, GeoPoint llPoint2) {
        double lat1 = ToDegree(llPoint1.getLat()) * Consts.pi / 180;
        double lat2 = ToDegree(llPoint2.getLat()) * Consts.pi / 180;
        double lng1 = ToDegree(llPoint1.getLon()) * Consts.pi / 180;
        double lng2 = ToDegree(llPoint2.getLon()) * Consts.pi / 180;

        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);
        double delta = lng2 - lng1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double p1 = Sqr((cl2 * sdelta));
        double p2 = Sqr((cl1 * sl2) - (sl1 * cl2 * cdelta));
        double p3 = Math.sqrt(p1 + p2);
        double p4 = sl1 * sl2;
        double p5 = cl1 * cl2 * cdelta;
        double p6 = p4 + p5;
        double p7 = p3 / p6;
        double anglerad = Math.atan(p7);

        if (anglerad< 0)
            anglerad += Consts.pi;

        return anglerad * Consts.EARTH_RAD;
    }

    public static double Mymod(double x, double y) { return x - y * Math.floor(x / y); }

    public static double DoubleDistanceToSegment(GeoPoint gp, GeoPoint gp1, GeoPoint gp2) {
        double d13 = DoubleDistance(gp1, gp);
        double az13 = AzimuthRadian(gp1, gp);
        double az12 = AzimuthRadian(gp1, gp2);
        double a312 = Mymod(az13 - az12, 2*Consts.pi);
        if ((a312 >= 0.5*Consts.pi) && (a312 <= 1.5*Consts.pi))
            return d13;
        double az23 = AzimuthRadian(gp2, gp);
        double a321 = Mymod(az23 - (Consts.pi + az12), 2*Consts.pi);
        if ((a321 >= 0.5*Consts.pi) && (a321 <= 1.5*Consts.pi))
            return DoubleDistance(gp2, gp);
        return Math.abs(Math.asin(Math.sin(d13/Consts.EARTH_RAD)*Math.sin(a312))*Consts.EARTH_RAD);
    }

    public static void main(String[] args) {
        GeoPoint geoPoint1 = new GeoPoint(55.835871, 37.520867);
        GeoPoint geoPoint2 = new GeoPoint(55.833798, 37.520438);

        int distance = GeoUtls.intDistance(geoPoint1, geoPoint2);
        if (distance > 0) {

        }
    }
}