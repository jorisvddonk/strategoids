/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids;

//import strategoids.drawing.DrawObjects;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import strategoids.drawing.Point;
import strategoids.effects.IEffect;
//import strategoids.deprecated.Gui;
import strategoids.misc.Int2D;
import strategoids.misc.ShipDistanceCombo;
import strategoids.objects.Asteroid;
import strategoids.ships.AI.Order;
import strategoids.ships.AI.OrderTarget;
import strategoids.ships.AI.Sector;
import strategoids.ships.Ship;
import strategoids.ships.bullets.IBullet;
import strategoids.ships.components.Builder;
import strategoids.ships.components.IComponent;

/**
 * The game logic. There's plenty of stuff here:
 * -ship movement
 * -ship AI
 * -bullet movement
 * -bullet hit detection
 * -ship radar detection
 * -keyboard events
 */
public class Logic implements Runnable {

    private Game game;
    private ArrayList<Integer> keys;
    //private DOLock dolock;
    private boolean gameover;
    private Main jg;
    private boolean restart = false;
    private boolean win = false;
    //private MyMouseListener mml;
    //private Gui gui;
    private boolean ailogic = true;
    private int aicount = 0;
    private static int RANGE_ATTACK_ENEMIES_WHEN_IDLE;
    private static int RANGE_CHECK_BULLETS_WITH_ENEMIES = 50;//4; // 2*2, since we're not doing sqrt
    private static int SIM_TICK_MULT = 1;
    private int tickmod;
    private static int TICKMOD_RETURNVALUE = 1;
    private static int TICKMOD_CHECKNEARBYENEMIES = 1;
    public String systemtimings;

    public void restart() {
        System.gc();
        //dolock.acquireW();
        Main.strategoids.lock.acquireW();
        win = false;
        game.init();
        gameover = false;
        restart = false;
        //dolock.releaseW();
        Main.strategoids.lock.releaseW();
        run();
    }

    public Logic(Game game, ArrayList<Integer> keys, boolean gameover) {
        this.game = game;
        this.keys = keys;
        //this.dolock = dolock;
        this.gameover = gameover;
        //this.mml = mml;
        //gui = new Gui();
        //gui.guielements.add(null);
    }

