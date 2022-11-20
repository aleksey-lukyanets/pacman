package pacman.model;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Карта игрового поля.
 */
public class FieldMap {
    
    private int cellSize;
    private Dimension gameCanvasSize;
    private Dimension gameFieldSize;
    private Point[] startLocations;
    private Point[] wallsLocations;
    private Point[] pilletsLocations;
    
    /**
     * 
     * @param fieldSizeCells размер игрового поля, в клетках
     * @param cellSize размер клетки игрового поля, в пикселах
     * @param startLocations упорядоченный перечень исходных расположений игроков
     * @param wallsLocations перечень расположений стен
     * @param pilletsLocations перечень расположения магических таблеток
     */
    public FieldMap(Dimension fieldSizeCells, int cellSize, Point[] startLocations, Point[] wallsLocations, Point[] pilletsLocations) {
        this.cellSize = cellSize;
        this.gameFieldSize = new Dimension(fieldSizeCells);
        this.gameCanvasSize = new Dimension(fieldSizeCells.width * cellSize, fieldSizeCells.height * cellSize);
        this.startLocations = startLocations;
        this.wallsLocations = wallsLocations;
        this.pilletsLocations = pilletsLocations;
    }
    
    public FieldMap() {
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

    /**
     * @param cellSize the cellSize to set
     */
    protected void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    /**
     * @param gameCanvasSize the gameCanvasSize to set
     */
    protected void setGameCanvasSize(Dimension gameCanvasSize) {
        this.gameCanvasSize = gameCanvasSize;
    }

    /**
     * @param gameFieldSize the gameFieldSize to set
     */
    protected void setGameFieldSize(Dimension gameFieldSize) {
        this.gameFieldSize = gameFieldSize;
    }

    /**
     * @param startLocations the startLocations to set
     */
    protected void setStartLocations(Point[] startLocations) {
        this.startLocations = startLocations;
    }

    /**
     * @param wallsLocations the wallsLocations to set
     */
    protected void setWallsLocations(Point[] wallsLocations) {
        this.wallsLocations = wallsLocations;
    }

    /**
     * @param pilletsLocations the wallsLocations to set
     */
    protected void setPilletsLocations(Point[] pilletsLocations) {
        this.pilletsLocations = pilletsLocations;
    }
}
