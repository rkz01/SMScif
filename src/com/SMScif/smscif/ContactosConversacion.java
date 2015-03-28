//Clase q muestra todos los contactos, cada uno con su sms mas reciente
package com.SMScif.smscif;
/*--------------------------content://sms/conversations-----*/
import java.util.ArrayList;

import com.SMScif.auxiliares.FechaFormato;
import com.SMScif.auxiliares.FiltrarNumeroTel;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class ContactosConversacion extends Activity {
	private String nombreSeleccionado, numeroSeleccionado, hiloSeleccionado;
	private String personaS, numeroS;
	private ListView listaContactos;
    private RecibeBroadcastNuevoSMS receiv = new RecibeBroadcastNuevoSMS();	
	private static final String ACTION_SMS_NUEVO = "SMS_NEW";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//No aparezca barra de titulo por defecto
		setContentView(R.layout.activity_contactos_conversacion);
		
		Typeface fontMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
		((TextView) findViewById(R.id.txTituloConversaciones)).setTypeface(fontMedium);
		
		IntentFilter filtro = new IntentFilter();	//Para cuando llega un SMS 
	    filtro.addAction(ACTION_SMS_NUEVO);   
		registerReceiver(receiv, filtro); 
		
		mostrarLista();
		}
	/**************************************Para animacion al presionar boton regresar*************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			overridePendingTransition(R.anim.right_in,R.anim.right_out);
			return true;
		}
		else
		return super.onKeyDown(keyCode, event);		
		
	}
	/*******************************************************************************************/
	 protected void onDestroy(){  
	    unregisterReceiver(receiv); 
		super.onDestroy();   
	}  
	/*******************************************************************************************/
  public class RecibeBroadcastNuevoSMS extends BroadcastReceiver  {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_SMS_NUEVO)) {
				new HiloNuevoSMS().execute("");	 
			}
		}    	
    } 
	 
	private void mostrarLista() {
		ArrayList<ContactosDatosHandler> smsList = new ArrayList<ContactosDatosHandler>();
		
		String[] projection2 = new String[] {"address", "type", "thread_id", "body", "date", "status"};	//columnas que regresa por cada renglon
		String selection2 =  "address" + " IS NOT NULL"  + ") GROUP BY (" + "thread_id";  //clausula de agrupar por hilo de conversacion, criterio de seleccion
		
		Uri mensajes = Uri.parse("content://sms");	//leer mensajes entrada content 
		Cursor m = getContentResolver().query(mensajes, projection2, selection2, null, null);
		
		String  cuerpoS = "", fechaS = "";
		String nombreP, numeroP, threadS, tipo;
		
		ContentResolver contentresolver = getContentResolver(); //para acceder a la infromacion que se proporsiona
	    Cursor cP = contentresolver.query(	//guarda los datos de consulta en un cursor
					Data.CONTENT_URI, new String[] {Data._ID, Phone.DISPLAY_NAME, Phone.NUMBER},
					Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE +"' AND " + Phone.NUMBER + " IS NOT NULL",
					null, null);
			
			int indiceNombre = cP.getColumnIndexOrThrow(Phone.DISPLAY_NAME);
			int indiceNumero = cP.getColumnIndex(Phone.NUMBER);  
			String dia, mes, anio, hora;
			FechaFormato formatoFecha = new FechaFormato(); 
			
		while (m.moveToNext()){
			//COLUMNS DE SMS
			/* _id, thread_id, address, person, date, protocol, read: 1 leido 0 no leido, status,
			(type : 1 inbox, 2 enviado, 3 en borrador, 4 outbox no se a enviado aun, 5 outgoing failed no se enviara,
			 6el outgoing se enviara mas tarde )
			*/
			    tipo = m.getString(m.getColumnIndex("type"));//tipo de sms
				numeroS = m.getString(m.getColumnIndex("address")); //Remitente Numero 2
				threadS = m.getString(m.getColumnIndex("thread_id")); //hilo del sms
		     	//fechaS = (String) DateFormat.format("MMM dd, yyyy  k:mm", m.getLong(m.getColumnIndex("date"))); //Fecha 4
								
				anio = (String) DateFormat.format("yyyy", m.getLong(m.getColumnIndex("date")));
				mes = (String) DateFormat.format("MMM", m.getLong(m.getColumnIndex("date")));
				dia = (String) DateFormat.format("dd", m.getLong(m.getColumnIndex("date"))); 
				hora = (String) DateFormat.format("h:mm a", m.getLong(m.getColumnIndex("date"))); 
				fechaS = formatoFecha.formatoFechaConversacion(anio, mes, dia, hora);
				
				cuerpoS = m.getString(m.getColumnIndex("body"));    //Cuerpo sms 12
				
				while(cP.moveToNext()){		//recorre contactos
					nombreP = cP.getString(indiceNombre);
					numeroP = cP.getString(indiceNumero);
					
					//---- Solo pasa el numero sin otros caracteres----
					FiltrarNumeroTel filter = new FiltrarNumeroTel();
					numeroP = filter.filtrarNumeroTelefono(numeroP);				
					//--------------------------------------------------
					if (numeroP.equals(numeroS)){	//si el numero de contacto es igual al numero del mensaje obtiene nombre de contacto
						personaS = nombreP;	
					}						
				}
				if(Integer.parseInt(tipo) == 3 && numeroS.equals("")){
					personaS = "rough";//para posteriormente obtener el string de resources
					}
			
				 cP.moveToFirst();	//rebobinar datos de contacto
							
				 smsList.add(new ContactosDatosHandler(numeroS, personaS, cuerpoS, fechaS, threadS, false));
				 cuerpoS="";fechaS=""; personaS=""; numeroS=""; threadS="";nombreP="";
			
				}while(m.moveToNext()); 
				
				
				
				listaContactos = (ListView) findViewById(R.id.listContactsConvers);
				listaContactos.setAdapter(new ListaAdaptador(this,R.layout.contacto_conversacion,smsList){
					@Override
					public void onEntrada(Object entrada, View view) {
				        if (entrada != null) {
				        	Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");	//tipografia
				            TextView textoPersona = (TextView) view.findViewById(R.id.texNameContact); /*referencia a contacto_datos.xml layout individual*/
				            textoPersona.setTypeface(fontRegular);
				            
				            if (((ContactosDatosHandler) entrada).contactoPersona!= null) 
				            	textoPersona.setText(((ContactosDatosHandler) entrada).contactoPersona); 		  
				            if (((ContactosDatosHandler) entrada).contactoPersona=="" || ((ContactosDatosHandler) entrada).contactoPersona== null ){
				            	textoPersona.setText(((ContactosDatosHandler) entrada).contactoNumero); 
						     }
				            if (((ContactosDatosHandler) entrada).contactoPersona == "rough"){
				            	textoPersona.setText(R.string.mensajeRoughDraft); 
						     }
				            
				            TextView textoBody = (TextView) view.findViewById(R.id.texBodySMS); 
				            textoBody.setTypeface(fontRegular);
				            if (((ContactosDatosHandler) entrada).contactoBody  != null)
				            	textoBody.setText(((ContactosDatosHandler) entrada).contactoBody); 
			            		   
				            TextView textoDate = (TextView) view.findViewById(R.id.txfecha); 
				            textoDate.setTypeface(fontRegular);
				            if (((ContactosDatosHandler) entrada).contactoDate  != null)
				            	textoDate.setText(((ContactosDatosHandler) entrada).contactoDate); 
			            		
				        }
					}
					
				});

				
		    	listaContactos.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
						ContactosDatosHandler contactoElegido = (ContactosDatosHandler)pariente.getItemAtPosition(posicion);
						/*numeroSeleccionado = contactoElegido.contactoNumero;
						nombreSeleccionado = contactoElegido.contactoPersona;*/
						numeroSeleccionado = contactoElegido.getNum();
						nombreSeleccionado = contactoElegido.getPersona();
						hiloSeleccionado = contactoElegido.getHilo();
						openConversation();
					}
					
				}); 			
				
		}

	public void openConversation(){
		Intent contactosms = new Intent(this,SMSConversacion.class);
		contactosms.putExtra("numero", numeroSeleccionado);
		contactosms.putExtra("hilo", hiloSeleccionado);
		contactosms.putExtra("nombrePersona", nombreSeleccionado);
		contactosms.putExtra("smsCifrado", "");
		startActivity(contactosms);
		overridePendingTransition(R.anim.left_in,R.anim.left_out);
		//finish();   
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contactos_conversacion, menu);
		return true;
	}
	/**************************************************Hilo al recibir un SMS *********************************************/
	private class HiloNuevoSMS extends AsyncTask<String, Void, String>{		
		  
		@Override
		protected void onPreExecute(){	//antes de ejecutar el hilo			
		}
		
		@Override
		protected String doInBackground(String... params) {	//ejecucion del hilo
			try{		    					
		    	Thread.sleep(1000);
			} catch (Exception e){
				Log.e(MainActivity.class.toString(), e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result){	//despues de ejecutar el hilo
			mostrarLista();							//se hace consulta para actualizar lista de sms
		}
	}
	
	/******************Presionar icono back*************************/
	public void cerrarContactosConversacion(View view){
		finish();
		overridePendingTransition(R.anim.right_in,R.anim.right_out);
	}
}