import java.io.IOException;

// BRANCH-AND-BOUND ALGORITHM FOR IP PROBLEM

// Methods of this class answer to Q10 and partially to Q11
// The principal methods of this class are :
// #) solve()		: Q10
// #) testSolve()   : Q11

public class BB {
	Solution bestSolution;
	int bestDataRate;
	Doubly sortedE;
	// Since bestSolution will always be an ILP solution during the calls of the recursive function,
	// We won't need its attribute k, therefore we can use it to count the number of calls of solveRecBB()
	
	public BB(Instance ins){
		// We construct the linked list sortedE as we did in the class LP
		this.sortedE = LP.constructSortedE(ins);
		// We initialize bestSolution with an existing solution for the LP problem
		this.bestSolution = new Solution(ins);
		LP.solveSubLP(bestSolution, sortedE, 0, ins.P);
		// We used solveSubLP rather than LP.solve() to avoid recomputing sortedE
		// we transform it to a feasible solution for the IP problem by removing the non-integer term
		bestSolution.convexCombination(0, 0, 0 , 0);
		// And now we have a good initial upper bound 
		this.bestDataRate = (int)bestSolution.dataRate();
	}
	
	// The following method answers to Q10,
	// it solves the IP problem corresponding to ins using the Branch-and-Bound algorithm described in the next method
	// It assumes that ins has already been preprocessed, and hence there exists a solution to the problem
	
	public static Solution solve(Instance ins) {
		BB bb = new BB(ins);
		bb.solveRecBB(new Solution(ins), 0, ins.P);
		return bb.bestSolution;
	}
	
	// sol is a partial solution constructed for n<n0, 
	// If n0==sol.ins.N, then sol is a complete solution, so we compare it to the best solution 
	public boolean solveRecBB(Solution sol, int n0, int leftBudgetPower) {
		// Increment the counter
		bestSolution.k++;
		// stop condition
		if (n0 == sol.ins.N) {
			int dataRate = (int)sol.dataRate();
			if (dataRate > bestDataRate) {
				bestDataRate = (int)dataRate;
				bestSolution = sol.copy();
			}
			return true;
		}
		// check if the relaxed sub-problem is feasible
		if (!LP.solveSubLP(sol, sortedE, n0, leftBudgetPower))
			return false;
		// check if solving the corresponding sub-IP problem can lead to a better data rate than bestDataRate
		float dataRate = sol.dataRate();
		if (dataRate < bestDataRate + 1)
			return false;
		// check if the solution obtained is a solution for the ILP-problem
		if (sol.isSolutionILP()) {
			bestDataRate = (int)dataRate;
			bestSolution = sol.copy();
			return true;
		}
		// Recursion
		Doubly d = sol.ins.t[n0];
		while ( d!= null ) {
			// LP.solveLP modifies sol, values of x[n] are recomputed at each call of the method solveSubLP,
			// so we don't need to reinitialize them, still we need to set lambda to 0
			sol.convexCombination(0,0,0,0);
			sol.x[n0] = new Couple(d.k, d.m);
			solveRecBB(sol, n0+1, leftBudgetPower-sol.ins.p(d));
			d = d.next;
		}
		return true;
	}
	
	// TEST METHODS -----------------------------------------------------------------------------
	
	public static void testSolve(String path) throws IOException {
		Instance ins = new Instance(path);
		double ti = System.currentTimeMillis();
		if (Preprocessing.preprocess(ins) == false)
			return;
		Solution sol = solve(ins);
		double tf = System.currentTimeMillis();
		System.out.println(sol);
		System.out.println("Run time in milliseconds : " + (tf-ti));
		System.out.println("Number of explored nodes : "+sol.k);
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
