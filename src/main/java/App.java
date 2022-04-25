import Controllers.MES;
import Viewers.MES_Viewer;

public class App {

    public static void main(String args[]) {

        MES mes = new MES(new MES_Viewer());
        while (true) {
            try{
                System.out.println("MES running");
                Thread.sleep(10000);
                mes.receiveInternalOrders();
                mes.displayInternalOrders();

            }
            catch (Exception e ){
                System.out.println(e);
            }

        }
    }
}
