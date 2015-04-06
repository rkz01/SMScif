/*Clase que obtiene informacion del sistema correspondiente 
 * a los sms de entrada para mostrar en una lista  
 * numero de remitente, nombre, fecha y cuerpo del sms
 * Requiere de la clase privada
 *  MyAdapter generador el adaptador de la lista y
 *  	poder mostrar el contenido en la lista
 *  SMSConversacionHandler.java manejador (headler) que almacenara
 *  	toda la informacion, un objeto para cada SMS
 *  sms_recibidos.xml layout individual que representara un	solo SMS
 */

package com.SMScif.smscif;
import java.util.ArrayList;

import com.SMScif.auxiliares.FechaFormato;
import com.SMScif.preferencias.PreferenciaActivity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;

public class SMSConversacion extends Activity {	
	private String nombrePersona = "", numerPersona = "", hilo = "";
	private String smsTexto = "";
	private ListView listaSms;
	RecibeBroadcastNewOutSMS receiver = new RecibeBroadcastNewOutSMS();	
	private ProgressBar progressBar;	
    private boolean flagBroadcastOutSms = false;   

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {/* entrada del telefono*/
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//No aparesca barra de titulo
		setContentView(R.layout.activity_sms_conversacion);
		progressBar = (ProgressBar) findViewById(R.id.barraProcesoCov);
		
		Typeface fontMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"); //tipografia
		Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
		((TextView) findViewById(R.id.txContacto)).setTypeface(fontMedium);
		((EditText) findViewById(R.id.editTSMSOutConv)).setTypeface(fontRegular);
		
		IntentFilter filter = new IntentFilter();	//Para cuando llega o se envia  un SMS 
		filter.addAction("SMS_SENT");
		filter.addAction("SMS_NEW");
		registerReceiver(receiver, filter);
		
		mostrarListView();
		}

