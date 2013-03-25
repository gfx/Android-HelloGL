package com.example.hellogl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	MyGLView myGLView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("XXX", "onCreate");
		myGLView = new MyGLView(this);
		setContentView(myGLView);
	}

	@Override
	protected void onResume(){
		super.onResume();
		myGLView.onResume();
	}

	@Override
	protected void onPause(){
		super.onPause();
		myGLView.onPause();
	}

	private static void checkError(final GL10 gl, final String tag) {
		final int errorCode = gl.glGetError();
		if (errorCode != GL10.GL_NO_ERROR) {
			Log.e(tag, GLU.gluErrorString(errorCode), new Throwable());
		}
	}


	class MyGLView extends GLSurfaceView {
		MyRenderer myRenderer;

		public MyGLView(Context context) {
			super(context);

			myRenderer = new MyRenderer();
			setRenderer(myRenderer);
		}
	}
	private static FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	private static ShortBuffer makeShortBuffer(short[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer ib = bb.asShortBuffer();
		ib.put(arr);
		ib.position(0);
		return ib;
	}

	class MyRenderer implements Renderer {
		private FloatBuffer vertexBuffer = makeFloatBuffer(new float[]{
					-3f,-3f,0,
					 3f,-3f,0,
					 3f, 3f,0,
					-3f, 3f,0
			});
		private FloatBuffer texBuffer = makeFloatBuffer(new float[]{
				0f,1f,
				1f,1f,
				1f,0f,
				0f,0f
		});
		private ShortBuffer indexBuffer = makeShortBuffer(new short[]{0,1,2,3,0});
		private int vertexCount = 5;

		private int[] texture = new int[1];

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			GLU.gluOrtho2D(gl, -12f, 12f, -20f, 20f);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
					GL10.GL_REPEAT);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
					GL10.GL_REPEAT);

			// set texture
			gl.glGenTextures(1, texture, 0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);

			final Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.box);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			/*
			int textureChecker[] = {
					-1, 0, -1, 0,
					0, -1, 0, -1,
					-1, 0, -1, 0,
					0, -1, 0, -1
			};
			gl.glTexImage2D(
					GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA,
					4, 4, 0,
					GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
					IntBuffer.wrap(textureChecker)
					);
			 */

			checkError(gl, TAG);

			bitmap.recycle();
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

			checkError(gl, TAG);

		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			gl.glViewport(0, 0, width, height);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
					GL10.GL_REPLACE);

			// draw texture
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);

			// draw vertexes
			gl.glFrontFace(GL10.GL_CCW);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
			gl.glDrawElements(GL10.GL_TRIANGLE_FAN, vertexCount, GL10.GL_UNSIGNED_SHORT, indexBuffer);
			gl.glDisable(GL10.GL_TEXTURE_2D);

			checkError(gl, TAG);
		}
	}
}

