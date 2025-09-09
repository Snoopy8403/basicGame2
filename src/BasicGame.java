import javax.xml.transform.Source;
import java.sql.SQLOutput;
import java.util.Random;

public class BasicGame {

    static final int GAME_LOOP_NUMBER = 100;
    static final int HEIGHT = 15;
    static final int WIDTH = 15;
    static final Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {
        //Pálya inicializálása
        String[][] level = new String[HEIGHT][WIDTH];
        initLevel(level);
        addRandomWalls(level);

        String playerMark = "O";
        int[] playerStartingCoordinates = getRandomStartingCoordinates(level);
        int playerRow = playerStartingCoordinates[0];
        int playerColumn = playerStartingCoordinates[1];
        Directon playerDirection = Directon.RIGHT;

        String enemyMark = "-";
        int[] enemyStartingCoordinates = getRandomStartingCoordinatesAtDistance(level, playerStartingCoordinates, 10);
        int enemyRow = enemyStartingCoordinates[0];
        int enemyColumn = enemyStartingCoordinates[1];
        Directon enemyDirection = Directon.LEFT;

        String powerUpMark = "*";
        int[] powerUpStartingCoordinates = getRandomStartingCoordinates(level);
        int powerUpRow = powerUpStartingCoordinates[0];
        int powerUpColumn = powerUpStartingCoordinates[1];
        boolean powerUpPresentOnLevel = false;
        int powerUpPresentsCounter = 0;
        boolean powerUpActive = false;
        int powerUpActiveCounter = 0;
        GameResult gameResult = GameResult.TIE;

        for (int iteracionNumber = 1; iteracionNumber <= GAME_LOOP_NUMBER; iteracionNumber++){
            //Player
            //irányváltás
            if (powerUpActive){
                playerDirection = changeDirectionTowards(level, playerDirection, playerRow, playerColumn, enemyRow, enemyColumn);
            }
            else {
                if (powerUpPresentOnLevel){
                    playerDirection = changeDirectionTowards(level, playerDirection, playerRow, playerColumn, powerUpRow, powerUpColumn);
                }
                if (iteracionNumber % 15 == 0) {
                    playerDirection = changeDirection(playerDirection);
                }
            }

            //ellenfél Léptetés
            int[] playerCoordinates = makeMove(playerDirection, level, playerRow, playerColumn);
            playerRow = playerCoordinates[0];
            playerColumn = playerCoordinates[1];

            //Enemy
            //irányváltás

            if (powerUpActive){
                Directon directionTowardsPlayer = changeDirectionTowards(level, enemyDirection, enemyRow, enemyColumn, playerRow, playerColumn);
                enemyDirection = getEscapeDirection(level, enemyRow, enemyColumn, directionTowardsPlayer);
            }else {
                enemyDirection = changeDirectionTowards(level, enemyDirection, enemyRow, enemyColumn, playerRow, playerColumn);
            }

            //Léptetés
            if (iteracionNumber%2 == 0) {
                int[] enemyCoordinates = makeMove(enemyDirection, level, enemyRow, enemyColumn);
                enemyRow = enemyCoordinates[0];
                enemyColumn = enemyCoordinates[1];
            }

            //powerup frissitése
            if (powerUpActive){
                powerUpActiveCounter++;
            }
            else {
                powerUpPresentsCounter++;
            }
            if (powerUpPresentsCounter >= 20){
                if (powerUpPresentOnLevel){
                    powerUpStartingCoordinates = getRandomStartingCoordinates(level);
                    powerUpRow = powerUpStartingCoordinates[0];
                    powerUpColumn = powerUpStartingCoordinates[1];
                }
                powerUpPresentOnLevel = !powerUpPresentOnLevel;
                powerUpPresentsCounter = 0;
            }
            if (powerUpActiveCounter >= 20){
                powerUpActive = false;
                powerUpActiveCounter = 0;
                powerUpStartingCoordinates = getRandomStartingCoordinates(level);
                powerUpRow = powerUpStartingCoordinates[0];
                powerUpColumn = powerUpStartingCoordinates[1];
            }

            //power up interaction the player
            if (powerUpPresentOnLevel && playerRow == powerUpRow && playerColumn == powerUpColumn)
            {
                powerUpActive = true;
                powerUpPresentOnLevel = false;
                powerUpActiveCounter = 0;
            }

            //Pálya és játékos kirajzolása
            draw(level, playerMark, playerRow, playerColumn, enemyMark, enemyRow, enemyColumn, powerUpMark, powerUpRow, powerUpColumn, powerUpPresentOnLevel, powerUpActive);

            //várakozás
            addSomeDelay(iteracionNumber, 200L);

            //kiléptetés ha elérték egymást
            if (playerRow == enemyRow && playerColumn == enemyColumn){
                if (powerUpActive){
                    gameResult = GameResult.WIN;
                }else {
                    gameResult = GameResult.LOSE;
                }
                break;
            }
        }

        switch (gameResult) {
            case WIN:
            System.out.println("Gratulálok, győztél!"); break;
            case LOSE:
            System.out.println("Sajnálom, vesztettél");break;
            case TIE:
                System.out.println("Döntetlen");break;
        }
    }

