package pacman.model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pacman.game.IGameField;

/**
 * Игровое поле. Хранит клетки игрового поля и их содержимое.
 */
public class Maze implements IGameField {

    private final Map<Point, MazeCell> mazeCells = new HashMap<Point, MazeCell>();
    private final Set<Point> foodLocation = new HashSet<Point>();
    private final Set<Point> pilletsLocation = new HashSet<Point>();

    /**
     * Создаёт игровое поле заданного размера.
     * <p>
     * В расположенные по периметру клетки поля добавляются стены.
     * 
     * @param gameSpaceSize размер игрового поля в клетках
     * @param gameSpace перечень клеток игрового поля
     */
    public Maze(Dimension gameSpaceSize, List<Point> gameSpace) {
        for (Point p : gameSpace) {
            MazeCell newMazeCell = new MazeCell();
            if ((p.x == 0) || (p.x >= gameSpaceSize.getWidth() - 1)
                    || (p.y == 0) || (p.y >= gameSpaceSize.getHeight() - 1)) {
                newMazeCell.addWall();
            }
            mazeCells.put(p, newMazeCell);
        }
    }

    /**
     * Создаёт игровое поле с заданными стенами и расположением еды.
     * 
     * @param field
     * @param gameSpace
     * @param food
     */
    public Maze(GameFieldMap field, List<Point> gameSpace, Point[] food) {//<editor-fold defaultstate="collapsed">
        
        this(field.getGameFieldSize(), gameSpace);
        
        Point[] walls = field.getWallsLocations();
        Point[] pillets = field.getPilletsLocations();
        
        for (Point place : walls) {
            this.mazeCells.get(place).addWall();
        }
        foodLocation.addAll(Arrays.asList(food));
        for (Point unit : food) {
            this.mazeCells.get(unit).addFood();
        }
        pilletsLocation.addAll(Arrays.asList(pillets));
        for (Point unit : pillets) {
            this.mazeCells.get(unit).addPillet();
        }
        //</editor-fold>
    }

    /**
     * Создаёт игровое поле с симметричными стенами и автозаполнением едой.
     * <p>
     * В конструктор достаточно передать перечень стен для левой половины поля,
     * правая половина будет заполнена стенами зеркально. Едой заполняются все
     * проходимые клетки поля, исключая клетки начального расположения игроков.
     *
     * @param field
     * @param gameSpace перечень клеток игрового поля
     * @param mirrorWalls отразить стены в правую часть, если <code>true</code>
     */
    public Maze(GameFieldMap field, List<Point> gameSpace, boolean mirrorWalls) {//<editor-fold defaultstate="collapsed">
        
        this(field.getGameFieldSize(), gameSpace);
        
        Dimension gameFieldSize = field.getGameFieldSize();
        Point[] walls = field.getWallsLocations();
        Point[] pillets = field.getPilletsLocations();
        Point[] startLocations = field.getStartLocations();
        
        if (mirrorWalls) {
            int halfFieldWidth = gameFieldSize.width / 2;
            for (Point place : walls) {
                if (place.x < halfFieldWidth) {
                    this.mazeCells.get(place).addWall();
                    Point mirroredPlace = new Point(gameFieldSize.width - place.x - 1, place.y);
                    this.mazeCells.get(mirroredPlace).addWall();
                }
            }
            if ((gameFieldSize.width % 2) == 1) {
                for (Point place : walls) {
                    if (place.x == halfFieldWidth - 1) {
                        Point middlePlace = new Point(halfFieldWidth, place.y);
                        this.mazeCells.get(middlePlace).addWall();
                    }
                }
            }
        } else {
            for (Point place : walls) {
                this.mazeCells.get(place).addWall();
            }
        }
        for (Point cell : this.mazeCells.keySet()) {
            if ((this.mazeCells.get(cell).isMovable())
                    && (!Arrays.asList(startLocations).contains(cell))
                    && (!Arrays.asList(pillets).contains(cell))) {
                this.mazeCells.get(cell).addFood();
                this.foodLocation.add(cell);
            }
        }
        pilletsLocation.addAll(Arrays.asList(pillets));
        for (Point unit : pillets) {
            this.mazeCells.get(unit).addPillet();
        }
        //</editor-fold>
    }

