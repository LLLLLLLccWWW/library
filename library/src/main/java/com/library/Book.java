package com.library;
public class Book {
    // 屬性（書的資料）
    // 使用 final 關鍵字表示這些屬性在創建後不能改變
    private final String title;    // 書名
    private final String author;   // 作者
    private final String isbn;     // ISBN 編號
    private boolean isAvailable; // 是否可借

    // 建構子（用來創建書的物件）
    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = true; // 預設書是可借的
    }

    public String getTitle() {return  title;}
    public String getAuthor() {return author;}
    public String getIsbn() {return isbn;}
    public boolean isAvailable() {return isAvailable;}

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    // 印出書的資訊
    @Override
    public String toString(){
        String status = isAvailable ? "可借" : "已借出";
        // 字串串接，顯示書的 ISBN、書名、作者和狀態
        return "[" + isbn +"] " + title + " - " + author + " (" + status + ")";

    }
}