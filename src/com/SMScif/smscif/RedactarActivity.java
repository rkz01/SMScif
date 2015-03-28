/*
 * Clase en la que se ingresa el texto del sms para ser enviado
 * o cifrado, si es el caso de cifrado se prosigue con la opcion de introducir 
 * la contraseña
 * 
 */
package com.SMScif.smscif;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import android.telephony.SmsManager; //manejo de mensajes de texto

public class RedactarActivity extends Activity {
	private String smsTexto="";
	private String numero="";
	private String smsCipher="";
	private String nombrePersona = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//No aparesca barra de titulo por defecto
		setContentView(R.layout.activity_redactar);
		
		setTipografia();
		
		Bundle extras = getIntent().getExtras();
		smsTexto = extras.getString("sms");  //consigue el string quecontiene texto cifrado
		((EditText) findViewById(R.id.editTSMSOut)).setText(smsTexto);  //coloca en editorTexto el sms cifrado
		
		numero = extras.getString("numero");
		((EditText) findViewById(R.id.editTnumeroOut)).setText(numero);
		
		nombrePersona = extras.getString("nombrePersona");
		int contenidoTexto = nombrePersona.length();	//verificar si existe un nombre de contacto
			if(contenidoTexto > 0){						//si se tiene nombre de contacto, cuando regresa de seleccionar contacto
				((TextView) findViewById(R.id.textPersonaRedactar)).setText(nombrePersona);//coloca nombre de contacto
				((TextView)findViewById(R.id.editTnumeroOut)).setEnabled(false);	//para evitar modificar numero de contacto	
			}
		
