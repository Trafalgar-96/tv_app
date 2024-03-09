package com.mobapphome.androidappupdater;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class DownloadApk extends AppCompatActivity {

    public static String UPDATE_DOMAIN = "https://auvatvupdate.net:30998/share/";
    public static String APP_NAME = "QuickTv.apk";

    private static ProgressDialog bar;
    private static final String TAG = "DownloadApk";
    private static Context context;
    private static Activity activity;


    public DownloadApk(Context context) {
        this.context = context;
        this.activity = (Activity) context;
    }


    public void startDownloadingApk() {
        String _downloadUrl = UPDATE_DOMAIN + APP_NAME;
        new DownloadNewVersion().execute();

    }


    private static class DownloadNewVersion extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (bar == null) {
                bar = new ProgressDialog(context);
                bar.setCancelable(false);
                bar.setMessage("Downloading...");
                bar.setIndeterminate(true);
                bar.setCanceledOnTouchOutside(false);
                bar.show();
            }


        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);


            bar.setIndeterminate(false);
            bar.setMax(100);
            bar.setProgress(progress[0]);
            String msg = "";
            if (progress[0] > 99) {

                msg = "Finishing... ";

            } else {

                msg = "Downloading... " + progress[0] + "%";
            }
            bar.setMessage(msg);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (bar.isShowing() && bar != null) {
                bar.dismiss();
                bar = null;
            }


            if (result) {

                Toast.makeText(context, "Update Done",
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(context, "Error: Try Again",
                        Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            Boolean flag = false;
            try {
                URL url = new URL(UPDATE_DOMAIN+APP_NAME);
//                System.out.println(url.);
//                System.out.println(UPDATE_DOMAIN+APP_NAME);
                // TODO http request
//                HttpURLConnection c = (HttpURLConnection) url.openConnection();
//                c.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());
//                Log.d(TAG, "doInBackground: "+c.getURL());


                //uncomment this if your uri is https
              HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
//                HttpsTrustManager.allowAllSSL();
                c.setInstanceFollowRedirects(HttpsURLConnection.getFollowRedirects());
                try {
                    c.setSSLSocketFactory(getSSLSocketFactory());
                    c.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                c.setRequestMethod("GET");
//                c.setDoOutput(true);
                c.connect();
                String PATH = context.getCacheDir() + "/";
                System.out.println(PATH);
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, APP_NAME);

                if (outputFile.exists()) {
                    outputFile.delete();
                }

                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();

                int total_size = c.getContentLength();//size of apk

                byte[] buffer = new byte[1024];
                int len1;
                long per;
                long downloaded = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                    downloaded += len1;
                    per = (downloaded * 100 / total_size);
                    publishProgress((int) per);
                }
                fos.close();
                is.close();
                OpenNewVersion(PATH);
                flag = true;
            } catch (MalformedURLException e) {
                Log.e(TAG, "Update Error: " + e.getMessage());
                flag = false;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return flag;

        }
    }

    private static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
//                return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
//                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
//                return hv.verify("hostname.com", session);
                return hostname.equals("auvatvupdate.net"); //TODO put your hostname here
            }
        };
    }

    private static TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }

    private static SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = context.getResources().openRawResource(R.raw.my_cert); // this cert file stored in \app\src\main\res\raw folder path
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);

        return sslContext.getSocketFactory();
    }
    private static void OpenNewVersion(String location) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(getUriFromFile(location),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
        activity.finish();

    }

    private static Uri getUriFromFile(String location) {

        if (Build.VERSION.SDK_INT < 24) {
            return Uri.fromFile(new File(location + APP_NAME));
        } else {
            return /*FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".files",
                    new File(location + APP_NAME));*/
            FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName()+ ".provider", new File(location + APP_NAME));
        }
    }
}