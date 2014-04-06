package edu.sjsu.cmpe.procurement.domain;

import java.util.ArrayList;
import java.util.List;

public class PublisherData {
	private String id;
	private List<Integer> book_isbns = new ArrayList<Integer>();

	public List<Integer> getbookisbns() {
		return book_isbns;
	}
	public void setOrder_book_isbns(List<Integer> book_isbns) {
		this.book_isbns = book_isbns;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	} 

}
