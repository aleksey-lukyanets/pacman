package pacman.model;

import algorithms.core.ISearchAlgorithm;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import pacman.game.IAction;
import pacman.game.IAnimatedGameModel;
import pacman.game.IAnimatedPlayer;
import pacman.game.IControlableGameModel;
import pacman.game.IGameField;

/**
 * Обобщённая модель игры.
 * <p>
 * Реализует интерфейсы взаимодействия с контроллером и графическим
 * представлением игры. Описывает и инициализирует обобщённые данные и методы
 * модели. Детали реализации конкретной модели игры определяются подклассами.
 * <p>
 * Реализация:<ol>
 * <li> Инициализировать список игроков в {@link reinitializePlayers};
 * <li> Рассчитывать и исполнять ход Пакмана в {@link performPacmanAction};
 * <li> Рассчитывать и исполнять ходы привидений вслед за ходом Пакмана;
 * <li> Уведомлять в момент хода Пакмана об исполнении им всех действий через
 * {@link setPacmanActionsDone};
 * <li> Уведомлять об изменении данных модели через {@link reportChanged}.</ol>
 */
public abstract class AbstractGameModel
        extends Observable
        implements IAnimatedGameModel, IControlableGameModel {

    protected final GameFieldMap gameFieldMap;
    protected final List<Player> players = new ArrayList<Player>();
    protected GameState<IAction> gameState;
    protected Maze myMaze;
    protected int totalGhosts = 1;
    
    private final List<Point> locationsPool = new ArrayList<Point>();
    private boolean pacmanActionsDone = true;

    /**
     * Создаёт новую обобщённую модель игры.
     * 
     * @param field карта игрового поля
     */
    public AbstractGameModel(GameFieldMap field) {
        this.gameFieldMap = field;
        for (int i = 0; i < field.getGameFieldSize().getWidth(); i++) {
            for (int j = 0; j < field.getGameFieldSize().getHeight(); j++) {
                locationsPool.add(new Point(i, j));
            }
        }
        NonstopAction.setLocationsPoll(locationsPool);
    }

    /**
     * Уведомляет наблюдателей об изменении состояния.
     */
    protected void reportChanged() {
        setChanged();
        notifyObservers();
        clearChanged();
    }

    /**
     * Уведомляет об исполнении Пакманом всех действий.
     */
    protected void setPacmanActionsDone() {
        this.pacmanActionsDone = true;
        reportChanged();
        this.pacmanActionsDone = false;
    }

    //------------------------------------------------- Переопределяемые методы
    
    /**
     * Повторно инициализирует список игроков <code>players</code>.
     * 
     * @return индекс игрока-Пакмана в списке <code>players</code>
     */
    protected abstract int reinitializePlayers();
    
    @Override
    public abstract void performPacmanAction(ISearchAlgorithm algorithm, Point goal);
    
    //----------------------------- Реализация интерфейса IControlableGameModel

    @Override
    public void reinitializeGame() {
        int pacmanId = reinitializePlayers();
        myMaze = new Maze(gameFieldMap, locationsPool, true);
        gameState = new GameState<IAction>(myMaze, totalGhosts, gameFieldMap.getStartLocations(), pacmanId, myMaze.getFood(), myMaze.getPillets(), getPossibleActions());
        setPacmanActionsDone();
    }

    @Override
    public void setGhostsNumber(int ghostsNumber) {
        totalGhosts = ghostsNumber;
    }

    @Override
    public boolean isGameComplete() {
        return gameState.isWin() || gameState.isLose();
    }

    @Override
    public Point getCellAddress(Point coordinate) {
        return new Point(coordinate.x / getMazeCellSize(), coordinate.y / getMazeCellSize());
    }
    
    //------------------------------------ Реализация интерфейса IAnimatedGameModel
 
    @Override
    public Dimension getGameCanvasDimension() {
        return gameFieldMap.getGameCanvasDimension();
    }

    @Override
    public int getMazeCellSize() {
        return gameFieldMap.getCellSize();
    }

    @Override
    public boolean isGameLost() {
        return gameState.isLose();
    }

    @Override
    public boolean isGameWon() {
        return gameState.isWin();
    }

    @Override
    public IGameField getMaze() {
        return myMaze;
    }

    @Override
    public IAnimatedPlayer[] getPlayers() {
        return players.toArray(new IAnimatedPlayer[players.size()]);
    }

    @Override
    public List<IAction> getPossibleActions() {
        return NonstopAction.getPossibleActions();
    }

    @Override
    public List<IAction[]> getCorners() {
        return NonstopAction.getCorners();
    }
    
    @Override
    public boolean isPacmanQueueEmpty() {
        return pacmanActionsDone;
    }
}
