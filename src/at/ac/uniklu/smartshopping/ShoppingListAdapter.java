package at.ac.uniklu.smartshopping;

import java.util.ArrayList;
import java.util.List;

import at.ac.uniklu.smartshopping.R;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

/**
 * Shopping list adapter.
 * 
 * @author Arda Akcay <ardaakcay@gmail.com>
 *
 */
public class ShoppingListAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private ViewHolder holder;
	private ArrayList<ShoppingItem> mShoppingList;
	private OnRadioButtonClickListener mListener;
	
	public ShoppingListAdapter(Context context) { 
        mInflater = LayoutInflater.from(context);        
    }
	
	public void setData(ArrayList<ShoppingItem> list) {
		mShoppingList = list;
	}
	
	public void setRadioButtonClickListener(OnRadioButtonClickListener listener) {
		mListener = listener;
	}
	
	public int getCount() {
		return (mShoppingList == null) ? 0 : mShoppingList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {			
			convertView = mInflater.inflate(R.layout.adapter_shopping_list, null);	
			holder = new ViewHolder();
			holder.checkedTextView = (CheckedTextView) convertView.findViewById(R.id.checkedTextView);
			convertView.setTag(holder);
		} 
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		ShoppingItem item = mShoppingList.get(position);
		
		holder.checkedTextView.setText(item.getText());
		holder.checkedTextView.setChecked(item.isChecked());
		holder.checkedTextView.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRadioButtonClick(position);
				}
			}
		});
		
		
        return convertView;
	}

	static class ViewHolder {
		CheckedTextView checkedTextView;
	}
	
	public interface OnRadioButtonClickListener {
		public abstract void onRadioButtonClick(int position);
	}
}