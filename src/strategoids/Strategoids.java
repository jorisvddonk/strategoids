/**
 *   Strategoids Real-Time-Strategy game/engine
 *   Copyright (C) 2010 - Strategoids Development Team
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see http://www.gnu.org/licenses/
 */
package strategoids;

import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.io.PrintStream;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.glu.GLU;
import strategoids.drawing.Point;
import strategoids.factions.Faction;
import strategoids.gui.GUIToolkitContainer;
import strategoids.misc.DOLock;
import strategoids.ships.Ship;
import strategoids.misc.MyKeyListener;
import strategoids.misc.MyOutputstream;

public class Strategoids {

    public int FIELDSIZE = 50;
    public final int TICKTIME = 16;
    public final int SHIP_WANTEDDISTANCE = 2;
    public static double SECTORSIZE = 100;
    public Game game;
    public Logic logic;
    public DOLock lock;
    public boolean gameover;
    public int width, height;
    public String errstring;
    public MyOutputstream myout;
    public Random random;
    public GUIToolkitContainer gui;
    public DoubleBuffer guiProjMatrix;
    public DoubleBuffer mainProjMatrix;
    public DoubleBuffer modelMatrix;
    public IntBuffer viewPort;
    public boolean requireScreenDimensionsrecalc = false;
    public java.awt.Point screenDimensionsRecalc_Mousepoint;
    public double gametranslate_X = 0, gametranslate_Y = 0;
    public MyKeyListener mykeylistener;
    public static String FILE_SHAPEFILE, FILE_SITUATIONFILE;
    public ClassLoader classloader;
    public GLU glu;
    public TextRenderer tr;

    public Strategoids(GLCanvas canvas) {
        classloader = Thread.currentThread().getContextClassLoader();
        FILE_SHAPEFILE = "/shape.txt";
        FILE_SITUATIONFILE = "/situation.txt";
        guiProjMatrix = DoubleBuffer.allocate(16);
        mainProjMatrix = DoubleBuffer.allocate(16);
        viewPort = IntBuffer.allocate(4);
        modelMatrix = DoubleBuffer.allocate(16);

        random = new Random();
        errstring = "";
        myout = new MyOutputstream(errstring);
        System.setErr(new PrintStream(myout));
        gameover = false;
        lock = new DOLock();
        gui = new GUIToolkitContainer(canvas);
        canvas.addMouseListener(gui);
        canvas.addMouseMotionListener(gui);
        canvas.addMouseWheelListener(gui);

        mykeylistener = new MyKeyListener();
        canvas.addKeyListener(mykeylistener);

        game = new Game();
        ExecutorService ex = Executors.newFixedThreadPool(1);
        logic = new Logic(game, mykeylistener.keys, gameover);
        ex.execute(logic);
    }

