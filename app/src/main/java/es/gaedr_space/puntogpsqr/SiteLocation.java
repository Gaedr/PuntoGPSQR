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
 * Clase que contiene el objeto localización
 * Extiende la clase SugarRecord para poder guardarse en la BD
 */
public class SiteLocation extends SugarRecord {
    private String name;
    private float latitude;
    private float longitude;

    /**
     * Constructor por defecto de la clase
     */
    @SuppressWarnings("unused")
    public SiteLocation() {
    }

    /**
     * Constructor parametrizado de la clase
     *
     * @param latitude  contiene la latitud por defecto
     * @param longitude contiene la longitud por defecto
     */
    public SiteLocation(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Constructor completo de la clase
     *
     * @param name      Nombre del lugar
     * @param latitude  por defecto
     * @param longitude por defecto
     */
    public SiteLocation(String name, float latitude, float longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Devuelve una nueva instancia del objeto
     *
     * @param latitude  latitud por defecto para su construcción
     * @param longitude longitud por defecto para su construcción
     * @return Una nueva instancia del objeto
     */
    public static SiteLocation newInstance(float latitude, float longitude) {
        return new SiteLocation(latitude, longitude);
    }

    /**
     * Devuelve el nombre
     *
     * @return nombre del lugar
     */
    public String getName() {
        return name;
    }

    /**
     * Devuelve la latitud del lugar
     *
     * @return latitud guardada
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Devuelve la longitud del lugar
     *
     * @return longitud guardada
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Setea el nombre del lugar
     *
     * @param name nombre que contendrá el lugar
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Comprueba si el lugar no está vacio
     * Se considera vacío si tanto la latitud como la longitud son 0
     *
     * @return true si el lugar no está seteado, false si contiene una dirección
     */
    public boolean isEmpty() {
        return this.getLatitude() == 0 && this.getLongitude() == 0;
    }

    /**
     * Compara entre dos localizaciones
     *
     * @param newSite que contiene una localizacion
     * @return true si contiene la misma dirección, false en caso contrario
     */
    @Override
    public boolean equals(Object newSite) {
        if (newSite instanceof SiteLocation) {
            SiteLocation siteCompare = (SiteLocation) newSite;
            return (this.latitude == siteCompare.getLatitude() &&
                    this.longitude == siteCompare.getLongitude());
        } else {
            return false;
        }
    }
}