    static Directon getEscapeDirection(String[][] level, int enemyRow, int enemyColumn, Directon directionTowardsPlayer) {
        Directon escapeDirection = getOppositeDirection(directionTowardsPlayer);
        switch (escapeDirection){
            case UP: if (level[enemyRow - 1][enemyColumn].equals(" ")){
                return Directon.UP;
            } else if (level[enemyRow][enemyColumn - 1].equals(" ")) {
                return Directon.LEFT;
            } else if (level[enemyRow][enemyColumn + 1].equals(" ")) {
                return Directon.RIGHT;
            } else {
                return Directon.UP;
            }

            case DOWN: if (level[enemyRow + 1][enemyColumn].equals(" ")){
                return Directon.DOWN;
            } else if (level[enemyRow][enemyColumn - 1].equals(" ")) {
                return Directon.LEFT;
            } else if (level[enemyRow][enemyColumn + 1].equals(" ")) {
                return Directon.RIGHT;
            } else {
                return Directon.DOWN;
            }

            case RIGHT: if (level[enemyRow][enemyColumn + 1].equals(" ")){
                return Directon.RIGHT;
            } else if (level[enemyRow - 1][enemyColumn].equals(" ")) {
                return Directon.UP;
            } else if (level[enemyRow + 1][enemyColumn].equals(" ")) {
                return Directon.DOWN;
            } else {
                return Directon.RIGHT;
            }

            case LEFT: if (level[enemyRow][enemyColumn - 1].equals(" ")){
                return Directon.LEFT;
            } else if (level[enemyRow - 1][enemyColumn].equals(" ")) {
                return Directon.UP;
            } else if (level[enemyRow + 1][enemyColumn].equals(" ")) {
                return Directon.DOWN;
            } else {
                return Directon.LEFT;
            }
            default: return escapeDirection;
        }
    }

    static Directon getOppositeDirection(Directon direction) {

        switch (direction){
            case UP: return Directon.DOWN;
            case DOWN: return Directon.UP;
            case LEFT: return Directon.RIGHT;
            case RIGHT: return Directon.LEFT;
            default: return direction;
        }
    }

    static int[] getRandomStartingCoordinatesAtDistance(String[][] level, int[] playerStartingCoordinates, int distance) {
        int playerStartingRow = playerStartingCoordinates[0];
        int playerStartingColumn = playerStartingCoordinates[1];
        int randomRow;
        int randomColumn;
        int counter = 0;
        do {
            randomRow = RANDOM.nextInt(HEIGHT);
            randomColumn = RANDOM.nextInt(WIDTH);
        } while (counter++ < 1_000 &&
                (!level[randomRow][randomColumn].equals(" ") ||
                        calculateDistance(randomRow, randomColumn, playerStartingRow, playerStartingColumn) < distance));
        return new int[]{randomRow, randomColumn};
    }

    static int calculateDistance(int row1, int column1, int row2, int column2) {
        int rowDifference = Math.abs(row1 - row2);
        int columnDifference = Math.abs(column1 - column2);
        return rowDifference + columnDifference;
    }

