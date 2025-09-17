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
        }while (!level.isPassable());

        System.out.println("Pálya inicializálások száma: " + counter);
        level.isPassable(true);

        Coordinates playerCoordinates = getRandomStartingCoordinates(level);
        Entity player = new Entity("0", playerCoordinates, level.getFarthestCorner(playerCoordinates), Directon.RIGHT);

        Coordinates enemyCoordinates = getRandomStartingCoordinatesAtDistance(level, player.getCoordinates(), 10);
        Entity enemy = new Entity("-", enemyCoordinates, level.getFarthestCorner(enemyCoordinates), Directon.LEFT);

        String powerUpMark = "*";
        Coordinates powerUpCoordinates = getRandomStartingCoordinates(level);
        boolean powerUpPresentOnLevel = false;
        int powerUpPresentsCounter = 0;
        boolean powerUpActive = false;
        int powerUpActiveCounter = 0;

        GameResult gameResult = GameResult.TIE;

        for (int iteracionNumber = 1; iteracionNumber <= GAME_LOOP_NUMBER; iteracionNumber++) {
            //Player irányváltás
            if (powerUpActive) {
                player.setDirection(level.getShortestPath(player.getDirection(), player.getCoordinates(), enemy.getCoordinates()));
            } else {
                if (powerUpPresentOnLevel) {
                    player.setDirection(level.getShortestPath(player.getDirection(), player.getCoordinates(), powerUpCoordinates));
                } else {
                    if (iteracionNumber % 100 == 0) {
                        player.setEscapeCoordinates(level.getFarthestCorner(player.getCoordinates()));
                    }
                    player.setDirection(level.getShortestPath(player.getDirection(), player.getCoordinates(), player.getEscapeCoordinates()));
                }
            }
            player.setCoordinates(makeMove(player.getDirection(), level, player.getCoordinates()));

            //Ellenfél irányváltás
            if (powerUpActive) {
                if (iteracionNumber % 100 == 0) {
                    enemy.setEscapeCoordinates(level.getFarthestCorner(enemy.getCoordinates()));
                }
                enemy.setDirection(level.getShortestPath(enemy.getDirection(), enemy.getCoordinates(), enemy.getEscapeCoordinates()));
            } else {
                enemy.setDirection(level.getShortestPath(enemy.getDirection(), enemy.getCoordinates(), player.getCoordinates()));
            }
            if (iteracionNumber % 2 == 0) {
                enemy.setCoordinates(makeMove(enemy.getDirection(), level, enemy.getCoordinates()));
            }

            //powerup frissitése
            if (powerUpActive) {
                powerUpActiveCounter++;
            } else {
                powerUpPresentsCounter++;
            }
            if (powerUpPresentsCounter >= 60) {
                if (powerUpPresentOnLevel) {
                    powerUpCoordinates = getRandomStartingCoordinates(level);
                }
                powerUpPresentOnLevel = !powerUpPresentOnLevel;
                powerUpPresentsCounter = 0;
            }
            if (powerUpActiveCounter >= 60) {
                powerUpActive = false;
                powerUpActiveCounter = 0;
                powerUpCoordinates = getRandomStartingCoordinates(level);
                player.setEscapeCoordinates(level.getFarthestCorner(player.getCoordinates()));
            }

            //power up interaction the player
            if (powerUpPresentOnLevel && player.getCoordinates().isSameAs(powerUpCoordinates)) {
                powerUpActive = true;
                powerUpPresentOnLevel = false;
                powerUpActiveCounter = 0;
                enemy.setEscapeCoordinates(level.getFarthestCorner(enemy.getCoordinates()));
            }

            //Pálya és játékos kirajzolása
            draw(level, player.getMark(), player.getCoordinates(), enemy.getMark(), enemy.getCoordinates(), powerUpMark, powerUpCoordinates, powerUpPresentOnLevel, powerUpActive);

            //várakozás
            addSomeDelay(iteracionNumber, 200L);

            //kiléptetés ha elérték egymást
            if (player.getCoordinates().isSameAs(enemy.getCoordinates())) {
                if (powerUpActive) {
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

    static Directon getEscapeDirection(String[][] level, int enemyRow, int enemyColumn, Directon directionTowardsPlayer) {
        Directon escapeDirection = getOppositeDirection(directionTowardsPlayer);
        switch (escapeDirection) {
            case UP:
                if (level[enemyRow - 1][enemyColumn].equals(" ")) {
                    return Directon.UP;
                } else if (level[enemyRow][enemyColumn - 1].equals(" ")) {
                    return Directon.LEFT;
                } else if (level[enemyRow][enemyColumn + 1].equals(" ")) {
                    return Directon.RIGHT;
                } else {
                    return Directon.UP;
                }

            case DOWN:
                if (level[enemyRow + 1][enemyColumn].equals(" ")) {
                    return Directon.DOWN;
                } else if (level[enemyRow][enemyColumn - 1].equals(" ")) {
                    return Directon.LEFT;
                } else if (level[enemyRow][enemyColumn + 1].equals(" ")) {
                    return Directon.RIGHT;
                } else {
                    return Directon.DOWN;
                }

            case RIGHT:
                if (level[enemyRow][enemyColumn + 1].equals(" ")) {
                    return Directon.RIGHT;
                } else if (level[enemyRow - 1][enemyColumn].equals(" ")) {
                    return Directon.UP;
                } else if (level[enemyRow + 1][enemyColumn].equals(" ")) {
                    return Directon.DOWN;
                } else {
                    return Directon.RIGHT;
                }

            case LEFT:
                if (level[enemyRow][enemyColumn - 1].equals(" ")) {
                    return Directon.LEFT;
                } else if (level[enemyRow - 1][enemyColumn].equals(" ")) {
                    return Directon.UP;
                } else if (level[enemyRow + 1][enemyColumn].equals(" ")) {
                    return Directon.DOWN;
                } else {
                    return Directon.LEFT;
                }
            default:
                return escapeDirection;
        }
    }

    static Directon getOppositeDirection(Directon direction) {
        switch (direction) {
            case UP:
                return Directon.DOWN;
            case DOWN:
                return Directon.UP;
            case LEFT:
                return Directon.RIGHT;
            case RIGHT:
                return Directon.LEFT;
            default:
                return direction;
        }
    }

    static Coordinates getRandomStartingCoordinates(Level level) {
        Coordinates randomCoordinates;
        do {
            randomCoordinates = new Coordinates(RANDOM.nextInt(HEIGHT), RANDOM.nextInt(WIDTH));
        } while(!level.isEmpty(randomCoordinates));
        return randomCoordinates;
    }

    static Coordinates getRandomStartingCoordinatesAtDistance(Level level, Coordinates playerStartingCoordinates, int distance) {
        Coordinates randomCoordinates;
        int counter = 0;
        do {
            randomCoordinates = getRandomStartingCoordinates(level);
        } while (counter++ < 1_000 && randomCoordinates.distanceFrom(playerStartingCoordinates) < distance);
        return randomCoordinates;
    }

    static Directon changeDirectionTowards(String[][] level, Directon originalEnemyDirection, int enemyRow, int enemyColumn, int playerRow, int playerColumn) {
        if (playerRow < enemyRow && level[enemyRow - 1][enemyColumn].equals(" ")) {
            return Directon.UP;
        }
        if (playerRow > enemyRow && level[enemyRow + 1][enemyColumn].equals(" ")) {
            return Directon.DOWN;
        }
        if (playerColumn < enemyColumn && level[enemyRow][enemyColumn - 1].equals(" ")) {
            return Directon.LEFT;
        }
        if (playerColumn > enemyColumn && level[enemyRow][enemyColumn + 1].equals(" ")) {
            return Directon.RIGHT;
        }
        return originalEnemyDirection;
    }

    static void addSomeDelay(int k, long timeOut) throws InterruptedException {
        System.out.println(k + " --------------");
        Thread.sleep(timeOut);
    }

    static Directon changeDirection(Directon directon) {
        switch (directon) {
            case RIGHT:
                return Directon.DOWN;
            case DOWN:
                return Directon.LEFT;
            case LEFT:
                return Directon.UP;
            case UP:
                return Directon.RIGHT;
        }
        return directon;
    }

    static Coordinates makeMove(Directon directon, Level level, Coordinates oldCoordinates) {
        Coordinates newCoordinates = new Coordinates(oldCoordinates.getRow(), oldCoordinates.getColumn());
        switch (directon) {
            case UP:
                if (level.isEmpty(new Coordinates(oldCoordinates.getRow() - 1, oldCoordinates.getColumn()))){
                    newCoordinates.setRow(oldCoordinates.getRow()-1);
            }
                break;
            case DOWN:
                if (level.isEmpty(new Coordinates(oldCoordinates.getRow() + 1, oldCoordinates.getColumn()))) {
                    newCoordinates.setRow(oldCoordinates.getRow()+1);
                }
                break;
            case LEFT:
                if (level.isEmpty(new Coordinates(oldCoordinates.getRow(), oldCoordinates.getColumn() - 1))) {
                    newCoordinates.setColumn(oldCoordinates.getColumn()-1);
                }
                break;
            case RIGHT:
                if (level.isEmpty(new Coordinates(oldCoordinates.getRow(), oldCoordinates.getColumn() + 1))) {
                    newCoordinates.setColumn(oldCoordinates.getColumn()+1);
                }break;
        }
        return newCoordinates;
    }

    static void draw(Level level, String playerMark, Coordinates playerCoordinates, String enemyMark, Coordinates enemyCoordinates, String powerUpMark, Coordinates powerUpCoordinates, boolean powerUpPresentOnLevel, boolean powerUpActive) {
        for (int row = 0; row < HEIGHT; row++) {
            for (int column = 0; column < WIDTH; column++) {
                Coordinates coordinatesToDraw = new Coordinates(row, column);
                 if (coordinatesToDraw.isSameAs(playerCoordinates)) {
                    System.out.print(playerMark);
                } else if (coordinatesToDraw.isSameAs(enemyCoordinates)) {
                    System.out.print(enemyMark);
                } else if (powerUpPresentOnLevel && coordinatesToDraw.isSameAs(powerUpCoordinates)) {
                    System.out.print(powerUpMark);
                } else {
                    System.out.print(level.getCell(coordinatesToDraw));
                }
            }
            System.out.println();
        }
        if (powerUpActive) {
            System.out.println("Powerup active!");
        }
    }
}
