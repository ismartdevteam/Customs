package mn.custom.splash;

import mn.custom.login.LoginAc;
import mn.custom.mongoliancustoms.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashAc extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
				startActivity(new Intent(SplashAc.this, LoginAc.class));
		
			}
		}, 2000);
	}

}
