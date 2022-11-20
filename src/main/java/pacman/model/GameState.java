package pacman.model;

import algorithms.core.IExtendedState;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import pacman.game.IAction;
import pacman.game.IGameField;

/**
 * Класс состояния игры.
 * <p>
 * Хранит и управляет изменяемыми элементами игры: расположение игроков и еды.
 * Поддерживает создание копий, что позволяет использовать класс в поисковых
 * алгоритмах для расчёта действий игроков.
 * <p>
 * Через интерфейс {@link algorithms.IExtendedState} поисковый алгоритм
 * производит манипулирование "теоретическими" копиями состояния игры для
 * вычисления оптимального действия игрока. Когда оптимальное действие найдено,
 * модель игры {@link GameModel} запрашивает изменение своего экземпляра
 * состояния через функцию {@link performPlayerAction}. Состояние игры применяет
 * действие и рассчитывает его последствия, после чего модель игры должна
 * получить у экземпляра состояния новое положение игрока, обновлённое множество
 * единиц еды и, при необходимости, узнать, закончилась ли игра победой или
 * поражением.
 * <p>
 * Важно обратить внимание, что данные о местоположении игроков и расположении
 * еды дублируются в этом классе, поскольку эти сведения необходимы для
 * алгоритмических расчётов и могут меняться в процессе расчёта (вплоть до
 * опустошения). Именно поэтому актуальное расположение игроков и еды должно
 * быть получено из экземпляра этого класса после запроса на передвижение
 * реального игрока.
 *
 * @param <T> тип действий, предпринимаемых игроками
 */
public class GameState<T extends IAction> implements IExtendedState<T> {

    private static final int CONFUSED_STEPS = 20;
    
    private final IGameField myMaze;
    private final int totalGhosts;
    private final int pacmanId;
    private final Set<T> gameFieldActions;
    private final ScoreCounter scoreCounter;
    private final Set<Point> foodLocation;                          // Координаты единиц еды
    private final Set<Point> pilletsLocation;                          //
    private final HashMap<Integer, Point> playersLocation;
    private final HashMap<Integer, Boolean> isGhostConfused;
    private final List<Point> playerStarts;
    
    private int actionsTillConfusionEnd = 0;
    private boolean gameWon = false;                                       // Признак победы Пакмана
    private boolean gameLost = false;                                         // Признак поражения Пакмана

    /**
     * Создаёт новое состояние игры.
     * 
     * Переданными в конструктор данными инициализируются собственная внутренняя
     * копия списка элементов еды и массив расположения игроков.
     *
     * @param maze используемое игровое поле
     * @param totalGhosts
     * @param playerStarts расположение игроков
     * @param pacmanIndex индекс Пакмана
     * @param food перечень расположения элементов еды
     * @param pillets
     * @param possibleActions перечень возможных действий игроков
     */
    public GameState(IGameField maze, int totalGhosts, Point[] playerStarts, int pacmanIndex, Collection<Point> food, Collection<Point> pillets, Collection<T> possibleActions) {
        myMaze = maze;
        this.totalGhosts = totalGhosts;
        this.playerStarts = new ArrayList<Point>(Arrays.asList(playerStarts));
        this.pacmanId = pacmanIndex;
        gameFieldActions = new HashSet(possibleActions);
        scoreCounter = new ScoreCounter();
        foodLocation = new HashSet<Point>(food);
        pilletsLocation = new HashSet<Point>(pillets);
        
        isGhostConfused = new HashMap<Integer, Boolean>();
        for (int id = 0; id < (totalGhosts + 1); id++) {
            if (id != pacmanIndex) {
                isGhostConfused.put(id, Boolean.FALSE);
            }
        }

        playersLocation = new HashMap<Integer, Point>();
        for (int id = 0; id < (totalGhosts + 1); id++) {
            playersLocation.put(id, playerStarts[id]);
        }
    }

    /**
     * Конструктор для создания копии.
     *
     * @param prototype копируемое состояние игры
     */
    public GameState(GameState prototype) {
        this.myMaze = prototype.myMaze;
        this.totalGhosts = prototype.totalGhosts;
        this.playerStarts = new ArrayList<Point>(prototype.playerStarts);
        this.pacmanId = prototype.pacmanId;
        this.gameFieldActions = new HashSet<T>(prototype.gameFieldActions);
        this.scoreCounter = new ScoreCounter(prototype.scoreCounter);
        this.foodLocation = new HashSet<Point>(prototype.foodLocation);
        this.pilletsLocation = new HashSet<Point>(prototype.pilletsLocation);
        this.playersLocation = new HashMap<Integer, Point>(prototype.playersLocation);
        this.isGhostConfused = new HashMap<Integer, Boolean>(prototype.isGhostConfused);
        this.actionsTillConfusionEnd = prototype.actionsTillConfusionEnd;
        this.gameWon = prototype.gameWon;
        this.gameLost = prototype.gameLost;
    }

