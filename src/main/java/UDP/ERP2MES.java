package UDP;


public class ERP2MES {

    private GreetClient client;
    private static int port = 20000;

    public String receivingFromERP()  {

        String response = new String();
        try {
            client = new GreetClient();
            client.startConnection("127.0.0.1", port);

            response = client.sendMessage("internalOrders");
            client.sendMessage(".");
            client.stopConnection();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return response;

    }
}
