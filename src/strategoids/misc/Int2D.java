/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.misc;

import java.util.Comparator;

/**
 * 2D integer. Composed of 2 integers (int1 and int2).
 */
public class Int2D implements Comparator {

    public int int1;
    public int int2;

    public Int2D(int int1, int int2) {
        this.int1 = int1;
        this.int2 = int2;
    }

    @Override
    public String toString() {
        return "Int2D: [" + int1 + ":" + int2 + "]";
    }

    public int compare(Object o1, Object o2) {
        Int2D i1 = (Int2D) o1;
        Int2D i2 = (Int2D) o2;
        if (i1.int1 == i2.int1) {
            if (i1.int2 == i2.int2) {
                return 0;
            } else if (i1.int2 < i2.int2) {
                return -1;
            }
            return 1;
        } else if (i1.int1 < i2.int1) {
            return -1;
        } else if (i1.int1 > i2.int1) {
            return 1;
        }
        System.err.println("Int2D.Compare: THIS SHOULD NOT HAPPEN!!!!");
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        Int2D o = (Int2D)obj;
        if (this.int1 == o.int1 && this.int2 == o.int2) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return int2;
    }

    




}
