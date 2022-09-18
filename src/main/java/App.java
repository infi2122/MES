import Controllers.MES;
import Controllers.ZoneE;
import Controllers.zoneA;
import Controllers.zoneC;
import OPC_UA.opcConnection;
import Viewers.MES_Viewer;
import comsProtocols.ClientTCP;
import comsProtocols.sharedResources;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    private static final String ERP_IP = "127.0.0.1";
    private static final int ERP_to_MES_port = 20000;


    public static void main(String[] args) {

        sharedResources sharedBuffer = new sharedResources();
        sharedBuffer.setInternalOrdersConcat("empty");
        sharedBuffer.setFinishedOrdersTimes("empty");

        MES mes = new MES(new MES_Viewer(), sharedBuffer);

        ClientTCP client = new ClientTCP();
        client.startConnection(ERP_IP, ERP_to_MES_port, sharedBuffer, 0, 59);

        /* For synchronize time in ERP and MES */
        mes.setStartTime(true);
        /* *************************************** */

        opcConnection opcConnection = new opcConnection();
        opcConnection.createOPCconnection();
        mes.setOpcClient(opcConnection);

        ScheduledExecutorService schedulerERP = Executors.newScheduledThreadPool(5);
        schedulerERP.scheduleAtFixedRate(new myMES(mes), 0, 60, TimeUnit.SECONDS);
        schedulerERP.scheduleAtFixedRate(new myTimer(mes), 0, 1, TimeUnit.SECONDS);
        schedulerERP.scheduleAtFixedRate(new zoneA(mes), 0, 200, TimeUnit.MILLISECONDS);
        schedulerERP.scheduleAtFixedRate(new zoneC(mes), 20, 200, TimeUnit.MILLISECONDS);
        schedulerERP.scheduleAtFixedRate(new ZoneE(mes), 100, 200, TimeUnit.MILLISECONDS);

        Scanner input = new Scanner(System.in);
        // CONTEXT MENU
        boolean exit = true;
        while (exit) {
            //erp.cleanTerminal();
            mes.displayMenu();
            switch (input.nextLine()) {
                case "1" -> mes.displayInternalOrders();
                case "2" -> mes.displayEntryWH();
                case "3" -> mes.displayExitWH();
                case "4" -> mes.displayPieceHistory();
                // Display de estatísticas
                case "5" -> mes.displayCounters();
                case "6" -> mes.displayMachinesTimmings();
                case "7" -> mes.displayPusherCounters();
                case "0" -> exit = false;
                default -> {
                    continue;
                }
            }
            if (exit)
                mes.displayCurrentDay();
        }
        System.exit(0);
    }

    private static class myMES extends Thread {

        private final MES mes;

        public myMES(MES mes1) {
            this.mes = mes1;
        }

        @Override
        public void run() {
            try{
                synchronized (mes) {
                    mes.receiveInternalOrders();
                    mes.orderTimes();
                }
            }
            catch( Throwable t ){
                System.out.println("My MES" + t.getMessage() );
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
            try{
                synchronized (mes) {
                    mes.countTime();
                }
            }
            catch( Throwable t ){
                System.out.println("Timer" + t.getMessage() );
            }

        }
    }


}
