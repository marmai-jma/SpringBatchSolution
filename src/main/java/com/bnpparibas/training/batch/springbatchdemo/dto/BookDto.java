package com.bnpparibas.training.batch.springbatchdemo.dto;

public class BookDto {

	private String title;

	private String author;

	private String isbn;

	private String publisher;

	private Integer publishedOn;

	// Constructors, Getters and Setters

	/**
	 * Default constructor used for BeanWrapperFieldSetMapper that using Reflexion
	 * to populate fields against the file.
	 *
	 * mandatory to avoid java.lang.InstantiationException
	 */
	public BookDto() {

	}

	// All fields constructor
	public BookDto(final String title, final String author, final String isbn, final String publisher,
			final Integer publishedOn) {
		super();
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.publisher = publisher;
		this.publishedOn = publishedOn;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(final String author) {
		this.author = author;
	}

	/**
	 * @return the isbn
	 */
	public String getIsbn() {
		return isbn;
	}

	/**
	 * @param isbn
	 *            the isbn to set
	 */
	public void setIsbn(final String isbn) {
		this.isbn = isbn;
	}

	/**
	 * @return the publisher
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * @param publisher
	 *            the publisher to set
	 */
	public void setPublisher(final String publisher) {
		this.publisher = publisher;
	}

	/**
	 * @return the publishedOn
	 */
	public Integer getPublishedOn() {
		return publishedOn;
	}

	/**
	 * @param publishedOn
	 *            the publishedOn to set
	 */
	public void setPublishedOn(final Integer publishedOn) {
		this.publishedOn = publishedOn;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BookDto [title=").append(title) //
		.append(", author=").append(author) //
		.append(", isbn=").append(isbn) //
		.append(", publisher=").append(publisher) //
		.append(", publishedOn=").append(publishedOn).append("]");
		return builder.toString();
	}

}
