package maestrovs.photocube;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;


//http://android-coffee.com/tutorial-how-to-draw-3d-photo-cube-in-android-studio-1-4/

public class MyGLActivity extends Activity {

    private MySurface glView;                   // use GLSurfaceView
    // Call back when the activity is started, to initialize the view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        RelativeLayout surfRel=(RelativeLayout) findViewById(R.id.surfRel);

         glView = new MySurface(this);           // Allocate a GLSurfaceView.

       glView.setRenderer(new MyGLRenderer(this)); // Use a custom renderer
       // glView.setRenderer(new LightRenderer(this,40,40));

        Log.d("my","start");

        surfRel.addView(glView);





        //this.setContentView(glView);                // This activity sets to GLSurfaceView
    }

    // Call back when the activity is going into the background
    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
    }

    // Call back after onPause()
    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("my", "___"+event.toString());
        return true;
    }



}