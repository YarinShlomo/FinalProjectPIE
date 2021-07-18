
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Matrix {
    /**
     * Neighboring Indices are up,down, left,right
     *   1 0 0
     *   0 1 1
     *   0 0 0
     *   1 1 1
     *
     * [[(0,0),
     * [(1,1) ,(1,2)],
     * [(3,0),(3,1),(3,2)]]
     *
     *
     * 1 0 0
     * 0 1 1
     * 0 1 0
     * 0 1 1
     *
     *
     */

    int[][] primitiveMatrix;

    public Matrix(int[][] oArray){
        List<int[]> list = new ArrayList<>();
        for (int[] row : oArray) {
            int[] clone = row.clone();
            list.add(clone);
        }
        primitiveMatrix = list.toArray(new int[0][]);
    }



    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : primitiveMatrix) {
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }



    public Collection<Index> getNeighbors(final Index index){
        Collection<Index> list = new ArrayList<>();
        int extracted = -1;
        try{
            extracted = primitiveMatrix[index.row+1][index.column];//down
            list.add(new Index(index.row+1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row][index.column+1];//right
            list.add(new Index(index.row,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row-1][index.column];//up
            list.add(new Index(index.row-1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row][index.column-1];//left
            list.add(new Index(index.row,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row-1][index.column-1];//left-up
            list.add(new Index(index.row-1,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row+1][index.column+1];//right-down
            list.add(new Index(index.row+1,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row+1][index.column-1];//left-down
            list.add(new Index(index.row+1,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row-1][index.column+1];//right-up
            list.add(new Index(index.row-1,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        return list;
    }

    public Collection<Index> getReachable(Index index) {
        ArrayList<Index> filteredIndices = new ArrayList<>();
        this.getNeighbors(index).stream().filter(i-> getValue(i)==1)
                .map(neighbor->filteredIndices.add(neighbor)).collect(Collectors.toList());
        return filteredIndices;
    }

    public final int[][] getPrimitiveMatrix() {
        return primitiveMatrix;
    }

    public int getValue(final Index index){
        return primitiveMatrix[index.row][index.column];
    }

    public void printMatrix(){
        for (int[] row : primitiveMatrix) {
            String s = Arrays.toString(row);
            System.out.println(s);
        }
    }



    public List<Index> matrixToList(int[][] matrix){
        ArrayList<Index> list = new ArrayList<>();
        for(int i=0; i<matrix.length;i++){
            for(int j=0; j<matrix[i].length;j++)
                list.add(new Index(i,j));
        }
        return list;
    }

    public ArrayList<Index> getOnes(){
        ArrayList<Index> list = new ArrayList<>();
        this.matrixToList(this.primitiveMatrix).stream().filter(i->getValue(i)==1).map(list::add).collect((Collectors.toList()));
        return list;
    }

    public Collection<? extends HashSet<Index>> getAllSCCs() {
        List<Index> listOfOnes = this.getOnes();
        List<HashSet<Index>> multiComponents = new ArrayList<>();
        HashSet<Index> singleSCC;


        while(!listOfOnes.isEmpty()){
            singleSCC = (HashSet<Index>) getSingleSCC(this,listOfOnes.remove(0));
            multiComponents.add(singleSCC);
            listOfOnes.removeAll(singleSCC);
        }

        return multiComponents.stream().sorted(Comparator.comparing(HashSet::size)).collect(Collectors.toList());
    }

    public Collection<Index> getSingleSCC(Matrix matrix , Index index){
        TraversableMatrix myTraversableMat = new TraversableMatrix(matrix);
        myTraversableMat.setStartIndex(index);
        ThreadLocalDfsVisit<Index> singleSearch =new ThreadLocalDfsVisit<Index>();
        HashSet<Index> singleSCC = (HashSet<Index>) singleSearch.traverse(myTraversableMat);
        return singleSCC;
    }

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
                            //TODO: create write lock instead of using AtomicBoolean
                            isValid.set(false);
                            break;
                        }
                    }
                }
            }
        });

        //checking for validation part2 , if there is a 1 without any neighbors.
        Thread validation2 = new Thread(()->{
            for(int i=0;i<primitiveMatrix.length;i++){
                if(isValid.get() == false) break;
                for(int j=0;j<primitiveMatrix[0].length;j++){
                    if(this.getNeighbors(new Index(i, j)).size() == 0) {
                        isValid.set(false);
                        break;
                    }
                }
            }
        });
        validation1.start();
        validation2.start();
        validation1.join();
        validation2.join();

        List<HashSet<Index>> scc = new ArrayList<>();
        scc.addAll(getAllSCCs());
        if(isValid.get() == true){
            return scc.size();
        }
        else{//is not valid!
            return -1;
        }


    }

}
