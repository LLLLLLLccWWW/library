public class Main{
    public static void main(String[] args) {
        // 創建圖書館物件
        Library library = new Library("我的圖書館");

        library.addBook(new Book("Java入門", "王小明", "001"));
        library.addBook(new Book("Python基礎", "李小華", "002"));
        library.addBook(new Book("C++進階", "張大偉", "003"));

        library.listAllBooks();

        System.out.println("\n搜尋結果 :");
        Book foundBook = library.searchByTitle("Python");
        if(foundBook != null){
            System.out.println("找到書籍: " + foundBook);
        }
    }
}