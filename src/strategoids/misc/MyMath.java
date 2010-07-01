/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.misc;

/**
 * Lots and lots of random math functions, woo!
 */
public class MyMath {
    public static double FixDeg(double angle) {
        double ang = angle;
        while (ang < 0) {ang += 360;}
        while (ang > 360) {ang -= 360;}
        return ang;
        //return ((angle > 360? angle - 360 : angle) < 0? angle + 360 : angle);
    }

    public static int ClosestDegDirection(double angle, double toangle) {
        double distL = (toangle - angle);
        if (distL < 0) {
            distL = (360 + distL);
        }
        double distR = (toangle - angle);
        if (distR > 0) {
            distL = (-360 + distL);
        }
        distR *= -1;
        int ret = 0;
        if (distL < distR) {
            ret = -1;
        }
        if (distL > distR) {
            ret = 1;
        }
        return ret;
    }

    public static double ClosestDegAngle(double angle, double toangle) {
        double distL = (toangle - angle);
        if (distL < 0) {
            distL = (360 + distL);
        }
        double distR = (toangle - angle);
        if (distR > 0) {
            distL = (-360 + distL);
        }
        distR *= -1;
        double ret = 0;
        if (distL < distR) {
            ret = distL;
        }
        if (distL > distR) {
            ret = distR;
        }
        return ret;
    }

    public static int FurthestDegDirection(double angle, double toangle) {
        double distL = (toangle - angle);
        if (distL < 0) {
            distL = (360 + distL);
        }
        double distR = (toangle - angle);
        if (distR > 0) {
            distL = (-360 + distL);
        }
        distR *= -1;
        int ret = 0;
        if (distL < distR) {
            ret = 1;
        }
        if (distL > distR) {
            ret = -1;
        }
        return ret;
    }

    public static double FurthestDegAngle(double angle, double toangle) {
        double distL = (toangle - angle);
        if (distL < 0) {
            distL = (360 + distL);
        }
        double distR = (toangle - angle);
        if (distR > 0) {
            distL = (-360 + distL);
        }
        distR *= -1;
        double ret = 0;
        if (distL < distR) {
            ret = distR;
        }
        if (distL > distR) {
            ret = distL;
        }
        return ret;
    }

}