    /**
     * Эвристическая оценка состояния игры.
     */
    private class ScoreCounter {//<editor-fold defaultstate="collapsed">
        
        private static final int WIN_SCORE = +500;                                           // 
        private static final int LOSE_SCORE = -500;                                           // 
        private static final int FOOD_SCORE = +5;                                           // Очки за одну единицу еды
        private static final int PILLET_SCORE = +10;                                           // 
        private static final int GHOST_SCORE = +50;                                           // 
        private static final int STEP_PINALTY = -1;                                           // Штраф за каждое передвижение
        private int stepsDone = 0;                                                  // Количество сделанных шагов
        private int foodUnitsEaten = 0;                                             // Количество съеденных единиц еды
        private int magicPilletsEaten = 0;                                                  // Количество сделанных шагов
        private int confusedGhostsEaten = 0;                                                  // Количество сделанных шагов

        public ScoreCounter() {
        }

        private ScoreCounter(ScoreCounter e) {
            this.foodUnitsEaten = e.foodUnitsEaten;
            this.stepsDone = e.stepsDone;
        }

        public void incrementStepsDone() {
            stepsDone++;
        }

        public void incrementEatenFood() {
            foodUnitsEaten++;
        }

        private void incrementEatenPillets() {
            magicPilletsEaten++;
        }

        private void incrementEatenGhosts() {
            confusedGhostsEaten++;
        }
        
        /**
         * Вычисление счёта в игре - оценочная функция.
         *
         * Позволяет Пакману сравнивать степень полезности возможных действий.
         *
         * @return значение оценочной функции
         */
        public float getGameScore() {
            float evaluation = 0;

            if (isWin()) {
                evaluation += WIN_SCORE;
            }

            if (isLose()) {
                evaluation += LOSE_SCORE;
            }
            
            return (evaluation + STEP_PINALTY * stepsDone
                    + FOOD_SCORE * foodUnitsEaten
                    + PILLET_SCORE * magicPilletsEaten
                    + GHOST_SCORE * confusedGhostsEaten);
        }
        
        /**
         * Вычисление счёта в игре - оценочная функция.
         *
         * Позволяет Пакману сравнивать степень полезности возможных действий.
         *
         * @return значение оценочной функции
         */
        public float getPacmanEvaluation() {
            float evaluation = 0;
            
            evaluation += getWeightedDistanceToNearestFood(2);
            evaluation += getWeightedSumDistancesToFood(1);
            evaluation -= getWeightedSumDistancesToGhosts(20);
            evaluation += getWeightedDistanceToNearestConfused(5);
            
//            if (foodLocation.size() == 1) {
//                Point p = foodLocation.iterator().next();
//                evaluation += 100500 / getManhattanDistance(getPlayerLocation(pacmanId), p);
//            }

//            if (isWin()) {
//                evaluation += WIN_SCORE;
//            }
//
//            if (isLose()) {
//                evaluation -= LOSE_SCORE;
//            }

            return evaluation;
        }

        private float getWeightedSumDistancesToFood(int weight) {//<editor-fold defaultstate="collapsed">
            
            float funcTotalFood = 0;
            
            int totalFood = foodLocation.size();
            if (totalFood != 0) {
                funcTotalFood = weight / totalFood;
            }
            
            return funcTotalFood;//</editor-fold>
        }
        
        private float getWeightedDistanceToNearestFood(int weight) {//<editor-fold defaultstate="collapsed">
            float funcNearestFood = 0;
            
            ArrayList<Integer> distance = new ArrayList<Integer>();
            for (Point food : foodLocation) {
                distance.add(getManhattanDistance(getPlayerLocation(pacmanId), food));
            }
            float minDistance = Float.MAX_VALUE;
            for (int i = 0; i < distance.size(); i++) {
                if (distance.get(i) < minDistance) {
                    minDistance = distance.get(i);
                }
            }
            if (minDistance != 0) {
                funcNearestFood = weight / minDistance;
            }
            
            return funcNearestFood;//</editor-fold>
        }
        
