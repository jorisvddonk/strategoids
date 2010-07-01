/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.factions;

/**
 * The Faction class is primarily a storage/identification class used to check
 * if one faction is an enemy of another. Perhaps this will one day also contain
 * resource amounts and ships.
 */
public class Faction {
    public String name;

    public byte fID;
    public double r, g, b;

    public Faction(String name, byte fID, double r, double g, double b) {
        this.name = name;
        this.fID = fID;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public boolean equals(Object obj) {
        Faction f = (Faction)obj;
        if (this.fID == (f.fID)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Faction: \"" + name + "\" fID:" + fID + " rgb:[" + r + " " + g + " " + b + "]";
    }





}
