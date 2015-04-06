//Genera los datos para mostrarse en la vista de seleccion de multiples contactos para cifrar sus sms
package com.SMScif.cifraExixtentes;

import java.util.ArrayList;

import com.SMScif.auxiliares.FiltrarNumeroTel;
import com.SMScif.smscif.IngresoClave;
import com.SMScif.smscif.R;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CifrarContactos extends Activity {
	private String personaS, numeroS;
	MyAdapter dataAdapter = null;
	CheckBox cbAll;
	private boolean estado = false;
	int tam = 0;
	String [] smsSms, smsIds, smsThreads;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//No aparesca barra de titulo
		setContentView(R.layout.activity_contactos_cifrar);
		//Genera la vista de la lista del ArrayList y permite el comportamiento del boton de (All)
		
		Typeface fontMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"); //tipografia
		((TextView) findViewById(R.id.tituloContactosCifrar)).setTypeface(fontMedium);	
		
		mostrarListView();
		cBoxAllClick();   
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

	//metodo que proporciona los datos a la ArrayLIst
	private void mostrarListView() {
		ArrayList<CifrarContactosDatos> smsList = new ArrayList<CifrarContactosDatos>(); //arrayList de los contactos
		
		String[] projection2 = new String[] {"address", "type", "thread_id",};	//columnas que regresa por cada renglon
		String selection2 =  "address" + " IS NOT NULL"  + ") GROUP BY (" + "thread_id";  //clausula de agrupar por hilo, criterio de seleccion
	
		
		Uri mensajes = Uri.parse("content://sms");	//leer mensajes entrada content 
		Cursor m = getContentResolver().query(mensajes, projection2, selection2, null, null);
			
		String nombreP, numeroP, tipo, threadS;
		ContentResolver contentresolver = getContentResolver(); //para acceder a la infromacion que se proporsiona
		Cursor cP = contentresolver.query(	//guarda los datos de consulta en un cursor
				Data.CONTENT_URI, new String[] {Data._ID, Phone.DISPLAY_NAME, Phone.NUMBER},
				Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE +"' AND " + Phone.NUMBER + " IS NOT NULL",
				null, null);
	
		int indiceNombre = cP.getColumnIndexOrThrow(Phone.DISPLAY_NAME);
		int inidiceNumero = cP.getColumnIndex(Phone.NUMBER);
		
		while (m.moveToNext()){
			
			    tipo = m.getString(m.getColumnIndex("type"));//tipo de sms
				numeroS = m.getString(m.getColumnIndex("address")); //Remitente Numero 2
				threadS = m.getString(m.getColumnIndex("thread_id")); //hilo del sms
		     	
				while(cP.moveToNext()){		//recorre contactos
					nombreP = cP.getString(indiceNombre);
					numeroP = cP.getString(inidiceNumero);	
					
					FiltrarNumeroTel filter = new FiltrarNumeroTel();
					numeroP = filter.filtrarNumeroTelefono(numeroP);
					
				if (numeroP.equals(numeroS)){	//si el numero de contacto es igual al numero del mensaje obtiene nombre de contacto
						personaS = nombreP;	
					}						
				}
				if(Integer.parseInt(tipo) == 3 && numeroS.equals("")){
					personaS = "rough";//para posteriormente obtener el string de resources
					}
			 	cP.moveToFirst();	//rebobinar datos de contacto
				smsList.add(new CifrarContactosDatos(numeroS, personaS, threadS, false));
			    personaS=""; numeroS=""; threadS="";
		}

	   
		//los datos del Myadaptador de datos  son colocados en la lista de la vista
	    dataAdapter = new MyAdapter(this, R.layout.contacto_cifar_datos, smsList);
		ListView listView = (ListView) findViewById(R.id.listContactCheck);
		listView.setAdapter(dataAdapter);	
		m.close(); cP.close();
		}
	
private class MyAdapter extends ArrayAdapter<CifrarContactosDatos> {

		private ArrayList<CifrarContactosDatos> smsList;

		public MyAdapter(Context context, int textViewResourceId,
				ArrayList<CifrarContactosDatos> smsList) {
			super(context, textViewResourceId, smsList);
			this.smsList = new ArrayList<CifrarContactosDatos>();
			this.smsList.addAll(smsList);
		}

		private class ViewHolder {
			CheckedTextView textoNumero; //para poder realizar una seleccion de sms de un solo contacto
			CheckBox textoNombre; //para seleccionar sms por multiples contactos
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.contacto_cifar_datos, null);
				holder = new ViewHolder();
				//se traen los views donde se mostraran los datos
				holder.textoNumero = (CheckedTextView ) convertView
						.findViewById(R.id.textNumContacto);
				holder.textoNombre = (CheckBox) convertView
						.findViewById(R.id.checkBoxC); 
											
				convertView.setTag(holder);
				//cuando se pusha en el num de contacto se llama al metodo de cifrado de un contacto especifico
				holder.textoNumero.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckedTextView ct = (CheckedTextView) v;   
				    	ct.setChecked(true);
						CifrarContactosDatos sms = (CifrarContactosDatos) ct.getTag();
						sms.setSelected(true);
						//este metodo cambiara la vista al contacto elegido 
						if(sms.getNom()== null || sms.getNom()== ""){
							smsContacto(sms.getNum(), sms.getNum(), sms.getHilo());}
						else{smsContacto(sms.getNum(), sms.getNom(), sms.getHilo());}	
						cbAll = (CheckBox) findViewById(R.id.All_contacts);
						cbAll.setSelected(false);
						cbAll.setChecked(false); 
					}

				});
				//sis se pusha en el check de nombre solo se coloca el estado del check segun la capa 8
				holder.textoNombre.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						CifrarContactosDatos sms2 = (CifrarContactosDatos) cb.getTag();
						sms2.setSelected(cb.isChecked());
					    sms2.setSelected(cb.isChecked());
						cbAll = (CheckBox) findViewById(R.id.All_contacts);
						cbAll.setSelected(false);
						cbAll.setChecked(false); 
					   }
				 });
			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}
