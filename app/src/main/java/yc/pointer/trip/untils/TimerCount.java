package yc.pointer.trip.untils;

import android.os.CountDownTimer;
import android.widget.Button;

public class TimerCount extends CountDownTimer{

	private Button button;
	
	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

	public TimerCount(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	public void onFinish() {
		button.setText("获取验证码");
		button.setClickable(true);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		button.setClickable(false);
		button.setText(millisUntilFinished / 1000 + "s");
	}

}
