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
	
	private String cardID;
	private String cardShortUrl;
	private String boardName;
	private String listName;
	private String cardName;
	private Date dueDate;
	
/////////////////////////////////////////////////Konstruktor/////////////////////////////////////////////////
	
	/**
	 * Der Konstruktor der Klasse {@link TrelloCardDataObject}.
	 *
	 * @param boardName
	 * @param listName
	 * @param cardName
	 * @param dueDate 
	 */
	public TrelloCardDataObject(String cardID, String cardShortUrl, String boardName, String listName, String cardName, Date dueDate) {
		//Datenfelder initialisieren
		this.cardID = cardID;
		this.cardShortUrl = cardShortUrl;
		this.boardName = boardName;
		this.listName = listName;
		this.cardName = cardName;
		this.dueDate = dueDate;
	}

//////////////////////////////////////////////Getter und Setter//////////////////////////////////////////////

	public String getCardID() {
		return cardID;
	}

    public String getCardShortUrl() {
        return cardShortUrl;
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
	
///////////////////////////////////////////////geerbte Methoden//////////////////////////////////////////////

	@Override
	public String toString() {
		return "/////////////////////////////////\n" + cardID + "\n" + cardShortUrl + "\n" + cardName + "\n" + boardName + "\n" + listName + "\n" + dueDate;
	}


//////////////////////////////////////////////////Methoden///////////////////////////////////////////////////
	
	
	
	
	
///////////////////////////////////////////////Innere Klassen////////////////////////////////////////////////	
	
	
	
	
}