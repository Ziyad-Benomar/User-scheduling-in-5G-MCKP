import java.io.IOException;
import java.util.Random;

//ONLINE PROGRAMMING

//Methods of this class answer to Q12 and Q13
//the principal methods of this class are :
//#) solve()	    : Q6
//#) testSolve()   : Q7

// (channelSet[n]==0) <==> (n is still available)
// eAverage is the expected slope we will use in our quality function
public class OP {
	Instance ins;
	int[] channelSet;
	int pMax, rMax, budgetPower;
	float eAverage;
	
	// 
	public OP(int K, int M, int N, int pMax, int rMax, int budgetPower) {
		this.ins = randomInstance(K, M, N, pMax, rMax, budgetPower);
		this.channelSet = new int[ins.N];
		this.pMax = pMax;
		this.rMax = rMax;
		this.budgetPower = budgetPower;
		this.eAverage = computeEAverage(pMax, rMax);
	}
	
	// CONSTRUCTOR -------------------------------------------------------------------------------
	
	// This method generates a random instance of IP problem
	public static Instance randomInstance(int K, int M, int N, int pMax, int rMax, int budgetPower) {
		int[][][] p = randomMatrix(K, M, N, pMax);
		int[][][] r = randomMatrix(K, M, N, rMax);
		Instance ins = new Instance(p, r, budgetPower);
		return ins;
	}
	// this method generates a matrix with random values between 1 and vMax both included
	public static int[][][] randomMatrix(int K, int M, int N, int vMax){
		int[][][] matrix = new int[K][M][N];
		for (int k=0; k<K; k++)
			for (int m=0; m<M; m++)
				for (int n=0; n<N; n++)
					// random value between 1 and vMax both included
					matrix[k][m][n] = (int)(vMax*Math.random())+1;
		return matrix;
	}
	// This method computes the average slope r/p, if p and r are uniform random variables on [1,pMax] and [1,rMax]
	public static float computeEAverage(int pMax, int rMax) {
		float e = 0;
		for (int p=1; p<=pMax; p++)
			for (int r=1; r<=rMax; r++)
				e = e + (float)r/p;
		return e/(pMax*rMax);
	}
	
	
	// SOLVING THE PROBLEM ----------------------------------------------------------------------------
	
	
	// The following function answers to Q12
	
	public static Solution solve(int K, int M, int N, int pMax, int rMax, int budgetPower) {
		OP op = new OP(K, M, N, pMax, rMax, budgetPower);
		Solution sol = new Solution(op.ins);
		// Since we are sure we won't need the attribute sol.k for the online programming, we 
		// we will use to it to precise whether the solution is complete or not
		// 1 means complete, and 0 means not complete
		sol.k = 1;
		if (op.constructCompleteSolution(sol)==false) {
			// uncomment the following line if you want to be warned each time a solution is not complete (
			//System.out.println("The computed solution is not complete : there are channels that didn't serve any user");
			sol.k = 0;
		}
		return sol;
	}
	
	// This function is the heart of our online programming
	public boolean constructCompleteSolution(Solution sol) {
		int k = 0;
		// Nk is the cardinal of the set of left channels
		int Nk = ins.N;
		while (Nk>0 && k<ins.K && budgetPower>0) {
			// At each step k, we only read the values of p_kmn, r_kmn having the corresponding k 
			for (int n=0; n<ins.N; n++) {
				// checking if n has already served a user
				if (channelSet[n]==-1)
					continue;
				// Finding the best feasible (p_kmn, r_kmn) for the current k
				int mStar = 0;
				float qualStar = quality(ins.p(k,0,n), ins.r(k,0,n), Nk);
				for (int m=1; m<ins.M; m++) {
					float qual = quality(ins.p(k,m,n), ins.r(k,m,n), Nk);
					if ( qual > qualStar) {
						mStar = m;
						qualStar = qual;
					}
				}
				//
				if (ins.p(k,mStar,n)<=budgetPower && goodEnough(qualStar, k, Nk)) {
					sol.x[n] = new Couple(k,mStar);
					channelSet[n] = -1;
					budgetPower -= ins.p(k,mStar,n);
				}
			}
			// The next user arrives !
			k ++;
		}
		// Since we are working heuristically, there is a risk of having a non complete solution
		boolean solIsComplete = true;
		for (int n=0; n< ins.N; n++) {
			if (channelSet[n] == 0) {
				sol.x[n] = new Couple(-1,-1);
				solIsComplete = false;
			}		
		}
		return solIsComplete;
	}
	
