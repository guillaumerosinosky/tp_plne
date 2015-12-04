package plne;
import java.util.ArrayList;
import java.util.TreeSet;

import ilog.opl.*;
import ilog.concert.*;
import ilog.cplex.IloCplex;


public class Conventionnal {

	public static void defModel(IloCplex cplex) throws IloException {
        
        Map map = new Map();
        int numNodes = map.getNumNodes(); 
        double[][] weight = map.weights; 
        IloIntVar[][] x = new IloIntVar[numNodes][];
        for(int i = 0; i < numNodes; i++){
        	x[i] = cplex.boolVarArray(numNodes);
        }
        IloObjective obj = cplex.addMinimize();	
        IloLinearNumExpr objectif = cplex.linearNumExpr();
        for(int j = 0; j < numNodes; j++){
        	for(int i = 0; i < numNodes; i++){
        		objectif.addTerm(weight[j][i],  x[j][i]);
        	}
        }
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
        
		TreeSet<Integer> F = new TreeSet<Integer>();
		for(int i = 1; i < numNodes; i++){
			F.add(i);
		}
		ArrayList<ArrayList<Integer>> coupes = Tools.coupe(F, 1, 3);
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
		
	}
	
	public static void main(String[] args) {

		try{
			IloCplex cplex = new IloCplex();
			defModel(cplex);
			if ( cplex.solve() ) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value  = " + cplex.getObjValue());

			}
			cplex.end();
		}
		catch (IloException e) {
			System.err.println("Concert exception '" + e + "' caught");
		}
	}
	
}
