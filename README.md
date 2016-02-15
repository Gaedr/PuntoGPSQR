##Práctica 3
##ANDROID-GYMKANA
###Aplicación Punto GPSQR
### Autores
* Samuel Peregrina Morillas
* Nieves Victoria Velásquez Díaz

### Duración de la práctica.
Desde 12-Ene-2016 hasta 15-Feb-2016

### Breve descripción de la práctica.
Para la realización de esta práctica, se programarán cinco aplicaciónes android diferentes de forma que, cada una hace uso de los distintos sensores que posee el dispositivo android.

### Descripción del problema.
Esta aplicación consiste en, mediante el uso de la cámara y el GPS, leer un código QR que contendrá una latitud y una longitud para después obtener un recorrido desde la posición actual obtenida a través del GPS del móvil hasta la indicada en el código QR.

###Clases
Las clases usadas durante la realización de esta práctica han sido:
* **GPSService:** esta clase implementa la clase Service y se utiliza para obtener la posición actual del usuario a partir del GPS y de la red móvil.
* **LocationActivity:** esta clase es la encargada de contener el fragmento **LocationListFragment** que es el que contendrá la lista de las localizaciónes almacenadas en la BD.
* **LocationViewAdapter:** esta clase es el adaptador para visualizar cada elemento en la lista que se mostrará en el fragment comentado anteriomente.
* **QRVisorFragment:** fragment encargado de manejar los elementos de la cámara y captura de los QR, asi como de la previsualización continua de la cámara mientras se mantenga este fragment.
Esta clase a su vez implementa la interfaz **ZXingScannerView.ResultHandler**, perteneciente la librería [BarCodeScanner](https://github.com/dm77/barcodescanner), una librería para trabajar con los códigos captados a través de la cámara.
* **SiteLocation:** clase contenedora de las localizaciónes que guardaremos en la BD.
* **MainActivity:** actividad inicial encargada de lanzar el fragment **QRVisorFragment** y de recoger el evento que lanzará la aplicación *GoogleMaps*.

###Métodos.
* Métodos a destacar dentro de la clase **GPSService**:
	* **getLocation:** este método obtendra la localización del GPS y de la red comprobando si estos están activos para hacelo, en caso de que no pueda con ninguno lanzará un mensaje para que el usuario configure el GPS.
* Métodos a destacar en **QRVisorFragment**:
	* **setupFormats:** este método genera la lista del tipo de códigos que leerá el visor y lanzará un evento de lectura
	* **handleResults:** este método se lanzará cada vez que se encuentre un código QR y lanzará *GoogleMaps* en caso de que contenga información correcta o dará un error en caso contrario.
	* **mapsLauncher:** método estático que genera el intent de *GoogleMaps* con la localización actual y el destino

El resto de métodos no se comentan ya que pertenecen al ciclo de vida de la aplicación.

###Bibliografía
* [BarCodeScanner](https://github.com/dm77/barcodescanner): librería de reconocimiento de códigos a través de la cámara
* [iconmonstr](http://iconmonstr.com/): librería de imagenes con licencia GNU
* [SGOliver: localización Geografica en Android (I)](http://www.sgoliver.net/blog/localización-geografica-en-android-i/) : obtencion de la posición actual a través del GPS
