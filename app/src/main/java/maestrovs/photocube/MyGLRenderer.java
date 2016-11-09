package maestrovs.photocube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private PhotoCube cube;     // (NEW)



    private static int dalnostZ = 30;


   // Sphere mSphere;
   // Cylinder cylinder;

   ArrayList<Telo> planets = new ArrayList();
    private int[] textureIDs = new int[100];

    float step0=1.5f;
    float step1=2f;
    float step3=2.5f;
    private float currentOrbit=0;
    private float currentState=0;
    private float stepState=40;


    Planet space;

    Planet sun;
    Planet mercury;
    Planet venus;
    Planet earth;
    Planet mars;
    Planet jupiter;
    Planet saturn;
    Planet uranus;
    Planet neptune;
    Planet pluto;
    Planet charon;

    Context context;

    private FloatBuffer lPos;
    private FloatBuffer lab, ldb, lsb;


    // Constructor
    public MyGLRenderer(Context context) {

    this.context=context;





        int[] resId = {
                R.drawable.metal_texture_011,
                R.drawable.metal_texture_011,
                R.drawable.metal_texture_011,
                R.drawable.metal_texture_011,
                R.drawable.metal_texture_011,
                R.drawable.metal_texture_011
        } ;

        Cube kvadrat = new Cube(context, new Coord3d(20f,0f,15f),new Coord3d(1,1,1),resId);
        kvadrat.setRGB(3,1,1,1);
        setTextureId(kvadrat);
        planets.add(kvadrat);

        Surface surface = new Surface(context,new Coord3d(-10f,-5f,-20f),80,80,80,R.drawable.h_map4,R.drawable.s_map4_1);
        surface.setRGB(3,1,1,1);
        setTextureId(surface);
        planets.add(surface);

        Surface surface2 = new Surface(context,new Coord3d(-10f,15f,-20f),80,80,80,R.drawable.h_montain,R.drawable.h_montain);
        surface2.setRGB(3,1,1,1);
        setTextureId(surface2);
        planets.add(surface2);

        sun = new Planet(context, new Coord3d(0,15,1.8f),45.0f,60,R.drawable.space_red);
        /*sun.setFi(12,0.2f);
        sun.setTeta(180,0);
        sun.setRGB(0.5f,1,1,1);
        setTextureId(sun);
        planets.add(sun);*/





        lPos = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        lPos.put(new float[]{5f, 1.0f, 5f, 1.0f});//положение светила
        lPos.position(0);
        lab = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        // THIS WORKS..
        lab.put(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        lab.position(0);
        ldb = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        // SO DOES THIS..
        ldb.put(new float[]{0.5f, 0.5f, 1.0f, 1.0f});
        ldb.position(0);
        // BUT NOT THIS
        lsb = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        lsb.put(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
        lsb.position(0);

        // Blending (NEW)
        boolean blendingEnabled = false;  // Is blending on? (NEW)

    }

    int textureId=0;

    private void setTextureId(Telo telo)
    {

       // planet.setTextureId(textureId);
        int sdvig= telo.getBitmaps();
        int[] textMass=new int[sdvig];
        for(int i=0;i<sdvig;i++)
        {
            textMass[i]=textureId+i;
        }
        telo.setTextureId(textMass);

        textureId+=sdvig;
    }

    // Call back when the surface is first created or re-created.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //gl.glEnable(GL10.GL_LIGHTING);//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

       // gl.glViewport(0, 0, 1, 1);
       // gl.glMatrixMode(GL10.GL_PROJECTION);
       // gl.glLoadIdentity();

        gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
        gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
        gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance

        // Setup Texture, each time the surface is created (NEW)
       // cube.loadTexture(gl);             // Load images into textures (NEW)
      //  cylinder.loadTexture(gl);
        //sun.loadTexture(gl);

        for( int f = 0; f<planets.size();f++)
        {
            //if(planets.get(f) instanceof  Planet)

                Telo planet =  planets.get(f);




              int imagesSize = planet.getBitmaps();

            for(int i=0;i<imagesSize;i++) {

                Bitmap bm = planet.getMyBitmap(i);


                if (bm != null/*&&!(planet instanceof PlanetRing)*/) {


                    Log.d("my", "bitmap ok");

                    gl.glBindTexture(GL10.GL_TEXTURE_2D, planet.getTextureId(i));
                    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
                    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
                    // Build Texture from loaded bitmap for the currently-bind texture ID
                    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm, 0);

                    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
                }
            }


        }


        gl.glFrontFace(GL10.GL_CCW);




        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lPos);

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lab);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, ldb);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lsb);
        gl.glEnable(GL10.GL_LIGHT0);



        gl.glEnable(GL10.GL_TEXTURE_2D);  // Enable texture (NEW)


        // Setup Blending (NEW)
        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);           // Full brightness, 50% alpha (NEW)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE); // Select blending function (NEW)


    }

    // Call back after onSurfaceCreated() or whenever the window's size changes
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;   // To prevent divide by zero
        float aspect = (float)width / height;

        Log.d("my","?surf changed");

        //aspect=-3;

        // Set the viewport (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);



        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
        gl.glLoadIdentity();                 // Reset projection matrix
        // Use perspective projection
        GLU.gluPerspective(gl, dalnostZ, aspect, 0.1f, 200.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
        gl.glLoadIdentity();                 // Reset

        // You OpenGL|ES display re-sizing code here
        // ......
    }

    float dZ=-0.0f;


    float angleRotate = 10;
    float rotateX=0.1f;
    float rotateY=0.05f;
    float rotateZ=0;

    float dRotate=0.01f;


   /* double rotateX=0;
    double dfi=1.2;*/

    double drx=0, dry=0, drz=0;

    // Call back to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear color and depth buffers
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // ----- Render the Cube -----
        gl.glLoadIdentity();

       // gl.glEnable(GL10.GL_BLEND);       // Turn blending on (NEW)
       // gl.glDisable(GL10.GL_DEPTH_TEST);


        // Reset the model-view matrix
        gl.glTranslatef(0.0f, -5.0f, Z);   // Translate into the screen
        Z+=speedZ;
                //speedZ;




        angleRotate-=speedX;

        gl.glRotatef(angleRotate, 0, 5,0); // Rotate
        //  gl.glRotatef(angleCube, 0.2f, 0.3f, 0.15f);


        drx=0;
        dry=0;
        drz=0;


        for(Telo telo: planets) {
           // Log.d("my","draw all");
           // telo.rotateXYZ(drx,dry,drz);
            telo.draw(gl);
        }




       // Z+= speedZ;
        speedZ = speedZ /koefdiv;

        if(Math.abs(speedZ)<=koefZ){
            speedZ =0;
        }


        X+= speedX;
        speedX = speedX /koefdiv;

        if(Math.abs(speedX)<=koefX){
            speedX =0;
        }


    }

    float koefdiv=1.09f;

    float Z=-10;
    float X=0;

    float speedZ = 0.0f;
    float koefZ=0.2f;


    float speedX=0;
    float koefX=0.02f;




    public void setAngle(float dx, float dy) {

        speedZ = koefZ*dy;

        speedX = koefX*dx;



    }

    private void createSunSystem()
    {

        sun = new Planet(context, new Coord3d(0,0,-50),1.0f,26,R.drawable.sun2);
        sun.setFi(12,0.2f);
        sun.setTeta(180,0);
        sun.setRGB(0.5f,1,1,1);
        setTextureId(sun);
        planets.add(sun);

        currentOrbit+=step0;
        currentState+=stepState;

        //sun.addSatellite(space,0,0,0.04f);


        mercury = new Planet(context, new Coord3d(1,1,1),0.1f,20,R.drawable.mercury);
        mercury.setRGB(3,1,1,1);
        mercury.setFi(12,0.2f);
        setTextureId(mercury);
        planets.add(mercury);
        sun.addSatellite(mercury,currentOrbit,currentState,0.04f);

        currentOrbit+=step0;
        currentState+=stepState;

        venus = new Planet(context, new Coord3d(1,1,1),0.19f,26,R.drawable.venus);
        venus.setRGB(3,1,1,1);
        venus.setFi(-12,0.3f);
        setTextureId(venus);
        planets.add(venus);
        sun.addSatellite(venus,currentOrbit,currentState,0.05f);

        currentOrbit+=step0;
        currentState+=stepState;


        earth = new Planet(context, new Coord3d(1,1,1),0.2f,30,R.drawable.earth_big);
        earth.setRGB(3,1,1,1);
        earth.setFi(12,1.3f);
        earth.setTeta(180,0.0f);
        setTextureId(earth);
        planets.add(earth);

        Planet moon = new Planet(context, new Coord3d(1,1,1),0.1f,20,R.drawable.moon);
        moon.setRGB(3,1,1,1);
        moon.setFi(12,0.2f);
        setTextureId(moon);
        planets.add(moon);
        earth.addSatellite(moon,0.8f,0,0.4f);
        sun.addSatellite(earth,currentOrbit,currentState,0.04f);

        currentOrbit+=step0;
        currentState+=stepState;


        mars = new Planet(context, new Coord3d(1,1,1),0.15f,26,R.drawable.mars);
        mars.setRGB(3,1,1,1);
        mars.setFi(-2,0.2f);
        mars.setTeta(180,0.0f);
        setTextureId(mars);
        planets.add(mars);
        sun.addSatellite(mars,currentOrbit,currentState,0.02f);

        currentOrbit+=step0;
        currentState+=stepState;

        jupiter = new Planet(context, new Coord3d(1,1,1),0.35f,30,R.drawable.jupiter);
        jupiter.setRGB(3,1,1,1);
        jupiter.setFi(-2,1.6f);
        jupiter.setTeta(180,0.0f);
        setTextureId(jupiter);
        planets.add(jupiter);
        sun.addSatellite(jupiter,currentOrbit,currentState,0.008f);

        currentOrbit+=step0;
        currentState+=stepState;

        saturn = new Planet(context, new Coord3d(0,0,-2),0.28f,26,R.drawable.saturn, true);
        saturn.setRGB(3,1,1,1);
        saturn.setFi(-2,1.6f);
        saturn.setTeta(180,0.0f);
        setTextureId(saturn);
        planets.add(saturn);

        sun.addSatellite(saturn,currentOrbit,currentState,0.09f);

        currentOrbit+=step0;
        currentState+=stepState;

        uranus = new Planet(context, new Coord3d(1,1,1),0.20f,20,R.drawable.uranus);
        uranus.setRGB(3,1,1,1);
        uranus.setFi(-2,1.6f);
        uranus.setTeta(180,0.0f);
        setTextureId(uranus);
        planets.add(uranus);
        sun.addSatellite(uranus,currentOrbit,currentState,0.008f);

        currentOrbit+=step0;
        currentState+=stepState;

        neptune = new Planet(context, new Coord3d(1,1,1),0.19f,20,R.drawable.neptune);
        neptune.setRGB(3,1,1,1);
        neptune.setFi(-2,1.6f);
        neptune.setTeta(180,0.0f);
        setTextureId(neptune);
        planets.add(neptune);
        sun.addSatellite(neptune,currentOrbit,currentState,0.008f);

        currentOrbit+=step0;
        currentState+=stepState;

        pluto = new Planet(context, new Coord3d(1,1,1),0.06f,16,R.drawable.pluto);
        pluto.setRGB(3,1,1,1);
        pluto.setFi(-2,1.6f);
        pluto.setTeta(180,0.0f);
        setTextureId(pluto);
        planets.add(pluto);
        sun.addSatellite(pluto,currentOrbit,currentState,0.008f);


        currentOrbit+=step0;


    }



}