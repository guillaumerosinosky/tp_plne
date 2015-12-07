package plne;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.TreeSet;

public class Sequential {

	public static IloIntVar[][] defModel(IloCplex cplex, Map map) throws IloException {
        
        int numNodes = map.getNumNodes(); 
        double[][] weight = map.weights; 
        // Def variables
        IloIntVar[][] x = new IloIntVar[numNodes][];
        IloIntVar[] u = cplex.intVarArray(numNodes, 0, Integer.MAX_VALUE);
        for(int i = 0; i < numNodes; i++){
        	x[i] = cplex.boolVarArray(numNodes);
        }
        
        // Objectif
        IloObjective obj = cplex.addMinimize();	
        IloLinearNumExpr objectif = cplex.linearNumExpr();
        for(int j = 0; j < numNodes; j++){
        	for(int i = 0; i < numNodes; i++){
        		objectif.addTerm(weight[j][i],  x[j][i]);
        	}
        }
        obj.setExpr(objectif);
        
       // contrainte 1 
        for(int j = 0; j < numNodes; j++){
        	IloLinearNumExpr chemins = cplex.linearNumExpr();
        	for(int i = 0; i < numNodes; i++){
        		if(i!=j){
        			chemins.addTerm(1, x[j][i]);
        		}
        	}
        	cplex.addEq(1, chemins); 
        }
       // contrainte 2
        for(int j = 0; j < numNodes; j++){
        	IloLinearNumExpr chemins = cplex.linearNumExpr();
        	for(int i = 0; i < numNodes; i++){
        		if(i!=j){
        			chemins.addTerm(1, x[i][j]);
        		}
        	}
        	cplex.addEq(1, chemins); 
        }
		// contrainte 6
        for (int i = 1; i < numNodes; i++){
        	for (int j = 1; j < numNodes; j++){
        		if(j!=i){
        			cplex.addLe(
        					cplex.sum(
        							u[i],
        							cplex.negative(u[j]),
        							cplex.prod(numNodes, x[i][j])),
        					numNodes-1);
        		}
        	}
        }
		return x;

	}
	
	public static void main(String[] args) {

		try{
			Map map = new Map();
			IloCplex cplex = new IloCplex();
			IloIntVar[][] x = defModel(cplex, map);
			if ( cplex.solve() ) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value  = " + cplex.getObjValue());

			}
			ArrayList<Integer> resX = Tools.getResult(cplex, x);
			cplex.end();
		}
		catch (IloException e) {
			System.err.println("Concert exception '" + e + "' caught");
		}
	}

}
