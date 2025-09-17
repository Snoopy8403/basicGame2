import java.util.Random;

public class Level {

    private final int height;
    private final int width;
    private final String[][] level;
    private final Random RANDOM;

    public Level(Random random, int height, int width){
        this.RANDOM = random;
        this.height = height;
        this.width = width;
        this.level = new String[height][width];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int lastRowIndex = height - 1;
                int lastColumnIndex = width - 1;
                if (row == 0 || row == lastRowIndex || column == 0 || column == lastColumnIndex) {
                    level[row][column] = "X";
                } else {
                    level[row][column] = " ";
                }
            }
        }
    }

    public void addRandomWalls() {
        addRandomWalls(10, 10);
    }

    public void addRandomWalls(int numberOfHorizontalWalls, int numberOfVerticalWalls) {
        for (int i = 0; i < numberOfHorizontalWalls; i++) {
            addHorizontalWall();
        }
        for (int i = 0; i < numberOfVerticalWalls; i++) {
            addVerticalWall();
        }
    }

    private void addHorizontalWall() {
        int wallWidth = RANDOM.nextInt(width - 3);
        int wallRow = RANDOM.nextInt((height - 2) + 1);
        int wallColumn = RANDOM.nextInt((width - 2) - wallWidth);
        for (int i = 0; i < wallWidth; i++) {
            level[wallRow][wallColumn + i] = "X";
        }
    }

    private void addVerticalWall() {
        int wallHeight = RANDOM.nextInt(height - 3);
        int wallColumn = RANDOM.nextInt((width - 2) + 1);
        int wallRow = RANDOM.nextInt((height - 2) - wallHeight);
        for (int i = 0; i < wallHeight; i++) {
            level[wallRow + i][wallColumn] = "X";
        }
    }

    public boolean isPassable() {
        return isPassable(false);
    }

    public boolean isPassable(boolean draw) {
        //PÁLYA lemásolása
        String[][] levelCopy = copy(level);

        //Első szóköz és csillaggal helyettesítése
        outer:
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (" ".equals(levelCopy[row][column])) {
                    levelCopy[row][column] = "*";
                    break outer;
                }
            }
        }

        //csillagok terjesztése
        while (spreadAsterix(levelCopy)) {
        }

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (" ".equals(levelCopy[row][column])) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSpreadAsterixWithCheck(String[][] levelCopy) {
        boolean[][] mask = new boolean[height][width];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if ("*".equals(levelCopy[row][column])) {
                    mask[row][column] = true;
                }
            }
        }

        boolean changed = false;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
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
    private boolean spreadAsterix(String[][] levelCopy) {
        boolean changed = false;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
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

    private String[][] copy(String[][] level) {
        String[][] copy = new String[height][width];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                copy[row][column] = level[row][column];
            }
        }
        return copy;
    }

    public boolean isEmpty(Coordinates coordinates) {
        return " ".equals(level[coordinates.getRow()][coordinates.getColumn()]);
    }

    public Coordinates getFarthestCorner(Coordinates from) {
        String[][] levelCopy = copy(level);
        levelCopy[from.getRow()][from.getColumn()] = "*";

        int farthestRow = 0;
        int farthestColumn = 0;
        while (isSpreadAsterixWithCheck(levelCopy)) {
            outer:
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
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

    public Directon getShortestPath(Directon defaultDirection, Coordinates from, Coordinates to) {
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

    public String getCell(Coordinates coordinates) {
        return level[coordinates.getRow()][coordinates.getColumn()];
    }
}
