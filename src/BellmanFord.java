import java.util.*;

public class BellmanFord<T> {

    public List<List<Index>> allPathsToDestanation(Matrix matrix, Index source, Index dest){
        //note by vika
        List<List<Index>> result = new ArrayList<>();
        Set<Index> finished = new HashSet<>();
        Queue<List<Index>> queue = new LinkedList<>();
        queue.add(Arrays.asList(source));
        while(!queue.isEmpty()){
            List<Index> path = queue.poll();
            Index lastIndex = path.get(path.size()-1);

            if(lastIndex.equals(dest)){
                result.add(new ArrayList<>(path));
            } else{
                finished.add(lastIndex);
                List<Index> reachableIndices = (List<Index>) matrix.getNeighbors(lastIndex);
                for(Index neighbor : reachableIndices){
                    if(!finished.contains(neighbor)){
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

    public List<List<Index>> filterPaths(List<List<Index>> result, Matrix matrix){
        int minSum = 0;
        int localSum = 0 ;
        List<Integer> sums = new ArrayList();
        /*int minSize = singleFiltered.size();
        result.removeIf(singleArray -> singleArray.size() > minSize);*/
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
        System.out.println(bfsLogic.allPathsToDestanation(matrix, new Index(0,0), new Index(2,1)));

    }



}

