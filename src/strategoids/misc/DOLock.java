/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.misc;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A lock (mutex) used for locking things so that there aren't any
 * concurrentmodification exceptions.
 */
public class DOLock {

    Mutex m = new Mutex();

    public DOLock() {
         m = new Mutex();
    }

    public void acquireW() {
        try {
            m.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DOLock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void releaseW() {
        m.release();
    }

    public void acquireR() {
        try {
            m.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DOLock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void releaseR() {
        m.release();
    }
}
