import java.util.Queue;     import java.util.Stack;
import java.util.Iterator;  import java.util.LinkedList;
import java.util.NoSuchElementException;
//Alterations to book code of Dijkstra and Prims to make my life easier. Plus depenent code for Dijkstras 
public class Dijkstra 
{
    private DijkstraSP[] all;
    private static final String RESET="\u001B[0m";
    private static final String RED="\033[1;31m";
    /**
     * Computes a shortest paths tree from each vertex to to every other vertex in
     * the edge-weighted digraph {@code G}.
     * @param G the edge-weighted digraph
     * @throws IllegalArgumentException if an edge weight is negative
     * @throws IllegalArgumentException unless {@code 0<=s<V}
     */
    public Dijkstra(NetworkList G)
    {
        all=new DijkstraSP[G.getV()];
        for(int v=0; v<G.getV(); v++)
            all[v]=new DijkstraSP(G, v);
    }

    /**
     * Returns a shortest path from vertex {@code s} to vertex {@code t}.
     * @param  s the source vertex
     * @param  t the destination vertex
     * @return a shortest path from vertex {@code s} to vertex {@code t}
     *         as an iterable of edges, and {@code null} if no such path
     * @throws IllegalArgumentException unless {@code 0<=s<V}
     * @throws IllegalArgumentException unless {@code 0<=t<V}
     */
    public Iterable<NetworkData> path(int s, int t)
    {
        validateVertex(s);  validateVertex(t);
        return all[s].pathTo(t);
    }

    /**
     * Is there a path from the vertex {@code s} to vertex {@code t}?
     * @param  s the source vertex
     * @param  t the destination vertex
     * @return {@code true} if there is a path from vertex {@code s} 
     *         to vertex {@code t}, and {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0<=s<V}
     * @throws IllegalArgumentException unless {@code 0<=t<V}
     */
    public boolean hasPath(int s, int t)
    {
        validateVertex(s);  validateVertex(t);
        return dist(s, t)<Double.POSITIVE_INFINITY;
    }

    /**
     * Returns the length of a shortest path from vertex {@code s} to vertex {@code t}.
     * @param  s the source vertex
     * @param  t the destination vertex
     * @return the length of a shortest path from vertex {@code s} to vertex {@code t};
     *         {@code Double.POSITIVE_INFINITY} if no such path
     * @throws IllegalArgumentException unless {@code 0<=s<V}
     * @throws IllegalArgumentException unless {@code 0<=t<V}
     */
    public double dist(int s, int t)
    {
        validateVertex(s);  validateVertex(t);
        return all[s].distTo(t);
    }

    // throw an IllegalArgumentException unless {@code 0<=v<V}
    private void validateVertex(int v)
    {
        int V=all.length;
        if(v<0||v>=V)
            throw new IllegalArgumentException(RED+"vertex "+v+" is not between 0 and "+(V-1)+RESET);
    }
}

class DijkstraSP 
{
    private double[] distTo;        // distTo[v]=distance  of shortest s->v path
    private NetworkData[] edgeTo;   // edgeTo[v]=last edge on shortest s->v path
    private IndexMinPQ<Double> pq;  // priority queue of vertices
    private static final String RESET="\u001B[0m";
    private static final String RED="\033[1;31m";
    /**
     * Computes a shortest-paths tree from the source vertex {@code s} to every other
     * vertex in the edge-weighted digraph {@code G}.
     * @param  G the edge-weighted digraph
     * @param  s the source vertex
     * @throws IllegalArgumentException if an edge weight is negative
     * @throws IllegalArgumentException unless {@code 0<=s<V}
     */
    public DijkstraSP(NetworkList G, int s)
    {
        for(NetworkData e : G.getEdges()){
            if(e.getLatency()<0)
                throw new IllegalArgumentException(RED+"edge "+e+" has negative weight"+RESET);
        }
        distTo=new double[G.getV()];    edgeTo=new NetworkData[G.getV()];
        validateVertex(s);
        for (int v=0; v<G.getV(); v++)
            distTo[v]=Double.POSITIVE_INFINITY;
        distTo[s]=0.0;
        // relax vertices in order of distance from s
        pq=new IndexMinPQ<Double>(G.getV());
        pq.insert(s, distTo[s]);
        while(!pq.isEmpty()){
            int v=pq.delMin();
            for(NetworkData e : G.getAtV(v))
                relax(e);
        }
        // check optimality conditions
        assert check(G, s);
    }

