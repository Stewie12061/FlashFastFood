package com.example.flashfastfood.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flashfastfood.R;

public class SpinnerAdapter extends BaseAdapter {

    Context context;
    int flags[];
    String[] payMentMethod;
    LayoutInflater inflter;

    public SpinnerAdapter(Context applicationContext, int[] flags, String[] payMentMethod) {
        this.context = applicationContext;
        this.flags = flags;
        this.payMentMethod = payMentMethod;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.items_custom_spinner, null);
        ImageView icon = (ImageView) convertView.findViewById(R.id.imageView);
        TextView names = (TextView) convertView.findViewById(R.id.textView);
        icon.setImageResource(flags[position]);
        names.setText(payMentMethod[position]);
        return convertView;
    }
}
