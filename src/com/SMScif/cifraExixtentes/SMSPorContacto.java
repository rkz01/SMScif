//Muestra todos los sms de un contacto, seleccionado previamente en la clase contactos check
package com.SMScif.cifraExixtentes;

import java.util.ArrayList;

import com.SMScif.auxiliares.FechaFormato;
import com.SMScif.smscif.IngresoClave;
import com.SMScif.smscif.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SMSPorContacto extends Activity { 
	MyAdapter dataAdapter = null;
	private String nombrePersona = "", numerPersona = "", hilo= ""; 
	private boolean estado = false;
	CheckBox cbAll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//No aparesca barra de titulo
		setContentView(R.layout.activity_cifrar_porcontacto);
		
		Typeface fontMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"); //tipografia
		((TextView) findViewById(R.id.tNombreContact)).setTypeface(fontMedium);	
		
		//Genera la vista de la lista del ArrayList y permite el comportamiento del boton de (All)
		mostrarListView();
		checkBoxAllClick();
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

	private void mostrarListView() {

		ArrayList<SMSPorContactoDatos> smsList = new ArrayList<SMSPorContactoDatos>();//ArrayList q tendra los sms y otros datos
		String id = "", thread = "", fechaS = "", cuerpoS = "" , numeroS = "",
		       personaS = "", nombreP = "", numeroP = "", tipo = "";

		Bundle extras = getIntent().getExtras();
		nombrePersona = extras.getString("nombrePersona");  
		numerPersona = extras.getString("numero");
		hilo = extras.getString("hilo");
		//coloca el nombre dela persona en caso q no exista el numero
		 if (nombrePersona == " ") {
			((TextView) findViewById(R.id.tNombreContact))   
					.setText(numerPersona); 
			}
		 else if ("rough".equals(nombrePersona)) {
				((TextView) findViewById(R.id.tNombreContact))
						.setText(R.string.mensajeRoughDraft); 
				}
			
		 else if (nombrePersona != " " && !"rough".equals(nombrePersona)){
			 ((TextView) findViewById(R.id.tNombreContact))
					.setText(nombrePersona);
		 		}
		 
		// solo busca sms del contacto seleccionado
		String[] arg = new String[] {hilo};//argumento de hilo de la conversacion
		   
		Cursor cP = getContentResolver().query(	//guarda los datos de consulta en un cursor
				Data.CONTENT_URI, new String[] {Data._ID, Data.DISPLAY_NAME, Phone.NUMBER},
				Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE +"' AND " + Phone.NUMBER + " IS NOT NULL",
				null, Data.DISPLAY_NAME + " ASC");
	
		Uri mensajes = Uri.parse("content://sms");
		Cursor c = getContentResolver().query(mensajes, null, "thread_id=?", arg, null);
		int indiceNombre = cP.getColumnIndexOrThrow(Data.DISPLAY_NAME);
		int inidiceNumero = cP.getColumnIndex(Phone.NUMBER);

		String dia, mes, anio, hora;
		FechaFormato formatoFecha = new FechaFormato(); 
		
		while (c.moveToNext()) {
			id = c.getString(c.getColumnIndex("_id")); // columna 0
			numeroS = c.getString(c.getColumnIndex("address")); //Numero column 2
	     	thread = c.getString(c.getColumnIndex("thread_id")); //hilo del sms
	     	tipo = c.getString(c.getColumnIndex("type"));//tipo de sms
	     	cuerpoS = c.getString(c.getColumnIndex("body"));
	     	anio = (String) DateFormat.format("yyyy", c.getLong(c.getColumnIndex("date")));
			mes = (String) DateFormat.format("MMM", c.getLong(c.getColumnIndex("date")));
			dia = (String) DateFormat.format("dd", c.getLong(c.getColumnIndex("date"))); 
			hora = (String) DateFormat.format("h:mm a", c.getLong(c.getColumnIndex("date"))); //Fecha 4
			fechaS = formatoFecha.formatoFechaDetalle(anio, mes, dia, hora);
			

				while(cP.moveToNext()){		//recorre contactos
					nombreP = cP.getString(indiceNombre);
					numeroP = cP.getString(inidiceNumero);					
					if (numeroP.equals(numeroS)){	//si el numero de contacto es igual al numero del mensaje obtiene nombre de contacto
						personaS = nombreP;						
						}
					}
					cP.moveToFirst();	//rebobinar datos de contacto	
				if(Integer.parseInt(tipo) != 1 && Integer.parseInt(tipo)!= 3)//si el sms no es de entrada o est en borrador
				  {  
					personaS= "me";
				  }
				else if(Integer.parseInt(tipo) == 3){
					personaS="rough";//para posteriormente obtener el string de resources
					}
				if(Integer.parseInt(tipo) != 1 && Integer.parseInt(tipo) != 2 && Integer.parseInt(tipo) != 3 ){
					numeroS = "no sent";//para posteriormente obtener el string de resources
					}
			smsList.add(new SMSPorContactoDatos(id, thread, fechaS, cuerpoS, numeroS, personaS, false));
			personaS="";
		}
		c.close(); cP.close();//cerrar cursores 
		// crea un ArrayAdapter del String Array
		dataAdapter = new MyAdapter(this, R.layout.sms_cifrar_porcontacto, smsList);
		ListView listView = (ListView) findViewById(R.id.lsmscontacto);
		listView.setAdapter(dataAdapter);

	}
//clase del adaptador de la lista de mensajes
	private class MyAdapter extends ArrayAdapter<SMSPorContactoDatos> {

		private ArrayList<SMSPorContactoDatos> smsList;

		public MyAdapter(Context context, int textViewResourceId,
				ArrayList<SMSPorContactoDatos> smsList) {
			super(context, textViewResourceId, smsList);
			this.smsList = new ArrayList<SMSPorContactoDatos>();
			this.smsList.addAll(smsList);
		}

		private class ViewHolder {
			TextView textoFecha;
			TextView textoPerson;
			CheckBox textoSms;
		}

//Se muestra la vista de la lista de los sms del contacto
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.sms_cifrar_porcontacto, null);
				holder = new ViewHolder();
				holder.textoFecha = (TextView) convertView.findViewById(R.id.tfecha);
				holder.textoPerson = (TextView) convertView.findViewById(R.id.txpersona);
				holder.textoSms = (CheckBox) convertView.findViewById(R.id.checkBoxd);
				
				Typeface fontRegular= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"); //tipografia
				((TextView) convertView.findViewById(R.id.tfecha)).setTypeface(fontRegular);
				((TextView) convertView.findViewById(R.id.txpersona)).setTypeface(fontRegular);
				
				convertView.setTag(holder);
//Evento click al selecionar algun chek de la lista
				holder.textoSms.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
//obj de la lista q se le coloca el estado que el ususrio a chec o des chequeado
						SMSPorContactoDatos sms = (SMSPorContactoDatos) cb.getTag();
						sms.setSelected(cb.isChecked());
//cuando un chekc individual es chekado el de todo es puesto en false
						cbAll = (CheckBox) findViewById(R.id.All);
						cbAll.setSelected(false);
						cbAll.setChecked(false);

					}

				});
			}

			else { 
				holder = (ViewHolder) convertView.getTag();
				}
