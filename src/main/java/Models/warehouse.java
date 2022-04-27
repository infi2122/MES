package Models;

import java.util.ArrayList;

public class warehouse {

    private ArrayList<piece> pieces = new ArrayList<>();

    public ArrayList<piece> getPieces() {
        return pieces;
    }

    public void addNewPiece(piece newPiece) {
        getPieces().add(newPiece);
    }


    public int[] typeCounter() {

        int[] cnt = new int[10];

        for (piece curr : getPieces()) {
            int i = 1;

            while (curr.getExpectedType() != i && i < cnt.length) {
                i++;
            }

            cnt[i]++;

        }
        return cnt;
    }
}
