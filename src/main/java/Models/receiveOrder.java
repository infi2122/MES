package Models;

public class receiveOrder {

    private int rawMaterialOrderID;
    private int pieceType;
    private int arrivalDate;
    private int qty;

    public receiveOrder(int rawMaterialOrderID, int pieceType, int arrivalDate, int qty) {
        this.rawMaterialOrderID = rawMaterialOrderID;
        this.pieceType = pieceType;
        this.arrivalDate = arrivalDate;
        this.qty = qty;
    }

    public int getRawMaterialOrderID() {
        return rawMaterialOrderID;
    }

    public int getPieceType() {
        return pieceType;
    }

    public int getArrivalDate() {
        return arrivalDate;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
