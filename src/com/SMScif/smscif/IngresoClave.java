/*Clase de la actividad que solicita la usuario la contrasena k para aplicar el cifrado, al conseguir la contraseña se aplica el cifrado al texto (sms) y lo regresa cifrado  a la actividad redactar  */

package com.SMScif.smscif;
import com.SMScif.algoritmoCifrar.ProsesarTexto;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class IngresoClave extends Activity {
	private String texto = "";
    private String [] smsSmsList;
    private String [] smsIdsList;
    private String [] smsThreadsList;
	private String textoCifrado ="";
	private String numero = "";
	private String persona = "";
	private boolean activateButton = false;	
	private String clave = "";
	private int tam; 
	private ProgressBar progressBar;
	final Handler mHandler = new Handler();
	private boolean desdeConversacion = false;
	private String hilo = "";
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//No aparesca barra de titulo por defecto
		setContentView(R.layout.activity_ingreso_clave);
		
		setTipografia();
		
		Bundle extras = getIntent().getExtras(); //obtiene valor de string enviados, sms
		texto = extras.getString("sms");
		numero = extras.getString("numero");//recibe numero para que no se borre
		persona = extras.getString("nombrePersona");
		activateButton= extras.getBoolean("activar");
	    smsSmsList=	 extras.getStringArray("smsSms");		
		smsIdsList=	 extras.getStringArray("smsIds");
		smsThreadsList=	 extras.getStringArray("smsThreads");
		desdeConversacion = extras.getBoolean("desdeConversacion");	//cuando se crea desde conversacion
		if(desdeConversacion == true)
			hilo = extras.getString("hilo");
		
///////if CifrarContactos o SMSPorContacto class llamo a esta class, then set invisible el boton para cifrado individual 	
	if (activateButton == true)	{
		findViewById(R.id.BotonAbrir).setVisibility(View.INVISIBLE);
		findViewById(R.id.BotonAbrir).setEnabled(false);
		findViewById(R.id.BotonCifAll).setVisibility(View.VISIBLE);
		findViewById(R.id.BotonCifAll).setEnabled(true);
        }
	else {
		findViewById(R.id.BotonAbrir).setVisibility(View.VISIBLE);
		findViewById(R.id.BotonAbrir).setEnabled(true);
		findViewById(R.id.BotonCifAll).setVisibility(View.INVISIBLE);
		findViewById(R.id.BotonCifAll).setEnabled(false);
		}
	//this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	
	private void setTipografia(){
		Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");	//tipografia  
		Typeface fontMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
		((TextView) findViewById(R.id.tituloClave)).setTypeface(fontMedium);
		((TextView) findViewById(R.id.textView1)).setTypeface(fontRegular);
		((TextView) findViewById(R.id.editTdescifrarSMS)).setTypeface(fontRegular);
	}
	
	/**************************************/
	@Override
	protected void onDestroy(){
		texto = "";
		textoCifrado ="";
		numero = "";
		((EditText) findViewById(R.id.editTdescifrarSMS)).setText("");
		smsSmsList=	null;		
		smsIdsList=	null;
		smsThreadsList= null;
		super.onDestroy();
	}	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ingreso_clave, menu);
		return true;
	}*/
/**********************************************************************/
	public void aplicarCifrado(View view){	
		int tamClave = 0;
		String clave = "";
		EditText editorTexto;
		editorTexto = (EditText) findViewById(R.id.editTdescifrarSMS);
		clave = editorTexto.getText().toString(); //conseguir clave textEdit, del usuario		
		tamClave = clave.length();
		
		if (tamClave >0){ /*si se ingreso una cadena para contraseña*/
			ProsesarTexto prosesarSms = new ProsesarTexto(); 
			textoCifrado  = prosesarSms.procesoCifrado(texto, clave);//cifrar  SMS,contraseña,contexto
			
			if (desdeConversacion == true){
				Intent regresoConversacion = new Intent(this,SMSConversacion.class);
				regresoConversacion.putExtra("smsCifrado", textoCifrado); //regresa le texto cifrado a la actividad redactar.
				regresoConversacion.putExtra("numero", numero);
				regresoConversacion.putExtra("nombrePersona", persona);
				regresoConversacion.putExtra("hilo", hilo);
				regresoConversacion.putExtra("sms", texto);
				startActivity(regresoConversacion);
				finish();  //finaliza la actividad, llama a onDestroy
			}
			else{
				Intent regresoRedactar = new Intent(this,RedactarActivity.class);
				regresoRedactar.putExtra("smsCifrado", textoCifrado); //regresa le texto cifrado a la actividad redactar.
				regresoRedactar.putExtra("numero", numero);
				regresoRedactar.putExtra("nombrePersona", persona);
				regresoRedactar.putExtra("sms", texto);
				startActivity(regresoRedactar);
				finish();  //finaliza la actividad, llama a onDestroy
			}
			
		}
		else{	//no se ingresa clave
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);	       
		    dialog.setTitle(R.string.tituloCDialogo);
		    dialog.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
		    dialog.setMessage(R.string.mensajeCifradoDialogo);
		    dialog.show();
		}
		
	}
	
