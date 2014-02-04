package com.junglee.commonlib.location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import com.junglee.commonlib.logging.Logger;

/**
 * LocationTracker can be used to get the geo-location of the device. It can be used to get the geo-location one time, 
 * or it can also be requested to set up listeners to keep track of the location changes.
 * <p> 
 * It can get the geo-location of the device using gps or network. If it gets the location from both the sources, then 
 * it tries to guess which one of those is more precise depending on different parameters. It can be used to get the 
 * location in terms of latitude/longitude, or more readable form like city, or city along with pincode and country. 
 * As of now, it is only used to get the location one time when the object is constructed. The listeners for the 
 * location updates are not implemented. If required those can be implemented to keep a continuous check on location 
 * changes.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class LocationTracker implements LocationListener {
	private static final String TAG = "LocationTracker";
	private Context context = null;
	private boolean listenToUpdates = false;
	 
    // flag for GPS status
    boolean isGPSEnabled = false;
 
    // flag for network status
    boolean isNetworkEnabled = false;
 
    boolean canGetLocation = false;
 
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
 
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    
    private static final boolean NEED_FINE_LOCATION = true;
 
    // Declaring a Location Manager
    protected LocationManager locationManager;
 
    public LocationTracker(Context c, boolean listenToUpdates) {
    	this.context = c;
    	this.listenToUpdates = listenToUpdates;
    	
    	retrieveLocation();
    	
    	if(!this.listenToUpdates) {
    		stopUsingGPS();
    	}
    }
    
    public Location retrieveLocation() {    	
    	
        try {
            locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
 
            if(!NEED_FINE_LOCATION) {
            	isGPSEnabled = false;
            } else {
            	// getting GPS status
            	isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
 
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            
            Logger.info(TAG, "GPS_ENABLED:"+isGPSEnabled+", NTWRK_ENABLED:"+isNetworkEnabled);
 
            
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            	this.canGetLocation = false;
            	this.location = null;
            	this.latitude = 0.0;
            	this.longitude = 0.0;
            	
            	showSettingsAlert();
            } else {
            	Location gps_location = null, net_location = null;
            	
                this.canGetLocation = true;
                // if Network Enabled get location from Network Provider
                if (isNetworkEnabled) {
                	if(listenToUpdates) {
                		locationManager.requestLocationUpdates(
                				LocationManager.NETWORK_PROVIDER,
                				MIN_TIME_BW_UPDATES,
                				MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                	}
                    if (locationManager != null) {
                        net_location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);                        
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                	if(listenToUpdates) {
                		locationManager.requestLocationUpdates(
                				LocationManager.GPS_PROVIDER,
                				MIN_TIME_BW_UPDATES,
                				MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                	}
                	if (locationManager != null) {
                		gps_location = locationManager
                				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                	}
                }
                
                location = getBetterLocation(gps_location, net_location);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
            
            
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }
    
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationTracker.this);
        }       
    }
    
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
         
        return latitude;
    }
     
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
         
        return longitude;
    }
    
    public String getReadableLocation() {
    	String locationString = null;
    	
    	if(location == null) return locationString;
    	
    	Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());   
    	try {
			List<Address> myList = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
			if(myList.size() > 0) {
				Address addr = myList.get(0);
				String locality = addr.getLocality();
				String postalCode = addr.getPostalCode();
				String country = addr.getCountryName();
				if(postalCode != null) {
					postalCode = String.format(" - %s", postalCode);
				} else {
					postalCode = "";
				}
				locationString = String.format("%s%s, %s", locality, postalCode, country);
			}
		} catch (IOException e) {
			locationString = "Network Error";
		}
    	
    	return locationString;
    }
    
    public String getCity() {
    	String cityString = null;
    	
    	if(location == null) return cityString;
    	
    	Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());   
    	try {
			List<Address> myList = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
			if(myList.size() > 0) {
				Address addr = myList.get(0);
				cityString = addr.getLocality();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return cityString;
    }
    
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS not enabled");
  
        // Setting Dialog Message
        alertDialog.setMessage("Do you want to enable it from the settings menu?");
  
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);
  
        // On pressing Settings button
        alertDialog.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }
    
    protected Location getBetterLocation(Location location1, Location location2) {
    	if (location1 != null && location2 != null) {
    		// Check whether the new location fix is newer or older
    		long timeDelta = location1.getTime() - location2.getTime();
    		boolean firstIsNewer = timeDelta > TWO_MINUTES;
    		boolean secondIsNewer = timeDelta < -TWO_MINUTES;
    		
    		if (firstIsNewer) {
    			return location1;
    		} else if (secondIsNewer) {
    			return location2;
    		}


    		int accuracyDelta = (int) (location1.getAccuracy() - location2.getAccuracy());
    		boolean firstMoreAccurate = accuracyDelta > 0;
    		
    		if(firstMoreAccurate) {
    			return location1;
    		} else {
    			return location2;
    		}
    	} else if(location1 != null) {
    		return location1;
    	} else if(location2 != null) {
    		return location2;
    	} else {
    		return null;
    	}
    }
    
    
    
    
    @Override
    public void onLocationChanged(Location location) {
    }
 
    @Override
    public void onProviderDisabled(String provider) {
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
 
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