    @SuppressWarnings("static-access")
    public void run() {
        RANGE_ATTACK_ENEMIES_WHEN_IDLE = (int) (Main.strategoids.SECTORSIZE * Main.strategoids.SECTORSIZE) * 2;
        while (true) {
            while (!restart) {
                //int maxscore = 0;
                //Fortress.dolock.acquireW();
                Main.strategoids.lock.acquireW();
                Random random = new Random();
                long lastshot = 0;

                //dolock.releaseW();
                Main.strategoids.lock.releaseW();
                long tick_time = System.nanoTime();
                while (!gameover) {
                    try {
                        long sleeptime = (Main.strategoids.TICKTIME - ((System.nanoTime() - tick_time) / 100000));
                        if (sleeptime < 1) {
                            sleeptime = 1;
                        }
                        systemtimings = "tick sleep: " + sleeptime + " nano time taken: " + (System.nanoTime() - tick_time);
                        //System.out.println(systemtimings);

                        //Thread.sleep(sleeptime);
                        Thread.sleep(Main.strategoids.TICKTIME);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //dolock.acquireW();
                    tick_time = System.nanoTime();
                    Main.strategoids.lock.acquireW();
                    for (int loopcount = 0; loopcount < SIM_TICK_MULT; loopcount++) {

                        tickmod -= 1;
                        if (tickmod < 0) {
                            tickmod = TICKMOD_RETURNVALUE;
                        }

                        for (IEffect ieffect : game.effects) {
                            ieffect.movement_tick();
                            ieffect.process_tick();
                        }

                        for (Asteroid a : game.asteroids) {
                            a.movement_tick();
                        }


                        for (IBullet ieffect : game.bullets) {
                            ieffect.movement_tick();
                            ieffect.process_tick();
                            boolean done = false;
                            if (!ieffect.mustDestroy()) {
                                Int2D int2d = Main.strategoids.game.getSector_Int2D(ieffect.getX(), ieffect.getY());
                                for (int j = 0; j < 9; j++) { //walk through 9 sectors..
                                    if (!done) {
                                        switch (j) {
                                            case 1://right 1,0
                                                int2d.int1 += 1;
                                                break;
                                            case 2://left -1,0
                                                int2d.int1 -= 2;
                                                break;
                                            case 3://top 0,-1
                                                int2d.int1 += 1;
                                                int2d.int2 -= 1;
                                                break;
                                            case 4://bottom 0,1
                                                int2d.int2 += 2;
                                                break;
                                            case 5://bottomright 1,1
                                                int2d.int1 += 1;
                                                break;
                                            case 6://bottomleft -1,1
                                                int2d.int1 -= 2;
                                                break;
                                            case 7://topleft -1,-1
                                                int2d.int2 -= 2;
                                                break;
                                            case 8://topright 1,-1
                                                int2d.int1 += 2;
                                                break;
                                        }
                                        Sector sector = Main.strategoids.game.getSector_specific(int2d);
                                        if (sector != null) {
                                            //System.out.println("OMG");
                                            //System.out.println("Checking against " + sector.ships.size() + " ships");

                                            for (int i = 0; i < sector.ships.size(); i++) {
                                                //for (int i = 0; i < game.ships.size(); i++) {
                                                if (!done) {
                                                    if (Main.strategoids.factions_areEnemies(ieffect.getFaction(), sector.ships.get(i).getFaction())) {
                                                        if (ieffect.isInstaHit() || Point2D.distanceSq(ieffect.getX(), ieffect.getY(), sector.ships.get(i).getX(), sector.ships.get(i).getY()) < RANGE_CHECK_BULLETS_WITH_ENEMIES) {
                                                            if (ieffect.checkCollisionWith(sector.ships.get(i))) {
                                                                //System.err.println("Ship hit!");
                                                                sector.ships.get(i).subtractHitPoints(1);
                                                                ieffect.forceDestroy();
                                                                done = true;
                                                                //Check if it has to be destroyed..
                                                                if (sector.ships.get(i).getHitPoints() <= 0) { //remove it!
                                                                    sector.ships.get(i).orders_clear();
                                                                    for (Ship s : game.ships) {
                                                                        if (s.getOrders() != null) {
                                                                            for (int it = 0; it < s.getOrders().size(); it++) {
                                                                                if (s.getOrders().get(it).target.targetship == sector.ships.get(i)) {
                                                                                    s.getOrders().remove(it);
                                                                                    it--;
                                                                                }
                                                                            }
                                                                            if (s.getOrders().size() == 0) { //the ship that just got destroyed was the ship this dude was attacking..
                                                                                if (s.getReturnPoint() != null) { //he has a return point set! :D
                                                                                    s.order_attackMove(s.getReturnPoint().x, s.getReturnPoint().y);
                                                                                    s.setReturnPoint(null);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    Main.strategoids.game.destroyandremoveShip(sector.ships.get(i));

                                                                    i--;
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }



                        //---Get rid of ships with fewer than 0 HP left..
                        //long tick_time2 = System.nanoTime();
                        /*for (int i = 0; i < game.ships.size(); i++) {
                        if (game.ships.get(i).getHitPoints() < 0) { //remove it!
                        game.ships.get(i).orders_clear();
                        for (Ship s : game.ships) {
                        if (s.getOrders() != null) {
                        for (int it = 0; it < s.getOrders().size(); it++) {
                        if (s.getOrders().get(it).target.targetship == game.ships.get(i)) {
                        s.getOrders().remove(it);
                        it--;
                        }
                        }
                        if (s.getOrders().size() == 0) { //the ship that just got destroyed was the ship this dude was attacking..
                        if (s.getReturnPoint() != null) { //he has a return point set! :D
                        s.order_attackMove(s.getReturnPoint().x, s.getReturnPoint().y);
                        s.setReturnPoint(null);
                        }
                        }
                        }
                        }
                        game.ships.remove(i);
                        i--;
                        }
                        }*/
                        //System.out.println(game.ships.size() + "    nanotime: " + (System.nanoTime() - tick_time2));
                        //----

                        //if (ailogic) {
                        //    aicount++;
                        // }




                        for (int si = 0; si < game.ships.size(); si++) {
                            Ship Ship = game.ships.get(si);
                            if (ailogic && !Ship.equals(game.Player)) {
                                Ship.ai_tick();
                            }
                            Ship.movement_tick();
                            if (tickmod == si % TICKMOD_CHECKNEARBYENEMIES) {
                                if (Ship.getOrder() == null || Ship.getOrder().getOrderType() == Order.OrderType.attackmove) {
                                    boolean done = false;
                                    if (Ship.getOrder() == null) {
                                        if (Ship.getReturnPoint() != null) {
                                            Ship.order_move(Ship.getReturnPoint().x, Ship.getReturnPoint().y);
                                            Ship.setReturnPoint(null);
                                            done = true;
                                        }
                                    }

                                    Ship nearestenemyship = nearestShip(Ship);
                                    if (nearestenemyship != null) {
                                        if (Ship.getOrder() != null) { //so order = order.attackmove
                                            Order newOrder = new Order();
                                            newOrder.ordertype = Order.OrderType.attack;
                                            newOrder.target = new OrderTarget(nearestenemyship);
                                            Ship.getOrders().add(0, newOrder);
                                        } else {
                                            Ship.order_attackShip(nearestenemyship);
                                            if (Ship.getReturnPoint() == null) {
                                                Ship.setReturnPoint(new Point(Ship.getX(), Ship.getY()));
                                            }
                                        }
                                    }



                                    /*Int2D int2d = Fortress.game.getSector_Int2D(Ship.getX(), Ship.getY());
                                    for (int j = 0; j < 9; j++) { //walk through 9 sectors..
                                    if (!done) {
                                    switch (j) {
                                    case 1://right 1,0
                                    int2d.int1 += 1;
                                    break;
                                    case 2://left -1,0
                                    int2d.int1 -= 2;
                                    break;
                                    case 3://top 0,-1
                                    int2d.int1 += 1;
                                    int2d.int2 -= 1;
                                    break;
                                    case 4://bottom 0,1
                                    int2d.int2 += 2;
                                    break;
                                    case 5://bottomright 1,1
                                    int2d.int1 += 1;
                                    break;
                                    case 6://bottomleft -1,1
                                    int2d.int1 -= 2;
                                    break;
                                    case 7://topleft -1,-1
                                    int2d.int2 -= 2;
                                    break;
                                    case 8://topright 1,-1
                                    int2d.int1 += 2;
                                    break;
                                    }
                                    Sector sector = Fortress.game.getSector_specific(int2d);
                                    System.out.println("j: " + j + " Sector: " + sector);
                                    if (sector != null && sector.ships != null) {
                                    //System.out.println("OMG");

                                    for (int ist = 0; ist < sector.ships.size(); ist++) {
                                    if (!done && Fortress.ships_areEnemies(sector.ships.get(ist), Ship) && Point2D.distanceSq(sector.ships.get(ist).getX(), sector.ships.get(ist).getY(), Ship.getX(), Ship.getY()) < RANGE_ATTACK_ENEMIES_WHEN_IDLE) { //Automatically engage enemies that are nearby
                                    if (Ship.getOrder() != null) { //so order = order.attackmove
                                    Order newOrder = new Order();
                                    newOrder.ordertype = Order.OrderType.attack;
                                    newOrder.target = new OrderTarget(sector.ships.get(ist));
                                    Ship.getOrders().add(0, newOrder);
                                    } else {
                                    Ship.order_attackShip(sector.ships.get(ist));
                                    if (Ship.getReturnPoint() == null) {
                                    Ship.setReturnPoint(new Point(Ship.getX(), Ship.getY()));
                                    }
                                    }
                                    done = true;
                                    }
                                    }
                                    }
                                    }
                                    }*/
                                }
                            }
                        }

                        if (keys.contains(49)) { //1
                            for (int i = 0; i < 1; i++) {
                                //BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(Strategoids.FILE_SHAPEFILE)));
                                //Ship enemyship = ObjectSpawn.create_from_shapefile_Ship("fighter", 1, (Strategoids.FIELDSIZE * 10 - (Strategoids.random.nextDouble() * Strategoids.FIELDSIZE) * 20), (Strategoids.FIELDSIZE * 10 - (Strategoids.random.nextDouble() * Strategoids.FIELDSIZE) * 20), Strategoids.random.nextDouble() * 360, game.factions.get(1),input);
                                game.ships.add(Main.strategoids.game.create_ship_from_storage("fighter", (Main.strategoids.FIELDSIZE * 10 - (Main.strategoids.random.nextDouble() * Main.strategoids.FIELDSIZE) * 20), (Main.strategoids.FIELDSIZE * 10 - (Main.strategoids.random.nextDouble() * Main.strategoids.FIELDSIZE) * 20), Main.strategoids.random.nextDouble() * 360, game.factions.get(1)));
                            }
                        }

                        if (keys.contains(50)) { //2
                            for (int i = 0; i < 1; i++) {
                                //BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(Strategoids.FILE_SHAPEFILE)));
                                //game.ships.add(ObjectSpawn.create_from_shapefile_Ship("fighter", 1, (Strategoids.FIELDSIZE - (Strategoids.random.nextDouble() * Strategoids.FIELDSIZE) * 2), (Strategoids.FIELDSIZE - (Strategoids.random.nextDouble() * Strategoids.FIELDSIZE) * 2), Strategoids.random.nextDouble() * 360, game.factions.get(0), input));
                                game.ships.add(Main.strategoids.game.create_ship_from_storage("fighter", (Main.strategoids.FIELDSIZE - (Main.strategoids.random.nextDouble() * Main.strategoids.FIELDSIZE) * 2), (Main.strategoids.FIELDSIZE - (Main.strategoids.random.nextDouble() * Main.strategoids.FIELDSIZE) * 2), Main.strategoids.random.nextDouble() * 360, game.factions.get(0)));
                            }
                        }

                        if (keys.contains(51)) { //3
                            //BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(Strategoids.FILE_SHAPEFILE)));
                            //game.ships.add(ObjectSpawn.create_from_shapefile_Ship("frigate", 1, (Strategoids.FIELDSIZE - (Strategoids.random.nextDouble() * Strategoids.FIELDSIZE) * 2), (Strategoids.FIELDSIZE - (Strategoids.random.nextDouble() * Strategoids.FIELDSIZE) * 2), Strategoids.random.nextDouble() * 360, game.factions.get(0), input));
                            game.ships.add(Main.strategoids.game.create_ship_from_storage("frigate", (Main.strategoids.FIELDSIZE - (Main.strategoids.random.nextDouble() * Main.strategoids.FIELDSIZE) * 2), (Main.strategoids.FIELDSIZE - (Main.strategoids.random.nextDouble() * Main.strategoids.FIELDSIZE) * 2), Main.strategoids.random.nextDouble() * 360, game.factions.get(0)));
                        }
                        if (keys.contains(52)) { //4
                            //BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(Strategoids.FILE_SHAPEFILE)));
                            //game.ships.add(ObjectSpawn.create_from_shapefile_Ship("missile_cruiser", 1, (Strategoids.FIELDSIZE - (Strategoids.random.nextDouble() * Strategoids.FIELDSIZE) * 2), (Strategoids.FIELDSIZE - (Strategoids.random.nextDouble() * Strategoids.FIELDSIZE) * 2), Strategoids.random.nextDouble() * 360, game.factions.get(0), input));
                            game.ships.add(Main.strategoids.game.create_ship_from_storage("missile_cruiser", (Main.strategoids.FIELDSIZE - (Main.strategoids.random.nextDouble() * Main.strategoids.FIELDSIZE) * 2), (Main.strategoids.FIELDSIZE - (Main.strategoids.random.nextDouble() * Main.strategoids.FIELDSIZE) * 2), Main.strategoids.random.nextDouble() * 360, game.factions.get(0)));
                        }
                        if (keys.contains(53)) { //5
                            //BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(Strategoids.FILE_SHAPEFILE)));
                            //game.ships.add(ObjectSpawn.create_from_shapefile_Ship("gunship", 1, (Strategoids.FIELDSIZE - (Strategoids.random.nextDouble() * Strategoids.FIELDSIZE) * 2), (Strategoids.FIELDSIZE - (Strategoids.random.nextDouble() * Strategoids.FIELDSIZE) * 2), Strategoids.random.nextDouble() * 360, game.factions.get(0), input));
                            game.ships.add(Main.strategoids.game.create_ship_from_storage("gunship", (Main.strategoids.FIELDSIZE - (Main.strategoids.random.nextDouble() * Main.strategoids.FIELDSIZE) * 2), (Main.strategoids.FIELDSIZE - (Main.strategoids.random.nextDouble() * Main.strategoids.FIELDSIZE) * 2), Main.strategoids.random.nextDouble() * 360, game.factions.get(0)));
                        }

                        if (keys.contains(112)) { //F1
                            SIM_TICK_MULT = 1;
                        }

                        if (keys.contains(113)) { //F2
                            SIM_TICK_MULT = 2;
                        }

                        if (keys.contains(114)) { //F3
                            SIM_TICK_MULT = 5;
                        }

                        if (keys.contains(115)) { //F4
                            SIM_TICK_MULT = 10;
                        }

                        if (keys.contains(116)) { //F5
                            SIM_TICK_MULT = 20;
                        }

                        if (keys.contains(117)) { //F6
                            SIM_TICK_MULT = 50;
                        }

                        if (keys.contains(118)) { //F7
                            SIM_TICK_MULT = 500;
                        }

                        if (keys.contains(119)) { //F8
                            SIM_TICK_MULT = 2000;
                        }



                        if (keys.contains(37)) { //LEFT
                            Main.strategoids.translateGameDisplay(1, 0);
                            Main.strategoids.requireScreenDimensionsrecalc = true;
                        }
                        if (keys.contains(38)) { //UP
                            Main.strategoids.translateGameDisplay(0, -1);
                            Main.strategoids.requireScreenDimensionsrecalc = true;
                        }
                        if (keys.contains(39)) { //RIGHT
                            Main.strategoids.translateGameDisplay(-1, 0);
                            Main.strategoids.requireScreenDimensionsrecalc = true;
                        }
                        if (keys.contains(40)) { //DOWN
                            Main.strategoids.translateGameDisplay(0, 1);
                            Main.strategoids.requireScreenDimensionsrecalc = true;
                        }

                        if (keys.contains(32)) { //space; clear selected ship orders
                            for (Ship i : game.selectedships) {
                                i.orders_clear();
                            }
                        }

                        game.cleanupEffects();

                        if (keys.contains(81)) { //q
                            for (Ship s : game.selectedships) {
                                for (IComponent c : s.getComponents()) {
                                    if (c.getComponentType() == IComponent.ComponentType.builder) {
                                        Builder b = (Builder) c;
                                        b.add("fighter");
                                    }
                                }
                            }
                        }
                        if (keys.contains(87)) { //w
                            for (Ship s : game.selectedships) {
                                for (IComponent c : s.getComponents()) {
                                    if (c.getComponentType() == IComponent.ComponentType.builder) {
                                        Builder b = (Builder) c;
                                        b.add("frigate");
                                    }
                                }
                            }
                        }
                        if (keys.contains(69)) { //e
                            for (Ship s : game.selectedships) {
                                for (IComponent c : s.getComponents()) {
                                    if (c.getComponentType() == IComponent.ComponentType.builder) {
                                        Builder b = (Builder) c;
                                        b.add("missile_cruiser");
                                    }
                                }
                            }
                        }
                    }
                    //dolock.releaseW();
                    Main.strategoids.lock.releaseW();
                }
                if (gameover) {
                    System.err.println("GAME OVER");
                    try {
                        Thread.sleep(2500);
                    } catch (Exception e) {
                    }
                }
                restart = true;
            }
            restart();
        }

    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
        }
    }

    public static Ship nearestShip(Ship Ship) {
        boolean done = false;
        int int1 = Ship.getSector().sector_number.int1;
        int int2 = Ship.getSector().sector_number.int2;
        PriorityQueue<ShipDistanceCombo> ships = new PriorityQueue<ShipDistanceCombo>();
        for (int i = 0; i < 10; i++) {
            if (!done) {
                checkShipsNearbyShip(Ship, int1, int2, i, ships);
                if (ships.size() > 0) {
                    done = true;
                }
            }
        }
        if (ships.size() > 0) {
            return ships.peek().ship;
        } else {
            return null;
        }
    }

    public static void checkShipsNearbyShip(Ship Ship, int x, int y, int rangesector, PriorityQueue<ShipDistanceCombo> ships) {
        x -= rangesector;
        y -= rangesector;
        x -= 1;

        for (int i = 0; i < 4; i++) {
            for (int j = (rangesector * 2) - (i == 0 ? 0 : (i == 3 ? 2 : 1)); j >= 0; j--) {
                switch (i) {
                    case 0:
                        x += 1;
                        break;
                    case 1:
                        y += 1;
                        break;
                    case 2:
                        x -= 1;
                        break;
                    case 3:
                        y -= 1;
                        break;
                }
                checkShipsNearbyShip_specific_sector(x, y, Ship, ships);
            }
        }
    }

    public static void checkShipsNearbyShip_specific_sector(int x, int y, Ship Ship, PriorityQueue<ShipDistanceCombo> ships) {
        Sector sector = Main.strategoids.game.getSector_specific(x, y);
        //System.out.println("j: " + j + " Sector: " + sector);
        if (sector != null && sector.ships != null) {
            for (int ist = 0; ist < sector.ships.size(); ist++) {
                Ship l = sector.ships.get(ist);
                if (Main.strategoids.ships_areEnemies(l, Ship)) {
                    ships.add(new ShipDistanceCombo(l, Point2D.distanceSq(l.getX(), l.getY(), Ship.getX(), Ship.getY())));
                }
            }
        }
    }
}
