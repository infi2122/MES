import Controllers.MES;
import Viewers.MES_Viewer;

public class App {

    public static void main(String args[]) {

        MES mes = new MES(new MES_Viewer());
        while (true) {
            try{

                mes.receiveInternalOrders();
                mes.displayInternalOrders();
                Thread.sleep(60000);
            }
            catch (Exception e ){
                System.out.println(e);
            }

        }
    }
}
