package com.SMScif.preferencias;
import com.SMScif.smscif.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

public class PreferenciaActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Mostramos el contenido de la pantalla de preferencias.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PreferenciaFragment()).commit();
	}
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

}
