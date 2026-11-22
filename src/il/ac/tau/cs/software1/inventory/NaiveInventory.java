package il.ac.tau.cs.software1.inventory;

/*
 * A simple array-based Inventory (Question 6).
 */
public class NaiveInventory implements Inventory {

	private static final int INITIAL_CAPACITY = 100;
	private Collectible[] collectibles;
	private int counter;
	
	public NaiveInventory() {
		collectibles = new Collectible[INITIAL_CAPACITY];
		counter = 0;
	}

	@Override
	public void collect(Collectible collectible) {
		if (counter == collectibles.length){doubleSize();}
		collectibles[counter] = collectible;
		counter++;
	}

	private void doubleSize(){
		Collectible[] update = new Collectible[2*collectibles.length];
		for (int i = 0; i < collectibles.length; i++) {
			update[i] = collectibles[i];
		}
		collectibles = update;
	}

	@Override
	public float getTotalWeight() {
		float total = 0.f;
		for (int j =0; j<counter; j++) {
			total += collectibles[j].getWeight();	
		}
		return total;
	}

	@Override
	public int getCurrentCount() {
		return counter;
	}

	// Returns the array of Collectibles that is stored in the field of this class
	public Collectible[] getCollArr() {
		return collectibles;
	}
}
