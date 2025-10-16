package basicgame.entity;

import basicgame.Coordinates;
import basicgame.Directon;
import basicgame.Level;

public class Player extends MovingEntity{
    public Player(String mark, Coordinates coordinates, Coordinates escapeCoordinates, Directon direction, Level level) {
        super(mark, coordinates, escapeCoordinates, direction, level);
    }
}
