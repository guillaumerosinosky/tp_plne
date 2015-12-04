package plne;

public class Map {

	public double[][] weights;
	private int[] demand;
	private double[] x;
	private double[] y;

	public double getX(int node) {
		return x[node];
	}

	public double getY(int node) {
		return y[node];
	}

	private int numNodes;

	public Map() {
		setNumNodes(15);
		demand = new int[getNumNodes()];
		weights = new double[getNumNodes()][getNumNodes()];
		x = new double[getNumNodes()];
		y = new double[getNumNodes()];
		for(int i = 0; i<getNumNodes(); i++){
			demand[i] = 1;
			x[i] = Math.random();
			y[i] = Math.random();
		}
		for(int i = 0; i<getNumNodes(); i++){
			for(int j = 0; j<getNumNodes(); j++){
				weights[i][j] = (Math.hypot(x[i]-x[j], y[i]-y[j])*1000);
			}
		}
	}

	public double getWeight(int i, int j) {
		return weights[i][j];
	}

	public int getDemand(int destination) {
		return demand[destination];
	}

	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

}
