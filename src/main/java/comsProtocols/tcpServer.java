package comsProtocols;

import Models.piecesHistory;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class tcpServer {

    /* Thread timing */
    private int initialDelay = 0;
    private int periodicDelay = 55;

    private ServerSocket serverSocket;

    public void start(int port, String piecesHistories ) {
        try {

            serverSocket = new ServerSocket(port);

            ScheduledExecutorService scheduler
                    = Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate(new EchoClientHandler(serverSocket, piecesHistories), initialDelay, periodicDelay, TimeUnit.SECONDS);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class EchoClientHandler extends Thread {
        private ServerSocket serverSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String piecesHistories;

        public EchoClientHandler(ServerSocket socket, String piecesHistories) {
            this.serverSocket = socket;
            this.piecesHistories = piecesHistories;
        }

        public void run() {
            try {
                Socket clientSocket = serverSocket.accept();

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {

                    // Envia as stats das encomendas finalizadas
                    if (inputLine.equals("orderStats")) {
                        System.out.println("tcpServer/ orderStats: "+piecesHistories);
                        out.println(piecesHistories);
                    }

                }

                in.close();
                out.close();
                clientSocket.close();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

//    public static void main(String[] args) {
//        tcpServer server = new tcpServer();
//        server.start(5555, new ArrayList<>());
//    }

}