//se coloan en los views ahora si los datos dela smsList
			CifrarContactosDatos sms = smsList.get(position);
			holder.textoNombre.setText(sms.getNom());
			holder.textoNumero.setText(" ");//holder.textoNumero.setText(sms.getNum());
			if(sms.getNom()== null || sms.getNom()== ""){
				holder.textoNombre.setText(sms.getNum());
				holder.textoNumero.setText("");
			}
		
			if(sms.getNom()=="rough"){//para saber si es un borrador
				holder.textoNombre.setText(R.string.mensajeRoughDraft);
			    }
			
	/*		if(sms.getNom()=="rough" && sms.getNum()!= null){//para saber si es un borrador y tiene numero
				holder.textoNombre.setText(sms.getNom());
			    }*/
			holder.textoNumero.setChecked(sms.isSelected());
			holder.textoNumero.setTag(sms);
            holder.textoNombre.setChecked(sms.isSelected());
			holder.textoNombre.setTag(sms);
			return convertView;
		}
	}

//se abre la clase para cifrar los sms de 1 contacto elegido
public void smsContacto(String number, String  name, String threadd){
    String nombreP = "", numeroP = "", hiloP= "";
	ArrayList<CifrarContactosDatos> smsList = dataAdapter.smsList;
//como ya se eligio cifrar solo por un contacto pone en deseleccionado a todos los demas
	for (int j = 0; j < smsList.size(); j++) {
		CifrarContactosDatos smsc = smsList.get(j);
		if (smsc.isSelected() && smsList.get(j) != null) {
		smsc.setSelected(false);  //Pone en deseleccionada todos los check
		}
	} dataAdapter.notifyDataSetChanged();//notifica el cambio para actualizar la vista, si no se quedaria con la vista q el SO reutiliza del estado anterior
	
	nombreP =name;
	numeroP =number;
	hiloP =threadd;

	  Intent abrirSMSPorContacto = new Intent(this,SMSPorContacto.class);
	  abrirSMSPorContacto.putExtra("numero",numeroP); 
	  abrirSMSPorContacto.putExtra("hilo",hiloP);
	  abrirSMSPorContacto.putExtra("nombrePersona", nombreP); 
	 //no se finishea la actividad para q el usuario pueda regresar a elegir otro contacto   
	  startActivity(abrirSMSPorContacto); 
	  overridePendingTransition(R.anim.left_in,R.anim.left_out); 
	 }

