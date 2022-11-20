package pacman.model;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Карта игрового поля.
 */
public class GameFieldMap {
    
    private final int cellSize;
    private final Dimension gameCanvasSize;
    private final Dimension gameFieldSize;
    private final Point[] startLocations;
    private final Point[] wallsLocations;
    private final Point[] pilletsLocations;
    
    /**
     * 
     * @param mazeSize размер игрового поля, в клетках
     * @param cellSize размер клетки игрового поля, в пикселах
     * @param startLocations упорядоченный перечень исходных расположений игроков
     * @param wallsLocations перечень расположений стен
     * @param pilletsLocations перечень расположения магических таблеток
     */
    public GameFieldMap(Dimension mazeSize, int cellSize, Point[] startLocations, Point[] wallsLocations, Point[] pilletsLocations) {
        this.cellSize = cellSize;
        this.gameFieldSize = new Dimension(mazeSize);
        this.gameCanvasSize = new Dimension(mazeSize.width * cellSize, mazeSize.height * cellSize);
        this.startLocations = startLocations;
        this.wallsLocations = wallsLocations;
        this.pilletsLocations = pilletsLocations;
    }

    /**
     * @return размер клетки игрового поля, в пикселах
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * @return размер холста игрового поля, в пикселах
     */
    public Dimension getGameCanvasDimension() {
        return gameCanvasSize;
    }

    /**
     * @return размер игрового поля, в клетках
     */
    public Dimension getGameFieldSize() {
        return gameFieldSize;
    }

    /**
     * @return упорядоченный перечень исходных расположений игроков
     */
    public Point[] getStartLocations() {
        return startLocations;
    }

    /**
     * @return перечень расположений стен
     */
    public Point[] getWallsLocations() {
        return wallsLocations;
    }

    /**
     * @return перечень расположения магических таблеток
     */
    public Point[] getPilletsLocations() {
        return pilletsLocations;
    }
}
