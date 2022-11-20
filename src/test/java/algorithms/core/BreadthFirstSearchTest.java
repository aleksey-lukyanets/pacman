package algorithms.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import algorithms.mocks.BasicGraphMock;
import algorithms.mocks.ManualGraphMock;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Алексей
 */
public class BreadthFirstSearchTest {

    public BreadthFirstSearchTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        System.out.println("BreadthFirstSearch:");
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    private enum TestActions {

        FIRST, SECOND, THIRD;
    }

    /**
     * Тест на различие поиска в ширину и глубину (BFS vs DFS).
     *
     * Проверяет результат поиска пути по тестовому графу. Проверка перечня
     * открытых в процессе поиска пути вершин графа выявляет, действительно ли
     * применяется поиск в ширину (при условии, что путь найден верно).
     *
     * Тестовый граф, *A - начало, G - цель:
     *
     * /-- B | ^ | | | *A -->[G] | | ^ | V | \-->D ----/
     */
    @Test
    public void testBfsVsDfs() {
        System.out.println("    - Тест на различие поиска в ширину и глубину (BFS vs. DFS)");
        
        BreadthFirstSearch<TestActions> bfs = new BreadthFirstSearch<TestActions>();

        // Создание тестового графа
        Point B = new Point(0, 0);
        Point A = new Point(0, 1);
        Point D = new Point(0, 2);
        Point G = new Point(1, 1);
        ManualGraphMock<TestActions, Point> graph = new ManualGraphMock<TestActions, Point>(new Point[]{A, B, D, G});
        graph.addAction(A, B, TestActions.FIRST);
        graph.addAction(A, G, TestActions.SECOND);
        graph.addAction(A, D, TestActions.THIRD);
        graph.addAction(B, D, TestActions.FIRST);
        graph.addAction(D, G, TestActions.FIRST);
        BasicGraphMock<TestActions> bfsVsDfsGraph = new BasicGraphMock<TestActions>(graph);
        
        // Проверка пути
        ArrayList<TestActions> expResult = new ArrayList<TestActions>();
        expResult.add(TestActions.SECOND);
        assertEquals(expResult,
                bfs.getSolution(bfsVsDfsGraph, A, G),
                "Неверный путь к цели.");

        // Проверка перечня открытых вершин графа
        Set<Point> expClosedNodes = new HashSet<Point>();
        expClosedNodes.add(A);
        expClosedNodes.add(B);
        assertEquals(expClosedNodes,
                bfs.getLastClosedNodes(),
                "Неверный порядок обхода вершин.");
    }

    /**
     * Тест на поиск в циклическом графе.
     *
     * Проверяет найденный путь и порядок обхода вершин графа.
     *
     * Тестовый граф, *A - начало, G - цель:
     *
     * B <--> C ^ /| | / | V / V *A<-/ [G]
     */
    @Test
    public void testCyclicGraph() {
        System.out.println("    - Тест на поиск в циклическом графе");
        
        BreadthFirstSearch<TestActions> bfs = new BreadthFirstSearch<TestActions>();

        // Создание тестового графа
        Point B = new Point(0, 0);
        Point A = new Point(0, 1);
        Point C = new Point(1, 0);
        Point G = new Point(1, 1);
        ManualGraphMock<TestActions, Point> graph = new ManualGraphMock<TestActions, Point>(new Point[]{A, B, C, G});
        graph.addAction(A, B, TestActions.FIRST);
        graph.addAction(B, A, TestActions.FIRST);
        graph.addAction(B, C, TestActions.SECOND);
        graph.addAction(C, A, TestActions.FIRST);
        graph.addAction(C, G, TestActions.SECOND);
        graph.addAction(C, B, TestActions.THIRD);
        BasicGraphMock<TestActions> bfsVsDfsGraph = new BasicGraphMock<TestActions>(graph);

        // Проверка пути
        ArrayList<TestActions> expResult = new ArrayList<TestActions>();
        expResult.add(TestActions.FIRST);
        expResult.add(TestActions.SECOND);
        expResult.add(TestActions.SECOND);
        assertEquals(expResult,
                bfs.getSolution(bfsVsDfsGraph, A, G),
                "Неверный путь к цели.");

        // Проверка перечня открытых вершин графа
        Set<Point> expClosedNodes = new HashSet<Point>();
        expClosedNodes.add(A);
        expClosedNodes.add(B);
        expClosedNodes.add(C);
        assertEquals(expClosedNodes,
                bfs.getLastClosedNodes(),
                "Неверный порядок обхода вершин.");
    }
}
