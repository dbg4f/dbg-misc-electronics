package dbg.electronics.robodrv.emulator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EmulatorLauncher {


    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(4444);

        System.out.println("Emulator started");

        do {

            Socket clientSocket = serverSocket.accept();

            System.out.println("clientSocket = " + clientSocket);

            McEmulator emulator = new McEmulator(clientSocket.getInputStream(), clientSocket.getOutputStream());

            try {
                launchAdc(emulator);
                emulator.main();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            System.out.println("End of cycle");

        } while (!Thread.currentThread().isInterrupted());


    }

    static void launchAdc(final McEmulator emulator){

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (!Thread.currentThread().isInterrupted()) {


                    for (int i=0; i<ADC_SAMPLES.length; i++) {
                        try {
                            emulator.sendAdc(new int[] {
                                    ADC_SAMPLES[i][2],
                                    ADC_SAMPLES[i][0],
                                    ADC_SAMPLES[i][2],
                                    ADC_SAMPLES[i][1]
                            });
                            //System.out.println("i = " + i);
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            break;
                        }

                        try {
                            Thread.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            break;
                        }

                    }

/*
                    for (int i=0; i<255; i++) {

                        try {
                            emulator.sendAdc(new int[]{i, i, i, i});
                            System.out.println("i = " + i);
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            break;
                        }

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            break;
                        }

                    }
  */

                }

            }
        }).start();


    }


   private final static int ADC_SAMPLES[][] = {
           {127	,0	,0	,0},
           {127	,71	,0	,0},
           {131	,71	,0	,0},
           {131	,61	,0	,0},
           {132	,60	,0	,0},
           {127	,60	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {126	,61	,0	,0},
           {126	,71	,0	,0},
           {131	,71	,0	,0},
           {131	,61	,0	,0},
           {132	,61	,0	,0},
           {132	,60	,0	,0},
           {128	,60	,0	,0},
           {128	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,71	,0	,0},
           {127	,71	,0	,0},
           {127	,61	,0	,0},
           {127	,60	,0	,0},
           {126	,60	,0	,0},
           {128	,60	,0	,0},
           {128	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {132	,61	,0	,0},
           {132	,71	,0	,0},
           {132	,71	,0	,0},
           {132	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,70	,0	,0},
           {133	,61	,0	,0},
           {128	,61	,0	,0},
           {128	,61	,0	,0},
           {132	,61	,0	,0},
           {132	,71	,0	,0},
           {132	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {128	,61	,0	,0},
           {128	,61	,0	,0},
           {126	,61	,0	,0},
           {126	,60	,0	,0},
           {128	,72	,0	,0},
           {132	,72	,0	,0},
           {132	,60	,0	,0},
           {127	,60	,0	,0},
           {127	,60	,0	,0},
           {131	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,72	,0	,0},
           {126	,72	,0	,0},
           {126	,60	,0	,0},
           {132	,72	,0	,0},
           {132	,72	,0	,0},
           {132	,61	,0	,0},
           {131	,61	,0	,0},
           {131	,61	,0	,0},
           {128	,61	,0	,0},
           {126	,61	,0	,0},
           {126	,72	,0	,0},
           {132	,72	,0	,0},
           {132	,60	,0	,0},
           {132	,60	,0	,0},
           {132	,61	,0	,0},
           {126	,61	,0	,0},
           {126	,72	,0	,0},
           {133	,72	,0	,0},
           {133	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,60	,0	,0},
           {132	,60	,0	,0},
           {132	,60	,0	,0},
           {127	,60	,0	,0},
           {127	,72	,0	,0},
           {126	,72	,0	,0},
           {126	,60	,0	,0},
           {128	,60	,0	,0},
           {128	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,70	,0	,0},
           {127	,70	,0	,0},
           {127	,61	,0	,0},
           {133	,61	,0	,0},
           {133	,72	,0	,0},
           {128	,72	,0	,0},
           {128	,60	,0	,0},
           {132	,60	,0	,0},
           {132	,61	,0	,0},
           {132	,71	,0	,0},
           {131	,71	,0	,0},
           {131	,60	,0	,0},
           {127	,60	,0	,0},
           {127	,72	,0	,0},
           {127	,71	,0	,0},
           {126	,71	,0	,0},
           {126	,61	,0	,0},
           {132	,61	,0	,0},
           {132	,61	,0	,0},
           {127	,60	,0	,0},
           {128	,60	,0	,0},
           {128	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,72	,0	,0},
           {127	,61	,0	,0},
           {131	,61	,0	,0},
           {131	,72	,0	,0},
           {132	,72	,0	,0},
           {132	,60	,0	,0},
           {128	,72	,0	,0},
           {127	,72	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,71	,0	,0},
           {131	,61	,0	,0},
           {132	,61	,0	,0},
           {132	,70	,0	,0},
           {128	,70	,0	,0},
           {128	,61	,0	,0},
           {128	,61	,0	,0},
           {132	,61	,0	,0},
           {132	,71	,0	,0},
           {127	,71	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,60	,0	,0},
           {133	,60	,0	,0},
           {133	,61	,0	,0},
           {127	,61	,0	,0},
           {127	,61	,0	,0},
           {126	,61	,0	,0},
           {126	,71	,0	,0},
           {131	,71	,0	,0},
           {131	,61	,0	,0},
           {132	,61	,0	,0},
           {132	,60	,0	,0},
           {128	,60	,0	,0},
           {128	,61	,0	,0},
           {132	,61	,0	,0},
           {132	,61	,0	,0},
           {126	,61	,0	,0},
           {126	,71	,0	,0},
           {131	,71	,0	,0},
           {131	,60	,0	,0},
           {132	,60	,0	,0},
           {132	,60	,0	,0},
           {128	,60	,0	,0},
           {128	,73	,0	,0},
           {127	,73	,0	,0},
           {127	,62	,0	,0},
           {127	,62	,0	,0},
           {127	,61	,0	,0},
           {131	,61	,0	,0},
           {131	,60	,0	,0},
           {132	,60	,0	,0},
           {132	,70	,0	,0},
           {128	,70	,0	,0},
           {128	,61	,0	,0},
           {128	,61	,0	,0},
           {128	,61	,0	,0},
           {132	,61	,0	,0},
           {132	,71	,0	,0},
           {127	,71	,0	,0},
           {127	,60	,0	,0},
           {127	,60	,0	,0},
           {127	,60	,0	,0},
           {133	,60	,0	,0},
           {133	,61	,0	,0},
           {128	,61	,0	,0},
           {128	,61	,0	,0},
           {131	,61	,0	,0},
           {131	,71	,0	,0},
           {133	,71	,0	,0},
           {133	,61	,0	,0},
           {132	,61	,0	,0},
           {132	,60	,0	,0},
           {127	,60	,0	,0},
           {127	,61	,0	,0},
           {132	,61	,0	,0},
           {132	,61	,0	,0},
           {128	,71	,0	,0},
           {132	,71	,0	,0},
           {132	,60	,0	,0},
           {128	,60	,0	,0},
           {128	,61	,0	,0},
           {133	,61	,0	,0},
           {133	,73	,0	,0},
           {127	,73	,0	,0},
           {127	,62	,0	,0},
           {128	,62	,0	,0},
           {128	,61	,0	,0},
           {127	,61	,0	,0},

    };

}
