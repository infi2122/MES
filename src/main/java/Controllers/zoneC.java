package Controllers;
import Models.productionOrder;
import Models.piece;

public class zoneC extends Thread{
    private MES mes;

    public zoneC(MES mes1) {
        this.mes = mes1;
    }

    @Override
    public void run() {

        synchronized (mes) {


        }
    }
}
