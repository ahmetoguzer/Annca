package io.memfis19.annca.internal.controller.impl;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;

import java.io.File;

import io.memfis19.annca.internal.configuration.AnncaConfiguration;
import io.memfis19.annca.internal.configuration.ConfigurationProvider;
import io.memfis19.annca.internal.controller.CameraController;
import io.memfis19.annca.internal.controller.view.CameraView;
import io.memfis19.annca.internal.manager.CameraManager;
import io.memfis19.annca.internal.manager.impl.Camera2Manager;
import io.memfis19.annca.internal.manager.listener.CameraCloseListener;
import io.memfis19.annca.internal.manager.listener.CameraOpenListener;
import io.memfis19.annca.internal.manager.listener.CameraPhotoListener;
import io.memfis19.annca.internal.manager.listener.CameraVideoListener;
import io.memfis19.annca.internal.utils.CameraHelper;

/**
 * Created by memfis on 7/6/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Controller implements CameraController<String>,
        CameraOpenListener<String, Size, TextureView.SurfaceTextureListener>,
        CameraPhotoListener, CameraVideoListener<Size>, CameraCloseListener<String> {

    private final static String TAG = "Camera2Controller";

    private CameraManager<String, Size, TextureView.SurfaceTextureListener> camera2Manager;

    private String currentCameraId;

    private File outputFile;

    private CameraView cameraView;
    private ConfigurationProvider configurationProvider;

    public Camera2Controller(CameraView cameraView, ConfigurationProvider configurationProvider) {
        this.cameraView = cameraView;
        this.configurationProvider = configurationProvider;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        camera2Manager = Camera2Manager.getInstance();
        camera2Manager.initializeCameraManager(configurationProvider, cameraView.getActivity());
        currentCameraId = camera2Manager.getFaceBackCameraId();
    }

    @Override
    public void onResume() {
        camera2Manager.openCamera(currentCameraId, this);
    }

    @Override
    public void onPause() {
        camera2Manager.closeCamera(null);
        cameraView.releaseCameraPreview();
    }

    @Override
    public void onDestroy() {
        camera2Manager.releaseCameraManager();
    }

    @Override
    public void takePhoto() {
        outputFile = CameraHelper.getOutputMediaFile(cameraView.getActivity(), AnncaConfiguration.MEDIA_ACTION_PHOTO);
        camera2Manager.takePhoto(outputFile, this);
    }

    @Override
    public void startVideoRecord() {
        outputFile = CameraHelper.getOutputMediaFile(cameraView.getActivity(), AnncaConfiguration.MEDIA_ACTION_VIDEO);
        camera2Manager.startVideoRecord(outputFile, this);
    }

    @Override
    public void stopVideoRecord() {
        camera2Manager.stopVideoRecord();
    }

    @Override
    public boolean isVideoRecording() {
        return camera2Manager.isVideoRecording();
    }

    @Override
    public void switchCamera(final @AnncaConfiguration.CameraFace int cameraFace) {
        currentCameraId = camera2Manager.getCurrentCameraId().equals(camera2Manager.getFaceFrontCameraId()) ?
                camera2Manager.getFaceBackCameraId() : camera2Manager.getFaceFrontCameraId();

        camera2Manager.closeCamera(this);
    }

    @Override
    public void switchQuality() {
        camera2Manager.closeCamera(this);
    }

    @Override
    public int getNumberOfCameras() {
        return camera2Manager.getNumberOfCameras();
    }

    @Override
    public int getMediaAction() {
        return configurationProvider.getMediaAction();
    }

    @Override
    public int getCameraOrientation() {
        if (camera2Manager.getCurrentCameraId().equals(camera2Manager.getFaceBackCameraId()))
            return camera2Manager.getFaceBackCameraOrientation();
        return camera2Manager.getFaceFrontCameraOrientation();
    }

    @Override
    public File getOutputFile() {
        return outputFile;
    }

    @Override
    public String getCurrentCameraId() {
        return currentCameraId;
    }

    @Override
    public void onCameraOpened(String openedCameraId, Size previewSize, TextureView.SurfaceTextureListener surfaceTextureListener) {
        cameraView.updateUiForMediaAction(AnncaConfiguration.MEDIA_ACTION_UNSPECIFIED);
        cameraView.updateCameraPreview(previewSize, surfaceTextureListener);
        cameraView.updateCameraSwitcher(camera2Manager.getNumberOfCameras());
    }

    @Override
    public void onCameraOpenError() {
        Log.e(TAG, "onCameraOpenError");
    }

    @Override
    public void onCameraClosed(String closedCameraId) {
        cameraView.releaseCameraPreview();

        camera2Manager.openCamera(currentCameraId, this);
    }

    @Override
    public void onPhotoTaken(File photoFile) {
        cameraView.onPhotoTaken();
    }

    @Override
    public void onPhotoTakeError() {

    }

    @Override
    public void onVideoRecordStarted(Size videoSize) {
        cameraView.onVideoRecordStart(videoSize.getWidth(), videoSize.getHeight());
    }

    @Override
    public void onVideoRecordStopped(File videoFile) {
        cameraView.onVideoRecordStop();
    }

    @Override
    public void onVideoRecordError() {

    }
}
