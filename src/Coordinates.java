public class Coordinates {
    private int row;
    private int column;

    public Coordinates(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Coordinates(Coordinates other){
        this(other.getRow(), other.getColumn());
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isSameAs(Coordinates other){
        return this.row == other.row && this.column == other.column;
    }

    public int distanceFrom(Coordinates other) {
        int rowDifference = Math.abs(row - other.row);
        int columnDifference = Math.abs(column - other.column);
        return rowDifference + columnDifference;
    }
}