    /**
     * Отдельная клетка лабиринта.
     */
    private class MazeCell {//<editor-fold defaultstate="collapsed">
        
        private final List<CellContent> content = new ArrayList<CellContent>();

        /**
         * Конструктор.
         *
         * @param cellType код типа содержимого клетки
         */
        public MazeCell() {
            content.add(CellContent.PASSAGE);
        }

        private void addWall() {
            getContent().remove(CellContent.PASSAGE);
            getContent().add(CellContent.WALL);
        }

        private void addFood() {
            getContent().add(CellContent.FOOD);
        }

        private void removeFood() {
            getContent().remove(CellContent.FOOD);
        }

        private void addPillet() {
            getContent().add(CellContent.PILLET);
        }

        private void removePillet() {
            getContent().remove(CellContent.PILLET);
        }

        /**
         * @return может ли игрок находиться в клетке
         */
        private boolean isMovable() {
            return getContent().contains(CellContent.PASSAGE);
        }

        private List<CellContent> getContent() {
            return content;
        }
        //</editor-fold>
    }

    //--------------------------------------------------------- Операции с едой
    
    /**
     * Возвращает перечень элементов еды на игровом поле.
     * 
     * @return перечень элементов еды на игровом поле
     */
    public Set<Point> getFood() {
        return Collections.unmodifiableSet(foodLocation);
    }

    /**
     * Возвращает перечень магических таблеток на игровом поле.
     * 
     * @return перечень магических таблеток на игровом поле
     */
    public Set<Point> getPillets() {
        return Collections.unmodifiableSet(pilletsLocation);
    }

    /**
     * Обновить перечень элементов еды.
     * 
     * В текущем перечне еды оставляет только те элементы, которые есть в
     * переданном перечне. Соответствующим образом обновляет содержимое клеток.
     * 
     * @param freshFoodList новый перечень элементов еды
     */
    public void refreshFood(Collection<Point> freshFoodList) {
        if (!freshFoodList.isEmpty()) {
            Set<Point> difference = new HashSet<Point>(foodLocation);
            difference.removeAll(freshFoodList);
            for (Point location : difference) {
                mazeCells.get(location).removeFood();
                foodLocation.remove(location);
            }
        } else {
            foodLocation.clear();
        }
    }

    /**
     * Обновить перечень магических таблеток.
     * 
     * В текущем перечне таблеток оставляет только те элементы, которые есть в
     * переданном перечне. Соответствующим образом обновляет содержимое клеток.
     * 
     * @param freshPilletsList новый перечень магических таблеток
     */
    public void refreshPillets(Collection<Point> freshPilletsList) {
        if (!freshPilletsList.isEmpty()) {
            Set<Point> difference = new HashSet<Point>(pilletsLocation);
            difference.removeAll(freshPilletsList);
            for (Point location : difference) {
                mazeCells.get(location).removePillet();
                pilletsLocation.remove(location);
            }
        } else {
            // Нельзя делать через .clear, потому что игра не завершается
            // после съедения всех таблеток
            for (MazeCell cell : mazeCells.values()) {
                if (cell.getContent().contains(CellContent.PILLET)) {
                    cell.removePillet();
                }
            }
        }
    }
    
    //------------------------------------------ Операции интерфейса IGameField
    
    @Override
    public Point[] getCells() {
        return mazeCells.keySet().toArray(new Point[mazeCells.size()]);
    }

    @Override
    public List<CellContent> getCellContent(Point location) {
        List<CellContent> content = mazeCells.get(location).getContent();
        return Collections.unmodifiableList(content);
    }

    @Override
    public boolean isCellMovable(Point cellCoordinate) {
        if (cellCoordinate == null) {
            return false;
        }
        if (mazeCells.containsKey(cellCoordinate) != true) {
            return false;
        }
        return mazeCells.get(cellCoordinate).isMovable();
    }
}
