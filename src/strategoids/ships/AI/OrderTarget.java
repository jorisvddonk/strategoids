/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.ships.AI;

import strategoids.ships.Ship;

/**
 * Order target.
 * either a coordinate or a ship.
 * Probably also in the future an asteroid or similar static non-ship targetable
 * object.
 */
public class OrderTarget {
    private double x;
    private double y;
    public Ship targetship;
    public boolean isship;

    public OrderTarget(double x, double y) {
        this.x = x;
        this.y = y;
        this.targetship = null;
        this.isship = false;
    }

    public OrderTarget(Ship targetship) {
        this.targetship = targetship;
        this.x = 0;
        this.y = 0;
        this.isship = true;
    }

    public void setCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
        this.targetship = null;
        this.isship = false;
    }

    public void setShip(Ship targetship) {
        this.targetship = targetship;
        this.x = 0;
        this.y = 0;
        this.isship = true;
    }

    public double getX() {
        if (!isship) {
            return x;
        } else {
            return targetship.getX();
        }
    }

    public double getY() {
        if (!isship) {
            return y;
        } else {
            return targetship.getY();
        }
    }

    public boolean isShip() {
        return (isship);
    }

    @Override
    public String toString() {
        if (isShip()) {
            return "Ship: " + targetship.toString() + " @ " + getX() + " - " + getY();
        } else {
            return "Position: " + x + " - " + y;
        }
    }


}