/***********************metodo invocado al cifrar multiples sms *************************/
	public void aplicarCifradoVarios(View view){	
		
		int tamClave = 0;
		EditText editorTexto; ImageButton botonCifAll;  TextView tStatus;
		
		editorTexto = (EditText) findViewById(R.id.editTdescifrarSMS);
		botonCifAll = (ImageButton) findViewById(R.id.BotonCifAll);
		tStatus = (TextView) findViewById(R.id.txStatus);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
	     
		clave = editorTexto.getText().toString(); //conseguir clave textEdit, del usuario		
		tamClave = clave.length();
	
		if (tamClave >0){ ///*si se ingreso una cadena para contrasena
			//cuando este cifrando le bloqueamos el boton y el edit a la capa 8 
			//para q no vaya a lanzar otra vez el hilo a la cola de ejecucion
			botonCifAll.setEnabled(false);
			botonCifAll.setVisibility(View.INVISIBLE);
			editorTexto.setEnabled(false);
        
			progressBar.setVisibility(View.VISIBLE);
			tStatus.setVisibility(View.VISIBLE);
			
			//se llama a otro hilo para q ejecute el cifrado y update en 2o plano
			threadUpdate();	
		}
		else{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);	       
		    dialog.setTitle(R.string.tituloCDialogo);
		    dialog.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
		    dialog.setMessage(R.string.mensajeCifradoDialogo);
		    dialog.show();
		 		}
			
	}
	
/*********************Hilo q cifrara y actualizara************************************/
	  protected void threadUpdate() {
		 //para colocar el limite de la barra de progress
		  tam = smsThreadsList.length;
		  progressBar.setMax(tam-1);
		 //t sera el obj hilo 
		  Thread t = new Thread(){
			   public void run() {
			
				try {
					String message = "", id = "" , thread ="";
					String[] arg;
					Uri mensajes = Uri.parse("content://sms/");	
					ProsesarTexto prosesarSms = new ProsesarTexto();
					ContentValues val = new ContentValues(); 
					
						for(int i=0; i< tam;i++){ //el proceso cifra sms por sms
							  progressBar.setProgress(i); //para q la barra muestre el progreso 
							  id = smsIdsList[i];
						      thread = smsThreadsList[i];  //id thread y message son los datos a cifrar
						      message = smsSmsList[i];
						      arg = new String [] {thread, id};//argumentos a cumplir para actualizar
						       
						      	textoCifrado  = prosesarSms.procesoCifrado(message, clave);
			                    val.put("body" , textoCifrado); //se reemplazara el body actual por su equivalente cifrado
			                    getContentResolver().update(mensajes, val, "thread_id=? and _id=? ", arg);
			               }	
						Thread.sleep(100);//espera en 100ms para avisar q acabo su proceso
					 } catch (InterruptedException e) //espera la interrupcion del hilo
					{
						e.printStackTrace(); 
					} 
				mHandler.post(ejecutarAccion);//al acabar el hilo
				}
		};
		t.start();  //arrancar el hilo 
	  }	
	  //solo notifica y cierra la actividadq lo llamo
	 final Runnable ejecutarAccion = new Runnable (){
		 public void run(){
		Toast.makeText(IngresoClave.this, R.string.succesful_encrypt, Toast.LENGTH_SHORT).show();
		IngresoClave.this.finish();
		 }
	 };
	  
	 public void cerrarIngresoclave(View view){
		 finish();
	 }
	  
	  
}

