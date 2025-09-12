import java.util.Random;
public class BasicGame {

    static final int GAME_LOOP_NUMBER = 1_000;
    static final int HEIGHT = 40;
    static final int WIDTH = 40;
    static final Random RANDOM = new Random(100L);

    public static void main(String[] args) throws InterruptedException {
        //Pálya inicializálása
        String[][] level = new String[HEIGHT][WIDTH];
        int counter = 0;
        do {
            initLevel(level);
            addRandomWalls(level);
            counter++;
        } while (!isPassable(level));
        System.out.println("Pálya inicializálások száma: " + counter);
        isPassable(level, true);

        String playerMark = "O";
        Coordinates playerCoordinates = getRandomStartingCoordinates(level);
        Coordinates playerEscapeCoordinates = getFarthestCorner(level, playerCoordinates);
        Directon playerDirection = Directon.RIGHT;

        String enemyMark = "-";
        Coordinates enemyCoordinates = getRandomStartingCoordinatesAtDistance(level, playerCoordinates, 10);
        Coordinates enemyEscapeCoordinates = getFarthestCorner(level, enemyCoordinates);
        Directon enemyDirection = Directon.LEFT;

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
                playerDirection = getShortestPath(level, playerDirection, playerCoordinates, enemyCoordinates);
            } else {
                if (powerUpPresentOnLevel) {
                    playerDirection = getShortestPath(level, playerDirection, playerCoordinates, powerUpCoordinates);
                } else {
                    if (iteracionNumber % 100 == 0) {
                        playerEscapeCoordinates = getFarthestCorner(level, playerCoordinates);
                    }
                    playerDirection = getShortestPath(level, playerDirection, playerCoordinates, playerEscapeCoordinates);
                }
            }
            playerCoordinates = makeMove(playerDirection, level, playerCoordinates);

            //Ellenfél irányváltás
            if (powerUpActive) {
                if (iteracionNumber % 100 == 0) {
                    enemyEscapeCoordinates = getFarthestCorner(level, enemyCoordinates);
                }
                enemyDirection = getShortestPath(level, enemyDirection, enemyCoordinates, enemyEscapeCoordinates);
            } else {
                enemyDirection = getShortestPath(level, enemyDirection, enemyCoordinates, playerCoordinates);
            }
            if (iteracionNumber % 2 == 0) {
                enemyCoordinates = makeMove(enemyDirection, level, enemyCoordinates);
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
                playerEscapeCoordinates = getFarthestCorner(level, playerCoordinates);
            }

            //power up interaction the player
            if (powerUpPresentOnLevel && playerCoordinates.isSameAs(powerUpCoordinates)) {
                powerUpActive = true;
                powerUpPresentOnLevel = false;
                powerUpActiveCounter = 0;
                enemyEscapeCoordinates = getFarthestCorner(level, enemyCoordinates);
            }

            //Pálya és játékos kirajzolása
            draw(level, playerMark, playerCoordinates, enemyMark, enemyCoordinates, powerUpMark, powerUpCoordinates, powerUpPresentOnLevel, powerUpActive);

            //várakozás
            addSomeDelay(iteracionNumber, 200L);

