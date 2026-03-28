package com.library;
public class Member {
    // 屬性（成員的資料）
    // 使用 final 關鍵字表示這些屬性在創建後不能改變
    private final String name;     // 姓名
    private final String memberId;   // 會員編號
    private final String phone;     // 電話


    // 建構子（用來創建成員的物件）
    public Member(String name, String memberId, String phone) {
        this.name = name;
        this.memberId = memberId;
        this.phone = phone;
    }

    public String getName() {return name;}
    public String getMemberId() {return memberId;}
    public String getPhone() {return phone;}

    // 印出成員的資訊
    @Override
    public String toString(){
        return "會員姓名: " + name + ", 會員編號: " + memberId + ", 電話: " + phone;
    }
}