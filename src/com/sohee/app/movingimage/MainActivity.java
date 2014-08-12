package com.sohee.app.movingimage;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	final int MODE_CHANGE_ROW = 1;
	final int MODE_MOVE_ROW = 2;
	
	final int DIRECTION_DECREASE = 1;
	final int DIRECTION_INCREASE = 2;
	
	final int BIG_ROW_HEIGHT = 161;
	final int SMALL_ROW_HEIGHT = 72;

	final int SLEEP_TIME_FOR_NEXT_ROW = 1000;
	final int SLEEP_TIME_FOR_INCREASE = 8;
	
	Timer aniTimer = new Timer();
	TimerTask task = null;
	boolean isActive = true;
	int currPoint = 0;
	int[] button_ids = new int[] {
			R.id.start_button1,
			R.id.start_button2,
			R.id.start_button3,
			R.id.start_button4,
			R.id.start_button5,	
			R.id.start_button6,			
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startAnimation();
	}

	private void startAnimation() {
		task = new TimerTask() {
			@Override
			public void run() {
				Message m = new Message();
				m.arg1 = MODE_CHANGE_ROW;
				aniHandler.sendMessage(m);
			}
		};

		aniTimer.schedule(task, 0, SLEEP_TIME_FOR_NEXT_ROW);
	}
	
	Handler aniHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.arg1){
			case MODE_CHANGE_ROW:
				RelativeLayout button = null;
				RelativeLayout button2 = null;
				button = (RelativeLayout)findViewById(button_ids[currPoint]);

				if(currPoint == 5){
					button2 = (RelativeLayout)findViewById(button_ids[0]);
				}else{
					button2 = (RelativeLayout)findViewById(button_ids[currPoint+1]);
				}

				if(isActive){
					if(button != null && button2 != null){
						playAni2(button,button2);
					}
				}
				if(currPoint!=5){
					currPoint++;
				}else{
					currPoint = 0;
				}
				break;
				
			case MODE_MOVE_ROW:
				RelativeLayout bt = (RelativeLayout) msg.obj;
				int i = msg.arg2;
				LinearLayout.LayoutParams b1 = (LayoutParams) bt.getLayoutParams();
				int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

				if(i==DIRECTION_DECREASE){
					b1.height -= h;
				}else if(i==DIRECTION_INCREASE){
					b1.height += h;
				}
				bt.setLayoutParams(b1);
				break;
			}
		}
                
		private void playAni2(final RelativeLayout b, final RelativeLayout b1) {
			new Thread(){
				@Override
				public void run() {
					super.run();
					try {
						Log.d(TAG, "PLAY2, Thread Running");
						for(int j=0; j<89; j++){
							Message m = new Message();
							m.obj = b;
							m.arg1 = MODE_MOVE_ROW;
							m.arg2 = DIRECTION_DECREASE;
							m.what = j;

							Message m2 = new Message();
							m2.obj = b1;
							m2.arg1 = MODE_MOVE_ROW;
							m2.arg2 = DIRECTION_INCREASE;
							m2.what = j;

							aniHandler.sendMessage(m);
							aniHandler.sendMessage(m2);
							sleep(SLEEP_TIME_FOR_INCREASE);
						}

						this.interrupt();
						this.finalize();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		isActive = false;	
	}

	@Override
	protected void onResume() {
		super.onResume();
		isActive = true;
		menuSort(0);

	}
	
	private void menuSort(int startPoint){
		currPoint = startPoint;
		int h = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

		for(int i=0; i<button_ids.length; i++){
			LinearLayout.LayoutParams m = (LayoutParams)findViewById(button_ids[i]).getLayoutParams();
			if(i==startPoint){
				m.height = h*BIG_ROW_HEIGHT;
			}else{
				m.height = h*SMALL_ROW_HEIGHT;
			}
			findViewById(button_ids[i]).setLayoutParams(m);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
