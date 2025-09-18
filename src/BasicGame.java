import java.util.Random;

public class BasicGame {

    static final int GAME_LOOP_NUMBER = 1_000;
    static final int HEIGHT = 40;
    static final int WIDTH = 40;
    static final Random RANDOM = new Random(100L);

    public static void main(String[] args) throws InterruptedException {

        Level level;
        int counter = 0;
        do {
            level = new Level(RANDOM, HEIGHT, WIDTH);
            level.addRandomWalls();
        } while (!level.isPassable());

        System.out.println("Pálya inicializálások száma: " + counter);
        level.isPassable(true);

        Coordinates playerCoordinates = level.getRandomCoordinates();
        MovingEntity player = new MovingEntity("0", playerCoordinates, level.getFarthestCorner(playerCoordinates), Directon.RIGHT, level);

        Coordinates enemyCoordinates = level.getRandomCoordinatesAtDistance(player.getCoordinates(), 10);
        MovingEntity enemy = new MovingEntity("-", enemyCoordinates, level.getFarthestCorner(enemyCoordinates), Directon.LEFT, level);

        Powerup powerup = new Powerup("*", level.getRandomCoordinates(), level);

        GameResult gameResult = GameResult.TIE;

        for (int iteracionNumber = 1; iteracionNumber <= GAME_LOOP_NUMBER; iteracionNumber++) {
            //Player irányváltás
            if (powerup.isActive()) {
                player.setDirection(level.getShortestPath(player.getDirection(), player.getCoordinates(), enemy.getCoordinates()));
            } else {
                if (powerup.isPresentOnLevel()) {
                    player.setDirection(level.getShortestPath(player.getDirection(), player.getCoordinates(), powerup.getCoordinates()));
                } else {
                    if (iteracionNumber % 100 == 0) {
                        player.setEscapeCoordinates(level.getFarthestCorner(player.getCoordinates()));
                    }
                    player.setDirection(level.getShortestPath(player.getDirection(), player.getCoordinates(), player.getEscapeCoordinates()));
                }
            }
            player.update();

            //Ellenfél irányváltás
            if (powerup.isActive()) {
                if (iteracionNumber % 100 == 0) {
                    enemy.setEscapeCoordinates(level.getFarthestCorner(enemy.getCoordinates()));
                }
                enemy.setDirection(level.getShortestPath(enemy.getDirection(), enemy.getCoordinates(), enemy.getEscapeCoordinates()));
            } else {
                enemy.setDirection(level.getShortestPath(enemy.getDirection(), enemy.getCoordinates(), player.getCoordinates()));
            }
            if (iteracionNumber % 2 == 0) {
                enemy.update();
            }

            //powerup frissitése
            if (powerup.update()) {
                player.setEscapeCoordinates(level.getFarthestCorner(player.getCoordinates()));
            }

            //power up interaction the player
            if (powerup.isPresentOnLevel() && player.getCoordinates().isSameAs(powerup.getCoordinates())) {
                powerup.activate();
                powerup.hideOnLevel();
                powerup.resetPresenceCounter();
                enemy.setEscapeCoordinates(level.getFarthestCorner(enemy.getCoordinates()));
            }

            //Pálya és játékos kirajzolása
            draw(level, player, enemy, powerup);
            //várakozás
            addSomeDelay(iteracionNumber, 200L);

            //kiléptetés ha elérték egymást
            if (player.getCoordinates().isSameAs(enemy.getCoordinates())) {
                if (powerup.isActive()) {
                    gameResult = GameResult.WIN;
                } else {
                    gameResult = GameResult.LOSE;
                }
                break;
            }
        }

        switch (gameResult) {
            case WIN:
                System.out.println("Gratulálok, győztél!");
                break;
            case LOSE:
                System.out.println("Sajnálom, vesztettél");
                break;
            case TIE:
                System.out.println("Döntetlen");
                break;
        }
    }

    static void addSomeDelay(int k, long timeOut) throws InterruptedException {
        System.out.println(k + " --------------");
        Thread.sleep(timeOut);
    }

    static void draw(Level level, Entity player, Entity enemy, Powerup powerup) {
        for (int row = 0; row < HEIGHT; row++) {
            for (int column = 0; column < WIDTH; column++) {
                Coordinates coordinatesToDraw = new Coordinates(row, column);
                if (coordinatesToDraw.isSameAs(player.getCoordinates())) {
                    System.out.print(player.getMark());
                } else if (coordinatesToDraw.isSameAs(enemy.getCoordinates())) {
                    System.out.print(enemy.getMark());
                } else if (powerup.isPresentOnLevel() && coordinatesToDraw.isSameAs(powerup.getCoordinates())) {
                    System.out.print(powerup.getMark());
                } else {
                    System.out.print(level.getCell(coordinatesToDraw));
                }
            }
            System.out.println();
        }
        if (powerup.isActive()) {
            System.out.println("Powerup active!");
        }
    }
}
