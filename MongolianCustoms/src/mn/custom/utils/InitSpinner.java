package mn.custom.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mn.custom.database.Data;
import mn.custom.mongoliancustoms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class InitSpinner {
	Spinner spin;
	Context con;
	int mode = 0;
	private ArrayAdapter<String> adapter;
	private List<Data> datas;
	private List<String> dataStr;
	String id;
	String name;
	RequestQueue mRequestQueue;
//	private String selStr=null;
	public InitSpinner(Context con, Spinner spin, int mode) {
		this.con = con;
		this.spin = spin;
		this.mode = mode;
		mRequestQueue = Volley.newRequestQueue(con);
		datas=new ArrayList<Data>();
		dataStr=new ArrayList<String>();
	}
	
	public void selItem(String code){
		
		for(Data data :datas){
			if(data.id.contains(code))
				spin.setSelection(datas.indexOf(data));
		}
		
	}
	public void makeAdapter( String ip,final Map<String, String> params) {

		CustomRequest logRequest = new CustomRequest(Method.POST,
				con.getString(R.string.mainIp) + ip, null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							if (response != null
									&& response.getInt("error_number") == 1000) {
								makeData(response.getJSONArray("data"), 1);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("error", error.getMessage() + "");
					}

				}) {

			@Override
			protected Map<String, String> getParams() {
		

				return params;
			}

		};
		mRequestQueue.add(logRequest);

	}

	protected void makeData(JSONArray jsonArray, int i) throws JSONException {
		// TODO Auto-generated method stub
		switch (i) {
		case 1:
			id = "COMMON_DETAIL_CD";
			name = "COMMON_DETAIL_CD_NM";
			break;

		default:
			id = "COMMON_DETAIL_CD";
			name = "COMMON_DETAIL_CD_NM";
			break;
		}
		Data data = null;
		for (int j = 0; j < jsonArray.length(); j++) {
			JSONObject object = jsonArray.getJSONObject(j);
			data=new Data();
			data.id = object.getString(id);
			data.name = object.getString(name);
			dataStr.add(data.id+"-"+data.name);
			datas.add(data);
		}
		adapter = new ArrayAdapter<String>(con,
				android.R.layout.simple_spinner_item, dataStr);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(adapter);
	
	
	}
//	public String getSe
}
