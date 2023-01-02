package kaba4cow.engine.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.toolbox.Printer;
import kaba4cow.engine.utils.ProgramUtils;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.util.vector.Vector3f;

public class AudioManager {

	private static final Map<String, Integer> buffers = new HashMap<String, Integer>();
	private static final List<Source> sources = new ArrayList<Source>();

	public static final int LINEAR_DISTANCE = AL11.AL_LINEAR_DISTANCE;
	public static final int LINEAR_DISTANCE_CLAMPED = AL11.AL_LINEAR_DISTANCE_CLAMPED;
	public static final int EXPONENT_DISTANCE = AL11.AL_EXPONENT_DISTANCE;
	public static final int EXPONENT_DISTANCE_CLAMPED = AL11.AL_EXPONENT_DISTANCE_CLAMPED;

	public static void init() {
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public static void update() {
		Source current = null;
		for (int i = sources.size() - 1; i >= 0; i--) {
			current = sources.get(i);
			if (current.wasStopped() && current.isDeleteOnStop())
				current.delete();
		}
	}

	public static int sources() {
		return sources.size();
	}

	public static int buffers() {
		return buffers.size();
	}

	public static void setDistanceModel(int distanceModel) {
		AL10.alDistanceModel(distanceModel);
	}

	public static void setListenerData(Vector3f position, Vector3f velocity) {
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
		AL10.alListener3f(AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
	}

	public static int load(String file) {
		if (buffers.containsKey(file))
			return buffers.get(file);
		Printer.println("LOADING AUDIO: " + file);
		int buffer = AL10.alGenBuffers();
		WaveData waveData = WaveData.create(ProgramUtils.getAudioLocation()
				+ file);
		AL10.alBufferData(buffer, waveData.format, waveData.data,
				waveData.samplerate);
		waveData.dispose();
		buffers.put(file, buffer);
		return buffer;
	}

	public static int get(String file) {
		if (!buffers.containsKey(file))
			load(file);
		return buffers.get(file);
	}

	protected static void add(Source source) {
		if (source != null && !sources.contains(source))
			sources.add(source);
	}

	protected static void remove(Source source) {
		if (source != null && sources.contains(source)) {
			sources.remove(source);
		}
	}

	public static void cleanUp() {
		Printer.println("CLEANING UP " + sources.size() + " SOURCES");
		for (int i = 0; i < sources.size(); i++)
			sources.get(i).delete();
		Printer.println("CLEANING UP " + buffers.size() + " BUFFERS");
		for (String key : buffers.keySet())
			AL10.alDeleteBuffers(buffers.get(key));
		AL.destroy();
	}

}
