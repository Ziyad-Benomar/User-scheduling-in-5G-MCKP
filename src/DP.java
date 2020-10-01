import java.io.IOException;

// DYNAMIC PROGRAMMING ALGORITHM FOR IP PROBLEM

// Methods of this class answer to Q8, Q9 and partially to Q11
// The principal methods of this class are :
// #) solve()		           : Q8
// #) testSolve()              : Q11
// #) solveAlternative()       : Q9
// #) testSolveAlternative()   : Q11
// R[n0][p] is the maximum data rate for the sup-IP problem : find x[n] for n>=n0, with the left budget power p

public class DP {
	
	
	// FIRST APPROACH ------------------------------------------------------------------------------
	
	// The following method answers to Q8,
	// it solves the IP problem corresponding to ins using a DP algorithm
	// It assumes that ins has already been preprocessed, and hence there exists a solution to the problem	
	
	public static Solution solve(Instance ins) {
		Solution sol = new Solution(ins);
		int[][] R = computeR(ins);
		int p = ins.P;
		for (int n=0; n<ins.N; n++) {
			Doubly d = ins.t[n];
			while (d != null) {
				if (R[n][p] == ins.r(d) + R[n+1][p-ins.p(d)]) {
					sol.x[n] = new Couple(d.k, d.m);
					p = p-ins.p(d);
					d = null;
				} else
					d = d.next;
			}
		}
		return sol;
	}

	// The following method computes the table of maximum data rate for each sub-problem 
	
	public static int[][] computeR(Instance ins) {
		// Initializing R
		int[][] R = new int[ins.N+1][ins.P+1];
		// R[ins.N][p] = R[n][0] = 0 for all values of n and p
		for (int n=ins.N-1; n>=0; n--) {
			for (int p=0; p<ins.P+1; p++) {
				Doubly d = ins.t[n];
				while (d != null && ins.p(d) <= p ) {
					if (R[n][p]< ins.r(d) + R[n+1][p-ins.p(d)])
						R[n][p] = ins.r(d) + R[n+1][p-ins.p(d)];
					d = d.next;
				}
			}
		}
		return R;
	}
	
	// SECOND APPROACH ------------------------------------------------------------------------------
	
	// The following method answers to Q9,
	// it solves the IP problem corresponding to ins using a DP algorithm, and using minimum possible power
	// It assumes that ins has already been preprocessed, and hence there exists a solution to the problem
	
	public static Solution solveAlternative(Instance ins) {
		int U = (int)LP.solve(ins).dataRate();
		Solution sol = new Solution(ins);
		int[][] P = computeP(ins, U);
		int r = U;
		while (P[0][r]==-1)
			r--;
		System.out.println("Max value == " +r);
		for (int n=0; n<ins.N; n++) {
			Doubly d = ins.t[n];
			while (d != null) {
				if (ins.r(d)<=r && P[n+1][r-ins.r(d)]!=-1 && P[n][r] == P[n+1][r-ins.r(d)] + ins.p(d)) {
					sol.x[n] = new Couple(d.k, d.m);
					r = r-ins.r(d);
					d = null;
				} else
					d = d.next;
			}
		}
		return sol;
	}

	// The following function computes the table of minimum possible power achieving a data rate r<=U
	
	public static int[][] computeP(Instance ins, int U){
		int[][] W = new int[ins.N+1][U+1];
		// We initialize the two last line of W
		for (int r=0; r<U+1; r++) {
			// When W[n][r]==-1 then no solution is possible
			W[ins.N-1][r] = Integer.MAX_VALUE;
			Doubly d = ins.t[ins.N-1];
			while (d!= null && ins.r(d)<=r) {
				if (r==ins.r(d) && W[ins.N-1][r] > ins.p(d))
					W[ins.N-1][r] = ins.p(d);
				d = d.next;
			}
			if (W[ins.N-1][r] == Integer.MAX_VALUE)
				W[ins.N-1][r] = -1;
		}
		// We construct the rest of W
		for (int n=ins.N-2; n>=0; n--) {
			for (int r=0; r<U+1; r++) {
				// We compute W[n][r] using the recursion equation
				W[n][r] = -1;
				int min = Integer.MAX_VALUE;
				Doubly d = ins.t[n];
				while (d!=null) {
					if (ins.r(d)>r || W[n+1][r-ins.r(d)]==-1) {
						d = d.next;
						continue;
					}
					if (min > W[n+1][r-ins.r(d)] + ins.p(d))
						min = W[n+1][r-ins.r(d)] + ins.p(d);
					d = d.next;
				}
				if (min != Integer.MAX_VALUE)
					W[n][r] = min;
			}
		}
		return W;
	}
	
	
	
	// TEST METHODS -----------------------------------------------------------------------------
	
	// This method tests the first DP approach
	public static void testSolve(String path) throws IOException {
		Instance ins = new Instance(path);
		if (Preprocessing.preprocess(ins) == false)
			return;
		double ti = System.currentTimeMillis();
		Solution sol = solve(ins);
		double tf = System.currentTimeMillis();
		System.out.println(sol);
		System.out.println("Run time in milliseconds : " + (tf-ti));
	}
	// This method tests the second DP approach
	public static void testSolveAlternative(String path) throws IOException {
		Instance ins = new Instance(path);
		if (Preprocessing.preprocess(ins) == false)
			return;
		double ti = System.currentTimeMillis();
		Solution sol = solveAlternative(ins);
		System.out.println(sol);
		double tf = System.currentTimeMillis();
		System.out.println("Run time in milliseconds : " + (tf-ti));
	}
	
	public static void main(String[] args) throws IOException {
		//Uncomment the commented lines to test the alternative DP algorithm
		
		System.out.println("TEST FILE : test1.txt");
		testSolve("test1.txt");
		//testSolveAlternative("test1.txt");
		
		System.out.println("\nTEST FILE : test2.txt");
		testSolve("test2.txt");
		//testSolveAlternative("test2.txt");
		
		System.out.println("\nTEST FILE : test3.txt");
		testSolve("test3.txt");
		//testSolveAlternative("test3.txt");
		
		System.out.println("\nTEST FILE : test4.txt");
		testSolve("test4.txt");
		//testSolveAlternative("test4.txt");
		
		System.out.println("\nTEST FILE : test5.txt");
		testSolve("test5.txt");
		//testSolveAlternative("test5.txt");
	}
	
}
