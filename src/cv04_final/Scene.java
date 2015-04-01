/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv04_final;

import com.jogamp.opengl.util.awt.ImageUtil;
import static javax.media.opengl.GL2.*;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_MIRRORED_REPEAT;
import static javax.media.opengl.GL.GL_REPEAT;
import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL.GL_TRIANGLES;
import static javax.media.opengl.GL.GL_UNSIGNED_INT;
import static javax.media.opengl.GL2GL3.GL_FILL;
import static javax.media.opengl.GL2GL3.GL_LINE;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT1;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPOT_DIRECTION;
import static javax.media.opengl.fixedfunc.GLPointerFunc.GL_NORMAL_ARRAY;
import static javax.media.opengl.fixedfunc.GLPointerFunc.GL_VERTEX_ARRAY;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 *
 * @author dvdkouril
 */
public class Scene implements GLEventListener
{
    private GLU glu;
    private GLUT glut;
    private ObjLoader model;
    
    private float time;
    private float amplitude;
    private float radius;
    private float azimuth;
    private float altitude;
    private float[] cartesian;
    private float scale;
    private float[] lightPos;
    private float[] lightPos2;
    
    public float mouseX, mouseY;
    public float cameraAngleX = 0;
    public float cameraAngleY = 0;
    
    private boolean mouseDown = false;
    
    private Texture[] dice = new Texture[6];
    private Texture wood;
    private Texture earthTex;
    
    public Scene()
    {
        //model = new ObjLoader("/resources/can.obj");
        //model = new ObjLoader("\\Users\\dvdthepmkr\\Downloads\\EX3_final\\src\\ex3_final\\resources\\box.obj");
        
        //model.load();
        
        cartesian = new float[3];
        lightPos = new float[]{ 4, 4, 4, 1};
        lightPos2 = new float[]{ 4, 4, -4, 1};
        time = 0;
        amplitude = 2;
        radius = 4;
        azimuth = 0;
        altitude = 0;
    }

    @Override
    public void init(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        
        glu = new GLU();
        glut = new GLUT();
                
       //  Povolit svetla
       gl.glEnable(GL_LIGHTING);
       gl.glEnable(GL_LIGHT0);
       gl.glEnable(GL_LIGHT1);
       gl.glEnable(GL_NORMALIZE);
       gl.glEnable(GL_TEXTURE_2D);
       gl.glEnable(GL_CULL_FACE);
       gl.glCullFace(GL_BACK);
       
       gl.glClearDepth(1.0f);
       //gl.glClearColor(.6f,.6f,.6f,1);
       gl.glClearColor(.1f, .1f, .1f, 1);
       
       //   Povolit Depth test
       gl.glEnable(GL_DEPTH_TEST);

       //gl.glShadeModel(GL_FLAT);
       
       //gl.glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);
       
       // TEXTURES
       //dice1 = loadTexture(gl, this.getClass().getResource("/resources/dice1.png"), TextureIO.PNG);
       String name = "";
       try {
          
          for (int i = 0; i < 6; i++) {
              name = "/resources/dice" + (i + 1) + ".png";
              dice[i] = loadTexture(gl, name, null);
          }
          name = "/resources/wood.jpg";
          wood = loadTexture(gl, name, null);
          name = "/resources/earthmap1k.jpg";
          earthTex = loadTexture(gl, name, null);
       } catch (IOException ex) {
           System.err.println("File not found:" + name);
       }
       
       //   Light 1
       float[] diffuseLight1 = {0.0f, 0.1f, 0.99f}; 
       float[] ambientLight1 = {0.1f, 0.1f, 0.1f}; 
       float[] specularLight1 = {0.0f, 0.0f, 0.99f};
       
       //gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLight1, 0);
       //gl.glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight1, 0);
//       gl.glLightfv(GL_LIGHT0, GL_SPECULAR, specularLight1, 0);
       
       //   Light 2
       float[] diffuseLight2 = {0.88f, 0.88f, 0.0f}; 
       float[] ambientLight2 = {1.0f, 1.0f, 1.0f}; 
       float[] specularLight2 = {0.78f, 0.78f, 0.0f};

       gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, new float[]{1.0f, 1.0f, 1.0f}, 0);      
              
       gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[]{1.0f, 1.0f, 1.0f}, 0);
       gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, new float[]{1.0f, 1.0f, 1.0f}, 0);
       gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, new float[]{1.0f, 1.0f, 1.0f}, 0);
       gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 100);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) 
    {
    }

    @Override
    public void display(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        time += 0.01;
        
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();        
                
        //gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        //System.out.println("Mouse Pressed: " + mouseDown);
        System.out.println("cameAngleX " + cameraAngleX);
        
        //  Camera
        polarCoordinates(azimuth, altitude, cartesian);
        
        glu.gluLookAt(cartesian[0], cartesian[1], cartesian[2], 0, 0, 0, 0, 1, 0);     

        gl.glRotatef(cameraAngleX, 1, 0, 0);
        gl.glRotatef(cameraAngleY, 0, 1, 0);
        // Lights
        gl.glPushMatrix();
            gl.glTranslatef(0, 5.0f, 0);
            gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[]{0.0f, 0.0f, 0.0f, 1}, 0);
        gl.glPopMatrix();
        gl.glPushMatrix();
            gl.glRotatef(time*50, 0, 1, 0);
            gl.glTranslatef(20.0f, 0.0f, 0);
            gl.glLightfv(GL_LIGHT1, GL_POSITION, new float[]{0.0f, 0.0f, 0.0f, 1}, 0);
            glut.glutSolidSphere(1.0f, 10, 10);
        gl.glPopMatrix();
        //wood.bind(gl);
        earthTex.bind(gl);
        GLUquadric earth = glu.gluNewQuadric();
        gl.glPushMatrix();
        glu.gluQuadricTexture(earth, true);
        glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
        glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
        gl.glRotatef(90, 1, 0, 0);
        //glu.gluSphere(earth, 5.0f, 16, 16);
        glu.gluDeleteQuadric(earth);
        gl.glPopMatrix();
        //drawObj(gl, model);
        
        // Draw Floor
        //wood.bind(gl);
        gl.glPushMatrix();
        gl.glScalef(5, 5, 5);
        wood.bind(gl);
        
        //drawTexturedFloor1(gl);
        //drawTexturedFloor0(gl);
        gl.glPopMatrix();
        
        drawFloor(gl, -10, 10, -10, 10, 0.5f, 0.5f);
        
        // Draw cube
        /*gl.glPushMatrix(); 
        gl.glScalef(5, 5, 5);
        drawCube(gl);
        gl.glPopMatrix();*/
        
        gl.glScalef(5, 5, 5);
        drawAxes(gl, 2);
       
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }
    
    private void drawTexturedFloor0(GL2 gl) {
        gl.glBegin(GL_QUADS);
            gl.glNormal3f(0, 1, 0);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-1.0f, 0, -1.0f);
            gl.glTexCoord2f(0, 0.5f);
            gl.glVertex3f(-1.0f, 0, 0.0f);
            gl.glTexCoord2f(0.5f, 0.5f);
            gl.glVertex3f(0.0f, 0, 0.0f);
            gl.glTexCoord2f(0.5f, 1);
            gl.glVertex3f(0.0f, 0, -1.0f);
            
            //right top
            gl.glTexCoord2f(0.5f, 0.5f);
            gl.glVertex3f(0.0f, 0, 0.0f);
            gl.glTexCoord2f(1, 0.5f);
            gl.glVertex3f(1.0f, 0, 0.0f);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3f(1.0f, 0, -1.0f);
            gl.glTexCoord2f(0.5f, 1);
            gl.glVertex3f(0.0f, 0, -1.0f);
            
            // bottom right  
            gl.glTexCoord2f(0.5f, 0.5f);
            gl.glVertex3f(0.0f, 0, 0.0f);
            gl.glTexCoord2f(0.5f, 0);
            gl.glVertex3f(0.0f, 0, 1.0f);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3f(1.0f, 0, 1.0f);
            gl.glTexCoord2f(1, 0.5f);
            gl.glVertex3f(1.0f, 0, 0.0f);
            
            // left bottom
            gl.glTexCoord2d(0, 0);
            gl.glVertex3f(-1.0f, 0, 1.0f); 
            gl.glTexCoord2f(0.5f, 0);
            gl.glVertex3f(0.0f, 0, 1.0f);
            gl.glTexCoord2f(0.5f, 0.5f);
            gl.glVertex3f(0.0f, 0, 0.0f);
            gl.glTexCoord2f(0, 0.5f);
            gl.glVertex3f(-1.0f, 0, 0.0f);
        gl.glEnd();
        gl.glPopMatrix();
    }
    
    private void drawTexturedFloor1(GL2 gl) {
       gl.glBegin(GL_QUADS);
            gl.glNormal3f(0, 1, 0);
            gl.glTexCoord2d(0, 2);
            gl.glVertex3f(-1.0f, 0, -1.0f);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3f(-1.0f, 0, 0.0f);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3f(0.0f, 0, 0.0f);
            gl.glTexCoord2d(0, 2);
            gl.glVertex3f(0.0f, 0, -1.0f);
            
            
            gl.glVertex3f(0.0f, 0, 0.0f);
            gl.glVertex3f(1.0f, 0, 0.0f);
            gl.glVertex3f(1.0f, 0, -1.0f);
            gl.glVertex3f(0.0f, 0, -1.0f);
            
            gl.glTexCoord2d(0, 0);
            gl.glVertex3f(-1.0f, 0, 1.0f);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3f(0.0f, 0, 1.0f);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3f(0.0f, 0, 0.0f);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3f(-1.0f, 0, 0.0f);
            
            gl.glVertex3f(0.0f, 0, 0.0f);
            gl.glVertex3f(0.0f, 0, 1.0f);
            gl.glVertex3f(1.0f, 0, 1.0f);
            gl.glVertex3f(1.0f, 0, 0.0f);
        gl.glEnd();
        gl.glPopMatrix();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        GL2 gl = drawable.getGL().getGL2();
        
        gl.glShadeModel(GL_SMOOTH);
        
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(55, (float)width/height, 1, 1000);
        
        gl.glViewport(0, 0, width, height);
        
        gl.glMatrixMode(GL_MODELVIEW);
        
    }
    
    private void drawAxes(GL2 gl, float size) {
        // Store OpenGL attributes, e.g., previous state of lighting
	gl.glPushAttrib(GL_ALL_ATTRIB_BITS);

	gl.glDisable(GL_LIGHTING);
        gl.glLineWidth(3.0f);

	gl.glBegin(GL_LINES);

        // X - red
        gl.glColor3f(1,0,0);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(size, 0.0f, 0.0f);
        // Y - green
        gl.glColor3f(0,1,0);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(0.0f, size, 0.0f);
        // Z - blue
        gl.glColor3f(0,0,1);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, size);

        gl.glEnd();

	// Restore OpenGL attributes
	gl.glPopAttrib();
    }
    
    private void drawCube(GL2 gl) {

        //gl.glActiveTexture(GL_TEXTURE0);
        //dice1.bind(gl);
        //dice1.enable(gl);
        earthTex.bind(gl);
        
        gl.glBegin(GL_QUADS);
        
        TextureCoords tc = earthTex.getImageTexCoords();
        
        //predna stena
        gl.glNormal3f(0, 0, 1f);
        //gl.glTexCoord2f(0.0f, 0.0f);
        gl.glTexCoord2f(tc.left(), tc.bottom());
        gl.glVertex3f(-1f, -1f, 1f);
        
//        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glTexCoord2f(tc.right(), tc.bottom());
        gl.glVertex3f(1f, -1f, 1f);
        
//        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glTexCoord2f(tc.right(), tc.top());
        gl.glVertex3f(1f, 1f, 1f);
        
//        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glTexCoord2f(tc.left(), tc.top());
        gl.glVertex3f(-1f, 1f, 1f);
        
        
        //zadna stena
        gl.glNormal3f(0, 0, -1f);
        //gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1f, 1f, -1f);
        
        //gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1f, 1f, -1f);
        
        //gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1f, -1f, -1f);
        
        //gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1f, -1f, -1f);

               
        //prava stena
        gl.glNormal3f(1, 0, 0);
        gl.glVertex3f(1f, -1f, -1f);
        gl.glVertex3f(1f, 1f, -1f);
        gl.glVertex3f(1f, 1f, 1f);
        gl.glVertex3f(1f, -1f, 1f);
        
        //lava stena
        gl.glNormal3f(-1, 0, 0);
        gl.glVertex3f(-1f, -1f, 1f);
        gl.glVertex3f(-1f, 1f, 1f);
        gl.glVertex3f(-1f, 1f, -1f);
        gl.glVertex3f(-1f, -1f, -1f);
        
        //horna stena
        gl.glNormal3f(0, 1,0);
        gl.glVertex3f(-1f, 1f, 1f);
        gl.glVertex3f(1f, 1f, 1f);
        gl.glVertex3f(1f, 1f, -1f);
        gl.glVertex3f(-1f, 1f, -1f);
        
        //dolna stena
        gl.glNormal3f(0, -1, 0);
        gl.glVertex3f(-1f, -1f, -1f);
        gl.glVertex3f(1f, -1f, -1f);
        gl.glVertex3f(1f, -1f, 1f);
        gl.glVertex3f(-1f, -1f, 1f);

        gl.glEnd();
    }
    
    private Texture loadTexture(GL2 gl, String filename, String suffix) throws IOException {
        try {
            InputStream is = Scene.class.getResourceAsStream(filename);
            if (is != null) {
                Texture tex = TextureIO.newTexture(is, true, suffix);
                
                tex.setTexParameteri(gl, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
                tex.setTexParameteri(gl, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                //tex.setTexParameteri(gl, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
                //tex.setTexParameteri(gl, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
                //tex.setTexParameteri(gl, GL_TEXTURE_WRAP_S, GL_REPEAT);
                //tex.setTexParameteri(gl, GL_TEXTURE_WRAP_T, GL_REPEAT);
                tex.setTexParameteri(gl, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                //tex.setTexParameteri(gl, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                
                return tex;
            } else {
                throw new IOException("File not found!");
            }
            
            
        } catch (NullPointerException ex) {
            System.err.println("filename was null!");
        }
        return null;
    }
    
    private void drawFloor(GL2 gl, float x_from, float x_to, float z_from, float z_to, float x_step, float z_step) {
        
        //wood.bind(gl);
        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        int z_steps = (int)((x_to - x_from) / x_step);
        int x_steps = (int)((z_to - z_from) / z_step);
        //System.out.println("x_steps = " + x_steps + "; z_steps = " + z_steps);
        for (float z = z_from; z < z_to; z += z_step) {
            gl.glBegin(GL_QUAD_STRIP);
            for (float x = x_from; x <=  x_to; x += x_step) {
                gl.glTexCoord2d((x + ((x_to - x_from) / 2) ) / (x_to - x_from), (z + ((z_to - z_from) / 2) ) / (z_to - z_from));
                gl.glVertex3f(x, 0, z);
                
                gl.glTexCoord2d((x + ((x_to - x_from) / 2) ) / (x_to - x_from), (z + z_step + ((x_to - x_from) / 2) ) / (z_to - z_from));
                gl.glVertex3f(x, 0, z + z_step);
            }
            gl.glEnd();
        }
    }
    
    private void drawAxis(GL2 gl)
    {
        gl.glBegin(GL_LINES);
        
        
        
        gl.glEnd();
    }
    
    private void polarCoordinates(float azimuth, float altitude, float[] cartesian)
    {
        float a = (float) (20 * Math.cos(Math.toRadians(altitude)));
        cartesian[0] = (float) (a * Math.cos(Math.toRadians(azimuth)));
        cartesian[1] = (float) (20 * Math.sin(Math.toRadians(altitude)));
        cartesian[2] = (float) (a * Math.sin(Math.toRadians(azimuth)));
    }
    
    public void setAzimuth(float val)
    {
        this.azimuth = val;
    }
    
    public void setAltitude(float val)
    {
        this.altitude = val;
    }
    public void setMouseDown(boolean mousedwn) {
        mouseDown = mousedwn;
    }
    
    public boolean getMouseDown() {
        return mouseDown;
    }
    
    private void drawObj(GL2 gl, ObjLoader model)
    {
        gl.glShadeModel(GL_SMOOTH);
            for(int i = 0; i < model.getVertexIndices().size(); i++)
            {
                
                int[] index = model.getVertexIndices().get(i);
                int[] normalIndex = model.getNormalIndices().get(i);
                
                gl.glBegin(GL_TRIANGLES);    
                    float[] vertex = model.getVertices().get(index[0]);
                    float[] normal = model.getNormals().get(normalIndex[0]);
                    gl.glNormal3f(normal[0], normal[1], normal[2]);
                    gl.glVertex3f(vertex[0],vertex[1],vertex[2]);
                    
                    
                    vertex = model.getVertices().get(index[1]);
                    normal = model.getNormals().get(normalIndex[1]);
                    gl.glNormal3f(normal[0], normal[1], normal[2]);
                    gl.glVertex3f(vertex[0],vertex[1],vertex[2]);
                    

                    vertex = model.getVertices().get(index[2]);
                    normal = model.getNormals().get(normalIndex[2]);
                    gl.glNormal3f(normal[0], normal[1], normal[2]);
                    gl.glVertex3f(vertex[0],vertex[1],vertex[2]);
                    
                gl.glEnd();
            }
        
        
        
    }   

}