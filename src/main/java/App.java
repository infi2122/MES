import Controllers.MES;
import OPC_UA.opcConnection;
import UDP.ERPtunnel;
import Viewers.MES_Viewer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String args[]) {

        MES mes = new MES(new MES_Viewer());

        ERPtunnel ERP2tcp = new ERPtunnel();
        ERP2tcp.openConnection();

        opcConnection opcConnection = new opcConnection();

        /* For synchronize time in ERP and MES */
        mes.setErp2MesTunnel(ERP2tcp);
        mes.setStartTime();
        /* *************************************** */

        opcConnection.createOPCconnection();
        mes.setOpcClient(opcConnection);

        ScheduledExecutorService schedulerERP = Executors.newScheduledThreadPool(2);
        schedulerERP.scheduleAtFixedRate(new myMES(mes),0,2, TimeUnit.SECONDS);
        schedulerERP.scheduleAtFixedRate(new myTimer(mes),0,1,TimeUnit.SECONDS);

        return;

    }

    private static class myMES extends Thread {

        private final MES mes;

        public myMES(MES mes1) {
            this.mes = mes1;

        }

        @Override
        public void run() {

            synchronized (mes) {
                //mes.receiveInternalOrders();
                //mes.displayInternalOrders();
                mes.testeReadMCT();
                mes.testeWriteMCT();
            }
        }

    }

    private static class myTimer extends Thread {

        private final MES mes;

        public myTimer(MES mes1) {
            this.mes = mes1;
        }

        @Override
        public void run() {
            synchronized (mes) {
                mes.countTime();
            }
        }
    }
}
