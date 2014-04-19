package algorithms.mocks;

import algorithms.core.IExtendedState;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 * Класс для имитации графа игровых состояний.
 * <p>
 * Имитирует граф игровых состояний (не путать с позициями), в котором состояния
 * соответствуют вершинам переданного графа {@link ManualGraphMock}.
 * Предназначен для тестирования алгоритмов, использующих интерфейс состояния
 * {@link algorithms.core.IExtendedState} (e.g.
 * {@link algorithms.core.Minimax}).
 *
 * @param <T> тип действий, предпринимаемых игроками
 */
public class ExtendedGraphMock<T> implements IExtendedState<T> {

    private static final Set<String> lastClosedNodes = new TreeSet<String>();

    private final int totalPlayers;
    private final int humanId;
    private final ManualGraphMock<T, String> statesGraph;       // Граф игровых состояний
    private final Map<String, Float> statesScores;              // Счёт игры в состояниях
    private final List<String> isWin;                           // Список выигрышных для humanId состояний
    private final List<String> isLose;                          // Список проигрышных для humanId состояний

    private String markedState;                                 // Маркер, передвигаемый между состояниями в процессе поиска решения

    /**
     * Создаёт новый имитатор графа игровых состояний.
     * 
     * @param graph граф состояний
     * @param startState начальное расположение
     * @param totalPlayers количество игроков
     * @param pacmanId идентификатор игрока, который управляет человек
     */
    public ExtendedGraphMock(ManualGraphMock graph, String startState, int totalPlayers, int pacmanId) {
        this.statesGraph = graph;
        this.humanId = pacmanId;
        this.totalPlayers = totalPlayers;
        isWin = new ArrayList<String>();
        isLose = new ArrayList<String>();
        statesScores = new HashMap<String, Float>();
        markedState = startState;
    }

    /**
     * Конструктор для клонирования.
     *
     * @param prototype клонируемый объект
     */
    public ExtendedGraphMock(ExtendedGraphMock prototype) {
        this.statesGraph = prototype.statesGraph;
        this.humanId = prototype.humanId;
        this.isWin = new ArrayList<String>(prototype.isWin);
        this.isLose = new ArrayList<String>(prototype.isLose);
        this.statesScores = new HashMap<String, Float>(prototype.statesScores);
        this.totalPlayers = prototype.totalPlayers;
        this.markedState = prototype.markedState;
    }

    /**
     * Возвращает перечень вершин графа, открытых при последнем поиске.
     * 
     * @return перечень вершин графа, открытых при последнем поиске
     */
    public static final Set<String> getLastClosedNodes() {
        return lastClosedNodes;
    }

    /**
     * Очищает перечень вершин графа, открытых при последнем поиске.
     */
    public static final void clearLastClosedNodes() {
        lastClosedNodes.clear();
    }

    /**
     * Добавляет дополнительные параметры вершины графа (состояния игры).
     * 
     * Параметры <code>isWin</code> и <code>isLose</code> являются
     * взимоисключающими, не будут установлены в <code>true</code> одновременно.
     * 
     * @param node вершина графа (состояние игры)
     * @param isWin состояние является победой humanId
     * @param isLose состояние является проигрышем humanId
     * @param gameScore объективный счёт игры
     */
    public void addNodeParams(String node, boolean isWin, boolean isLose, float gameScore) {
        if (statesGraph.isNodeExist(node)) {
            this.statesScores.put(node, gameScore);
            if (isWin) {
                this.isWin.add(node);
            } else if (isLose) {
                this.isLose.add(node);
            }
        }
    }
    
    //------------------------------------------------------- Методы интерфейса

    @Override
    public List<T> getLegalActions(int playerId) {
        List<T> actions = new ArrayList<T>();
        LinkedHashMap<String, T> actionPairs = statesGraph.getLegalActions(markedState);
        Iterator<Entry<String, T>> it = actionPairs.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, T> entry = it.next();
            actions.add(entry.getValue());
        }
        return actions;
    }

    @Override
    public IExtendedState getSuccessorState(int playerId, T action) {
        ExtendedGraphMock newState = new ExtendedGraphMock(this);
        lastClosedNodes.add(markedState);
        newState.performPlayerAction(playerId, action);
        return newState;
    }

    /**
     * Расчёт и применение последствий действия игрока.
     * В имитаторе последствием считается переход в состояние, определённое
     * графом <code>statesGraph</code>. Переход обозначается переносом маркера
     * <code>markedState</code>.
     * @param playerId идентификатор игрока
     * @param action действие игрока
     */
    private void performPlayerAction(int playerId, T action) {
        String newState = null;
        LinkedHashMap<String, T> actionPairs = statesGraph.getLegalActions(markedState);
        Iterator<Entry<String, T>> it = actionPairs.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, T> entry = it.next();
            if (entry.getValue().equals(action)) {
                newState = entry.getKey();
                break;
            }
        }
        markedState = newState;
    }

    @Override
    public int getPlayersNumber() {
        return totalPlayers;
    }

    @Override
    public boolean isWin() {
        if (isWin.contains(markedState)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isLose() {
        if (isLose.contains(markedState)) {
            return true;
        }
        return false;
    }

    @Override
    public float getGameScore() {
        if (statesScores.containsKey(markedState)) {
            lastClosedNodes.add(markedState);
            return statesScores.get(markedState);
        }
        return 0;
    }

    @Override
    public float getPacmanEvaluation() {
        return getGameScore();
    }

    @Override
    public LinkedHashMap<Point, T> getLegalActionsAsMap(Point place) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LinkedHashMap<Point, T> getLegalActionsAsMapNoKins(Point place, int playerId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
