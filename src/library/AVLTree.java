package library;
public class AVLTree {

    class Node {
        Book book;
        Node left, right;
        int height;

        Node(Book book) {
            this.book = book;
            this.height = 1;
        }
    }
    Node root;

    public AVLTree() {
        root = null;
    }

    int getHeight(Node n) {
        if (n == null) return 0;
        return n.height;
    }

    int getBalance(Node n) {
        if (n == null) return 0;
        return getHeight(n.left) - getHeight(n.right);
    }

    Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;
        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;

        return x;
    }

    Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;

        return y;
    }

    Node insert(Node node, Book book) {

        if (node == null)
            return new Node(book);

        if (book.ISBN < node.book.ISBN)
            node.left = insert(node.left, book);
        else if (book.ISBN > node.book.ISBN)
            node.right = insert(node.right, book);
        else
            return node;

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));

        int balance = getBalance(node);

        // LL
        if (balance > 1 && book.ISBN < node.left.book.ISBN)
            return rightRotate(node);

        // RR
        if (balance < -1 && book.ISBN > node.right.book.ISBN)
            return leftRotate(node);

        // LR
        if (balance > 1 && book.ISBN > node.left.book.ISBN) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // RL
        if (balance < -1 && book.ISBN < node.right.book.ISBN) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void insert(Book book) {
        root = insert(root, book);
    }

    // -------------------------
    // getMinValueNode
    // -------------------------
    Node getMinValueNode(Node node) {
        Node current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    Node delete(Node node, int ISBN) {

        if (node == null)
            return node;

        if (ISBN < node.book.ISBN)
            node.left = delete(node.left, ISBN);

        else if (ISBN > node.book.ISBN)
            node.right = delete(node.right, ISBN);

        else {
            // حالة بدون ابناء
            if (node.left == null && node.right == null)
                return null;

                // حالة ابن واحد
            else if (node.left == null)
                return node.right;

            else if (node.right == null)
                return node.left;

            // حالة ابنين
            Node temp = getMinValueNode(node.right);
            node.book = temp.book;
            node.right = delete(node.right, temp.book.ISBN);
        }

        if (node == null)
            return null;

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));

        int balance = getBalance(node);

        // LL
        if (balance > 1 && getBalance(node.left) >= 0)
            return rightRotate(node);

        // LR
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // RR
        if (balance < -1 && getBalance(node.right) <= 0)
            return leftRotate(node);

        // RL
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void delete(int ISBN) {
        root = delete(root, ISBN);
    }

    public Book search(int ISBN) {
        Node current = root;

        while (current != null) {
            if (ISBN == current.book.ISBN)
                return current.book;
            else if (ISBN < current.book.ISBN)
                current = current.left;
            else
                current = current.right;
        }

        return null;
    }

    void inOrder(Node node) {
        if (node == null)
            return;

        inOrder(node.left);

        System.out.println("ISBN: " + node.book.ISBN +
                        " | العنوان: " + node.book.title +
                        " | المؤلف: " + node.book.author +
                        " | المتاح: " + node.book.availableCopies +
                        " | الكلي: " + node.book.totalCopies
        );

        inOrder(node.right);
    }

    public void inOrder() {
        inOrder(root);
    }
}