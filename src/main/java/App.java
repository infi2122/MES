import Controllers.MES;
import Viewers.MES_Viewer;

public class App {

    public static void main(String args[]) {

        MES mes = new MES(new MES_Viewer());
        while (true) {
            try{
                Thread.sleep(60000);
                mes.receiveInternalOrders();
                mes.displayInternalOrders();

            }
            catch (Exception e ){
                System.out.println(e);
            }

        }
    }
}
