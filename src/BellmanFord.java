import java.util.*;

/********************************************/
/********* Task #4 in Bellman Ford  , TODO: multithreaded way **************************/
/********************************************/

public class BellmanFord<T> {

    /**
     * This function finds all paths from source to destination according to the neighbours (up down left and right) of the indices.
     * @param matrix type of Matrix
     * @param source Index
     * @param dest Index
     * @return all paths from source to destination
     */
    public List<List<Index>> allPathsToDestination(Matrix matrix, Index source, Index dest){
        List<List<Index>> result = new ArrayList<>();
        //Set<Index> finished = new HashSet<>();
        Queue<List<Index>> queue = new LinkedList<>();
        queue.add(Arrays.asList(source));
        while(!queue.isEmpty()){
            List<Index> path = queue.poll();
            Index lastIndex = path.get(path.size()-1);

            if(lastIndex.equals(dest)){
                result.add(new ArrayList<>(path));
            } else{
               // finished.add(lastIndex);
                List<Index> neighborIndices = (List<Index>) matrix.getNeighbors(lastIndex);
                for(Index neighbor : neighborIndices){
                    if(!path.contains(neighbor)){
                        List<Index> list = new ArrayList<>(path);
                        list.add(neighbor);
                        queue.add(list);
                    }
                }
            }

        }
        result = filterPaths(result,matrix);
        return result;
    }


    /**
     * this function filters all the paths from source to destination, for each destination we sum the values and we choose the minimum sum
     * then we add all the paths with the minimum sum to filteredResults list which we return.
     * @param result type of List of Lists of Index
     * @param matrix type of Matrix
     * @return filteredResults
     */
    public List<List<Index>> filterPaths(List<List<Index>> result, Matrix matrix){
        int minSum = 0;
        int localSum = 0 ;
        List<Integer> sums = new ArrayList();

        for(List<Index> i : result){
            for(Index j : i){
                 localSum += matrix.getValue(j);
            }
            sums.add(localSum);
            localSum = 0;
        }
        minSum += Collections.min(sums);
        List<List<Index>> filteredResults = new ArrayList<>();
        for(int i=0; i< result.size();i++){
            if(sums.get(i) == minSum){
                filteredResults.add(result.get(i));
            }
        }
        return filteredResults;
    }

    public static void main(String[] args) {
        int[][] source = {
                {300, 999, 1},
                {7, 0, 3},
                {-90, 8, 1}
        };
        Matrix matrix = new Matrix(source);
        BellmanFord bfsLogic = new BellmanFord();
        System.out.println(bfsLogic.allPathsToDestination(matrix, new Index(0,0), new Index(2,1)));

    }



}

