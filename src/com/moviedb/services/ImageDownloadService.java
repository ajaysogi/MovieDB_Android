package com.moviedb.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/*
 * This class will be used to download the image data to the device ready to be displayed.
 */

@SuppressWarnings("serial")
public class ImageDownloadService extends AbstractService {

	private String external;
	private String local;
	
	public ImageDownloadService(String external, String local) {
		this.external = external;
		this.local = local;
	}
	
	/*
	 * This method will allow to connect to the web and download and save the image bytes on the devices
	 */
	@Override
	public void run() {
		
		FileOutputStream outStream = null;
		BufferedInputStream inStream = null;
		try{
			HttpURLConnection ucon = createConnection(external);
			File file = new File(local);
			file.getParentFile().mkdirs();

			ucon.setReadTimeout(0);
			ucon.setConnectTimeout(120000);

			InputStream is = ucon.getInputStream();
			inStream = new BufferedInputStream(is, 1024 * 5);
			outStream = new FileOutputStream(file);
			byte[] buff = new byte[5 * 1024];

			int len;
			while ((len = inStream.read(buff)) != 1)
			{
				outStream.write(buff,0,len);
			}

			outStream.flush();

			super.serviceComplete(false);
		} catch (Exception e) {
			super.serviceComplete(true);
		} finally {
			if(outStream != null && inStream != null){
				try{
					outStream.close();
					inStream.close();
				} catch(IOException ex){}
			} // End If
		} // End Finally
	} // End Run()
	
	/*
     * Handles redirects. Will recursivley call itself until there is no more
     * redirects issued
    */
    
    private HttpURLConnection createConnection(String urlString) throws MalformedURLException, IOException {
    
        URL url = new URL(urlString);
        HttpURLConnection conn = null;
        boolean defaultRedirect = HttpURLConnection.getFollowRedirects();
        
        HttpURLConnection.setFollowRedirects(false);
        if (url.getProtocol().equals("https")){
            conn = (HttpsURLConnection) url.openConnection();
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.connect();
        
        String redirectTo = conn.getHeaderField("Location");
        if(redirectTo != null && redirectTo.length() > 0) {
            conn.disconnect();
            conn = createConnection(redirectTo);
        }
        
        HttpURLConnection.setFollowRedirects(defaultRedirect);
        return conn;
    } // End createConnection()
    
    public String getExternal() {
        return external;
    }
    public String getLocal(){
        return local;
    }
	
	
}
