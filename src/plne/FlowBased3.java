package plne;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.TreeSet;

import vrp.Map;

public class FlowBased3 {
	final static String path = "/home/guillaume/tmp/";
	public static IloIntVar[][] defModel(IloCplex cplex, Map map) throws IloException {
		IloConstraint con;
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
        IloNumVar[][][] y = new IloNumVar[numNodes][][];
        for(int i = 0; i < numNodes; i++){
        	x[i] = cplex.boolVarArray(numNodes);
        	y[i] = new IloNumVar[numNodes][];
        	for(int j = 0; j < numNodes; j++){
        		y[i][j] = cplex.numVarArray(numNodes, 0, Integer.MAX_VALUE, IloNumVarType.Int);
        	}
        }
    	for(int i = 0; i < numNodes; i++){
            for(int j = 0; j < numNodes; j++){
            	x[i][j].setName(String.format("x_%d_%d", i, j));
            	for (int k = 0; k < numNodes; k++)
            		y[i][j][k].setName(String.format("y_%d_%d_%d", i, j,k));
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
        	con = cplex.addEq(1, chemins); 
        	con.setName(String.format("c1_%d", j));
        }
       // contrainte 2
        for(int j = 0; j < numNodes; j++){
        	IloLinearNumExpr chemins = cplex.linearNumExpr();
        	for(int i = 0; i < numNodes; i++){
        		if(i!=j){
        			chemins.addTerm(1, x[i][j]);
        		}
        	}
        	con = cplex.addEq(1, chemins); 
        	con.setName(String.format("c2_%d", j));
        }
        
        // contrainte 17
    	for(int i = 0; i < numNodes; i++){
            for(int j = 0; j < numNodes; j++){
            	for (int k = 1; k < numNodes; k++) {
            		IloLinearNumExpr flowSeventeen = cplex.linearNumExpr();
            		flowSeventeen.addTerm(1, y[i][j][k]);
            		con = cplex.addLe(flowSeventeen, x[i][j]);
            		con.setName(String.format("c17_%d_%d_%d", i, j, k));
            	}
            }
    	}

    	// contrainte 18
    	for (int k = 1; k < numNodes; k++) {
    		IloLinearNumExpr flowEighteen = cplex.linearNumExpr();
        	for(int i = 0; i < numNodes; i++){
        		flowEighteen.addTerm(1, y[0][i][k]);
        	}
        	con = cplex.addEq(flowEighteen, 1);
        	con.setName(String.format("c18_%d", k));
    	}

    	// contrainte 19
    	for (int k = 1; k < numNodes; k++) {
    		IloLinearNumExpr flowNineteen = cplex.linearNumExpr();
        	for(int i = 0; i < numNodes; i++){
        		flowNineteen.addTerm(1, y[i][0][k]);
        	}
        	con = cplex.addEq(flowNineteen, 0);
        	con.setName(String.format("c19_%d", k));
    	}
    	
    	// contrainte 20
    	for (int k = 1; k < numNodes; k++) {
    		IloLinearNumExpr flowTwenty = cplex.linearNumExpr();
        	for(int i = 0; i < numNodes; i++){
        		flowTwenty.addTerm(1, y[i][k][k]);
        	}
        	con = cplex.addEq(flowTwenty, 1);
        	con.setName(String.format("c20_%d", k));
    	}
   	
    	// contrainte 21
    	for (int k = 1; k < numNodes; k++) {
    		IloLinearNumExpr flowTwentyone = cplex.linearNumExpr();
        	for(int j = 0; j < numNodes; j++){
        		flowTwentyone.addTerm(1, y[k][j][k]);
        	}
        	con = cplex.addEq(flowTwentyone, 0);
        	con.setName(String.format("c21_%d", k));
    	}
    	
    	// contrainte 22
    	for (int k = 1; k < numNodes; k++) {
        	for(int j = 1; j < numNodes; j++){
        		if (j != k) {
            		IloLinearNumExpr flowTwentyTwo = cplex.linearNumExpr();
            		for (int i = 0 ; i < numNodes; i++) {
            			flowTwentyTwo.addTerm(1, y[i][j][k]);
            		}
            		
            		for (int i = 0 ; i < numNodes; i++) {
            			flowTwentyTwo.addTerm(-1, y[j][i][k]);
            		}
            		
                	con = cplex.addEq(flowTwentyTwo, 0);
                	con.setName(String.format("c22_%d_%d", k, j));            		
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
			cplex.exportModel(path + "F3.lp");
			if ( cplex.solve() ) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value  = " + cplex.getObjValue());

			}
			ArrayList<Integer> resX = Tools.getResult(cplex, x);
			cplex.end();
		}
		catch (IloException e) {
			/*
			 * Conflict resolution
			 * cplex> read F2.lp
 			 * cplex> optimize
			 * .....  [output log]
			 *	cplex> conflict
			 * cplex> write filename.iis clp
			 */
			System.err.println("Concert exception '" + e + "' caught");
			
		}
	}

}
