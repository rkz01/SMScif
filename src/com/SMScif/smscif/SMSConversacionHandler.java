/*manejador (handler), cada objeto almacenara informacion correspondiente
 * a un sms en especifico
 * */
package com.SMScif.smscif;

public class SMSConversacionHandler {
	public String textoFecha = null;
	public String textoCuerpo = null;
	public String textoPerson = null;
	public String textoNum = null;
	public String textoEstado = null;
	public boolean selected = false;

	
	public SMSConversacionHandler(String fechat, String cuerpot, String personat, String numerot, String estadot, boolean selected) {
		this.textoFecha = fechat;
		this.textoCuerpo = cuerpot;
		this.textoPerson = personat;
		this.textoNum = numerot;
		this.textoEstado = estadot;
		this.selected = selected; 
	} 

	public String getFecha() {
		return textoFecha;
	}
 

	public String getCuerpo() {
		return textoCuerpo;
	}
	
	public String getPersona() {
		return textoPerson;
	}
	
	public String getNum() {
		return textoNum;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public String getEstadoSMS() {
		return textoEstado;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
