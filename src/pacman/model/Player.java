package pacman.model;

import java.awt.Point;
import pacman.game.IAnimatedPlayer;
import pacman.game.IAction;

/**
 * Модель игрока. Объединяет общие для любого игрока данные.
 */
public class Player implements IAnimatedPlayer, IMovableEntity {

    private final PlayerType nativePlayerType;
    private final int colorId;
    
    private Point currentLocation;
    private IAction currentAction;
    private Point animationShift;
    private boolean isConfused = false;

    /**
     * Создаёт новую модель игрока.
     *
     * @param playerType тип игрока
     * @param colorId идентификатор цветовой схемы
     * @param playerStart начальное положение игрока
     * @param startDirection направление, в котором повёрнут игрок
     */
    public Player(PlayerType playerType, int colorId, Point playerStart, IAction startDirection) {
        this.nativePlayerType = playerType;
        this.colorId = colorId;
        this.currentLocation = playerStart;
        currentAction = startDirection;
        animationShift = new Point(0, 0);
    }
    
    /**
     * Устанавливает расположение игрока на поле.
     * 
     * @param location клетка расположения игрока на поле
     */
    public void setLocation(Point location) {
        currentLocation = location;
    }
    
    public void setConfused(boolean confused) {
        isConfused = confused;
    }
    
    public boolean isConfused() {
        return isConfused;
    }
    
    @Override
    public int hashCode() {
        return (7 * nativePlayerType.ordinal() + 23 * colorId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Player other = (Player) obj;
        if (this.nativePlayerType != other.nativePlayerType) {
            return false;
        }
        if (this.colorId != other.colorId) {
            return false;
        }
        return true;
    }

    //----------------------------------- Реализация интерфейса IMovableEntity
    
    @Override
    public void setCurrentAction(IAction action) {
        currentAction = action;
    }

    @Override
    public void setAnimationShift(Point shift) {
        animationShift = shift;
    }
    
    //----------------------------------- Реализация интерфейса IAnimatedPlayer
    
    @Override
    public Point getLocation() {
        return currentLocation;
    }

    @Override
    public IAction getCurrentAction() {
        return currentAction;
    }

    @Override
    public PlayerType getType() {
        if (isConfused()) {
            return PlayerType.CONFUSED_GHOST;
        } else {
            return nativePlayerType;
        }
    }

    @Override
    public Point getShiftVector() {
        return animationShift;
    }

    @Override
    public int getShiftScalar() {
        return currentAction.getShiftScalar(animationShift);
    }

    @Override
    public int getColorId() {
        return colorId;
    }
}
