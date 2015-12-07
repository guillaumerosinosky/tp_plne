package plne;
import java.util.ArrayList;
import java.util.TreeSet;

import ilog.opl.*;
import ilog.concert.*;
import ilog.cplex.IloCplex;


public class Conventionnal {

	public static IloIntVar[][] defModel(IloCplex cplex, Map map) throws IloException {
        
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
		return x;
		
	}
	
	public static void main(String[] args) {

		try{
			Map map = new Map();
			IloCplex cplex = new IloCplex();
			IloIntVar[][] x = defModel(cplex, map);
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
