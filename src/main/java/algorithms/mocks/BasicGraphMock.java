package algorithms.mocks;

import algorithms.core.IBasicState;
import java.awt.Point;
import java.util.LinkedHashMap;

/**
 * Класс для имитации графа игровых позиций.
 * <p>
 * Имитирует граф игровых позиций (не путать с состояниями), в котором позиции
 * соответствуют вершинам переданного графа {@link ManualGraphMock}.
 * Предназначен для тестирования алгоритмов, использующих интерфейс состояния
 * {@link algorithms.core.IBasicState} (e.g.
 * {@link algorithms.core.BreadthFirstSearch}).
 *
 * @param <T> тип действий, предпринимаемых игроками
 */
public class BasicGraphMock<T> implements IBasicState<T> {

    private final ManualGraphMock graph;

    /**
     * Создаёт новый граф.
     *
     * @param graph
     */
    public BasicGraphMock(ManualGraphMock graph) {
        this.graph = graph;
    }

    /**
     * Конструктор для клонирования.
     *
     * @param bg клонируемый объект
     */
    public BasicGraphMock(BasicGraphMock bg) {
        this.graph = bg.graph;
    }

    @Override
    public LinkedHashMap<Point, T> getLegalActionsAsMap(Point place) {
        return graph.getLegalActions(place);
    }

    @Override
    public LinkedHashMap<Point, T> getLegalActionsAsMapNoKins(Point place, int playerId) {
        return graph.getLegalActions(place);
    }
}
