package de.ahans.kohonencolors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.View;

public class NeuronView extends View {

	private int numRows = 20;
	private int numCols = 20;

	private int[][][] mColors;
	private int[] mInput;
	private int[] mBestMatchingNeuron;

	public NeuronView(Context context) {
		super(context);
		mColors = null;
	}

	public void setColors(int[][][] colors) {
		mColors = colors;
		numRows = colors.length;
		numCols = colors[0].length;
	}

	public void setBestMatchingNeuron(int[] bestMatching) {
		mBestMatchingNeuron = bestMatching;
	}

	public void setInput(int[] input) {
		mInput = input;
	}

	@Override
	public void draw(Canvas canvas) {
		Rect rect = canvas.getClipBounds();
		int minWidth = rect.width() < rect.height() ? rect.width() : rect.height();
		// calc max tile width
		int tileWidth = minWidth / numRows;
		int tileHeight = tileWidth;

		int offsetX = (rect.width() - tileWidth*numCols) / 2;
		int offsetY = (rect.height() - tileHeight*numRows) / 2;

		Paint paint = new Paint();
		Paint ovalPaint = new Paint();
		ovalPaint.setColor(Color.BLACK);
		ovalPaint.setStyle(Style.STROKE);
		ovalPaint.setStrokeWidth(2f);

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				paint.setColor(0xff000000 | (mColors[i][j][0] << 16) | (mColors[i][j][1] << 8) | mColors[i][j][2]);
				canvas.drawRect(new Rect(j*tileWidth+offsetX, i*tileHeight+offsetY, (j+1)*tileWidth+offsetX, (i+1)*tileHeight+offsetY), paint);
			}
		}

		if (mBestMatchingNeuron != null) {
//			canvas.drawOval(new RectF(new Rect(mBestMatchingNeuron[0]*tileWidth+offsetX, mBestMatchingNeuron[1]*tileHeight+offsetY,
//					(mBestMatchingNeuron[0]+1)*tileWidth+offsetX, (mBestMatchingNeuron[1]+1)*tileHeight+offsetY)), ovalPaint);
			canvas.drawRect(new Rect(mBestMatchingNeuron[0]*tileWidth+offsetX, mBestMatchingNeuron[1]*tileHeight+offsetY,
					(mBestMatchingNeuron[0]+1)*tileWidth+offsetX, (mBestMatchingNeuron[1]+1)*tileHeight+offsetY), ovalPaint);
		}

//		if (mInput != null) {
//			paint.setColor(0xff000000 | (mInput[0] << 16) | (mInput[1] << 8) | mInput[2]);
//			canvas.drawRect(new Rect(offsetX, offsetY+numRows*tileHeight+10, offsetX+20, offsetY+numRows*tileHeight+30), paint);
//		}
	}
}
