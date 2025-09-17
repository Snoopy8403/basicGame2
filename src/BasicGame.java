import java.util.Random;
public class BasicGame {

    static final int GAME_LOOP_NUMBER = 1_000;
    static final int HEIGHT = 40;
    static final int WIDTH = 40;
    static final Random RANDOM = new Random(100L);

    public static void main(String[] args) throws InterruptedException {

        Level level = new Level(RANDOM, HEIGHT, WIDTH);
        int counter = 0;
        do {
            level.addRandomWalls();
        }while (!isPassable(level));


        String[][] level = new String[HEIGHT][WIDTH];
        int counter = 0;
        do {
            addRandomWalls(level);
            counter++;
        } while (!isPassable(level));
        System.out.println("Pálya inicializálások száma: " + counter);
        isPassable(level, true);

        Coordinates playerCoordinates = getRandomStartingCoordinates(level);
        Entity player = new Entity("0", playerCoordinates, getFarthestCorner(level, playerCoordinates), Directon.RIGHT);

        Coordinates enemyCoordinates = getRandomStartingCoordinatesAtDistance(level, player.getCoordinates(), 10);
        Entity enemy = new Entity("-", enemyCoordinates, getFarthestCorner(level, enemyCoordinates), Directon.LEFT);

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
                player.setDirection(getShortestPath(level, player.getDirection(), player.getCoordinates(), enemy.getCoordinates()));
            } else {
                if (powerUpPresentOnLevel) {
                    player.setDirection(getShortestPath(level, player.getDirection(), player.getCoordinates(), powerUpCoordinates));
                } else {
                    if (iteracionNumber % 100 == 0) {
                        player.setEscapeCoordinates(getFarthestCorner(level, player.getCoordinates()));
                    }
                    player.setDirection(getShortestPath(level, player.getDirection(), player.getCoordinates(), player.getEscapeCoordinates()));
                }
            }
            player.setCoordinates(makeMove(player.getDirection(), level, player.getCoordinates()));

            //Ellenfél irányváltás
            if (powerUpActive) {
                if (iteracionNumber % 100 == 0) {
                    enemy.setEscapeCoordinates(getFarthestCorner(level, enemy.getCoordinates()));
                }
                enemy.setDirection(getShortestPath(level, enemy.getDirection(), enemy.getCoordinates(), enemy.getEscapeCoordinates()));
            } else {
                enemy.setDirection(getShortestPath(level, enemy.getDirection(), enemy.getCoordinates(), player.getCoordinates()));
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
                player.setEscapeCoordinates(getFarthestCorner(level, player.getCoordinates()));
            }

