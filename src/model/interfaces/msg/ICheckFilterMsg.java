package model.interfaces.msg;

/**
 * Interface that models the msg sent from the user to the server in order to notify the positive filter checks
 * @author Matteo
 *
 */
public interface ICheckFilterMsg extends IMobilityMsg {
	/**
	 * gets a String with the positive checks
	 * @return
	 */
	String getPositiveChecks();
}
