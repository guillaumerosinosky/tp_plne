package plne;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.TreeSet;

public class TimeStaged3 {

	public static IloIntVar[][] defModel(IloCplex cplex, Map map) throws IloException {
        
        int numNodes = map.getNumNodes(); 
        double[][] weight = map.weights; 
        // Def variables
        IloIntVar[][] x = new IloIntVar[numNodes][];
        IloIntVar[][][] y = new IloIntVar[numNodes][][];
        for(int i = 0; i < numNodes; i++){
        	x[i] = cplex.boolVarArray(numNodes);
        	y[i] = new IloIntVar[numNodes][];
        	for(int j = 0; j < numNodes; j++){
        		y[i][j] = cplex.boolVarArray(numNodes);
        	}
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
        
        // Time Constraints
        // contrainte 25
        for(int j = 0; j < numNodes; j++){
        	for(int i = 0; i < numNodes; i++){
        		if(i!=j){
        			IloLinearNumExpr link = cplex.linearNumExpr();
        			link.addTerm(1, x[i][j]);
        			for (int t = 0; t < numNodes; t++){
        				link.addTerm(-1, y[i][j][t]);
        			}
        			cplex.addEq(link,0);
        		}
        	}
        }
        // contrainte 31
		IloLinearNumExpr time31 = cplex.linearNumExpr();
        for (int j = 0; j < numNodes; j++){
        	time31.addTerm(1, y[0][j][0]);
        }
    	cplex.addEq(1,	time31);  
    	
    	// contrainte 32
		IloLinearNumExpr time32 = cplex.linearNumExpr();
        for (int i = 0; i < numNodes; i++){
        	time32.addTerm(1, y[i][0][numNodes-1]);
        }
    	cplex.addEq(1, time32);

    	// contrainte 33
        for (int t = 1; t < numNodes; t++){
        	for(int i = 1; i < numNodes; i++){
        		IloLinearNumExpr time33 = cplex.linearNumExpr();
        		for(int j = 0; j < numNodes; j++){
            		time33.addTerm(1, y[i][j][t]);
            	}
        		for(int k = 0; k < numNodes; k++){
            		time33.addTerm(-1, y[k][i][t-1]);
            	}
        		cplex.addEq(time33, 0);
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
				cplex.output().println("Solution status = " +  Tools.getX(cplex, x));
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
