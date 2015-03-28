/*
 * Clase qeu detecta el broadcast que se genera al enviar un sms, realiza el registro 
 * dentro del sistema del envio de sms
 * utiliza la actividad com.SMSCIF.smscif.MostrarCDialogo.java para generar cuadros de dialogo  */
package com.SMScif.auxiliares;

import java.util.Date;
import com.SMScif.smscif.R;
import com.SMScif.smscif.RedactarActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

public class RecibeBroadcastEnvio extends BroadcastReceiver  {
	private static final String ACTION_SMS_SENT = "SMS_SENT";
	private String numero, textoEnviar, tipo;
	//private String titulo, titulo1, textoExpandido;
	
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();	
		String titulo, titulo1, textoExpandido, persona;
		
		if (action.equals(ACTION_SMS_SENT)) {
			Bundle extras = intent.getExtras();
			numero = extras.getString("numero");
			textoEnviar = extras.getString("texto");

			NotificacionBarra crearNotificacion = new NotificacionBarra();
			InfoSistema informacion = new InfoSistema(context);	//instancia para conseguir nombre contacto a partir de numero
			persona = informacion.conseguirNombreContacto(numero);
            Intent abrirRedactar = new Intent(context,RedactarActivity.class);	//actividad para reenviar, en caso de no se alla enviado sms
						
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                	tipo = "2"; // el sms fue enviado
					moveToSent(context);//llama metodo para ejecucion de hilo
                	String mEnvio = context.getResources().getString(R.string.mensajeEnviado);
                    Toast.makeText(context, mEnvio, Toast.LENGTH_LONG).show();
                    break;
                    
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                	tipo = "5"; // fallo enviado
					moveToSent(context);
                    titulo = context.getResources().getString(R.string.mensajeFalloProblema);	//titulo al abrir barra 
                    textoExpandido = context.getResources().getString(R.string.mensajeFalloEnvioGeneric);
                    titulo1 = context.getResources().getString(R.string.mensajeFalloEnvio);	//titulo al aparecer notificacion
        			abrirRedactar.putExtra("smsCifrado", textoEnviar);	//text cifrado
        			abrirRedactar.putExtra("sms", "");
        			abrirRedactar.putExtra("numero", numero);
        			abrirRedactar.putExtra("nombrePersona", persona); 
                    crearNotificacion.mostrarNotificacion(context, titulo1, titulo, textoExpandido,abrirRedactar);//muestra notificacion en barra 
                    break;
                    
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                	tipo = "5"; 
					moveToSent(context);
                	titulo = context.getResources().getString(R.string.mensajeFalloProblema);
                    textoExpandido = context.getResources().getString(R.string.mensajeErrorServicio);
                    titulo1 = context.getResources().getString(R.string.mensajeFalloEnvio);
        			abrirRedactar.putExtra("smsCifrado", textoEnviar);
        			abrirRedactar.putExtra("sms", "");
        			abrirRedactar.putExtra("numero", numero);
        			abrirRedactar.putExtra("nombrePersona", persona); 
                    crearNotificacion.mostrarNotificacion(context, titulo1, titulo, textoExpandido,abrirRedactar);
                    break;
                    
                case SmsManager.RESULT_ERROR_NULL_PDU:
                	tipo = "5"; 
					moveToSent(context);
					titulo1 = context.getResources().getString(R.string.mensajeFalloEnvio);
					titulo = context.getResources().getString(R.string.mensajeFalloProblema);
                    textoExpandido = context.getResources().getString(R.string.mensajeErrorPDU);    				
        			abrirRedactar.putExtra("smsCifrado", textoEnviar);
        			abrirRedactar.putExtra("sms", "");
        			abrirRedactar.putExtra("numero", numero);
        			abrirRedactar.putExtra("nombrePersona", persona); 
                    crearNotificacion.mostrarNotificacion(context, titulo1, titulo, textoExpandido,abrirRedactar);
                    break;
                    
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                	tipo = "5"; 
					moveToSent(context);
					titulo1 = context.getResources().getString(R.string.mensajeFalloEnvio);
                	titulo = context.getResources().getString(R.string.mensajeFalloProblema);
                    textoExpandido = context.getResources().getString(R.string.mensajeErrorRadio);  
        			abrirRedactar.putExtra("smsCifrado", textoEnviar);
        			abrirRedactar.putExtra("sms", "");
        			abrirRedactar.putExtra("numero", numero);
        			abrirRedactar.putExtra("nombrePersona", persona); 
                    crearNotificacion.mostrarNotificacion(context, titulo1, titulo, textoExpandido,abrirRedactar);
                    break;                
            }
        }
    }	
	
	
	
	
	private void moveToSent(final Context context) {
		 //t sera el obj hilo 
		  Thread t = new Thread(){
			  private String numberPhone=numero;
			  private String textBody=textoEnviar;
			  private String typeSMS=tipo;
			  private String threadS = "", threadCompare = "";
			  Uri mensajes = Uri.parse("content://sms/");
			  Uri mmssms = Uri.parse("content://mms-sms/threadID/");
				
			  public void run() {
				  
				try {    
					ContentValues value = new ContentValues();
				
					FiltrarNumeroTel filter = new FiltrarNumeroTel();
					numberPhone = filter.filtrarNumeroTelefono(numberPhone);
					  								
					//String[] projection = new String[] {"max("+"thread_id"+")AS maximo"};//clausula de maximo hilo de conversacion
					String[] projection = new String[] { "address", "thread_id" }; 
					String[] arg = new String[] { numberPhone };
					Cursor cursorThread = context.getContentResolver().query(mensajes, projection,"address=?", arg, null);
					
						try{//si encuentra el numero en una conversacion obtiene su hilo
							cursorThread.moveToFirst();
							threadCompare = cursorThread.getString(cursorThread.getColumnIndex("thread_id"));
							threadS = threadCompare;
						  }
						catch(Exception e){
							//si no estuviese el num en alguna conversacion crea recipiente nuevo
							Uri.Builder builder = mmssms.buildUpon(); 
							String[] recipientNew = {numberPhone}; 
							
							for(String recipient : recipientNew){
								builder.appendQueryParameter("recipient", recipient);
								}	
							 Uri uriBuild = builder.build();
							 Long threadId = (long) 0; 
							 //trae el id(_id) del hilo de conversacion(thread_id) creado por el recipiente
							 Cursor cursorId = context.getContentResolver().query(uriBuild, new String[]{"_id"}, null, null, null);
							 if (cursorId != null) { 
								 try {  
									 if (cursorId.moveToFirst()) { 
										 threadId = cursorId.getLong(0); 
										 threadS=threadId.toString();  
										 //Toast.makeText(getApplicationContext(),"threadS new"+ threadS, Toast.LENGTH_LONG).show();
									 	} 
								 	}
								 finally { cursorId.close(); }
								 } //Toast.makeText(getApplicationContext(),"No encontred:"+ numberPhone , Toast.LENGTH_LONG).show();
						}
				
						value.put("thread_id", threadS); 
						value.put("body", textBody);
						value.put("address", numberPhone);
						value.put("date", new Date().getTime());
						value.put("type", typeSMS);
				
						context.getContentResolver().insert(mensajes, value);
						//Toast.makeText(getApplicationContext(),"hilo:"+threadS+" Num:"+numberPhone+" Tipo:"+typeSMS, Toast.LENGTH_LONG).show();
			   
						Thread.sleep(100);//espera en 100ms para mandar su interrupcion
					 } 
					catch (InterruptedException e) //espera la interrupcion del hilo
						{e.printStackTrace(); } 
				
				//mHandler.post(ejecutarAccion);//al acabar el hilo
				}
		  };
		t.start();  //arrancar el hilo 
	  }		  
}
