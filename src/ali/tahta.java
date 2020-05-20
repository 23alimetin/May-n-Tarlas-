/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ali;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class tahta extends JPanel {
    // Constants
    private final int HUCRE_BOYUT  = 15;
    private final int RESIM_SAYISI = 13;

    private final int RESIMDEKI_MAYIN       = 9;
    private final int IMAGE_COVER      = 10;
    private final int IMAGE_MARK       = 11;
    private final int IMAGE_WRONG_MARK = 12;

    private JLabel durumCubugu;

    private int totalMines = 40;
    private int kalanMayinlar;

    private int satirlar = 16, sutunlar = 16;

    private hucreler[][] cells;
    private Image[] img;

    private boolean inGame;

    public tahta(JLabel durumCubugu) {
        this.durumCubugu = durumCubugu;

        this.img = new Image[RESIM_SAYISI];
        for (int i = 0; i < RESIM_SAYISI; i++) {
            String path = "img/j" + i + ".gif";
            img[i] = new ImageIcon(path).getImage();
        }

        this.setDoubleBuffered(true);
        this.addMouseListener(new MinesAdapter());
        this.newGame();
    }

    private void initCells () {
        this.cells = new hucreler[satirlar][sutunlar];

        for (int i = 0; i < this.satirlar; ++i) {
            for (int j = 0; j < this.sutunlar; ++j) {
                this.cells[i][j] = new hucreler();
            }
        }
    }

    public void newGame () {
        Random random;

        random = new Random();

        this.inGame = true;
        this.kalanMayinlar = totalMines;

        this.initCells();
        this.durumCubugu.setText(Integer.toString(this.kalanMayinlar));

        int remainder = totalMines;
        while (remainder >= 0) {
            int randX = random.nextInt(this.satirlar);
            int randY = random.nextInt(this.sutunlar);

            hucreler cell = this.cells[randX][randY];
            if (!cell.isMine()) {
                cell.setMine(true);
                remainder--;
            }
        }

        this.setMineCounts();
    }

    private void setMineCounts() {

        for (int i = 0; i < this.sutunlar; ++i) {
            for (int j = 0; j < this.satirlar; ++j) {
                hucreler cell = this.cells[i][j];

                if (!cell.isMine()) {
                    int count = countMinesAround(i, j);
                    cell.setAroundMines(count);
                }
            }
        }
    }

    private int countMinesAround(int x, int y) {
        int count = 0;

        for (int i = -1; i <= 1; ++i) {
            int xIndex = x + i;
            if (xIndex < 0 || xIndex >= this.satirlar) {
                continue;
            }

            for (int j = -1; j <= 1; ++j) {
                int yIndex = y + j;
                if (yIndex < 0 || yIndex >= this.sutunlar) {
                    continue;
                }

                if (i == 0 && j == 0) {
                    continue;
                }

                if (this.cells[xIndex][yIndex].isMine()) {
                    count++;
                }
            }
        }

        return count;
    }

    public void paint(Graphics g) {
        int coveredCells = 0;

        for (int i = 0; i < this.satirlar; i++) {
            for (int j = 0; j < this.sutunlar; j++) {
                hucreler cell = this.cells[i][j];
                int imageType;
                int xPosition, yPosition;

                if (cell.isCovered()) {
                    coveredCells++;
                }

                if (inGame) {
                    if (cell.isMine() && !cell.isCovered()) {
                        inGame = false;
                    }
                }

                imageType = this.decideImageType(cell);

                xPosition = (j * HUCRE_BOYUT);
                yPosition = (i * HUCRE_BOYUT);

                g.drawImage(img[imageType], xPosition, yPosition, this);
            }
        }

        if (coveredCells == 0 && inGame) {
            inGame = false;
            durumCubugu.setText("Kazandın");
        } else if (!inGame) {
            durumCubugu.setText("Kaybettin");
        }
    }

    private int decideImageType(hucreler cell) {
        int imageType = cell.getValue();

        if (!inGame) {
            if (cell.isCovered() && cell.isMine()) {
                cell.uncover();
                imageType = RESIMDEKI_MAYIN;
            } else if (cell.isMarked()) {
                if (cell.isMine()) {
                    imageType = IMAGE_MARK;
                } else {
                    imageType = IMAGE_WRONG_MARK;
                }
            }
        } else {
            if (cell.isMarked()) {
                imageType = IMAGE_MARK;
            } else if (cell.isCovered()) {
                imageType = IMAGE_COVER;
            }
        }

        return imageType;
    }

    public void findEmptyCells(int x, int y, int depth) {

        for (int i = -1; i <= 1; ++i) {
            int xIndex = x + i;

            if (xIndex < 0 || xIndex >= this.satirlar) {
                continue;
            }

            for (int j = -1; j <= 1; ++j) {
                int yIndex = y + j;

                if (yIndex < 0 || yIndex >= this.sutunlar) {
                    continue;
                }

                if (!(i == 0 || j == 0)) {
                    continue;
                }

                hucreler cell = this.cells[xIndex][yIndex];
                if (checkEmpty(cell)) {
                    cell.uncover();
                    cell.checked();

                    uncoverAroundCell(xIndex, yIndex);
                    findEmptyCells(xIndex, yIndex, depth + 1);
                }
            }
        }

        if (depth == 0) {
            this.clearAllCells();
        }
    }

    private void uncoverAroundCell(int x, int y) {
        for (int i = -1; i <= 1; ++i) {
            int xIndex = x + i;

            if (xIndex < 0 || xIndex >= this.satirlar) {
                continue;
            }

            for (int j = -1; j <= 1; ++j) {
                int yIndex = y + j;

                if (yIndex < 0 || yIndex >= this.sutunlar) {
                    continue;
                }

                if (i == 0 || j == 0) {
                    continue;
                }

                hucreler cell = this.cells[xIndex][yIndex];
                if (cell.isCovered() && !cell.isEmpty()) {
                    cell.uncover();
                }
            }
        }
    }

    private boolean checkEmpty(hucreler cell) {
        if (!cell.isChecked()) {
            if (cell.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private void clearAllCells() {
        for (int i = 0; i < this.satirlar; ++i) {
            for (int j = 0; j < this.sutunlar; ++j) {
                this.cells[i][j].clearChecked();
            }
        }
    }

    class MinesAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int pressedCol = e.getX() / HUCRE_BOYUT;
            int pressedRow = e.getY() / HUCRE_BOYUT;

            boolean doRepaint = false;
            hucreler pressedCell;

            if (!inGame) {
                newGame();
                repaint();
            }

            if ((pressedCol < 0 || pressedCol >= sutunlar)
                || (pressedRow < 0 || pressedRow >= satirlar)) {
                return;
            }

            pressedCell = cells[pressedRow][pressedCol];

            if (e.getButton() == MouseEvent.BUTTON3) {
                doRepaint = true;

                if (!pressedCell.isCovered()) {
                    return;
                }

                String str;
                if (!pressedCell.isMarked()) {
                    pressedCell.setMark(true);
                    kalanMayinlar--;
                } else {
                    pressedCell.setMark(false);
                    kalanMayinlar++;
                }

                durumCubugu.setText(Integer.toString(kalanMayinlar));
            } else {
                if (pressedCell.isMarked() || !pressedCell.isCovered()) {
                    return;
                }

                doRepaint = true;

                pressedCell.uncover();
                if (pressedCell.isMine()) {
                    inGame = false;
                } else if (pressedCell.isEmpty()) {
                    findEmptyCells(pressedRow, pressedCol, 0);
                }
            }

            if (doRepaint) {
                repaint();
            }
        }
    }
}