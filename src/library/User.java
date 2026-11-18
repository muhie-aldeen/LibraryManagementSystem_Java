package library;
import java.util.ArrayList;

public class User {

    String name;
    int id;
    boolean isGraduate;
    int borrowedCount;
    ArrayList<Book> borrowedBooks;
    public ArrayList<BorrowRecord> historyRecords;

    public User(int id, String name, boolean isGraduate) {
        this.name = name;
        this.id = id;
        this.isGraduate = isGraduate;
        this.borrowedCount = 0;
        this.borrowedBooks = new ArrayList<>();
        this.historyRecords = new ArrayList<>();
    }

    public void borrowBook(Book book, String borrowDate, String expectedReturnDate) {
        borrowedBooks.add(book);
        borrowedCount++;
        BorrowRecord record = new BorrowRecord(this.id, book.ISBN, borrowDate, expectedReturnDate);
        historyRecords.add(record);
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
        borrowedCount--;
    }

    public boolean hasBook(int ISBN) {
        for (Book b : borrowedBooks) {
            if (b.ISBN == ISBN)
                return true;
        }
        return false;
    }
}