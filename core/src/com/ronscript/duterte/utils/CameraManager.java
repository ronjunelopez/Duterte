package com.ronscript.duterte.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class CameraManager {
    static Vector3 tmpV3 = new Vector3();
    static Vector2 tmpV2 = new Vector2();
    private static CameraManager ourInstance = new CameraManager();
    private Camera camera;
    private Viewport viewport;

    private CameraManager() {
    }

    public static CameraManager getInstance() {
        return ourInstance;
    }

    public static Camera getCamera() {
        return  getInstance().camera;
    }

    public static Viewport getViewport(){
        return getInstance().viewport;
    }

    /**
     * Convert screen point to world point
     * @param screenPoint Vector3 point
     * @return Vector3 world point
     */
    public static Vector3 getScreenToWorldPoint(Vector3 screenPoint) {
        if(getViewport() != null) {
            tmpV3.set(getViewport().unproject(screenPoint));
        } else {
            tmpV3.set(getCamera().unproject(screenPoint));
        }
        return tmpV3;
    }

    public static Vector2 getScreenToWorldPoint2(Vector3 screenPoint) {
        Vector3 worldPoint = getScreenToWorldPoint(screenPoint);
        return tmpV2.set(worldPoint.x, worldPoint.y);
    }

    public static Matrix4 getProjectionMatrix() {
        return getInstance().camera.combined;
    }

    public void setup(Camera camera, Viewport viewport) {
        this.camera = camera;
        this.viewport = viewport;
    }

    public boolean isOutsideCamera(Vector2 position){
        return isOutsideCamera(position, 0,0);
    }

    public boolean isOutsideCamera(Vector2 position, float offset){
        return isOutsideCamera(position, offset, offset);
    }

    public boolean isOutsideCamera(float x, float y, float offset){
        return isOutsideCamera(new Vector2(x, y), offset, offset);
    }

    public boolean isOutsideCamera(Vector2 position, float offsetX, float offsetY) {
        boolean isOutside = false;
        float CAMERA_WIDTH = Constants.WIDTH;
        float CAMERA_HEIGHT = Constants.HEIGHT;

        Vector3 camPos = camera.position;
        if(position.x > camPos.x + (CAMERA_WIDTH + (offsetX )) / 2
                || position.x < camPos.x - (CAMERA_WIDTH + (offsetX )) / 2
                || position.y  > camPos.y + (CAMERA_HEIGHT + (offsetY )) / 2
                || position.y  < camPos.y - (CAMERA_HEIGHT + (offsetY )) / 2  ) {
            isOutside = true;
        }
        return isOutside;
    }

}
