package mn.custom.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mn.custom.database.Clearance;
import mn.custom.mongoliancustoms.R;
import mn.custom.mongoliancustoms.RegisterAc;
import mn.custom.utils.CustomRequest;
import mn.custom.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;

public class CustomClearance extends Fragment implements
		OnMenuItemClickListener {
	ViewGroup v;
	private ListView clearList;
	private List<Clearance> clearDataAr;
	private RequestQueue mRequestQueue;
	private ProgressDialog loadDialog;
	private SharedPreferences sp;
	private ClearAdapter adapter;
	public static final String DATEPICKER_TAG = "datepicker";
	private String sdate;
	private String edate;
	private Calendar c;
	private Dialog dateChoosedialog;
	private int mYear;
	private int mMonth;
	private int mDay;
	private SimpleDateFormat format;

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		clearDataAr = new ArrayList<Clearance>();
		c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(mYear - 1900, mMonth, mDay);
		sdate = edate = DateFormat.format("yyyy-MM-dd", date).toString();

		mRequestQueue = Volley.newRequestQueue(getActivity());

		sp = getActivity().getSharedPreferences("login", 0);
		getData(sdate, edate, "mn", null, null);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
	
		inflater.inflate(R.menu.clear_menu	, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}




	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		if (id == R.id.menu_add) {
			startActivity(new Intent(getActivity(), RegisterAc.class));
			return true;
		}
		if (id == R.id.menu_filter) {
			dateChoosedialog = new Dialog(getActivity(), R.style.CustomDialogTheme);
			dateChoosedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dateChoosedialog.setContentView(R.layout.filter_dialog);

			final TextView dSdate = (TextView) dateChoosedialog
					.findViewById(R.id.dateDialogSdate);
			final TextView dEdate = (TextView) dateChoosedialog
					.findViewById(R.id.dateDialogEdate);
			final EditText reportEdit = (EditText) dateChoosedialog
					.findViewById(R.id.filter_report);
			final EditText manifestEdit = (EditText) dateChoosedialog
					.findViewById(R.id.filter_manifest);
			Button ok = (Button) dateChoosedialog
					.findViewById(R.id.dateDialogOk);
			dSdate.setText(sdate);
			dEdate.setText(edate);

			dSdate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Date d;
					try {
						d = format.parse(dSdate.getText() + "");
						showCal(dSdate, d);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			dEdate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// showCal(dEdate);
					Date d;
					try {
						d = format.parse(dEdate.getText() + "");
						showCal(dEdate, d);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					sdate = dSdate.getText() + "";
					edate = dEdate.getText() + "";
					String reportStr = null;
					if (reportEdit.getText().toString().length() > 2)
						reportStr = reportEdit.getText().toString();
					String manifestStr = null;
					if (manifestEdit.getText().toString().length() > 2)
						manifestStr = manifestEdit.getText().toString();
					getData(sdate, edate, "mn", reportStr, manifestStr);
					dateChoosedialog.dismiss();
				}
			});
			dateChoosedialog.show();
			return true;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = (ViewGroup) inflater.inflate(R.layout.clearance, container, false);
		clearList = (ListView) v.findViewById(R.id.clearanceList);
		Locale locale = new Locale("mn");
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getActivity().getResources().updateConfiguration(config,
				getActivity().getResources().getDisplayMetrics());
		return v;
	}

	private void showCal(final TextView tv, Date date) {
		c.setTime(date);

		DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePickerDialog datePickerDialog,
							int year, int month, int day) {
						// TODO Auto-generated method stub
						String monthStr = (month + 1) + "";
						if (month + 1 < 10)
							monthStr = "0" + (month + 1);
						String dayStr = day + "";
						if (day < 10)
							dayStr = "0" + day;
						tv.setText(year + "-" + monthStr + "-" + dayStr);
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
						.get(Calendar.DAY_OF_MONTH), true);
		datePickerDialog.setYearRange(1985, 2028);
		datePickerDialog.setCloseOnSingleTapDay(false);

		datePickerDialog.show(getActivity().getSupportFragmentManager(),
				DATEPICKER_TAG);

	}

	private void getData(final String startdate, final String enddate,
			final String lang, final String report, final String manifest) {

		final String reqEdate = enddate.replace("-", "");
		final String reqSdate = startdate.replace("-", "");
		Log.e("requestdate", reqSdate + "--------" + reqEdate);
		loadDialog = Utils.prog(getActivity(), getString(R.string.loading));
		CustomRequest logRequest = new CustomRequest(Method.POST,
				getString(R.string.mainIp) + "clearance.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("json", response + "");
						try {
							if (response.getInt("error_number") == 1000) {
								Data(response.getJSONArray("data"));
							} else {
								if (response.getInt("error_number") == 1001) {
									loadDialog.dismiss();
									Toast.makeText(
											getActivity(),
											response.getString("error_description"),
											Toast.LENGTH_LONG).show();
								} else {
									error();
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							loadDialog.dismiss();
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("error", error.getMessage() + "");
						error();
					}

				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("user", sp.getString("user", ""));
				params.put("sdate", reqSdate);
				params.put("edate", reqEdate);
				params.put("lang", lang);
				if (report != null)
					params.put("report", report);
				if (manifest != null)
					params.put("manifest", manifest);
				return params;
			}

		};
		mRequestQueue.add(logRequest);

	}

	private void Data(JSONArray arr) throws JSONException {
		clearDataAr.clear();
		Toast.makeText(getActivity(), arr.length() + " мэдээлэл байна",
				Toast.LENGTH_LONG).show();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject data = arr.getJSONObject(i);
			Clearance clear = new Clearance();
			clear.reportNum = data.optString("SIMPIMPDCLRNO");
			clear.manifestNum = data.getString("CARGOMGMTNO");
			clear.status = data.optString("PRGSSTATUSFGNM");
			clear.statusCol = data.optInt("ROWCOLOR");
			clear.createdDate = data.optString("DCLRDATE");
			clear.aanName = data.optString("COMPNM");
			clear.register = data.getString("PIN");
			clear.formNum = data.optString("TAXTYPE");
			clear.hardCode = data.optString("HARDCD");
			clear.allDuty = data.optString("TOTTAXAMT");
			clear.fee = data.optString("COMMISSION");
			clear.allWeight = data.optString("TOTWGT")
					+ data.optString("TOTWGTUNITCD");
			clearDataAr.add(clear);
		}
		adapter = new ClearAdapter(getActivity(), clearDataAr);
		clearList.setAdapter(adapter);
		loadDialog.dismiss();
	}

	private void error() {
		loadDialog.dismiss();

		Toast.makeText(getActivity(), getString(R.string.mainError),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	private class ClearAdapter extends ArrayAdapter<Clearance> {
		Context mContext;

		public ClearAdapter(Context context, List<Clearance> objects) {
			super(context, 0, 0, objects);
			// TODO Auto-generated constructor stub
			this.mContext = context;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			Clearance clear = getItem(position);
			ViewHolder hol = null;
			if (v == null) {
				v = ((Activity) mContext).getLayoutInflater().inflate(
						R.layout.clearance_item, null);
				hol = new ViewHolder();
				hol.reportNum = (TextView) v.findViewById(R.id.reportNum);
				hol.manifest = (TextView) v.findViewById(R.id.manifestNum);
				hol.status = (TextView) v.findViewById(R.id.status);
				hol.createdDate = (TextView) v.findViewById(R.id.createdDate);
				hol.aanName = (TextView) v.findViewById(R.id.aanName);
				hol.register = (TextView) v.findViewById(R.id.register);
				hol.formNum = (TextView) v.findViewById(R.id.formNum);
				hol.reportNum = (TextView) v.findViewById(R.id.reportNum);
				hol.hardcode = (TextView) v.findViewById(R.id.hardNum);
				hol.allDuty = (TextView) v.findViewById(R.id.allDuty);
				hol.fee = (TextView) v.findViewById(R.id.fee);
				hol.allWeight = (TextView) v.findViewById(R.id.allWeight);
				v.setTag(hol);
			} else {
				hol = (ViewHolder) v.getTag();
			}
			hol.reportNum.setText(clear.reportNum + "");
			hol.manifest.setText(clear.manifestNum + "");
			hol.status.setText(clear.status + "");
			hol.createdDate.setText(clear.createdDate);

			hol.aanName.setText(clear.aanName + "");
			hol.register.setText(clear.register + "");
			hol.formNum.setText(clear.formNum + "");

			hol.hardcode.setText(clear.hardCode + "");
			hol.allDuty.setText(clear.allDuty + "");
			hol.fee.setText(clear.fee + "");
			hol.allWeight.setText(clear.allWeight + "");
			return v;
		}

		class ViewHolder {
			TextView reportNum;
			TextView manifest;
			TextView status;
			TextView createdDate;
			TextView aanName;
			TextView register;
			TextView formNum;
			TextView hardcode;
			TextView allDuty;
			TextView fee;
			TextView allWeight;

		}
	}

}
