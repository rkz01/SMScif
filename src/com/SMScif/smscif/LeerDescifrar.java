/*
 * Clase de la actividad leer que se tiene que ingresar un texto cifrado
 * y su clave para ser descifrada, el texto descifrado se muestra en esta
 * misma actividad 
 */
package com.SMScif.smscif;
  
import com.SMScif.algoritmoCifrar.ProsesarTexto;
import com.SMScif.auxiliares.VerificarCifrado;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageButton;

public class LeerDescifrar extends Activity {
	private String smsCifrado = "";
	private String clave = "";
	private String smsDescifrado = "";
	private String numero = "", persona = "", personaSelected = "";
	private String tipoSMs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//No aparesca barra de titulo por defecto
		setContentView(R.layout.activity_leerdescifrar);
		
		setTipografia();
		Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");	//solo para textView que muestra SMS
		Typeface fontLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
		
		Bundle extras = getIntent().getExtras();
		smsCifrado = extras.getString("smsInCifrado");  //consigue el string quecontiene texto cifrado
		((TextView) findViewById(R.id.textVdescifrarSMS)).setText(smsCifrado);  //coloca en editorTexto el sms cifrado
		VerificarCifrado verificarCifrado = new VerificarCifrado();
		if (!verificarCifrado.verificarSMSCifrado(smsCifrado)){	//Si un sms no es cifrado no muestra ingreso de contraseña
			((EditText) findViewById(R.id.editTDescifrarClave)).setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.textEtiquetaLeerSMS)).setVisibility(View.INVISIBLE);
			((ImageButton) findViewById(R.id.BotonDescifrar)).setEnabled(false);
			((TextView) findViewById(R.id.textVdescifrarSMS)).setTypeface(fontRegular);
		}
		else{	//si un SMS esta cifrado
			((TextView) findViewById(R.id.textVdescifrarSMS)).setTypeface(fontLight);
		}
		
		numero = extras.getString("numeroIn");  //consigue el numero 
		persona = extras.getString("personaIn");
		personaSelected = extras.getString("persona");
		tipoSMs = extras.getString("tipoSms");
		  
		ImageButton botonReply = (ImageButton) findViewById(R.id.BotonResponder);
		if((tipoSMs.equals("no sent"))||(tipoSMs.equals("rough"))){		
			botonReply.setImageResource(R.drawable.ic_action_forward);	  
		}
		else{		
			botonReply.setImageResource(R.drawable.ic_action_reply);}
		
		
		//si la persona es yo no se muestra el numero, debido a que getline1() no siempre apunta al numero local		 
		if(persona.equals("me")){
			((TextView) findViewById(R.id.textNumeroSmsPersona)).setText(R.string.text_me);
			((TextView) findViewById(R.id.textNumeroSms)).setVisibility(View.INVISIBLE);
			botonReply.setImageResource(R.drawable.ic_action_forward);
		}
		else if(persona.equals("rough")){
			 ((TextView) findViewById(R.id.textNumeroSmsPersona)).setText(R.string.mensajeRoughDraft);
			 ((TextView) findViewById(R.id.textNumeroSms)).setVisibility(View.INVISIBLE);
			 botonReply.setImageResource(R.drawable.ic_action_forward);
		}
		else if((!persona.equals("rough")||(!persona.equals("me")))){		/*SMS entreda*/
			((TextView) findViewById(R.id.textNumeroSms)).setText(numero);
			((TextView) findViewById(R.id.textNumeroSmsPersona)).setText(personaSelected);
			botonReply.setImageResource(R.drawable.ic_action_reply);
		}
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);	//para que no aparesca en automatico el teclado al iniciar actividad
	}
	
	private void setTipografia(){
		Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");	//tipografia  
		Typeface fontMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");		
		
		((TextView) findViewById(R.id.textNumeroSmsPersona)).setTypeface(fontMedium);	//titulo de activity
		((TextView) findViewById(R.id.textNumeroSms)).setTypeface(fontRegular); 
		((TextView) findViewById(R.id.textEtiquetaLeerSMS)).setTypeface(fontRegular);
		((EditText) findViewById(R.id.editTDescifrarClave)).setTypeface(fontRegular);			
	}
	/*****************************************************************************/
	@Override
	protected void onDestroy(){ //cuando la actividad es destruida
		((TextView) findViewById(R.id.textVdescifrarSMS)).setText("");//borrar textodescifrado
		((EditText) findViewById(R.id.editTDescifrarClave)).setText("");//borrar clave
		((TextView) findViewById(R.id.textNumeroSms)).setText("");
		smsCifrado = "";
		clave = "";
		smsDescifrado = "";
		super.onDestroy();
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
	/*************************************************************************************************/
	@Override
	protected void onPause(){ //cuando esta actividad pasa a pausa
		((TextView) findViewById(R.id.textVdescifrarSMS)).setText("");//borrar textodescifrado
		((EditText) findViewById(R.id.editTDescifrarClave)).setText("");//borrar clave
		((TextView) findViewById(R.id.textNumeroSms)).setText(""); /*borrar numero*/
		smsCifrado = "";
		clave = "";
		smsDescifrado = "";	
		super.onPause();
	}	
	@Override
	protected void onStop(){ //cuando esta actividad pasa a pausa
		((TextView) findViewById(R.id.textVdescifrarSMS)).setText("");//borrar textodescifrado
		((EditText) findViewById(R.id.editTDescifrarClave)).setText("");//borrar clave
		((TextView) findViewById(R.id.textNumeroSms)).setText(""); /*borrar numero*/
		smsCifrado = "";
		clave = "";
		smsDescifrado = "";	
		super.onStop();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.leer, menu);
		return true;
	}
	/*************************************************************************************/		
	public void descifrar(View view){
		EditText editorTexto;	
		TextView vistaTexto;
		int tamClave=0;
		
		vistaTexto = (TextView) findViewById(R.id.textVdescifrarSMS); //obtener texto cifrado
		smsCifrado = vistaTexto.getText().toString();
		editorTexto = (EditText) findViewById(R.id.editTDescifrarClave); //obtener clave
		clave = editorTexto.getText().toString(); 
		
		tamClave = clave.length();
		if (tamClave != 0){
			ProsesarTexto procesarTexto= new ProsesarTexto();	//descifrar
			smsDescifrado = procesarTexto.procesoDescifrado(smsCifrado, clave); 
			((TextView) findViewById(R.id.textVdescifrarSMS)).setText(smsDescifrado);//mostrar textodescifrado
			((TextView) findViewById(R.id.textVdescifrarSMS)).requestFocus();
			((TextView) findViewById(R.id.textEtiquetaLeerSMS)).setText(R.string.text_descifrarSMSMostrar);
			((EditText) findViewById(R.id.editTDescifrarClave)).setText("");//para clave
			((EditText) findViewById(R.id.editTDescifrarClave)).setVisibility(View.INVISIBLE);	//ocultar
			((ImageButton) findViewById(R.id.BotonDescifrar)).setEnabled(false);
			Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
			((TextView) findViewById(R.id.textVdescifrarSMS)).setTypeface(fontRegular);
		}
		
		else{	//si no se intruce clave descifrado, muestra cuadro dialogo
			 AlertDialog.Builder dialog = new AlertDialog.Builder(this);	       
		     dialog.setTitle(R.string.tituloCDialogo);
		     dialog.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
		     dialog.setMessage(R.string.mensajeDescifradoDialogo);
		     dialog.show();
			
		}
		
	}
	
	public void responder(View view){
		TextView vistaTexto;
				
		vistaTexto = (TextView) findViewById(R.id.textNumeroSms);
		numero = vistaTexto.getText().toString();
		vistaTexto = (TextView) findViewById(R.id.textNumeroSmsPersona);
		persona = vistaTexto.getText().toString();
		if(tipoSMs.equals("no sent")||tipoSMs.equals("rough")){     
			//Cuando el mensaje sea tipo "no enviado" o "borrador"se pasa el texto 
			//para q lo pueda reenviar en lugar de responder
			Intent abrirRedactar = new Intent(this,RedactarActivity.class);
			abrirRedactar.putExtra("smsCifrado", smsCifrado);
			abrirRedactar.putExtra("sms", "");
			abrirRedactar.putExtra("numero", numero);
			abrirRedactar.putExtra("nombrePersona", ""); 
			startActivity(abrirRedactar);
			}
		else{   
			Intent abrirRedactar = new Intent(this,RedactarActivity.class);
			abrirRedactar.putExtra("smsCifrado", "");
			abrirRedactar.putExtra("sms", "");
			abrirRedactar.putExtra("numero", numero);
			abrirRedactar.putExtra("nombrePersona", persona);
			startActivity(abrirRedactar);
			}
		finish();//al eliminar esta actividad vuelve a estar visible la actividad principal
	}
	
	public void cerrarLeerDescifrar(View view){
		finish();
		overridePendingTransition(R.anim.right_in,R.anim.right_out);
	}
}
