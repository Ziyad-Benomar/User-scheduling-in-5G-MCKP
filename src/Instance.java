import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


// This class transforms a text file to an object we can manipulate in our program.
// This object represents an instance of the LP or IP problem, for which we will try to find a solution.
// The principal method of this class is the constructor

// #) N,K,M are the dimensions of the matrixes p (power) and r (data rate),
// #) P is the budget power
// #) For each n : t[n] is a linked list of triplets (k,m,n) sorted in ascending order of p_kmn. 
// 	  We will use t whenever we need to pass over the triplets (k,m,n).
//	  Hence, removing a triplet from the problem is the same as removing it from t.

public class Instance {
	int N,K,M,P;
	int[][][] p,r;
	Doubly[] t;
	
	// This first constructor will be used only in the class OP (online programming)
	// this constructor assumes that p and r have the same dimensions
	public Instance(int[][][] p, int[][][] r, int P) {
		this.p = p;
		this.r = r;
		this.K = p.length;
		this.M = p[0].length;
		this.N = p[0][0].length;
		this.P = P;
		constructT();
	}
	
	// This constructor is the more important one, it takes in argument the path to a test file 
	// It assumes that the text file has the correct format
	public Instance(String path) throws IOException{
		BufferedReader bf = new BufferedReader(new FileReader(path));
		// Initializing N, K, M and P
		this.N = (int)Float.parseFloat(bf.readLine());
		this.M = (int)Float.parseFloat(bf.readLine());
		this.K = (int)Float.parseFloat(bf.readLine());
		this.P = (int)Float.parseFloat(bf.readLine());
		// constructing p and r
		this.p = constructMatrix_kmn(bf);
		this.r = constructMatrix_kmn(bf);
		bf.close();
		// constructing t
		constructT();
	}
	
	// Construction of p and r ------------------------------------------------------------------
	
	public int[][][] constructMatrix_kmn(BufferedReader bf) throws IOException {
		int[][][] matrix = new int[K][M][N];
		for(int n=0; n<N; n++) {
			for(int k=0; k<K; k++) {
				String[] line = bf.readLine().trim().split("\\s+");
				for (int m=0; m<M; m++) 
					matrix[k][m][n] = (int)Float.parseFloat(line[m]);
			}
		}
		return matrix;
	}
	
	// Methods for easy access to p and r's values
	
	public int p(int k, int m, int n) {
		return this.p[k][m][n];
	}
	
	public int r(int k, int m, int n) {
		return this.r[k][m][n];
	}
	
	// a triplet has attributes k, m, and n
	public int p(Doubly triplet) {
		return this.p[triplet.k][triplet.m][triplet.n];
	}
	public int r(Doubly triplet) {
		return this.r[triplet.k][triplet.m][triplet.n];
	}
	
	// Construction of t -----------------------------------------------------------------------
	
	public void constructT() {
		t = new Doubly[N];
		for (int n=0; n<N; n++) {
			Doubly[] a = new Doubly[K*M];
			int i =0;
			for (int k=0; k<K; k++)
				for (int m=0; m<M; m++)
					a[i++] = new Doubly(k,m,n);
			t[n] = sortAscendingP(a);
		}
	}
	
	// We use the merge-sort algorithm to sort triplets (k,m,n) in ascending order of p_kmn
	// the following method assumes that initially a[i].prev == a[i].next == null for each i
	
	public Doubly sortAscendingP(Doubly[] a) {
		mergeSortRec(a, new Doubly[a.length], 0, a.length);
		// Now that a is sorted, we will link its elements
		for (int i=0; i<a.length-1; i++) {
			a[i].next = a[i+1];
			a[i+1].prev = a[i];
		}
		return a[0];
	}
	public void mergeSortRec(Doubly[] a, Doubly[] tmp, int left, int right) {
		if (left >= right - 1)
			return;
		int med = left +(right - left)/2;
		mergeSortRec(a, tmp, med, right);
		mergeSortRec(a, tmp, left, med);
		for (int i=left; i< right; i++)
			tmp[i] = a[i];
		mergeAscendingP(tmp, a, left, med, right);
	}
	public void mergeAscendingP(Doubly[] a1, Doubly[] a2, int left, int med, int right) {
		int i = left, j = med;
		for (int s=left; s<right; s++) {
			if (i<med && (j == right || p(a1[i]) < p(a1[j]) ))
				a2[s] = a1[i++];
			else
				a2[s] = a1[j++];
		}
	}
	
	// TESTING (NOT USEFUL FOR THE PROJECT) --------------------------------------------------------------------------------
	
	public static void main(String[] args) throws IOException {
		
		// Test the creation of an instance
		Instance ins = new Instance("test3.txt");
		System.out.println("K = " + ins.K);
		System.out.println("M = " + ins.M);
		System.out.println("N = " + ins.N);
		System.out.println("P = " + ins.P);
		for (int n = 0 ; n<ins.N; n++)
			System.out.println("t["+n+"] : " + ins.t[n]);
		for (int n = 0 ; n<ins.N; n++) {
			Doubly s = ins.t[n];
			System.out.print("p_kmn for n = "+n +" : ");
			while (s != null) {
				System.out.print(ins.p(s) +"  ");
				s = s.next;
			}
			System.out.println("");
		}
	}


}
