import java.util.Random;

public class BasicGame {

    static final int GAME_LOOP_NUMBER = 100;
    static final int HEIGHT = 15;
    static final int WIDTH = 15;
    static final Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {

        String playerMark = "O";
        int playerRow = 2;
        int playerColumn = 2;
        Directon playerDirection = Directon.RIGHT;

        String enemyMark = "-";
        int enemyRow = 7;
        int enemyColumn = 4;
        Directon enemyDirection = Directon.LEFT;


        //Pálya inicializálása
        String[][] level = new String[HEIGHT][WIDTH];
        initLevel(level);
        addRandomWalls(level, 1, 1);

        for (int iteracionNumber = 1; iteracionNumber <= GAME_LOOP_NUMBER; iteracionNumber++){
            //Player
            //irányváltás
            if (iteracionNumber % 15 == 0){
                playerDirection = changeDirection(playerDirection);
            }
            //Léptetés
            int[] playerCoordinates = makeMove(playerDirection, level, playerRow, playerColumn);
            playerRow = playerCoordinates[0];
            playerColumn = playerCoordinates[1];

            //Enemy
            //irányváltás
            if (iteracionNumber % 10 == 0){
                enemyDirection = changeEnemyDirection(level, enemyDirection, playerRow, playerColumn, enemyRow, enemyColumn);
            }
            //Léptetés
            if (iteracionNumber%2 == 0) {
                int[] enemyCoordinates = makeMove(enemyDirection, level, enemyRow, enemyColumn);
                enemyRow = enemyCoordinates[0];
                enemyColumn = enemyCoordinates[1];
            }

            //Pálya és játékos kirajzolása
            draw(level, playerMark, playerRow, playerColumn, enemyMark, enemyRow, enemyColumn);

            //várakozás
            addSomeDelay(iteracionNumber, 200L);

            //kiléptetés ha elérték egymást
            if (playerRow == enemyRow && playerColumn == enemyColumn){
                break;
            }
        }
            System.out.println("Játék vége!");
    }

    static Directon changeEnemyDirection(String[][] level, Directon originalEnemyDirection, int playerRow, int playerColumn, int enemyRow, int enemyColumn) {
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

    static void draw(String[][] board, String playerMark, int playerRow, int playerColumn, String enemyMark, int enemyRow, int enemyColumn){
         for (int row = 0; row < board.length; row++) {
             for (int column = 0; column < board[row].length; column++) {
                 if (row == playerRow && column == playerColumn) {
                     System.out.print(playerMark);
                 } else if (row == enemyRow && column == enemyColumn) {
                     System.out.print(enemyMark);
                 } else {
                     System.out.print(board[row][column]);
                 }
             }
             System.out.println();
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