
// Objects of this class are doubly linked lists of triplets.
// We constructed this class just to have a precise control on the complexities of all our methods
// The attribute lpDominated is used to determine which triplets are LP-dominated, since we shoudn't remove them from the list

public class Doubly {
	int k,m,n;
	boolean lpDominated;
	Doubly next,prev;
	
	// BASIC METHODS ----------------------------------------------------------------------------
	
	// Constructs a unique mesh/triple
	public Doubly(int k, int m, int n) {
		this.k = k;
		this.m = m;
		this.n = n;
		this.lpDominated = false;
		this.next = this.prev = null;
	}
	
	// Constructs linked meshes/triples in the same order given by l, l[i] = [k_i, m_i, n_i]
	public Doubly(int[][] l) {
		this.k = l[0][0];
		this.m = l[0][1];
		this.n = l[0][2];
		for(int i=l.length-1; i>0; i--) {
			this.insertAfter(l[i][0], l[i][1], l[i][2]);
		}
	}
	
	// Inserts a new mesh/triple (k,m,n) after the current object
	public void insertAfter(int k, int m, int n) {
		Doubly d = new Doubly(k,m,n);
		d.next = this.next;
		d.prev = this;
		this.next = d;
		if (d.next!=null) d.next.prev = d;
	}
	
	// Inserts a new mesh/triple (k,m,n) before the current object
	public void insertBefore(int k, int m, int n) {
		Doubly d = new Doubly(k,m,n);
		d.prev = this.prev;
		d.next = this;
		this.prev = d;
		if (d.prev!=null) d.prev.next = d;
	}
	
	// removes the current mesh, and links its previous and next mesh
	public void remove() {
		if (this.prev != null) this.prev.next = this.next;
		if (this.next != null) this.next.prev = this.prev;
	}
	
	// returns the size of the triplets linked list beginning with the current mesh
	public int size() {
		int size = 0;
		Doubly s = this;
		while (s != null) {
			s = s.next;
			size++;
		}
		return size;
	}
	
	// returns the size of a list of Doublys, which is the sum of the sizes of each Doubly of the list
	public static int size(Doubly[] t) {
		int s = 0;
		for(int n=0; n<t.length; n++)
			s += t[n].size();
		return s;
	}
	
	// Returns a string corresponding to the triplets linked list beginning with the current mesh
	public String toString() {
		String s = "";
		System.out.print("[HEAD] --> ");
		Doubly d = this;
		while (d != null) {
			s += "("+d.k+","+d.m+","+d.n + ") --> ";
			d = d.next;
		}
		s += "[TAIL] .";
		return s;
	}
	
	// METHODS FOR THE LP PROBLEM -------------------------------------------------------------
	
	// When working on a LP problem, LP-dominated terms will be ignored
	// These two methods are used in Preprocessing.removeLPdominated()
	
	// returns the next triplet not LP-dominated (having lpDominated==false)
	public Doubly nextLP() {
		Doubly s = this.next;
		while( s != null && s.lpDominated == true)
			s = s.next;
		return s;
	}
	
	// returns the previous triplet not LP-dominated (having lpDominated==false)
	public Doubly prevLP() {
		Doubly s = this.prev;
		while(s != null && s.lpDominated == true)
			s = s.prev;
		return s;
	}
	
	// TEST METHODS (NOT USE FOR THE PROJECT) ----------------------------------------------------------------------

	public static void main(String[] args) {
		// Test insert, remove
		Doubly d = new Doubly(4,5,6);
		d.insertAfter(13,14,15);
		d.next.insertBefore(10, 11, 12);
		d.insertAfter(7, 8, 9);
		d.insertBefore(1, 2, 3);
		System.out.println(d);
		System.out.println(d.size());
		
		// Test constructor
		int[][] l = new int[3][3];
		l[0] = new int[]{1,2,3};
		l[1] = new int[] {4,5,6};
		l[2] = new int[] {7,8,9};
		
		Doubly d2 = new Doubly(l);
		Doubly s = d2.next;
		s.prev.next = null;
		System.out.println(d2);
		System.out.println(d2.size());
	}
	
}
