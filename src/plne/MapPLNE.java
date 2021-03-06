package plne;

import java.nio.file.Paths;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import vrp.Map;

public class MapPLNE extends Map{
	public void generateDot(String pathFile) {
		StringBuilder sb = new StringBuilder();
		sb.append("graph g1 {");
		sb.append("overlap=false;");
		sb.append("splines=true;");
		for (int i=0; i< getNumNodes(); i++){
			for (int j=0; j<getNumNodes(); j++) 
				if (i > j)
			{
				int w = getWeight(i, j);
				if (w != 0) {
					sb.append(String.format("%d -- %d[label=%d];", i,j,w));
				}
			}
		}
		sb.append("}");
		
		try {
			Files.write(Paths.get(pathFile), sb.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 /*
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
 */
	public MapPLNE(MapPLNE map) {	
		super(map);
	}

	public MapPLNE( int numNodes) {
		super(numNodes);
	}
	
	public MapPLNE(){
		super();
	}

	public MapPLNE(String filename) {
		super(filename);
	}
	
	@Override
	public int getCapacity() {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public int getDemand(int destination) {
		return 0;
	}
	
}
