package mn.custom.login;

import java.util.HashMap;
import java.util.Map;

import mn.custom.mongoliancustoms.MainActivity;
import mn.custom.mongoliancustoms.R;
import mn.custom.utils.CustomRequest;
import mn.custom.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class LoginAc extends Activity implements OnClickListener {
	private EditText username;
	private EditText password;
	private Button login;
	private LinearLayout remLin;
	private CheckBox remCheck;
	private RequestQueue mRequestQueue;
	private ProgressDialog loginDialog;
	private SharedPreferences sp;
//	private DatabaseHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		sp = getSharedPreferences("login", MODE_PRIVATE);
		init();
		// init view
	}

	private void init() {
		username = (EditText) findViewById(R.id.loginUser);
		password = (EditText) findViewById(R.id.loginPass);
		login = (Button) findViewById(R.id.loginLogin);
		remLin = (LinearLayout) findViewById(R.id.loginRemLin);
		remCheck = (CheckBox) findViewById(R.id.loginRemCheck);
		login.setOnClickListener(this);

		remLin.setOnClickListener(this);

		username.setText(sp.getString("user", ""));
		password.setText(sp.getString("pass", ""));
		if (sp.getString("user", "").length() > 2)
			remCheck.setChecked(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == login) {
			if (login.getText().toString().trim().length() < 2) {
				Toast.makeText(LoginAc.this, getString(R.string.usernameError),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (password.getText().toString().length() < 2) {
				Toast.makeText(LoginAc.this, getString(R.string.passwordError),
						Toast.LENGTH_SHORT).show();
				return;
			}
			// if(Utils.isNetworkAvailable(this)){
			login(username.getText() + "", password.getText() + "");
			// }
		}
		if (v == remLin) {
			if (remCheck.isChecked())
				remCheck.setChecked(false);
			else
				remCheck.setChecked(true);
		}
	}

	private void login(String user, String pass) {
		Editor editor = sp.edit();
		if (remCheck.isChecked()) {

			editor.putString("user", user);
			editor.putString("pass", pass);
		} else
			editor.clear();
		editor.commit();
		mRequestQueue = Volley.newRequestQueue(this);
		loginDialog = Utils.prog(LoginAc.this, getString(R.string.loading));

		CustomRequest logRequest = new CustomRequest(Method.POST,
				getString(R.string.mainIp) + "login.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							if (response.getInt("error_number") == 1000) {
								loggedIn(response.getJSONObject("data"));
							} else {
								error(R.string.loginError);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						NetworkResponse networkResponse = error.networkResponse;
						if (networkResponse == null) {
							error(R.string.unauthorized);
						} else
							error(R.string.loginError);
					}

				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("user", username.getText().toString().trim());
				params.put("pass", password.getText().toString());
				params.put("ip", "0.0.0.0");
				return params;
			}

		};
		mRequestQueue.add(logRequest);

	}

	private void error(int id) {
		loginDialog.dismiss();

		Toast.makeText(LoginAc.this, getString(id), Toast.LENGTH_SHORT).show();
	}

	private void loggedIn(JSONObject user) {
//		helper = new DatabaseHelper(this);
		Editor edit=sp.edit();
		
		edit.putString("uname", user.optString("USERNM"));
		edit.putString("userCode", user.optString("CSTMCD"));
		edit.commit();
		// User cus=new User();
		// cus.id=user.optString("USERID");
		// cus.fname=user.optString("USERNM");
		//
		// cus.lname=user.optString("REGUSERSNM");
		// cus.register=user.optString("USERPIN");
		// cus.lang=user.optString("DEFAULTLANGFGCD");
		// cus.CSTMCD=user.optString("CSTMCD");
		// cus.mobile=user.optString("USERCELLPHONENO");
		// cus.tel=user.optString("USERTELNO");
		// cus.address=user.optString("USERADDR");
		// cus.email=user.optString("USEREMAIL");
		// cus.pageCount=user.optString("USERPAGECOUNT");
		// try {
		// helper.deleteUser();
		// helper.getUserDao().create(cus);
		//
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		loginDialog.dismiss();
		finish();
		startActivity(new Intent(LoginAc.this, MainActivity.class));
		overridePendingTransition(
				android.support.v7.appcompat.R.anim.abc_fade_in,
				android.support.v7.appcompat.R.anim.abc_fade_out);

	}
}
