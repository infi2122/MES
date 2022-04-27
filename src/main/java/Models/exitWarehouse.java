package Models;

public class exitWarehouse extends warehouse{

    public int typeCounter(int type) {

        int[] cnt = typeCounter();

        return cnt[type];

    }
}
