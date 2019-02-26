package com.yahoo.eslam_m_abdelaziz.uber.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.yahoo.eslam_m_abdelaziz.uber.R;

public class CustomIfoWindow implements GoogleMap.InfoWindowAdapter {

    View myView;

    public CustomIfoWindow(Context context) {
        this.myView = LayoutInflater.from(context)
                .inflate(R.layout.custom_rider_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txtPickupTitle = (TextView) this.myView.findViewById(R.id.txtPickupInfo);
        txtPickupTitle.setText(marker.getTitle());

        TextView txtPickupSnippet = (TextView) this.myView.findViewById(R.id.txtPickupSnippet);
        txtPickupSnippet.setText(marker.getSnippet());

        return this.myView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
