package com.library;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class Library{
    private final String name;
    private final List<Book> books; // 書籍清單
    private final String DB_URL = "jdbc:sqlite:library.db"; // SQLite 資料庫 URL

    // 建構子
    public Library(String name) {
        this.name = name;
        this.books = new ArrayList<>(); // 初始化書籍清單
        initialDatabase(); // 初始化資料庫
        loadFromDatabase(); // 從資料庫載入書籍
        
    }

    private void initialDatabase(){
        // 這裡可以放置初始化資料庫的程式碼，例如建立資料表、插入初始資料等
        try(Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement()){
            String sql = """
                    CREATE TABLE IF NOT EXISTS books (
                    isbn TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    isAvailable INTEGER DEFAULT 1
                    )
                """;
            stmt.execute(sql);
            // 初始化資料庫時加入這兩張新表（放在 initialDatabase() 裡）
            String memberSql = """
                CREATE TABLE IF NOT EXISTS members (
                    member_id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    phone TEXT
                )
            """;
            stmt.execute(memberSql);

            String borrowSql = """
                CREATE TABLE IF NOT EXISTS borrow_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    isbn TEXT NOT NULL,
                    member_id TEXT NOT NULL,
                    borrow_date TEXT NOT NULL,
                    return_date TEXT
                )
            """;
            stmt.execute(borrowSql);
        } catch (SQLException e) {
            System.out.println("資料庫初始化失敗: " + e.getMessage());
        }
    }
    // 從資料庫載入書籍
    private void loadFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
            books.clear(); // 清空目前的書籍清單
            while(rs.next()){
                Book book = new Book(
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn")
                );
                book.setAvailable(rs.getInt("isAvailable") == 1);
                books.add(book);
            }
        } catch (SQLException e) {
            System.out.println("載入失敗：" + e.getMessage());
        }
    }

    // 新增會員
    public void addMember(Member member){
        String sql = "INSERT INTO members (member_id, name, phone) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getMemberId());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getPhone());
            pstmt.executeUpdate();
            System.out.println("會員已新增: " + member.getName());
        } catch (SQLException e) {
            System.out.println("新增會員失敗：" + e.getMessage());
        }
    }

    // 列出所有會員
    public void listAllMembers(){
        String sql = "SELECT * FROM members";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("圖書館的所有會員：");
            while(rs.next()){
                Member member = new Member(
                    rs.getString("name"),
                    rs.getString("member_id"),
                    rs.getString("phone")
                );
                System.out.println(member);
            }
        } catch (SQLException e) {
            System.out.println("載入會員失敗：" + e.getMessage());
        }
    }

    // 新增書籍到圖書館
    public void addBook(Book book){
        String sql = "INSERT INTO books (isbn, title, author, isAvailable) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.executeUpdate();
            books.add(book); // 同步新增到內存清單
            System.out.println("書籍已新增到圖書館: " + book.getTitle());
        } catch (SQLException e) {
            System.out.println("新增書籍失敗：" + e.getMessage());
        }
    }

    // 列出圖書館的所有書籍
    public void listAllBooks(){
        if(books.isEmpty()){
            System.out.println("圖書館目前沒有書籍。");
        } else {
            System.out.println("圖書館的所有書籍：");
            // : = in的意思，for迴圈會依序取出books清單中的每一本書，並將其指派給book變數
            for(Book book : books){
                System.out.println(book);   // 會自動呼叫 toString()
            }
        }
    }

    // 借書
    public void borrowBook(String isbn, String memberId){
        for(Book book : books){
            if(book.getIsbn().equals(isbn)){
                if(book.isAvailable()){
                    book.setAvailable(false);
                    updateDatabase(book);
                    String sql = "INSERT INTO borrow_records (isbn, member_id, borrow_date) VALUES (?, ?, datetime('now'))";
                    try (Connection conn = DriverManager.getConnection(DB_URL);
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, isbn);
                        pstmt.setString(2, memberId);
                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("記錄借閱失敗：" + e.getMessage());
                    }
                    System.out.println("你已成功借到: " + book.getTitle());
                    return;
                } else {
                    System.out.println("很抱歉，" + book.getTitle() + " 已經被借出。");
                    return;
                }
            }
        }
        System.out.println("找不到 ISBN 為「" + isbn + "」的書籍。");
    }

    // 查詢某會員的借閱記錄
    public void listBorrowRecords(String memberId) {
        String sql = """
            SELECT b.title, r.borrow_date, r.return_date
            FROM borrow_records r
            JOIN books b ON r.isbn = b.isbn
            WHERE r.member_id = ?
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("=== 會員 " + memberId + " 的借閱記錄 ===");
            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                String returnDate = rs.getString("return_date") != null
                    ? rs.getString("return_date") : "未歸還";
                System.out.println(rs.getString("title")
                    + " | 借出：" + rs.getString("borrow_date")
                    + " | 歸還：" + returnDate);
            }
            if (!hasRecords) System.out.println("沒有借閱記錄。");
        } catch (SQLException e) {
            System.out.println("查詢失敗：" + e.getMessage());
        }
    }

    public void returnBook(String isbn,String memberId){
        for(Book book : books){
            if(book.getIsbn().equals(isbn)){
                if(!book.isAvailable()){
                    book.setAvailable(true);
                    updateDatabase(book);
                    String sql = """
                        UPDATE borrow_records SET return_date = datetime('now')
                        WHERE id = (
                            SELECT id FROM borrow_records
                            WHERE isbn = ? AND member_id = ? AND return_date IS NULL
                            ORDER BY borrow_date ASC LIMIT 1
                        )
                    """;
                    try (Connection conn = DriverManager.getConnection(DB_URL);
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, isbn);
                        pstmt.setString(2, memberId);
                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("更新借閱記錄失敗：" + e.getMessage());
                    }
                    System.out.println("你已成功歸還: " + book.getTitle());
                    return;
                } else {
                    System.out.println(book.getTitle() + " 並未被借出。");
                    return;
                }
            }
        }
        System.out.println("找不到 ISBN 為「" + isbn + "」的書籍。");
    }

    // 刪除書籍
    public void deleteBook(String isbn){
        String sql = "DELETE FROM BOOKS WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);  // 將 isbn 填入 SQL 的第一個 ? 佔位符

            int rows = pstmt.executeUpdate();  // 執行 SQL，回傳受影響的行數

            if (rows > 0) {  // 如果影響行數大於 0，表示有找到並刪除該書
                
                // removeIf 會遍歷 books 清單，把符合條件的書移除
                // book -> book.getIsbn().equals(isbn) 是 Lambda 表達式
                // 意思是：「對於每一本 book，如果它的 isbn 等於我們要刪的 isbn 就移除」
                books.removeIf(book -> book.getIsbn().equals(isbn));
                
                System.out.println("書籍已刪除: " + isbn);
            } else {  // 影響行數為 0，表示資料庫裡找不到這個 isbn
                System.out.println("找不到 ISBN 為「" + isbn + "」的書籍，無法刪除。");
            }
        } catch (SQLException e) {
            System.out.println("刪除書籍失敗：" + e.getMessage());
        }
    }

    // 更新資料庫的借閱狀態
    private  void updateDatabase(Book book){
        String sql = "UPDATE books SET isAvailable = ? WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, book.isAvailable() ? 1 : 0);
            pstmt.setString(2, book.getIsbn());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("更新資料庫失敗：" + e.getMessage());
        }
    }
    // 用書名搜尋書籍
    public Book searchByTitle(String keyword){
        for(Book book : books){
            if(book.getTitle().contains(keyword)){
                return  book; // 找到符合的書籍，回傳該書物件
            }
        }
        System.out.println("找不到含有「" + keyword + "」的書籍。");
        return null;
    }

    public String getName() {
        return name;
    }
}