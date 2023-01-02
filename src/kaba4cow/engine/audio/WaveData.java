package kaba4cow.engine.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

public class WaveData {

	public final int format;
	public final int samplerate;
	public final int totalBytes;
	public final int bytesPerFrame;
	public final ByteBuffer data;

	private final AudioInputStream stream;
	private final byte[] dataArray;

	private WaveData(AudioInputStream stream) {
		AudioFormat audioFormat = stream.getFormat();
		this.stream = stream;
		this.format = getOpenALFormat(audioFormat.getChannels(),
				audioFormat.getSampleSizeInBits());
		this.samplerate = (int) audioFormat.getSampleRate();
		this.bytesPerFrame = audioFormat.getFrameSize();
		this.totalBytes = (int) (stream.getFrameLength() * bytesPerFrame);
		this.data = BufferUtils.createByteBuffer(totalBytes);
		this.dataArray = new byte[totalBytes];
		this.loadData();
	}

	protected void dispose() {
		try {
			stream.close();
			data.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ByteBuffer loadData() {
		try {
			int bytesRead = stream.read(dataArray, 0, totalBytes);
			data.clear();
			data.put(dataArray, 0, bytesRead);
			data.flip();
		} catch (IOException e) {
			System.err.println("Couldn't read bytes from audio stream");
			e.printStackTrace();
		}
		return data;
	}

	public static WaveData create(String file) {
		try {
			FileInputStream ins = new FileInputStream(new File("resources/"
					+ file + ".wav"));
			InputStream bufferedInput = new BufferedInputStream(ins);
			AudioInputStream stream = null;
			try {
				stream = AudioSystem.getAudioInputStream(bufferedInput);
			} catch (IOException | UnsupportedAudioFileException e) {
				e.printStackTrace();
				return null;
			}
			WaveData wavStream = new WaveData(stream);
			return wavStream;
		} catch (FileNotFoundException e1) {
			System.err.println("Couldn't find file: " + file);
			e1.printStackTrace();
			return null;
		}
	}

	private static int getOpenALFormat(int channels, int bitsPerSample) {
		if (channels == 1)
			return bitsPerSample == 8 ? AL10.AL_FORMAT_MONO8
					: AL10.AL_FORMAT_MONO16;
		else
			return bitsPerSample == 8 ? AL10.AL_FORMAT_STEREO8
					: AL10.AL_FORMAT_STEREO16;
	}

}
