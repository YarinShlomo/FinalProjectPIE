import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        result = filterPathsThreads(result,matrix);
        return result;
    }


    /**
     * this function filters all the paths from source to destination, for each destination we sum the values and we choose the minimum sum
     * then we add all the paths with the minimum sum to filteredResults list which we return.
     * @param result type of List of Lists of Index
     * @param matrix type of Matrix
     * @return filteredResults
     */
/*    public List<List<Index>> filterPaths(List<List<Index>> result, Matrix matrix){
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
    }*/

    /**
     * This function uses multi-threads to achieve maximum effect of calculating values of the lists.
     * In sumLogic we calculate the minimum sum of each path, then we filter and returns the collection of paths with the minimum weight.
     * @param result the lists of all paths to destination
     * @param matrix type of Matrix
     * @return all light-weight-paths from source to destination
     */
    public List<List<Index>> filterPathsThreads(List<List<Index>> result, Matrix matrix) {
        List<List<Index>> filteredResults;
        Map<List<Index>,Integer> pathSum = new HashMap<>();
        final Map<List<Index>,Integer> synPathSum = Collections.synchronizedMap(pathSum);
        final List<List<Index>> synResult = Collections.synchronizedList(result);
        AtomicInteger minPathSum = new AtomicInteger(Integer.MAX_VALUE);

        ThreadPoolExecutor threadPool =
                new ThreadPoolExecutor(2,3,30, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>());

        Runnable sumLogic = ()->{
            List<Index> specificPath = synResult.remove(0);
            AtomicInteger sum= new AtomicInteger(0);
            for(Index i : specificPath){
                sum.addAndGet(matrix.getValue(i));
            }

            if(sum.get() <= minPathSum.get()) {
                minPathSum.set(sum.get());
                synPathSum.put(specificPath, sum.get());
            }

        };

        for(int i=0;i<result.size();i++){
            threadPool.execute(sumLogic);
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE,TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        filteredResults = pathSum.entrySet().stream().filter(i -> i.getValue() == minPathSum.get()).map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return filteredResults;
    }


/*        public static void main(String[] args) {
        int[][] source = {
                {300, 999, 1},
                {7, 0, 3},
                {-90, 8, 1}
        };
        Matrix matrix = new Matrix(source);
        BellmanFord bfsLogic = new BellmanFord();
        System.out.println(bfsLogic.allPathsToDestination(matrix, new Index(0,0), new Index(2,1)));

    }*/



}

