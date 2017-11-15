package lejos.music;

import java.io.IOException;
import java.net.SocketException;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.network.BroadcastReceiver;

public class Launcher {
	/**
	 * Starts a robot, according to the pressed button, a different track will be played
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		final TrackReader trackReader = new TrackReader();
		
		final Track violin1 = trackReader.read(Launcher.class.getResourceAsStream("/lejos/music/samples/score01/violin1.txt"));
		final Track violin2 = trackReader.read(Launcher.class.getResourceAsStream("/lejos/music/samples/score01/violin2.txt"));
		final Track violoncello = trackReader.read(Launcher.class.getResourceAsStream("/lejos/music/samples/score01/violoncello.txt"));
		final Track contrabass = trackReader.read(Launcher.class.getResourceAsStream("/lejos/music/samples/score01/contrabass.txt"));

		final Track track01 = trackReader.read(Launcher.class.getResourceAsStream("/lejos/music/samples/score02/track01.txt"));
		final Track track02 = trackReader.read(Launcher.class.getResourceAsStream("/lejos/music/samples/score02/track02.txt"));
		final Track track03 = trackReader.read(Launcher.class.getResourceAsStream("/lejos/music/samples/score02/track03.txt"));


		violin1.setBpm(90);
		violin2.setBpm(90);
		violoncello.setBpm(90);
		contrabass.setBpm(90);

		track01.setBpm(90);
		track02.setBpm(90);
		track03.setBpm(90);

		final int button = Button.waitForAnyPress();
		
		if(button == Button.ID_UP) {
			playTrack(track01);
		} else if(button == Button.ID_RIGHT) {
			playTrack(track02);
		} else if(button == Button.ID_LEFT) {
			playTrack(track03);
		}
	}
	
	private static void playTrack(Track track) {
        LCD.drawString("Chef a droite", 0, 2);
        final int button = Button.waitForAnyPress();
        boolean coordinator;
        if(button == Button.ID_RIGHT) {
            coordinator = true;
        } else {
            coordinator = false;
        }

	    if(!coordinator){
            try {
                BroadcastReceiver.getInstance().addListener(track);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        LCD.clear();
		LCD.drawString("Playing...", 0, 2);

		while(!track.isOver()) {
            float track_time =  track.getTime();
			LCD.drawString(String.format("%.4f", track_time), 0, 3);
            track.play(Track.COORDINATOR);
		}
	}
}
