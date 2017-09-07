package model.interfaces.msg;

import java.util.List;

public interface ISBFMsg extends IMobilityMsg {
	/**
	 * Interface that models the message in which the SBF is incapsuled
	 * @author Matteo
	 *
	 */
	
	/**
	 * gets the bit mapping of the SBF
	 * @return bit mapping
	 */
	int getBitMapping();
	
	/**
	 * gets the hash family of the SBF
	 * @return hash family (1: SHA1
		               	    4: MD4
		                 	5: MD5)
	 */
	int getHashFamily();
	
	/**
	 * gets the number of hash functions of the SBF
	 * @return number of hash functions
	 */
	int getHashNumber();
	
	/**
	 * gets number of areas of the SBF
	 * @return number of areas
	 */
	int getAreaNumber();
	
	/**
	 * gets the hash salt of the SBF
	 * @return hash salt
	 */
	String getHashSalt();
	
	/**
	 * gets the SBF area elements
	 * @return SBF area elements
	 */
	List<Integer> getSBF();
}
