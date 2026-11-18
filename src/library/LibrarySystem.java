package library;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class LibrarySystem {

    public AVLTree books;
    public ArrayList<User> users;
    public HashMap<Integer, WaitingQueue> waitingLists;

    public LibrarySystem() {
        books = new AVLTree();
        users = new ArrayList<>();
        waitingLists = new HashMap<>();
    }

    private void processBorrow(User user, Book book) {
        String bDate = LocalDate.now().toString();
        String rDate = LocalDate.now().plusDays(14).toString();
        user.borrowBook(book, bDate, rDate);
        book.borrowCount++;
    }

    public void addBook(Book book) {
        books.insert(book);
        System.out.println("تمت إضافة الكتاب بنجاح.");
    }

    public void addUser(User user) {
        users.add(user);
        System.out.println("تمت إضافة المستخدم بنجاح.");
    }

    public User findUser(int id) {
        for (User u : users) {
            if (u.id == id)
                return u;
        }
        return null;
    }

    public void updateBookCopies(int ISBN, int additionalCopies) {
        Book book = books.search(ISBN);
        if (book != null) {
            book.totalCopies += additionalCopies;
            book.availableCopies += additionalCopies;

            WaitingQueue queue = waitingLists.get(ISBN);
            while (queue != null && !queue.isEmpty() && book.availableCopies > 0) {
                User next = queue.getNextUser();

                if (next.borrowedCount >= 3) {
                    continue;
                }

                book.availableCopies--;
                processBorrow(next, book);
            }
        }
    }

    public int borrowBook(int ISBN, int userId) {
        Book book = books.search(ISBN);
        if (book == null) return 0;

        User user = findUser(userId);
        if (user == null) return 0;

        if (user.borrowedCount >= 3) {
            return 2;
        }

        if (book.availableCopies > 0) {
            book.availableCopies--;
            processBorrow(user, book);
            return 1;
        }

        WaitingQueue queue = waitingLists.get(ISBN);
        if (queue == null) {
            queue = new WaitingQueue();
            waitingLists.put(ISBN, queue);
        }
        queue.addUser(user);
        return 3;
    }

    public void returnBook(int ISBN, int userId) {
        Book book = books.search(ISBN);
        User user = findUser(userId);

        if (book == null || user == null) return;
        if (!user.hasBook(ISBN)) return;

        user.returnBook(book);

        if (user.historyRecords != null && !user.historyRecords.isEmpty()) {
            for (int i = user.historyRecords.size() - 1; i >= 0; i--) {
                BorrowRecord record = user.historyRecords.get(i);
                if (record.bookIsbn == ISBN) {
                    record.status = "تم الإرجاع";
                    break;
                }
            }
        }

        WaitingQueue queue = waitingLists.get(ISBN);
        if (queue != null && !queue.isEmpty()) {
            User next = queue.getNextUser();

            if (next.borrowedCount >= 3) {
                book.availableCopies++;
                return;
            }

            processBorrow(next, book);
            return;
        }
        book.availableCopies++;
    }

    private void collectBooks(AVLTree.Node node, ArrayList<Book> allBooks) {
        if (node == null) return;
        collectBooks(node.left, allBooks);
        allBooks.add(node.book);
        collectBooks(node.right, allBooks);
    }

    public ArrayList<Book> getAllBooksList() {
        ArrayList<Book> list = new ArrayList<>();
        collectBooks(books.root, list);
        return list;
    }

    public String getMostBorrowedBook() {
        ArrayList<Book> allBooks = getAllBooksList();
        if (allBooks.isEmpty()) return "لا توجد كتب في المكتبة حالياً.";

        Book mostBorrowed = allBooks.get(0);
        for (Book b : allBooks) {
            if (b.borrowCount > mostBorrowed.borrowCount) {
                mostBorrowed = b;
            }
        }
        if (mostBorrowed.borrowCount == 0) return "لم يتم استعارة أي كتاب بعد.";
        return mostBorrowed.title + " (تمت استعارته " + mostBorrowed.borrowCount + " مرة)";
    }

    public String getMostPopularAuthor() {
        ArrayList<Book> allBooks = getAllBooksList();
        if (allBooks.isEmpty())
            return "لا توجد كتب";
        HashMap<String, Integer> authorScores = new HashMap<>();
        for (Book b : allBooks) {
            authorScores.put(b.author, authorScores.getOrDefault(b.author, 0) + b.borrowCount);
        }

        String topAuthor = "";
        int maxVotes = -1;
        for (Map.Entry<String, Integer> entry : authorScores.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                topAuthor = entry.getKey();
            }
        }
        if (maxVotes <= 0) return "لا يوجد استعارات للمؤلفين بعد";
        return topAuthor + " (إجمالي استعارات كتبه: " + maxVotes + ")";
    }

    public int getTotalAvailableCopiesCount() {
        ArrayList<Book> allBooks = getAllBooksList();
        int total = 0;
        for (Book b : allBooks) {
            total += b.availableCopies;
        }
        return total;
    }

    public void searchBook(int ISBN) {
        Book book = books.search(ISBN);
        if (book == null) return;
        System.out.println("اسم الكتاب: " + book.title);
    }

    public void displayBooks() {
        books.inOrder();
    }
}