        private float getWeightedSumDistancesToGhosts(int weight) {//<editor-fold defaultstate="collapsed">
            float funcGhost;
            
            int ghostDistance = 0;
            Point pacmanLocation = getPlayerLocation(pacmanId);
            for (int ghostId = 0; ghostId < getPlayersNumber(); ghostId++) {
                if (ghostId != pacmanId) {
                    if (isGhostConfused.get(ghostId) == Boolean.FALSE) {
                        ghostDistance += getManhattanDistance(pacmanLocation, getPlayerLocation(ghostId));
                    }
                }
            }
            if ((ghostDistance < 7) && (ghostDistance != 0)) {
                funcGhost = weight / ghostDistance;
            } else {
                funcGhost = 0;
            }
            
            return funcGhost;//</editor-fold>
        }
        
        private float getWeightedDistanceToNearestConfused(int weight) {//<editor-fold defaultstate="collapsed">
            float funcNearestConfused = 0;
            
            TreeSet<Integer> distance = new TreeSet<Integer>();
            for (int ghostId : playersLocation.keySet()) {
                if (ghostId != pacmanId) {
                    if (isGhostConfused.get(ghostId) == Boolean.TRUE) {
                        distance.add(getManhattanDistance(
                                getPlayerLocation(pacmanId),
                                getPlayerLocation(ghostId)));
                    }
                }
            }
            if (!distance.isEmpty()) {
                funcNearestConfused = weight / distance.first();
            }
            
            return funcNearestConfused;//</editor-fold>
        }
        //</editor-fold>
    }

    /**
     * Возвращает значение манхеттенского расстояния между координатами
     * {@literal from} и {@literal to}.
     *
     * @param from координаты первой клетки, счёт от нуля
     * @param to координаты второй клетки, счёт от нуля
     * @return значение манхеттенского расстояния
     */
    public static int getManhattanDistance(Point from, Point to) {
        return (Math.abs(from.x - to.x) + Math.abs(from.y - to.y));
    }

    /**
     * Обрабатывает запрос на действие игрока с последующим расчётом последствий
     * и внесением соответствующих изменений в текущую модель игры.
     * 
     * @param playerId индетификатор игрока
     * @param action действие игрока
     * @param mediator интерфейс внесения изменений в модель игры
     */
    public void performPlayerAction(int playerId, T action, IModelDataMediator mediator) {
        performActionConsequences(playerId, action, true, mediator);
    }
    
    /**
     * Применяет действие игрока и рассчитывает его последствия.
     * 
     * В зависимости от значения {@literal modifyGameModel} вносит или не вносит
     * (для теоретических расчётов) изменения в текущую модель игры.
     *
     * @param playerId идентификатор игрока
     * @param action действие игрока
     * @param modifyGameModel необходимость внесения изменений в модель игры
     * @param mediator интерфейс внесения изменений в модель игры
     */
    private void performActionConsequences(int playerId, T action, boolean modifyGameModel, IModelDataMediator mediator) {
        //<editor-fold defaultstate="collapsed">
        
        // Изменение местоположения игрока
        if (action != null) {
            Point currentLocation = getPlayerLocation(playerId);
            Point newLocation = action.getLocationAfterAction(currentLocation);
            playersLocation.put(playerId, newLocation);
            if (modifyGameModel) mediator.setPlayerLocation(playerId, newLocation);
        }
        
        // Пакман настигнут привидением
        if (isPacmanCought()) {
            gameLost = true;
            return;
        }
        
        // Последствия хода Пакмана
        if (playerId == pacmanId) {
            scoreCounter.incrementStepsDone();
            
            // Съеден элемент еды
            if (foodLocation.contains(getPlayerLocation(playerId))) {
                foodLocation.remove(getPlayerLocation(playerId));
                scoreCounter.incrementEatenFood();
            }
            
            // Съедена магическая таблетка
            if (pilletsLocation.contains(getPlayerLocation(playerId))) {
                pilletsLocation.remove(getPlayerLocation(playerId));
                scoreCounter.incrementEatenPillets();
                for (int ghostId : isGhostConfused.keySet()) {
                    isGhostConfused.put(ghostId, Boolean.TRUE);
                    actionsTillConfusionEnd = CONFUSED_STEPS;
                }
            }
        }
        
        // Съедено контуженное привидение
        int ghostId = getEatenGhostId();
        if (ghostId != pacmanId) {
            if (isGhostConfused.get(ghostId)) {
                scoreCounter.incrementEatenGhosts();
                isGhostConfused.put(ghostId, Boolean.FALSE);
                playersLocation.put(ghostId, playerStarts.get(ghostId));
                if (modifyGameModel) {
                    mediator.setConfusedGhostEaten(ghostId);
                }
            }
        }
        //</editor-fold>
    }

