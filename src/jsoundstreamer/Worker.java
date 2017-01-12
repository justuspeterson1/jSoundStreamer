/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsoundstreamer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Set;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;
import org.webbitserver.WebSocketConnection;

public class Worker implements Runnable {

    private final Thread internalThread;
    byte[] tmpBuf = new byte[40960];
    AudioFormat format;
    TargetDataLine audioLine;
    Port in_port;
    Set<WebSocketConnection> peers;
    private oggEncoder encoder;

    AudioInputStream audioInputStream;

    public Worker(Set<WebSocketConnection> peers, TargetDataLine line) {
        this.audioLine = line;
        this.peers = peers;
        this.format = getAudioFormat();
        this.encoder = new oggEncoder();

        internalThread = new Thread(this);
        internalThread.setDaemon(true);
        internalThread.start();
    }

    AudioFormat getAudioFormat() {
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                44100.0F,
                16,
                2,
                2 * 2,
                44100.0F,
                false);
    }
    public byte[] encodePcmToMp3(byte[] pcm, AudioFormat baseFormat) {
        LameEncoder encoder = new LameEncoder(baseFormat, 128, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);
        //sampleSizeInBits = sourceFormat.getSampleSizeInBits()
        ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
        byte[] buffer = new byte[encoder.getPCMBufferSize()];

        int bytesToTransfer = Math.min(buffer.length, pcm.length);
        int bytesWritten;
        int currentPcmPosition = 0;
        while (0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
    //    bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer);
          currentPcmPosition += bytesToTransfer;
          bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);

          mp3.write(buffer, 0, bytesWritten);
       }
        
        encoder.close();
        return mp3.toByteArray();
      }

    @Override
    public void run() {
        int bytesRead = 0;
        audioLine.start();

        while (true) {
            try {
                
                Thread.sleep(10);
                while (!peers.isEmpty()) {
                    
                    bytesRead = audioLine.read(tmpBuf, 0, tmpBuf.length);

                    ByteArrayInputStream bais = new ByteArrayInputStream(tmpBuf);
                    
                    audioInputStream = new AudioInputStream(bais, format, tmpBuf.length / format.getFrameSize());
                    AudioFormat baseFormat = audioInputStream.getFormat();
                    ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, BAOS);
                    
                    //wave to ogg
                    //byte[] ogg = this.encoder.encode(BAOS.toByteArray());
                    //end wave to ogg
                    byte[] pcm = encodePcmToMp3(BAOS.toByteArray(), baseFormat);
                    for (WebSocketConnection peer : peers) {
                        peer.send(pcm);
                        //peer.send(ogg);
                    }
                    BAOS.close();
                    bais.close();
                    //audioLine.stop();
                }
            } catch (Exception e) {
                System.out.println(e.getCause());
            }
        }
    }
}
