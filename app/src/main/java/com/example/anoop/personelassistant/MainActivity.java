package com.example.anoop.personelassistant;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextToSpeech tts;
    String MusicPath="",MusicName="";
    MediaPlayer mediaPlayer;

    String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
    };

    Camera cam = null;

    public void turnOnFlashLight() {
        try {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception throws in turning on flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

    public void turnOffFlashLight() {
        try {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception throws in turning off flashlight.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getMp3Songs();
       // ListAllSongs();

        tts = new TextToSpeech(this, this);
        // tts.setPitch(1);

        //doit();

        //RotateAnimation animation = new RotateAnimation(0f, 360f);
        RotateAnimation animation = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(1000);
        //Animation anim = AnimationUtils.loadAnimation(getApplicationContext());

        RotateAnimation animation1 = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation1.setRepeatCount(Animation.INFINITE);
        animation1.setDuration(1000);

        RotateAnimation animation2 = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation2.setRepeatCount(Animation.INFINITE);
        animation2.setDuration(1000);

        ImageView heartin = (ImageView) findViewById(R.id.heartin);
        heartin.startAnimation(animation);

        //ImageView heartout= (ImageView) findViewById(R.id.heartout);
        //heartout.startAnimation(animation1);

        ImageView heartinin = (ImageView) findViewById(R.id.heartinin);
        heartinin.startAnimation(animation2);

        //speech();


    }

    public String readContact(String CheckName) {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        String phoneNum = "";
        int flag = 0;

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);


                    String Name = name.toLowerCase();
                    String hh = name.toLowerCase() + " ";


                    if (Name.equals(CheckName.toLowerCase()) || (Name.toLowerCase().equals(CheckName.toLowerCase())) || Name.contains(CheckName)/*||Name.trim().equals(CheckName.trim().toLowerCase()) ||hh.indexOf(CheckName.toLowerCase()) !=-1*/
                            ) {
                        flag = 1;
                        // get the phone number
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            System.out.println("Name" + name);
                            System.out.println("phone" + phone);
                            /*if (phone == "") {
                                speakOut("Sorry sir,I can't recognize the phone number.");
                                return "";
                            }*/
                            phoneNum = phone.toString();
                            break;
                        }
                        pCur.close();
                        if (flag == 1) {
                            break;
                        }

                    }

                }
            }
        }
        return phoneNum;
    }

    public String readContacts(String CheckName) {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        String phone = null;
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer
                        .parseInt(cur.getString(cur
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);

                    String Name = name.toLowerCase().trim();
                    System.out.println("Name : " + Name);
                    System.out.println("CheckName : " + CheckName.toLowerCase().trim());

                    if (Name.equals(CheckName.toLowerCase().trim())) {

                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = ?", new String[]{id}, null);
                        //while (pCur.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        System.out.println("phone :" + phone);

                        String phones = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                        //}
                        pCur.close();
                        return phone;


                    }


                }

            }

        }
        return phone;
    }


    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // btnSpeak.setEnabled(true);

                doit();


            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    public void doit() {

        String s = "Hi Sir, I am Jarvis, Initializing all engines, please wait a moment." + "Ok sir What can i do for you";
        //speakOut(s);
        //s="Sir Initializing all engines, please wait a moment.";
        speakOut(s);

    }


    private void speakOut(String t) {

        RotateAnimation animation = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(1000);
        //Animation anim = AnimationUtils.loadAnimation(getApplicationContext());

        ImageView imageView = (ImageView) findViewById(R.id.jarvis);
        //imageView.startAnimation(animation);

        String text = t.toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        speech();
    }

    private void speakOut1(String t) {

        RotateAnimation animation = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(1000);
        //Animation anim = AnimationUtils.loadAnimation(getApplicationContext());

        ImageView imageView = (ImageView) findViewById(R.id.jarvis);
        //imageView.startAnimation(animation);

        String text = t.toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);


    }

    public void speech() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }

    }


    public void Speech(View view) {

        speech();


    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String strDate = "Current Time : " + mdformat.format(calendar.getTime());
        return (strDate);
    }
   /* public void getMp3Songs() {

        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor;

        String song_name,fullpath,album_name,artist_name;

        //cursor = managedQuery(allsongsuri, STAR, selection, null, null);
        cursor = managedQuery(allsongsuri,projection, selection, null, null);

        int count=0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    song_name = cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    int song_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media._ID));

                    fullpath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                    //fullsongpath.add(fullpath);

                    album_name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    int album_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    artist_name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    int artist_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));

                    count++;
                    if(count==10){
                        break;
                    }

                    System.out.println(song_name);


                } while (cursor.moveToNext());

            }
            cursor.close();
            //db.closeDatabase();
        }
    }*/

    String[] STAR = { "*" };
    int totalSongs;
    public boolean ListAllSongs(String song)
    {
        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        int count=0;
        boolean check=false;


        Cursor cursor = managedQuery(allsongsuri, STAR, selection, null, null);
            totalSongs = cursor.getCount();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        count++;
                        String songname = cursor
                                .getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        int song_id = cursor.getInt(cursor
                                .getColumnIndex(MediaStore.Audio.Media._ID));
                        String fullpath = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DATA));
                        //fullsongpath.add(fullpath);
                        String albumname = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        int album_id = cursor
                                .getInt(cursor
                                        .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                        String artistname = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        int artist_id = cursor
                                .getInt(cursor
                                        .getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));

                        System.out.println(songname);

                        if (songname.contains(song)||(songname.trim().equals(song.trim().toLowerCase())) ){

                            check=true;

                            MusicPath=fullpath;
                            MusicName=songname;
                            break;
                        }



                        if(count==10){
                            break;
                        }

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

        System.out.println("MusicName  :  "+MusicName);
        System.out.println("MusicPath  :  "+MusicPath);

        return check;

    }

    public void onActivityResult(int request_code, int result_code, Intent i) {

        super.onActivityResult(request_code, result_code, i);
        switch (request_code) {
            case 100:
                if (result_code == RESULT_OK && i != null) {
                    ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //resultText.setText(result.get(0).toString());

                    String sentence = result.get(0).toString();
                    String unni = "Jarvis";

                    String hello = "Hello";
                    String hi = "Hi";
                    String what = "what";

                    String who = "who";
                    String search = "search";
                    String where = "where";
                /*String what="what";
                String what="what";
                String what="what";
                String what="what";
                String what="what";*/

                    String qstn = "";
                    String call = "call";
                    String to = "to";
                    String play = "play";


                    // System.out.println(sentence);
                    String search1 = "start";
                    String search01 = "open";
                    String search2 = "camera";
                    String search3 = "gallery";
                    String search4 = "browser";
                    String search5 = "whatsapp";
                    String search6 = "hike";
                    String search7 = "settings";
                    String search8 = "music";
                    String search9 = "facebook";
                    String search10 = "dialler";
                    String search11 = "gmail";
                    String search12 = "on";
                    String search13 = "flash";
                    String search14 = "off";
                    String search15 = "close";
                    String search16 = "stop";
                    String search17 = "shutdown";
                    String search18 = "shut";
                    String search19 = "down";


                    System.out.println(sentence);


                    if ((sentence.toLowerCase().indexOf(search1.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search01.toLowerCase()) != -1) && (sentence.toLowerCase().indexOf(search2.toLowerCase()) != -1)) {

                        //resultText.setText("Opening "+search2);
                        Toast.makeText(getApplicationContext(), "Opening " + search2, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        speakOut("Opening " + search2);
                        startActivity(intent);


                    }

                    else if ((sentence.toLowerCase().indexOf(call.toLowerCase()) != -1) && (sentence.toLowerCase().indexOf(to.toLowerCase()) != -1)) {

                        String Name = sentence.toLowerCase();
                        int l = Name.length();
                        int index = sentence.toLowerCase().indexOf(to.toLowerCase());
                        String newStr = sentence.substring(index + 3, l);
                        System.out.println(newStr);

                        String phnNo = readContact(newStr);

                        if (phnNo != "") {

                            System.out.println("Phone num=  " + phnNo);
                            Toast.makeText(getApplicationContext(), "Calling " + phnNo, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Intent.ACTION_CALL);

                            intent.setData(Uri.parse("tel:" + phnNo.toString()));
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            startActivity(intent);

                    }
                }

                else if ((sentence.toLowerCase().indexOf(play.toLowerCase()) != -1)) {

                    String Names = sentence.toLowerCase();
                    int ll = Names.length();
                    int indexes = sentence.toLowerCase().indexOf(play.toLowerCase());
                    String newStrs = sentence.substring(indexes + 5, ll);
                    System.out.println("Music :"+newStrs);
                    boolean check;
                    check= ListAllSongs(newStrs);
                    if(check==true){

                        /*Uri uri = Uri.parse(MusicPath);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setDataAndType(uri, "audio/*");
                        startActivity(intent);*/

                        mediaPlayer = MediaPlayer.create(this, Uri.parse(MusicPath));
                        mediaPlayer.start();
                    }

                }
                    else if ((sentence.toLowerCase().indexOf(search16.toLowerCase()) != -1) && (sentence.toLowerCase().indexOf(search8.toLowerCase()) != -1)) {



                        /*Uri uri = Uri.parse(MusicPath);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setDataAndType(uri, "audio/*");
                        startActivity(intent);*/

                            mediaPlayer.stop();


                    }


                else if ((sentence.toLowerCase().indexOf(unni.toLowerCase()) != -1)){

                    // resultText.setText("Opening "+search3);
                    String s="Yes Sir, I am Listening you. You can ask any questions.";
                    //speakOut(s);
                    //s="Sir Initializing all engines, please wait a moment.";
                    speakOut(s);


                }
                else if ( (sentence.toLowerCase().indexOf(hello.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(hi.toLowerCase()) != -1 ) && ( sentence.toLowerCase().indexOf(unni.toLowerCase()) != -1)){

                    // resultText.setText("Opening "+search3);
                    String s="Hi Sir, I am Jarvis. Your personnel Assistant. You can ask any questions.";
                    //speakOut(s);
                    //s="Sir Initializing all engines, please wait a moment.";
                    speakOut(s);


                }
                else if ( (sentence.toLowerCase().indexOf(what.toLowerCase()) != -1 ) && ( sentence.toLowerCase().indexOf("is") != -1) && ( sentence.toLowerCase().indexOf("time") != -1)){

                    //resultText.setText("Opening "+search4);
                    String tm=getCurrentTime();
                    speakOut(tm);


                }
                else if ( (sentence.toLowerCase().indexOf(what.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(where.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(who.toLowerCase()) != -1 ) && ( sentence.toLowerCase().indexOf("is") != -1) && ( (qstn= sentence.toLowerCase()) != " ")){

                    //resultText.setText("Opening "+search4);
                    System.out.println(qstn);
                    Toast.makeText(getApplicationContext(),"Opening "+search4,Toast.LENGTH_SHORT).show();
                    speakOut("Opening "+search4+" For your question");
                    Uri uriUrl = Uri.parse("http://www.google.com/search?q="+qstn);
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);


                }










                else if ( (sentence.toLowerCase().indexOf(search1.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search01.toLowerCase()) != -1 )&& ( sentence.toLowerCase().indexOf(search3.toLowerCase()) != -1) ){

                    // resultText.setText("Opening "+search3);
                    Toast.makeText(getApplicationContext(),"Opening "+search3,Toast.LENGTH_SHORT).show();
                    speakOut("Opening "+search3);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
                    startActivity(intent);

                }else if ( (sentence.toLowerCase().indexOf(search1.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search01.toLowerCase()) != -1 )&& ( sentence.toLowerCase().indexOf(search4.toLowerCase()) != -1) ){

                    //resultText.setText("Opening "+search4);
                    Toast.makeText(getApplicationContext(),"Opening "+search4,Toast.LENGTH_SHORT).show();
                    speakOut("Opening "+search4);
                    Uri uriUrl = Uri.parse("https://www.google.co.in/");
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);


                }else if ( (sentence.toLowerCase().indexOf(search1.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search01.toLowerCase()) != -1 )&& ( sentence.toLowerCase().indexOf(search5.toLowerCase()) != -1) ){

                    //resultText.setText("Opening "+search5);
                    Toast.makeText(getApplicationContext(),"Opening "+search5,Toast.LENGTH_SHORT).show();

                    try {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                        speakOut("Opening "+search5);
                        startActivity(launchIntent);
                    }catch (Exception e ){
                        //resultText.setText("Please install "+search5);
                        speakOut("Please install "+search5);
                        Toast.makeText(getApplicationContext(),"Please install "+search5,Toast.LENGTH_SHORT).show();

                    }


                }

                else if ( (sentence.toLowerCase().indexOf(search1.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search01.toLowerCase()) != -1 )&& ( sentence.toLowerCase().indexOf(search6.toLowerCase()) != -1) ){

                    //resultText.setText("Opening "+search6);
                    Toast.makeText(getApplicationContext(),"Opening "+search6,Toast.LENGTH_SHORT).show();
                    try {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.hike");
                        speakOut("Opening "+search6);
                        startActivity(launchIntent);
                    }catch (Exception e ){
                        speakOut("Please install "+search6);
                        Toast.makeText(getApplicationContext(),"Please install hike",Toast.LENGTH_SHORT).show();
                        //resultText.setText("Please install hike");

                    }


                }
                else if ( (sentence.toLowerCase().indexOf(search1.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search01.toLowerCase()) != -1 )&& ( sentence.toLowerCase().indexOf(search7.toLowerCase()) != -1) ){

                    // resultText.setText("Opening "+search7);
                    Toast.makeText(getApplicationContext(),"Opening "+search7,Toast.LENGTH_SHORT).show();
                    speakOut("Opening "+search7);


                    Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
                else if ( (sentence.toLowerCase().indexOf(search1.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search01.toLowerCase()) != -1 )&& ( sentence.toLowerCase().indexOf(search8.toLowerCase()) != -1) ){

                    // resultText.setText("Opening "+search8);
                    Toast.makeText(getApplicationContext(),"Opening "+search8,Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                    try {
                        //Intent intent = new Intent("android.intent.category.APP_MUSIC");
                        Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                        speakOut("Opening "+search8);
                        startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Please Try Again",Toast.LENGTH_SHORT).show();
                        //speakOut("Opening "+search8);
                        //resultText.setText("Please Try Again");
                    }

                }
                else if ( (sentence.toLowerCase().indexOf(search1.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search01.toLowerCase()) != -1 )&& ( sentence.toLowerCase().indexOf(search9.toLowerCase()) != -1) ){

                    //resultText.setText("Opening "+search9);
                    Toast.makeText(getApplicationContext(),"Opening "+search9,Toast.LENGTH_SHORT).show();
                    try {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
                        speakOut("Opening "+search9);
                        startActivity(launchIntent);
                    }catch (Exception e ){
                        Toast.makeText(getApplicationContext(),"Please install "+search9,Toast.LENGTH_SHORT).show();
                        speakOut("Please install "+search9);
                        //resultText.setText("Please install "+search9);

                    }
                }
                else if ( (sentence.toLowerCase().indexOf(search1.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search01.toLowerCase()) != -1 )&& ( sentence.toLowerCase().indexOf(search10.toLowerCase()) != -1) ){

                    //resultText.setText("Opening "+search10);
                    Toast.makeText(getApplicationContext(),"Opening "+search10,Toast.LENGTH_SHORT).show();
                    try {
                        Intent intent = new Intent("android.intent.action.DIAL");
                        speakOut("Opening "+search10);
                        startActivity(intent);
                    }catch (Exception e ){
                        Toast.makeText(getApplicationContext(),"Please install "+search10,Toast.LENGTH_SHORT).show();
                        speakOut("Please install "+search10);
                        //resultText.setText("Please install "+search10);

                    }
                }
                else if ( (sentence.toLowerCase().indexOf(search1.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search01.toLowerCase()) != -1 )&& ( sentence.toLowerCase().indexOf(search11.toLowerCase()) != -1) ){

                    //resultText.setText("Opening "+search11);
                    Toast.makeText(getApplicationContext(),"Opening "+search11,Toast.LENGTH_SHORT).show();
                    try {
                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                        speakOut("Opening "+search11);
                        startActivity(intent);
                    }catch (Exception e ){
                        Toast.makeText(getApplicationContext(),"Please install "+search11,Toast.LENGTH_SHORT).show();
                        speakOut("Please install "+search11);
                        //resultText.setText("Please install "+search11);

                    }
                }

                else if ( (sentence.toLowerCase().indexOf(search1.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search01.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search12.toLowerCase()) != -1 )&& ( sentence.toLowerCase().indexOf(search13.toLowerCase()) != -1) ){

                    // resultText.setText("Opening "+search13);

                    turnOnFlashLight();
                    speakOut("Switching on "+search13 +"light");

                }
                else if ( (sentence.toLowerCase().indexOf(search14.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search15.toLowerCase()) != -1 || sentence.toLowerCase().indexOf(search16.toLowerCase()) != -1 )&& ( sentence.toLowerCase().indexOf(search13.toLowerCase()) != -1) ){

                    // resultText.setText("Opening "+search7);

                    turnOffFlashLight();
                    speakOut("Switching off "+search13 +"light");

                }
                else if ( (sentence.toLowerCase().indexOf(search17.toLowerCase()) != -1 )||((sentence.toLowerCase().indexOf(search18.toLowerCase()) != -1 )&&(sentence.toLowerCase().indexOf(search19.toLowerCase()) != -1 )) ){

                    // resultText.setText("Opening "+search7);

                    speakOut1("Bye sir");

                    finish();
                    //System.exit(0);

                }

                else if((sentence.toLowerCase().indexOf(" ") != -1 )){

                    speech();

                }



                else {

                    //resultText.setText("Please try again");
                    Toast.makeText(getApplicationContext(),"Please try again",Toast.LENGTH_SHORT).show();
                    speakOut("Sorry Sir I can't start this operation");

                }
                //resultText.setText("Hello");

            }
                break;
        }
    }

    /*

    public void onBackPressed(){

    }*/



}
