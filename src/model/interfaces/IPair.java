package model.interfaces;

/**
 * interface that models a pair of object
 * @author BBC
 *
 */
public interface IPair<X,Y> {

	/**
	 * method invoked to get the first element of the pair
	 * @return first element of the pair
	 */
	X getFirst();
	
	/**
	 * method invoked to get the second element of the pair
	 * @return second element of the pair
	 */
	Y getSecond();
	
}
