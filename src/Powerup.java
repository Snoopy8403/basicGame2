public class Powerup extends Entity {

    private boolean presentOnLevel;
    private int presentsCounter;
    private boolean active;
    private int activeCounter;

    public Powerup(String mark, Coordinates coordinates, Level level) {
        super(mark, coordinates, level);
    }

    public int incrementPresenceCounter(){
        return ++presentsCounter;
    }

    public void resetPresenceCounter(){
        presentsCounter = 0;
    }

    public int incrementActiveCounter(){
        return ++activeCounter;
    }

    public void resetActiveCounter(){
        activeCounter = 0;
    }

    public void showOnLevel(){
        presentOnLevel = true;
    }

    public void hideOnLevel(){
        presentOnLevel = false;
    }

    public boolean isPresentOnLevel() {
        return presentOnLevel;
    }

    public void activate(){
        active = true;
    }

    public void deactivate(){
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public boolean update(){
        if (active) {
            incrementActiveCounter();
        } else {
            incrementPresenceCounter();
        }
        if (presentsCounter >= 60) {
            if (presentOnLevel) {
                setCoordinates(getLevel().getRandomCoordinates());
            }
            hideOnLevel();
            resetPresenceCounter();
        }
        if (activeCounter >= 60) {
            deactivate();
            resetActiveCounter();
            setCoordinates(getLevel().getRandomCoordinates());
            return true;
        }
        return false;
    }
}
