
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Matrix {
    /**
     * Neighboring Indices are up,down, left,right
     * 1 0 0
     * 0 1 1
     * 0 0 0
     * 1 1 1
     * <p>
     * [[(0,0),
     * [(1,1) ,(1,2)],
     * [(3,0),(3,1),(3,2)]]
     * <p>
     * <p>
     * 1 0 0
     * 0 1 1
     * 0 1 0
     * 0 1 1
     */

    int[][] primitiveMatrix;

    public Matrix(int[][] oArray) {
        List<int[]> list = new ArrayList<>();
        for (int[] row : oArray) {
            int[] clone = row.clone();
            list.add(clone);
        }
        primitiveMatrix = list.toArray(new int[0][]);
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : primitiveMatrix) {
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }


    public Collection<Index> getNeighbors(final Index index) {
        Collection<Index> list = new ArrayList<>();
        int extracted = -1;
        try {
            extracted = primitiveMatrix[index.row + 1][index.column];//down
            list.add(new Index(index.row + 1, index.column));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row][index.column + 1];//right
            list.add(new Index(index.row, index.column + 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row - 1][index.column];//up
            list.add(new Index(index.row - 1, index.column));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row][index.column - 1];//left
            list.add(new Index(index.row, index.column - 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return list;
    }
    public Collection<Index> getCrossNeighbors(final Index index){
        Collection<Index> list = new ArrayList<>();
        list = getNeighbors(index);
        int extracted = -1;
        try {
            extracted = primitiveMatrix[index.row - 1][index.column - 1];//left-up
            list.add(new Index(index.row - 1, index.column - 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row + 1][index.column + 1];//right-down
            list.add(new Index(index.row + 1, index.column + 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row + 1][index.column - 1];//left-down
            list.add(new Index(index.row + 1, index.column - 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row - 1][index.column + 1];//right-up
            list.add(new Index(index.row - 1, index.column + 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return list;
    }

    public Collection<Index> getReachable(Index index) {
        ArrayList<Index> filteredIndices = new ArrayList<>();
        this.getCrossNeighbors(index).stream().filter(i -> getValue(i) == 1)
                .map(neighbor -> filteredIndices.add(neighbor)).collect(Collectors.toList());
        return filteredIndices;
    }

    public final int[][] getPrimitiveMatrix() {
        return primitiveMatrix;
    }

    public int getValue(final Index index) {
        return primitiveMatrix[index.row][index.column];
    }

    public void printMatrix() {
        for (int[] row : primitiveMatrix) {
            String s = Arrays.toString(row);
            System.out.println(s);
        }
    }


    public List<Index> matrixToList(int[][] matrix) {
        ArrayList<Index> list = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++)
                list.add(new Index(i, j));
        }
        return list;
    }

    /********************************************/
    /********* Task #1 **************************/
    /********************************************/

    public ArrayList<Index> getOnes() {
        ArrayList<Index> list = new ArrayList<>();
        this.matrixToList(this.primitiveMatrix).stream().filter(i -> getValue(i) == 1).map(list::add).collect((Collectors.toList()));
        return list;
    }

    public Collection<? extends HashSet<Index>> getAllSCCs() {
        List<Index> listOfOnes = this.getOnes();
        List<HashSet<Index>> multiComponents = new ArrayList<>();
        HashSet<Index> singleSCC;


        while (!listOfOnes.isEmpty()) {
            singleSCC = (HashSet<Index>) getSingleSCC(this, listOfOnes.remove(0));
            multiComponents.add(singleSCC);
            listOfOnes.removeAll(singleSCC);
        }

        return multiComponents.stream().sorted(Comparator.comparing(HashSet::size)).collect(Collectors.toList());
    }

    public Collection<Index> getSingleSCC(Matrix matrix, Index index) {
        TraversableMatrix myTraversableMat = new TraversableMatrix(matrix);
        myTraversableMat.setStartIndex(index);
        ThreadLocalDfsVisit<Index> singleSearch = new ThreadLocalDfsVisit<Index>();
        HashSet<Index> singleSCC = (HashSet<Index>) singleSearch.traverse(myTraversableMat);
        return singleSCC;
    }


    /********************************************/
    /********* Task #1 V2 **************************/
    /********************************************/

    public Collection<? extends HashSet<Index>> getAllSCCs2() {
        List<Index> listOfOnes = this.getOnes();
        List<Index> syncListOfOnes = Collections.synchronizedList(listOfOnes);
        List<HashSet<Index>> multiComponents = Collections.synchronizedList(new ArrayList<HashSet<Index>>());
        List<Index> firstHalfOnes = syncListOfOnes.subList(0, syncListOfOnes.size() / 2);
        List<Index> secondHalfOnes = syncListOfOnes.subList(syncListOfOnes.size() / 2, syncListOfOnes.size() - 1);

        Thread part1 = new Thread(() -> {
            while (!firstHalfOnes.isEmpty()) {
                HashSet<Index> singleSCC = (HashSet<Index>) getSingleSCC(this, firstHalfOnes.remove(0));
                multiComponents.add(singleSCC);
                firstHalfOnes.removeAll(singleSCC);
                //  secondHalfOnes.removeAll(singleSCC);
            }
        });
        Thread part2 = new Thread(() -> {
            while (secondHalfOnes.size() != 0) {
                HashSet<Index> singleSCC2 = (HashSet<Index>) getSingleSCC(this, secondHalfOnes.remove(0));
                multiComponents.add(singleSCC2);
                secondHalfOnes.removeAll(singleSCC2);
                //firstHalfOnes.removeAll(singleSCC);
            }

        });

        part1.start();
        part2.start();
        try {
            part1.join();
            part2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<HashSet<Index>> result = new ArrayList<HashSet<Index>>();
        synchronized (multiComponents) {
            Iterator<HashSet<Index>> it = multiComponents.iterator();

            while (it.hasNext()) {
                result.add(it.next());
            }
        }
        return result;

    }




/********************************************/
    /********* Task #1 V3 **************************/
    /********************************************/

    public Collection<? extends HashSet<Index>> getAllSCCs3() {
        List<HashSet<Index>> result = new ArrayList<>();
        List<Index> listOfOnes = this.getOnes();
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        List<Index> firstHalf = listOfOnes.subList(0, listOfOnes.size()/2);
        List<Index> secondHalf = listOfOnes.subList(listOfOnes.size()/2, listOfOnes.size());
        Callable<List<HashSet<Index>>> task1 = ()-> {
            List<HashSet<Index>> multiComponents = new ArrayList<>();
            while(!firstHalf.isEmpty()){
                HashSet<Index> singleSCC = (HashSet<Index>) getSingleSCC(this, firstHalf.remove(0));
                multiComponents.add(singleSCC);
                firstHalf.removeAll(singleSCC);
            }
            return multiComponents;
        };
        Callable<List<HashSet<Index>>> task2 = ()-> {
            List<HashSet<Index>> multiComponents = new ArrayList<>();
            while(!secondHalf.isEmpty()){
                HashSet<Index> singleSCC = (HashSet<Index>) getSingleSCC(this, secondHalf.remove(0));
                multiComponents.add(singleSCC);
                secondHalf.removeAll(singleSCC);
            }
            return multiComponents;
        };
        Future<List<HashSet<Index>>> firstFutResult = threadPool.submit(task1);
        Future<List<HashSet<Index>>> secondFutResult = threadPool.submit(task2);
        try {
            List<HashSet<Index>> firstRes = firstFutResult.get();
            List<HashSet<Index>> secRes = secondFutResult.get();
            result.addAll(firstRes);
            result.addAll(secRes);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    /********************************************/
    /********* Task #3 **************************/
    /********************************************/

    public int submarines() throws InterruptedException {
        AtomicBoolean isValid = new AtomicBoolean(true);

        //checking for validation Part1 diagonal close rows left up to down right
        Thread validation1 = new Thread(()-> {
            for (int i = 0; i < primitiveMatrix.length - 1; i++)//rows diagonal left-up to he's righter-down index
            {
                if(isValid.get() == false) break;
                for(int j=0; j<primitiveMatrix[0].length-1; j++){
                    if(primitiveMatrix[i][j] == primitiveMatrix[i+1][j+1] && primitiveMatrix[i][j] == 1){
                        if(primitiveMatrix[i+1][j] == 0 || primitiveMatrix[i][j+1] == 0)
                        {
                            isValid.set(false);
                            break;
                        }
                    }
                }
            }
        });

        //checking for validation Part2 diagonal close rows left down to upper right
        Thread validation2 = new Thread(()-> {
            for(int i = 1; i < primitiveMatrix.length -1; i++)
            {
                if(isValid.get() == false) break;
                for(int j=0 ; j<primitiveMatrix[0].length - 1;j++)
                {
                    if(primitiveMatrix[i][j] == primitiveMatrix[i-1][j+1] && primitiveMatrix[i][j] == 1){
                        if(primitiveMatrix[i][j+1] ==0 || primitiveMatrix[i-1][j] == 0){
                            isValid.set(false);
                            break;
                        }
                    }
                }
            }
        });


        //checking for validation part3 , if there is a 1 without any neighbors.
        Thread validation3 = new Thread(()->{
            List<Index> ones = getOnes();
            for ( Index i : ones){
                if(getReachable(i).size() == 0){
                    isValid.set(false);
                }
            }
        });
        validation1.start();
        validation2.start();
        validation3.start();
        validation1.join();
        validation2.join();
        validation3.join();

        List<HashSet<Index>> scc = new ArrayList<>();
        scc.addAll(getAllSCCs());
        if(isValid.get() == true){
            return scc.size();
        }
        else{//is not valid!
            return -1;
        }


    }

    /********************************************/
    /********* Task #3 another version, TODO: multithreaded way **************************/
    /********************************************/

    public int submarinesAnotherVestion(){
        int result = 0;
        List<HashSet<Index>> scc = (List<HashSet<Index>>) getAllSCCs();
        for(HashSet<Index> singleScc : scc){
            result += isValidSubmarine(singleScc);
        }
        return result;
    }

    private int isValidSubmarine(HashSet<Index> scc){
        if(scc.size()==1){
            return 0;
        }

        int rightBound = Collections.max(scc, Comparator.comparingInt(Index::getColumn)).getColumn();
        int leftBound = Collections.min(scc, Comparator.comparingInt(Index::getColumn)).getColumn();
        int topBound = Collections.min(scc, Comparator.comparingInt(Index::getRow)).getRow();
        int bottomBound = Collections.max(scc, Comparator.comparingInt(Index::getRow)).getRow();

        int sizeOfScc = (rightBound - leftBound +1 ) * (bottomBound - topBound + 1);

        if(scc.size()==sizeOfScc){
            return 1;
        }
        return 0;
    }

    /********************************************/
    /********* Task #4 in Bellman Ford  , TODO: multithreaded way **************************/
    /********************************************/



}


