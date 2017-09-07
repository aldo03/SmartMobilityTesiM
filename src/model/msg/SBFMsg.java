package model.msg;

import java.util.List;

import model.interfaces.msg.ISBFMsg;

public class SBFMsg implements ISBFMsg {

	
	private String msgId;
	private int bitMapping;
	private int hashFamily;
	private int hashNumber;
	private int areaNumber;
	private String hashSalt;
	private List<Integer> sbf;
	
	
	
	public SBFMsg(String msgId, int bitMapping, int hashFamily, int hashNumber, int areaNumber, String hashSalt,
			List<Integer> sbf) {
		this.msgId = msgId;
		this.bitMapping = bitMapping;
		this.hashFamily = hashFamily;
		this.hashNumber = hashNumber;
		this.areaNumber = areaNumber;
		this.hashSalt = hashSalt;
		this.sbf = sbf;
	}

	@Override
	public String getMsgID() {
		return this.msgId;
	}

	@Override
	public int getBitMapping() {
		return this.bitMapping;
	}

	@Override
	public int getHashFamily() {
		return this.hashFamily;
	}

	@Override
	public int getHashNumber() {
		return this.hashNumber;
	}

	@Override
	public int getAreaNumber() {
		return this.areaNumber;
	}

	@Override
	public String getHashSalt() {
		return this.hashSalt;
	}

	@Override
	public List<Integer> getSBF() {
		return this.sbf;
	}

}
