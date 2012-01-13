package j3d.samples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bogdel
 * Date: 12.01.12
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
public class FileTracker implements Runnable {

    private final String name;

    private final File file;

    private List<String> lines = new ArrayList<String>();

    public FileTracker(String name) {
        this.name = name;

        file = new File(name);


    }

    private void read() throws IOException {

        //file.ge

        lines.clear();

        BufferedReader reader = new BufferedReader(new FileReader(name));
        String line;
        while((line = reader.readLine()) != null) {
            lines.add(line);
        }


    }

    public void launch() {
        new Thread(this).start();
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {

            
            

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }
}
