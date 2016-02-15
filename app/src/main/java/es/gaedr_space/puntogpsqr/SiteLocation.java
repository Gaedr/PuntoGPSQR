/*
 * Copyright (c) 2016. Samuel Peregrina Morillas <gaedr0@gmail.com>, Nieves V. Velásquez Díaz <chibi.pawn@gmail.com>
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

import com.orm.SugarRecord;

/**
 * Clase que contiene una localizacion
 */
public class SiteLocation extends SugarRecord {
    private String name;
    private float latitude;
    private float longitude;

    public SiteLocation() {
    }

    public SiteLocation(GPSService GPS) {

    }

    public SiteLocation(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SiteLocation(String name, float latitude, float longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static SiteLocation newInstance(float latitude, float longitude) {
        return new SiteLocation(latitude, longitude);
    }

    public String getName() {
        return name;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public boolean isEmpty() {
        return this.getLatitude() == 0 && this.getLongitude() == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SiteLocation) {
            SiteLocation siteCompare = (SiteLocation) o;
            return (this.latitude == siteCompare.getLatitude() &&
                    this.longitude == siteCompare.getLongitude());
        } else {
            return false;
        }
    }
}
