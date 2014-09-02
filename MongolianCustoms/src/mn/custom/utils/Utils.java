package mn.custom.utils;

import mn.custom.mongoliancustoms.R;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Utils {
	@SuppressLint("InlinedApi")
	public static ProgressDialog prog(Context con, String message) {
		ProgressDialog dialog = new ProgressDialog(con,
				ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
		dialog.setMessage(message);
		dialog.show();
		return dialog;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			return true;
		} else {
			Toast.makeText(context, context.getString(R.string.noNet),
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public static NetworkInfo network(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo;

	}
}
