package comsProtocols;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientTCP {

    private Socket socket;

    public void startConnection(String IP, int port, sharedResources sharedBuffer, int initialDelay, int period) {
        try {
            socket = new Socket(IP, port);
            System.out.println("Sucessfully connected to ERP");

            ScheduledExecutorService scheduler
                    = Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate(new serverHandler(socket, sharedBuffer), initialDelay, period, TimeUnit.SECONDS);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class serverHandler extends Thread {

        private Socket socket;
        private InputStreamReader isr;
        private BufferedReader br;
        private OutputStreamWriter osw;
        private BufferedWriter bw;
        private sharedResources sharedBuffer;
        private boolean odd = true;

        public serverHandler(Socket socket, sharedResources sharedBuffer) {
            this.socket = socket;
            this.sharedBuffer = sharedBuffer;
        }

        public void run() {
            sendRequest("startTime");
            sendRequest("internalOrders");
            acceptRequest("finishedOrdersTimes");

//            if (odd) {
//                sendRequest("startTime");
//                sendRequest("internalOrders");
//                //odd = false;
//            } else {
//                acceptRequest("finishedOrdersTimes");
//                odd = true;
//            }
        }

        public void sendRequest(String feature) {
            try {

                osw = new OutputStreamWriter(socket.getOutputStream());
                bw = new BufferedWriter(osw);
                bw.write(feature + "\n");
                bw.flush();

                isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);

                switch (feature) {
                    case "startTime":
                        sharedBuffer.setStartTime(Long.parseLong(br.readLine()));
                        break;
                    case "internalOrders":
                        sharedBuffer.setInternalOrdersConcat(br.readLine());
                        break;
                    default:
                        break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void acceptRequest(String feature) {
            try {
                isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);
                String toSend;
                if (br.readLine().equals(feature)) {

                    switch (feature) {
                        case "finishedOrdersTimes":
                            toSend = sharedBuffer.getFinishedOrdersTimes();
                            break;
                        default:
                            toSend = "empty";
                            break;
                    }
                    osw = new OutputStreamWriter(socket.getOutputStream());
                    bw = new BufferedWriter(osw);
                    bw.write(toSend + "\n");
                    bw.flush();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
