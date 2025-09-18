public class MovingEntity extends Entity {

    private Coordinates escapeCoordinates;
    private Directon direction;

    public MovingEntity(String mark, Coordinates coordinates, Coordinates escapeCoordinates, Directon direction, Level level) {
        super(mark, coordinates, level);
        this.escapeCoordinates = escapeCoordinates;
        this.direction = direction;
    }

    public Coordinates getEscapeCoordinates() {
        return escapeCoordinates;
    }

    public void setEscapeCoordinates(Coordinates escapeCoordinates) {
        this.escapeCoordinates = escapeCoordinates;
    }

    public Directon getDirection() {
        return direction;
    }

    public void setDirection(Directon direction) {
        this.direction = direction;
    }

    public boolean update() {
        Coordinates newCoordinates = new Coordinates(getCoordinates());
        switch (direction) {
            case UP:
                if (getLevel().isEmpty(new Coordinates(getCoordinates().getRow() - 1, getCoordinates().getColumn()))) {
                    newCoordinates.setRow(getCoordinates().getRow() - 1);
                }
                break;
            case DOWN:
                if (getLevel().isEmpty(new Coordinates(getCoordinates().getRow() + 1, getCoordinates().getColumn()))) {
                    newCoordinates.setRow(getCoordinates().getRow() + 1);
                }
                break;
            case LEFT:
                if (getLevel().isEmpty(new Coordinates(getCoordinates().getRow(), getCoordinates().getColumn() - 1))) {
                    newCoordinates.setColumn(getCoordinates().getColumn() - 1);
                }
                break;
            case RIGHT:
                if (getLevel().isEmpty(new Coordinates(getCoordinates().getRow(), getCoordinates().getColumn() + 1))) {
                    newCoordinates.setColumn(getCoordinates() .getColumn() + 1);
                }
                break;
        }
        return false;
    }
}
