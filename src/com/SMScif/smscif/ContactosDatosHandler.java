/*manejador (handler), cada objeto de esta clase almacenara informacion
 *  correspondiente a un contacto
 * 
 * */
package com.SMScif.smscif;

public class ContactosDatosHandler {
	public String contactoNumero; 
	public String contactoPersona;
	public String contactoBody;
	public String contactoDate;
	public String contactoThread;
	public boolean selected = false;
	
	public ContactosDatosHandler(String numerot, String personat, String bodyt, String datet, String threadt, boolean selected) { 
	    this.contactoNumero = numerot; 
	    this.contactoPersona = personat;
	    this.contactoBody = bodyt; //utilizado en clase de contactos conversacion para obtener el ultimi sms de cada contacto
	    this.contactoDate = datet;
	    this.contactoThread = threadt;
	    this.selected = selected; 
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getFecha() {
		return contactoDate;
	}
 
	public String getCuerpo() {
		return contactoBody;
	}
	
	public String getPersona() {
		return contactoPersona;
	}
	
	public String getNum() {
		return contactoNumero;
	}
	
	public String getHilo() {
		return contactoThread;
	}	
	
}
