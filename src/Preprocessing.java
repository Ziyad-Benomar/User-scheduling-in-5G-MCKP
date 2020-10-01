import java.io.IOException;

// PREPROCESSING AN INSTANCE BEFAORE SOLVING THE CORRESPONDING PROBLEM

// Methods of this class answer to questions : Q2, Q3, Q4, Q5
// the principal methods of this class are :
// #) preprocess()
// #) quickPreprocessing() : Q2
// #) removeIPdominated()  : Q3
// #) removeLPdominated()  : Q4
// #) testProcessiing()    : Q5

public class Preprocessing {
	
	// PREPROCESSING METHODS ---------------------------------------------------------------------
	
	// The following method is the preprocessing method we will use after.
	// It includes all the preprocessing methods suggested in Q2, Q3, and Q4.
	// It does not remove LP-dominated triplets, but just marks them
	// It returns false if the problem has no solution, and true otherwise
	
	public static boolean preprocess(Instance ins) {
		if (quickPreprocessing(ins) == false) {
			System.out.println("The IP problem for this instance has no solution");
			return false;
		}
		removeIPdominated(ins);
		findLPdominated(ins);
		return true;
	}
	
	// The following method answers to Q2
	// it returns false if the ILP problem corresponding to the instance ins has no feasible solution.
	// And it removes triplets (k,m,n) that prevent, when chosen, any solution to be feasible
	
	public static boolean quickPreprocessing(Instance ins) {
		// Computing the minimal total transmission power
		int minTotalP = 0;
		for (int n=0; n<ins.N; n++)
			// ins.t[n] is sorted in ascending order of p_kmn
			minTotalP += ins.p(ins.t[n]);
		// Checking the existence of at least one solution
		if (minTotalP > ins.P)
			return false;
		// Removing the triplets preventing any solution to be feasible
		for (int n=0; n<ins.N; n++) {
			Doubly s = ins.t[n].next;
			while (s!=null && ins.p(s) <= ins.P - minTotalP + ins.p(ins.t[n]))
				s = s.next;
			if (s != null)
				s = s.prev.next = null;
			}
		return true;
	}
	
	// The following method answers to Q3
	// It removes all the IP-dominated triplets and returns their number
	
	public static int removeIPdominated(Instance ins) {
		int count = 0;
		for (int n=0; n<ins.N; n++) {
			Doubly d1 = ins.t[n];
			Doubly d2 = d1.next;
			// at the end of each step, we have d2 == d1.next
			while (d2 != null) {
				if ( ins.r(d1) >= ins.r(d2) ) {
					d2.remove();
					count++;
					d2 = d2.next;
				}
				else {
					if (ins.p(d1) == ins.p(d2)) {
						d1.remove();
						if (ins.t[n]==d1)
							ins.t[n] = d2;
						count++;
					}
					d1 = d1.next;
					d2 = d2.next;
				}
			}
		}
		return count;
	}
	
	// The two following methods answer to Q4.
	
	// The following method finds all the LP-dominated triplets and returns their number
	// It sets their attribute lpDominated to true
	// It assumes that we have already removed IP-dominated triplets
	
	public static int findLPdominated(Instance ins) {
		int count = 0;
		for (int n=0; n<ins.N; n++) {
			Doubly d1 = ins.t[n];
			Doubly d2 = d1.next;
			if (d2 == null )
				continue;
			Doubly d3 = d2.next;
			while (d3 != null) {
				if ( (ins.r(d3)-ins.r(d2))*(ins.p(d2)-ins.p(d1)) >= (ins.r(d2)-ins.r(d1))*(ins.p(d3)-ins.p(d2))) {
					d2.lpDominated = true;
					count++;
					if (d1 == ins.t[n]) {
						d2 = d3;
						d3 = d3.nextLP();
					}
					else {
						d2 = d1;
						d1 = d1.prevLP();
					}
				}
				else {
					d1 = d2;
					d2 = d3;
					d3 = d3.nextLP();
				}
			}
		}
		return count;
	}
	
	// The following method returns a copy of ins.t from which all the LP-dominated triplets were removed
	// It does not modify the instance ins
	// It assumes that we have already applied the method findLPdminated to ins
	
	public static Doubly[] removeLPdominated(Instance ins) {
		Doubly[] t = ins.t;
		Doubly[] tLP = new Doubly[t.length];
		for (int n=0; n<t.length; n++) {
			tLP[n] = new Doubly(t[n].k, t[n].m, t[n].n);
			Doubly d = t[n].next;;
			Doubly dLP = tLP[n];
			while (d!=null) {
				if (d.lpDominated == false) {
					dLP.insertAfter(d.k, d.m, d.n);
					dLP = dLP.next;
				}
				d = d.next;
			}
		}
		return tLP;
	}
	
	
	
	// TEST METHODS ----------------------------------------------------------------------------------------
	
	// The following method answers to Q5.
	// It shows how the size of the instance changes after applying each of the previous methods
	
	public static void testPreprocessing(String path) throws IOException {
		Instance ins = new Instance(path);
		int numberTriplets = ins.N*ins.K*ins.M;
		System.out.println("Ititial number of triplets : " + numberTriplets);
		
		// Test quickPreprocessing
		System.out.println("After applying the method quickPreprocessing");
		if (quickPreprocessing(ins) == false) {
			System.out.println("The IP problem for this instance has no solution");
			return;
		}
		else {
			int count = Doubly.size(ins.t);
			System.out.println("Number of removed triplets   : " + (numberTriplets - count) );
			System.out.println("Number of remaining triplets : " + count);
			numberTriplets = count;
		}
		
		// Test removeIPdominated
		int countIP = removeIPdominated(ins);
		numberTriplets -= countIP;
		System.out.println("After applying the method removeIPdominated");
		System.out.println("Number of removed triplets   : " + countIP );
		System.out.println("Number of remaining triplets : " + numberTriplets);
		
		// Test findLPdominated
		int countLP = findLPdominated(ins);
		// you can uncomment the following lines to see how findLPdominated() reduces the data,
		// but it is not necessary since we visualize the result for removeLPdominated()
		//System.out.println("After applying the method findLPdominated");
		//System.out.println("Number of found triplets   : " + countLP );
		//System.out.println("Number of remaining triplets : " + numberTriplets);
		
		// Test removeLPdominated
		Doubly[] tLP = removeLPdominated(ins);
		int countRemainingLP = Doubly.size(tLP);
		System.out.println("After applying the method removeLPdominated");
		System.out.println("Number of removed triplets   : " + (numberTriplets - countRemainingLP) );
		System.out.println("Number of remaining triplets : " + countRemainingLP);
	}

	
	public static void main(String[] args) throws IOException {
		System.out.println("TEST FILE : test1.txt");
		testPreprocessing("test1.txt");
		System.out.println("\nTEST FILE : test2.txt");
		testPreprocessing("test2.txt");
		System.out.println("\nTEST FILE : test3.txt");
		testPreprocessing("test3.txt");
		System.out.println("\nTEST FILE : test4.txt");
		testPreprocessing("test4.txt");
		System.out.println("\nTEST FILE : test5.txt");
		testPreprocessing("test5.txt");
	}
}
