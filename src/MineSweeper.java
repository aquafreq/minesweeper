import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class MineSweeper {
    public static final int BOARD_BEGINNERS_SIZE = 9;
    public static final int BOARD_INTERMEDIATE_SIZE = 16;
    public static final int BOARD_ADVANCED_SIZE = 24;
    public static final String VISITED_CELL = "V";
    public static final String MINE_CELL = "*";
    public static final String EMPTY_CELL = "-";
    public static final String EMPTY_SPACE = " ";
    public static final String PLAYER_WON = "You won!";
    public static final String PLAYER_LOST = "You lost!";
    public static final String CURRENT_BOARD_STATUS = "Current status of board :";
    public static final String THIS_CELL_IS_ALREADY_VISITED = "This cell is already visited!";
    public static final String INVALID_BOARD_INDICES = "Invalid board indices!";
    public static final String INVALID_INPUT = "Invalid input!";
    public static final String ENTRY_MESSAGE = "Enter your move, (row, column) \r\n->";
    public static final String ENTER_THE_DIFFICULTY_LEVEL = "Enter the Difficulty Level";
    public static final String BEGINNERS_DIFFICULTY_MESSAGE = "Press 0 for BEGINNER (9 * 9 Cells and 10 Mines";
    public static final String INTERMEDIATE_DIFFICULTY_MESSAGE = "Press 1 for INTERMEDIATE (16 * 16 Cells and 40 Mines";
    public static final String ADVANCED_DIFFICULTY_MESSAGE = "Press 2 for ADVANCED (24 * 24 Cells and 99 Mines";
    public static final String BEGINNERS_LEVEL_INPUT = "0";
    public static final String INTERMEDIATE_LELVE_INPUT = "1";
    public static final String ADVANCED_LEVEL_INPUT = "2";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println(ENTER_THE_DIFFICULTY_LEVEL);
        System.out.println(BEGINNERS_DIFFICULTY_MESSAGE);
        System.out.println(INTERMEDIATE_DIFFICULTY_MESSAGE);
        System.out.println(ADVANCED_DIFFICULTY_MESSAGE);

        String difficulty = sc.nextLine();
        String[][] board = new String[0][0];
        int minesCount = 0;

        if (BEGINNERS_LEVEL_INPUT.equals(difficulty)) {
            board = fillBoard(BOARD_BEGINNERS_SIZE, BOARD_BEGINNERS_SIZE);
            minesCount = 10;
        } else if (INTERMEDIATE_LELVE_INPUT.equals(difficulty)) {
            board = fillBoard(BOARD_INTERMEDIATE_SIZE, BOARD_INTERMEDIATE_SIZE);
            minesCount = 40;
        } else if (ADVANCED_LEVEL_INPUT.equals(difficulty)) {
            board = fillBoard(BOARD_ADVANCED_SIZE, 24);
            minesCount = 99;
        }

        boolean firstStep = true;
        boolean playerAlive = true;

        while (playerAlive) {
            System.out.println(ENTRY_MESSAGE);
            String command = sc.nextLine();
            int[] positions;

            try {
                positions = Arrays.stream(command.split("\\s+")).mapToInt(Integer::parseInt).toArray();
            } catch (NumberFormatException e) {
                System.out.println(INVALID_INPUT);
                continue;
            }

            if (!checkIfPositionsAreValid(positions, board.length)) {
                System.out.println(INVALID_BOARD_INDICES);
                continue;
            }

            int row = positions[0];
            int col = positions[1];

            if (firstStep) {
                board[row][col] = VISITED_CELL;
                discoverEmptyCells(board, row, col);
                fillBoardMines(board, minesCount);
                firstStep = false;
            } else if (cellIsVisited(board, row, col)) {
                System.out.println(THIS_CELL_IS_ALREADY_VISITED);
                continue;
            } else {
                if (checkIfCellIsAMine(board, row, col)) {
                    playerAlive = false;
                } else if (checkIfAdjacentMines(board, row, col)) {
                    board[row][col] = countOfAdjacentMines(board, row, col);
                } else {
                    discoverEmptyCells(board, row, col);
                }
            }

            displayBoard(board, playerAlive);
        }
    }

    private static boolean cellIsVisited(String[][] board, int row, int col) {
        return VISITED_CELL.equals(board[row][col]);
    }

    private static void discoverEmptyCells(String[][] board, int row, int col) {
        int visitedCells = getCountOfAdjacentFields(board, row, col, VISITED_CELL);

        if (visitedCells > 0) {
            board[row][col] = VISITED_CELL;
        } else {
            for (int startRow = row - 1; startRow <= row + 1; startRow++) {
                for (int startCol = col - 1; startCol <= col + 1; startCol++) {
                    if (cellIndexIsValidInBoard(startRow, board.length) && cellIndexIsValidInBoard(startCol, board.length)) {
                        board[startRow][startCol] = VISITED_CELL;
                    }
                }
            }
        }
    }

    private static String countOfAdjacentMines(String[][] board, int row, int col) {
        int countOfNearbyMines = getCountOfAdjacentFields(board, row, col, MINE_CELL);
        return String.valueOf(countOfNearbyMines);
    }

    private static int getCountOfAdjacentFields(String[][] board, int row, int col, String field) {
        int count = 0;
        count += checkCellAroundPlayer(board, row - 1, col - 1, field); //topLeft
        count += checkCellAroundPlayer(board, row - 1, col, field); //top
        count += checkCellAroundPlayer(board, row - 1, col + 1, field); //topRight
        count += checkCellAroundPlayer(board, row, col - 1, field); //midLeft
        count += checkCellAroundPlayer(board, row, col + 1, field); //midRight
        count += checkCellAroundPlayer(board, row + 1, col - 1, field); //botLeft
        count += checkCellAroundPlayer(board, row + 1, col, field); //bot
        count += checkCellAroundPlayer(board, row + 1, col + 1, field); //botRight
        return count;
    }

    private static int checkCellAroundPlayer(String[][] board, int row, int col, String cell) {
        if (cellIndexIsValidInBoard(row, board.length) && cellIndexIsValidInBoard(col, board.length)) {
            return (cell.equals(board[row][col]) ? 1 : 0);
        }
        return 0;
    }

    private static boolean checkIfAdjacentMines(String[][] board, int row, int col) {
        return !countOfAdjacentMines(board, row, col).equals(Integer.toString(0));
    }

    private static boolean checkIfPositionsAreValid(int[] positions, int boardSize) {
        return positions.length == 2 &&
                cellIndexIsValidInBoard(positions[0], boardSize) &&
                cellIndexIsValidInBoard(positions[1], boardSize);
    }

    private static boolean cellIndexIsValidInBoard(int index, int boardSize) {
        return index >= 0 && index < boardSize;
    }

    private static boolean checkIfCellIsAMine(String[][] board, int row, int col) {
        return board[row][col].equals(MINE_CELL);
    }

    private static void fillBoardMines(String[][] board, int minesCount) {
        int length = board.length;
        while (minesCount > 0) {
            int randomRow = new Random().nextInt(length);
            int randomCol = new Random().nextInt(length);

            String target = board[randomRow][randomCol];

            if (!target.equals(MINE_CELL) && !target.equals(VISITED_CELL)) {
                minesCount--;
                board[randomRow][randomCol] = MINE_CELL;
            }
        }
    }

    private static String[][] fillBoard(int rows, int cols) {
        String[][] board = new String[rows][cols];

        for (String[] strings : board) {
            Arrays.fill(strings, EMPTY_CELL);
        }

        return board;
    }

    private static void displayBoard(String[][] board, boolean alive) {
        System.out.println(CURRENT_BOARD_STATUS);
        StringBuilder sb = new StringBuilder().append(EMPTY_SPACE).append(EMPTY_SPACE).append(EMPTY_SPACE);

        for (int i = 0; i < board.length; i++) {
            sb.append(i).append(EMPTY_SPACE);
        }

        for (int row = 0; row < board.length; row++) {
            sb.append(System.lineSeparator()).append(row).append(EMPTY_SPACE).append(EMPTY_SPACE);
            for (int col = 0; col < board[row].length; col++) {
                if (!alive) {
                    sb.append(board[row][col]);
                } else {
                    String cell = board[row][col].equals(MINE_CELL) ? EMPTY_CELL : board[row][col];
                    sb.append(cell);
                }
                sb.append(EMPTY_SPACE);
            }
        }

        System.out.println(sb.toString());
        if (!alive) {
            System.out.println(PLAYER_LOST);
        } else {
            boolean hasEmptyCellsInBoard =
                    Arrays
                            .stream(board)
                            .allMatch(col -> Arrays.stream(col).noneMatch(cell -> cell.equals(EMPTY_CELL)));

            if (hasEmptyCellsInBoard) {
                System.out.println(PLAYER_WON);
                System.exit(200);
            }
        }
    }
}

//0
//1 2
//2 3
//3 4
//4 5