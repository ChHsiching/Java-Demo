# Minesweeper Game

A simple Minesweeper game implemented in Java using Swing.

## How to Play

1. **Left-click** on a button to reveal its content.
   - If the revealed button contains a mine, the game is over.
   - If the revealed button is empty, it may reveal neighboring empty buttons.

2. **Right-click** on a button to flag or unflag it as a potential mine.

3. The game is won when all mines are correctly flagged.

## Controls

- **Reset Button:** Click to start a new game.

## Features

- Timer to track the time elapsed during the game.
- Flags to mark potential mine locations.

## How to Run

Compile and run the `MinesweeperGame` class, and the game window will appear.

```bash
javac MinesweeperGame.java
java MinesweeperGame
