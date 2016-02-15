/*
 * Copyright (c) 2016. Samuel Peregrina Morillas <gaedr@correo.ugr.es>, Nieves V. Velásquez Díaz <nievesvvd@correo.ugr.es>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.gaedr_space.puntogpsqr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import es.gaedr_space.puntogpsqr.LocationsListFragment.OnListFragmentInteractionListener;

public class LocationViewAdapter extends RecyclerView.Adapter<LocationViewAdapter.ViewHolder> {

    private final List<SiteLocation> siteLocationList;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;

    public LocationViewAdapter(Context context, List<SiteLocation> items, OnListFragmentInteractionListener listener) {
        mContext = context;
        siteLocationList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_site_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mSite = siteLocationList.get(position);
        holder.mLatitude.setText(mContext.getString(R.string.item_latitude, NumberFormat.getInstance().format(holder.mSite.getLatitude())));
        holder.mLongitude.setText(mContext.getString(R.string.item_longitude, NumberFormat.getInstance().format(holder.mSite.getLongitude())));
//        holder.mLatLon.setText(
//                mContext.getString(R.string.item_lat_lon,
//                        NumberFormat.getInstance().format(holder.mSite.getLatitude()),
//                        NumberFormat.getInstance().format(holder.mSite.getLongitude()))
//        );
        holder.mLocationId.setText(
                holder.mSite.getName() == null || holder.mSite.getName().isEmpty() ?
                        mContext.getString(R.string.item_default_name, holder.mSite.getId()) :
                        holder.mSite.getName()
        );

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(mContext, holder.mSite);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return siteLocationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        public final TextView mLocationId;
        public final TextView mLatitude;
        public final TextView mLongitude;
        public SiteLocation mSite;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLocationId = (TextView) mView.findViewById(R.id.location_id);
            mLatitude = (TextView) mView.findViewById(R.id.location_latitude);
            mLongitude = (TextView) mView.findViewById(R.id.location_longitude);
        }
    }
}
