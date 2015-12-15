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

public class FlowBased2 {
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
        IloNumVar[][] y = new IloNumVar[numNodes][];
        IloNumVar[][] z = new IloNumVar[numNodes][];
        for(int i = 0; i < numNodes; i++){
        	x[i] = cplex.boolVarArray(numNodes);
        	y[i] = cplex.numVarArray(numNodes, 0, Integer.MAX_VALUE);
        	z[i] = cplex.numVarArray(numNodes, 0, Integer.MAX_VALUE);
        }
    	for(int i = 0; i < numNodes; i++){
            for(int j = 0; j < numNodes; j++){
            	x[i][j].setName(String.format("x_%d_%d", i, j));
            	y[i][j].setName(String.format("y_%d_%d", i, j));
            	z[i][j].setName(String.format("z_%d_%d", i, j));
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
        
        // Flow Constraints
		// contrainte 11
        
        IloLinearNumExpr flowEleven = cplex.linearNumExpr();
        for(int j = 1; j < numNodes; j++){
        	flowEleven.addTerm(1, y[0][j]);
        	flowEleven.addTerm(-1, y[j][0]);
        }
        con = cplex.addEq(flowEleven, numNodes-1); 
        con.setName("c11");
       
       // contrainte 12 
        for (int i = 1; i < numNodes; i++){
        	IloLinearNumExpr flowTwelve = cplex.linearNumExpr();
        	for(int j = 0; j < numNodes; j++){
        		if(i!=j){
        			flowTwelve.addTerm(1, y[i][j]);
        			flowTwelve.addTerm(-1, y[j][i]);
        		}
        	}
        	con = cplex.addEq(flowTwelve, 1);
        	con.setName(String.format("c12_%d", i));
        }

		// contrainte 13
        IloLinearNumExpr flowThirteen = cplex.linearNumExpr();
        for(int j = 1; j < numNodes; j++){
        	flowThirteen.addTerm(1, z[0][j]);
        	flowThirteen.addTerm(-1, z[j][0]);
        }
        con = cplex.addEq(flowThirteen, -numNodes+1);
        con.setName("c13");

       // contrainte 14
        for (int i = 1; i < numNodes; i++){
        	IloLinearNumExpr flowFourteen = cplex.linearNumExpr();
        	for(int j = 0; j < numNodes; j++){
        		if(i!=j){
        			flowFourteen.addTerm(1, z[i][j]);
        			flowFourteen.addTerm(-1, z[j][i]);
        		}
        	}
        	con = cplex.addEq(flowFourteen, -1); 
        	con.setName(String.format("c14_%d", i));
        } 

        // contrainte 15
        for (int i = 0; i < numNodes; i++){
        	IloLinearNumExpr flowFifteen = cplex.linearNumExpr();
        	for (int j = 0; j < numNodes; j++)
        		//if (i!=j)
        	{
        		flowFifteen.addTerm(1, y[i][j]);
        		flowFifteen.addTerm(1, z[i][j]);
        	}
        	con = cplex.addEq(flowFifteen, numNodes-1);
        	con.setName(String.format("c15_%d", i));
        }

        // contrainte 16
        
        for(int j = 0; j < numNodes; j++){
        	for(int i = 0; i < numNodes; i++)
        		//if (i!=j)
        	{
        		con = cplex.addEq(
        				cplex.sum(y[i][j], z[i][j]), 
        				cplex.prod(numNodes-1, x[i][j])
        			);
        		con.setName(String.format("c16_%d_%d",i ,j));
        	}
        }
        return x; 

	}
	
	public static void main(String[] args) {

		try{
			Map map = new Map();
			IloCplex cplex = new IloCplex();
			IloIntVar[][] x = defModel(cplex, map);
			cplex.exportModel(path + "F2.lp");
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