//para asignar lo que tendra el contenedor en cada uno de sus componentes
			SMSPorContactoDatos sms = smsList.get(position);
			
		if (sms.getPersona()=="rough"){//para saber si es un borrador
			holder.textoPerson.setText(R.string.mensajeRoughDraft);
		    }		
	    
		if (sms.getPersona()!="me" && sms.getPersona()!="rough"){
			holder.textoPerson.setText(nombrePersona);
	       }
		
		if	(sms.getPersona()=="me"){//para saber si el numero es el propio
			holder.textoPerson.setText(R.string.text_me);
			holder.textoSms.setBackgroundResource(R.color.greyLight);	//fondo blanco Yo
			if (sms.getNumero()=="no sent"){//para saber si no es enviado
				holder.textoPerson.setText(R.string.mensajeNoSent);
				holder.textoSms.setBackgroundResource(R.color.redLight);	//fondo amarillo no enviado
				}
		    }
		else {
			holder.textoSms.setBackgroundResource(R.color.blueLight);	//fondo azul  No Yo
			
		}
		
	    	holder.textoFecha.setText(sms.getFecha());
			holder.textoSms.setText(sms.getCuerpo());
			holder.textoSms.setChecked(sms.isSelected());
			holder.textoSms.setTag(sms);
			return convertView;

		}

	}
