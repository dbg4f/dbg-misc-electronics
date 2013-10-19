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
                launchAdc(emulator);
                emulator.main();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (!Thread.currentThread().isInterrupted());


    }

    static void launchAdc(final McEmulator emulator){

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (!Thread.currentThread().isInterrupted()) {

                    for (int i=0; i<255; i++) {

                        try {
                            emulator.sendAdc(new int[]{i, i, i, i});
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            return;
                        }

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                    }


                }

            }
        }).start();


    }


}
