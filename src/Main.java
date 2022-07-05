import java.util.ArrayList;
import java.util.Random;

class Board {
/*
* The chessboard is represented as an array (named cols).
* The index numbers represent the column where the queen is located,
* and the values held by the index represent the row where the queen is located.
* */

    public static final int N = 8;
    private int[] cols;

    public int restart;

    public int moves;

    private Board[] successors;
    private int heuristic;
    public Board() {
        restart = 0;
        moves = 0;


        this.cols = new int[N];
        Random random = new Random();
        for (int i = 0; i < N; i++) {
            this.cols[i] = random.nextInt(N); // Queens are randomly placed.
        }
        this.heuristic = findHeuristic(); // Calculating heuristic.

        successors = new Board[(N - 1) * N]; // Each queen moves in its own column so there are 56 successors.
    }

    public Board(int[] arr) { // Constructor with parameter.
        this.cols = new int[N];

        for (int i = 0; i < N; i++) {
            this.cols[i] = arr[i];
        }

        this.heuristic = findHeuristic();

        successors = new Board[(N - 1) * N];
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cols[j] == i) {
                    str.append("Q ");
                } else {
                    str.append("* ");
                }
            }
            str.append("\n");
        }

        return str.toString();
    }

    public int findHeuristic() {
    /*
    * In order for the queens not to threaten each other horizontally,
    * there must be different values between 0 and 7 in the array.
    * In order for the queens not to threaten each other on the diagonal,
    * the index difference of the array and the difference of the values held by those indices
    * should not be equal to each other.
    * */
        int count = 0;

        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                int diff = Math.abs(cols[i] - cols[j]);

                if (0 == diff) {
                    count++;
                } else if (diff == Math.abs(i - j)) {
                    count++;
                }
            }
        }

        return count;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public void calcSuccessors() {
    /*
    * Each queen moves in its own column and then the new status is added to the 'successor' array.
    * */
        int[] temp = new int[N];

        for (int i = 0; i < N; i++) {
            temp[i] = cols[i];
        }

        int index = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cols[i] == j) {
                    continue;
                } else {
                    temp[i] = j;
                }
                successors[index] = new Board(temp);
                index++;
            }
        }
    }

    public Board best() { // It returns the board that has minimum heuristic.
        ArrayList<Board> neighbor = new ArrayList<>(); // It keeps the best boards.

        calcSuccessors();

        int min = heuristic;
        for (Board board : successors) {
            if (board.heuristic < min) {

                min = board.heuristic;

                neighbor.clear();
                neighbor.add(board);

            } else if ((board.heuristic == min) && board.heuristic < this.heuristic) {
                neighbor.add(board);
            }
        }


        // It chooses a random board in the best boards and returns it.
        int length = neighbor.size();
        if (0 == length) {
            return null;
        } else {
            Random random = new Random();
            int index = random.nextInt(length);
            return neighbor.get(index);
        }



    }

    public void hillClimb() {

        while (this.heuristic != 0) {

            Board successor = best();

            if (successor == null) {    // If successor value is null, it means stuck on local optimal.
                randomRestart();        // Therefore, it restarts.
                restart++;
            } else {                    // If successor value is not null, it updates heuristic and then
                                        // calculates successors again.

                heuristic = successor.heuristic;

                for (int i = 0; i < N; i++) {
                    cols[i] = successor.cols[i];
                }

                calcSuccessors();

                moves++;


            }

        }


    }


    public void randomRestart() { // It ensures to avoid stuck on local optimal.

        Board successor = new Board();

        for (int i = 0; i < N; i++) {
            cols[i] = successor.cols[i];
        }

        heuristic = successor.heuristic;

        calcSuccessors();

    }
}

public class Main {

    public static final int TEST = 10;

    public static void main(String[] args) {

        for (int i = 0; i < TEST; i++) {

            Board b = new Board();
            long start = System.nanoTime();
            b.hillClimb();
            long finish = System.nanoTime();


            System.out.println("| #" + (i+1) + " | Restart number: "+b.restart+"\t\t| Number of moves: "+b.moves+"\t\t| Time elapsed:  "+ ((finish - start) / 1000000) + "ms");
            System.out.println("+------------------------------+-------------+--------------------+");
        }

    }
}
