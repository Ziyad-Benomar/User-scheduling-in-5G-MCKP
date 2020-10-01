import java.io.IOException;
import java.util.ArrayList;

// GREEDY ALGORITHM FOR THE LP PROBLEM

// Methods of this class answer to Q6 and Q7
// the principal methods of this class are :
// #) solve()	    : Q6
// #) testSolve()   : Q7
// the methods solveSubLP() and constructSortedE() are used in this class and also in the class BB

public class LP {

	// The following method answers to Q6
	// It solves the LP problem corresponding to ins, using the greedy algorithm described in the method after.
	// It assumes that ins has already been preprocessed, and hence there exists a solution to the problem
	
	public static Solution solve(Instance ins) {
		Solution sol = new Solution(ins);
		solveSubLP(sol, constructSortedE(ins), 0, ins.P);
		return sol;
	}
	
	// The following method solves a sub-LP problem of ins, with the additional constraints :
	// #) values of sol.x_kmn for n<n0 are fixed (we have a partial solution)
	// #) the budget power is leftBudgetPower
	// It returns false if the sub-problem(n0, leftBudgetPower) has no solution and true otherwise
	// It assumes that ins has already been preprocessed
	
	// sol is a partial solution for n<n0, this method completes it into a solution of the initial LP problem
	// sortedE is a linked list of triplets that are not LP-dominated sorted in decreasing order of e_kmn
	
	public static boolean solveSubLP(Solution sol, Doubly sortedE, int n0, int leftBudgetPower) {
		Instance ins = sol.ins;
		// Initially we take the solution using minimum power (it exists because we have already done the preprocessing)
		int usedPower = 0;
		for (int n=n0; n<ins.N; n++) {
			sol.x[n] = new Couple(ins.t[n].k, ins.t[n].m);
			usedPower += ins.p(ins.t[n]);
		}
		if (usedPower > leftBudgetPower)
			return false;
		Doubly d = sortedE;
		// take the first d having n>n0, because we have a partial solution for n<n0
		// Note that since we pass over sortedE, LP-dominated terms are ignored because they are not in sortedE
		while (d!= null && d.n < n0)
			d = d.next;
		// At each step, we improve our solution by including the triplet maximizing e_kmn
		while (d!=null && (usedPower < leftBudgetPower) ) {
			// we are sure that d.n >= n0
			int n = d.n;
			// The following condition is equivalent to saying that we can replace x[n] by (d.k, d.m)
			if (usedPower + ins.p(d) - ins.p(sol.x[n].k, sol.x[n].m, n) <= leftBudgetPower) {
				usedPower = usedPower + ins.p(d) - ins.p(sol.x[n].k, sol.x[n].m, n);
				sol.x[d.n] = new Couple(d.k, d.m);
			} else {
				// If we can't, replace x[n] by 
				int p_kmn = ins.p(sol.x[n].k, sol.x[n].m, n);
				float lambda = (float)(leftBudgetPower - usedPower)/(ins.p(d) - p_kmn);
				sol.convexCombination(d.k, d.m, n, lambda);
				usedPower = leftBudgetPower;
			}
			// take the next d not LP-dominated having n>n0, because we have a partial solution for n<n0
			do {
			d = d.next;
			} while (d!=null && d.n < n0);
		}
		return true;
	}
	
	// CONSTRUCTION OF sortedE -----------------------------------------------------------------
	
	// This method returns a linked list of the triplets of ins.t sorted in decreasing order of e_kmn
	// It assumes ins has already been preprocessed
	
	public static Doubly constructSortedE(Instance ins) {
		Doubly[] tLP = Preprocessing.removeLPdominated(ins);
		int size = Doubly.size(tLP) - tLP.length;
		// a is the table we will sort in decreasing order of e
		Doubly[] a = new Doubly[size];
		int i = 0;
		for (int n=0; n<tLP.length; n++) {
			Doubly d = tLP[n].next;
			while (d!=null) {
				a[i++] = d;
				d = d.next;
			}
		}
		return sortDecreasingE(ins, a);
	}

	// We use the merge-sort algorithm to sort triplets (k,m,n) in decreasing order of e_kmn
	
	public static Doubly sortDecreasingE(Instance ins, Doubly[] a) {
		mergeSortRec(ins, a, new Doubly[a.length], 0, a.length);
		// Now that a is sorted, we will link its elements
		// We did not link them before in order to compute correctly the values e_kmn (see next methods)
		a[0].prev = null;
		for (int i=0; i<a.length-1; i++) {
			a[i].next = a[i+1];
			a[i+1].prev = a[i];
		}
		a[a.length - 1].next = null;
		return a[0];
	}
	public static void mergeSortRec(Instance ins, Doubly[] a, Doubly[] tmp, int left, int right) {
		if (left >= right - 1)
			return;
		int med = left +(right - left)/2;
		mergeSortRec(ins, a, tmp, med, right);
		mergeSortRec(ins, a, tmp, left, med);
		for (int i=left; i< right; i++)
			tmp[i] = a[i];
		mergeDescendingE(ins, tmp, a, left, med, right);
	}
	public static void mergeDescendingE(Instance ins, Doubly[] a1, Doubly[] a2, int left, int med, int right) {
		int i = left, j = med;
		for (int s=left; s<right; s++) {
			if (i<med && (j == right || e(ins, a1[i]) > e(ins, a1[j]) ))
				a2[s] = a1[i++];
			else
				a2[s] = a1[j++];
		}
	}
	
	// for the following method, d in argument is supposed to be a mesh of ins.t[n] for a certain n
	
	public static double e(Instance ins, Doubly d) {
		return (double)(ins.r(d) - ins.r(d.prev))/(ins.p(d) - ins.p(d.prev));
	}
	
	// TEST METHODS ---------------------------------------------------------------------
	
	// The following method answers to Q7
	// It takes in argument the path to a test file, and prints the solution of the corresponding LP problem
	
	public static void testSolve(String path) throws IOException {
		Instance ins = new Instance(path);
		double ti = System.currentTimeMillis();
		if (Preprocessing.preprocess(ins) == false)
			return;
		Solution sol = solve(ins);
		double tf = System.currentTimeMillis();
		System.out.println(sol);
		System.out.println("Run time in milliseconds : " + (tf-ti));
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("TEST FILE : test1.txt");
		testSolve("test1.txt");
		System.out.println("\nTEST FILE : test2.txt");
		testSolve("test2.txt");
		System.out.println("\nTEST FILE : test3.txt");
		testSolve("test3.txt");
		System.out.println("\nTEST FILE : test4.txt");
		testSolve("test4.txt");
		System.out.println("\nTEST FILE : test5.txt");
		testSolve("test5.txt");
	}
}
