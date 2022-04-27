import Controllers.MES;
import Viewers.MES_Viewer;

public class App {

    public static void main(String args[]) {
        System.out.println("MES running");

        MES mes = new MES(new MES_Viewer());
        while (true) {
            try{

                Thread.sleep(100);
                mes.receiveInternalOrders();
                mes.displayInternalOrders();

            }
            catch (Exception e ){
                System.out.println(e);
            }

        }
    }
}
