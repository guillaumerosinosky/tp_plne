package plne;

import java.util.ArrayList;
import java.util.Iterator;

import vrp.*;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cplex.IloCplex;

public class BenchMark {

	public static ArrayList<Integer> antColonySolution(MapPLNE map){
		PheromonedMap phMap = new PheromonedMap(map, 0.8);
		ArrayList<Integer> bestPath = new ArrayList<Integer>();
		Colony colony = new Colony(0, phMap, map.getNumNodes());
		colony.solve();
		bestPath.addAll(colony.getBestPath());
		System.out.println(bestPath);
		System.out.println("Valeur de l'objectif  : " + colony.getBestScore() );
		return bestPath;
	}
	
	public static void setInitSol(IloCplex cplex, IloIntVar[][] x, ArrayList<Integer> initSol) throws IloException{
		Iterator<Integer> it = initSol.iterator();
		int j,prec = it.next();
		double[] valXi = new double[x.length];
		for(int i = 0; i<valXi.length; i++){
			valXi[i] = 0;
		}
		while (it.hasNext()) {
			j = it.next();
			if(j==prec){
				continue;
			}
			valXi[j] = 1;
			cplex.addMIPStart(x[prec], valXi);
			valXi[j] = 0;
			prec = j;
		}

	}
	
	public static void main(String[] args) {
			MapPLNE map = new MapPLNE(9);
			double timeLimit = 3;
			ArrayList<Integer> initSol = antColonySolution(map);
			IloCplex cplexC = null;
			IloCplex cplex = null;
			try {
				cplexC = new IloCplex();
				cplexC.setOut(null);
				cplexC.setParam(IloCplex.DoubleParam.TiLim, 10);
				cplex = new IloCplex();
				cplex.setOut(null);
				cplex.setParam(IloCplex.DoubleParam.TiLim, 10);
			} catch (IloException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			double objVal;
			ArrayList<Integer> resX;
			IloIntVar[][] x;
			
			System.out.println("\n Conventionnal formulation");
			try {
				cplexC.clearModel();
				cplexC.setParam(IloCplex.DoubleParam.TiLim, timeLimit);
				x = Conventionnal.defModel(cplexC, map);
				setInitSol(cplexC, x, initSol);
				cplexC.solve();
				cplexC.getCplexTime();
				objVal = cplexC.getObjValue();
				resX = Tools.getResult(cplexC, x);
				cplexC.clearModel();
				System.out.println("Valeur de l'objectif  : " + objVal + " in " + cplexC.getCplexTime());
				System.out.println(resX);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("\n Flow F1 formulation");
			try {
				cplex.clearModel();
				cplex.setParam(IloCplex.DoubleParam.TiLim, timeLimit);
				x = FlowBased.defModel(cplex, map);
				setInitSol(cplex, x, initSol);
				cplex.solve();
				objVal = cplex.getObjValue();
				resX = Tools.getResult(cplex, x);
				cplex.clearModel();
				System.out.println("Valeur de l'objectif  : " + objVal + " in " + cplex.getCplexTime());

				System.out.println(resX);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("\n Flow F2 formulation");
			try {
				cplex.clearModel();
				cplex.setParam(IloCplex.DoubleParam.TiLim, timeLimit);
				x = FlowBased2.defModel(cplex, map);
				setInitSol(cplex, x, initSol);
				cplex.solve();
				objVal = cplex.getObjValue();
				resX = Tools.getResult(cplex, x);
				cplex.clearModel();
				System.out.println("Valeur de l'objectif  : " + objVal + " in " + cplex.getCplexTime());

				System.out.println(resX);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			System.out.println("\n Flow F3 formulation");
			try {
				cplex.clearModel();
				cplex.setParam(IloCplex.DoubleParam.TiLim, timeLimit);
				x = FlowBased3.defModel(cplex, map);
				setInitSol(cplex, x, initSol);
				cplex.solve();
				objVal = cplex.getObjValue();
				resX = Tools.getResult(cplex, x);
				cplex.clearModel();
				System.out.println("Valeur de l'objectif  : " + objVal + " in " + cplex.getCplexTime());

				System.out.println(resX);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			System.out.println("\n Sequential S formulation");
			try {
				cplex.clearModel();
				cplex.setParam(IloCplex.DoubleParam.TiLim, timeLimit);
				x = Sequential.defModel(cplex, map);
				setInitSol(cplex, x, initSol);
				cplex.solve();
				objVal = cplex.getObjValue();
				resX = Tools.getResult(cplex, x);
				cplex.clearModel();
				System.out.println("Valeur de l'objectif  : " + objVal + " in " + cplex.getCplexTime());

				System.out.println(resX);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("\n Time Staged T1 formulation");
			try {
				cplex.clearModel();
				cplex.setParam(IloCplex.DoubleParam.TiLim, timeLimit);
				x = TimeStaged1.defModel(cplex, map);
				setInitSol(cplex, x, initSol);
				cplex.solve();
				objVal = cplex.getObjValue();
				resX = Tools.getResult(cplex, x);
				cplex.clearModel();
				System.out.println("Valeur de l'objectif  : " + objVal + " in " + cplex.getCplexTime());

				System.out.println(resX);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("\n Time Staged T2 formulation");
			try {
				cplex.clearModel();
				cplex.setParam(IloCplex.DoubleParam.TiLim, timeLimit);
				x = TimeStaged2.defModel(cplex, map);
				setInitSol(cplex, x, initSol);
				cplex.solve();
				objVal = cplex.getObjValue();
				resX = Tools.getResult(cplex, x);
				cplex.clearModel();
				System.out.println("Valeur de l'objectif  : " + objVal + " in " + cplex.getCplexTime());

				System.out.println(resX);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("\n Time Staged T3 formulation");
			try {
				cplex.clearModel();
				cplex.setParam(IloCplex.DoubleParam.TiLim, timeLimit);
				x = TimeStaged3.defModel(cplex, map);
				setInitSol(cplex, x, initSol);
				cplex.solve();
				objVal = cplex.getObjValue();
				resX = Tools.getResult(cplex, x);
				cplex.clearModel();
				System.out.println("Valeur de l'objectif  : " + objVal + " in " + cplex.getCplexTime());

				System.out.println(resX);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cplex.end();
	}
	
}
