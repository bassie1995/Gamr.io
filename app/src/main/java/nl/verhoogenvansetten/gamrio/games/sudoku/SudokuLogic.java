package nl.verhoogenvansetten.gamrio.games.sudoku;

import android.util.Log;

import java.util.Random;

/**
 *  Class for generating and checking sudokus
 */

public class SudokuLogic {

    private final static int[][] defaultGrid = new int[9][9];

    static{
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                defaultGrid[i][j] = (i%3*3+i/3+j)%9+1;
            }
        }
    }

    public static void printGrid(int[][] grid){
        if(grid == null) grid = defaultGrid;

        for (int i = 0; i < grid.length; i++){
            String temp = "";
            for (int j = 0; j < grid[i].length; j++){
                temp += grid[i][j];
                if((j+1)%3==0) temp += " ";
            }
            print(temp);
            if((i+1)%3==0) print("\n");
        }
    }

    private static void print(String s){
        Log.d("SudokuLogic", s);
    }

    static int[][] generateCompletedSudoku(){
        int[] rows = getShuffledArray();
        int[] columns = getShuffledArray();
        int[][] res = new int[9][9];

        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                res[i][j] = defaultGrid[rows[i]][columns[j]];
            }
        }
        print("Sudoku approved: " + checkSudoku(res));
        return res;
    }

    public static int[][] removeNumbers(int[][] sudoku, int i){
        if(i >= 64){
            return null;
        }else if(i%2 == 0){
            i--;
        }
        int amountRemoved = 0;

        while(amountRemoved < i){
            Random random = new Random();
            int randomRow = random.nextInt(9);
            int randomColumn = random.nextInt(9);

            if(sudoku[randomRow][randomColumn] != 0){
                sudoku[randomRow][randomColumn] = 0;
                amountRemoved++;
            }
        }


        return sudoku;
    }

    private static int[] getShuffledArray(){
        int[][] array = new int[][] {{1, 2, 3}, {0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
        int[] res = new int[9];
        for(int i = 0; i < array.length; i++){
            array[i] = shuffleArray(array[i]);
        }

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                //System.arraycopy( array[array[0][i]], j, res, i*3+j , 3); #problems
                res[i*3+j] = array[array[0][i]][j];
            }
        }
        return res;
    }

    private static int[] shuffleArray(int[] array){
        int index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--){
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
        return array;
    }

    public static boolean checkSudoku(int[][] sudoku){
        return checkRows(sudoku) && checkColumns(sudoku) && checkBlocks(sudoku);
    }

    private static boolean checkRows(int[][] sudoku){
        for(int i = 0; i < 9; i++){
            int[] seen = new int[9];
            for (int j = 0; j < 9; j++) {
                seen[sudoku[i][j]-1]++;
            }
            for(int s : seen){
                if(s == 0){
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkColumns(int[][] sudoku){
        for(int i = 0; i < 9; i++){
            int[] seen = new int[9];
            for (int j = 0; j < 9; j++) {
                seen[sudoku[j][i]-1]++;
            }
            for(int s : seen){
                if(s == 0){
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkBlocks(int[][] sudoku){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                int[] seen = new int[9];
                for(int k = 0; k < 3; k++){
                    for(int l = 0; l < 3; l++){
                        seen[sudoku[i*3+k][j*3+l]-1]++;
                    }
                }
                for(int s : seen){
                    if(s == 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