//en el caso de solicitar el proceso de chek seleccionados, mediante el boton encrypt
public void requestKey(View view) {

	ArrayList<CifrarContactosDatos> smsList = dataAdapter.smsList;
	int n = 0, tam = 0;
	String id = "", thread = "", cuerpoS = "";

	for (int j = 0; j < smsList.size(); j++) {
		CifrarContactosDatos smsc = smsList.get(j);
		if (smsc.isSelected() && smsList.get(j) != null) {
			tam = tam + 1;
		}
	}
	//no se detecto ningun contacto seleccionado ya que el arraylist de contactos esta vacio
	if (tam == 0){
		Toast.makeText(this, R.string.mensajeNoSelected, Toast.LENGTH_SHORT).show();
		finish();   
	} 
	//si si hay contactos para cifrar sus sms se crean los arreglos al tamanio especifico ya q el array list se crea con todos sin saber si seran o no selecteds
	else{
	String[] names = new String[tam];
	String[] threads = new String[tam];

	for (int i = 0; i < smsList.size(); i++) {
		CifrarContactosDatos smsc = smsList.get(i);
		if (smsc.isSelected() && smsList.get(i) != null) {
			names[n] = smsc.getNom();
			threads[n] = smsc.getHilo();
			n = n + 1;
		} 
	}

//////////////////////////////////////////////////////////////////////////////////	
	//ahora se creara un arrayLIst de sms de los contactos
	ArrayList<SMSPorContactoDatos> smsLista = new ArrayList<SMSPorContactoDatos>();
	//este es elproceso donde se llenan todos los datos sms de los contactos elegidos
	//que estan en threads
	for(int s=0; s < threads.length; s++ )
	{//argumento de where para cada vuelta sera el hilo de conversacion
	String[] arg = new String[] { threads[s] };
	
	Uri mensajes = Uri.parse("content://sms/");
	//se realiza la consulta donde el where es el hilo y argumento antes definido
	Cursor c = getContentResolver().query(mensajes, null, "thread_id=?", arg, null);
	
	while (c.moveToNext()) {
		id = c.getString(c.getColumnIndex("_id")); // column 0
		thread = c.getString(c.getColumnIndex("thread_id"));  //column 1 hilo de sms
		cuerpoS = c.getString(c.getColumnIndex("body"));
		smsLista.add(new SMSPorContactoDatos(id, thread, "", cuerpoS,"","", false));
	}
	}

	 smsSms = new String[smsLista.size()];
	 smsIds = new String[smsLista.size()];
	 smsThreads = new String[smsLista.size()];

	for (int i = 0; i < smsLista.size(); i++) {
		    smsSms[i] = smsLista.get(i).getCuerpo();
			smsIds[i] = smsLista.get(i).getId();
			smsThreads[i] = smsLista.get(i).getThread();
		}  

	  Intent abrirIngresoClave = new Intent(this,IngresoClave.class);
	  abrirIngresoClave.putExtra("smsIds", smsIds);
	  abrirIngresoClave.putExtra("smsThreads", smsThreads);
	  abrirIngresoClave.putExtra("smsSms", smsSms); 
	  abrirIngresoClave.putExtra("activar",true);
	  startActivity(abrirIngresoClave); 
	  finish();   
	 }
}


//para q el check de Todo(All) pueda colocar en seleccionado o no a todos los restantes
private void cBoxAllClick() {

	cbAll = (CheckBox) findViewById(R.id.All_contacts);
	cbAll.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			estado = cbAll.isChecked();
			if (estado == true) {
				ArrayList<CifrarContactosDatos> smsList = dataAdapter.smsList;
				for (int i = 0; i < smsList.size(); i++) {
					CifrarContactosDatos smsc = smsList.get(i);
					smsc.setSelected(true);
				} dataAdapter.notifyDataSetChanged();
               } 
			
			else {
				ArrayList<CifrarContactosDatos> smsList = dataAdapter.smsList;
				for (int i = 0; i < smsList.size(); i++) {
					CifrarContactosDatos smsc = smsList.get(i);
					smsc.setSelected(false);
				   } dataAdapter.notifyDataSetChanged();
               }
         }
	});

}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contactos_check, menu);
		return true;
	}*/
	
	/****************************************Al presionar flecha regreso************/
	public void cerrarCifrarContactos(View view){
		finish();
		overridePendingTransition(R.anim.right_in,R.anim.right_out);
	}
	

}
