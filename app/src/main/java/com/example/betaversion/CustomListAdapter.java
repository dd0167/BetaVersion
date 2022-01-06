package com.example.betaversion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class CustomListAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> cityList;
    LayoutInflater inflter;

    public CustomListAdapter(Context applicationContext, ArrayList<String> cityList) {
        this.context = applicationContext;
        this.cityList = cityList;
        inflter = (LayoutInflater.from(applicationContext));
    }



    @Override
    public int getCount() {
        return cityList.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_listview_layout, null);
        TextView title = (TextView) view.findViewById(R.id.item_title);
        title.setText(cityList.get(i));
        ImageView more = (ImageView) view.findViewById(R.id.options);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), "trykrejfle", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
