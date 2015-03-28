package com.SMScif.auxiliares;


public class FiltrarNumeroTel {
	// ------------------- Solo pasa el numero sin otros caracteres---------------------------
	
	public String filtrarNumeroTelefono(String numberPhone ){
		String number = numberPhone;
		numberPhone = "";
	
		for (int x = 0; x < number.length(); x++) {
			if (Character.isDigit(number.charAt(x)))
				numberPhone += number.charAt(x);
			}
		return numberPhone;
	}
}	