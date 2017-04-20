package com.baibutao.app.waibao.yun.android.activites.common;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author lsb
 * 
 * @date 2012-7-10 下午07:46:11
 */
public abstract class AbstractBaseAdapter extends BaseAdapter {

	private List<?> itemList;

	public AbstractBaseAdapter(List<?> itemList) {
		this.itemList = itemList;
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < itemList.size()) {
			return itemList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
