package pacman.model;

import java.awt.Point;
import java.util.concurrent.TimeUnit;
import pacman.game.IAction;

/**
 * Исполнитель анимации одного действия.
 * <p>
 * Выполняет сдвиг анимации объекта на заданное расстояние в указанном
 * направлении. Об исполнении каждого шага и завершении анимации уведомляет
 * через интерфейс {@link IAnimationMediator}.
 * <p>
 * Использование: установить анимируемое действие через {@link setAction},
 * запустить поток исполнения. Для анимирования одного объекта может многократно
 * использоваться один экземпляр этого класса.
 */
public class ActionAnimator implements Runnable {
    
    private static final int ANIMATION_FPS = 40;    // Количество кадров анимации в секунду
    private static final int ANIMATION_STEPS = 11;  // Количество шагов анимации на полном отрезке
    
    private final IMovableEntity movableObject;     // Игрок, которому принадлежит анимация
    private final IAnimationMediator listener;
    private final int animationStepInPixels;        // Шаг анимации, в пикселах
    private final int stepLength;                   // Длина отрезка анимации
    
    private Point currentShift;                     // Текущие координаты анимации, в пикселах
    private IAction action;                         // Текущее действие
    private boolean isStopped = false;

    /**
     * Создаёт нового исполнителя.
     *
     * @param object анимируемый объект
     * @param listener обработчик уведомлений
     * @param stepLength протяжённость отрезка анимации, в пикселах
     */
    public ActionAnimator(IMovableEntity object, IAnimationMediator listener, int stepLength) {
        this.movableObject = object;
        this.listener = listener;
        this.stepLength = stepLength;
        this.animationStepInPixels = stepLength / ANIMATION_STEPS;
        this.currentShift = new Point(0, 0);
    }

    /**
     * Устанавливает анимируемое действие.
     *
     * @param action анимируемое действие
     */
    public void setAction(IAction action) {
        this.action = action;
        movableObject.setCurrentAction(action);
    }

    @Override
    public void run() {
        try {
            while (isAnimationIncomplete() && !isStopped) {
                movableObject.setAnimationShift(shiftAnimationStep(action));
                TimeUnit.MILLISECONDS.sleep(1000 / ANIMATION_FPS);
                listener.redrawGraphics();
            }
        } catch (InterruptedException e) {
        }
        currentShift.x = 0;
        currentShift.y = 0;
        isStopped = false;
        listener.notifyAnimationComplete();
    }

    /**
     * Прекращает процесс анимации.
     */
    public void stop() {
        isStopped = true;
    }

    /**
     * Перемещает анимацию на один шаг в указанном направлении.
     *
     * @param action направление анимации
     * @return новые координаты анимации
     */
    private Point shiftAnimationStep(IAction action) {
        currentShift = action.translateWithFactor(currentShift, animationStepInPixels);
        return new Point(currentShift.x - stepLength * action.getBasis().x,
                currentShift.y - stepLength * action.getBasis().y);
    }

    /**
     * @return <code>true</code> если выполнение анимации не завершено
     */
    private boolean isAnimationIncomplete() {
        return (Math.abs(currentShift.x) < animationStepInPixels * ANIMATION_STEPS)
                && (Math.abs(currentShift.y) < animationStepInPixels * ANIMATION_STEPS);
    }
}

//    <editor-fold defaultstate="collapsed" desc="реализация сдвига без потоков">
//    private Timer animator = null;                                              // Таймер, приводящий анимацию в движение
//    IAction action;
//    public void startAnimation() {
//        if (animationDone) {
//            animationDone = false;
//            
//            action = actionsSequence.element();
//            movableObject.setCurrentAction(action);
//            
//            animator = new Timer(1000 / ANIMATION_FPS, new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    if (ActivityExecutor.this.isAnimationIncomplete()) {
//                        ActivityExecutor.this.shiftAnimationStep(action);
//                        movableObject.setAnimationShift(currentShift);
//                        listener.refreshData();
//                    } else {
//                        animationDone = true;
//                        currentShift.x = 0;
//                        currentShift.y = 0;
//                        animator.stop();
//
//                        listener.setOneActionComplete(movableObject, action);
//
//                        actionsSequence.remove();
//                        if (actionsSequence.isEmpty() != true) {
//                            startAnimation();
//                        }
//                    }
//                    listener.refreshData();
//                }
//            });
//            animator.start();
//        }
//    }
//    </editor-fold>
