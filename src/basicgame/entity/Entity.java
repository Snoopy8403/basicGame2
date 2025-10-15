package basicgame.entity;

import basicgame.Coordinates;
import basicgame.Level;

public interface Entity {
     String getMark();

     void setMark(String mark);

     Coordinates getCoordinates();

     void setCoordinates(Coordinates coordinates);

     Level getLevel();

     boolean update();
}
