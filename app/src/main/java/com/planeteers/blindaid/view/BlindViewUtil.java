package com.planeteers.blindaid.view;

import android.view.MotionEvent;
import android.view.View;

import butterknife.OnTouch;
import timber.log.Timber;

/**
 * Created by Jose on 11/10/15.
 */
public class BlindViewUtil {

    public final static int SWIPE_RIGHT = 0;
    public final static int SWIPE_LEFT = 1;
    public final static int SWIPE_DOWN = 2;
    public final static int SWIPE_TOP = 3;

    public BlindViewUtil(BlindNavGestureListener gestureListener){
        this.gestureListener = gestureListener;
    }

    private int MOVEMENT_THRESHOLD = 300;

    private float xDown, xCurrent;
    private float yDown, yCurrent;
    private boolean isOnclick;
    private boolean isOnSwipe;
    private int swipeDirection = 0;

    private BlindNavGestureListener gestureListener;

    public interface BlindNavGestureListener{
        boolean onSwipeLeft();
        boolean onSwipeRight();
        boolean onSwipeUp();
        boolean onSwipeDown();
        boolean onClick();
    }

    public View.OnTouchListener blindTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            switch (action){
                case MotionEvent.ACTION_DOWN:
                    xDown = event.getX();
                    yDown = event.getY();

                    Timber.d("ACTION_DOWN xDown: %f yDown: %f", xDown, yDown);
                    isOnclick = true;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float currentX = event.getX();
                    float currentY = event.getY();

                    Timber.d("ACTION_MOVE currentX: %f currentY: %f XDelta: %f", xDown, yDown, currentX - xDown, currentY - yDown);

                    if(Math.abs(xDown - currentX) > MOVEMENT_THRESHOLD){
                        isOnclick = false;
                        xCurrent = currentX;
                        isOnSwipe = true;

                        if(currentX - xDown > 0){
                            swipeDirection = SWIPE_LEFT;
                            //user swiped towards the left
                        }else{
                            //user swiped towards the right
                            swipeDirection = SWIPE_RIGHT;
                        }

                        return true;
                    }

                    if(Math.abs(yDown - currentY) > MOVEMENT_THRESHOLD){
                        isOnclick = false;
                        yCurrent = currentY;
                        isOnSwipe = true;

                        if(currentY - yDown > 0){
                            //user swiped down
                            swipeDirection = SWIPE_DOWN;
                        }else{
                            //user swiped up
                            swipeDirection = SWIPE_TOP;
                        }

                        return true;
                    }

                    isOnSwipe = false;

                    return false;
                case MotionEvent.ACTION_UP:
                    if(isOnclick && gestureListener != null){
                        return gestureListener.onClick();
                    }

                    if(isOnSwipe){
                        switch (swipeDirection){
                            case SWIPE_LEFT:
                                if(gestureListener != null){
                                    gestureListener.onSwipeLeft();
                                }
                                break;
                            case SWIPE_TOP:
                                if(gestureListener != null){
                                    gestureListener.onSwipeUp();
                                }
                                break;
                            case SWIPE_DOWN:
                                if(gestureListener != null){
                                    gestureListener.onSwipeDown();
                                }
                                break;
                            case SWIPE_RIGHT:
                                if(gestureListener != null){
                                    gestureListener.onSwipeRight();
                                }
                                break;
                        }
                    }
                    return true;
                default:
                    return true;
            }
        }
    };

}
