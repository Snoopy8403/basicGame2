public class BasicGame {

    static int gameLoopNumber = 100;
    static int height = 15;
    static int width = 15;

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