	//Si no se realiza el unregisterReceiver genera errores en el LogCat
	protected void onDestroy(){
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.leer_recibidos, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	/*Opciones elegidas desde el menu*/
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_configuracion:
	        	Intent preferencias = new Intent(this,PreferenciaActivity.class);
	        	startActivity(preferencias);
	        	overridePendingTransition(R.anim.left_in,R.anim.left_out);	//Animacion al cambiar de vista(animacion de la nueva vista, animacion de esta vista que sale)
	            return true;
	            
	        case R.id.action_nuevoSms:	        	
	        	String textoVacio = "";//envia a redactar ya que abre por primera vez
	        	String numeroVacio = ""; //envia a redectar 
	        	String personaVacio = "";
	        	Intent abrirRedactar = new Intent(this,RedactarActivity.class); //evento llama layout
	        	abrirRedactar.putExtra("sms", textoVacio);//aqui esta vacio ya que es el inicio, mensaje plano no existe 
	        	abrirRedactar.putExtra("smsCifrado", textoVacio);//aqui esta vacio ya que es el inicio, mensaje cifrado no existe qui
	        	abrirRedactar.putExtra("numero", numeroVacio);//esta vacio numero 
	        	abrirRedactar.putExtra("nombrePersona", personaVacio);//esta vacio nombre contacto 
	        	startActivity(abrirRedactar);
	        	overridePendingTransition(R.anim.left_in,R.anim.left_out);	//Animacion al cambiar de vista(animacion de la nueva vista, animacion de esta vista que sale)
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	protected void onResume(){
		SharedPreferences preferecniaUsuario = PreferenceManager.getDefaultSharedPreferences(this); /*Instancia a preferecnias de esta app*/
		
		boolean enviaCifradoPref = preferecniaUsuario.getBoolean("preferecia_envio", true); /*optiene prerefencia (key, valDefecto)*/
		
		CheckBox checkenvioCifrado = ((CheckBox)findViewById(R.id.radioEnvioCifrado));
		if(enviaCifradoPref){
			checkenvioCifrado.setChecked(true);			
		}
		else checkenvioCifrado.setChecked(false);
		
		super.onResume();
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
		/**************************************cuando Recibe y envia SMS**************************************/
	public class RecibeBroadcastNewOutSMS extends BroadcastReceiver  {
		private static final String ACTION_SMS_NEW = "SMS_NEW";
		private static final String ACTION_SMS_SENT = "SMS_SENT";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_SMS_NEW)) {
				new HiloNewSMS().execute("");     				
			}
			if (action.equals(ACTION_SMS_SENT)) {
				flagBroadcastOutSms = true;
			}
		}    	
    }
	
		/***********************************Muestra ListView*********************************************************/
	private void mostrarListView() {
		ArrayList<SMSConversacionHandler> smsList = new ArrayList<SMSConversacionHandler>();//ArrayList q tendra losSMSPorContactoDa sms y otros datos
		String fechaS = "", cuerpoS = "" ,numeroS = "", estadoS = "",
		       personaS = "", nombreP = "", numeroP = "", tipo = "", smsCifrado="";

		Bundle extras = getIntent().getExtras();
		nombrePersona = extras.getString("nombrePersona");  
		numerPersona = extras.getString("numero");
		hilo = extras.getString("hilo");
		smsCifrado = extras.getString("smsCifrado");	//cuando se cifra respuesta
		smsTexto   = extras.getString("sms");
		
		//((EditText) findViewById(R.id.editTSMSOutConv)).setText(smsTexto);	//texto a responder		
		if(smsCifrado.length()>0){
			this.setEditResponder(smsCifrado);
			((ImageButton)findViewById(R.id.btnCifrarConv)).setEnabled(false);
			((EditText) findViewById(R.id.editTSMSOutConv)).setEnabled(false);	//si existe texto para cifrar
		}
		
		//coloca el nombre dela persona en caso q no exista el numero
		if ("".equals(nombrePersona)||nombrePersona==null) {
			((TextView) findViewById(R.id.txContacto))   
					.setText(numerPersona);  	//nombrePersona=numerPersona;
			}
		else if ("rough".equals(nombrePersona)) {   
				((TextView) findViewById(R.id.txContacto))
						.setText(R.string.mensajeRoughDraft);   
				//como es borrador y en caso de no tener destinatario, causa error al intentar responder
				//para ello se oculta el panel de ingresar una respuesta
				//findViewById(R.id.layoutEditCov).setBackgroundColor(color.teal);
				findViewById(R.id.editTSMSOutConv).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnCifrarConv).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnEnviarConv).setVisibility(View.INVISIBLE);
				}
		else if ("no sent".equals(nombrePersona)) {   
			((TextView) findViewById(R.id.txContacto))
					.setText(R.string.mensajeNoSent); 
			}
			
		else if (nombrePersona != "" && !"rough".equals(nombrePersona)&& !"no sent".equals(nombrePersona)){
			 ((TextView) findViewById(R.id.txContacto))
					.setText(nombrePersona);
		 		}  
		 
		// solo busca sms del contacto seleccionado
		String[] arg = new String[] {hilo};//argumento hilo de conversacion
		    
		Cursor cP = getContentResolver().query(	//guarda los datos de consulta en un cursor
				Data.CONTENT_URI, new String[] {Data._ID, Data.DISPLAY_NAME, Phone.NUMBER},
				Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE +"' AND " + Phone.NUMBER + " IS NOT NULL",
				null, Data.DISPLAY_NAME + " ASC");
	
		Uri mensajes = Uri.parse("content://sms");
		Cursor c = getContentResolver().query(mensajes, null, "thread_id=?", arg, " date ASC ");	//ordena por fecha para mostrar conversacion
		int indiceNombre = cP.getColumnIndexOrThrow(Data.DISPLAY_NAME);
		int inidiceNumero = cP.getColumnIndex(Phone.NUMBER);
		
		String dia, mes, anio, hora;
		FechaFormato formatoFecha = new FechaFormato(); 
		
		while (c.moveToNext()) {
			numeroS = c.getString(c.getColumnIndex("address")); //Numero column 2
	     	tipo = c.getString(c.getColumnIndex("type"));//tipo de sms
			//fechaS = (String) DateFormat.format("dd-MM-yy  k:mm",c.getLong(c.getColumnIndex("date")));
			cuerpoS = c.getString(c.getColumnIndex("body"));
			
			anio = (String) DateFormat.format("yyyy", c.getLong(c.getColumnIndex("date")));
			mes = (String) DateFormat.format("MMM", c.getLong(c.getColumnIndex("date")));
			dia = (String) DateFormat.format("dd", c.getLong(c.getColumnIndex("date"))); 
			hora = (String) DateFormat.format("h:mm a", c.getLong(c.getColumnIndex("date"))); //Fecha 4
			fechaS = formatoFecha.formatoFechaDetalle(anio, mes, dia, hora);
			
			while(cP.moveToNext()){//recorre contactos para obtener el nombre en cada sms
				nombreP = cP.getString(indiceNombre);
				numeroP = cP.getString(inidiceNumero);					
				if (numeroP.equals(numeroS)){	//si el numero de contacto es igual al numero del mensaje obtiene nombre de contacto
						personaS = nombreP;						
						}
				}
				cP.moveToFirst();	//rebobinar datos de contacto	
				
				if(Integer.parseInt(tipo) != 1 && Integer.parseInt(tipo)!= 3){//si el sms no es de entrada o est en borrador						  
						personaS= "me";
				}
				else if(Integer.parseInt(tipo) == 3){	//el nombre de persona es borrador
						personaS="rough";//para posteriormente obtener el string de resources
						estadoS="rough";    
						}
				if(Integer.parseInt(tipo) != 1 && Integer.parseInt(tipo) != 2 && Integer.parseInt(tipo) != 3 ){
						estadoS = "no sent";//para posteriormente obtener el string de resources
						}
				
			smsList.add(new SMSConversacionHandler(fechaS, cuerpoS, personaS, numeroS, estadoS, false));
			personaS=""; numeroS=""; estadoS="";
		}
		c.close(); cP.close();//cerrar cursores 

	
		listaSms = (ListView) findViewById(R.id.listaSMS);
		listaSms.setAdapter(new ListaAdaptador(this,R.layout.sms_conversacion_datos,smsList){
			@Override
			public void onEntrada(Object entrada, View view) {
		        if (entrada != null) {
		        	Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");	//tipografia
		           
		        	TextView textoPersona = (TextView) view.findViewById(R.id.textpersona); /*referencia a contacto_datos.xml layout individual*/
		            textoPersona.setTypeface(fontRegular);
		            TextView textoBody = (TextView) view.findViewById(R.id.textcuerpoSMS); 
		            textoBody.setTypeface(fontRegular);
		            TextView textoDate = (TextView) view.findViewById(R.id.textfecha); 
		            textoDate.setTypeface(fontRegular);
		            
		            if (((SMSConversacionHandler) entrada).textoPerson == "rough"){
		            	textoPersona.setText(R.string.mensajeRoughDraft); 
		            	textoBody.setBackgroundResource(R.color.redLight);
				        }
		            
		         	if ("".equals(nombrePersona)||nombrePersona==null){
		         		nombrePersona=numerPersona;
					    }
		         	
		            if  (((SMSConversacionHandler) entrada).textoPerson!="me" && ((SMSConversacionHandler) entrada).textoPerson!="rough"){
						 textoPersona.setText(nombrePersona);
						}
		            
		            if (((SMSConversacionHandler) entrada).textoPerson.equals("me")){//para saber si el numero es el propio
		    			textoPersona.setText(R.string.text_me);
		    			textoBody.setBackgroundResource(R.color.greyLight);		//color de fondo blanco Yo
		    		    
		    			if (((SMSConversacionHandler) entrada).textoEstado=="no sent"){//para saber si no es enviado
		    					textoPersona.setText(R.string.mensajeNoSent);
		    					textoBody.setBackgroundResource(R.color.redLight);		//color de fondo amarillo No enviado
		    				 }
		    	     }
		            else {
		   			 textoBody.setBackgroundResource(R.color.blueLight);	//color de fondo azul No es Yo 
		            }
		            
		          
		            if (((SMSConversacionHandler) entrada).textoCuerpo  != null){
		            	textoBody.setText(((SMSConversacionHandler) entrada).textoCuerpo); 
		             }
		            
					 if (((SMSConversacionHandler) entrada).textoFecha != null){
		            	textoDate.setText(((SMSConversacionHandler) entrada).textoFecha); 
		            }	
		        }
			}
			
		});
		
		listaSms.setSelection(listaSms.getAdapter().getCount()-1);	//Invertir lista para Conversacion
		
		listaSms.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
				SMSConversacionHandler smsElegido = (SMSConversacionHandler)pariente.getItemAtPosition(posicion);
			
				if(smsElegido.getPersona()== null || smsElegido.getPersona()== ""){
					abrirDescifrar(smsElegido.getCuerpo(), smsElegido.getNum(), smsElegido.getNum(), smsElegido.getEstadoSMS());
				    }
				else{abrirDescifrar(smsElegido.getCuerpo(), smsElegido.getPersona(), smsElegido.getNum(), smsElegido.getEstadoSMS());
				    }
			}
			
		});	
	}
	
	
        
	/********************************Abre actividad descifrar *********************************************************/
	public void abrirDescifrar(String SMSSeleccionado, String personaSeleccionada, String numerSel,  String tipoSMS) { /* metodo para abrir la actividad que descifra el SMS */
		Intent abrirDescifrar = new Intent(this, LeerDescifrar.class);
		abrirDescifrar.putExtra("smsInCifrado", SMSSeleccionado);
		abrirDescifrar.putExtra("numeroIn", numerPersona);
		abrirDescifrar.putExtra("personaIn", personaSeleccionada);
		abrirDescifrar.putExtra("persona", nombrePersona);
		abrirDescifrar.putExtra("tipoSms", tipoSMS);		
		startActivity(abrirDescifrar);
		overridePendingTransition(R.anim.left_in,R.anim.left_out);
		}
	
	/**********************************************REsponder******************************************/
	public void cifrarConv(View view){
		EditText editorTexto;
		Intent ingresoClave = new Intent(this,IngresoClave.class);
		AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
		
		editorTexto = (EditText) findViewById(R.id.editTSMSOutConv);	
		smsTexto = editorTexto.getText().toString(); 
		
		if(smsTexto.length()==0){
			dialogo.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
			dialogo.setTitle(R.string.tituloCDialogo);
			dialogo.setMessage(R.string.mensaje2CDialogo);
			dialogo.show();	}
		
		else{
			ingresoClave.putExtra("sms", smsTexto);
			ingresoClave.putExtra("numero", numerPersona);
			ingresoClave.putExtra("nombrePersona", nombrePersona);
			ingresoClave.putExtra("hilo", hilo);
			ingresoClave.putExtra("desdeConversacion", true);
		
			startActivity(ingresoClave);
			finish();}
		
	}
	
	public void verificarContenido(View view){
		String verificaTexto = "";
		EditText editorTexto;
		editorTexto = (EditText) findViewById(R.id.editTSMSOutConv);	
		verificaTexto = editorTexto.getText().toString(); 
		
		if(verificaTexto.length()>0){
			enviarSMS(verificaTexto);
		}
		else{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
			dialog.setTitle(R.string.tituloCDialogo);
		    dialog.setMessage(R.string.mensaje2CDialogo);
		    dialog.show();
		}
	}
	
	/*********************************************Enviar sms**************************************/	
	private void enviarSMS(String mensaje){
		CheckBox checkenvioCifrado = ((CheckBox)findViewById(R.id.radioEnvioCifrado));
		boolean checkCifradoEstado, editContenidoRespuestaActivado;
		SmsManager sms = SmsManager.getDefault();  
		Intent intentEnvio = new Intent("SMS_SENT");
		intentEnvio.putExtra("numero", numerPersona);		//Para que dentro del broatcastReceiver obtenga el num y texto para almacenar sms
		intentEnvio.putExtra("texto", mensaje);
		
		Context contexto = getApplicationContext();//Context de la aplicacion y no de la actividad, optimizar memoria
		PendingIntent piEnviado = PendingIntent.getBroadcast(contexto, 0, intentEnvio, PendingIntent.FLAG_UPDATE_CURRENT);
			//RecibeBroadcastEnvio.java recibe el PendingIntent para mostrar toast
		
		checkCifradoEstado = checkenvioCifrado.isChecked();
		editContenidoRespuestaActivado = ((EditText) findViewById(R.id.editTSMSOutConv)).isEnabled();
		
		if((checkCifradoEstado == true) && (editContenidoRespuestaActivado == false)){	//envio cifrado activo, sms cifrado
			sms.sendTextMessage(numerPersona, null, mensaje, piEnviado, null);          // envia mensaje cifrado, RecibeBroadcastEnvio espera respuesta de envio
			smsTexto = "";
		}
		if((checkCifradoEstado == true) && (editContenidoRespuestaActivado == true)){	//envio cifrado activado, sms no cifrado
			sms.sendTextMessage(numerPersona, null, mensaje, piEnviado, null); 
			smsTexto = "";
		}
		if((checkCifradoEstado == false) && (editContenidoRespuestaActivado == false)){	//envio cifrado desactivado, sms cifrado
			sms.sendTextMessage(numerPersona, null, smsTexto, piEnviado, null); 
			smsTexto = "";
		}
		if((checkCifradoEstado == false) && (editContenidoRespuestaActivado == true)){	//envio cifrado desactivado, sms no cifrado
			sms.sendTextMessage(numerPersona, null, mensaje, piEnviado, null); 
			smsTexto = "";
		}		
		new HiloBarraProgreso().execute("");	//ejecuta hilo que muestra circulo de proceso ENVIO, hasta que el SO da respuesta de estado de envio
	}
	
	/************************************************************Hilo mostrar progreso *********************************************/
	private class HiloBarraProgreso extends AsyncTask<String, Void, String>{		
		  
		@Override
		protected void onPreExecute(){	//antes de ejecutar el hilo
			progressBar.setVisibility(View.VISIBLE);
			estadoElementos(false);
		}
		
		@Override
		protected String doInBackground(String... params) {	//ejecucion del hilo hasta que se tiene respuesta de envio, desde sistema
			try{		    	
				boolean estadoEnvio = false;
				while(estadoEnvio == false){
					estadoEnvio = flagBroadcastOutSms;	//flagBroadcastEdoEnvio = true Cuando se recibe broadcast del SO del edo de envio
				}
		    	Thread.sleep(2000);
			} catch (Exception e){
				Log.e(MainActivity.class.toString(), e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result){	//despues de ejecutar el hilo
			 progressBar.setVisibility(View.GONE);
			 flagBroadcastOutSms = false;
			 mostrarListView();							//se hace consulta para actualizar lista de sms
			 setEditResponder("");
			 estadoElementos(true);
		}
	}	
	
	/**************************************************Hilo al recibir un SMS *********************************************/
	private class HiloNewSMS extends AsyncTask<String, Void, String>{		
		  
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
			 mostrarListView();							//se hace consulta para actualizar lista de sms
		}
	}	
	
	/*********************************************************************************************************************************/
	private void estadoElementos(boolean estado){
		 ((ImageButton) findViewById(R.id.btnEnviarConv)).setEnabled(estado);
		 ((ImageButton) findViewById(R.id.btnCifrarConv)).setEnabled(estado);	
		 //((Button) findViewById(R.id.btnCancelarConv)).setEnabled(estado);
		 ((EditText) findViewById(R.id.editTSMSOutConv)).setEnabled(estado);
	}
	private void setEditResponder(String text){
		((EditText) findViewById(R.id.editTSMSOutConv)).setText(text);	//texto a responder
	}
	
	/***********************************************Icono back***********************************************************/
	public void cerrarSMSConversacion(View view){
		finish();
		overridePendingTransition(R.anim.right_in,R.anim.right_out);
	}	
}