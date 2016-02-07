/*
 *  Copyright (C) 2015, 2016 - Samuel Peregrina Morillas <gaedr@correo.ugr.es>, Nieves V. Velásquez Díaz <nievesvvd@correo.ugr.es>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.gaedr_space.puntogpsqr;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRVisorFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    //    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZXingScannerView mScannerView;
    private boolean mAutoFocus;
    //    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = 0;
    private String TAG = this.getClass().getSimpleName();
    private double latitud;
    private double longitud;

    private GPSService GPS;

    public QRVisorFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_main, container, false);
        mScannerView = new ZXingScannerView(getActivity());
        if (savedInstanceState != null) {
            mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true);
//            mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = savedInstanceState.getInt(CAMERA_ID, -1);
        } else {
            mAutoFocus = true;
//            mSelectedIndices = null;
            mCameraId = -1;
        }
        setupFormats();
        return mScannerView;
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<>();
//        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
//            mSelectedIndices = new ArrayList<>();
//            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
//                mSelectedIndices.add(i);
//            }
//        }
//
//        for (int index : mSelectedIndices) {
//            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
//        }
        formats.add(BarcodeFormat.QR_CODE);
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void handleResult(Result result) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            Log.d(TAG, getContext().getString(R.string.error_sound) + " : " + e.getMessage());
        }

        if (conversorCoordenadas(result.getText())) {
            Snackbar.make(getView(), getContext().getString(R.string.latitude) + ": " + latitud + "\n" +
                    getContext().getString(R.string.longitude) + ": " + longitud, Snackbar.LENGTH_LONG).show();
            Log.d(TAG, "Contents = " + result.getText() + ", Format = " + result.getBarcodeFormat().toString());

            GPS = new GPSService(this.getContext());
            if (GPS.canGetLocation()) {

            } else {
                GPS.showSettingsAlert();
            }
        }
    }

    private boolean conversorCoordenadas(String text) {
        String[] cadena = text.split("_");
        try {
            latitud = Double.parseDouble(cadena[1]);
            longitud = Double.parseDouble(cadena[3]);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }


}
