public class Entity {

    private String mark;
    private Coordinates coordinates;
    private Coordinates escapeCoordinates;
    private Directon direction;

    public Entity(String mark, Coordinates coordinates, Coordinates escapeCoordinates, Directon direction) {
        this.mark = mark;
        this.coordinates = coordinates;
        this.escapeCoordinates = escapeCoordinates;
        this.direction = direction;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
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
}
