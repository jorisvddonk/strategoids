/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.gui;

import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import strategoids.Main;
import strategoids.drawing.DrawShape;
import strategoids.drawing.Point;
import strategoids.drawing.PointText;
import strategoids.ships.AI.Order;
import strategoids.ships.AI.OrderTarget;
import strategoids.ships.Ship;
import strategoids.ships.components.Builder;
import strategoids.ships.components.IComponent;

/**
 * The GUI Toolkit Container.
 * This will contain all logic behind the GUI, plus the rendering methods.
 */
public class GUIToolkitContainer extends MouseAdapter {

    public TextRenderer textrenderer;
    private ArrayList<GUIButton> GUIButtons;
    public ArrayList<GUILabel> Labels_Shipnames;
    public ArrayList<GUILabel> Labels_Selectedships;
    private ArrayList<GUILabel> Labels_GUI;
    private ArrayList<GUIElement> GUIElements;
    public Point topleft, bottomleft, topright, bottomright;
    private GLCanvas canvas;
    private double mousedragx, mousedragy;
    public boolean show_shiplabels = false;
    private ButtonAction foobuttonaction;
    private ButtonAction buttonbuttonaction;
    private Order draggingOrder;

    public GUIToolkitContainer(GLCanvas parent) {
        GUIElements = new ArrayList<GUIElement>();
        GUIElements.add(null);
        GUIElements.add(null);

        GUIButtons = new ArrayList<GUIButton>();
        textrenderer = new TextRenderer(new Font("Verdana", 0, 50), false);
        textrenderer.setSmoothing(true);
        textrenderer.setUseVertexArrays(true);

        Labels_Shipnames = new ArrayList<GUILabel>();
        Labels_Selectedships = new ArrayList<GUILabel>();
        Labels_GUI = new ArrayList<GUILabel>();

        this.canvas = parent;

        foobuttonaction = new ButtonAction() {

            public void doAction() {
                foobutton();
            }
        };
        buttonbuttonaction = new ButtonAction() {

            public void doAction() {
                buttonbutton();
            }
        };
    }

    public void recreateButtons() {
        GUIButtons.clear();
        GUIButtons.add(new GUIButton(topright.x, topright.y, topright.x - 30, topright.y - 20, "Return"));
        GUIButtons.add(new GUIButton(topleft.x, topleft.y, topleft.x + 50, topleft.y - 10, "Button!"));
        GUIButtons.get(0).setButtonAction(foobuttonaction);
        GUIButtons.get(1).setButtonAction(buttonbuttonaction);

        GUIElements.set(1, new GUILineSquare(bottomleft.x + 1, bottomleft.y + 1, bottomright.x - 1, bottomright.y + 20, 1, 1, 1, 1));
    }

    public void calcPoints(GL gl) {
        bottomleft = Main.strategoids.getGLCoordinates_GUI(0, 0);
        topleft = Main.strategoids.getGLCoordinates_GUI(0, canvas.getHeight());
        bottomright = Main.strategoids.getGLCoordinates_GUI(canvas.getWidth(), 0);
        topright = Main.strategoids.getGLCoordinates_GUI(canvas.getWidth(), canvas.getHeight());
        /*System.out.println("canvas width: " + canvas.getWidth() + "  canvas height: " + canvas.getHeight());
        System.out.println(topleft);
        System.out.println(topright);
        System.out.println(bottomleft);
        System.out.println(bottomright);*/

        recreateButtons();
    }

