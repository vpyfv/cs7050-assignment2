package assignment.actors;

public class DataKey {
	private String name;
	private int generation;

	// default constructor
	public DataKey() {
		this(null, 0);
	}
        
	public DataKey(String nm, int gen) {
		name = nm;
		generation = gen;
	}

	public String getName() {
		return name;
	}

	public int getGeneration() {
		return generation;
	}

	/**
	 * Returns 0 if this DataKey is equal to k, returns -1 if this DataKey is smaller
	 * than k, and it returns 1 otherwise. 
	 */
	public int compareTo(DataKey k) {
            if (this.getGeneration() == k.getGeneration()) {
                int compare = this.name.compareTo(k.getName());
                if (compare == 0){
                     return 0;
                } 
                else if (compare < 0) {
                    return -1;
                }
            }
            else if(this.getGeneration() < k.getGeneration()){
                    return -1;
            }
            return 1;
            
	}

	@Override
	public String toString() {
		return "DataKey{" +
				"name='" + name + '\'' +
				", generation=" + generation +
				'}';
	}
}
