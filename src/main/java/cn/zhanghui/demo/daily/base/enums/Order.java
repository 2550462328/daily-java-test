package cn.zhanghui.demo.daily.base.enums;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Order
 * @description 在枚举里面实现自定义方法，以及EnumSet和EnumMap的使用
 * @date 2020/4/20
 */
public class Order {

    private EnumSet<OrderStatus> unFinishedStatus = EnumSet.of(OrderStatus.Created, OrderStatus.Payed);

    private OrderStatus orderStatus;

    public enum OrderStatus {

        Created(0) {
            @Override
            public boolean isCreated() {
                return true;
            }
        },
        Payed(1) {
            @Override
            public boolean isPayed() {
                return true;
            }
        },
        Finished(2) {
            @Override
            public boolean isFinished() {
                return true;
            }
        };

        private int orderProcess;

        OrderStatus(int orderProcess) {
            this.orderProcess = orderProcess;
        }

        public boolean isCreated() {
            return false;
        }

        public boolean isPayed() {
            return false;
        }

        public boolean isFinished() {
            return false;
        }

        public int getOrderProcess() {
            return orderProcess;
        }

        public void setOrderProcess(int orderProcess) {
            this.orderProcess = orderProcess;
        }
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isOk() {
        return this.orderStatus.isFinished();
    }

    /**
     * 获取列表中订单状态不重复的元素个数
     *
     * @param orderList
     * @return int
     */
    public int getUnFinishedOrderSize(List<Order> orderList) {
        return orderList.stream().filter(order -> unFinishedStatus.contains(order.orderStatus)).collect(Collectors.toList()).size();
    }

    /**
     * 将列表中的元素根据订单状态进行分类
     *
     * @param orderList
     * @return java.util.EnumMap<cn.zhanghui.demo.daily.base.enums.Order.OrderStatus, java.util.List < cn.zhanghui.demo.daily.base.enums.Order>>
     */
    public EnumMap<OrderStatus, List<Order>> seperateOrderByStatus(List<Order> orderList) {
//        EnumMap<OrderStatus,List<Order>> enumMap = new EnumMap<OrderStatus, List<Order>>(OrderStatus.class);
//        for(Order order: orderList){
//            if(enumMap.containsKey(order.getOrderStatus())){
//                enumMap.get(order.getOrderStatus()).add(order);
//            }else{
//                List<Order> newOrders = new ArrayList<>();
//                newOrders.add(order);
//                enumMap.put(order.getOrderStatus(),newOrders);
//            }
//        }
//        return enumMap;
        //上述简化成下面
        EnumMap<OrderStatus, List<Order>> enumMap = orderList.stream().collect(Collectors.groupingBy(Order::getOrderStatus, () -> new EnumMap<>(OrderStatus.class), Collectors.toList()));
        return enumMap;
    }


    public static void main(String[] args) {
        List<Order> orderList = new ArrayList<>();
        Order order1 = new Order();
        order1.setOrderStatus(OrderStatus.Created);
        Order order2 = new Order();
        order2.setOrderStatus(OrderStatus.Finished);
        Order order3 = new Order();
        order3.setOrderStatus(OrderStatus.Payed);

        orderList.add(order1);
        orderList.add(order2);
        orderList.add(order3);

        // EnumSet测试
        System.out.println(new Order().getUnFinishedOrderSize(orderList));

        // EnumMap测试
        System.out.println(new Order().seperateOrderByStatus(orderList).size());

        System.out.println(new Order().unFinishedStatus.size());
    }
}
