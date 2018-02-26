package demo.rvpdownloader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Networkcheck {
	
	private Context _context;
	 
	public Networkcheck (Context context)
	    {
	        this._context = context;
	    }
	 
	    public boolean isConnectingToInternet(){
	        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(CONNECTIVITY_SERVICE);
	          if (connectivity != null)
	          {
	              NetworkInfo[] info = connectivity.getAllNetworkInfo();
	              if (info != null)
	                  for (int i = 0; i < info.length; i++)
	                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
	                      {
	                          return true;
	                      }
	 
	          }
	          return false;
	    }

	public Boolean networkcheck(){
		Boolean is3g=false,isWifi=false;
		ConnectivityManager manager = (ConnectivityManager) _context.getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo wifi =
				manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		NetworkInfo mobile =
				manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if( wifi.isAvailable() && wifi.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
			isWifi = true;
		}
		if( mobile.isAvailable() && mobile.getDetailedState() == NetworkInfo.DetailedState.CONNECTED ){
			is3g=true;
		}

		System.out.println(is3g+" net "+isWifi);
		if (is3g || isWifi) {
			if(isConnectingToInternet()==true) {
				return true;
			}else{
				return  false;
			}
		} else {
			return false;
		}
	}
	}


