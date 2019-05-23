package com.example.ebeats;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    ArrayList<Uri> uriarray1;
    Context context;
    int i=1;

    public GridAdapter(ArrayList<Uri> uriarray, Context context) {
        uriarray1 = uriarray;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ChatActivity.uriarray.size();
    }

    @Override
    public Object getItem(int position) {
        return ChatActivity.uriarray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.grid_layout,parent,false);
        ImageView gridImageView=view.findViewById(R.id.grid_imageView);
        TextView number=view.findViewById(R.id.number_textView);
        number.setText(String.valueOf(i));
        i=i+1;
      Picasso.get().load(ChatActivity.uriarray.get(position)).into(gridImageView);
        return view;
    }
}