    public void Render(GL gl) {
        //DoubleBuffer bf = DoubleBuffer.allocate(16);
        //gl.glGetDoublev(gl.GL_CURRENT_COLOR, bf);
        //System.out.println("Color: " + bf.get(0) + " | " + bf.get(1) + " | " + bf.get(2));

        gl.glPushMatrix();
        gl.glLoadIdentity();


        ///////////////Begin text 3d rendering. no begin3drendering() needed anymore now
        textrenderer.begin3DRendering();
        Labels_Shipnames.clear();
        if (show_shiplabels) {
            for (Ship i : Main.strategoids.game.ships) {
                PointText pt = i.getPointText();
                Point p = Main.strategoids.gameToGUI(pt.x, pt.y);
                GUILabel tempg = new GUILabel(p.x, p.y, pt.text);
                if (i.getFaction() != null) {
                    if (Main.strategoids.game.selectedships.contains(i)) {
                        tempg.a = 0.8;
                        tempg.size = 0.07;
                    } else {
                        tempg.a = 0.5;
                        tempg.size = 0.07;
                    }
                    tempg.r = i.getFaction().r;
                    tempg.g = i.getFaction().g;
                    tempg.b = i.getFaction().b;
                } else {
                    tempg.r = 1;
                    tempg.g = 1;
                    tempg.b = 1;
                    tempg.a = 0.2;
                    tempg.size = 0.07;
                }
                Labels_Shipnames.add(tempg);
            }
        }

        Labels_Selectedships.clear();
        int selshipcount = 0;
        boolean builderselected = false;
        for (Ship i : Main.strategoids.game.selectedships) {
            GUILabel.Render(gl, textrenderer, i.getPointText().text, topleft.x, topleft.y - 12.5 + (selshipcount * -2.5), 0.05, 1, 1, 1, 0.7);
            selshipcount++;

            for (IComponent c : i.getComponents()) {
                if (c.getComponentType() == IComponent.ComponentType.builder) {
                    builderselected = true;
                }
            }
        }
        if (builderselected) {
            GUILabel.Render(gl, textrenderer, "BUILDER MENU", 0, 0, 0.05, 1, 1, 1, 1);
            GUILabel.Render(gl, textrenderer, "q - fighter", 0, -3, 0.05, 1, 1, 1, 1);
            GUILabel.Render(gl, textrenderer, "w - frigate", 0, -6, 0.05, 1, 1, 1, 1);
            GUILabel.Render(gl, textrenderer, "e - missile carrier", 0, -9, 0.05, 1, 1, 1, 1);
        }

        for (GUILabel guil : Labels_Shipnames) {
            guil.Render(gl, textrenderer);
        }
        for (GUILabel guil : Labels_Selectedships) {
            guil.Render(gl, textrenderer);
        }
        for (GUILabel guil : Labels_GUI) {
            guil.Render(gl, textrenderer);
        }

        GUILabel.Render(gl, textrenderer, "Resources: 1337", bottomleft.x + 2, bottomleft.y + 15, 0.1, 1, 1, 1, 1);
        if (Main.strategoids.game.selectedships.size() > 0) {
            GUILabel.Render(gl, textrenderer, "Selected ships: " + Main.strategoids.game.selectedships.size(), bottomleft.x + 2, bottomleft.y + 10, 0.1, 1, 1, 1, 1);
        }
        GUILabel.Render(gl, textrenderer, "Numships: " + Main.strategoids.game.ships.size(), bottomleft.x + 2, bottomleft.y + 5, 0.1, 1, 1, 1, 1);
        GUILabel.Render(gl, textrenderer, Main.strategoids.logic.systemtimings, bottomleft.x + 50, bottomleft.y + 5, 0.1, 1, 1, 1, 1);

        textrenderer.end3DRendering();
        ///////End text rendering; following stuff should textrenderer.begin3drendering()!!!!

        for (GUIButton guib : GUIButtons) { //contains an internal textrenderer.begin3d
            guib.Render(gl, textrenderer);
        }

        for (GUIElement guie : GUIElements) {
            if (guie != null) {
                guie.Render(gl, textrenderer);
            }
        }

        for (Ship i : Main.strategoids.game.selectedships) {
            Point ipt = Main.strategoids.gameToGUI(i.getX(), i.getY());
            DrawShape.drawCircle(ipt.x, ipt.y, 3, gl, 0, 1, 1);
        }

        if (Main.strategoids.mykeylistener.keys.contains(88)) {
            showOrders(gl, Main.strategoids.game.ships, false);
        } else if (Main.strategoids.mykeylistener.keys.contains(90)) {
            showOrders(gl, Main.strategoids.game.ships, true);
        } else {
            showOrders(gl, Main.strategoids.game.selectedships, true);
        }


        gl.glPopMatrix();
    }

