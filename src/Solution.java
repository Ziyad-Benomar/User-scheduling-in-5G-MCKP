
// An object of this class represents the solution of an LP or ILP problem

// for each 0 <= n < ins.N, x[n] is the couple (k,m) such that x_kmn = 1

// For a solution of the ILP problem, we don't need the attributes k,m,n, and lambda.
// We need them for the LP problem, where there exists at most one n such that for two 
// different couples (k1,m1) and and (k2,m2) we have x_kmn != 0, in this case :
// x_(k1,m1,n) = lambda, and  x_(k2,m2,n) = 1 - lambda
// where (k1,m1) = (this.k, this.m) and (k2,m2) = (this.x[n].k, this.x[n].m)

public class Solution {
	Instance ins;
	Couple[] x;
	int k, m, n;
	float lambda;
	
	// Here are constructors of objects Solution
	
	public Solution(Instance ins) {
		this.ins = ins;
		this.x = new Couple[ins.N];
		this.lambda = 0;
	}
	
	public Solution(Instance ins, Couple[] x, int k, int m, int n, float lambda) {
		this.ins = ins;
		this.x = x;
		this.convexCombination(k, m, n, lambda);
	}
	
	// This method sets the values of k,m,n, and lambda
	public void convexCombination(int k, int m, int n, float lambda) {
		this.k = k;
		this.m = m;
		this.n = n;
		this.lambda = lambda;
	}
	
	// This method returns a copy of the current object
	public Solution copy() {
		Couple[] xCopy = new Couple[x.length];
		for (int i=0; i<x.length; i++)
			xCopy[i] = x[i];
		return new Solution(ins, xCopy, k, m, n, lambda);
	}
	
	// This method returns true if the solution found is a solution for the ILP problem
	public boolean isSolutionILP() {
		return ( lambda == 0 );
	}
	
	// This method returns the used power for a solution of the ILP or LP problem
	public float usedPower() {
		float usedP = 0;
		for (int i=0; i<x.length; i++)
			usedP += ins.p(x[i].k, x[i].m, i);
		if (lambda != 0)
			usedP += lambda*(ins.p(k,m,n) - ins.p(x[n].k, x[n].m, n));
		return usedP;
	}
	
	// This method returns the total dataRate for a solution of the ILP or LP problem
	public float dataRate() {
		float dataR = 0;
		for (int i=0; i<x.length; i++)
			dataR += ins.r(x[i].k, x[i].m, i);
		if (lambda != 0)
			dataR += lambda*(ins.r(k,m,n) - ins.r(x[n].k, x[n].m, n));
		return dataR;
	}
	
	// This method returns a string corresponding to the solution found
	// it shows the chosen triplets, the initial budget power, the total used power, and the total data rate
	
	public String toString() {
		String s = "{(k,m,n) | x_kmn=1 } = { ("+x[0].k+","+x[0].m+","+0+")";
		for (int i=1; i<this.x.length; i++)
			if (i != this.n)
				s += ", ("+x[i].k+","+x[i].m+","+i+")";
		s += " }";
		if (this.isSolutionILP()) {
			s += "\nThis is a solution for the ILP problem !";
			s +="\nThe initial budget power : " + ins.P;
			s += "\nThe total used power     : "+(int)usedPower();
			s += "\nThe total data rate      : "+(int)dataRate();
		} else {
			s += "\nFor n = " + n + " : x_{"+k+","+m+","+n+"} = "+ lambda+", x_{"+x[n].k+","+x[n].m+","+n+"} = "+(1-lambda);
			s +="\nThe initial budget power : " + ins.P;
			s += "\nThe total used power     : "+usedPower();
			s += "\nThe total data rate      : "+dataRate();
		}
		return s;
	}
	
	

	
}
