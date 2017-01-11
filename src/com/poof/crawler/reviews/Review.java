package com.poof.crawler.reviews;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author wilkey 
 * @mail admin@wilkey.vip
 * @Date 2017年1月10日 下午4:28:12
 */
public class Review implements Comparable<Review> {
	/**
	 * All the pieces in a review on Amazon.com
	 * 
	 * @param aitemID
	 *            the unique item ID of a product
	 * @param areviewID
	 *            the unique review ID
	 * @param acustomerName
	 *            displayed name of a customer
	 * @param acustomerID
	 *            unique customer ID
	 * @param atitle
	 *            title of the review
	 * @param arating
	 *            star rating out of 5 given by the customer
	 * @param afullRating
	 *            max rating can be given (5 for now)
	 * @param ahelpfulVotes
	 *            number of readers who rated the review as helpful
	 * @param atotalVotes
	 *            total number of readers who voted usefulness of the review
	 * @param verifiedornot
	 *            whether the review is from a verified purchase
	 * @param realnameornot
	 *            whether the customer is using his real name when writing the
	 *            review (obsolete, amazon no longer displays the badge)
	 * @param aReviewDate
	 *            date of the review
	 * @param acontent
	 *            textual content of the review
	 */
	
	
	String itemID;
	String reviewID;
	String customerName;
	String customerID;
	String title;
	double rating;
	double fullRating;
	int helpfulVotes;
	int totalVotes;
	boolean verifiedPurchase;
	String realName;
	Date reviewDate;
	String content;
	String comment;
	
	public Review(String aitemID, String areviewID, String acustomerName,
			String acustomerID, String atitle, int arating, int afullRating,
			int ahelpfulVotes, int atotalVotes, boolean verifiedornot,
			String realnameornot, Date aReviewDate, String acontent,String Comment) {
		itemID = aitemID;
		reviewID = areviewID;
		customerName = acustomerName;
		customerID = acustomerID;
		title = atitle;
		rating = arating;
		fullRating = afullRating;
		helpfulVotes = ahelpfulVotes;
		totalVotes = atotalVotes;
		verifiedPurchase = verifiedornot;
		realName = realnameornot;
		reviewDate = aReviewDate;
		content = acontent;
		comment = Comment;
	}
	
	
	//getter and setter methods 
	
	public String getComment() {
		return comment;
	}



	public void setComment(String comments) {
		this.comment= comments;
	}

	
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}



	public String getItemID() {
		return itemID;
	}



	public void setItemID(String itemID) {
		this.itemID = itemID;
	}



	public String getReviewID() {
		return reviewID;
	}



	public void setReviewID(String reviewID) {
		this.reviewID = reviewID;
	}



	public String getCustomerName() {
		return customerName;
	}



	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}



	public String getCustomerID() {
		return customerID;
	}



	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public double getRating() {
		return rating;
	}



	public void setRating(double rating) {
		this.rating = rating;
	}



	public double getFullRating() {
		return fullRating;
	}



	public void setFullRating(double fullRating) {
		this.fullRating = fullRating;
	}



	public int getHelpfulVotes() {
		return helpfulVotes;
	}



	public void setHelpfulVotes(int helpfulVotes) {
		this.helpfulVotes = helpfulVotes;
	}



	public int getTotalVotes() {
		return totalVotes;
	}



	public void setTotalVotes(int totalVotes) {
		this.totalVotes = totalVotes;
	}



	public boolean isVerifiedPurchase() {
		return verifiedPurchase;
	}



	public void setVerifiedPurchase(boolean verifiedPurchase) {
		this.verifiedPurchase = verifiedPurchase;
	}



	public String getRealName() {
		return realName;
	}



	public void setRealName(String realName) {
		this.realName = realName;
	}



	public Date getReviewDate() {
		return reviewDate;
	}



	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}

//add comparator to sort date

	public int compareTo(Review r) {
		return r.getReviewDate().compareTo(getReviewDate());
	}
	
	

	
}
