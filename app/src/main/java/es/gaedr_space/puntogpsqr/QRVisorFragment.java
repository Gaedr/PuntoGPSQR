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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Clase encargada de leer el código QR y mostrar su contenido
 *
 * @author gaedr
 */
public class QRVisorFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private final String TAG = this.getClass().getSimpleName();
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String CAMERA_ID = "CAMERA_ID";

    private boolean mAutoFocus;
    private int mCameraId;
    private SiteLocation mSiteLocation;

    private ZXingScannerView mScannerView;
    private GPSService GPS;

    /**
     * Constructor por defecto de la clase
     */
    public QRVisorFragment() {
    }

    public static Fragment newInstance() {
        return new QRVisorFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());
        if (savedInstanceState != null) {
            mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true);
            mCameraId = savedInstanceState.getInt(CAMERA_ID, -1);
        } else {
            mAutoFocus = true;
            mCameraId = -1;
        }
        setupFormats();
        return mScannerView;
    }

    /**
     * Método que setea los formatos aceptados por el lector
     */
    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    /**
     * Método que se lanza cada vez que se encuentra un resultado para su manejo
     *
     * @param result que contiene el contenido del QR
     */
    @Override
    public void handleResult(Result result) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            Log.d(TAG, getContext().getString(R.string.error_sound) + " : " + e.getMessage());
        }

        if (transforStringToCoordinates(result.getText())) {
            GPS = new GPSService(getActivity());
            if (GPS.canGetLocation()) {
                mSiteLocation.save();
                DialogFragment dialog = new DialogFragment() {
                    @NonNull
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setMessage(getString(R.string.dialog_message, mSiteLocation.getLatitude(), mSiteLocation.getLongitude()));

                        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!mSiteLocation.isEmpty()) {
                                    startActivity(mapsLauncher(mSiteLocation, GPS.getSiteLocation()));
                                }
                                mSiteLocation = null;
                                dismiss();
                            }
                        });

                        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mSiteLocation = null;
                                dismiss();
                            }
                        });

                        builder.setCancelable(false);
                        return builder.create();
                    }

                    @Override
                    public void onPause() {
                        mSiteLocation = null;
                        super.onPause();
                    }

                    @Override
                    public void onDetach() {
                        mSiteLocation = null;
                        super.onDetach();
                    }
                };
                dialog.show(getActivity().getSupportFragmentManager(), "dialog");
            } else {
                showSettingsAlert(getActivity());
            }
        }
        mScannerView.resumeCameraPreview(this);
    }

    /**
     * Método que transforma un string recibido en coordenadas
     * En caso de que la transformación sea correcta guarda estas
     *
     * @param text que contiene el texto que queremos convertir
     * @return true si la conversión ha sido correcta, false en caso contrario
     */
    private boolean transforStringToCoordinates(String text) {
        String[] cadena = text.split("_");
        boolean setted;
        float lat, lon;
        try {
            lat = Float.parseFloat(cadena[1]);
            lon = Float.parseFloat(cadena[3]);
            if (mSiteLocation == null || !mSiteLocation.equals(SiteLocation.newInstance(lat, lon))) {
                setted = true;
                mSiteLocation = new SiteLocation(lat, lon);
            } else {
                setted = false;
            }

        } catch (Exception e) {
            setted = false;
        }
        return setted;
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
        mScannerView.stopCamera();
    }

    /**
     * Método que muestra un diálogo para configurar el GPS
     */
    public static void showSettingsAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(context.getString(R.string.gps_settings_title));

        alertDialog.setMessage(context.getString(R.string.gps_settings_message));

        alertDialog.setPositiveButton(context.getString(R.string.gps_settings_setting),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton(context.getString(R.string.gps_settings_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    /**
     * Método que genera el Intent de la aplicación de Google Maps que señala la ruta entre dos puntos
     *
     * @param myLocation Localización de partida
     * @param destiny    Localización destino
     * @return Intent correspondiente a la aplicación con los parámetros
     */
    public static Intent mapsLauncher(SiteLocation myLocation, SiteLocation destiny) {
        String url = "http://maps.google.com/maps?saddr=" + myLocation.getLatitude() + "," + myLocation.getLongitude() +
                "&daddr=" + destiny.getLatitude() + "," + destiny.getLongitude();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        return intent;
    }
}
