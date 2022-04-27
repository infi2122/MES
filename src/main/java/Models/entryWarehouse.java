package Models;

public class entryWarehouse extends warehouse {


    public int typeCounter(int type) {

        int[] cnt = typeCounter();
        int type1 = 0, type2 = 0;
        int i = 1;

        while (i < cnt.length) {

            if (i == 8 || i == 6) {
                type2 = cnt[i];
            } else {
                type1 = cnt[i];
            }
            i++;
        }

        if (type == 1) {
            return type1;
        } else {
            return type2;
        }

    }
}