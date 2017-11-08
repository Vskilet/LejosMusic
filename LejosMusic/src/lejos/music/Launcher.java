package lejos.music;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.network.BroadcastManager;

public class Launcher {
	/**
	 * Starts a robot, according to the pressed button, a different track will be played
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

        BroadcastManager test = BroadcastManager.getInstance();
        test.broadcast("coucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucoucou".getBytes());

		final TrackReader trackReader = new TrackReader();
		
		final Track violin1 = trackReader.read(Launcher.class.getResourceAsStream("/lejos/music/samples/score01/violin1.txt"));
		final Track violin2 = trackReader.read(Launcher.class.getResourceAsStream("/lejos/music/samples/score01/violin2.txt"));
		final Track violoncello = trackReader.read(Launcher.class.getResourceAsStream("/lejos/music/samples/score01/violoncello.txt"));
		final Track contrabass = trackReader.read(Launcher.class.getResourceAsStream("/lejos/music/samples/score01/contrabass.txt"));
	
		violin1.setBpm(90);
		violin2.setBpm(90);
		violoncello.setBpm(90);
		contrabass.setBpm(90);
		
		final int button = Button.waitForAnyPress();
		
		if(button == Button.ID_UP) {
			playTrack(violin1);
		} else if(button == Button.ID_RIGHT) {
			playTrack(violin2);
		} else if(button == Button.ID_LEFT) {
			playTrack(violoncello);
		} else if(button == Button.ID_DOWN) {
			playTrack(contrabass);
		}
	}
	
	private static void playTrack(Track track) {	
		LCD.clear();
		LCD.drawString("Playing...", 0, 2);

        BroadcastManager broadcast_manager = null;
        try {
            broadcast_manager = BroadcastManager.getInstance();
        } catch (SocketException e) {
            e.printStackTrace();
        }

		while(!track.isOver()) {
            float track_time =  track.getTime();

			LCD.drawString(String.format("%.4f", track_time), 0, 3);
            
            byte[] message = ByteBuffer.allocate(4).putFloat(track_time).array();

            try {
                broadcast_manager.broadcast(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

            track.play();
		}
	}
}
