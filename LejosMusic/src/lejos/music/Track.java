package lejos.music;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lejos.hardware.Sound;
import lejos.network.BroadcastListener;

public class Track implements BroadcastListener {
	private List<Note> notes = new ArrayList<>();
	
	private int bpm = 60;
	
	private int position = 0;
	
	private float transpose = 1f;
	
	private float lastLen = 0;
	private float lastFreq = 0;
	private long lastStartTime = 0;
	private float time = 0;
	
	private Note partialNote = null;
	
	/**
	 * Gets the notes of this track
	 * @return the notes
	 */
	public List<Note> getNotes() {
		return this.notes;
	}
	
	/**
	 * Gets the BPM (default is 60)
	 * @return the BPM
	 */
	public int getBpm() {
		return bpm;
	}

	/**
	 * Sets the BPM
	 * @param bpm the BPM
	 */
	public void setBpm(int bpm) {
		this.bpm = bpm;
	}
	
	/**
	 * Gets the transpose value (default 1f)
	 * @return the transpose value
	 */
	public float getTranspose() {
		return transpose;
	}

	/**
	 * Sets the transpose value
	 * @param transpose the transpose value
	 */
	public void setTranspose(float transpose) {
		this.transpose = transpose;
	}
	
	/**
	 * Gets the time in the track
	 * @return the time
	 */
	public float getTime() {
		final long remainingTime;
		if(this.lastFreq == 0) {
			remainingTime = Note.lenToMillis(this.lastLen, this.bpm) - (new Date().getTime() - this.lastStartTime);			
		} else {
			remainingTime = Sound.getTime();
		}
		
		return this.time + Note.millisToLen((int) (Note.lenToMillis(this.lastLen, this.bpm) - remainingTime), this.bpm);
	}
	
	/**
	 * Update the position to the given time
	 * @param time the time
	 */
	public void setTime(float time) {
		this.reset();
		
		while(this.position < this.notes.size() && this.time + this.notes.get(this.position).getLen() < time) {
			this.time += this.notes.get(this.position).getLen();
			this.position++;
		}
		
		if(this.time < time) {
			this.partialNote = new Note(this.notes.get(this.position).getFreq(), time - this.time);
		}
	}

	/**
	 * Play the track
	 */
	public void play() {
		if(this.position < this.notes.size()) {
			final Note note;
			
			if(this.partialNote != null) {
				note = this.partialNote;
				this.partialNote = null;
			} else {
				note = this.notes.get(this.position);
			}
			
			note.play(this.bpm, this.transpose);
			this.time += note.getLen();
			this.lastLen = note.getLen();
			this.lastFreq = note.getFreq();
			this.lastStartTime = new Date().getTime();
			this.position++;
		}
	}
	
	/**
	 * @return <code>true</code> if the track is finished
	 */
	public boolean isOver() {
		return this.position >= this.notes.size();
	}
	
	/**
	 * Reset the track (set position and time to 0)
	 */
	public void reset() {
		this.position = 0;
		this.time = 0;
	}

    @Override
    public void onBroadcastReceived(byte[] message) {
        float DT = 0;
        float received_time = ByteBuffer.wrap(message).getFloat();
        if ((received_time - DT) > this.time){
            setTime(received_time);
        }
    }
}
