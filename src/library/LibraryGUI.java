package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LibraryGUI extends JFrame {
    private LibrarySystem system;

    private DefaultTableModel bookTableModel;
    private DefaultTableModel userTableModel;
    private JLabel bookReport;
    private JLabel authorReport;
    private JLabel countReport;

    public LibraryGUI() {
        system = new LibrarySystem();
        setTitle("نظام المكتبة");
        setSize(980, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("الكتب والمستعيرين", createManagementPanel());
        tabbedPane.addTab("العمليات والبحث", createOperationsPanel());
        tabbedPane.addTab("التقارير والإحصائيات", createReportsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        prepareDemoData();
        refreshData();
    }

    private JPanel createManagementPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bookPanel = new JPanel(new BorderLayout(5, 5));
        bookPanel.setBorder(BorderFactory.createTitledBorder("بيانات الكتب"));

        JPanel bookInput = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField txtIsbn = new JTextField();
        JTextField txtTitle = new JTextField();
        JTextField txtAuthor = new JTextField();
        JTextField txtCopies = new JTextField();

        bookInput.add(new JLabel("ISBN:"));
        bookInput.add(txtIsbn);
        bookInput.add(new JLabel("العنوان:"));
        bookInput.add(txtTitle);
        bookInput.add(new JLabel("المؤلف:"));
        bookInput.add(txtAuthor);
        bookInput.add(new JLabel("عدد النسخ الجديد:")); // توضيح بسيط للوظيفة
        bookInput.add(txtCopies);

        JButton btnAddBook = new JButton("إضافة كتاب");
        JButton btnDeleteBook = new JButton("حذف كتاب");
        JButton btnUpdateCopies = new JButton("تعديل النسخ"); // الزر الجديد

        // تجميع الأزرار الثلاثة في صف واحد متناسق وطبيعي
        JPanel bookButtonsPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        bookButtonsPanel.add(btnAddBook);
        bookButtonsPanel.add(btnUpdateCopies);
        bookButtonsPanel.add(btnDeleteBook);

        JPanel bookTop = new JPanel(new BorderLayout(5, 5));
        bookTop.add(bookInput, BorderLayout.CENTER);
        bookTop.add(bookButtonsPanel, BorderLayout.SOUTH);
        bookPanel.add(bookTop, BorderLayout.NORTH);

        bookTableModel = new DefaultTableModel(new Object[]{"ISBN", "العنوان", "المؤلف", "المتاح", "الإجمالي"}, 0);
        JTable bookTable = new JTable(bookTableModel);
        bookPanel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        JPanel userPanel = new JPanel(new BorderLayout(5, 5));
        userPanel.setBorder(BorderFactory.createTitledBorder("بيانات المستعيرين وسجلات الاستعارة"));

        JPanel userInput = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField txtUserId = new JTextField();
        JTextField txtUserName = new JTextField();
        JCheckBox chkGraduate = new JCheckBox("خريج");

        userInput.add(new JLabel("رقم المستعير (ID):"));
        userInput.add(txtUserId);
        userInput.add(new JLabel("الاسم:"));
        userInput.add(txtUserName);
        userInput.add(new JLabel("الحالة:"));
        userInput.add(chkGraduate);

        JButton btnAddUser = new JButton("إضافة مستخدم");

        JPanel userTop = new JPanel(new BorderLayout(5, 5));
        userTop.add(userInput, BorderLayout.CENTER);
        userTop.add(btnAddUser, BorderLayout.SOUTH);
        userPanel.add(userTop, BorderLayout.NORTH);

        userTableModel = new DefaultTableModel(new Object[]{"ID", "الاسم", "الحالة", "الكتب", "تاريخ الاستعارة", "تاريخ الإرجاع", "حالة الاستعارة"}, 0);
        JTable userTable = new JTable(userTableModel);
        userPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        mainPanel.add(bookPanel);
        mainPanel.add(userPanel);

        // حدث إضافة الكتب
        btnAddBook.addActionListener(e -> {
            try {
                int isbn = Integer.parseInt(txtIsbn.getText().trim());
                String title = txtTitle.getText().trim();
                String author = txtAuthor.getText().trim();
                int copies = Integer.parseInt(txtCopies.getText().trim());

                if (isbn <= 0 || copies < 0) {
                    JOptionPane.showMessageDialog(this, "خطأ: يرجى التأكد من صحة البيانات المدخلة.");
                    return;
                }

                Book existing = system.books.search(isbn);
                if (existing != null) {
                    JOptionPane.showMessageDialog(this, "الكتاب موجود بالفعل، إذا كنت تريد تعديل النسخ اضغط على زر 'تعديل النسخ'.");
                } else {
                    system.addBook(new Book(isbn, title, author, copies));
                    JOptionPane.showMessageDialog(this, "تم إضافة الكتاب بنجاح.");
                }
                refreshData();
                txtIsbn.setText("");
                txtTitle.setText("");
                txtAuthor.setText("");
                txtCopies.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "خطأ: يرجى التأكد من صحة البيانات المدخلة.");
            }
        });


        btnUpdateCopies.addActionListener(e -> {
            try {
                String isbnText = txtIsbn.getText().trim();
                String copiesText = txtCopies.getText().trim();

                if (isbnText.isEmpty() || copiesText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "يرجى إدخال رقم ISBN وعدد النسخ الجديد لإتمام التعديل.");
                    return;
                }

                int isbn = Integer.parseInt(isbnText);
                int additionalCopies = Integer.parseInt(copiesText);

                if (isbn <= 0 || additionalCopies < 0) {
                    JOptionPane.showMessageDialog(this, "خطأ: يرجى التأكد من صحة الأرقام المدخلة.");
                    return;
                }

                Book existing = system.books.search(isbn);
                if (existing == null) {
                    JOptionPane.showMessageDialog(this, "عذراً، هذا الكتاب غير مسجل لكي يتم تعديل نسخه.");
                    return;
                }

                // استدعاء دالة التحديث من السستم مباشرة
                system.updateBookCopies(isbn, additionalCopies);
                JOptionPane.showMessageDialog(this, "تم تحديث عدد النسخ بنجاح والمخزون ");

                refreshData();
                txtIsbn.setText("");
                txtTitle.setText("");
                txtAuthor.setText("");
                txtCopies.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "خطأ: يرجى التأكد من صحة البيانات المدخلة.");
            }
        });

        btnDeleteBook.addActionListener(e -> {
            try {
                String isbnText = txtIsbn.getText().trim();
                if (isbnText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "يرجى إدخال رقم ISBN في الحقل المخصص أولاً لتتم عملية الحذف.");
                    return;
                }
                int isbn = Integer.parseInt(isbnText);

                if (isbn <= 0) {
                    JOptionPane.showMessageDialog(this, "خطأ: يرجى إدخال رقم ISBN صحيح وصالح.");
                    return;
                }

                Book toDelete = system.books.search(isbn);
                if (toDelete == null) {
                    JOptionPane.showMessageDialog(this, "عذراً، هذا الكتاب غير مسجل لكي يتم حذفه.");
                    return;
                }

                system.books.delete(isbn);
                JOptionPane.showMessageDialog(this, "تم تنفيذ عملية الحذف بنجاح.");

                refreshData();
                txtIsbn.setText("");
                txtTitle.setText("");
                txtAuthor.setText("");
                txtCopies.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "خطأ: يرجى التأكد من إدخال رقم ISBN صحيح.");
            }
        });

        btnAddUser.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtUserId.getText().trim());
                String name = txtUserName.getText().trim();
                boolean isGrad = chkGraduate.isSelected();

                if (id <= 0) {
                    JOptionPane.showMessageDialog(this, "خطأ: يرجى التأكد من صحة البيانات المدخلة.");
                    return;
                }

                system.addUser(new User(id, name, isGrad));
                JOptionPane.showMessageDialog(this, "تم إضافة المستعير بنجاح.");
                refreshData();
                txtUserId.setText("");
                txtUserName.setText("");
                chkGraduate.setSelected(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "خطأ: يرجى إدخال رقم ID صحيح.");
            }
        });

        return mainPanel;
    }

    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel opGrid = new JPanel(new GridLayout(2, 1, 10, 10));

        JPanel txPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        txPanel.setBorder(BorderFactory.createTitledBorder("عمليات الإعارة والإرجاع"));
        JTextField txtOpIsbn = new JTextField(10);
        JTextField txtOpUserId = new JTextField(10);
        JButton btnBorrow = new JButton("إعارة كتاب");
        JButton btnReturn = new JButton("إرجاع كتاب");

        txPanel.add(new JLabel("ISBN الكتاب:"));
        txPanel.add(txtOpIsbn);
        txPanel.add(new JLabel("ID المستعير:"));
        txPanel.add(txtOpUserId);
        txPanel.add(btnBorrow);
        txPanel.add(btnReturn);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        searchPanel.setBorder(BorderFactory.createTitledBorder("البحث عن كتاب"));
        JTextField txtSearchIsbn = new JTextField(12);
        JButton btnSearch = new JButton("بحث");
        JTextArea txtSearchResult = new JTextArea(4, 40);
        txtSearchResult.setEditable(false);
        txtSearchResult.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        searchPanel.add(new JLabel("أدخل رقم ISBN:"));
        searchPanel.add(txtSearchIsbn);
        searchPanel.add(btnSearch);
        searchPanel.add(new JScrollPane(txtSearchResult));

        opGrid.add(txPanel);
        opGrid.add(searchPanel);
        panel.add(opGrid, BorderLayout.CENTER);

        btnBorrow.addActionListener(e -> {
            try {
                int isbn = Integer.parseInt(txtOpIsbn.getText().trim());
                int uId = Integer.parseInt(txtOpUserId.getText().trim());

                User u = system.findUser(uId);
                if (u == null) {
                    JOptionPane.showMessageDialog(this, "المستعير غير مسجل !");
                    return;
                }

                Book b = system.books.search(isbn);
                if (b == null) {
                    JOptionPane.showMessageDialog(this, "الكتاب غير موجود في النظام!");
                    return;
                }

                int result = system.borrowBook(isbn, uId);

                if (result == 1) {
                    JOptionPane.showMessageDialog(this, "تمت عملية الإعارة بنجاح.");
                } else if (result == 2) {
                    JOptionPane.showMessageDialog(this, "عذراً، هذا المستعير تجاوز الحد الأقصى المسموح به (3 كتب كحد أقصى).");
                } else if (result == 3) {
                    JOptionPane.showMessageDialog(this, "الكتاب غير متوفر حالياً، تم إضافة المستعير إلى قائمة الانتظار.");
                }

                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "حدث خطأ أثناء تنفيذ العملية.");
            }
        });

        btnReturn.addActionListener(e -> {
            try {
                int isbn = Integer.parseInt(txtOpIsbn.getText().trim());
                int uId = Integer.parseInt(txtOpUserId.getText().trim());

                User u = system.findUser(uId);
                if (u == null || !u.hasBook(isbn)) {
                    JOptionPane.showMessageDialog(this, "خطأ: هذا المستخدم لم يقم باستعارة هذا الكتاب أصلاً.");
                    return;
                }

                system.returnBook(isbn, uId);
                JOptionPane.showMessageDialog(this, "تم إرجاع الكتاب وتحديث البيانات والمخزون بنجاح.");

                refreshData();
                txtOpIsbn.setText("");
                txtOpUserId.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "فشلت العملية، يرجى التحقق من البيانات المدخلة.");
            }
        });

        btnSearch.addActionListener(e -> {
            try {
                int isbn = Integer.parseInt(txtSearchIsbn.getText().trim());
                Book b = system.books.search(isbn);
                if (b != null) {
                    txtSearchResult.setText(
                            " معلومات الكتاب:\n" +
                                    " - العنوان: " + b.title + "\n" +
                                    " - المؤلف: " + b.author + "\n" +
                                    " - النسخ المتاحة: " + b.availableCopies + " من إجمالي: " + b.totalCopies
                    );
                } else {
                    txtSearchResult.setText("لم يتم العثور على هذا الكتاب في السجلات.");
                }
            } catch (Exception ex) {
                txtSearchResult.setText("يرجى إدخال رقم ISBN صحيح.");
            }
        });

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel card1 = new JPanel(new BorderLayout());
        card1.setBorder(BorderFactory.createTitledBorder("الكتاب الأكثر استعارة"));
        bookReport = new JLabel("-", SwingConstants.CENTER);
        bookReport.setFont(new Font("Arial", Font.BOLD, 14));
        card1.add(bookReport, BorderLayout.CENTER);

        JPanel card2 = new JPanel(new BorderLayout());
        card2.setBorder(BorderFactory.createTitledBorder("المؤلف الأكثر قراءة"));
        authorReport = new JLabel("-", SwingConstants.CENTER);
        authorReport.setFont(new Font("Arial", Font.BOLD, 14));
        card2.add(authorReport, BorderLayout.CENTER);

        JPanel card3 = new JPanel(new BorderLayout());
        card3.setBorder(BorderFactory.createTitledBorder("إجمالي الكتب المتوفرة في المخزن"));
        countReport = new JLabel("-", SwingConstants.CENTER);
        countReport.setFont(new Font("Arial", Font.BOLD, 18));
        card3.add(countReport, BorderLayout.CENTER);

        panel.add(card1);
        panel.add(card2);
        panel.add(card3);

        return panel;
    }

    private void refreshData() {
        if (bookTableModel == null || userTableModel == null) return;

        bookTableModel.setRowCount(0);
        for (Book b : system.getAllBooksList()) {
            bookTableModel.addRow(new Object[]{b.ISBN, b.title, b.author, b.availableCopies, b.totalCopies});
        }

        userTableModel.setRowCount(0);
        for (User u : system.users) {
            String bDate = "-";
            String rDate = "-";
            String currentStatus = "-";

            if (u.historyRecords != null && !u.historyRecords.isEmpty()) {
                BorrowRecord targetRecord = null;

                for (BorrowRecord record : u.historyRecords) {
                    if (record.status.equals("مستعار")) {
                        targetRecord = record;
                        break;
                    }
                }

                if (targetRecord == null) {
                    targetRecord = u.historyRecords.get(u.historyRecords.size() - 1);
                }

                bDate = targetRecord.borrowDate;
                rDate = targetRecord.expectedReturnDate;
                currentStatus = targetRecord.status;
            }

            userTableModel.addRow(new Object[]{
                    u.id,
                    u.name,
                    u.isGraduate ? "خريج" : "طالب",
                    u.borrowedCount,
                    bDate,
                    rDate,
                    currentStatus
            });
        }

        bookReport.setText(system.getMostBorrowedBook());
        authorReport.setText(system.getMostPopularAuthor());
        countReport.setText(system.getTotalAvailableCopiesCount() + " كتاب");
    }

    private void prepareDemoData() {
        system.addBook(new Book(101, "Data Structures in Java", "Robert Lafore", 2));
        system.addBook(new Book(105, "Introduction to Algorithms", "Thomas Cormen", 1));
        system.addBook(new Book(103, "Clean Code", "Robert Martin", 3));

        system.addUser(new User(1, "شادي", false));
        system.addUser(new User(2, "أحمد", true));
    }
}