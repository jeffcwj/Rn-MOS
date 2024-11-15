package org.libsdl.app;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Process;
import android.util.Log;

/* loaded from: classes.dex */
public class SDLAudioManager {
    protected static final String TAG = "SDLAudio";
    protected static AudioRecord mAudioRecord;
    protected static AudioTrack mAudioTrack;

    public static native int nativeSetupJNI();

    public static void initialize() {
        mAudioTrack = null;
        mAudioRecord = null;
    }

    protected static String getAudioFormatString(int audioFormat) {
        switch (audioFormat) {
            case 2:
                return "16-bit";
            case 3:
                return "8-bit";
            case 4:
                return "float";
            default:
                return Integer.toString(audioFormat);
        }
    }

    protected static int[] open(boolean isCapture, int sampleRate, int audioFormat, int desiredChannels, int desiredFrames) {
        int sampleSize;
        int channelConfig;
        int minBufferSize;
        Log.v(TAG, "Opening " + (isCapture ? "capture" : "playback") + ", requested " + desiredFrames + " frames of " + desiredChannels + " channel " + getAudioFormatString(audioFormat) + " audio at " + sampleRate + " Hz");
        if (Build.VERSION.SDK_INT < 21) {
            if (desiredChannels > 2) {
                desiredChannels = 2;
            }
            if (sampleRate < 8000) {
                sampleRate = 8000;
            } else if (sampleRate > 48000) {
                sampleRate = 48000;
            }
        }
        if (audioFormat == 4) {
            int minSDKVersion = isCapture ? 23 : 21;
            if (Build.VERSION.SDK_INT < minSDKVersion) {
                audioFormat = 2;
            }
        }
        switch (audioFormat) {
            case 2:
                sampleSize = 2;
                break;
            case 3:
                sampleSize = 1;
                break;
            case 4:
                sampleSize = 4;
                break;
            default:
                Log.v(TAG, "Requested format " + audioFormat + ", getting ENCODING_PCM_16BIT");
                audioFormat = 2;
                sampleSize = 2;
                break;
        }
        if (isCapture) {
            switch (desiredChannels) {
                case 1:
                    channelConfig = 16;
                    break;
                case 2:
                    channelConfig = 12;
                    break;
                default:
                    Log.v(TAG, "Requested " + desiredChannels + " channels, getting stereo");
                    desiredChannels = 2;
                    channelConfig = 12;
                    break;
            }
        } else {
            switch (desiredChannels) {
                case 1:
                    channelConfig = 4;
                    break;
                case 2:
                    channelConfig = 12;
                    break;
                case 3:
                    channelConfig = 28;
                    break;
                case 4:
                    channelConfig = 204;
                    break;
                case 5:
                    channelConfig = 220;
                    break;
                case 6:
                    channelConfig = 252;
                    break;
                case 7:
                    channelConfig = 1276;
                    break;
                case 8:
                    if (Build.VERSION.SDK_INT >= 23) {
                        channelConfig = 6396;
                        break;
                    } else {
                        Log.v(TAG, "Requested " + desiredChannels + " channels, getting 5.1 surround");
                        desiredChannels = 6;
                        channelConfig = 252;
                        break;
                    }
                default:
                    Log.v(TAG, "Requested " + desiredChannels + " channels, getting stereo");
                    desiredChannels = 2;
                    channelConfig = 12;
                    break;
            }
        }
        int frameSize = sampleSize * desiredChannels;
        if (isCapture) {
            minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        } else {
            minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        }
        int desiredFrames2 = Math.max(desiredFrames, ((minBufferSize + frameSize) - 1) / frameSize);
        int[] results = new int[4];
        if (isCapture) {
            if (mAudioRecord == null) {
                mAudioRecord = new AudioRecord(0, sampleRate, channelConfig, audioFormat, desiredFrames2 * frameSize);
                if (mAudioRecord.getState() != 1) {
                    Log.e(TAG, "Failed during initialization of AudioRecord");
                    mAudioRecord.release();
                    mAudioRecord = null;
                    return null;
                }
                mAudioRecord.startRecording();
            }
            results[0] = mAudioRecord.getSampleRate();
            results[1] = mAudioRecord.getAudioFormat();
            results[2] = mAudioRecord.getChannelCount();
        } else {
            if (mAudioTrack == null) {
                mAudioTrack = new AudioTrack(3, sampleRate, channelConfig, audioFormat, desiredFrames2 * frameSize, 1);
                if (mAudioTrack.getState() != 1) {
                    Log.e(TAG, "Failed during initialization of Audio Track");
                    mAudioTrack.release();
                    mAudioTrack = null;
                    return null;
                }
                mAudioTrack.play();
            }
            results[0] = mAudioTrack.getSampleRate();
            results[1] = mAudioTrack.getAudioFormat();
            results[2] = mAudioTrack.getChannelCount();
        }
        results[3] = desiredFrames2;
        Log.v(TAG, "Opening " + (isCapture ? "capture" : "playback") + ", got " + results[3] + " frames of " + results[2] + " channel " + getAudioFormatString(results[1]) + " audio at " + results[0] + " Hz");
        return results;
    }

    public static int[] audioOpen(int sampleRate, int audioFormat, int desiredChannels, int desiredFrames) {
        return open(false, sampleRate, audioFormat, desiredChannels, desiredFrames);
    }

    public static void audioWriteFloatBuffer(float[] buffer) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < buffer.length) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i, 0);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(float)");
                return;
            }
        }
    }

    public static void audioWriteShortBuffer(short[] buffer) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < buffer.length) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(short)");
                return;
            }
        }
    }

    public static void audioWriteByteBuffer(byte[] buffer) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < buffer.length) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(byte)");
                return;
            }
        }
    }

    public static int[] captureOpen(int sampleRate, int audioFormat, int desiredChannels, int desiredFrames) {
        return open(true, sampleRate, audioFormat, desiredChannels, desiredFrames);
    }

    public static int captureReadFloatBuffer(float[] buffer, boolean blocking) {
        return mAudioRecord.read(buffer, 0, buffer.length, blocking ? 0 : 1);
    }

    public static int captureReadShortBuffer(short[] buffer, boolean blocking) {
        if (Build.VERSION.SDK_INT < 23) {
            return mAudioRecord.read(buffer, 0, buffer.length);
        }
        return mAudioRecord.read(buffer, 0, buffer.length, blocking ? 0 : 1);
    }

    public static int captureReadByteBuffer(byte[] buffer, boolean blocking) {
        if (Build.VERSION.SDK_INT < 23) {
            return mAudioRecord.read(buffer, 0, buffer.length);
        }
        return mAudioRecord.read(buffer, 0, buffer.length, blocking ? 0 : 1);
    }

    public static void audioClose() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public static void captureClose() {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    public static void audioSetThreadPriority(boolean iscapture, int device_id) {
        try {
            if (iscapture) {
                Thread.currentThread().setName("SDLAudioC" + device_id);
            } else {
                Thread.currentThread().setName("SDLAudioP" + device_id);
            }
            Process.setThreadPriority(-16);
        } catch (Exception e) {
            Log.v(TAG, "modify thread properties failed " + e.toString());
        }
    }
}
