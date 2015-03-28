package com.SMScif.smscif;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MostrarCDialogo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_mostrar_cdialogo);
		String titulo, textoMostrar,textoBoton;
		textoBoton = this.getResources().getString(R.string.boton_Aceptar);
		Bundle extras = getIntent().getExtras();
		titulo = extras.getString("titulo");
		textoMostrar = extras.getString("textoMostrar");
		
		AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
		dialogo.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));
		dialogo.setTitle(titulo);
		dialogo.setMessage(textoMostrar);
		dialogo.setNeutralButton(textoBoton,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				finish();
			}
		  } 
				);
	    dialogo.show();	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mostrar_cdialogo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
