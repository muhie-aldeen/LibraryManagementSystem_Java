package library;

public class BorrowRecord {
    public int userId;
    public int bookIsbn;
    public String borrowDate;
    public String expectedReturnDate;
    public String status;
    public BorrowRecord(int userId, int bookIsbn, String borrowDate, String expectedReturnDate) {
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
        this.status="مستعار ";
    }
}