	// This quality function will allow us to compare points (p,r)
	public float quality(int p, int r, int Nk) {
		// we set a maximal individual budget we allow the channel to use at this moment
		int maxIndBudgetP = budgetPower - (Nk-1)*(1+pMax/ins.M);
		if (p <= budgetPower/Nk)
			return r-p*eAverage;
		// If p > budgetPower/Nk, the quality of (p,r) is reduced
		else if (p <= maxIndBudgetP) {
			float reduction = (float)budgetPower/p;
			return reduction*(float)(r-p*eAverage);
		} // If p is bigger than this maximal allowed budget power, the quality of (p,r) is the lowest possible
		else
			return -pMax*rMax;
	}
	
	// This function decides if a point (p,r) is good enough to be kept or not
	public boolean goodEnough(float qualStar, int k, int Nk) {
		if (k==ins.K-1)
			return true;
		int countWorse = 0;
		for (int p=1; p<=pMax; p++)
			for (int r=1; r<=rMax; r++)
				if (quality(p,r,Nk) <= qualStar)
					countWorse++;
		
		double exigencyInitial = 0.945;
		double exigencyFinal = 0.855;
		
		double exigency = exigencyInitial + (double)k/(ins.K-2)*(exigencyFinal - exigencyInitial);
		return (float)countWorse/(pMax*rMax) > exigency;
	}
	
	
	
	// TESTING ----------------------------------------------------------------------------------------------
	
	// The two following methods answer to Q13
	// The following method tests the online solving of the IP problem having the parameters
	// K, M, N, pMax, rMax, budgetPower.
	public static float testSolve(int K, int M, int N, int pMax, int rMax, int budgetPower) {
		Solution sol = solve(K, M, N, pMax, rMax, budgetPower);
		// If the solution is not complete, then it won't be considered as a solution, hence its ratio is zero
		if (sol.k == 0)
			return 0;
		// Uncomment the following line if you want to print the solution given by the online algorithm
		//System.out.println(sol); // But in this case don't use the method computeRatio(E) with a large E
		// We compute the optimal solution to the current problem using the dinamic programming method
		Solution optimalSol = DP.solve(sol.ins);
		// We return the ratio between the two data rates
		return sol.dataRate()/optimalSol.dataRate();
	}
	
	// The following method returns the competitive ratio over E experiences with the values given in the subject
	// for P, K, M, N, pMax, rMax
	public static float computeRatio(int E) {
		// we will give the distribution of the ratio on the interval [0.5, 1]
		int Nb = 500;
		float[] distibution = new float[Nb];
		for (int n=0; n<Nb; n++)
			distibution[n] = 0;
		int countIncomplete = 0;
		// Computing the ratio
		double ti = System.currentTimeMillis();
		float ratio = 0;
		for (int i=0; i<E; i++) {
			float r = testSolve(10, 2, 4, 50, 100, 100);
			// Count incomplete solutions
			if (r==0)
				countIncomplete++;
			ratio += r;
			int n = (int)(r*1000) - 500;
			if (n>=0 && n<Nb)
				distibution[n]++;
		}
		ratio /= E;
		double tf = System.currentTimeMillis();
		float proba =0;
		for (int n=0; n<Nb; n++) {
			distibution[n] = (float)distibution[n]/E;
			proba += distibution[n];
		}
		System.out.println(proba);
			
		System.out.println("TEST ONLINE PROGRAM OVER "+ E +" EXPERIENCES");
		System.out.println("ratio : " +ratio);
		System.out.println((float)countIncomplete/E + "% of the solutions are not complete : they have channels that didn't serve any user");
		System.out.println("Run time : " + (tf - ti));
		
		String x = "x = np.array([ 0.5";
		String y = "y = np.array([ " + (float)distibution[0];
		for (int n=1; n<Nb; n++) {
			x += ", " + (n+500)/1000.0;
			y += ", " + distibution[n];
		}
		x += "])";
		y += "])";
		System.out.println("\nThe two following lines can be put in a python code in order to draw the distribution curve of the ratio");
		System.out.println(x);
		System.out.println(y);
		return ratio;
	}
	
	public static void main(String[] args) {
		computeRatio(1000);
	}
}
