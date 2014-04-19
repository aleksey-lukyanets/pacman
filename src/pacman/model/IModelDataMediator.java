package pacman.model;

import java.awt.Point;
import java.util.Collection;
import java.util.Map;

/**
 * Определяет интерфейс изменения данных модели игры.
 */
public interface IModelDataMediator {
    
    /**
     * Устанавливает новое расположение игрока на поле.
     * 
     * @param playerId идентификатор игрока
     * @param newLocation клетка расположения игрока на поле
     */
    void setPlayerLocation(int playerId, Point newLocation);
    
    /**
     * Обновляет перечень элементов еды.
     * 
     * @param food новый перечень элементов еды
     */
    void refreshFood(Collection<Point> food);

    /**
     * Обновляет перечень магических таблеток.
     * 
     * @param pillets новый перечень магических таблеток
     */
    void refreshPillets(Collection<Point> pillets);
    
    /**
     * Обновляет состояние привидений (контужено / не контужено).
     * 
     * @param isGhostConfused карта новых состояний привидений
     */
    void refreshGhostsConfused(Map<Integer, Boolean> isGhostConfused);
    
    /**
     * Уведомляет о съедении контуженного привидения Пакманом.
     * 
     * @param playerId идентификатор игрока
     */
    void setConfusedGhostEaten(int playerId);
}
