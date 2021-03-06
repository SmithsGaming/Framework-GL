/**
 * Copyright 2012 Jason Sorensen (sorensenj@smert.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.smert.frameworkgl.openal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.smert.frameworkgl.openal.codecs.Codec;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALContext;
import org.lwjgl.openal.ALDevice;
import org.lwjgl.openal.Util;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class OpenAL {

    private boolean contextSynchronized;
    private float defaultSourceMaxDistance;
    private float defaultSourceMusicVolume;
    private float defaultSourceReferenceDistance;
    private float defaultSourceRolloff;
    private float defaultSourceSoundVolume;
    private float masterVolume;
    private int contextFrequency;
    private int contextRefresh;
    private int maxChannels;
    private int numberOfMusicChannels;
    private int numberOfSoundChannels;
    private final Config config;
    private final List<Integer> musicSourcesPlaying;
    private final List<Integer> soundSourcesPlaying;
    private final List<OpenALSource> tempSources;
    private final Map<String, OpenALBuffer> nameToOpenALBuffer;
    private final Map<String, OpenALBuffer> nameToTempOpenALBuffer;
    private final Map<String, Codec> extensionToCodec;
    private ALContext alContext;
    private OpenALListener listener;
    private String device;

    public OpenAL() {
        config = new Config();
        musicSourcesPlaying = new ArrayList<>();
        soundSourcesPlaying = new ArrayList<>();
        tempSources = new ArrayList<>();
        nameToOpenALBuffer = new HashMap<>();
        nameToTempOpenALBuffer = new HashMap<>();
        extensionToCodec = new HashMap<>();
        reset();
    }

    protected void checkForError(String message) {
        int error = AL10.alGetError();
        if (error != AL10.AL_NO_ERROR) {
            String errorMessage = getErrorMessage(error);
            throw new RuntimeException("Error " + message + ": Error Number: " + error + " Message: " + errorMessage);
        }
    }

    protected OpenALBuffer createALBuffer() {
        OpenALBuffer buffer = AL.alFactory.createOpenALBuffer();
        buffer.create();
        return buffer;
    }

    protected OpenALSource createALSource() {
        OpenALSource source = AL.alFactory.createOpenALSource();
        source.create();
        return source;
    }

    protected OpenALBuffer createTempALBuffer(String audioFile) {
        OpenALBuffer buffer = createALBuffer();
        nameToTempOpenALBuffer.put(audioFile, buffer);
        return buffer;
    }

    protected OpenALSource createTempALSource() {
        OpenALSource source = createALSource();
        tempSources.add(source);
        return source;
    }

    protected Codec.Data getCodecData(String filename) throws IOException {

        // Get the extension from the filename
        int posOfLastPeriod = filename.lastIndexOf(".");
        if (posOfLastPeriod == -1) {
            throw new IllegalArgumentException("The filename must have an extension: " + filename);
        }
        String extension = filename.substring(posOfLastPeriod + 1).toLowerCase();

        // Does the codec for this extension exist?
        if (!extensionToCodec.containsKey(extension)) {
            throw new IllegalArgumentException("The extension has not been registered: " + extension);
        }

        // Load the filename using the codec
        Codec codec = extensionToCodec.get(extension);
        return codec.load(filename);
    }

    public void destroy() {
        org.lwjgl.openal.AL.destroy(alContext);
    }

    public float getDopplerFactor() {
        return AL10.alGetFloat(AL10.AL_DOPPLER_FACTOR);
    }

    public void setDopplerFactor(float factor) {
        AL10.alDopplerFactor(factor);
    }

    public void setDopplerVelocity(float velocity) {
        AL10.alDopplerVelocity(velocity);
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(float masterVolume) {
        this.masterVolume = masterVolume;
    }

    public float getVolume(int soundID) {
        if (soundID == 0) {
            return 0;
        }
        float volume = AL.sourceHelper.getGain(soundID);
        checkForError("getting sound volume");
        return volume;
    }

    public void setVolume(int soundID, float volume) {
        if (soundID == 0) {
            return;
        }
        AL.sourceHelper.setGain(soundID, volume);
        checkForError("setting sound volume");
    }

    public int getDistanceModel() {
        return AL10.alGetInteger(AL10.AL_DISTANCE_MODEL);
    }

    public void setDistanceModelInverseDistance() {
        AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE);
    }

    public void setDistanceModelInverseDistanceClamped() {
        AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
    }

    public void setDistanceModelNone() {
        AL10.alDistanceModel(AL10.AL_NONE);
    }

    public Config getConfig() {
        return config;
    }

    public OpenALListener getListener() {
        return listener;
    }

    public void setListener(OpenALListener listener) {
        this.listener = listener;
    }

    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case AL10.AL_NO_ERROR:
                message = "";
                break;
            case AL10.AL_INVALID_ENUM:
                message = "Invalid enum";
                break;
            case AL10.AL_INVALID_NAME:
                message = "Invalid name";
                break;
            case AL10.AL_INVALID_OPERATION:
                message = "Invalid operation";
                break;
            case AL10.AL_INVALID_VALUE:
                message = "Invalid value";
                break;
            case AL10.AL_OUT_OF_MEMORY:
                message = "Out of memory";
                break;
            default:
                message = "Unknown error";
                break;
        }
        return message;
    }

    public String getExtensions() {
        return AL10.alGetString(AL10.AL_EXTENSIONS);
    }

    public String getRenderer() {
        return AL10.alGetString(AL10.AL_RENDERER);
    }

    public String getVendor() {
        return AL10.alGetString(AL10.AL_VENDOR);
    }

    public String getVersion() {
        return AL10.alGetString(AL10.AL_VERSION);
    }

    public void init() {
        if (alContext != null) {
            return;
        }

        // Initialize OpenAL
        ALDevice alDevice = ALDevice.create(device);
        alContext = ALContext.create(alDevice, contextFrequency, contextRefresh, contextSynchronized);

        // Check for errors
        checkForError("initializing OpenAL");

        // Set a default distance model
        setDistanceModelInverseDistanceClamped();
        update();
    }

    public boolean isPlaying(int soundID) {
        if (soundID == 0) {
            return false;
        }
        boolean isPlaying = (AL.sourceHelper.getSourceState(soundID) == AL10.AL_PLAYING);
        checkForError("checking if sound was playing");
        return isPlaying;
    }

    public void pause(int soundID) {
        if (soundID == 0) {
            return;
        }
        AL.sourceHelper.pause(soundID);
        checkForError("pausing sound");
    }

    public int playMusic(String audioFile, boolean loop) throws IOException {
        return playMusic(audioFile, loop, true);
    }

    public int playMusic(String audioFile, boolean loop, boolean priority) throws IOException {

        // Get codec and load the audio file
        Codec.Data codecData = getCodecData(audioFile);

        // Create a new source
        OpenALSource source = createTempALSource();
        int sourceID = source.getSourceID();

        // Create a new buffer
        OpenALBuffer buffer = createTempALBuffer(audioFile);
        int bufferID = buffer.getBufferID();

        // Send buffer data
        AL.bufferHelper.setData(bufferID, codecData.format, codecData.buffer, codecData.sampleRate);
        checkForError("setting buffer data");

        // Set source parameters
        AL.sourceHelper.setBuffer(sourceID, bufferID);
        AL.sourceHelper.setGain(sourceID, defaultSourceMusicVolume);
        AL.sourceHelper.setLooping(sourceID, loop ? AL.sourceHelper.getConstTrue() : AL.sourceHelper.getConstFalse());
        AL.sourceHelper.setPosition(sourceID, 0, 0, 0);
        AL.sourceHelper.setVelocity(sourceID, 0, 0, 0);
        checkForError("setting source parameters");

        // Play music
        AL.sourceHelper.play(sourceID);
        checkForError("playing music");

        return sourceID;
    }

    public int playSound(String audioFile, boolean loop, boolean priority) throws IOException {
        return playSound(audioFile, loop, priority, 0, 0, 0, defaultSourceMaxDistance, defaultSourceReferenceDistance,
                defaultSourceRolloff);
    }

    public int playSound(String audioFile, boolean loop, boolean priority, float x, float y, float z)
            throws IOException {
        return playSound(audioFile, loop, priority, x, y, z, defaultSourceMaxDistance, defaultSourceReferenceDistance,
                defaultSourceRolloff);
    }

    public int playSound(String audioFile, boolean loop, boolean priority, float x, float y, float z,
            float maxDistance) throws IOException {
        return playSound(audioFile, loop, priority, x, y, z, maxDistance, defaultSourceReferenceDistance,
                defaultSourceRolloff);
    }

    public int playSound(String audioFile, boolean loop, boolean priority, float x, float y, float z,
            float maxDistance, float referenceDistance) throws IOException {
        return playSound(audioFile, loop, priority, x, y, z, maxDistance, referenceDistance, defaultSourceRolloff);
    }

    public int playSound(String audioFile, boolean loop, boolean priority, float x, float y, float z,
            float maxDistance, float referenceDistance, float rolloff) throws IOException {

        // Get codec and load the audio file
        Codec.Data codecData = getCodecData(audioFile);

        // Create a new source
        OpenALSource source = createTempALSource();
        int sourceID = source.getSourceID();

        // Create a new buffer
        OpenALBuffer buffer = createTempALBuffer(audioFile);
        int bufferID = buffer.getBufferID();

        // Send buffer data
        AL.bufferHelper.setData(bufferID, codecData.format, codecData.buffer, codecData.sampleRate);
        checkForError("setting buffer data");

        // Set source parameters
        AL.sourceHelper.setBuffer(sourceID, bufferID);
        AL.sourceHelper.setGain(sourceID, defaultSourceSoundVolume);
        AL.sourceHelper.setLooping(sourceID, loop ? AL.sourceHelper.getConstTrue() : AL.sourceHelper.getConstFalse());
        AL.sourceHelper.setMaxDistance(sourceID, maxDistance);
        AL.sourceHelper.setPosition(sourceID, x, y, z);
        AL.sourceHelper.setReferenceDistance(sourceID, referenceDistance);
        AL.sourceHelper.setRolloffFactor(sourceID, rolloff);
        AL.sourceHelper.setVelocity(sourceID, 0, 0, 0);
        checkForError("setting source parameters");

        // Play sound
        AL.sourceHelper.play(sourceID);
        checkForError("playing sound");

        return sourceID;
    }

    public void registerCodec(String extension, Codec codec) {
        extension = extension.toLowerCase();
        if (extensionToCodec.containsKey(extension)) {
            throw new IllegalArgumentException("The extension has already been registered: " + extension);
        }
        extensionToCodec.put(extension, codec);
    }

    public final void reset() {
        contextSynchronized = false;
        defaultSourceMaxDistance = Float.MAX_VALUE;
        defaultSourceMusicVolume = 1f;
        defaultSourceReferenceDistance = 1f;
        defaultSourceRolloff = 1f;
        defaultSourceSoundVolume = 1f;
        masterVolume = .8f;
        contextFrequency = 44100;
        contextRefresh = 60;
        maxChannels = 32;
        numberOfMusicChannels = 4;
        numberOfSoundChannels = 28;
        musicSourcesPlaying.clear();
        soundSourcesPlaying.clear();
        nameToTempOpenALBuffer.clear();
        tempSources.clear();
        nameToOpenALBuffer.clear();
        listener = null;
        device = null;
        assert ((numberOfMusicChannels + numberOfSoundChannels) == maxChannels);
    }

    public void resume(int soundID) {
        if (soundID == 0) {
            return;
        }
        AL.sourceHelper.play(soundID);
        checkForError("resuming sound");
    }

    public void rewind(int soundID) {
        if (soundID == 0) {
            return;
        }
        AL.sourceHelper.rewind(soundID);
        checkForError("rewinding sound");
    }

    public void stop(int soundID) {
        if (soundID == 0) {
            return;
        }
        AL.sourceHelper.stop(soundID);
        checkForError("stopping sound");
    }

    public void unregisterCodec(String extension) {
        extension = extension.toLowerCase();
        if (!extensionToCodec.containsKey(extension)) {
            throw new IllegalArgumentException("The extension has not been registered: " + extension);
        }
        extensionToCodec.remove(extension);
    }

    public void update() {

        // Update the listener
        listener.setGain(masterVolume);
        listener.update();
        checkForError("updating listener");

        Util.checkALError();
    }

    public class Config {

        public float getDefaultSourceMaxDistance() {
            return defaultSourceMaxDistance;
        }

        public void setDefaultSourceMaxDistance(float defaultSourceMaxDistance) {
            OpenAL.this.defaultSourceMaxDistance = defaultSourceMaxDistance;
        }

        public float getDefaultSourceMusicVolume() {
            return defaultSourceMusicVolume;
        }

        public void setDefaultSourceMusicVolume(float defaultSourceMusicVolume) {
            OpenAL.this.defaultSourceMusicVolume = defaultSourceMusicVolume;
        }

        public float getDefaultSourceReferenceDistance() {
            return defaultSourceReferenceDistance;
        }

        public void setDefaultSourceReferenceDistance(float defaultSourceReferenceDistance) {
            OpenAL.this.defaultSourceReferenceDistance = defaultSourceReferenceDistance;
        }

        public float getDefaultSourceRolloff() {
            return defaultSourceRolloff;
        }

        public void setDefaultSourceRolloff(float defaultSourceRolloff) {
            OpenAL.this.defaultSourceRolloff = defaultSourceRolloff;
        }

        public float getDefaultSourceSoundVolume() {
            return defaultSourceSoundVolume;
        }

        public void setDefaultSourceSoundVolume(float defaultSourceSoundVolume) {
            OpenAL.this.defaultSourceSoundVolume = defaultSourceSoundVolume;
        }

        public int getContextFrequency() {
            return contextFrequency;
        }

        public void setContextFrequency(int contextFrequency) {
            OpenAL.this.contextFrequency = contextFrequency;
        }

        public int getContextRefresh() {
            return contextRefresh;
        }

        public void setContextRefresh(int contextRefresh) {
            OpenAL.this.contextRefresh = contextRefresh;
        }

        public int getMaxChannels() {
            return maxChannels;
        }

        public void setMaxChannels(int maxChannels) {
            OpenAL.this.maxChannels = maxChannels;
        }

        public int getNumberOfMusicChannels() {
            return numberOfMusicChannels;
        }

        public void setNumberOfMusicChannels(int numberOfMusicChannels) {
            OpenAL.this.numberOfMusicChannels = numberOfMusicChannels;
        }

        public int getNumberOfSoundChannels() {
            return numberOfSoundChannels;
        }

        public void setNumberOfSoundChannels(int numberOfSoundChannels) {
            OpenAL.this.numberOfSoundChannels = numberOfSoundChannels;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            OpenAL.this.device = device;
        }

        public boolean isContextSynchronized() {
            return contextSynchronized;
        }

        public void setContextSynchronized(boolean contextSynchronized) {
            OpenAL.this.contextSynchronized = contextSynchronized;
        }

    }

}
