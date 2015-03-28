/* Clase que crea y muestra una notificacion en la barra de estado*/
package com.SMScif.auxiliares;

import com.SMScif.smscif.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificacionBarra {
	private static final int ID_NOTIFICACION = 1;
		
	/*(contexto de la app, titulo al aparecer en la barra de edo., titulo al abrir b.edo., informacion aparece, actividad a abrir al dar clic )*/
	public void mostrarNotificacion (Context context, String titulo, String texto, String textoExpandido, Intent intent){
		NotificationManager nm;
		Notification notificacion;
		
		nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificacion = new Notification(	//notificacion 
				R.drawable.ic_stat_notificacion,	//icono notificacion
				texto,
				System.currentTimeMillis()	//para obtener instante en que se creo
				);
		
		if (intent == null){
			notificacion.setLatestEventInfo(context, titulo, textoExpandido, null);
		}
		else{
			PendingIntent intentoP = PendingIntent.getActivity(context,0,intent, 0);	//intent contendra la actividad a abrir, puede ser null
			notificacion.setLatestEventInfo(context, titulo, textoExpandido, intentoP);			
		}
				
		
		
		notificacion.flags = Notification.FLAG_AUTO_CANCEL;	//Es eliminado cuando el usuario da clic sobre la notificacion
		
		nm.notify(ID_NOTIFICACION, notificacion);		//lanza notificacion		
		
	}
}
