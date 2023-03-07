package com.mta.firealarm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AlertHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<Alert> alerts;

    public AlertHistoryAdapter(Context context, List<Alert> alerts) {
        this.context = context;
        this.alerts = alerts;
    }

    @Override
    public int getCount() {
        return alerts.size();
    }

    @Override
    public Object getItem(int i) {
        return alerts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View row = inflater.inflate(R.layout.list_item, viewGroup, false);
        TextView eventType = row.findViewById(R.id.event_type);
        TextView source = row.findViewById(R.id.source);
        TextView timestamp = row.findViewById(R.id.timestamp);
        Alert alert = alerts.get(i);
        eventType.setText(alert.getEventType());
        source.setText("Live Stream Source: " + alert.getSource());
        timestamp.setText(alert.getTimestamp());
        return row;
    }
}
