package de.ahans.kohonencolors;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class KohonenColors extends Activity implements OnSeekBarChangeListener{

	private boolean mPaused;
	private KohonenMapThread mThread;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NeuronView view = new NeuronView(this);
        mThread = new KohonenMapThread(view);
        mPaused = true;

        SeekBar seekBar = new SeekBar(this);
        seekBar.setOnSeekBarChangeListener(this);

        RelativeLayout layout = new RelativeLayout(this);

        layout.addView(view, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        layout.addView(seekBar, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));


        setContentView(layout);
        new Thread(mThread).start();
    }

    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    	mPaused = !mPaused;
	    	synchronized (mThread) {
	    		mThread.notifyAll();
			}
    	}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	public void onBackPressed() {
		mThread.reset();
		mPaused = true;
	}

	// menu handling
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.small:
	    	mThread.setSize(KohonenMapThread.SIZE_SMALL);
	        return true;
	    case R.id.medium:
	    	mThread.setSize(KohonenMapThread.SIZE_MEDIUM);
	        return true;
	    case R.id.large:
	    	mThread.setSize(KohonenMapThread.SIZE_LARGE);
	        return true;

//	    case R.id.slow:
//	    	mThread.setSpeed(KohonenMapThread.SPEED_SLOW);
//	    	return true;
//	    case R.id.fast:
//	    	mThread.setSpeed(KohonenMapThread.SPEED_FAST);
//	    	return true;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}


	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
		mThread.setSpeed((int)Math.round(Math.pow(1.08, (double)progress)));
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
	}

	class KohonenMapThread implements Runnable {
    	KohonenNet mNet;
    	private int[][][] mColors;
    	private int[] mInput;
    	NeuronView mView;
    	private Random mRandom;

    	public static final int SIZE_SMALL = 0;
    	public static final int SIZE_MEDIUM = 1;
    	public static final int SIZE_LARGE = 2;
    	private final int[] SIZES = { 20, 40, 60 };
    	private int mSize;

//    	public static final int SPEED_SLOW = 0;
//    	public static final int SPEED_FAST = 1;
//    	private final int[] DELAYS = { 200, 20 };
    	private int mSpeed;

    	public KohonenMapThread(NeuronView view) {
    		mView = view;
    		mSize = SIZE_SMALL;
//    		mSpeed = SPEED_SLOW;
    		mSpeed = 2000;
    		reset();
    	}

    	public void setSize(int size) {
    		if (size >= SIZE_SMALL && size <= SIZE_LARGE && mSize != size) {
    			mPaused = true;
    			mSize = size;
    			reset();
    		}
    	}

    	public void setSpeed(int speed) {
    		/*if (speed >= SPEED_SLOW && speed <= SPEED_FAST)*/ mSpeed = speed;
    	}

    	public void reset() {
    		mColors = new int[SIZES[mSize]][SIZES[mSize]][3];
    		mView.setColors(mColors);
    		mInput = new int[3];
    		mView.setInput(mInput);
    		mRandom = new Random();
    		mNet = new KohonenNet(SIZES[mSize], SIZES[mSize], 3);
    		updateColors(false, null);
    	}

    	private void updateColors(boolean setBestMatching, float[] input) {
    		for (int i = 0; i < SIZES[mSize]; i++) {
    			for (int j = 0; j < SIZES[mSize]; j++) {
    				for (int c = 0; c < 3; c++) {
    					mColors[i][j][c] = (int) Math.floor((double) mNet.weights[i][j][c] * 256.0);
    				}
    			}
    		}
    		if (input != null) {
    			for (int c = 0; c < 3; c++) mInput[c] = (int) Math.floor((double) input[c] * 256.0);
    			mView.setInput(mInput);
    		}
    		mView.setBestMatchingNeuron(setBestMatching ? mNet.getLastBestMatching() : null);
    		mView.postInvalidate();
    	}

		@Override
		public void run() {
			float[] input = new float[3];
			while (true) {
				while (mPaused) {
					try {
						synchronized (this) {
							wait();
						}
					} catch (InterruptedException e) {}
				}
				for (int i = 0; i < 3; i++) input[i] = mRandom.nextFloat();
				mNet.learn(input);
				updateColors(true, input);
				try {
//					Thread.sleep(DELAYS[mSpeed]);
					Thread.sleep(mSpeed);
				} catch (InterruptedException ex) {}
			}
		}
    }
}