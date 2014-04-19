package pacman.game;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import pacman.game.GameController.PacmanMode;

/**
 * Панель установки опций игры.
 */
public class OptionsPanel extends JPanel {
    
    private final GameController myController;
    private final Integer[] ghostNumberOptions = {1, 2, 3, 4};
    
    private final ButtonGroup buttonGroup1 = new ButtonGroup();
    private final JButton buttonStart = new JButton();
    private final JLabel labelSelectControlMode = new JLabel();
    private final JRadioButton radioModeMinimax = new JRadioButton();
    private final JRadioButton radioModeManual = new JRadioButton();
    private final JRadioButton radioModeReflex = new JRadioButton();
    private final JLabel labelSelectGhostsNumber = new JLabel();
    private final JComboBox comboGhostsNumber = new JComboBox();

    /**
     * Создаёт новую панель установки опций игры.
     * 
     * @param controller контроллер игры
     */
    public OptionsPanel(GameController controller) {
        myController = controller;
        
        // Конфигурирование панели-основы, которая задаст размер окна
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setMinimumSize(new Dimension(400, 350));
        this.setPreferredSize(new Dimension(400, 350));
        this.setMaximumSize(new Dimension(400, 350));
        //this.setBackground(Color.green);
        
        // Базовая панель опций: содержит элементы управления
        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.PAGE_AXIS));
        basePanel.setMinimumSize(new Dimension(320, 170));
        basePanel.setPreferredSize(new Dimension(320, 170));
        basePanel.setMaximumSize(new Dimension(320, 170));
        basePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        //basePanel.setBackground(Color.red);
        
        // Добавление элементов выбора режима Пакмана
        basePanel.add(labelSelectControlMode);
        basePanel.add(Box.createRigidArea(new Dimension(0, 6)));
        basePanel.add(radioModeManual);
        basePanel.add(Box.createRigidArea(new Dimension(0, 3)));
        basePanel.add(radioModeMinimax);
        basePanel.add(Box.createRigidArea(new Dimension(0, 3)));
        basePanel.add(radioModeReflex);
        
        // Добавление элементов выбора количества призраков
        JPanel totalGhostsPane = new JPanel();
        totalGhostsPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalGhostsPane.setLayout(new BoxLayout(totalGhostsPane, BoxLayout.LINE_AXIS));
        totalGhostsPane.add(labelSelectGhostsNumber);
        totalGhostsPane.add(Box.createRigidArea(new Dimension(10, 0)));
        totalGhostsPane.add(comboGhostsNumber);
        totalGhostsPane.add(Box.createHorizontalGlue());
        basePanel.add(Box.createVerticalGlue());
        basePanel.add(totalGhostsPane);
        
        // Добавление кнопки начала игры
        JPanel buttonPane = new JPanel();
        buttonPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(buttonStart);
        buttonPane.add(Box.createHorizontalGlue());
        basePanel.add(Box.createVerticalGlue());
        basePanel.add(Box.createVerticalGlue());
        basePanel.add(buttonPane);
        
        // Добавление базовой панели на панель-основу
        this.add(Box.createHorizontalGlue());
        this.add(Box.createVerticalGlue());
        this.add(basePanel, BorderLayout.CENTER);
        this.add(Box.createHorizontalGlue());
        this.add(Box.createVerticalGlue());

        //------------------------------- Конфигурирование элементов управления

        buttonGroup1.add(radioModeMinimax);
        buttonGroup1.add(radioModeManual);
        buttonGroup1.add(radioModeReflex);

        labelSelectControlMode.setText("Хотите сами управлять Пакманом?");

        radioModeManual.setText("да, я буду давать указания мышью");
        radioModeManual.setSelected(true);
        radioModeManual.addActionListener(new ActionListener() {
            //<editor-fold defaultstate="collapsed" desc="actionPerformed">
            @Override
            public void actionPerformed(ActionEvent evt) {
                myController.setControlOption(PacmanMode.MANUAL);
            }
            //</editor-fold>
        });

        radioModeMinimax.setText("нет, пусть Пакман всё решает сам и много думает");
        radioModeMinimax.addActionListener(new ActionListener() {
            //<editor-fold defaultstate="collapsed" desc="actionPerformed">
            @Override
            public void actionPerformed(ActionEvent evt) {
                myController.setControlOption(PacmanMode.AUTO_THINK_MUCH);
            }
            //</editor-fold>
        });

        radioModeReflex.setText("нет, пусть Пакман всё решает сам и думает мало");
        radioModeReflex.addActionListener(new ActionListener() {
            //<editor-fold defaultstate="collapsed" desc="actionPerformed">
            @Override
            public void actionPerformed(ActionEvent evt) {
                myController.setControlOption(PacmanMode.AUTO_THINK_LESS);
            }
            //</editor-fold>
        });

        labelSelectGhostsNumber.setText("Количество преследующих призраков:");
        
        comboGhostsNumber.setModel(new DefaultComboBoxModel(ghostNumberOptions));
        comboGhostsNumber.setMaximumSize(new Dimension(50, 25));
        comboGhostsNumber.addActionListener(new ActionListener() {
            //<editor-fold defaultstate="collapsed" desc="actionPerformed">
            @Override
            public void actionPerformed(ActionEvent evt) {
                myController.setGhostsNumber(ghostNumberOptions[comboGhostsNumber.getSelectedIndex()]);
            }
            //</editor-fold>
        });

        buttonStart.setText("Начать игру");
        buttonStart.addActionListener(new ActionListener() {
            //<editor-fold defaultstate="collapsed" desc="actionPerformed">
            @Override
            public void actionPerformed(ActionEvent evt) {
                myController.performOptionsSelected();
            }
            //</editor-fold>
        });
    }
}
