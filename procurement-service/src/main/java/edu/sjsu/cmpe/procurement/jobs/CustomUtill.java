package edu.sjsu.cmpe.procurement.jobs;

import edu.sjsu.cmpe.procurement.domain.Book;

public class CustomUtill {
	public static String ConvertToFormat(Book input){
		String output = "";
		long isbn = input.getIsbn();
		output = String.valueOf(isbn)+":"+input.getTitle()+":"+input.getCategory()+":"+input.getCoverimage();
		return output;
	}


}
