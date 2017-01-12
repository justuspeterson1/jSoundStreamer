/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsoundstreamer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author incode3
 */
public class Streamer extends Thread {
    private SoundCardAnalizer analizer;
    private ArrayList<TargetDataLine> audioLines;
    private int initialPort = 7777;
    //private oggEncoder encoder;
    
    public Streamer() {
        this.analizer = new SoundCardAnalizer();
        //this.encoder = new oggEncoder();
    }
    
    @Override
    public void run()
    {
        audioLines = analizer.getAvailableMixers();
        
        for (TargetDataLine line : audioLines) {
            WebSocketHandler handler = new WebSocketHandler();
            handler.connect(initialPort++);

            Worker worker = new Worker(handler.getPeers(), line);
        }
        
//        try {
//            byte[] array = Files.readAllBytes(new File("./africa-toto.wav").toPath());
//            System.out.println(array.length);
//            byte[] arr1 = this.encoder.encode(array);
//            System.out.println(arr1.length);
//            FileOutputStream fos = new FileOutputStream("./africa-toto.ogg");
//            fos.write(arr1);
//            fos.close();
//        } catch (IOException ex) {
//            Logger.getLogger(Streamer.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}