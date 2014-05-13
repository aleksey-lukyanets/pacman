package pacman.launch;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JFrame;
import pacman.game.GameController;
import pacman.model.AbstractGameModel;
import pacman.model.GameFieldMap;
import pacman.model.GameModelImp;
import pacman.view.GameView;

/**
 * Сборщик игры.
 * <p>
 * Создаёт и подключает модель, вид, контроллер. После создания можно запросить
 * размер игрового поля в пикселах и ссылку на контроллер игры.
 */
public class GameAssembler {

    private final AbstractGameModel model;
    private final GameView view;
    private final GameController controller;

    //<editor-fold defaultstate="collapsed" desc="Объявление топологии лабиринта">
    private final Dimension gameCanvasSize = new Dimension(20, 11);
    private final Point[] startLocations = new Point[]{new Point(9, 9), new Point(9, 5), new Point(10, 5), new Point(8, 5), new Point(11, 5)};  // Исходная позиция привидения
    private final Point[] initialWallsLocation = new Point[]{
        new Point(2, 2), new Point(2, 3), new Point(2, 4), new Point(2, 6), new Point(2, 7), new Point(2, 8),
        new Point(3, 2), new Point(3, 8),
        new Point(4, 4), new Point(4, 6),
        new Point(5, 1), new Point(5, 2), new Point(5, 4), new Point(5, 6), new Point(5, 8), new Point(5, 9),
        new Point(7, 2), new Point(7, 4), new Point(7, 5), new Point(7, 6), new Point(7, 8),
        new Point(8, 2), new Point(8, 4), new Point(8, 6), new Point(8, 8),
        new Point(9, 2), new Point(9, 6), new Point(9, 8)
    };
    private final Point[] pilletsLocations = new Point[]{new Point(1, 1), new Point(1, 9), new Point(18, 1), new Point(18, 9)};
    //</editor-fold>
    
    private final GameFieldMap gameMap;

    /**
     * Создаёт новый сборщик игры.
     *
     * @param frame фрейм для размещения графического представления
     * @param contentPane контейнер
     * @param cellSize размер клетки игрового поля в пикселах
     */
    public GameAssembler(JFrame frame, Container contentPane, int cellSize) {
        gameMap = new GameFieldMap(gameCanvasSize, cellSize, startLocations, initialWallsLocation, pilletsLocations);
        model = new GameModelImp(gameMap);
        
        view = new GameView();
        view.setModel(model);

        controller = new GameController(model, view, frame, contentPane);
        view.setController(controller);
        
        model.addObserver(view);
        model.addObserver(controller);
    }

    /**
     * Возвращает размер игрового поля, в пикселах.
     *
     * @return размер игрового поля, в пикселах
     */
    public Dimension getGameCanvasSize() {
        return gameCanvasSize;
    }
}
