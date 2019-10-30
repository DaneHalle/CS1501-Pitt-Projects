/**
 * This code is a mashup of MinPQ.java and MaxPQ.java provided by the book with changes
 */
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Arrays;
public class MinMaxPQ
{
	private boolean min;
	private apartments[] queue;
	private int n;
	private Comparator<apartments> comparator;
	public MinMaxPQ(boolean isMin, Comparator<apartments> compare)
	{
		min=isMin;
		comparator=compare;
		n=0;
		queue=new apartments[1]; 
	}

	public boolean isEmpty()
	{
		return n==0;
	}

	public int getSize()
	{
		return n;
	}

	public apartments top(){
        if(isEmpty()){
        	throw new NoSuchElementException("Priority queue underflow");
        }
        return queue[1];
    }

    private void resize(){
    	if(n==queue.length-1){
    		queue=Arrays.copyOf(queue, 2*queue.length);
    	}
    	if(n>0&&n==queue.length-1/4){
    		queue=Arrays.copyOf(queue, queue.length/2);
    	}
	}

	public void insert(apartments in)
	{
		resize();
		if(min){
	        queue[++n]=in;
	        swim(n);
	        assert isMinHeap();
		}else{
	        queue[++n]=in;
	        swim(n);
	        assert isMaxHeap();
		}
	}

	public apartments deleteTop()
	{
		if(min){
			if(isEmpty()){ 
				throw new NoSuchElementException("Priority queue underflow");
			}
	        apartments top=queue[1];
	        exch(1, n--);
	        sink(1);
	        queue[n+1]=null;     // to avoid loiterig and help with garbage collection
	        resize();
	        assert isMinHeap();
	        return top;
		}else{
			if(isEmpty()){ 
				throw new NoSuchElementException("Priority queue underflow");
			}
	        apartments top=queue[1];
	        exch(1, n--);
	        sink(1);
	        queue[n+1]=null;     // to avoid loiteing and help with garbage collection
	        resize();
	        assert isMaxHeap();
	        return top;
		}
	}

	public int contains(String street, String apartment, int zip)
	{
		String gottenStreet; 
		String gottenApt; 
		int gottenZip;
		for(int i=1; i<=n; i++){
			gottenStreet=queue[i].getStreetAddress();
			gottenApt=queue[i].getApartmentNumber();
			gottenZip=queue[i].getZipCode();
			if(gottenStreet.equals(street)&&gottenApt.equals(apartment)&&gottenZip==zip){
				return i;
			}
		}
		return -1;
	}

	public apartments get(int i)
	{
		return queue[i];
	}

	public apartments remove(String street, String apartment, int zip)
	{
		int found=contains(street, apartment, zip);
		return remove(found);
	}

	public apartments remove(int found)
	{
		if(found==-1){
			return null;
		}
		if(found==1){
			return deleteTop();
		}else if(min){
			apartments here=queue[found];
			exch(found, n--);
			sink(found);
			queue[n+1]=null;
			resize();
			assert isMinHeap();
			return here;
		}else{
			apartments here=queue[found];
			exch(found, n--);
			sink(found);
			queue[n+1]=null;
			resize();
			assert isMaxHeap();
			return here;
		}
	}

	private void swim(int k)
	{
		if(min){
	        while(k>1&&greater(k/2, k)){
	            exch(k, k/2);
	            k=k/2;
	        }
		}else{
			while(k>1&&less(k/2, k)){
	            exch(k, k/2);
	            k=k/2;
	        }
		}
	}

	private void sink(int k)
	{
		if(min){
			while(2*k<=n){
	            int j=2*k;
	            if(j<n&&greater(j, j+1)){
	            	j++;
	            }
	            if(!greater(k, j)){ 
	            	break;
	            }
	            exch(k, j);
	            k=j;
	        }
		}else{
			while(2*k<=n){
	            int j=2*k;
	            if(j<n&&less(j, j+1)){ 
	            	j++;
	            }
	            if(!less(k, j)){ 
	            	break;
	            }
	            exch(k, j);
	            k=j;
	        }
		}
	}

	private boolean greater(int i, int j)
	{
        if(comparator==null){
            return ((Comparable<apartments>)queue[i]).compareTo(queue[j])>0;
        }else{
            return comparator.compare(queue[i], queue[j])>0;
        }
    }

    private boolean less(int i, int j)
    {
        if(comparator==null){
            return ((Comparable<apartments>)queue[i]).compareTo(queue[j])<0;
        }else{
            return comparator.compare(queue[i], queue[j])<0;
        }
    }

    private void exch(int i, int j)
    {
        apartments swap=queue[i];
        queue[i]=queue[j];
        queue[j]=swap;
    }

    private boolean isMinHeap()
    {
        return isMinHeap(1);
    }

    private boolean isMinHeap(int k)
    {
        if(k>n){ 
	    	return true;
	    }
        int left=2*k;
        int right=2*k+1;
        if(left<=n&&greater(k, left)){
        	return false;
        }
        if(right<=n&&greater(k, right)){ 
        	return false;
        }
        return isMinHeap(left)&&isMinHeap(right);
    }

    private boolean isMaxHeap()
    {
        return isMaxHeap(1);
    }

    private boolean isMaxHeap(int k)
    {
        if(k>n){ 
        	return true;
        }
        int left=2*k;
        int right=2*k+1;
        if(left<=n&&less(k, left)){  
        	return false;
        }
        if(right<=n&&less(k, right)){ 
        	return false;
        }
        return isMaxHeap(left)&&isMaxHeap(right);
    }
}