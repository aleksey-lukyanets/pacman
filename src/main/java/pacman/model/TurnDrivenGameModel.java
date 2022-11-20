package pacman.model;

import algorithms.core.ISearchAlgorithm;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import pacman.game.IAction;

/**
 * Модель игры по турам.
 * <p>
 * Игра ведётся по турам, в каждом из которых игроки делают по одному ходу.
 * Пакман делает первый ход в туре, получая действие из очереди действий
 * {@link pacmanActionsQueue}; если очередь действий пуста, то об этом
 * уведомляется контроллер игры, который должен предоставить новые действия.
 * После запуска действия Пакмана (и во время его анимации) рассчитываются и
 * запускаются действия привидений. Новый тур игры начинается, когда все игроки
 * выполнили свои действия.
 * <p>
 * Реализация:<ol>
 * <li> Инициализировать список игроков в {@link initializePlayers};
 * <li> Возвращать идентификатор Пакмана в {@link getPacmanId};
 * <li> Изменять статус хода игрока через {@link setPlayerStatusDone};
 * <li> Расчитывать последовательность действий Пакмана через заданный алгоритм
 * в {@link getPacmanActionsSequence};
 * <li> Исполнять действия игроков в {@link startPlayerAction};
 * <li> Рассчитывать и запускать действия привидений в {@link letGhostsAct};
 * <li> При необходимости выполнять действия по завершении тура в {@link performTurnFinished}.</ol>
 */ 
public abstract class TurnDrivenGameModel
        extends AbstractGameModel
        implements Runnable {
    
    // Очередь действий Пакмана
    private final BlockingQueue<IAction> pacmanActionsQueue = new LinkedBlockingQueue<IAction>();
    private final List<Integer> playersId = new ArrayList<Integer>();
    
    private TurnStatus turnStatus = TurnStatus.PACMAN_ACTION;
    private CountDownLatch finish;
    
    /**
     * Создаёт новую модель игры.
     *
     * @param gameField карта игрового поля
     */
    public TurnDrivenGameModel(GameFieldMap gameField) {
        super(gameField);
    }
    
    /**
     * Фаза выполнения тура игры.
     */
    private enum TurnStatus {NOBODY_ACTED, PACMAN_ACTION, EVERYBODY_ACTED};
    
    //---------------------------------------------------- Методы инициализации
    
    @Override
    public int reinitializePlayers() {
        players.clear();
        initializePlayers();
        initializeTurnData();
        return getPacmanId();
    }
    
    /**
     * Повторно инициализирует специфические для этого класса данные.
     */
    private void initializeTurnData() {
        playersId.clear();
        for (int i = 0; i < (totalGhosts + 1); i++) {
            playersId.add(i);
        }
        pacmanActionsQueue.clear();
        turnStatus = TurnStatus.PACMAN_ACTION;
        clearActionsComplete();
    }

    /**
     * Инициализация списка игроков.
     */
    protected abstract void initializePlayers();

    /**
     * Возвращает идентификатор игрока-Пакмана.
     * 
     * @return индекс игрока-Пакмана в списке <code>players</code>
     */
    protected abstract int getPacmanId();

    //---------------------------------------- Управление передвижением игроков
    
    @Override
    public void performPacmanAction(ISearchAlgorithm algorithm, Point goal) {
        synchronized (pacmanActionsQueue) {
            if (pacmanActionsQueue.isEmpty()) {
                Queue<IAction> sequence = getPacmanActionsSequence(algorithm, goal);
                pacmanActionsQueue.addAll(sequence);
            }
        }
    }
    
    @Override
    public boolean isPacmanQueueEmpty() {
        return pacmanActionsQueue.isEmpty();
    }
    
    /**
     * Устанавливает состояние игрока в "ход сделан".
     * <p>
     * Потоково-безопасен. Должен вызываться последним действием метода.
     * 
     * @param playerId идентификатор игрока
     */
    protected void setPlayerStatusDone(int playerId) {
        //System.out.println("Прибыл игрок " + playerId);
        finish.countDown();
    }
    
    @Override
    public final void run() {//<editor-fold defaultstate="collapsed">
        while (true) {
            /*
             * Обработка фаз тура игры
             */
            switch (turnStatus) {
                
                // Запуск хода Пакмана с последующими ходами привидений
                case PACMAN_ACTION:
                {
                    if (pacmanActionsQueue.isEmpty()) {
                        setPacmanActionsDone();                                 // Запросить новые ходы Пакмана
                    }
                    
                    try {
                        IAction action = pacmanActionsQueue.take();
                        //System.out.println("==========\nНовый тур");
                        startPlayerAction(getPacmanId(), action);               // Запуск хода Пакмана
                    } catch (InterruptedException ie) {}
                    
                    letGhostsAct();                                             // Расчёт и запуск ходов привидений
                    turnStatus = TurnStatus.EVERYBODY_ACTED;
                }
                break;
                    
                // Завершение тура: все игроки исполнили своих ходы
                case EVERYBODY_ACTED:
                {
                    // Ожидать, пока все игроки завершат ходы
                    try {
                        finish.await();
                        //System.out.println("Тур завершён");
                    } catch (InterruptedException ie) {}
                    
                    performTurnFinished();                                      // Завершить тур
                    reportChanged();                                            // Уведомить об изменении данных модели
                    clearActionsComplete();                                     // Перевести игроков в режим ожидания
                    turnStatus = TurnStatus.PACMAN_ACTION;
                }
                break;
            }
            
            // Если игра завершена победой или поражением,
            // то завершить выполнение
            if (isGameComplete()) {
                break;
            }
        }
        //</editor-fold>
    }

    /**
     * Очистить все флаги завершения ходов игроками в текущем туре игры.
     */
    private void clearActionsComplete() {
        finish = new CountDownLatch(playersId.size());
    }
    
    //----------------------------------------- Реализация передвижения игроков
    
    /**
     * Рассчитывает и возвращает последовательность ходов Пакмана.
     * 
     * @param algorithm алгоритм для расчёта действия
     * @param goal координата целевого расположения на игровом поле
     * @return последовательность ходов Пакмана
     */
    protected abstract Queue<IAction> getPacmanActionsSequence(ISearchAlgorithm algorithm, Point goal);
    
    /**
     * Запускает выполнение хода игроком.
     * 
     * @param playerId идентификатор игрока
     * @param action ход игрока
     */
    protected abstract void startPlayerAction(int playerId, IAction action);

    /**
     * Вычисление и запуск ходов привидений.
     * <p>
     * Каждое привидение выполняет строго один ход.
     * <p>
     * Инструкции по установке статуса хода см. {@link startPlayerAction}.
     */
    protected abstract void letGhostsAct();

    /**
     * Завершение тура игры.
     */
    protected abstract void performTurnFinished();
}
