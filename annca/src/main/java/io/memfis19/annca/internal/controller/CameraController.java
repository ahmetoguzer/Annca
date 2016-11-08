package io.memfis19.annca.internal.controller;

import android.os.Bundle;

import java.io.File;

import io.memfis19.annca.internal.configuration.AnncaConfiguration;

/**
 * Created by memfis on 7/6/16.
 */
public interface CameraController<CameraId> {

    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onDestroy();

    void takePhoto();

    void startVideoRecord();

    void stopVideoRecord();

    boolean isVideoRecording();

    void switchCamera(@AnncaConfiguration.CameraFace int cameraFace);

    void switchQuality();

    int getNumberOfCameras();

    @AnncaConfiguration.MediaAction
    int getMediaAction();

    int getCameraOrientation();

    CameraId getCurrentCameraId();

    File getOutputFile();

}
