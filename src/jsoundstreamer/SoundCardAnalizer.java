/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsoundstreamer;

import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class SoundCardAnalizer {

    ArrayList<TargetDataLine> audioLines;
    AudioFormat format;

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
    
    public SoundCardAnalizer() {
        this.format = getAudioFormat();
        this.audioLines = new ArrayList<TargetDataLine>();
    }

    public ArrayList<TargetDataLine> getAvailableMixers() {
        final DataLine.Info targetDataLineInfo = new DataLine.Info(TargetDataLine.class, format);

        for (int i = 0; i < AudioSystem.getMixerInfo().length; i++) {
            try {
                Mixer targetMixer = AudioSystem.getMixer(AudioSystem.getMixerInfo()[i]);
                TargetDataLine audioLine = (TargetDataLine) targetMixer.getLine(targetDataLineInfo);
                audioLine.open(format);
                audioLines.add(audioLine);
            } catch (LineUnavailableException lUav) {
                System.err.println(i);
                continue;
            } catch (IllegalArgumentException iae) {
                System.err.println(i);
                continue;
            }
        }
        return audioLines;
    }
}
