import Controllers.MES;
import Controllers.ZoneE;
import Controllers.zoneA;
import Controllers.zoneC;
import OPC_UA.opcConnection;
import Viewers.MES_Viewer;
import comsProtocols.ClientTCP;
import comsProtocols.sharedResources;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    private static String ERP_IP = "127.0.0.1";
    private static int ERP_to_MES_port = 20000;


    public static void main(String args[]) {

        sharedResources sharedBuffer = new sharedResources();

        MES mes = new MES(new MES_Viewer(), sharedBuffer);

//        mes.testMES_zonaA();
//        mes.testMES_zonaC();
//        mes.testMES_zonaE();
        ClientTCP client = new ClientTCP();
        client.startConnection(ERP_IP, ERP_to_MES_port, sharedBuffer);

//        tcpServer MESserver = new tcpServer();
//        MESserver.start(MES_to_ERP_port,mes.getPieceHistories_str());
//
//        ERP_to_MES erp2mes = new ERP_to_MES();
//        erp2mes.openConnection(ERP_to_MES_port,ERP_IP);
//        mes.setErp_to_Mes(erp2mes);
//
//
        /* For synchronize time in ERP and MES */
        mes.setStartTime(true);
        /* *************************************** */

//        opcConnection opcConnection = new opcConnection();
//        opcConnection.createOPCconnection();
//        mes.setOpcClient(opcConnection);

        ScheduledExecutorService schedulerERP = Executors.newScheduledThreadPool(5);
        schedulerERP.scheduleAtFixedRate(new myMES(mes), 0, 60, TimeUnit.SECONDS);
        schedulerERP.scheduleAtFixedRate(new myTimer(mes), 0, 1, TimeUnit.SECONDS);
        schedulerERP.scheduleAtFixedRate(new zoneA(mes), 0, 200, TimeUnit.MILLISECONDS);
        schedulerERP.scheduleAtFixedRate(new zoneC(mes), 0, 100, TimeUnit.MILLISECONDS);
        schedulerERP.scheduleAtFixedRate(new ZoneE(mes), 0, 200, TimeUnit.MILLISECONDS);

    }

    private static class myMES extends Thread {

        private final MES mes;

        public myMES(MES mes1) {
            this.mes = mes1;
        }

        @Override
        public void run() {
            synchronized (mes) {
                mes.receiveInternalOrders();
                mes.displayInternalOrders();
                mes.displayEntryWH();
                mes.displayExitWH();
                //mes.orderTimes();
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