    /**
     * @return <code>true</code>, если Пакман настигнут приведением
     */
    private boolean isPacmanCought() {
        Point pacmanLocation = getPlayerLocation(pacmanId);
        for (int ghostId : playersLocation.keySet()) {
            if (ghostId != pacmanId) {
                if ((isGhostConfused.get(ghostId) == Boolean.FALSE)
                        && (getPlayerLocation(ghostId).equals(pacmanLocation))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return <code>this.pacmanId</code>, если привидение не было съедено,
     * иначе - идентификатор съеденного Пакманом контуженного привидения
     */
    private int getEatenGhostId() {
        int playerId = pacmanId;
        Point pacmanLocation = getPlayerLocation(pacmanId);
        for (int ghostId : playersLocation.keySet()) {
            if (ghostId != pacmanId) {
                if ((isGhostConfused.get(ghostId) == Boolean.TRUE)
                        && (getPlayerLocation(ghostId).equals(pacmanLocation))) {
                    playerId = ghostId;
                }
            }
        }
        return playerId;
    }
    
    /**
     * @param playerId идентификатор игрока
     * @return местоположение игрока с заданным идентификатором
     */
    private Point getPlayerLocation(int playerId) {
        return playersLocation.get(playerId);
    }
    
    /**
     * Обрабатывает запрос на расчёт последствий одного тура игры, с внесением
     * соответствующих изменений в текущую модель игры.
     * 
     * @param mediator интерфейс внесения изменений в модель игры
     */
    public void performTurnFinished(IModelDataMediator mediator) {
        if (actionsTillConfusionEnd == 0) {
            for (int ghostId : isGhostConfused.keySet()) {
                isGhostConfused.put(ghostId, Boolean.FALSE);
            }
        } else {
            actionsTillConfusionEnd--;
        }
        
        if (foodLocation.isEmpty()) {
            gameWon = true;
        }
        
        mediator.refreshFood(Collections.unmodifiableSet(foodLocation));
        mediator.refreshPillets(Collections.unmodifiableSet(pilletsLocation));
        mediator.refreshGhostsConfused(Collections.unmodifiableMap(isGhostConfused));
    }

    //--------------------------------------------------- Методы IExtendedState
    
    @Override
    public boolean isWin() {
        return gameWon;
    }

    @Override
    public boolean isLose() {
        return gameLost;
    }

    @Override
    public int getPlayersNumber() {
        return (totalGhosts + 1);
    }

    @Override
    public GameState getSuccessorState(int playerId, T action) {
        GameState newState = new GameState(this);
        newState.performActionConsequences(playerId, action, false, null);
        return newState;
    }

    @Override
    public List<T> getLegalActions(int playerId) {
        List<T> actions = new ArrayList<T>();
        Point place = getPlayerLocation(playerId);
        for (T action : gameFieldActions) {
            Point destination = action.getLocationAfterAction(place);
            if (myMaze.isCellMovable(destination)) {
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    public float getGameScore() {
        return scoreCounter.getGameScore();
    }

    @Override
    public float getPacmanEvaluation() {
        return scoreCounter.getPacmanEvaluation();
    }

    @Override
    public LinkedHashMap<Point, T> getLegalActionsAsMap(Point place) {
        LinkedHashMap<Point, T> s = new LinkedHashMap<Point, T>();
        for (T action : gameFieldActions) {
            Point destination = action.getLocationAfterAction(place);
            if (myMaze.isCellMovable(destination)) {
                s.put(destination, action);
            }
        }
        return s;
    }

    @Override
    public LinkedHashMap<Point, T> getLegalActionsAsMapNoKins(Point place, int playerId) {
        LinkedHashMap<Point, T> s = new LinkedHashMap<Point, T>();
        for (T action : gameFieldActions) {
            Point destination = action.getLocationAfterAction(place);
            if (isDestinationMovable(destination)) {
                s.put(destination, action);
            }
        }
        return s;
    }

    /**
     * @param destination координата клетки игрового поля
     * @return <code>true</code>, если клетка является проходимой и в ней не
     * находится ни одного привидения
     */
    private boolean isDestinationMovable(Point destination) {
        return myMaze.isCellMovable(destination) && !isOccupiedByGhosts(destination);
    }

    /**
     * @param location координата клетки игрового поля
     * @return <code>true</code>, если в клетке нет ни одного привидения
     */
    private boolean isOccupiedByGhosts(Point location) {
        ArrayList<Point> ghostsLocations = new ArrayList<Point>();
        for (int playerId : playersLocation.keySet()) {
            if (playerId != pacmanId) {
                ghostsLocations.add(playersLocation.get(playerId));
            }
        }
        return ghostsLocations.contains(location);
    }
}
