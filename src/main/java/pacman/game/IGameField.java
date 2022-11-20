package pacman.game;

import java.awt.Point;
import java.util.List;

/**
 * Интерфейс игрового поля.
 * <p>
 * Определяет базовый интерфейс игрового поля.
 */
public interface IGameField {
    
    /**
     * Тип содержимого клетки игрового поля.
     */
    public enum CellContent {//<editor-fold defaultstate="collapsed">
        
        /**
         * Возможность нахождения в клетке игрока.
         */
        PASSAGE,
        
        /**
         * Стена.
         */
        WALL,
        
        /**
         * Единица еды.
         */
        FOOD,
        
        /**
         * Магическая таблетка.
         */
        PILLET;
    //</editor-fold>
    }

    /**
     * Возвращает перечень клеток этого игрового поля.
     * 
     * @return перечень клеток этого игрового поля
     */
    public Point[] getCells();
    
    /**
     * Возвращает перечень содержимого клетки.
     * 
     * @param location координаты клетки
     * @return перечень содержимого клетки
     * @see CellContent
     */
    public List<CellContent> getCellContent(Point location);
    
    /**
     * Возвращает <code>true</code> если игрок может находиться в указанной
     * клетке игрового поля.
     * 
     * Также возвращает <code>false</code>, если значение находится за пределами
     * игрового поля или передан аргумент null.
     * 
     * @param place клетка игрового поля
     * @return <code>true</code> если игрок может находиться в указанной клетке
     */
    boolean isCellMovable(Point place);
}
