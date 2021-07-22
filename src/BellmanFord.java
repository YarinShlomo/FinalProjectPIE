import java.util.*;

public class BellmanFord<T> {
    protected final ThreadLocal<Stack<Node<T>>> stackThreadLocal =
            ThreadLocal.withInitial(Stack::new);
    protected final ThreadLocal<Set<Node<T>>> setThreadLocal =
            ThreadLocal.withInitial(()->new HashSet<>());

    protected void threadLocalPush(Node<T> node){
        stackThreadLocal.get().push(node);
    }

    protected Node<T> threadLocalPop(){
        return stackThreadLocal.get().pop();
    }

    public Set<T> traverse(Traversable<T> partOfGraph){
        threadLocalPush(partOfGraph.getOrigin());
        while(!stackThreadLocal.get().isEmpty()) {
            Node<T> poppedNode = threadLocalPop();
            setThreadLocal.get().add(poppedNode);
            Collection<Node<T>> neighborNode = partOfGraph.getNeighborNodes(poppedNode);
            IntSummaryStatistics stats = neighborNode
                    .stream().flatMap(Collection::stream)
                    .mapToInt(Integer::intValue)
                    .summaryStatistics();
            int min = stats.getMin();
        }
    }

}