            //power up interaction the player
            if (powerUpPresentOnLevel && player.getCoordinates().isSameAs(powerUpCoordinates)) {
                powerUpActive = true;
                powerUpPresentOnLevel = false;
                powerUpActiveCounter = 0;
                enemy.setEscapeCoordinates(getFarthestCorner(level, enemy.getCoordinates()));
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

    private static Coordinates getFarthestCorner(String[][] level, Coordinates from) {
        String[][] levelCopy = copy(level);
        levelCopy[from.getRow()][from.getColumn()] = "*";

        int farthestRow = 0;
        int farthestColumn = 0;
        while (isSpreadAsterixWithCheck(levelCopy)) {
            outer:
            for (int row = 0; row < HEIGHT; row++) {
                for (int column = 0; column < WIDTH; column++) {
                    if (" ".equals(levelCopy[row][column])) {
                        farthestRow = row;
                        farthestColumn = column;
                        break outer;
                    }
                }
            }
        }

        return new Coordinates(farthestRow, farthestColumn);
    }

    static Directon getShortestPath(String[][] level, Directon defaultDirection, Coordinates from, Coordinates to) {
        //PÁLYA lemásolása
        String[][] levelCopy = copy(level);

        //Első csillag lehelyezése a célpontra
        levelCopy[to.getRow()][to.getColumn()] = "*";

        //csillagok terjesztése
        while (isSpreadAsterixWithCheck(levelCopy)) {

            if ("*".equals(levelCopy[from.getRow() - 1][from.getColumn()])) {
                return Directon.UP;
            }
            if ("*".equals(levelCopy[from.getRow() + 1][from.getColumn()])) {
                return Directon.DOWN;
            }
            if ("*".equals(levelCopy[from.getRow()][from.getColumn() - 1])) {
                return Directon.LEFT;
            }
            if ("*".equals(levelCopy[from.getRow()][from.getColumn() + 1])) {
                return Directon.RIGHT;
            }
        }
        return defaultDirection;
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

    static Coordinates getRandomStartingCoordinatesAtDistance(String[][] level, Coordinates playerStartingCoordinates, int distance) {
        int playerStartingRow = playerStartingCoordinates.getRow();
        int playerStartingColumn = playerStartingCoordinates.getColumn();
        int randomRow;
        int randomColumn;
        int counter = 0;
        do {
            randomRow = RANDOM.nextInt(HEIGHT);
            randomColumn = RANDOM.nextInt(WIDTH);
        } while (counter++ < 1_000 &&
                (!level[randomRow][randomColumn].equals(" ") ||
                        calculateDistance(randomRow, randomColumn, playerStartingRow, playerStartingColumn) < distance));
        return new Coordinates(randomRow, randomColumn);
    }

    static int calculateDistance(int row1, int column1, int row2, int column2) {
        int rowDifference = Math.abs(row1 - row2);
        int columnDifference = Math.abs(column1 - column2);
        return rowDifference + columnDifference;
    }

    static Coordinates getRandomStartingCoordinates(String[][] level) {
        int randomRow;
        int randomColumn;
        do {
            randomRow = RANDOM.nextInt(HEIGHT);
            randomColumn = RANDOM.nextInt(WIDTH);
        } while (!level[randomRow][randomColumn].equals(" "));
        return new Coordinates(randomRow, randomColumn);
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

    static Coordinates makeMove(Directon directon, String[][] level, Coordinates oldCoordinates) {
        Coordinates newCoordinates = new Coordinates(oldCoordinates.getRow(), oldCoordinates.getColumn());
        switch (directon) {
            case UP:
                if (level[oldCoordinates.getRow() - 1][oldCoordinates.getColumn()].equals(" ")) {
                    newCoordinates.setRow(oldCoordinates.getRow()-1);
                }
                break;
            case DOWN:
                if (level[oldCoordinates.getRow() + 1][oldCoordinates.getColumn()].equals(" ")) {
                    newCoordinates.setRow(oldCoordinates.getRow()+1);
                }
                break;
            case LEFT:
                if (level[oldCoordinates.getRow()][oldCoordinates.getColumn() - 1].equals(" ")) {
                    newCoordinates.setColumn(oldCoordinates.getColumn()-1);
                }
                break;
            case RIGHT:
                if (level[oldCoordinates.getRow()][oldCoordinates.getColumn() + 1].equals(" ")) {
                    newCoordinates.setColumn(oldCoordinates.getColumn()+1);
                }break;
        }
        return newCoordinates;
    }

    static void draw(String[][] board, String playerMark, Coordinates playerCoordinates, String enemyMark, Coordinates enemyCoordinates, String powerUpMark, Coordinates powerUpCoordinates, boolean powerUpPresentOnLevel, boolean powerUpActive) {
        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                Coordinates coordinatesToDraw = new Coordinates(row, column);
                 if (coordinatesToDraw.isSameAs(playerCoordinates)) {
                    System.out.print(playerMark);
                } else if (coordinatesToDraw.isSameAs(enemyCoordinates)) {
                    System.out.print(enemyMark);
                } else if (powerUpPresentOnLevel && coordinatesToDraw.isSameAs(powerUpCoordinates)) {
                    System.out.print(powerUpMark);
                } else {
                    System.out.print(board[row][column]);
                }
            }
            System.out.println();
        }
        if (powerUpActive) {
            System.out.println("Powerup active!");
        }
    }
}