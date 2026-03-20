import java.util.ArrayList;
import java.util.List;

public class Library{
    private final String name;
    private final List<Book> books; // 書籍清單

    // 建構子
    public Library(String name) {
        this.name = name;
        this.books = new ArrayList<>(); // 初始化書籍清單
    }

    // 新增書籍到圖書館
    public void addBook(Book book){
        books.add(book);
        System.out.println("書籍已新增到圖書館: " + book.getTitle());
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