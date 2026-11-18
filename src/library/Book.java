package library;

public class Book {
    public int ISBN;
    public String title;
    public String author;
    public int totalCopies;
    public int availableCopies;
    public int borrowCount;

    public Book() {
        this.ISBN = 0;
        this.title = "";
        this.author = "";
        this.totalCopies = 0;
        this.availableCopies = 0;
        this.borrowCount = 0;
    }

    public Book(int ISBN, String title, String author, int totalCopies) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }
}