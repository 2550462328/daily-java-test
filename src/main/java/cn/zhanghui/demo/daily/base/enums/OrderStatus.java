package cn.zhanghui.demo.daily.base.enums;

public enum OrderStatus {
    CREATED(1, "已创建"),
    PAYED(2, "已支付"),
    FINISHED(3, "已完成");

    private int code;
    private String message;

    OrderStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
