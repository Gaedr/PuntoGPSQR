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

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
    private double latitud;
    private double longitud;

    private ZXingScannerView mScannerView;
    private GPSService GPS;

    /**
     * Constructor por defecto de la clase
     */
    public QRVisorFragment() {
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
            GPS = new GPSService(this.getContext());
            if (GPS.canGetLocation()) {
                DialogFragment dialog = new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setMessage(getString(R.string.dialog_message, latitud, longitud));

                        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (latitud != 0 && longitud != 0) {
                                    String url = "http://maps.google.com/maps?saddr=" + GPS.getLatitude() + "," + GPS.getLongitude() +
                                            "&daddr=" + latitud + "," + longitud;
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                    startActivity(intent);
                                }
                                dismiss();
                            }
                        });

                        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dismiss();
                            }
                        });

                        return builder.create();
                    }

                    @Override
                    public void onPause() {
                        super.onPause();
                        latitud = longitud = 0;
                    }
                };
                dialog.show(getActivity().getFragmentManager(), "dialog");
            } else {
                showSettingsAlert();
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
        double lat, lon;
        try {
            lat = Double.parseDouble(cadena[1]);
            lon = Double.parseDouble(cadena[3]);
            setCoordinates(lat, lon);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Método que guarda las coordenadas
     *
     * @param lat latitud que queremos guardar
     * @param lon longitud que queremos guardar
     */
    private void setCoordinates(double lat, double lon) {
        latitud = lat;
        longitud = lon;
    }

    /**
     * Método que muestra un diálogo para configurar el GPS
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        alertDialog.setTitle(getContext().getString(R.string.gps_settings_title));

        alertDialog.setMessage(getContext().getString(R.string.gps_settings_message));

        // Acción del botón "Configurar"
        alertDialog.setPositiveButton(getContext().getString(R.string.gps_settings_setting),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getContext().startActivity(intent);
                    }
                });

        // Acción del botón "Cancelar"
        alertDialog.setNegativeButton(getContext().getString(R.string.gps_settings_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
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
}
