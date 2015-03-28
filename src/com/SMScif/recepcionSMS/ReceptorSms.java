/*
 * Clase que se encuentra ejecutandose en segundo plano y cuando recibe el broadcast 
 * de que se ha recibido un nuevo sms
 *  verifica si es cifrado para abrirlo*/
package com.SMScif.recepcionSMS;

import com.SMScif.auxiliares.InfoSistema;
import com.SMScif.auxiliares.NotificacionBarra;
import com.SMScif.auxiliares.VerificarCifrado;
import com.SMScif.smscif.LeerDescifrar;
import com.SMScif.smscif.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class ReceptorSms extends BroadcastReceiver{
	private static final String ACTION_SMS_IN = "android.provider.Telephony.SMS_RECEIVED";
	
	public void onReceive(Context context, Intent intent){
		Bundle bundle = intent.getExtras();
		String action = intent.getAction();	
		SmsMessage [] sms = null;
		String mensajeRecibido = "", numeroR = "";
		VerificarCifrado verificarCifrado = new VerificarCifrado();		//checa si el sms tiene espacio en blanco
		
		if (action.equals(ACTION_SMS_IN)) {
			if (bundle != null){
				Object[] pdus = (Object[]) bundle.get("pdus");
				sms = new SmsMessage[pdus.length];
							
				for(int i=0; i<sms.length; i++){
					sms[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
					mensajeRecibido = sms[i].getMessageBody().toString();
					numeroR = sms[i].getOriginatingAddress();			
				}
				
				if (verificarCifrado.verificarSMSCifrado(mensajeRecibido)){
					NotificacionBarra notificacion = new NotificacionBarra();
					String titulo = context.getResources().getString(R.string.mensajeNuevoSms);	
					String persona = "";
					
					InfoSistema informacion = new InfoSistema(context);
					persona = informacion.conseguirNombreContacto(numeroR);
					
					Intent intentDescifrar = new Intent(context, LeerDescifrar.class);
					intentDescifrar.putExtra("numeroIn", numeroR);
					intentDescifrar.putExtra("smsInCifrado", mensajeRecibido);
					intentDescifrar.putExtra("persona", persona);
					intentDescifrar.putExtra("personaIn", persona);
					intentDescifrar.putExtra("tipoSms", "");
					
					notificacion.mostrarNotificacion(context, persona, titulo, mensajeRecibido, intentDescifrar);				
				}
				   
				Intent i = new Intent();
				i.setAction("SMS_NEW");	//Para actividad de conversacion 
				context.sendBroadcast(i);
			}				
		}		
	}	
}
