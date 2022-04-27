package UDP;


public class ERPtunnel {

    private static tcpClient client;
    private static int port = 20000;

    public static tcpClient getClient() {
        return client;
    }

    public static void setClient(tcpClient client) {
        ERPtunnel.client = client;
    }

    public void openConnection() {
        setClient(new tcpClient());
        getClient().startConnection("127.0.0.1", port);
    }

    public void stopConnection() {

        getClient().stopConnection();
    }

    public long initTimer() {
        String str = getClient().sendMessage("time");
        if (str != null)
            return Long.parseLong(str);
        else
            return -1;
    }


    public String receivingOrdersFromERP() {

        String response = new String();
        try {
            response = getClient().sendMessage("internalOrders");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return response;

    }
}
