package pacman.game;

import algorithms.core.BreadthFirstSearch;
import algorithms.core.Minimax;
import algorithms.core.Reflex;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import pacman.view.GameView;

/**
 * Контроллер игры.
 * <ol>
 * <li> Обрабатывает запросы от графического интерфейса.
 * <li> Управляет заменой панели опций на панель игры и обратно.
 * <li> Управляет передвижением Пакмана: по уведомлению от игры о выполнении
 * Пакманом имевшейся у него очереди действий, передаёт в игру алгоритм
 * получения новой очереди действий.</ol>
 */
public class GameController implements Observer {

    private final IControlableGameModel myModel;            // Модель игры
    private final JPanel myView;                            // Графическое представление игры (панель)
    private final Container myContentPane;                  // Корневая панель для размещения панелей игры
    private final JFrame myFrame;                           // Окно игры
    private final OptionsPanel optionsPanel;                // Панель опций игры
    private final ExecutorService modelThreadService;
    private final BreadthFirstSearch<IAction> pacmanBfs;    // Экземпляр поиска в ширину
    
    private PacmanMode pacmanControlMode;                   // Режим принятия решений Пакманом

    /**
     * Создаёт новый контроллер игры.
     * 
     * @param frame фрейм для размещения графического представления
     * @param model ссылка на модель игры
     * @param view ссылка на графическое представление
     * @param contentPane контейнер
     */
    public GameController(IControlableGameModel model, GameView view, JFrame frame, Container contentPane) {
        myModel = model;
        myView = view;
        myContentPane = contentPane;
        myFrame = frame;

        optionsPanel = new OptionsPanel(this);
        pacmanControlMode = PacmanMode.AUTO_THINK_MUCH;
        pacmanBfs = new BreadthFirstSearch<IAction>();
        
        modelThreadService = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "game-model-thread");
                return t;
            }});
        
        startGame();
    }
    
    /**
     * Способ управления Пакманом.
     */
    public enum PacmanMode {//<editor-fold defaultstate="collapsed">
    
        /**
         * Ручное управление кликом мыши.
         */
        MANUAL(false),
        /**
         * Пакман как реагирующий агент.
         */
        AUTO_THINK_LESS(true),
        /**
         * Пакман как конкурирующий агент (минимакс).
         */
        AUTO_THINK_MUCH(true);
        
        private final boolean autoTurnStart;
        
        private PacmanMode(boolean autoTurnStart) {
            this.autoTurnStart = autoTurnStart;
        }
        
        /**
         * Возвращает <code>true</code>, если способ принятия решений Пакманом
         * является автоматическим.
         * <p>
         * При автоматическом принятии решений новый тур игры начинается сразу
         * после завершения предыдущего.
         * 
         * @return <code>true</code>, если способ принятия решений Пакманом
         * является автоматическим
         */
        public boolean isNonmanual() {
            return autoTurnStart;
        }
    //</editor-fold>
    }

    @Override
    public void update(Observable o, Object arg) {
        if (myModel.isPacmanQueueEmpty()) {
            performPacmanQueueEmpty();
        }
    }

    //----------------------------------------------------- Методы запуска игры
    
    /**
     * Устанавливает режим принятия решений Пакманом.
     * 
     * @param option идентификатор режима принятия решений Пакманом
     * @see PacmanMode
     */
    public void setControlOption(PacmanMode option) {
        pacmanControlMode = option;
    }

    /**
     * Устанавливает количество привидений.
     * 
     * @param ghostsNumber количество привидений, 1...4
     */
    public void setGhostsNumber(int ghostsNumber) {
        myModel.setGhostsNumber(ghostsNumber);
    }

    /**
     * Уведомляет о завершении установки опций игры.
     */
    public void performOptionsSelected() {
        myModel.reinitializeGame();
        setPanel(myView);
        modelThreadService.submit((Runnable) myModel);
    }

    /**
     * Делает видимой указанную панель, скрывает остальные.
     * @param panel панель, которая должна быть видимой
     */
    private void setPanel(JPanel panel) {
        myFrame.setVisible(false);

        myContentPane.removeAll();
        myContentPane.revalidate();
        myContentPane.add(panel, BorderLayout.CENTER);
        myContentPane.setMaximumSize(panel.getPreferredSize());
        myContentPane.setPreferredSize(panel.getPreferredSize());
        myContentPane.revalidate();

        myFrame.pack();
        myFrame.setLocationRelativeTo(null);
        myFrame.setVisible(true);
    }
    
    //----------------------------------------------------- Методы времени игры
    
    /**
     * Обрабатывает клик мыши по игровому полю.
     * 
     * В ручном режиме управления Пакманом координаты клика будут переданы как
     * его целевое расположение.
     * 
     * @param e событие мыши
     */
    public void handleMouseClick(MouseEvent e) {
        if (!myModel.isGameComplete()) {
            if (!pacmanControlMode.isNonmanual()) {
                performPacmanAction(e.getPoint());
            }
        } else {
            startGame();
        }
    }

    private void startGame() {
        setPanel(optionsPanel);
    }

    /**
     * Уведомляет о выполнении Пакманом всех переданных ему действий.
     * <p>
     * Если режим управления Пакманом - автоматический, то Пакман получит новое
     * действие без участия пользователя.
     */
    public void performPacmanQueueEmpty() {
        if (pacmanControlMode.isNonmanual()) {
            performPacmanAction(null);
        }
    }
    
    private void performPacmanAction(Point clickLocation) {
        switch (pacmanControlMode) {
            // Ручное управление Пакманом
            case MANUAL:
                Point targetLocation = myModel.getCellAddress(clickLocation);
                myModel.performPacmanAction(pacmanBfs, targetLocation);
                break;

            // Думает мало - реагирующий агент
            case AUTO_THINK_LESS:
                myModel.performPacmanAction(new Reflex<IAction>(), null);
                break;

            // Думает много - конкурирующий агент
            case AUTO_THINK_MUCH:
                myModel.performPacmanAction(new Minimax<IAction>(4, Minimax.PruningMode.ON), null);
                break;
        }
    }
}
