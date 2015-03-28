package com.SMScif.auxiliares;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class InfoSistema {
	private Context context;
	
	public InfoSistema(Context contexto){
		context = contexto;
	}
		
	public String conseguirNombreContacto(String numeroS){
		String nombreContacto = "", nombreP = "", numeroP = "", numeroEntrada = "";
		
		Cursor cP = context.getContentResolver().query(	//guarda los datos de consulta en un cursor
				Data.CONTENT_URI, new String[] {Data._ID, Data.DISPLAY_NAME, Phone.NUMBER},
				Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE +"' AND " + Phone.NUMBER + " IS NOT NULL",
				null, Data.DISPLAY_NAME + " ASC");
		
		int indiceNombre = cP.getColumnIndexOrThrow(Data.DISPLAY_NAME);
		int inidiceNumero = cP.getColumnIndex(Phone.NUMBER);
		
		if (numeroS.charAt(0) == '+'){	//si el numero esta en formato internacional +52
			for (int x = 3; x < numeroS.length(); x++) {
				numeroEntrada += numeroS.charAt(x);
			}
		}		
		else 
			numeroEntrada = numeroS;
		
		nombreContacto = numeroEntrada;
		while(cP.moveToNext()){//recorre contactos para obtener el nombre en cada sms
			nombreP = cP.getString(indiceNombre);
			numeroP = cP.getString(inidiceNumero);					
			if (numeroP.equals(numeroEntrada)){	//si el numero de contacto es igual al numero del mensaje obtiene nombre de contacto
				nombreContacto = nombreP;						
			}			
		}				
		cP.moveToFirst();	//rebobinar datos de contacto	
		//cP.close();
		return nombreContacto;
	}	
}