//metodo al presionar el botton cifrar
	public void solicitarClave(View view) {

		/*StringBuffer responseText = new StringBuffer();
		responseText.append(" SMS seleccionados:");  */
		ArrayList<SMSPorContactoDatos> smsList = dataAdapter.smsList;//se trae el modelo de la lista de sms con sus estados actuales
		int n = 0, tam = 0;
		//como la lista contiene todos los mensajes solo se obtendra el numero de los q fueron elegidos
		for (int j = 0; j < smsList.size(); j++) {
			SMSPorContactoDatos smsc = smsList.get(j);
			if (smsc.isSelected() && smsList.get(j) != null) {
				tam = tam + 1;
			}
		}
        String[] smsSms = new String[tam];
		String[] smsIds = new String[tam];
		String[] smsThreads = new String[tam];

		for (int i = 0; i < smsList.size(); i++) {
			SMSPorContactoDatos smsc = smsList.get(i);
			if (smsc.isSelected() && smsList.get(i) != null) {
				smsSms[n] = smsc.getCuerpo();
				smsIds[n] = smsc.getId();
				smsThreads[n] = smsc.getThread();
				n = n + 1;
			}
		}
		/*responseText.append("\n" + "size:" + smsIds.length);
		Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_LONG)
				.show(); */

	if (tam == 0)//si no hubo sms elegidos se notifica y finaliza la actividad
	   { 
		Toast.makeText(this, R.string.mensajeNoSelected, Toast.LENGTH_SHORT).show(); 
		finish();   
	    } 
	else {	
	Intent abrirIngresoClave = new
		  Intent(this,IngresoClave.class);
		  abrirIngresoClave.putExtra("smsIds", smsIds);
		  abrirIngresoClave.putExtra("smsThreads", smsThreads);
		  abrirIngresoClave.putExtra("smsSms", smsSms);
		  abrirIngresoClave.putExtra("numero","numero"); 
		  abrirIngresoClave.putExtra("nombrePersona", nombrePersona); 
		  abrirIngresoClave.putExtra("activar",true);
		   
		  startActivity(abrirIngresoClave); 
		  finish(); }
		 }

//Metodo cuando se selecciona check de seleccionar o no todo
	private void checkBoxAllClick() {

		cbAll = (CheckBox) findViewById(R.id.All);
		cbAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				estado = cbAll.isChecked();
//si el check esta en estado checado, entonces se pone en estado seleccionado todos los demas
//pero si es puesto como no checado por el user, pone todo como no seleccionado
				if (estado == true) {
					ArrayList<SMSPorContactoDatos> smsList = dataAdapter.smsList;
					for (int i = 0; i < smsList.size(); i++) {
						SMSPorContactoDatos smsc = smsList.get(i);
						smsc.setSelected(true);
					} dataAdapter.notifyDataSetChanged();//avisa que se hizo un ajuste por codigo y actualiza la vista 
                   } 
				
				else {
					ArrayList<SMSPorContactoDatos> smsList = dataAdapter.smsList;
					for (int i = 0; i < smsList.size(); i++) {
						SMSPorContactoDatos smsc = smsList.get(i);
						smsc.setSelected(false);
					   } dataAdapter.notifyDataSetChanged();
                   }
             }
		});

	}
	/*************************Al presionar regreso************/
	public void cerrarCifrarPorContactos(View view){
		finish();
		overridePendingTransition(R.anim.right_in,R.anim.right_out);
	}
}