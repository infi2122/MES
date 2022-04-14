package Models;

public class internalOrders {

    private int orderID;
    private int startDate;

    public int getOrderID() {
        return orderID;
    }
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getStartDate() {
        return startDate;
    }
    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public internalOrders(int orderID, int startDate) {
        this.orderID = orderID;
        this.startDate = startDate;
    }
}
