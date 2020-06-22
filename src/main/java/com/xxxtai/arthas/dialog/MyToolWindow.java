package com.xxxtai.arthas.dialog;

import java.awt.*;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class MyToolWindow {
    private final JPanel mainPanel;
    private final JTextArea jTextArea;
    private static MyToolWindow instance;

    public MyToolWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        jTextArea = new JTextArea();

        DefaultCaret caret = (DefaultCaret) jTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JBScrollPane scrollPane = new JBScrollPane();
        scrollPane.setPreferredSize(new Dimension((int)screenSize.getWidth() - 35, (int)screenSize.getHeight() / 8 * 3 - 35));
        scrollPane.setVisible(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        scrollPane.getVerticalScrollBar().setDoubleBuffered(true);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(jTextArea);

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension((int)screenSize.getWidth() - 35, (int)screenSize.getHeight() / 8 * 3));
        mainPanel.add(scrollPane);
    }

    public JPanel getContent() {
        return mainPanel;
    }

    public JTextArea getjTextArea() {
        return jTextArea;
    }

    public static MyToolWindow getInstance() {
        if (instance == null) {
            instance = new MyToolWindow();
        }
        return instance;
    }
}
