package il.ac.tau.cs.software1.inventory;

/*
 * A simple array-based Inventory (Question 6).
 */
public class NaiveInventory implements Inventory {

	private static final int INITIAL_CAPACITY = 100;
	private Collectible[] collected;
	private int counter;
	
	public NaiveInventory() {
		collected = new Collectible[INITIAL_CAPACITY];
		counter = 0;
	}

	@Override
	public void collect(Collectible collectible) {
		if (counter == collected.length){doubleSize();}
		collected[counter] = collectible;
		counter++;
	}

	private void doubleSize(){
		Collectible[] update = new Collectible[2*collected.length];
		for (int i = 0; i < collected.length; i++) {
			update[i] = collected[i];
		}
		collected = update;
	}

	@Override
	public float getTotalWeight() {
		float total = 0.f;
		for (int j =0; j<counter; j++) {
			total += collected[j].getWeight();	
		}
		return total;
	}

	@Override
	public int getCurrentCount() {
		return counter;
	}

	// Returns the array of Collectibles that is stored in the field of this class
	public Collectible[] getCollArr() {
		return collected;
	}
}
