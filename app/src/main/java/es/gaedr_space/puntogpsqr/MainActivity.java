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
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.orm.SugarContext;

public class MainActivity extends AppCompatActivity implements LocationsListFragment.OnListFragmentInteractionListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final String LOCATION_LIST_TAG = "location_list_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SugarContext.init(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.scanner_fragment, QRVisorFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_locations) {
            /*getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scanner_fragment, LocationsListFragment.newInstance(), LOCATION_LIST_TAG)
                    .commit();*/
            Intent i = new Intent(this, LocationsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Context context, SiteLocation mSite) {
        final GPSService GPS = new GPSService(this);
        Log.d(TAG, "Pulsado item");
        if (GPS.canGetLocation()) {
            Log.d(TAG, "Abriendo Maps");
            startActivity(QRVisorFragment.mapsLauncher(GPS.getSiteLocation(), mSite));
        }
    }
}
