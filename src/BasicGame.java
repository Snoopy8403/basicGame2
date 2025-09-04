import java.util.Random;

public class BasicGame {

    static int gameLoopNumber = 100;
    static int height = 15;
    static int width = 15;
    static Random random = new Random();

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
        String[][] level = new String[height][width];
        initLevel(level);
        addRandomWalls(level, 1, 1);
//test
        for (int iteracionNumber = 1; iteracionNumber <= gameLoopNumber; iteracionNumber++){
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
                enemyDirection = changeDirection(enemyDirection);
            }
            //Léptetés
            int[] enemyCoordinates = makeMove(enemyDirection, level, enemyRow, enemyColumn);
            enemyRow = enemyCoordinates[0];
            enemyColumn = enemyCoordinates[1];


            //Pálya és játékos kirajzolása
            draw(level, playerMark, playerRow, playerColumn, enemyMark, enemyRow, enemyColumn);

            //várakozás
            addSomeDelay(iteracionNumber, 200L);

            if (playerRow == enemyRow && playerColumn == enemyColumn){
                break;
            }
        }
            System.out.println("Játék vége!");
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
        int wallWidth = random.nextInt(width-3);
        int wallRow = random.nextInt((height - 2) +1 );
        int wallColumn = random.nextInt((width - 2) - wallWidth );
        for (int i = 0; i < wallWidth; i++){
            level[wallRow][wallColumn + i] = "X";
        }
    }

    static void addVerticalWall(String[][] level){
        int wallHeight = random.nextInt(height-3);
        int wallColumn = random.nextInt((width - 2) +1 );
        int wallRow = random.nextInt((height - 2) - wallHeight );
        for (int i = 0; i < wallHeight; i++){
            level[wallRow + i][wallColumn] = "X";
        }
    }

    private static void addSomeDelay(int k, long timeOut) throws InterruptedException {
        System.out.println(k + " --------------");
        Thread.sleep(timeOut);
    }

    private static void initLevel(String[][] level) {
        for (int row = 0; row < level.length; row++) {
            for (int column = 0; column < level[row].length; column++) {
                if (row == 0 || row == height-1 || column == 0 || column == width-1) {
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