package plne;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.cppimpl.IloBoolVar;
import ilog.cplex.*;
import ilog.cplex.IloCplex.UnknownObjectException;


public class Tools {
	
	static ArrayList<ArrayList<Integer>> coupe(
			SortedSet<Integer> includedNodes,
			int minSize, int maxSize){
		ArrayList<ArrayList<Integer>> results = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> L = new ArrayList<Integer>();
		recCombinaison(L, includedNodes, minSize, maxSize, results);
		return results;
	}
	
	static void recCombinaison(
			ArrayList<Integer> L, SortedSet<Integer> F, 
			int minSize, int maxSize, 
			ArrayList<ArrayList<Integer>> results ){
		if(maxSize == 0) {
			results.add(L);
			return;
		}else{
			if(L.size()>=minSize){
				results.add(L);
			}
			for(int x : F ){
				TreeSet<Integer> G = new TreeSet<Integer>();
				G.addAll(F.tailSet(x));
				G.remove(x);
				ArrayList<Integer> L2 = (ArrayList<Integer>) L.clone();
				L2.add(x);
				recCombinaison(L2, G, minSize, maxSize-1, results);
			}
		}
	}
	
	static ArrayList<Integer> getResult(IloCplex cplex, IloIntVar[][] x) throws UnknownObjectException, IloException{
		ArrayList<Integer> res = new ArrayList<Integer>();
		int pos = 0;
		boolean  init = true;
		res.add(pos);
		while(pos != 0 || init){
			init = false;
			for (int i = 0; i < x[pos].length; i++) {
				if (cplex.getValue(x[pos][i]) > 0.9) {
					res.add(i);
					pos = i;
					break;
				}
			}
		}
		return res;
	}
	

	public static String getX(IloCplex cplex, IloIntVar[][] x) throws UnknownObjectException, IloException {
		String res = "\n";
		for(int i = 0; i<x.length; i++){
			for(int j = 0; j<x[0].length; j++){
				res = res + ' '  + cplex.getValue(x[i][j]);
			}
			res += '\n';
		}
		return res;
	}
	
	public static void main(String[] args) {
		TreeSet<Integer> F = new TreeSet<Integer>();
		for(int i = 1; i < 8; i++){
			F.add(i);
		}
		ArrayList<ArrayList<Integer>> res = coupe(F, 1, 8);
		System.out.println(res);
	}


}
