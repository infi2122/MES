package comsProtocols;


public class ERP_to_MES {

    private static tcpClient client;

    public static tcpClient getClient() {
        return client;
    }

    public static void setClient(tcpClient client) {
        ERP_to_MES.client = client;
    }

    public void openConnection(int port, String ip) {
        setClient(new tcpClient());
        getClient().startConnection(ip, port);
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
