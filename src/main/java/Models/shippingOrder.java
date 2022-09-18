package Models;

public class shippingOrder {

    private int manufacturingID;
    private int startDate;
    private int qty;

    public shippingOrder(int manufacturingID, int startDate, int qty) {
        this.manufacturingID = manufacturingID;
        this.startDate = startDate;
        this.qty = qty;
    }

    public int getManufacturingID() {
        return manufacturingID;
    }

    public int getStartDate() {
        return startDate;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
