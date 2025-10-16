package basicgame.entity;

import basicgame.Coordinates;
import basicgame.Directon;
import basicgame.Level;

public class Enemy extends MovingEntity{
    public Enemy(String mark, Coordinates coordinates, Coordinates escapeCoordinates, Directon direction, Level level) {
        super(mark, coordinates, escapeCoordinates, direction, level);
    }
}
