//package com.example.dx144_xl.testapp;
//
//import android.graphics.Canvas;
//import android.view.SurfaceHolder;
//
//class PanelThread extends Thread {
//    private SurfaceHolder _surfaceHolder;
//    private DrawingPanel _panel;
//    private boolean _run = false;
//    private PanelThread _thread;
//
//
//    public PanelThread(SurfaceHolder surfaceHolder, DrawingPanel panel) {
//        _surfaceHolder = surfaceHolder;
//        _panel = panel;
//    }
//
//
//    public void setRunning(boolean run) { //Allow us to stop the thread
//        _run = run;
//    }
//
//
//    @Override
//    public void run() {
//        Canvas c;
//        while (_run) {     //When setRunning(false) occurs, _run is
//            c = null;      //set to false and loop ends, stopping thread
//
//
//            try {
//
//
//                c = _surfaceHolder.lockCanvas(null);
//                synchronized (_surfaceHolder) {
//
//
//                    //Insert methods to modify positions of items in onDraw()
//                    postInvalidate();
//
//
//                }
//            } finally {
//                if (c != null) {
//                    _surfaceHolder.unlockCanvasAndPost(c);
//                }
//            }
//        }
//    }
//
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//
//
//        setWillNotDraw(false); //Allows us to use invalidate() to call onDraw()
//
//
//        _thread = new PanelThread(getHolder(), this); //Start the thread that
//        _thread.setRunning(true);                     //will make calls to
//        _thread.start();                              //onDraw()
//    }
//
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        try {
//            _thread.setRunning(false);                //Tells thread to stop
//            _thread.join();                           //Removes thread from mem.
//        } catch (InterruptedException e) {}
//    }
//
//    public SurfaceHolder getHolder() {
//        return _surfaceHolder;
//    }
//}
