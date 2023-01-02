package kaba4cow.engine.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.toolbox.maths.Vectors;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

public class Source {

	private static final Map<String, SourceContainer> map = new HashMap<String, SourceContainer>();

	private final int source;

	private final SourceContainer container;

	private float gain;

	private boolean wasStopped;
	private boolean deleteOnStop;

	private static class SourceContainer {

		private List<Source> sources;
		private float volume;
		private float rolloffFactor;
		private float referenceDistance;
		private float maxDistance;

		public SourceContainer() {
			this.sources = new ArrayList<Source>();
			this.volume = 1f;
			this.rolloffFactor = 1f;
			this.referenceDistance = 1f;
			this.maxDistance = Float.POSITIVE_INFINITY;
		}

		public SourceContainer add(Source source) {
			sources.add(source);
			AudioManager.add(source);
			return this;
		}

		public void setVolume(float volume) {
			this.volume = volume;
			sources.forEach((source) -> source.setGain(source.gain));
		}

		public void setRolloffFactor(float rolloffFactor) {
			this.rolloffFactor = rolloffFactor;
		}

		public void setReferenceDistance(float referenceDistance) {
			this.referenceDistance = referenceDistance;
		}

		public void setMaxDistance(float maxDistance) {
			this.maxDistance = maxDistance;
		}

	}

	public Source(String tag, Vector3f position) {
		this.source = AL10.alGenSources();
		this.deleteOnStop = false;
		this.wasStopped = false;
		this.container = getContainer(tag).add(this);
		this.setGain(1f);
		this.setPitch(1f);
		this.setPosition(position);
		this.setRolloffFactor(container.rolloffFactor);
		this.setReferenceDistance(container.referenceDistance);
		this.setMaxDistance(container.maxDistance);
	}

	public Source(String tag) {
		this(tag, Vectors.INIT3);
	}

	private static SourceContainer getContainer(String tag) {
		if (!map.containsKey(tag))
			map.put(tag, new SourceContainer());
		return map.get(tag);
	}

	public static void setSourcesVolume(String tag, float volume) {
		if (map.containsKey(tag))
			map.get(tag).setVolume(volume);
	}

	public static void setSourcesRolloffFactor(String tag, float rolloffFactor) {
		if (map.containsKey(tag))
			map.get(tag).setRolloffFactor(rolloffFactor);
	}

	public static void setSourcesReferenceDistance(String tag,
			float referenceDistance) {
		if (map.containsKey(tag))
			map.get(tag).setReferenceDistance(referenceDistance);
	}

	public static void setSourcesMaxDistance(String tag, float maxDistance) {
		if (map.containsKey(tag))
			map.get(tag).setMaxDistance(maxDistance);
	}

	public Source play(int buffer) {
		stop();
		AL10.alSourcei(source, AL10.AL_BUFFER, buffer);
		AL10.alSourcePlay(source);
		return this;
	}

	public Source stop() {
		if (isPlaying())
			wasStopped = true;
		AL10.alSourceStop(source);
		return this;
	}

	public Source setGain(float gain) {
		this.gain = gain;
		AL10.alSourcef(source, AL10.AL_GAIN, container.volume * gain);
		return this;
	}

	public Source setPitch(float pitch) {
		AL10.alSourcef(source, AL10.AL_PITCH, pitch);
		return this;
	}

	public Source setPosition(Vector3f position) {
		AL10.alSource3f(source, AL10.AL_POSITION, position.x, position.y,
				position.z);
		return this;
	}

	public Source setVelocity(Vector3f velocity) {
		AL10.alSource3f(source, AL10.AL_VELOCITY, velocity.x, velocity.y,
				velocity.z);
		return this;
	}

	public Source setRolloffFactor(float factor) {
		AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, factor);
		return this;
	}

	public Source setReferenceDistance(float distance) {
		AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, distance);
		return this;
	}

	public Source setMaxDistance(float distance) {
		AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, distance);
		return this;
	}

	public Source pauseOn() {
		AL10.alSourcePause(source);
		return this;
	}

	public Source pauseOff() {
		AL10.alSourcePlay(source);
		return this;
	}

	public Source loopOn() {
		AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_TRUE);
		return this;
	}

	public Source loopOff() {
		AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE);
		return this;
	}

	public boolean isPlaying() {
		return AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public boolean isDeleteOnStop() {
		return deleteOnStop;
	}

	public Source setDeleteOnStop(boolean deleteOnStop) {
		this.deleteOnStop = deleteOnStop;
		return this;
	}

	public boolean wasStopped() {
		return wasStopped;
	}

	public void delete() {
		stop();
		AL10.alDeleteSources(source);
		AudioManager.remove(this);
	}

	public int getSource() {
		return source;
	}

}
