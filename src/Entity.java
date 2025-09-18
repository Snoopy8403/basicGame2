public class Entity {

    private String mark;
    private Coordinates coordinates;
    private Level level;

    public Entity(String mark, Coordinates coordinates, Level level) {
        this.mark = mark;
        this.coordinates = coordinates;
        this.level = level;
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

    public Level getLevel() {
        return level;
    }

    public boolean update(){
        return false;
    }
}