    // relax edge e and update pq if changed
    private void relax(NetworkData e)
    {
        int v=e.getStart(), w=e.getEnd();
        if(distTo[w]>distTo[v]+e.getLatency()){
            distTo[w]=distTo[v]+e.getLatency(); edgeTo[w]=e;
            if(pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }

    /**
     * Returns the length of a shortest path from the source vertex {@code s} to vertex {@code v}.
     * @param  v the destination vertex
     * @return the length of a shortest path from the source vertex {@code s} to vertex {@code v};
     *         {@code Double.POSITIVE_INFINITY} if no such path
     * @throws IllegalArgumentException unless {@code 0<=v<V}
     */
    public double distTo(int v)
    {
        validateVertex(v);
        return distTo[v];
    }

    /**
     * Returns true if there is a path from the source vertex {@code s} to vertex {@code v}.
     * @param  v the destination vertex
     * @return {@code true} if there is a path from the source vertex
     *         {@code s} to vertex {@code v}; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0<=v<V}
     */
    public boolean hasPathTo(int v)
    {
        validateVertex(v);
        return distTo[v]<Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path from the source vertex {@code s} to vertex {@code v}.
     * @param  v the destination vertex
     * @return a shortest path from the source vertex {@code s} to vertex {@code v}
     *         as an iterable of edges, and {@code null} if no such path
     * @throws IllegalArgumentException unless {@code 0<=v<V}
     */
    public Iterable<NetworkData> pathTo(int v)
    {
        validateVertex(v);
        if(!hasPathTo(v)) return null;
        LinkedList<NetworkData> path=new LinkedList<NetworkData>();
        for(NetworkData e=edgeTo[v]; e!=null; e=edgeTo[e.getStart()]){
            path.add(0, e);
        }
        return path;
    }


    // check optimality conditions:
    // (i) for all edges e:            distTo[e.getEnd()]<=distTo[e.getStart()]+e.weight()
    // (ii) for all edge e on the SPT: distTo[e.getEnd()]==distTo[e.getStart()]+e.weight()
    private boolean check(NetworkList G, int s)
    {
        // check that edge weights are nonnegative
        for(NetworkData e : G.getEdges()){
            if(e.getLatency()<0){
                System.err.println(RED+"negative edge weight detected"+RESET);
                return false;
            }
        }
        // check that distTo[v] and edgeTo[v] are consistent
        if(distTo[s]!=0.0||edgeTo[s]!=null){
            System.err.println(RED+"distTo[s] and edgeTo[s] inconsistent"+RESET);
            return false;
        }
        for(int v=0; v<G.getV(); v++){
            if(v==s) continue;
            if(edgeTo[v]==null&&distTo[v]!=Double.POSITIVE_INFINITY){
                System.err.println(RED+"distTo[] and edgeTo[] inconsistent"+RESET);
                return false;
            }
        }
        // check that all edges e=v->w satisfy distTo[w]<=distTo[v]+e.weight()
        for(int v=0; v<G.getV(); v++){
            for(NetworkData e : G.getAtV(v)){
                int w=e.getEnd();
                if(distTo[v]+e.getLatency()<distTo[w]){
                    System.err.println(RED+"edge "+e+" not relaxed"+RESET);
                    return false;
                }
            }
        }
        // check that all edges e=v->w on SPT satisfy distTo[w]==distTo[v]+e.weight()
        for (int w=0; w<G.getV(); w++){
            if(edgeTo[w]==null) continue;
            NetworkData e=edgeTo[w];
            int v=e.getStart();
            if(w!=e.getEnd()) return false;
            if(distTo[v]+e.getLatency()!=distTo[w]){
                System.err.println(RED+"edge "+e+" on shortest path not tight"+RESET);
                return false;
            }
        }
        return true;
    }

    // throw an IllegalArgumentException unless {@code 0<=v<V}
    private void validateVertex(int v)
    {
        int V=distTo.length;
        if(v<0||v>=V)
            throw new IllegalArgumentException(RED+"vertex "+v+" is not between 0 and "+(V-1)+RESET);
    }
}

class IndexMinPQ<Key extends Comparable<Key>>
{
    private int maxN;        // maximum number of elements on PQ
    private int n;           // number of elements on PQ
    private int[] pq;        // binary heap using 1-based indexing
    private int[] qp;        // inverse of pq - qp[pq[i]]=pq[qp[i]]=i
    private Key[] keys;      // keys[i]=priority of i
    private static final String RESET="\u001B[0m";
    private static final String RED="\033[1;31m";
    /**
     * Initializes an empty indexed priority queue with indices between {@code 0}
     * and {@code maxN - 1}.
     * @param  maxN the keys on this priority queue are index from {@code 0}
     *         {@code maxN - 1}
     * @throws IllegalArgumentException if {@code maxN<0}
     */
    @SuppressWarnings("unchecked")
    public IndexMinPQ(int maxN)
    {
        if(maxN<0) throw new IllegalArgumentException();
        this.maxN=maxN; n=0;
        keys=(Key[]) new Comparable[maxN+1];    // make this of length maxN??
        pq=new int[maxN+1]; qp=new int[maxN+1]; // make this of length maxN??
        for(int i=0; i<=maxN; i++)
            qp[i]=-1;
    }

    /**
     * Returns true if this priority queue is empty.
     * @return {@code true} if this priority queue is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty()
    {
        return n==0;
    }

    /**
     * Is {@code i} an index on this priority queue?
     * @param  i an index
     * @return {@code true} if {@code i} is an index on this priority queue;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0<=i<maxN}
     */
    public boolean contains(int i)
    {
        if(i<0||i>=maxN) throw new IllegalArgumentException();
        return qp[i]!=-1;
    }

    /**
     * Associates key with index {@code i}.
     * @param  i an index
     * @param  key the key to associate with index {@code i}
     * @throws IllegalArgumentException unless {@code 0<=i<maxN}
     * @throws IllegalArgumentException if there already is an item associated
     *         with index {@code i}
     */
    public void insert(int i, Key key)
    {
        if(i<0||i>=maxN) throw new IllegalArgumentException();
        if(contains(i)){ 
            throw new IllegalArgumentException(RED+"index is already in the priority queue"+RESET);
        }
        n++;
        qp[i]=n;        pq[n]=i;
        keys[i]=key;    swim(n);
    }

    /**
     * Removes a minimum key and returns its associated index.
     * @return an index associated with a minimum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int delMin()
    {
        if(n==0) throw new NoSuchElementException(RED+"Priority queue underflow"+RESET);
        int min=pq[1];  exch(1, n--);   sink(1);
        assert min==pq[n+1];
        qp[min]=-1;        // delete
        keys[min]=null;    // to help with garbage collection
        pq[n+1]=-1;        // not needed
        return min;
    }

    /**
     * Decrease the key associated with index {@code i} to the specified value.
     * @param  i the index of the key to decrease
     * @param  key decrease the key associated with index {@code i} to this key
     * @throws IllegalArgumentException unless {@code 0<=i<maxN}
     * @throws IllegalArgumentException if {@code key>=keyOf(i)}
     * @throws NoSuchElementException no key is associated with index {@code i}
     */
    public void decreaseKey(int i, Key key)
    {
        if(i<0||i>=maxN) throw new IllegalArgumentException();
        if(!contains(i)) throw new NoSuchElementException(RED+"index is not in the priority queue"+RESET);
        if(keys[i].compareTo(key)<=0){
            String err=RED+"Calling decreaseKey() with given argument would not ";
                err+="strictly decrease the key"+RESET;
            throw new IllegalArgumentException(err);
        }
        keys[i]=key;    swim(qp[i]);
    }

    /***************************************************************************
     * General helper functions.
     ***************************************************************************/
    private boolean greater(int i, int j)
    {
        return keys[pq[i]].compareTo(keys[pq[j]])>0;
    }

    private void exch(int i, int j)
    {
        int swap=pq[i];
        pq[i]=pq[j];    pq[j]=swap;
        qp[pq[i]]=i;    qp[pq[j]]=j;
    }

    /***************************************************************************
     * Heap helper functions.
     ***************************************************************************/
    private void swim(int k)
    {
        while(k>1&&greater(k/2, k)){
            exch(k, k/2);   k=k/2;
        }
    }

    private void sink(int k)
    {
        while(2*k<=n){
            int j=2*k;
            if(j<n&&greater(j, j+1)) j++;
            if(!greater(k, j)) break;
            exch(k, j); k=j;
        }
    }
}

//Alteration of given PrimMST code
class Prims 
{
    private NetworkData[] edgeTo; //Shortest edge from tree vertex to non-tree vertex
    private double[] latencyTo;      // latencyTo[v]=getTime of shortest such edge
    private boolean[] marked;     // marked[v]=true if v on tree, false otherwise
    private IndexMinPQ<Double> pq;
    private static final String RESET="\u001B[0m";
    private static final String RED="\033[1;31m";
    private static final String PURPLE="\u001B[35m";
    private static final String CYAN="\u001B[36m";
    /**
     * Compute a minimum spanning tree (or forest) of an edge-getTimed graph.
     * @param NetList the edge-getTimeed graph
     */
    public Prims(NetworkList NetList) 
    {
        edgeTo=new NetworkData[NetList.getV()];
        latencyTo=new double[NetList.getV()];
        marked=new boolean[NetList.getV()];
        pq=new IndexMinPQ<Double>(NetList.getV());
        for(int v=0; v<NetList.getV(); v++)
            latencyTo[v]=Double.POSITIVE_INFINITY;
        for(int v=0; v<NetList.getV(); v++)      // run from each vertex to find
            if(!marked[v]) prim(NetList, v);      // minimum spanning forest
    }

    // run Prim's algorithm in NetworkList NetList, starting from vertex s
    private void prim(NetworkList NetList, int s) 
    {
        latencyTo[s]=0.0;
        pq.insert(s, latencyTo[s]);
        while(!pq.isEmpty()){
            int v=pq.delMin();  scan(NetList, v);
        }
    }

    // scan vertex v
    private void scan(NetworkList NetList, int v) 
    {
        marked[v]=true;
        for(NetworkData edge : NetList.getAtV(v)) 
        {
            int w=edge.getOther(v);
            if(marked[w]) continue;         // v-w is obsolete edge
            if(edge.getLatency()<latencyTo[w]){
                latencyTo[w]=edge.getLatency();
                edgeTo[w]=edge;
                if(pq.contains(w)) pq.decreaseKey(w, latencyTo[w]);
                else                pq.insert(w, latencyTo[w]);
            }
        }
    }

    //returns MST of list
    public String toString() 
    {
        String s="";
        for(int i=0; i<edgeTo.length; i++){
            NetworkData edge=edgeTo[i];
            if(edge==null)
                continue;
            int v1=edge.getStart();    int v2=edge.getOther(v1);
            int via=v1;
            if(v1==i)
                via=v2;
            double latency=latencyTo[i];
            s+=PURPLE+"["+CYAN+i+PURPLE+"] <--> ["+CYAN+via+PURPLE+"] with a latency ";
                s+="of "+CYAN+latency+PURPLE+" ns.\n";
        }
        return s;
    }
}