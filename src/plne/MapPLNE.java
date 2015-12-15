package plne;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import vrp.Map;

public class MapPLNE extends Map{

	private int[][] weights;
	private int[] demands;
	private double[] x;
	private double[] y;
	private int numNodes;
	private int capacity;

	public double getX(int node) {
		return x[node];
	}

	public double getY(int node) {
		return y[node];
	}

	public int getCapacity() {
		return capacity;
	}

	public MapPLNE(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    String line;
		    Scanner st;
		    if((line = br.readLine()) != null){
		       st = new Scanner(line);
		       numNodes = st.nextInt() + 1; 
		       capacity = st.nextInt();
		    }
		    demands = new int[getNumNodes()];
		    weights = new int[getNumNodes()][getNumNodes()];
		    x = new double[getNumNodes()];
		    y = new double[getNumNodes()];
		    int k = 0;
		    while ((line = br.readLine()) != null) {
		    	st = new Scanner(line);
		    	x[k] = st.nextDouble();
		    	y[k] = st.nextDouble();
		    	if(k!=0){
		    		demands[k] = st.nextInt();
		    	}
		    	k++;
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0; i<getNumNodes(); i++){
			for(int j = 0; j<getNumNodes(); j++){
				weights[i][j] = (int)(Math.hypot(x[i]-x[j], y[i]-y[j])*1000);
			}
		}

	}

	public MapPLNE(MapPLNE map) {	
		this.numNodes = map.numNodes;
		this.capacity = map.capacity;
		demands = new int[getNumNodes()];
		weights = new int[getNumNodes()][getNumNodes()];
		x = new double[getNumNodes()];
		y = new double[getNumNodes()];
		for(int i = 0; i<getNumNodes(); i++){
			x[i] = map.getX(i);
			y[i] = map.getY(i);
			demands[i] = map.getDemand(i);
		}
		for(int i = 0; i<getNumNodes(); i++){
			for(int j = 0; j<getNumNodes(); j++){
				weights[i][j] = (int)(Math.hypot(x[i]-x[j], y[i]-y[j])*1000);
			}
		}
	}

	public MapPLNE( int numNodes) {
		setNumNodes(numNodes);
		capacity = Integer.MAX_VALUE;
		demands = new int[getNumNodes()];
		weights = new int[getNumNodes()][getNumNodes()];
		x = new double[getNumNodes()];
		y = new double[getNumNodes()];
		for(int i = 0; i<getNumNodes(); i++){
			demands[i] = 1;
			x[i] = Math.random();
			y[i] = Math.random();
		}
		for(int i = 0; i<getNumNodes(); i++){
			for(int j = 0; j<getNumNodes(); j++){
				weights[i][j] = (int)(Math.hypot(x[i]-x[j], y[i]-y[j])*1000);
			}
		}
	}
	
	public MapPLNE(){
		this(8);
	}


	public int getWeight(int i, int j) {
		return weights[i][j];
	}

	public int getDemand(int destination) {
		return demands[destination];
	}

	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public int getTotalDemand() {
		int[] tmp = Arrays.copyOf(demands, demands.length);
		Arrays.parallelPrefix(tmp, (x, y) -> x + y);
		return tmp[tmp.length-1];
	}
}
