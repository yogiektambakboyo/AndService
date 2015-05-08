package com.yogi.AndService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.location.*;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

public class YogiActivity extends Activity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    private LocationManager locationManager=null;
    private LocationListener locationListener=null;

    private Button btnGetLocation = null;
    private ProgressBar pb =null;

    String cityName=" ";
    List<Address> addresses;

    String longitude = "";
    String latitude = "";

    private static final String username = "cslocationservice01@gmail.com";
    private static final String password = "cslocation1988";

    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss");
    String strDate = sdf.format(c.getTime());

    String email = "yogiektambakboyo@gmail.com";
    String subject = "Location";
    String message = "My Location";

    TelephonyManager telephonyManager;
    String myIMEI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);


        btnGetLocation = (Button) findViewById(R.id.btnLocation);
        btnGetLocation.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        myIMEI = telephonyManager.getDeviceId()+" - " + telephonyManager.getLine1Number() + " - " + telephonyManager.getSimSerialNumber();
        Toast.makeText(this.getBaseContext(),myIMEI,Toast.LENGTH_SHORT).show();
    }

    public void generateNoteOnSD(String sFileName, String sBody){
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile,true);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
            pb.setVisibility(View.VISIBLE);
            locationListener = new MyLocationListener();

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0,locationListener);

        generateNoteOnSD("Notes","\n"+strDate+";"+cityName+";"+longitude+";"+latitude);

        email = "yogiektambakboyo@gmail.com";
        subject = "Location";
        message = "My Location - "+myIMEI;
        sendMail(email, subject, message);


    }


    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            pb.setVisibility(View.INVISIBLE);
            longitude = "Longitude: " +loc.getLongitude();
            latitude = "Latitude: " +loc.getLatitude();

            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (addresses.size() > 0)
                cityName=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }

    public void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("cslocationservice01@gmail.com", "cslocationservice01"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);

        Multipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(messageBody, "text/html");
        mp.addBodyPart(htmlPart);

        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
        if (!root.exists()) {
            root.mkdirs();
        }
        File gpxfile = new File(root, "Notes");

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        FileDataSource fileDataSource =new FileDataSource(gpxfile);
        messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
        messageBodyPart.setFileName(gpxfile.getName());
        mp.addBodyPart(messageBodyPart);

        message.setContent(mp);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(YogiActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
