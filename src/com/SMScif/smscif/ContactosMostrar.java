/*
 * clase que obtiene y muestra informacion de los contactos almacenados en el 
 * telefono (nombre, numero telefonico)*/
package com.SMScif.smscif;

import java.util.ArrayList;

import com.SMScif.cifraExixtentes.SMSPorContacto;

import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactosMostrar extends Activity {
	private ArrayList<ContactosDatosHandler> datosContactos = new ArrayList<ContactosDatosHandler>();
	private ListView listaContactos;
	private String mensajeC="";//almaceanr mensaje c. si se ingresa antes de numero
	private String nombreSeleccionado, numeroSeleccionado;
	
	@Override
	protected void onDestroy(){ //al destruir la actividad
		mensajeC = null;	
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//No aparesca barra de titulo por defecto
		setContentView(R.layout.activity_contactos_mostrar);
		
		Typeface fontMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
		((TextView)findViewById(R.id.textTituloContactos)).setTypeface(fontMedium);
		
		Bundle extras = getIntent().getExtras();
		mensajeC = extras.getString("smsCifrado"); 
		
	    ContentResolver contentresolver = getContentResolver(); //para acceder a la infromacion que se proporsiona
		Cursor c = contentresolver.query(	//guarda los datos de consulta en un cursor
				Data.CONTENT_URI, new String[] {Data._ID, Data.DISPLAY_NAME, Phone.NUMBER},
				Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE +"' AND " + Phone.NUMBER + " IS NOT NULL",
				null, Data.DISPLAY_NAME + " ASC");
		//estructura query= (tabla objetivo, campos a consultar, where, null, ordered by)
        //startManagingCursor(c); //asociar ciclo de vida  del cursos a la actividad
	
		int indiceNombre = c.getColumnIndexOrThrow(Data.DISPLAY_NAME);
		int inidiceNumero = c.getColumnIndex(Phone.NUMBER);   
	if(c.moveToFirst()){
			do{ 
				nombreSeleccionado = c.getString(indiceNombre);
				numeroSeleccionado = c.getString(inidiceNumero);
				datosContactos.add(new ContactosDatosHandler(numeroSeleccionado, nombreSeleccionado,null,null,null,false));
				}while(c.moveToNext());
			
		}
	 c.close();
		
				
		listaContactos = (ListView) findViewById(R.id.listaContactos);
		listaContactos.setAdapter(new ListaAdaptador(this,R.layout.contacto_datos,datosContactos){
			@Override
			public void onEntrada(Object entrada, View view) {
		        if (entrada != null) {
		        	Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");	//tipografia
		            TextView textoPersona = (TextView) view.findViewById(R.id.textNombreContacto); /*referencia a contacto_datos.xml layout individual*/
		            textoPersona.setTypeface(fontRegular);
		            
		            if (textoPersona != null) 
		            	textoPersona.setText(((ContactosDatosHandler) entrada).contactoPersona); 		  /*desde headler ContactosDAtosHeader.java */

		            TextView textoNumero = (TextView) view.findViewById(R.id.textNumeroContacto); 
		            textoNumero.setTypeface(fontRegular);
		            if (textoNumero != null)
		            	textoNumero.setText(((ContactosDatosHandler) entrada).contactoNumero); 
	            		            
		        }
			}
			
		});

		
    	listaContactos.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
				ContactosDatosHandler contactoElegido = (ContactosDatosHandler)pariente.getItemAtPosition(posicion);
				numeroSeleccionado = contactoElegido.contactoNumero;
				nombreSeleccionado = contactoElegido.contactoPersona;
				abrirRedactar();
			}
			
		}); 
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contactos_mostrar, menu);
		return true;
	}
	
	public void abrirRedactar(){
		String textoVacio = "";
		Intent regresarRedactar = new Intent(this,RedactarActivity.class);
		regresarRedactar.putExtra("numero", numeroSeleccionado);
		regresarRedactar.putExtra("nombrePersona", nombreSeleccionado);
		regresarRedactar.putExtra("smsCifrado", mensajeC);
		regresarRedactar.putExtra("sms", textoVacio);
		startActivity(regresarRedactar);
		finish();
	}
	public void abrirSMSContacto(){
		Intent contactosms = new Intent(this,SMSPorContacto.class);
		contactosms.putExtra("numero", numeroSeleccionado);
		contactosms.putExtra("nombrePersona", nombreSeleccionado);
		startActivity(contactosms);
		finish();
	}
	
	public void cerrarContactos (View view){
		finish();
	}

}
