package model.interfaces.msg;

/**
 * Interface that models a user message
 * @author BBC
 *
 */
public interface IUserMobilityMsg extends IMobilityMsg {
	/**
	 * Gets the ID of the user
	 * @return the ID of the user
	 */
	String getUserID();
}
