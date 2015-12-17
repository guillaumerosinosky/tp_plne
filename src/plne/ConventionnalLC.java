package plne;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import vrp.Map;
import ilog.opl.*;
import ilog.concert.*;
import ilog.cplex.IloCplex;


public class ConventionnalLC {

	public static class SubtourLazyConsCallback 
	extends IloCplex.LazyConstraintCallback {
		final IloIntVar[][] x;
		final int numNodes;
		final IloCplex cplex;

		SubtourLazyConsCallback(IloIntVar[][] x, IloCplex cplex) { 
			this.cplex = cplex;
			this.x = x;
			numNodes = x.length;
		}

		int checkTour(double [][] sol, ArrayList<Integer> seen) {
			int j, n    = this.numNodes;
			double tol = 0.1;
			int last    = -1;
			int length  = 0;
			int current = 0;
			seen.clear();
			for(int i = 0; i<n; i++)
				seen.add(0);

			// Search for a subtour if sol[] is integer feasible

			while ( seen.get(current) == 0 ) {
				length++;
				seen.set(current,  length);
				for (j = 0; j < n; j++) {
					if ( sol[current][j] >= 1.0-tol ) break;
				}
				last    = current;
				current = j;
			}
			return length;
		}


		IloRange addCut(double[][] sol, IloCplex cplex) throws IloException {
			int i, j;

			ArrayList<Integer> seen = new ArrayList<Integer>();
			int length = checkTour(sol, seen);
			if ( length >= numNodes ) {
				return null;
			}

			// Create and add subtour constraint ---
			// No more than 'length-1' edges between members of the subtour

			IloLinearIntExpr clique = cplex.linearIntExpr();
			for (i = 0; i < numNodes; i++) {
				if ( seen.get(i) != 0 ) {
					for (j = 0; j < numNodes; j++) {
						if ( seen.get(j) != 0 & i!=j)
							clique.addTerm( x[j][i], 1);
					}
				}
			}
			IloRange cut = cplex.le(clique,  length-1);
			return cut;
		}

		
		public void main() throws IloException {

			// Get the current x solution

			double[][] sol = new double[numNodes][]; 
			for (int i = 0; i < numNodes; i++){ 
				sol[i] = getValues(x[i]);
			}
			// Benders' cut separation
			IloRange cut = addCut(sol, this.cplex);
			if ( cut != null) add(cut);
		}

	} // END BendersLazyConsCallback

	
	public static IloIntVar[][] defModel(IloCplex cplex, Map map, int scoreInit) throws IloException {
        int numNodes = map.getNumNodes(); 
        double[][] weight = new double[numNodes][];
        IloIntVar[][] x = new IloIntVar[numNodes][];
        for(int i = 0; i < numNodes; i++){
        	x[i] = cplex.boolVarArray(numNodes);
        	weight[i] = new double[numNodes];
        	for(int j = 0; j < numNodes; j++){
        		weight[i][j] = map.getWeight(i, j);
        	}
        }
        IloObjective obj = cplex.addMinimize();	
        IloLinearNumExpr objectif = cplex.linearNumExpr();
        for(int j = 0; j < numNodes; j++){
        	for(int i = 0; i < numNodes; i++){
        		objectif.addTerm(weight[j][i],  x[j][i]);
        	}
        }
        cplex.addLe(objectif, scoreInit);
        obj.setExpr(objectif);
        
        
        for(int j = 0; j < numNodes; j++){
        	IloLinearNumExpr chemins = cplex.linearNumExpr();
        	for(int i = 0; i < numNodes; i++){
        		if(i!=j){
        			chemins.addTerm(1, x[j][i]);
        		}
        	}
        	cplex.addEq(1, chemins); 
        }
        for(int j = 0; j < numNodes; j++){
        	IloLinearNumExpr chemins = cplex.linearNumExpr();
        	for(int i = 0; i < numNodes; i++){
        		if(i!=j){
        			chemins.addTerm(1, x[i][j]);
        		}
        	}
        	cplex.addEq(1, chemins); 
        }
        
        
        
        cplex.setParam(IloCplex.IntParam.MIPSearch, IloCplex.MIPSearch.Traditional);
        cplex.setParam(IloCplex.IntParam.Threads, 1);
        cplex.setParam(IloCplex.BooleanParam.PreInd, false);
        cplex.use(new SubtourLazyConsCallback(x, cplex)); 
        /*
		TreeSet<Integer> F = new TreeSet<Integer>();
		for(int i = 1; i < numNodes; i++){
			F.add(i);
		}
		ArrayList<ArrayList<Integer>> coupes = Tools.coupe(F, 1, F.size());
        for(ArrayList<Integer> M : coupes){
        	IloLinearNumExpr coupeCard = cplex.linearNumExpr();
        	for(int j : M){
        		for(int i : M){
        			if(i!=j){
        				coupeCard.addTerm(1, x[i][j]);
        			}
        		}
        		cplex.addLe(coupeCard, M.size() - 1);
        	}
        }
        */
		return x;
		
	}
	
	public static void main(String[] args) {

		try{
			Map map = new Map();
			IloCplex cplex = new IloCplex();
			IloIntVar[][] x = defModel(cplex, map, Integer.MAX_VALUE);
			if ( cplex.solve() ) {
				cplex.output().println("Solution status = " + Tools.getX(cplex, x));
				cplex.output().println("Solution value  = " + cplex.getObjValue());

			}
			ArrayList<Integer> resX = Tools.getResult(cplex, x);
			System.out.println(resX);
			cplex.end();
		}
		catch (IloException e) {
			System.err.println("Concert exception '" + e + "' caught");
		}
	}

	
}