    public void init(GL gl) {
        lock.acquireW();
        System.err.println("INIT GL IS: " + gl.getClass().getName());
        game.create_Storage(gl);
        game.init();
        lock.releaseW();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height, GL gl) {
        glu = new GLU();
        boolean dimensions_changed = false;
        if (this.width != width) {
            if (this.height != height) {
                dimensions_changed = true;
            }
        }
        this.width = width;
        this.height = height;



        if (height <= 0) { // avoid a divide by zero error!
            height = 1;
        }
        float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        double fieldsize_modded = Math.pow(FIELDSIZE, 2);
        glu.gluOrtho2D(-((double) width / (double) height * fieldsize_modded), ((double) width / (double) height * fieldsize_modded), -fieldsize_modded, fieldsize_modded);
        gl.glTranslated(gametranslate_X, gametranslate_Y, 0);
        if (screenDimensionsRecalc_Mousepoint != null) {
            Point temppt = getGLCoordinates_Game(screenDimensionsRecalc_Mousepoint.x, screenDimensionsRecalc_Mousepoint.y);
            gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, mainProjMatrix);
            Point temppt2 = getGLCoordinates_Game(screenDimensionsRecalc_Mousepoint.x, screenDimensionsRecalc_Mousepoint.y);
            gl.glTranslated(temppt2.x - temppt.x, temppt2.y - temppt.y, 0);
            gametranslate_X += temppt2.x - temppt.x;
            gametranslate_Y += temppt2.y - temppt.y;
            screenDimensionsRecalc_Mousepoint = null;
        }
        gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, mainProjMatrix);
        gl.glGetIntegerv(gl.GL_VIEWPORT, viewPort);

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        //gl.glScaled(1, -1, 1);
        gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelMatrix);
        //gl.glScaled(1, -1, 1);

        if (dimensions_changed) {
            GUIDrawing_start(gl, true);
            gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, guiProjMatrix);
            gui.calcPoints(gl);
            GUIDrawing_end(gl);
        }
    }

    public void display(GL gl) {
        lock.acquireR();
        game.draw(gl);
        lock.releaseR();
        if (tr == null) {
            tr = new TextRenderer(new Font("Verdana", 0, 10));
        }
        GUIDrawing_start(gl, false);
        lock.acquireR();
        gui.Render(gl);
        GUIDrawing_end(gl);
        lock.releaseR();
    }

    public void GUIDrawing_start(GL gl, boolean initdisplaymode) {
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        if (initdisplaymode) {
            GUIDrawing_initDisplaymode(gl);
        } else {
            GUIDrawing_setDisplaymode(gl);
        }
        gl.glMatrixMode(gl.GL_MODELVIEW);
    }

    public void GUIDrawing_end(GL gl) {
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(gl.GL_MODELVIEW);
    }

    public void GUIDrawing_setDisplaymode(GL gl) {
        gl.glLoadMatrixd(guiProjMatrix);
    }

    public void GUIDrawing_initDisplaymode(GL gl) {
        glu = new GLU();
        glu.gluOrtho2D(-((double) width / (double) height) * 100, ((double) width / (double) height) * 100, -100, 100);
        //glu.gluOrtho2D(-((double)width/(double)height*fieldsize), ((double)width/(double)height*fieldsize), -fieldsize, fieldsize);
        //glu.gluOrtho2D(-((double)width/(double)height*(fieldsize/10)), ((double)width/(double)height*(fieldsize/10)), -(fieldsize/10), (fieldsize/10));
        //glu.gluOrtho2D(-10, 10, -10, 10);
        //glu.gluOrtho2D(-((double)width/(double)height*fieldsize), ((double)width/(double)height*fieldsize), -fieldsize, fieldsize);
    }

    /**
     * Transforms screen coordinates into GL coordinates of the game
     * @param x - x screen coordinate
     * @param y - y screen coordinate
     * @return Point object depicting the game's GL coordinates
     */
    public Point getGLCoordinates_Game(double x, double y) {
        return Point.getCoordinatesOfScreenPoint(x, y, mainProjMatrix, modelMatrix, viewPort);
    }

    public Point getScreenCoordinates_Game(double x, double y) {
        return Point.getCoordinatesOfGLPoint(x, y, mainProjMatrix, modelMatrix, viewPort);
    }

    /**
     * Transforms screen coordinates into GL coordinates of the GUI
     * @param x - x screen coordinate
     * @param y - y screen coordinate
     * @return Point object depicting the GUI's GL coordinates
     */
    public Point getGLCoordinates_GUI(double x, double y, boolean inverse_y) {
        Point temppt = Point.getCoordinatesOfScreenPoint(x, y, guiProjMatrix, modelMatrix, viewPort);
        if (inverse_y) {
            temppt.y = -temppt.y;
        }
        return temppt;
    }

    public Point getGLCoordinates_GUI(double x, double y) {
        return getGLCoordinates_GUI(x, y, false);
    }

    public Point getScreenCoordinates_GUI(double x, double y) {
        return Point.getCoordinatesOfGLPoint(x, y, guiProjMatrix, modelMatrix, viewPort);
    }

    public Point gameToGUI(double x, double y) {
        Point temppt = getScreenCoordinates_Game(x, y);
        return getGLCoordinates_GUI(temppt.x, temppt.y);
    }

    public void translateGameDisplay(double x, double y) {
        gametranslate_X += (x * FIELDSIZE) * 0.1;
        gametranslate_Y += (y * FIELDSIZE) * 0.1;
    }

    public boolean isScreenPointCloseToGamePoint(double scrx, double scry, double gamx, double gamy) {
        Point temppt = getScreenCoordinates_Game(gamx, gamy);
        return isPointCloseToPoint(scrx, scry, temppt.x, temppt.y);
    }

    public boolean isPointCloseToPoint(double p1x, double p1y, double p2x, double p2y) {
        if (Point2D.distance(p1x, p1y, p2x, p2y) < 10) {
            return true;
        } else {
            return false;
        }
    }

    public boolean ship_isEnemy(Ship ship) {
        return factions_areEnemies(ship.getFaction(), game.factions.get(0));
    }

    public boolean ships_areEnemies(Ship ship1, Ship ship2) {
        return factions_areEnemies(ship1.getFaction(), ship2.getFaction());
    }

    public boolean factions_areEnemies(Faction f1, Faction f2) {
        if (f1 != null && f2 != null) {
            return (!f1.equals(f2)); //might want to change this some day when factions can be allies..
        } else {
            return false;
        }
    }

    public boolean factions_isMine(Faction f1) {
        if (f1 != null) {
            return (f1.equals(game.factions.get(0))); //might want to change this some day when factions can be allies..
        } else {
            return false;
        }
    }

    public boolean ship_isMine(Ship ship) {
        Faction f = ship.getFaction();
        return factions_isMine(f);
    }

    public static ArrayList<String> string_matches(String string, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher myMatcher = pattern.matcher(string);
        ArrayList<String> retlist = new ArrayList<String>();
        int m = 0;
        while (myMatcher.find()) {
            retlist.add(myMatcher.group());
        }
        return retlist;
    }
}
