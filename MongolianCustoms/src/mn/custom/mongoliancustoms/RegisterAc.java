package mn.custom.mongoliancustoms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.custom.database.Aan;
import mn.custom.database.Data;
import mn.custom.database.Manifest;
import mn.custom.utils.CustomAutoCompleteView;
import mn.custom.utils.CustomRequest;
import mn.custom.utils.InitSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

@SuppressLint("DefaultLocale")
public class RegisterAc extends ActionBarActivity implements OnClickListener {
	private ActionBar actionBar;
	private EditText typeCd;
	private ImageButton typeCdFil;
	private TextView typeCdName;
	private List<Data> typeCdList;
	private RequestQueue mRequestQueue;
	private SharedPreferences sp;
	private AutoCompleteTextView manifestSearch;
	private ImageButton manifestFil;
	private CustomAutoCompleteView venicleSearch;
	private ImageButton venicleFil;
	private Calendar c;
	private SimpleDateFormat format;
	private String manSdate;
	private String manEdate;

	private TextView typeName;
	private EditText regYear;
	private EditText regMonth;
	private EditText regDay;
	private String[] types = { "Импорт", "Экспорт" };
	private String userId;

	private Spinner taxTypeSpinner;
	private Spinner taxTypeSpinnerSec;
	private Spinner weightSpin;
	// country
	private EditText regionEdit;
	private ImageButton regionSearch;
	private TextView regionName;
	// aan
	private CustomAutoCompleteView aanSearch;
	private ImageButton aanFil;
	private EditText aanReg;
	private TextView aanName;
	private EditText aanPhone;
	private EditText aanAddress;
	private List<Aan> aanList;
	private List<Manifest> manifestList;
	private EditText weight;
	private EditText quantity;
	private InitSpinner weightInitSpinner;
	// progress bars
	private ProgressBar typeProg;
	private ProgressBar manProg;
	private ProgressBar venProg;
	private ProgressBar countryProg;
	private ProgressBar aanProg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		c = Calendar.getInstance();
		format = new SimpleDateFormat("yyyyMMdd");
		sp = getSharedPreferences("login", MODE_PRIVATE);
		userId = sp.getString("userCode", "09").substring(0, 2);
		Log.i("user", userId+"");
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getString(R.string.registerac));
		actionBar.setDisplayUseLogoEnabled(false);
		mRequestQueue = Volley.newRequestQueue(this);
		initView();
	}

	private String toDate(int year, int month, int day, String format) {
		@SuppressWarnings("deprecation")
		Date date = new Date(year - 1900, month, day);
		return DateFormat.format(format, date).toString();
	}

	private void initView() {
		weight = (EditText) findViewById(R.id.reg_gross);
		quantity = (EditText) findViewById(R.id.reg_quantity);
		// gorim
		typeCd = (EditText) findViewById(R.id.reg_typeCd);
		typeCdFil = (ImageButton) findViewById(R.id.reg_typeCd_filter);
		typeCdFil.setOnClickListener(this);
		typeCdName = (TextView) findViewById(R.id.reg_typeCd_name);
		// type
		typeName = (TextView) findViewById(R.id.reg_typeName);
		typeName.setOnClickListener(this);
		typeName.setTag(1);
		// date
		regYear = (EditText) findViewById(R.id.reg_typeReg_year);
		regMonth = (EditText) findViewById(R.id.reg_typeReg_month);
		regDay = (EditText) findViewById(R.id.reg_typeReg_day);
		String date[] = toDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd").split("-");
		regYear.setText(date[0]);
		regMonth.setText(date[1]);
		regDay.setText(date[2]);

		// manifest
		manifestList = new ArrayList<Manifest>();
		manifestSearch = (AutoCompleteTextView) findViewById(R.id.reg_manNo);
		manifestFil = (ImageButton) findViewById(R.id.reg_manNo_filter);
		manifestFil.setOnClickListener(this);
		manInit();
		// venicle
		venicleSearch = (CustomAutoCompleteView) findViewById(R.id.reg_venicleNo);
		venicleFil = (ImageButton) findViewById(R.id.reg_venicleFil);
		venicleFil.setOnClickListener(this);
		// adapter
		// adapter = new DataAdapter(this, dataList);
		// taxtype
		initTaxSpinner();
		weightSpin = (Spinner) findViewById(R.id.reg_weightSpin);
		weightInitSpinner = new InitSpinner(this, weightSpin, 1);
		weightInitSpinner.makeAdapter("packingUnit.php", null);
		// region
		regionEdit = (EditText) findViewById(R.id.reg_countryCd);
		regionSearch = (ImageButton) findViewById(R.id.reg_countryCd_filter);
		regionSearch.setOnClickListener(this);
		regionName = (TextView) findViewById(R.id.reg_country_name);
		// aan
		aanList = new ArrayList<Aan>();
		aanSearch = (CustomAutoCompleteView) findViewById(R.id.reg_aanNameSearch);
		aanFil = (ImageButton) findViewById(R.id.reg_aanNameFil);
		aanFil.setOnClickListener(this);
		aanName = (TextView) findViewById(R.id.reg_aanName);
		aanReg = (EditText) findViewById(R.id.reg_aanRegId);
		aanPhone = (EditText) findViewById(R.id.reg_aanPhone);
		aanAddress = (EditText) findViewById(R.id.reg_aanAddress);
		// progress bars
		typeProg = (ProgressBar) findViewById(R.id.reg_typeCdProg);
		manProg = (ProgressBar) findViewById(R.id.reg_manProg);
		venProg = (ProgressBar) findViewById(R.id.reg_venProg);
		countryProg = (ProgressBar) findViewById(R.id.reg_countryProg);
		aanProg = (ProgressBar) findViewById(R.id.reg_aanProg);

	}

	private void initTaxSpinner() {
		taxTypeSpinner = (Spinner) findViewById(R.id.reg_dutyNum);
		taxTypeSpinnerSec = (Spinner) findViewById(R.id.reg_dutyNumSec);
		InitSpinner initTaxtype = new InitSpinner(this, taxTypeSpinner, 1);
		initTaxtype.makeAdapter("taxType.php", null);
		taxTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.i("pos", position + "");
				if (position == 2) {
					taxTypeSpinnerSec.setVisibility(View.VISIBLE);
					Map<String, String> params = new HashMap<String, String>();
					params.put("typeId", "3");
					InitSpinner initTaxtypeSec = new InitSpinner(
							RegisterAc.this, taxTypeSpinnerSec, 2);
					initTaxtypeSec.makeAdapter("taxType.php", params);
				} else
					taxTypeSpinnerSec.setVisibility(View.GONE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void manInit() {

		manEdate = toDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd");
		manSdate = toDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) - 1,
				c.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd");
		manifestSearch.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				final Dialog dateDial = new Dialog(RegisterAc.this,
						R.style.CustomDialogTheme);
				dateDial.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dateDial.setContentView(R.layout.man_fil_dialog);
				final EditText dialsDate = (EditText) dateDial
						.findViewById(R.id.manDialogSdate);
				final EditText dialeDate = (EditText) dateDial
						.findViewById(R.id.manDialogEdate);
				dialsDate.setText(manSdate + "");
				dialeDate.setText(manEdate + "");
				Button ok = (Button) dateDial.findViewById(R.id.manDialogOk);
				ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						manSdate = dialsDate.getText() + "";
						manEdate = dialeDate.getText() + "";
						dateDial.dismiss();
						Manifest(manifestSearch.getText() + "");
					}
				});

				dateDial.show();
				return true;
			}
		});
	}

	private void Manifest(final String manifest) {
		manProg.setVisibility(View.VISIBLE);
		CustomRequest logRequest = new CustomRequest(Method.POST,
				this.getString(R.string.mainIp) + "cargoManifest.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						manProg.setVisibility(View.GONE);
						// TODO Auto-generated method stub
						Log.e("manifest", response+"");
						try {
							if (response != null
									&& response.getInt("error_number") == 1000) {
								makeData(response.getJSONArray("data"), 1);
							}
							else{
								Toast.makeText(RegisterAc.this,
										getString(R.string.noData) + "",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							manProg.setVisibility(View.GONE);
							e.printStackTrace();
						}
						//

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						manProg.setVisibility(View.GONE);
						Log.i("error", error.getMessage() + "");
					}

				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("sdate", manSdate);
				params.put("edate", manEdate);
				params.put("user", userId);
				
				if (manifest != null)
					params.put("manifest", manifest.toUpperCase());
				// if (billNo != null)
				// params.put("billNo", billNo);
				// if (wagonNo != null)
				// params.put("edate", wagonNo);
				return params;
			}

		};
		mRequestQueue.add(logRequest);
	}

	private void Venicle(final String venicleNo) {
		venProg.setVisibility(View.VISIBLE);
		CustomRequest logRequest = new CustomRequest(Method.POST,
				this.getString(R.string.mainIp) + "crosVenicle.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						venProg.setVisibility(View.GONE);
						try {
							if (response != null
									&& response.getInt("error_number") == 1000) {
								makeData(response.getJSONArray("data"), 2);
							}
							else{
								Toast.makeText(RegisterAc.this,
										getString(R.string.noData) + "",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							venProg.setVisibility(View.GONE);
							e.printStackTrace();
						}
						//

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						venProg.setVisibility(View.GONE);
						Log.i("error", error.getMessage() + "");
					}

				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();

				params.put("user", userId);
				if (venicleNo != null)
					params.put("venicleNo", venicleNo.toUpperCase());

				return params;
			}

		};
		mRequestQueue.add(logRequest);
	}

	private void makeData(JSONArray ar, int from) throws JSONException {
		DataAdapter adapter = null;
		Log.i("data lenght--->", ar.length() + "");
		final List<Data> dataList = new ArrayList<Data>();
		manifestList.clear();
		aanList.clear();
		for (int i = 0; i < ar.length(); i++) {
			JSONObject item = ar.getJSONObject(i);
			Data data = new Data();
			switch (from) {
			case 1:
				Manifest man = new Manifest();
				man.cargoNo = item.getString("CARGOMGMTNO");
				man.qty = item.getString("QTY");
				man.qtyUnit = item.getString("QTYUNIT");
				man.weight = item.getString("WGT");
				man.aan = new Aan();
				man.aan.name = item.getString("CONSIGNEENM").replace("\\", "");
				man.aan.address = item.getString("CONSIGNEEADDR");
				man.wagonNo = item.getString("WAGONNO");
				man.billNo = item.getString("BLNO");
				data.id = man.cargoNo;

				data.name = man.aan.name + "-" + man.wagonNo + "-" + man.billNo;
				manifestList.add(man);
				break;
			case 2:
				data.id = item.optString("BORDRCROSMGMTNO");
				data.name = item.optString("VEHCLNO");

				break;
			case 3:
				Aan aan = new Aan();
				aan.code = item.getString("COMP_CD");
				aan.name = item.getString("COMP_NM");
				aan.regId = item.getString("COMP_REG_NO");
				aan.phone = item.getString("COMP_TEL_NO");
				aan.address = item.getString("COMP_ADDR");
				data.id = aan.code;
				data.name = aan.name + "-" + aan.regId;
				aanList.add(aan);
				break;

			}

			dataList.add(data);

		}

		adapter = new DataAdapter(this, dataList);
		adapter.setNotifyOnChange(true);
		if (from == 1) {
			manifestSearch.setAdapter(adapter);
			manifestSearch.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Data seldata = (Data) manifestSearch.getAdapter().getItem(
							position);
					manifestSearch.setText(seldata + "");
					Manifest man = manifestList.get(position);
					aanName.setText(man.aan.name + "");
					aanAddress.setText(man.aan.address);
					weight.setText(man.weight);
					quantity.setText(man.qty);
					weightInitSpinner.selItem(man.qtyUnit);
				}
			});
			manifestSearch.showDropDown();

		} else {
			if (from == 2) {
				venicleSearch.setAdapter(adapter);

				venicleSearch
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub

								Data seldata = (Data) venicleSearch
										.getAdapter().getItem(position);
								venicleSearch.setText(seldata.toString());
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
								// TODO Auto-generated method stub

							}
						});
				venicleSearch.showDropDown();
			} else {
				aanSearch.setAdapter(adapter);

				aanSearch.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Data seldata = (Data) aanSearch.getAdapter().getItem(
								position);
						aanSearch.setText(seldata.toString());

						Aan selAan = aanList.get(position);
						aanName.setText(selAan.name + "");
						aanReg.setText(selAan.regId + "");
						aanPhone.setText(selAan.phone + "");
						aanAddress.setText(selAan.address + "");
					}
				});

				aanSearch.showDropDown();
			}
		}

	}

	public void findRegion(final String str) {
		countryProg.setVisibility(View.VISIBLE);
		CustomRequest logRequest = new CustomRequest(Method.POST,
				this.getString(R.string.mainIp) + "region.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						countryProg.setVisibility(View.GONE);
						try {
							if (response != null
									&& response.getInt("error_number") == 1000) {
								JSONObject data = response
										.getJSONObject("data");
								regionName.setText("");
								regionName.setText(data
										.getString("COMMON_DETAIL_CD_NM"));
								regionEdit.setText(data
										.getString("COMMON_DETAIL_CD") + "");
								// makeData(response.getJSONArray("data"));
							} else {
								regionName.setText(" ");
								Toast.makeText(RegisterAc.this,
										getString(R.string.noData) + "",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							countryProg.setVisibility(View.GONE);
							e.printStackTrace();

						}
						//

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("error", error.getMessage() + "");
						countryProg.setVisibility(View.GONE);
					}

				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("value", str);

				return params;
			}

		};
		mRequestQueue.add(logRequest);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.reg_aanNameFil:
			if (aanSearch.getText().toString().length() > 2)
				aan(aanSearch.getText() + "");
			break;
		case R.id.reg_countryCd_filter:
			findRegion(regionEdit.getText() + "");
			break;
		case R.id.reg_manNo_filter:
			if (manifestSearch.getText().toString().length() > 2)
				Manifest(manifestSearch.getText() + "");
			break;
		case R.id.reg_venicleFil:
			if (venicleSearch.getText().toString().length() > 1)
				Venicle(venicleSearch.getText() + "");
			break;
		case R.id.reg_typeCd_filter:
			String gorim = typeCd.getText().toString();
			if (gorim.length() > 1)
				clearanceTypeName(gorim);

			break;
		case R.id.reg_typeName:
			int typeCase = (Integer) v.getTag();
			switch (typeCase) {
			case 1:
				typeName.setText(types[1]);
				typeName.setTag(2);
				break;
			case 2:
				typeName.setText(types[0]);
				typeName.setTag(1);
				break;

			}
			break;

		default:
			break;
		}
	}

	private void aan(final String query) {
		aanProg.setVisibility(View.VISIBLE);
		CustomRequest aanRequest = new CustomRequest(Method.POST,
				this.getString(R.string.mainIp) + "aan.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						aanProg.setVisibility(View.GONE);
						try {
							if (response != null
									&& response.getInt("error_number") == 1000) {
								makeData(response.getJSONArray("data"), 3);
							} else {
								aanName.setText(" ");
								Toast.makeText(RegisterAc.this,
										getString(R.string.noData) + "",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							aanProg.setVisibility(View.GONE);
							e.printStackTrace();

						}
						//

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						aanProg.setVisibility(View.GONE);
						Log.i("error", error.getMessage() + "");
					}

				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", query);

				return params;
			}

		};
		mRequestQueue.add(aanRequest);

	}

	private void clearanceTypeName(final String code) {
		typeProg.setVisibility(View.VISIBLE);
		CustomRequest logRequest = new CustomRequest(Method.POST,
				this.getString(R.string.mainIp) + "clearanceTypeCd.php", null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						typeProg.setVisibility(View.GONE);
						try {
							if (response != null
									&& response.getInt("error_number") == 1000) {
								JSONObject data = response
										.getJSONObject("data");
								typeCd.setText("");
								typeCdName.setText(data
										.getString("DCLR_TYPE_NM"));
								typeCd.append(data.getString("DCLR_TYPE_CD")
										+ "");
							} else {
								typeCdName.setText(" ");
								Toast.makeText(RegisterAc.this,
										getString(R.string.noData) + "",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							typeProg.setVisibility(View.GONE);
							e.printStackTrace();
						}
						//

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("error", error.getMessage() + "");
						typeProg.setVisibility(View.GONE);
					}

				}) {

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("typeCode", code);

				return params;
			}

		};
		mRequestQueue.add(logRequest);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Only show items in the action bar relevant to this screen
		// if the drawer is not showing. Otherwise, let the drawer
		// decide what to show in the action bar.
		getMenuInflater().inflate(R.menu.reg_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_save) {
			save();
			// startActivity(new Intent(DocOne.this,DocOneReg.class));
			return true;
		}
		if (id == android.R.id.home)
			onBackPressed();
		return true;
	}

	public void save() {

	}

}
