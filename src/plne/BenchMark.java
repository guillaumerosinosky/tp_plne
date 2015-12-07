package plne;

import java.util.ArrayList;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cplex.IloCplex;

public class BenchMark {

	
	public static void main(String[] args) {
		try{
			Map map = new Map();
			IloCplex cplex = new IloCplex();
			cplex.setOut(null);
			double objVal;
			ArrayList<Integer> resX;
			IloIntVar[][] x;
			
			x = Conventionnal.defModel(cplex, map);
			cplex.solve();
			objVal = cplex.getObjValue();
			resX = Tools.getResult(cplex, x);
			cplex.clearModel();
			System.out.println("Valeur de l'objectif  : " + objVal);
			System.out.println(resX);

			x = FlowBased.defModel(cplex, map);
			cplex.solve();
			objVal = cplex.getObjValue();
			resX = Tools.getResult(cplex, x);
			cplex.clearModel();
			System.out.println("Valeur de l'objectif  : " + objVal);
			System.out.println(resX);
			
			x = Sequential.defModel(cplex, map);
			cplex.solve();
			objVal = cplex.getObjValue();
			resX = Tools.getResult(cplex, x);
			cplex.clearModel();
			System.out.println("Valeur de l'objectif  : " + objVal);
			System.out.println(resX);
			
			x = TimeStaged1.defModel(cplex, map);
			cplex.solve();
			objVal = cplex.getObjValue();
			resX = Tools.getResult(cplex, x);
			cplex.clearModel();
			System.out.println("Valeur de l'objectif  : " + objVal);
			System.out.println(resX);
			cplex.end();
		}
		catch (IloException e) {
			System.err.println("Concert exception '" + e + "' caught");
		}
	}
	
}
