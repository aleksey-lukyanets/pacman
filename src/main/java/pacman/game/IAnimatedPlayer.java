package pacman.game;

import java.awt.Point;

/**
 * Интерфейс анимирования игрока.
 * <p>
 * Определяет базовый интерфейс получения данных об игроке для изображения и
 * анимирования его фигуры.
 */
public interface IAnimatedPlayer {

    /**
     * Тип игрока.
     */
    public enum PlayerType {//<editor-fold defaultstate="collapsed">
        
        /**
         * Контуженное привидение.
         */
        CONFUSED_GHOST,
        
        /**
         * Пакман.
         */
        PACMAN,
        
        /**
         * Привидение.
         */
        HORRIFIC_GHOST;
        //</editor-fold>
    }

    /**
     * Возвращает тип этого игрока.
     * 
     * @return тип этого игрока
     * @see PlayerType
     */
    public PlayerType getType();

    /**
     * Возвращает идентификатор цветовой схемы этого игрока.
     * 
     * @return идентификатор цветовой схемы этого игрока
     */
    public int getColorId();
    
    /**
     * Возвращает расположение этого игрока на игровом поле.
     * 
     * @return расположение этого игрока
     */
    public Point getLocation();
    
    /**
     * Возвращает текущее действие этого игрока.
     * 
     * @return текущее действие этого игрока
     */
    public IAction getCurrentAction();
    
    /**
     * Возвращает сдвиг фигуры этого игрока относительно центра клетки игрового
     * поля в формате {@literal (dx, dy)}.
     * 
     * @return сдвиг фигуры этого игрока, в пикселах
     */
    public Point getShiftVector();
    
    /**
     * Возвращает линейный сдвиг фигуры этого игрока относительно центра клетки
     * игрового поля.
     * 
     * @return линейный сдвиг фигуры этого игрока, в пикселах
     */
    public int getShiftScalar();
}
