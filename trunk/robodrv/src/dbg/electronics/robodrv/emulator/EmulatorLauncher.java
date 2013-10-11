package dbg.electronics.robodrv.emulator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EmulatorLauncher {


    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(4444);

        do {

            Socket clientSocket = serverSocket.accept();

            System.out.println("clientSocket = " + clientSocket);

            McEmulator emulator = new McEmulator(clientSocket.getInputStream(), clientSocket.getOutputStream());

            try {
                emulator.main();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (!Thread.currentThread().isInterrupted());


    }


}
