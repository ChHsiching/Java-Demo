import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GobangGame extends JFrame {
    private final int BOARD_SIZE = 15;
    private JButton[][] board;
    private boolean isBlackTurn = true;

    public GobangGame() {
        setTitle("五子棋游戏");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        board = new JButton[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();

        setVisible(true);
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new JButton();
                board[i][j].setBackground(new Color(210, 180, 140));
                board[i][j].addActionListener(new BoardActionListener(i, j));
                add(board[i][j]);
            }
        }
    }

    private class BoardActionListener implements ActionListener {
        private int x, y;

        public BoardActionListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();
            if (clickedButton.getBackground().equals(new Color(210, 180, 140))) {
                if (isBlackTurn) {
                    clickedButton.setBackground(Color.BLACK);
                } else {
                    clickedButton.setBackground(Color.WHITE);
                }
                clickedButton.setEnabled(false);
                
                if (checkWin(x, y)) {
                    String winner = isBlackTurn ? "黑子" : "白子";
                    JOptionPane.showMessageDialog(null, winner + "胜利！");
                    resetGame();
                } else {
                    isBlackTurn = !isBlackTurn;
                }
            }
        }
    }

    private boolean checkWin(int x, int y) {
        Color targetColor = isBlackTurn ? Color.BLACK : Color.WHITE;
    
        // 检查水平方向
        if (countConsecutivePieces(x, y, 0, 1, targetColor) + countConsecutivePieces(x, y, 0, -1, targetColor) >= 10) return true;
    
        // 检查垂直方向
        if (countConsecutivePieces(x, y, 1, 0, targetColor) + countConsecutivePieces(x, y, -1, 0, targetColor) >= 10) return true;
    
        // 检查对角线方向
        if (countConsecutivePieces(x, y, 1, 1, targetColor) + countConsecutivePieces(x, y, -1, -1, targetColor) >= 10) return true;
    
        // 检查反对角线方向
        if (countConsecutivePieces(x, y, 1, -1, targetColor) + countConsecutivePieces(x, y, -1, 1, targetColor) >= 10) return true;
    
        return false;
    }
    

    private int countConsecutivePieces(int x, int y, int dx, int dy, Color targetColor) {
        int count = 1; // 因为当前点击的位置已经有一个棋子了
        int tx = x + dx;
        int ty = y + dy;

        while (tx >= 0 && tx < BOARD_SIZE && ty >= 0 && ty < BOARD_SIZE && board[tx][ty].getBackground().equals(targetColor)) {
            count++;
            tx += dx;
            ty += dy;
        }

        tx = x - dx;
        ty = y - dy;
        while (tx >= 0 && tx < BOARD_SIZE && ty >= 0 && ty < BOARD_SIZE && board[tx][ty].getBackground().equals(targetColor)) {
            count++;
            tx -= dx;
            ty -= dy;
        }

        return count;
    }

    private void resetGame() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j].setBackground(new Color(210, 180, 140));
                board[i][j].setEnabled(true);
            }
        }
        isBlackTurn = true; // 黑子先走
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GobangGame();
            }
        });
    }
}
