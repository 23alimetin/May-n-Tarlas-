/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ali;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class mayinlar extends JFrame {
    private final int WIDTH = 250;
    private final int HEIGHT = 290;

    private JLabel statusbar;

    public mayinlar() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setTitle("ali metin mayin tarlasi");

        statusbar = new JLabel("");
        add(statusbar, BorderLayout.SOUTH);

        add(new tahta(statusbar));

        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new mayinlar();
    }
}