import java.util.List;

public class Demo {

   /* public static void main(String[] args) {

        int[][] myArray = {
                {1,1,0,0,0},
                {0,0,1,1,1},
                {1,1,0,0,0}
        };
        *//*
        Goal: Find SCC from index (0,0)
        expected: [(0,0),(0,1),(0,2),(1,2)(1,3),(1,4)]
        1. Create a Matrix object that wraps the myArray object V
        2. Create an instance of TraversableMatrix that gets Matrix object in the constructor V
        3. Set the starting index (origin) of the TraversableMatrix object V
        4. Create an instance of the scanning object V
        5. Invoke traverse method
         *//*

        Matrix matrix = new Matrix(myArray);
        TraversableMatrix myTraversable = new TraversableMatrix(matrix);
        myTraversable.setStartIndex(new Index(0,0));
        ThreadLocalDfsVisit<Index> algorithm = new ThreadLocalDfsVisit<>();
        List<Index> singleSCC = (List<Index>) algorithm.traverse(myTraversable);
        System.out.println(singleSCC);
    }*/
}
