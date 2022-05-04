package Models;

public class piece {

    private int pieceID;
    private int rawMaterialID;
    private int orderID;
    private int expectedType;
    private int WHarrival;
    private int productionStart;
    private int productionEnd;
    private int shippingStart;
    private int shippingEnd;

    public piece(int pieceID, int rawMaterialID, int WHarrival) {
        this.pieceID = pieceID;
        this.rawMaterialID = rawMaterialID;
        this.WHarrival = WHarrival;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getExpectedType() {
        return expectedType;
    }

    public void setExpectedType(int expectedType) {
        this.expectedType = expectedType;
    }

    public int getWHarrival() {
        return WHarrival;
    }

    public void setWHarrival(int WHarrival) {
        this.WHarrival = WHarrival;
    }

    public int getProductionStart() {
        return productionStart;
    }

    public void setProductionStart(int productionStart) {
        this.productionStart = productionStart;
    }

    public int getProductionEnd() {
        return productionEnd;
    }

    public void setProductionEnd(int productionEnd) {
        this.productionEnd = productionEnd;
    }

    public int getShippingStart() {
        return shippingStart;
    }

    public void setShippingStart(int shippingStart) {
        this.shippingStart = shippingStart;
    }

    public int getShippingEnd() {
        return shippingEnd;
    }

    public void setShippingEnd(int shippingEnd) {
        this.shippingEnd = shippingEnd;
    }


}
