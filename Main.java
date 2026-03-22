import java.util.Scanner;
import java.io.PrintStream;

public class Main{
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        // 創建圖書館物件
        Library library = new Library("我的圖書館");
        Scanner scanner = new Scanner(System.in, "UTF-8");

        library.addBook(new Book("Java入門", "王小明", "001"));
        library.addBook(new Book("Python基礎", "李小華", "002"));
        library.addBook(new Book("C++進階", "張大偉", "003"));

        System.out.println("\n搜尋結果 :");
        Book foundBook = library.searchByTitle("Python");
        if(foundBook != null){
            System.out.println("找到書籍: " + foundBook);
        }

        // System.out.println("\n--- 借書測試 ---");
        // library.borrowBook("002");
        // library.borrowBook("002");
        // library.listAllBooks();

        // System.out.println("\n--- 歸還測試 ---");
        // library.returnBook("002");
        // library.listAllBooks();

        int choice = -1;

        while(choice != 0){
            System.out.println("\n=== 圖書館系統 ===");
            System.out.println("1. 列出所有書籍");
            System.out.println("2. 搜尋書籍");
            System.out.println("3. 借書");
            System.out.println("4. 還書");
            System.out.println("5. 新增書籍");
            System.out.println("0. 離開");
            System.out.print("請輸入選項：");

            choice = scanner.nextInt();
            scanner.nextLine(); // 清除換行符

            switch (choice) {
                case 1:
                    library.listAllBooks();                  
                    break;

                case 2:
                    System.out.print("請輸入書名關鍵字：");
                    String keyword = scanner.nextLine();
                    Book found = library.searchByTitle(keyword);
                    if(found != null){
                        System.out.println("找到書籍: " + found);
                    } else {
                        System.out.println("未找到書籍。");
                    }
                    break;
                case 3:
                    System.out.print("請輸入要借的書籍 ISBN：");
                    String borrowIsbn = scanner.nextLine();
                    library.borrowBook(borrowIsbn);
                    break;

                case 4:
                    System.out.print("請輸入要還的書籍 ISBN：");
                    String returnIsbn = scanner.nextLine();
                    library.returnBook(returnIsbn);
                    break;

                case 5:
                    System.out.print("請輸入書名：");
                    String title = scanner.nextLine();
                    System.out.print("請輸入作者：");
                    String author = scanner.nextLine();
                    System.out.print("請輸入 ISBN：");
                    String isbn = scanner.nextLine();
                    library.addBook(new Book(title, author, isbn));
                    break;

                case 0:
                    System.out.println("感謝使用圖書館系統！");
                    break;
                    
                default:
                    System.out.println("無效的選項，請重新輸入。");
            }      
        }
        scanner.close();
    }
}