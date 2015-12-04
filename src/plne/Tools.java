package plne;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;


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
				SortedSet<Integer> G = F.tailSet(x);
				ArrayList<Integer> L2 = (ArrayList<Integer>) L.clone();
				L2.add(x);
				recCombinaison(L2, G, minSize, maxSize-1, results);
			}
		}
	}
	
	public static void main(String[] args) {
		TreeSet<Integer> F = new TreeSet<Integer>();
		for(int i = 0; i < 3; i++){
			F.add(i);
		}
		ArrayList<ArrayList<Integer>> res = coupe(F, 2, 3);
		System.out.println(res);
	}

}