    static int[] getRandomStartingCoordinates(String[][] level) {
        int randomRow;
        int randomColumn;
        do {
            randomRow = RANDOM.nextInt(HEIGHT);
            randomColumn = RANDOM.nextInt(WIDTH);
        } while (!level[randomRow][randomColumn].equals(" "));
        return new int[]{randomRow, randomColumn};
    }

    static Directon changeDirectionTowards(String[][] level, Directon originalEnemyDirection, int enemyRow, int enemyColumn, int playerRow, int playerColumn) {
    if (playerRow < enemyRow && level[enemyRow-1][enemyColumn].equals(" ")){
        return Directon.UP;
    }
    if (playerRow > enemyRow && level[enemyRow+1][enemyColumn].equals(" ")){
        return Directon.DOWN;
    }
    if (playerColumn < enemyColumn && level[enemyRow][enemyColumn - 1].equals(" ")){
        return Directon.LEFT;
    }
    if (playerColumn > enemyColumn && level[enemyRow][enemyColumn + 1].equals(" ")){
        return Directon.RIGHT;
    }
        return originalEnemyDirection;
    }

    static void addRandomWalls(String[][] level){
        addRandomWalls(level, 3, 2);
    }

    static void addRandomWalls(String[][] level, int numberOfHorizontalWalls, int numberOfVerticalWalls){
        for (int i = 0; i < numberOfHorizontalWalls; i++){
            addHorizontalWall(level);
        }
        for (int i = 0; i < numberOfVerticalWalls; i++){
            addVerticalWall(level);
        }
    }

    static void addHorizontalWall(String[][] level){
        int wallWidth = RANDOM.nextInt(WIDTH -3);
        int wallRow = RANDOM.nextInt((HEIGHT - 2) +1 );
        int wallColumn = RANDOM.nextInt((WIDTH - 2) - wallWidth );
        for (int i = 0; i < wallWidth; i++){
            level[wallRow][wallColumn + i] = "X";
        }
    }

    static void addVerticalWall(String[][] level){
        int wallHeight = RANDOM.nextInt(HEIGHT -3);
        int wallColumn = RANDOM.nextInt((WIDTH - 2) +1 );
        int wallRow = RANDOM.nextInt((HEIGHT - 2) - wallHeight );
        for (int i = 0; i < wallHeight; i++){
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
                if (row == 0 || row == HEIGHT -1 || column == 0 || column == WIDTH -1) {
                    level[row][column] = "X";
                } else {
                    level[row][column] = " ";
                }
            }
        }
    }

    static Directon changeDirection(Directon directon) {
        switch (directon){
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

    static void draw(String[][] board, String playerMark, int playerRow, int playerColumn, String enemyMark, int enemyRow, int enemyColumn, String powerUpMark, int powerUpRow, int powerUpColumn, boolean powerUpPresentOnLevel, boolean powerUpActive){
         for (int row = 0; row < board.length; row++) {
             for (int column = 0; column < board[row].length; column++) {
                 if (row == playerRow && column == playerColumn) {
                     System.out.print(playerMark);
                 } else if (row == enemyRow && column == enemyColumn) {
                     System.out.print(enemyMark);
                 } else if (powerUpPresentOnLevel && (row == powerUpRow && column == powerUpColumn)) {
                     System.out.print(powerUpMark);
                 } else {
                     System.out.print(board[row][column]);
                 }
             }
             System.out.println();
         }             if (powerUpActive) {
            System.out.println("Powerup active!");
        }
     }

     static int[] makeMove(Directon directon,String[][] level, int row, int column){
         switch (directon){
             case UP:
                 if (level[row - 1][column].equals(" ")){
                     row--;
                 }
                 break;
             case DOWN:
                 if (level[row + 1][column].equals(" ")){
                     row++;
                 }
                 break;
             case LEFT:
                 if (level[row][column - 1].equals(" ")){
                     column--;
                 }
                 break;
             case RIGHT:
                 if (level[row][column + 1].equals(" ")){
                     column++;
                 }
                 break;
         }
         return new int[] {row, column};
     }
}