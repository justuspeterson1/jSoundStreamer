/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsoundstreamer;

public class JSoundStreamer {
    
    static Streamer streamer;;

    public static void main(String[] args) {
        streamer = new Streamer();
        streamer.start();
    }

}
