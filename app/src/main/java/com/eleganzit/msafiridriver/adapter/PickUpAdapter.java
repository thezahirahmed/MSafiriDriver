package com.eleganzit.msafiridriver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.eleganzit.msafiridriver.R;

import java.util.ArrayList;

public class PickUpAdapter extends BaseAdapter {

    Context context;
    ArrayList arrayList;

    public PickUpAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view=layoutInflater.inflate(R.layout.pay_statement,null);

        return view;
    }
}