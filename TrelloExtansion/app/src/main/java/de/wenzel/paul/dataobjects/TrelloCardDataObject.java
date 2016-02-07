package de.wenzel.paul.dataobjects;

import java.util.Date;

/**
 * Die Klasse {@link TrelloCardDataObject} [...]
 * 
 * 
 * @author Paul Wenzel
 *
 */
public class TrelloCardDataObject {
	
/////////////////////////////////////////////////Datenfelder/////////////////////////////////////////////////
	
	private String cardId;
	private String cardShortUrl;
	private String boardName;
	private String boardId;
	private String listName;
	private String cardName;
	private Date dueDate;
	private boolean highPriority;
	private String highPriorityLabelId;

/////////////////////////////////////////////////Konstruktor/////////////////////////////////////////////////
	
	/**
	 * Der Konstruktor der Klasse {@link TrelloCardDataObject}.
	 *
	 * @param boardName
	 * @param listName
	 * @param cardName
	 * @param dueDate 
	 */
	public TrelloCardDataObject(String cardId, String cardShortUrl, String boardId, String boardName, String listName, String cardName, Date dueDate, boolean highPriority, String highPriorityLabelId) {
		//Datenfelder initialisieren
		this.cardId = cardId;
		this.cardShortUrl = cardShortUrl;
		this.boardId = boardId;
		this.boardName = boardName;
		this.listName = listName;
		this.cardName = cardName;
		this.dueDate = dueDate;
		this.highPriority = highPriority;
        this.highPriorityLabelId = highPriorityLabelId;
	}

//////////////////////////////////////////////Getter und Setter//////////////////////////////////////////////

	public String getCardId() {
		return cardId;
	}

    public String getCardShortUrl() {
        return cardShortUrl;
    }

	public String getBoardId() {
		return boardId;
	}

	public String getBoardName() {
		return boardName;
	}
	
	public String getListName() {
		return listName;
	}
	
	public String getCardName() {
		return cardName;
	}
	
	public Date getDueDate() {
		return dueDate;
	}

	public boolean isHighPriority() {
		return highPriority;
	}

    public String getHighPriorityLabelId() {
        return highPriorityLabelId;
    }

    ///////////////////////////////////////////////geerbte Methoden//////////////////////////////////////////////

	@Override
	public String toString() {
		return "/////////////////////////////////\n" + cardId + "\n" + cardShortUrl + "\n" + cardName + "\n" + boardId +"\n" + boardName + "\n" + listName + "\n" + dueDate + "\n" + "highPriority:" + highPriority + "\n" + highPriorityLabelId;
	}


//////////////////////////////////////////////////Methoden///////////////////////////////////////////////////
	
	
	
	
	
///////////////////////////////////////////////Innere Klassen////////////////////////////////////////////////	
	
	
	
	
}