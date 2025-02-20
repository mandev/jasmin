package com.adlitteram.jasmin.gui.widget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class Slider<ParentType extends Container> {

    private static final int RIGHT = 0x01;
    private static final int LEFT = 0x02;
    private static final int TOP = 0x03;
    private static final int BOTTOM = 0x04;
    private final JPanel basePanel = new JPanel();
    private final ParentType parent;
    private final Object lock = new Object();
    private final ArrayList<Component> jPanels = new ArrayList<>();
    private final boolean useSlideButton = false;
    private boolean isSlideInProgress = false;

    private final JPanel glassPane;

    {
        glassPane = new JPanel();
        glassPane.setOpaque(false);
        glassPane.addMouseListener(new MouseAdapter() {
        });
        glassPane.addMouseMotionListener(new MouseMotionAdapter() {
        });
        glassPane.addKeyListener(new KeyAdapter() {
        });
    }

    public Slider(final ParentType parent) {
        if (parent == null) {
            throw new RuntimeException("ProgramCheck: Parent can not be null.");
        }
        if ((parent instanceof JFrame) || (parent instanceof JDialog) || (parent instanceof JWindow) || (parent instanceof JPanel)) {
        } else {
            throw new RuntimeException("ProgramCheck: Parent type not supported. " + parent.getClass().getSimpleName());
        }
        this.parent = parent;
        attach();
        basePanel.setSize(parent.getSize());
        basePanel.setLayout(new BorderLayout());
        if (useSlideButton) {
            final JPanel statusPanel = new JPanel();
            basePanel.add(statusPanel, BorderLayout.SOUTH);
            statusPanel.add(new JButton("Slide Left") {
                {
                    setMargin(new Insets(0, 0, 0, 0));
                    addActionListener((final ActionEvent e) -> {
                        slideLeft();
                    });
                }
            });
            statusPanel.add(new JButton("Slide Right") {
                {
                    setMargin(new Insets(0, 0, 0, 0));
                    addActionListener((final ActionEvent e) -> {
                        slideRight();
                    });
                }
            });
            statusPanel.add(new JButton("Slide Up") {
                {
                    setMargin(new Insets(0, 0, 0, 0));
                    addActionListener((final ActionEvent e) -> {
                        slideTop();
                    });
                }
            });
            statusPanel.add(new JButton("Slide Down") {
                {
                    setMargin(new Insets(0, 0, 0, 0));
                    addActionListener((final ActionEvent e) -> {
                        slideBottom();
                    });
                }
            });
        }
    }

    public JPanel getBasePanel() {
        return basePanel;
    }

    private void attach() {
        final ParentType w = this.parent;
        if (w instanceof JFrame j) {
            if (j.getContentPane().getComponents().length > 0) {
                throw new RuntimeException("ProgramCheck: Parent already contains content.");
            }
            j.getContentPane().add(basePanel);
        }
        if (w instanceof JDialog j) {
            if (j.getContentPane().getComponents().length > 0) {
                throw new RuntimeException("ProgramCheck: Parent already contains content.");
            }
            j.getContentPane().add(basePanel);
        }
        if (w instanceof JWindow j) {
            if (j.getContentPane().getComponents().length > 0) {
                throw new RuntimeException("ProgramCheck: Parent already contains content.");
            }
            j.getContentPane().add(basePanel);
        }
        if (w instanceof JPanel j) {
            if (j.getComponents().length > 0) {
                throw new RuntimeException("ProgramCheck: Parent already contains content.");
            }
            j.add(basePanel);
        }
    }

    public void addComponent(final Component component) {
        if (!jPanels.contains(component)) {
            jPanels.add(component);
            if (jPanels.size() == 1) {
                basePanel.add(component);
            }
            component.setSize(basePanel.getSize());
            component.setLocation(0, 0);
        }
    }

    public void removeComponent(final Component component) {
        jPanels.remove(component);
    }

    public void slideLeft() {
        slide(LEFT);
    }

    public void slideRight() {
        slide(RIGHT);
    }

    public void slideTop() {
        slide(TOP);
    }

    public void slideBottom() {
        slide(BOTTOM);
    }

    private void enableUserInput(final ParentType w) {
        if (w instanceof JFrame) {
            ((JFrame) w).getGlassPane().setVisible(false);
        }
        if (w instanceof JDialog) {
            ((JDialog) w).getGlassPane().setVisible(false);
        }
        if (w instanceof JWindow) {
            ((JWindow) w).getGlassPane().setVisible(false);
        }
    }

    private void disableUserInput(final ParentType w) {
        if (w instanceof JFrame) {
            ((JFrame) w).setGlassPane(glassPane);
        }
        if (w instanceof JDialog) {
            ((JDialog) w).setGlassPane(glassPane);
        }
        if (w instanceof JWindow) {
            ((JWindow) w).setGlassPane(glassPane);
        }
        glassPane.setVisible(true);
    }

    private void enableTransparentOverylay() {
        if (parent instanceof JFrame) {
            ((JFrame) parent).getContentPane().setBackground(jPanels.get(0).getBackground());
            parent.remove(basePanel);
            parent.validate();
        }
        if (parent instanceof JDialog) {
            ((JDialog) parent).getContentPane().setBackground(jPanels.get(0).getBackground());
            parent.remove(basePanel);
            parent.validate();
        }
        if (parent instanceof JWindow) {
            ((JWindow) parent).getContentPane().setBackground(jPanels.get(0).getBackground());
            parent.remove(basePanel);
            parent.validate();
        }
    }

    private void slide(final int slideType) {
        if (!isSlideInProgress) {
            isSlideInProgress = true;
            final Thread thread = new Thread(() -> {
                parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                disableUserInput(parent);
                slide(true, slideType);
                enableUserInput(parent);
                parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                isSlideInProgress = false;
            });
            thread.setDaemon(true);
            thread.start();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void slide(final boolean useLoop, final int slideType) {
        if (jPanels.size() < 2) {
            return;
        }
        synchronized (lock) {
            Component componentOld = null;
            Component componentNew = null;
            if ((slideType == LEFT) || (slideType == TOP)) {
                componentNew = jPanels.remove(jPanels.size() - 1);
                componentOld = jPanels.get(0);
                jPanels.add(0, componentNew);
            }
            if ((slideType == RIGHT) || (slideType == BOTTOM)) {
                componentOld = jPanels.remove(0);
                jPanels.add(componentOld);
                componentNew = jPanels.get(0);
            }
            final int w = componentOld.getWidth();
            final int h = componentOld.getHeight();
            final Point p1 = componentOld.getLocation();
            final Point p2 = new Point(0, 0);
            if (slideType == LEFT) {
                p2.x += w;
            }
            if (slideType == RIGHT) {
                p2.x -= w;
            }
            if (slideType == TOP) {
                p2.y += h;
            }
            if (slideType == BOTTOM) {
                p2.y -= h;
            }
            componentNew.setLocation(p2);
            int step = 0;
            if ((slideType == LEFT) || (slideType == RIGHT)) {
                step = (int) (((float) parent.getWidth() / (float) Toolkit.getDefaultToolkit().getScreenSize().width) * 40.f);
            } else {
                step = (int) (((float) parent.getHeight() / (float) Toolkit.getDefaultToolkit().getScreenSize().height) * 20.f);
            }
            step = step < 1 ? 1 : step;

            basePanel.add(componentNew);
            basePanel.revalidate();
            if (useLoop) {
                final int max = (slideType == LEFT) || (slideType == RIGHT) ? w : h;
                for (int i = 0; i != (max / step); i++) {
                    switch (slideType) {
                        case LEFT: {
                            p1.x -= step;
                            componentOld.setLocation(p1);
                            p2.x -= step;
                            componentNew.setLocation(p2);
                            break;
                        }
                        case RIGHT: {
                            p1.x += step;
                            componentOld.setLocation(p1);
                            p2.x += step;
                            componentNew.setLocation(p2);
                            break;
                        }
                        case TOP: {
                            p1.y -= step;
                            componentOld.setLocation(p1);
                            p2.y -= step;
                            componentNew.setLocation(p2);
                            break;
                        }
                        case BOTTOM: {
                            p1.y += step;
                            componentOld.setLocation(p1);
                            p2.y += step;
                            componentNew.setLocation(p2);
                            break;
                        }
                        default:
                            new RuntimeException("ProgramCheck").printStackTrace();
                            break;
                    }

                    try {
                        Thread.sleep(200 / (max / step));
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            componentOld.setLocation(-10000, -10000);
            componentNew.setLocation(0, 0);
        }
    }
}