		smsCipher = extras.getString("smsCifrado");  //consigue el string quecontiene texto cifrado
		contenidoTexto = smsCipher.length();	/*comprobar si se tiene texto a enviar*/
		if (contenidoTexto > 0){
			TextView textoCifrado = ((TextView) findViewById(R.id.textVSMSOutCifrido));
			textoCifrado.setBackgroundColor(getResources().getColor(R.color.white));	//color de fondo
			textoCifrado.setText(smsCipher);		//muestra texto cifrado
			textoCifrado.requestFocus();
			((TextView)findViewById(R.id.textVEtiquetaCifro)).setText(R.string.text_etiquetaCifradoRedactar);	//etiqueta mensaje cifrado
			((ImageButton)findViewById(R.id.BotonCifrar)).setEnabled(false);		//desactiva boton cifrado, evitar cifrar dos veces
			
		}
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);	//para que no aparesca en automatico el teclado al iniciar actividad
		
	}
	
	private void setTipografia(){
		Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");	//tipografia  
		Typeface fontMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
		Typeface fontLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
				
		((TextView) findViewById(R.id.textPersonaRedactar)).setTypeface(fontMedium);
		((EditText) findViewById(R.id.editTnumeroOut)).setTypeface(fontRegular);
		((EditText) findViewById(R.id.editTSMSOut)).setTypeface(fontRegular);
		((TextView) findViewById(R.id.textVEtiquetaCifro)).setTypeface(fontRegular);
		((TextView) findViewById(R.id.textVSMSOutCifrido)).setTypeface(fontLight);
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
	/***************************************************************************************/
	@Override
	protected void onDestroy(){ //al destruir la actividad
		smsTexto = null;	
		numero = null;
		smsCipher = null;
		((EditText) findViewById(R.id.editTSMSOut)).setText(""); 	//borrar texto plano
		((EditText) findViewById(R.id.editTnumeroOut)).setText(""); //borra numero
		((TextView) findViewById(R.id.textPersonaRedactar)).setText("");//borra nombre de contacto
		((TextView) findViewById(R.id.textVSMSOutCifrido)).setText(""); //borra texto cifrado
		super.onDestroy();
	}
	@Override
	protected void onStop(){ //cuando esta actividad se detiene
		((EditText) findViewById(R.id.editTSMSOut)).setText(""); //borrar texto plano
		smsTexto = null;
		super.onStop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.redactar, menu);
		return true;
	}	
	/*********************************************************************/
	public void ingresoClave(View view){
		EditText editorTexto; AlertDialog.Builder dialogo = new AlertDialog.Builder(this);		
		Intent abrirIngresoClave = new Intent(this,IngresoClave.class);//evento abrir ventana IngrsoClave
		
		editorTexto = (EditText) findViewById(R.id.editTSMSOut);  //consigue texto SMS		
		smsTexto = editorTexto.getText().toString(); //consigue cadena caracteres 
		if(smsTexto.length()==0){
			dialogo.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
			dialogo.setTitle(R.string.tituloCDialogo);
		    dialogo.setMessage(R.string.mensaje2CDialogo);
		    dialogo.show();	
		}
		else {
		abrirIngresoClave.putExtra("sms", smsTexto); //coloca cadena caraceteres
		editorTexto = (EditText) findViewById(R.id.editTnumeroOut);
		numero = editorTexto.getText().toString();
		abrirIngresoClave.putExtra("numero", numero); //para que vuelva a crear esta actividad
		abrirIngresoClave.putExtra("nombrePersona", nombrePersona);
		
		startActivity(abrirIngresoClave);
		finish();
		} 
	}
	
	public void contactos(View view){
		EditText editorTexto;//almaceanr mensaje si se ingresa antes de numero
		editorTexto = (EditText) findViewById(R.id.editTSMSOut);  //consigue texto SMS		
		smsTexto = editorTexto.getText().toString(); //consigue cadena caracteres 
		
		Intent verContactos = new Intent(this,ContactosMostrar.class);
		verContactos.putExtra("smsCifrado", smsCipher);
		startActivity(verContactos);
		finish();
	}
	
	public void verificarContenido(View view){   //envia un mensaje de texto
		boolean verificarNumero = false;
		int contenidoTextoCipher, contenidoTextoPlano;
		EditText editorTexto;
		TextView textoCifrado;
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		
		editorTexto = (EditText) findViewById(R.id.editTnumeroOut);
		numero = editorTexto.getText().toString();
		editorTexto = (EditText) findViewById(R.id.editTSMSOut);
		smsTexto = editorTexto.getText().toString(); 
		textoCifrado = (TextView) findViewById(R.id.textVSMSOutCifrido);
		smsCipher = textoCifrado.getText().toString();
		
		
		verificarNumero = comprobarNumero(numero);	/*comprobar si se ingreso el numero*/
		contenidoTextoCipher = smsCipher.length();	/*comprobar si se tiene texto cifrado*/
		contenidoTextoPlano = smsTexto.length();	/*comprobar si se tiene texto plano*/
		
		if ((verificarNumero == true) && (contenidoTextoCipher > 0)){
			enviarSMS(smsCipher);		//envia texto cifrado	
		}
		else if(verificarNumero == false){		/*si no se ingreso numero muestra cuadro de dialogo advirtiendo*/
			   dialog.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
			   dialog.setTitle(R.string.tituloCDialogo);	/*consigue titulo desde string.xml*/
		       dialog.setMessage(R.string.mensaje1CDialogo);
		       dialog.show();
		}
		else if(contenidoTextoPlano == 0){			/*muestra cuadro dialogo si no existe texto plano*/
			   dialog.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
			   dialog.setTitle(R.string.tituloCDialogo);
		       dialog.setMessage(R.string.mensaje2CDialogo);
		       dialog.show();
		}
		else if((contenidoTextoPlano > 0)&&(contenidoTextoCipher == 0)){			/*muestra cuadro dialogo si no existe texto cifrado pero si texto plano*/
			   dialog.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
			   dialog.setTitle(R.string.tituloCDialogo);
		       dialog.setMessage(R.string.mensaje3CDialogo);	//pregunta
		       dialog.setPositiveButton(R.string.boton_aplicarCifradoClave,	//OK
		    		   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   enviarSMS(smsTexto);	//envia texto plano
                   }
               });
		       dialog.setNegativeButton(R.string.boton_Cancelar,
		                new DialogInterface.OnClickListener() {
		    	   		public void onClick(DialogInterface dialog, int id) {
		    	   			dialog.cancel();
                   }
               });
		       dialog.show();
		}
	}
	
	private boolean comprobarNumero(String cadena){	/*comprobar si el string tiene numero*/
		boolean esNumero = false;
		int size = cadena.length();  
		   for(int i = 0; i < size; i ++) {  
			   esNumero = Character.isDigit(cadena.charAt(i));  /*comprobar si el caracter es numerico*/
		   }  
		return(esNumero);
	}
	
	/**********************************************************************************************************************/
	private void enviarSMS(String textoEnviar){		
		SmsManager sms = SmsManager.getDefault();  
		Intent intentEnvio = new Intent("SMS_SENT");
		intentEnvio.putExtra("numero", numero);		//Para que dentro del broatcastReceiver obtenga el num y texto para almacenar sms
		intentEnvio.putExtra("texto", textoEnviar);
		
		Context contexto = getApplicationContext();//Context de la aplicacion y no de la actividad, optimizar memoria
		PendingIntent piEnviado = PendingIntent.getBroadcast(contexto, 0, intentEnvio, PendingIntent.FLAG_UPDATE_CURRENT);
			//RecibeBroadcastEnvio.java recibe el PendingIntent para mostrar toast
		
		sms.sendTextMessage(numero, null, textoEnviar, piEnviado, null); // envia mensaje, RecibeBroadcastEnvio espera respuesta de envio
		finish();//al eliminar esta actividad vuelve a estar visible la actividad principal
	}
	
	/***********************Al presionar imageBottom deshacer****************************/
	public void deshacer(View view){
		smsTexto = "";
		smsCipher = "";		
		numero = "";
		Context contexto = getApplicationContext();
		nombrePersona = contexto.getResources().getString(R.string.mensajeNuevoSms);
		
		((EditText) findViewById(R.id.editTSMSOut)).setText(smsTexto);
		((EditText) findViewById(R.id.editTnumeroOut)).setText(numero);
		((TextView) findViewById(R.id.textPersonaRedactar)).setText(nombrePersona);		
		
		((TextView)findViewById(R.id.textVEtiquetaCifro)).setText("");
		
		((TextView)findViewById(R.id.editTnumeroOut)).setEnabled(true);
		((ImageButton)findViewById(R.id.BotonCifrar)).setEnabled(true);
		
		((TextView) findViewById(R.id.textVSMSOutCifrido)).setText(smsCipher);
		//((TextView) findViewById(R.id.textVSMSOutCifrido)).setBackgroundResource(R.drawable.lineatransparente);
		//textoCifrado.setBackgroundColor(getResources().getColor(R.color.white));	//color de fondo
		//textoCifrado.setText(smsCipher);		//muestra texto cifrado
		//textoCifrado.requestFocus();		
	}
	
	/***********************************Al presionar icono regreso ****************************/
	public void cerrar(View view){
		finish();
		overridePendingTransition(R.anim.right_in,R.anim.right_out);
	}
}