    public void showOrders(GL gl, ArrayList<Ship> ships, boolean onlymine) {
        for (Ship i : ships) {
            if (Main.strategoids.ship_isMine(i) || !onlymine) {
                if (i.getOrders() != null) {
                    gl.glLoadIdentity();
                    gl.glBegin(GL.GL_LINE_STRIP);


                    Order firstPersistentOrder = null;
                    Point ipt = Main.strategoids.gameToGUI(i.getX(), i.getY());
                    Point iopt = null;
                    double r = 1, g = 1, b = 1, a = 0.4;
                    boolean colorize;

                    gl.glVertex2d(ipt.x, ipt.y);
                    for (Order iO : i.getOrders()) {
                        if (iO.persistent) {
                            if (firstPersistentOrder == null) {
                                firstPersistentOrder = iO;
                            }
                        }
                        colorize = false;
                        iopt = Main.strategoids.gameToGUI(iO.getTargetPosition().x, iO.getTargetPosition().y);
                        if (r != (iO.ordertype == Order.OrderType.attack || iO.ordertype == Order.OrderType.attackmove ? 1 : 0)) {
                            r = (iO.ordertype == Order.OrderType.attack || iO.ordertype == Order.OrderType.attackmove ? 1 : 0);
                            colorize = true;
                        }
                        if (g != (iO.ordertype == Order.OrderType.attack ? 0 : 1)) {
                            g = (iO.ordertype == Order.OrderType.attack ? 0 : 1);
                            colorize = true;
                        }
                        if (b != (iO.ordertype == Order.OrderType.attackmove ? 1 : 0)) {
                            b = (iO.ordertype == Order.OrderType.attackmove ? 1 : 0);
                            colorize = true;
                        }
                        if (colorize) {
                            gl.glColor4d(r, g, b, a);
                        }
                        gl.glVertex2d(iopt.x, iopt.y);
                    }
                    gl.glEnd();
                    /*if (firstPersistentOrder != null) { //TODO, BUG
                    ipt = Fortress.gameToGUI(firstPersistentOrder.getTargetPosition().x, firstPersistentOrder.getTargetPosition().y);
                    System.out.println(ipt + "         " + iopt);
                    gl.glLoadIdentity();
                    Line.draw(gl, ipt.x, ipt.y, iopt.x, iopt.y, (firstPersistentOrder.ordertype == Order.OrderType.attack || firstPersistentOrder.ordertype == Order.OrderType.attackmove ? 1 : 0), (firstPersistentOrder.ordertype == Order.OrderType.attack ? 0 : 1), (firstPersistentOrder.ordertype == Order.OrderType.attackmove ? 1 : 0), 0.4); //Order line for last persistent item (in case a loop exists)
                    }*/

                    /*for (Order iO : i.getOrders()) {
                    if (iO != null) {
                    if (iO.persistent) {
                    if (firstPersistentOrder == null) {
                    firstPersistentOrder = iO;
                    }
                    //Point persisPt = Fortress.gameToGUI(iO.getTargetPosition().x, iO.getTargetPosition().y);
                    //DrawShape.drawCircle(persisPt.x, persisPt.y, (double) 20 / (double) Fortress.fieldsize, gl, 1, 1, 0); //Persistent order circles
                    }
                    Point iopt = Fortress.gameToGUI(iO.getTargetPosition().x, iO.getTargetPosition().y);

                    //if (iO.target.isShip()) {
                    //    DrawShape.drawCircle(iopt.x, iopt.y, (double) 25 / (double) Fortress.fieldsize, gl, 0.2, 0.2, 1); //Persistent order circles
                    //}
                    //Line.draw(gl, ipt.x, ipt.y, iopt.x, iopt.y, (iO.ordertype == Order.OrderType.attack || iO.ordertype == Order.OrderType.attackmove ? 1 : 0), (iO.ordertype == Order.OrderType.attack ? 0 : 1), (iO.ordertype == Order.OrderType.attackmove ? 1 : 0), 0.4); //Order lines
                    ipt = iopt;
                    }
                    }
                    if (firstPersistentOrder != null) {
                    Point iopt = Fortress.gameToGUI(firstPersistentOrder.getTargetPosition().x, firstPersistentOrder.getTargetPosition().y);
                    Line.draw(gl, ipt.x, ipt.y, iopt.x, iopt.y, (firstPersistentOrder.ordertype == Order.OrderType.attack || firstPersistentOrder.ordertype == Order.OrderType.attackmove ? 1 : 0), (firstPersistentOrder.ordertype == Order.OrderType.attack ? 0 : 1), (firstPersistentOrder.ordertype == Order.OrderType.attackmove ? 1 : 0), 0.4); //Order line for last persistent item (in case a loop exists)
                    }*/
                }
            }
        }
        for (Order iO : Main.strategoids.game.orders) {
            Point iopt = Main.strategoids.gameToGUI(iO.getTargetPosition().x, iO.getTargetPosition().y);
            if (iO.persistent) {
                DrawShape.drawCircle(iopt.x, iopt.y, (double) 20 / (double) Main.strategoids.FIELDSIZE, gl, 1, 1, 0); //Persistent order circles
            }
            if (iO.target.isShip()) {
                DrawShape.drawCircle(iopt.x, iopt.y, (double) 25 / (double) Main.strategoids.FIELDSIZE, gl, 0.2, 0.2, 1); //Persistent order circles
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Main.strategoids.lock.acquireR();
        super.mouseClicked(e);
        double mousex = e.getX();
        double mousey = Main.strategoids.height - e.getY();
        Point guipt = Main.strategoids.getGLCoordinates_GUI(e.getX(), mousey);
        Point gamept = Main.strategoids.getGLCoordinates_Game(e.getX(), mousey);
        mousedragx = gamept.x;
        mousedragy = gamept.y;
        draggingOrder = null;
        //System.out.println("Mouse clicked! " + e.getX() + " | " + e.getY() + "  [" + x + " | " + y + "]");
        if (e.getButton() == e.BUTTON1) {
            leftMouse_Pressed(e, mousex, mousey, guipt, gamept);
        } else if (e.getButton() == e.BUTTON3) { //right mouse button. *NOT* middle (yeah.. I know.. D:)
            rightMouse_Pressed(e, mousex, mousey, gamept);
        }
        Main.strategoids.lock.releaseR();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Main.strategoids.lock.acquireR();
        double mousey = Main.strategoids.height - e.getY();
        Point gamept = Main.strategoids.getGLCoordinates_Game(e.getX(), mousey);

        if (draggingOrder == null) {
            Point gametogui1 = Main.strategoids.gameToGUI(gamept.x, gamept.y);
            Point gametogui2 = Main.strategoids.gameToGUI(mousedragx, mousedragy);

            if (Math.abs(gamept.x - mousedragx) > 1 && Math.abs(gamept.y - mousedragy) > 1) { //Display selection box
                GUIElements.set(0, new GUILineSquare(gametogui1.x, gametogui1.y, gametogui2.x, gametogui2.y));
            } else {
                GUIElements.set(0, null);
            }
        } else { //dragging an order
            draggingOrder.target.setCoordinates(gamept.x, gamept.y);
        }
        Main.strategoids.lock.releaseR();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Main.strategoids.lock.acquireR();
        GUIElements.set(0, null);
        double mousex = e.getX();
        double mousey = Main.strategoids.height - e.getY();
        Point gamept = Main.strategoids.getGLCoordinates_Game(e.getX(), mousey);

        if (e.getButton() == e.BUTTON1) {
            leftMouse_Released(e, mousex, mousey, gamept);
        }
        Main.strategoids.lock.releaseR();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Main.strategoids.FIELDSIZE += e.getWheelRotation();
        Main.strategoids.screenDimensionsRecalc_Mousepoint = new java.awt.Point(e.getX(), Main.strategoids.height - e.getY());
        Main.strategoids.FIELDSIZE = Math.max(1, Main.strategoids.FIELDSIZE);
        //System.out.println("Fieldsize is now: "+Fortress.fieldsize);
        Main.strategoids.requireScreenDimensionsrecalc = true;
    }

    public void leftMouse_Pressed(MouseEvent e, double mousex, double mousey, Point guipt, Point gamept) {
        for (GUIButton guib : GUIButtons) {
            if (guipt.x < Math.max(guib.xend, guib.xorg) && guipt.x > Math.min(guib.xend, guib.xorg) && guipt.y < Math.max(guib.yend, guib.yorg) && guipt.y > Math.min(guib.yend, guib.yorg)) {
                System.out.println("Button pressed: " + guib);
                guib.performAction();
            }
        }
        if (draggingOrder == null) {
            for (Order o : Main.strategoids.game.orders) { //only global orders can be edited as of now... TODO change that + make sure you can't drag orders that aren't being displayed
                if (!o.target.isShip()) {
                    if (Main.strategoids.isScreenPointCloseToGamePoint(mousex, mousey, o.getTargetPosition().x, o.getTargetPosition().y)) { //Clickin' an order!
                        draggingOrder = o;
                    }
                }
            }
        }
    }

    public void rightMouse_Pressed(MouseEvent e, double mousex, double mousey, Point gamept) {
        Order order = new Order();
        order.target = null;
        boolean addedtoglobals = false;

        for (Ship i : Main.strategoids.game.ships) { //check if clicking on a ship
            if (Main.strategoids.isScreenPointCloseToGamePoint(mousex, mousey, i.getX(), i.getY())) {
                order.target = new OrderTarget(i);
            }
        }
        for (Ship i : Main.strategoids.game.selectedships) { //TODO: check if we can put this FOR loop somewhere else
            boolean orderMade = false;

            if (e.isShiftDown()) { //want to queue up; check if we're trying to craft a patrol TODO: CHECK IF THERE IS BUG HERE (POSSIBLEBUG) regarding adding order to queue
                for (int ordi = 0; ordi < i.getOrders().size(); ordi++) {
                    Order o = i.getOrders().get(ordi);
                    if (o.getOrderType() != Order.OrderType.attack) {
                        if (Main.strategoids.isScreenPointCloseToGamePoint(mousex, mousey, o.getTargetPosition().x, o.getTargetPosition().y)) { //clicking on existing order (move/attackmove)
                            for (int ordi2 = ordi; ordi2 < i.getOrders().size(); ordi2++) { //set all orders following that one to PERSISTENT
                                i.getOrders().get(ordi2).persistent = true;
                                //System.out.println("Persistency: " + i + " - " + ordi2);
                                orderMade = true;
                            }
                        }
                    }
                }
            }

            if (!orderMade) {
                if (!e.isShiftDown()) { //Not trying to queue; get rid of existing orders
                    i.orders_clear();
                }
                if (order.target == null || !order.target.isShip()) { //target is a point in space
                    if (e.isControlDown()) {
                        order.ordertype = Order.OrderType.attackmove;
                        order.target = new OrderTarget(gamept.x, gamept.y);
                    } else {
                        order.ordertype = Order.OrderType.move;
                        order.target = new OrderTarget(gamept.x, gamept.y);
                    }
                } else { //ship targeted
                    System.out.println("Ship targeted: " + order.target.targetship.getPointText().text + " " + order.target.getX() + " " + order.target.getY());
                    //System.out.println("Enemies? " + Fortress.ships_areEnemies(i, ot.targetship));
                    if (e.isControlDown()) { //control down
                        if (Main.strategoids.ships_areEnemies(i, order.target.targetship)) {
                            order.ordertype = Order.OrderType.move; //follow enemy ship
                        } else {
                            order.ordertype = Order.OrderType.attack; //attack friendly ship (won't work since friendly fire is off. DESIGNBUG, TODO)
                        }
                    } else { //control not down
                        if (Main.strategoids.ships_areEnemies(i, order.target.targetship)) {
                            order.ordertype = Order.OrderType.attack; //attack enemy ship
                        } else {
                            order.ordertype = Order.OrderType.move; //folow friendly ship
                        }
                    }
                }
            }

            if (order.target != null && order.ordertype != null) { //check if we've crafted a succesful order
                if (!addedtoglobals) {
                    Main.strategoids.game.orders.add(order);
                    addedtoglobals = true;
                }
                i.orderAdd(order);
            }
        }
    }

    public void leftMouse_Released(MouseEvent e, double mousex, double mousey, Point gamept) {
        if (draggingOrder != null) { //dragging an order around
            draggingOrder = null;
        } else { //not dragging an order
            if (Math.abs(gamept.x - mousedragx) > 1 && Math.abs(gamept.y - mousedragy) > 1) { //We have a selection box!
                if (!e.isShiftDown() && !e.isControlDown()) { //Clear if neither shift or control are down..
                    Main.strategoids.game.selectedships.clear();
                }
                synchronized (Main.strategoids.game.ships) {
                    for (Ship i : Main.strategoids.game.ships) {
                        if (Main.strategoids.ship_isMine(i)) {
                            if (Math.min(gamept.x, mousedragx) < i.getX() && Math.max(gamept.x, mousedragx) > i.getX()) {
                                if (Math.min(gamept.y, mousedragy) < i.getY() && Math.max(gamept.y, mousedragy) > i.getY()) {
                                    if (!e.isControlDown()) {
                                        if (!Main.strategoids.game.selectedships.contains(i)) {
                                            Main.strategoids.game.selectedships.add(i);
                                            Main.strategoids.game.checkTutorial_b("unit_selected");
                                            //System.out.println("Selected: " + i);

                                        }
                                    } else {
                                        if (Main.strategoids.game.selectedships.contains(i)) {
                                            Main.strategoids.game.selectedships.remove(i);
                                        }
                                    }
                                    //System.out.println("Ship dragged: " + i);
                                }
                            }
                        }
                    }
                }
            } else { //No selection box; single click
                if (!e.isShiftDown() && !e.isControlDown()) { //Clear if neither shift or control are down..
                    Main.strategoids.game.selectedships.clear();
                }
                for (Ship i : Main.strategoids.game.ships) {
                    if (Main.strategoids.ship_isMine(i)) {
                        if (Main.strategoids.isScreenPointCloseToGamePoint(mousex, mousey, i.getX(), i.getY())) {
                            if (!e.isControlDown()) {
                                if (!Main.strategoids.game.selectedships.contains(i)) {
                                    Main.strategoids.game.selectedships.add(i);
                                }
                            } else {
                                if (Main.strategoids.game.selectedships.contains(i)) {
                                    Main.strategoids.game.selectedships.remove(i);
                                }
                            }
                        }
                        //System.out.println("Ship clicked: " + i);
                        }
                }
            }
        }
    }

    public void foobutton() {
        System.out.println("Number of sectors: " + Main.strategoids.game.sectormap.size());
        /*for (Int2D i : Fortress.game.sectormap.keySet()) {
        System.out.println(Fortress.game.sectormap.get(i));
        }*/
        System.out.println(Main.strategoids.game.sectormap.values());
    }

    public void buttonbutton() {
        show_shiplabels = !show_shiplabels;
    }
}
