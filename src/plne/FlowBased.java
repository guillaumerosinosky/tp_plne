package plne;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.TreeSet;

import vrp.Map;

public class FlowBased {

	public static IloIntVar[][] defModel(IloCplex cplex, Map map) throws IloException {
        
        int numNodes = map.getNumNodes(); 
        double[][] weight = new double[numNodes][];
        for(int i = 0; i < numNodes; i++){
        	weight[i] = new double[numNodes];
        	for(int j = 0; j < numNodes; j++){
        		weight[i][j] = map.getWeight(i, j);
        	}
        }
        // Def variables
        IloIntVar[][] x = new IloIntVar[numNodes][];
        IloNumVar[][] y = new IloNumVar[numNodes][];
        for(int i = 0; i < numNodes; i++){
        	x[i] = cplex.boolVarArray(numNodes);
        	y[i] = cplex.numVarArray(numNodes, 0, Double.MAX_VALUE);
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
        
        // Flow Constraints
		// contrainte 7
        for (int i = 0; i < numNodes; i++){
        	for (int j = 0; j < numNodes; j++){
        		if(j!=i){
        			cplex.addLe(y[i][j], cplex.prod(numNodes-1, x[i][j]));
        		}
        	}
        }
        // contrainte 8
        IloLinearNumExpr flowEight = cplex.linearNumExpr();
        for(int j = 1; j < numNodes; j++){
        	flowEight.addTerm(1, y[0][j]);
        }
        cplex.addEq(flowEight, numNodes-1); 

        // contrainte 9
        for(int j = 1; j < numNodes; j++){
        	IloLinearNumExpr flow = cplex.linearNumExpr();
        	for(int i = 0; i < numNodes; i++){
        		if(i!=j){
        			flow.addTerm(1, y[i][j]);
        		}
        	}
        	for(int k = 0; k < numNodes; k++){
        		if(k!=j){
        			flow.addTerm(-1, y[j][k]);
        		}
        	}
        	cplex.addEq(flow, 1); 
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
