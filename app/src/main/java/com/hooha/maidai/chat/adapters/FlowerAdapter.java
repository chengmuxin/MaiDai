package com.hooha.maidai.chat.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.model.Bang;

import java.util.List;

/**
 * Created by MG on 2016/9/13.
 */
public class FlowerAdapter extends ArrayAdapter<Bang> {

    private int resourceId;

    public FlowerAdapter(Context context, int resource, List<Bang> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bang bang = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.no = (TextView) view.findViewById(R.id.bang_no_f);
            viewHolder.name = (TextView) view.findViewById(R.id.bang_name_f);
            viewHolder.num = (TextView) view.findViewById(R.id.bang_num_f);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (position < 3) {
            viewHolder.no.setText("Top-" + (position + 1));
        } else {
            viewHolder.no.setText("" + (position + 1));
        }

        if(!TextUtils.isEmpty(bang.getName())){
            viewHolder.name.setText(bang.getName());
        }
        viewHolder.num.setText(bang.getFlowerNum());
        return view;

    }

    class ViewHolder {
        TextView no;
        TextView name;
        TextView num;
    }
}
