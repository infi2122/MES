package Models;

import java.util.ArrayList;

public class piecesHistory {

    private int manufacturingID;
    private int qty;
    private ArrayList<piece> pieces;
    private int meanProductionTime;
    private int meanTimeInSFS;

    public piecesHistory(int manufacturingID, int qty) {
        this.manufacturingID = manufacturingID;
        this.qty = qty;
        this.pieces = new ArrayList<>();
        this.meanProductionTime = 0;
        this.meanTimeInSFS = 0;
    }

    public int getManufacturingID() {
        return manufacturingID;
    }

    public void setManufacturingID(int manufacturingID) {
        this.manufacturingID = manufacturingID;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public ArrayList<piece> getPieces() {
        return pieces;
    }

    public void addNwePieces(piece newPiece) {
        getPieces().add(newPiece);
    }

    public int getMeanProductionTime() {
        return meanProductionTime;
    }

    public void setMeanProductionTime(int meanProductionTime) {
        this.meanProductionTime = meanProductionTime;
    }

    public int getMeanTimeInSFS() {
        return meanTimeInSFS;
    }

    public void setMeanTimeInSFS(int meanTimeInSFS) {
        this.meanTimeInSFS = meanTimeInSFS;
    }
}
