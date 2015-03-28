package com.SMScif.auxiliares;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import android.text.format.DateFormat;

public class FechaFormato {
	private String anioActual="", mesActual="", diaActual=""; 
	
	public FechaFormato(){
		Calendar cal = new GregorianCalendar();
		Date date = cal.getTime();		
		diaActual = (String) DateFormat.format("dd",date);
		mesActual = (String) DateFormat.format("MMM",date);
		anioActual = (String) DateFormat.format("yyyy",date);
	}
		
	public String formatoFechaConversacion(String anio, String mes, String dia, String hora){
		String formatoMostrar = "";
		
		if((diaActual.equals(dia)) && (mesActual.equals(mes)) && (anioActual.equals(anio))){
			formatoMostrar = hora;		//Si la fecha es hoy, solo mostrar la hora 
		}
		else if (anioActual.equals(anio)){//Cuando es dentro del mismo año
			formatoMostrar = mes+" "+dia;
		}
		else{
			formatoMostrar = mes+" "+dia+", "+anio;
		}
   	 
		return formatoMostrar;
	}
	
	
	public String formatoFechaDetalle(String anio, String mes, String dia, String hora){
		String formatoMostrar = "";		
		
		if((diaActual.equals(dia)) && (mesActual.equals(mes)) && (anioActual.equals(anio))){
			formatoMostrar = hora;		//Si la fecha es hoy, solo mostrar la hora 
		}
		else if (anioActual.equals(anio)){//Cuando es dentro del mismo año
			formatoMostrar = mes+" "+dia+", "+hora;
		}
		else{
			formatoMostrar = mes+" "+dia+", "+anio+"  "+hora;
		}
   	 
		return formatoMostrar;
	}

}
