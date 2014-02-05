package com.tobolkac.triviaapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

/**
 * TODO: document your custom view class.
 */
public class CorrectQuestionView extends View {

	//Canvas canvas;
	
	int numCorrect = -1;
	
	int[] correctArray;
	
	boolean questionProgress = false;
	
	boolean displayResults = false;
	
	public Animation anim;
	
	public void setQuestionProgress(boolean b)
	{
		this.questionProgress = b;
	}
	
	public int getNumCorrect()
	{
		return numCorrect;
	}
	
	public void setNumCorrect(int n)
	{
		this.numCorrect = n;
		invalidate();
		requestLayout();
	}
	public void setDisplayResults(boolean b)
	{
		this.displayResults = b;
	}
	
	public void setCorrectArray(int[] ca)
	{
		this.correctArray = ca;
	}
	
	public CorrectQuestionView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	private void createAnimation(Canvas canvas) {
        anim = new RotateAnimation(0, 360, getWidth()/2, getHeight()/2);
        anim.setDuration(10000L);
        startAnimation(anim);
   }
	
	public CorrectQuestionView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs,
			    R.styleable.CorrectQuestionView);
			 
			final int N = a.getIndexCount();
			for (int i = 0; i < N; ++i)
			{
			    int attr = a.getIndex(i);
			    switch (attr)
			    {
			        case R.styleable.CorrectQuestionView_numCorrect:
			            numCorrect = Integer.getInteger(a.getString(attr));
			            
			    }
			}
			a.recycle();

	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		
		//this.canvas = canvas;
		int centerX = (int)(getWidth()/10);
        int centerY = (int)(getHeight()/2);
        int space = getWidth()/10;
        //black gray thing = 67, 60, 50
        //yellow using = 248, 176, 53
        
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        
        paint.setARGB(200, 10, 10, 5);  
        
        Paint paint2 = new Paint();
        paint2.setFlags(Paint.ANTI_ALIAS_FLAG);
        
        paint2.setARGB(200, 248, 176, 53);
        
        Paint paint3 = new Paint();
        paint3.setFlags(Paint.ANTI_ALIAS_FLAG);
        
        paint3.setARGB(50, 67, 60, 50);  
        
        Paint paint4 = new Paint();
        paint4.setFlags(Paint.ANTI_ALIAS_FLAG);
        //green color for correct answer
        paint4.setARGB(200, 73, 173, 16);
        if(!questionProgress && !displayResults)
		{
	        for (int i = 0; i<10; i++)
	        {
	        	
	        	canvas.drawCircle(centerX+(i*32), centerY,13, paint3);
	        	
	        }
	        //numCorrect = 5;
	        if (numCorrect != -1)
	        {	
	        	int i = 0;
	        	for (i = 0; i<numCorrect; i++)
	            {
	        		//canvas.drawCircle(centerX+(i*32)-3, centerY-3,13, paint3);
	        		canvas.drawCircle(centerX+(i*32), centerY,13, paint2);
	        	}
	        	for (int j = i; j<10; j++)
	            {
	        		//canvas.drawCircle(centerX+(i*32)-3, centerY-3,13, paint3);
	        		//canvas.drawCircle(centerX+(j*32), centerY,13, paint);
	        	}
	        }
	        //canvas.drawCircle(centerX, centerY,10, paint);
		}
        else if(questionProgress) //using to kepp track of question progress
        {
        	for (int i = 0; i<correctArray.length; i++)
	        {
	        	canvas.drawCircle(centerX+(i*32), centerY,13, paint3);
	        	
	        }
        	if (numCorrect > 0)
	        {	
	        	int i = 0;
	        	for (i = 0; i<numCorrect; i++)
	            {
	        		//canvas.drawCircle(centerX+(i*32)-3, centerY-3,13, paint3);
	        		if (i==(numCorrect-1))
	        		{
	        			canvas.drawCircle(centerX+(i*32), centerY,13, paint);
	        		}
	        		
	        		if (correctArray[i] == 1)
	        		{
	        			canvas.drawCircle(centerX+(i*32), centerY,13, paint4);
	        		}
	        		else
	        		{
	        			
	        		}
	        	}
	        	
	        }
        }
        else if(displayResults)
        {
        	for (int i = 0; i<correctArray.length; i++)
	        {
	        	canvas.drawCircle(centerX+(i*32), centerY,13, paint3);
	        	
	        }
        	if (numCorrect > 0)
	        {	
	        	int i = 0;
	        	for (i = 0; i<correctArray.length; i++)
	            {
	        		
	        		
	        		if (correctArray[i] == 1)
	        		{
	        			canvas.drawCircle(centerX+(i*32), centerY,13, paint4);
	        		}
	        		else
	        		{
	        			
	        		}
	        	}
	        	
	        }
        }
	}
	
	public void drawCorrect(int number)
	{
		
		int centerX = (int)(getWidth()/10);
        int centerY = (int)(getHeight()/2);
        int space = getWidth()/10;
        //black gray thing = 67, 60, 50
        //yellow using = 248, 176, 53
        Paint paint = new Paint();
        paint.setARGB(100, 248, 176, 53);  //The square will draw in the color blue now
       
        for (int i = 0; i<10; i++)
        {
        	//canvas.drawCircle(centerX+(i*25), centerY,10, paint);
        }
	}

	
}