            //kiléptetés ha elérték egymást
            if (playerCoordinates.isSameAs(enemyCoordinates)) {
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

        Coordinates fartherstCorner = new Coordinates();
        fartherstCorner.setRow(farthestRow);
        fartherstCorner.setColumn(farthestColumn);
        return fartherstCorner;
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

    static boolean isPassable(String[][] level) {
        return isPassable(level, false);
    }

    static boolean isPassable(String[][] level, boolean draw) {
        //PÁLYA lemásolása
        String[][] levelCopy = copy(level);

        //Első szóköz és csillaggal helyettesítése
        outer:
        for (int row = 0; row < HEIGHT; row++) {
            for (int column = 0; column < WIDTH; column++) {
                if (" ".equals(levelCopy[row][column])) {
                    levelCopy[row][column] = "*";
                    break outer;
                }
            }
        }

        //csillagok terjesztése
        while (spreadAsterix(levelCopy)) {
        }

        for (int row = 0; row < HEIGHT; row++) {
            for (int column = 0; column < WIDTH; column++) {
                if (" ".equals(levelCopy[row][column])) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean isSpreadAsterixWithCheck(String[][] levelCopy) {
        boolean[][] mask = new boolean[HEIGHT][WIDTH];
        for (int row = 0; row < HEIGHT; row++) {
            for (int column = 0; column < WIDTH; column++) {
                if ("*".equals(levelCopy[row][column])) {
                    mask[row][column] = true;
                }
            }
        }

        boolean changed = false;
        for (int row = 0; row < HEIGHT; row++) {
            for (int column = 0; column < WIDTH; column++) {
                if ("*".equals(levelCopy[row][column]) && mask[row][column]) {
                    if (" ".equals(levelCopy[row - 1][column])) {
                        levelCopy[row - 1][column] = "*";
                        changed = true;
                    }
                    if (" ".equals(levelCopy[row + 1][column])) {
                        levelCopy[row + 1][column] = "*";
                        changed = true;
                    }
                    if (" ".equals(levelCopy[row][column - 1])) {
                        levelCopy[row][column - 1] = "*";
                        changed = true;
                    }
                    if (" ".equals(levelCopy[row][column + 1])) {
                        levelCopy[row][column + 1] = "*";
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    //A csillag melletti szóközök csillagal helyettesítése
    static boolean spreadAsterix(String[][] levelCopy) {
        boolean changed = false;
        for (int row = 0; row < HEIGHT; row++) {
            for (int column = 0; column < WIDTH; column++) {
                if ("*".equals(levelCopy[row][column])) {
                    if (" ".equals(levelCopy[row - 1][column])) {
                        levelCopy[row - 1][column] = "*";
                        changed = true;
                    }
                    if (" ".equals(levelCopy[row + 1][column])) {
                        levelCopy[row + 1][column] = "*";
                        changed = true;
                    }
                    if (" ".equals(levelCopy[row][column - 1])) {
                        levelCopy[row][column - 1] = "*";
                        changed = true;
                    }
                    if (" ".equals(levelCopy[row][column + 1])) {
                        levelCopy[row][column + 1] = "*";
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    static String[][] copy(String[][] level) {
        String[][] copy = new String[HEIGHT][WIDTH];
        for (int row = 0; row < HEIGHT; row++) {
            for (int column = 0; column < WIDTH; column++) {
                copy[row][column] = level[row][column];
            }
        }
        return copy;
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
        Coordinates startingCoordinates = new Coordinates();
        startingCoordinates.setRow(randomRow);
        startingCoordinates.setColumn(randomColumn);
        return startingCoordinates;
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
        Coordinates startingCoordinates = new Coordinates();
        startingCoordinates.setRow(randomRow);
        startingCoordinates.setColumn(randomColumn);
        return startingCoordinates;
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

    static void addRandomWalls(String[][] level) {
        addRandomWalls(level, 10, 10);
    }

    static void addRandomWalls(String[][] level, int numberOfHorizontalWalls, int numberOfVerticalWalls) {
        for (int i = 0; i < numberOfHorizontalWalls; i++) {
            addHorizontalWall(level);
        }
        for (int i = 0; i < numberOfVerticalWalls; i++) {
            addVerticalWall(level);
        }
    }

    static void addHorizontalWall(String[][] level) {
        int wallWidth = RANDOM.nextInt(WIDTH - 3);
        int wallRow = RANDOM.nextInt((HEIGHT - 2) + 1);
        int wallColumn = RANDOM.nextInt((WIDTH - 2) - wallWidth);
        for (int i = 0; i < wallWidth; i++) {
            level[wallRow][wallColumn + i] = "X";
        }
    }

    static void addVerticalWall(String[][] level) {
        int wallHeight = RANDOM.nextInt(HEIGHT - 3);
        int wallColumn = RANDOM.nextInt((WIDTH - 2) + 1);
        int wallRow = RANDOM.nextInt((HEIGHT - 2) - wallHeight);
        for (int i = 0; i < wallHeight; i++) {
            level[wallRow + i][wallColumn] = "X";
        }
    }

    static void addSomeDelay(int k, long timeOut) throws InterruptedException {
        System.out.println(k + " --------------");
        Thread.sleep(timeOut);
    }

    static void initLevel(String[][] level) {
        for (int row = 0; row < level.length; row++) {
            for (int column = 0; column < level[row].length; column++) {
                if (row == 0 || row == HEIGHT - 1 || column == 0 || column == WIDTH - 1) {
                    level[row][column] = "X";
                } else {
                    level[row][column] = " ";
                }
            }
        }
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

    static void draw(String[][] board, String playerMark, Coordinates playerCoordinates, String enemyMark, Coordinates enemyCoordinates, String powerUpMark, Coordinates powerUpCoordinates, boolean powerUpPresentOnLevel, boolean powerUpActive) {
        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                Coordinates coordinatesToDraw = new Coordinates();
                coordinatesToDraw.setRow(row);
                coordinatesToDraw.setColumn(column);
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

    static Coordinates makeMove(Directon directon, String[][] level, Coordinates oldCoordinates) {
        Coordinates newCoordinates = new Coordinates();
        newCoordinates.setRow(oldCoordinates.getRow());
        newCoordinates.setColumn(oldCoordinates.getColumn());
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
}