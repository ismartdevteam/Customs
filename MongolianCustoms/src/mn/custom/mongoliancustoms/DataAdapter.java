package mn.custom.mongoliancustoms;

import java.util.List;

import mn.custom.database.Data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DataAdapter extends ArrayAdapter<Data> {

	Context mContext;

	public DataAdapter(Context context, List<Data> items) {

		super(context, 0, 0, items);
		this.mContext = context;

	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		Data data = getItem(position);
		holder hol = null;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) ((Activity) mContext)
					.getLayoutInflater();
			hol = new holder();
			v = inflater.inflate(R.layout.data_item, parent, false);
			hol.text = (TextView) v.findViewById(R.id.text);
			v.setTag(hol);
		} else
			hol = (holder) v.getTag();
		hol.text.setText(data.id+"-"+data.name+"");
		return v;

	}

	class holder {
		TextView text;
	}

}