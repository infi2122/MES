package Models;

import java.util.ArrayList;

public class productionOrder {

    private int manufacturingID;
    private int finalType;
    private int startDate;
    private int qty;
    private ArrayList<rawMaterial> rawMaterials;

    private int pieces_in_sfs;
    private int pieces_done;


    public productionOrder(int manufactID, int final_type, int start_time, int qty) {
        this.manufacturingID = manufactID;
        this.qty = qty;
        this.finalType = final_type;
        this.startDate = start_time;
    }

    public int getManufacturingID() {
        return manufacturingID;
    }
    public int getQty() {
        return qty;
    }
    public int getFinalType() {
        return finalType;
    }
    public ArrayList<rawMaterial> getRawMaterials() {
        return rawMaterials;
    }

    public void setRawMaterials(ArrayList<rawMaterial> rawMaterials) {
        this.rawMaterials = new ArrayList<>(rawMaterials);
    }

    public int getStartDate() {
        return startDate;
    }

    public int getPieces_in_sfs() {
        return pieces_in_sfs;
    }

    public void setPieces_in_sfs(int pieces_in_sfs) {
        this.pieces_in_sfs = pieces_in_sfs;
    }

    public int getPieces_done() {
        return pieces_done;
    }

    public void setPieces_done(int pieces_done) {
        this.pieces_done = pieces_done;
    }

    public boolean is_Done(){
        for (rawMaterial currRM : getRawMaterials()) {
            if( currRM.getQty_used() > 0) return false;
        }
        return true;
    }

}
