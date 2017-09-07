package model.msg;

import model.interfaces.msg.ICellReachedMsg;

public class CellReachedMsg implements ICellReachedMsg {

	private String msgId;
	private String cellId;
	private String userId;
	
	public CellReachedMsg(String msgId, String cellId, String userId){
		this.msgId = msgId;
		this.cellId = cellId;
		this.userId = userId;
	}
	
	@Override
	public String getMsgID() {
		return this.msgId;
	}

	@Override
	public String getCellId() {
		return this.cellId;
	}

	@Override
	public String getUserID() {
		return this.userId;
	}

}
