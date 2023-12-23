import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MinesweeperGame {

    private JFrame frame;
    private MineButton[][] buttons;
    private boolean[][] mines;
    private int rows = 10;
    private int cols = 10;
    private int numMines = 10;
    private int timeElapsed = 0; // 用于跟踪游戏时间
    private JLabel minesLabel;
    private JLabel timeLabel;
    private Timer timer;

    public MinesweeperGame() {
        frame = new JFrame("Minesweeper");
        frame.setSize(500, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        frame.add(topPanel, BorderLayout.NORTH);

        minesLabel = new JLabel("Mines: " + numMines);
        topPanel.add(minesLabel);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());
        topPanel.add(resetButton);

        timeLabel = new JLabel("Time: 0");
        topPanel.add(timeLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(rows, cols));

        buttons = new MineButton[rows][cols];
        mines = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                buttons[i][j] = new MineButton(i, j);
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                buttonPanel.add(buttons[i][j]);
            }
        }

        placeMines();

        frame.add(buttonPanel, BorderLayout.CENTER);

        timer = new Timer(1000, e -> {
            timeElapsed++;
            timeLabel.setText("Time: " + timeElapsed);
        });
        timer.start();

        frame.setVisible(true);
    }


    private void placeMines() {
        Random rand = new Random();
        int count = 0;
        while (count < numMines) {
            int x = rand.nextInt(rows);
            int y = rand.nextInt(cols);
            if (!mines[x][y]) {
                mines[x][y] = true;
                count++;
            }
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int x, y;
        private int correctFlagCount = 0;
    
        public ButtonClickListener(int x, int y) {
            this.x = x;
            this.y = y;
        }
    
        @Override
        public void actionPerformed(ActionEvent e) {
            if (mines[x][y]) {
                revealAllButtons();
                frame.setTitle("Game Over!");
                timer.stop();
            } else {
                if (countNeighboringMines(x, y) == 0) {
                    expandZeros(x, y);
                } else {
                    int count = countNeighboringMines(x, y);
                    buttons[x][y].setNumber(count);
                }
            }
    
            // 检查所有地雷是否都被正确标记
            checkAllMinesFlagged();
        }
        
        private void revealAllButtons() {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (mines[i][j]) {
                        buttons[i][j].setText("X"); // 显示地雷位置
                    } else {
                        int count = countNeighboringMines(i, j);
                        buttons[i][j].setNumber(count); // 显示周围的地雷数量
                    }
                    buttons[i][j].setEnabled(false); // 禁用所有按钮
                }
            }
        }

        private void expandZeros(int x, int y) {
            if (x < 0 || x >= rows || y < 0 || y >= cols || buttons[x][y].isEnabled() == false) {
                return;
            }
            int count = countNeighboringMines(x, y);
            buttons[x][y].setNumber(count);
    
            if (count == 0) {
                buttons[x][y].setEnabled(false);  // 当前按钮被点击，禁用它
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int newX = x + i;
                        int newY = y + j;
                        if (newX >= 0 && newX < rows && newY >= 0 && newY < cols) {
                            expandZeros(newX, newY);  // 递归展开相邻区域
                        }
                    }
                }
            }
        }
    
        private int countNeighboringMines(int x, int y) {
            int count = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newX = x + i;
                    int newY = y + j;
                    if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && mines[newX][newY]) {
                        count++;
                    }
                }
            }
            return count;
        }

        private void checkAllMinesFlagged() {
            correctFlagCount = 0; // 重置正确标记的地雷计数
    
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (buttons[i][j].getText().equals("F") && mines[i][j]) {
                        correctFlagCount++;
                    }
                }
            }
    
            // 如果所有地雷都被正确标记，则显示胜利消息
            if (correctFlagCount == numMines) {
                JOptionPane.showMessageDialog(frame, "Congratulations! You won!");
                timer.stop();
            }
        }
    }

    private void resetGame() {
        timeElapsed = 0; // 重置时间
        timeLabel.setText("Time: 0"); // 更新显示的时间
        if (timer.isRunning()) { // 检查计时器是否正在运行，如果是，则停止
            timer.stop();
        }
        frame.setTitle("Minesweeper");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                buttons[i][j].reset();
                mines[i][j] = false;
                buttons[i][j].setBackground(null);
                buttons[i][j].setEnabled(true);
            }
        }
        placeMines();
        timer.start(); // 重新启动计时器
    }

    private class MineButton extends JButton {
        private int number;
        private boolean isFlagged = false;
    
        public MineButton(int x, int y) {
            setPreferredSize(new Dimension(40, 40));
            reset();
        
            // 设置字体大小和粗细
            setFont(new Font("Arial", Font.BOLD, 22)); // Arial 字体，粗体，大小为 24
        
            // 右键点击事件监听
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        toggleFlag();
                        // 右键标记后禁用或启用按钮
                        if (isFlagged) {
                            setEnabled(false);
                        } else {
                            setEnabled(true);
                        }
                    }
                }
            });
        }
    
        public void setNumber(int num) {
            this.number = num;
            float hue = (float) (this.number * 30) / 360;
            Color textColor = Color.getHSBColor(hue, 1.0f, 1.0f);
            setForeground(textColor);
            setText(String.valueOf(num));
        }
    
        public void toggleFlag() {
            if (!isFlagged && getText().isEmpty()) {
                setText("F"); // F表示旗帜
                isFlagged = true;
            } else if (getText().equals("F")) {
                setText("");
                isFlagged = false;
            }
        }
    
        public void reset() {
            this.number = 0;
            setForeground(Color.BLACK);
            setText("");
            setEnabled(true);  // 重置时启用按钮
            isFlagged = false; // 重置标记状态
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MinesweeperGame());
    }
}
