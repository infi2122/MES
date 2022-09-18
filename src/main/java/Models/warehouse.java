package Models;

import java.util.ArrayList;

public class warehouse {

    private final int MAXIMUM_CAPACITY = 24;

    private ArrayList<piece> pieces = new ArrayList<>();

    public ArrayList<piece> getPieces() {
        return pieces;
    }

    public void addNewPiece(piece newPiece) {
        getPieces().add(newPiece);
    }

    public int getMAXIMUM_CAPACITY() {
        return MAXIMUM_CAPACITY;
    }

    public boolean is_WH_full(){
        return getPieces().size()==MAXIMUM_CAPACITY;
    }

}
