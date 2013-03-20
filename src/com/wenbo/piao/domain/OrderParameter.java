package com.wenbo.piao.domain;

import org.jsoup.nodes.Document;

public class OrderParameter {

	private Document document;
	
	private int ticketType;
	
	private String[] parameters;

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public int getTicketType() {
		return ticketType;
	}

	public void setTicketType(int ticketType) {
		this.ticketType = ticketType;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}
	
	
}
