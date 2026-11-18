package library;

public class AVLNode {
    public Book book;
    public AVLNode left;
    public AVLNode right;
    public int height;

    public AVLNode(Book book) {
        this.book = book;
        this.left = null;
        this.right = null;
        this.height = 1;
    }
}