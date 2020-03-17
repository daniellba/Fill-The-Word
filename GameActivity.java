package com.danielbenami_tomermaalumi.filltheword;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GameActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String MY_DB_NAME = "pictures.db";
    private SQLiteDatabase picturesDB = null;
    private ImageView imageView;
    private TextView txvTimer, txvResult;
    private Button btnHint, btnDelete, btnEnter, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8;
    private TextToSpeech tts;  // Text To Speech engine
    private MediaPlayer mediaPlayer;
    private Stage stage;
    private String path, tempR, result;
    private char[] letters = new char[8];
    private boolean musicIsOn, timeIsOn, stageVictory = false;
    private int radioId, timercount = 0, k = 1;
    Thread t = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Hide the Activity Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide the Activity Action Bar
        getSupportActionBar().hide();

        init();
        //Init Text-To-Speech (TTS) Engine
        initTTS();
        // create DataBase
        createDB();

        startStage(k);

        // event listeners
        btnHint.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnEnter.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
    }

    public void startStage(int k)
    {
        stage = getStageDB(k);
        this.timeIsOn = false;

        // show the image
        Bitmap bmp = BitmapFactory.decodeByteArray(stage.getImage(), 0, stage.getImage().length);
        imageView.setImageBitmap(bmp);

        // show the random letters
        randomLetter(stage.getName());

        // show the text Result
        result = "";
        tempR = "__";

        for (int i = 0; i < stage.getLength() - 1; i++)
        {
            tempR += " __";
        }
        txvResult.setText(tempR);

        timerCountdown();
    }

    public void onClick(View v)
    {
        Log.d("debug", "onClick");

        switch (v.getId())
        {
            case R.id.btnHint:
                speech();
                break;

            case R.id.btnDelete:
                result = "";
                txvResult.setText(tempR);
                break;

            case R.id.btnEnter:
                isVictory(result);
                break;

            case R.id.btn1:
                result += letters[0];
                txvResult.setText(result);
                break;

            case R.id.btn2:
                result += letters[1];
                txvResult.setText(result);
                break;

            case R.id.btn3:
                result += letters[2];
                txvResult.setText(result);
                break;

            case R.id.btn4:
                result += letters[3];
                txvResult.setText(result);
                break;

            case R.id.btn5:
                result += letters[4];
                txvResult.setText(result);
                break;

            case R.id.btn6:
                result += letters[5];
                txvResult.setText(result);
                break;

            case R.id.btn7:
                result += letters[6];
                txvResult.setText(result);
                break;

            case R.id.btn8:
                result += letters[7];
                txvResult.setText(result);
                break;
        }
    }

    private void init()
    {
        loadData();
        imageView = findViewById(R.id.imgView);
        txvTimer = findViewById(R.id.txvTimer);
        txvResult = findViewById(R.id.txvResult);
        btnHint = findViewById(R.id.btnHint);
        btnDelete = findViewById(R.id.btnDelete);
        btnEnter = findViewById(R.id.btnEnter);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
    }

    // Init Text-To-Speech (TTS) Engine
    private void initTTS()
    {
        // create TextToSpeech object.
        // implement the onInit callback to make sure TTS is ready to use.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                if (status == TextToSpeech.SUCCESS)
                {
                    int result = tts.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Log.d("TTS", "TextToSpeech Error - Language not supported!");
                    }
                    else
                    {
                        Log.d("TTS", "TextToSpeech Ready to use.");
                        btnHint.setEnabled(true);
                    }
                }
                else
                {
                    Log.d("TTS", "TextToSpeech Error - initialization failed!");
                }
            }
        });
    }

    public void createDB()
    {
        try
        {
            // Opens a current database or creates it
            // Pass the database name, designate that only this app can use it
            // and a DatabaseErrorHandler in the case of database corruption
            picturesDB = openOrCreateDatabase(MY_DB_NAME, MODE_PRIVATE, null);
            picturesDB.execSQL("DROP TABLE IF EXISTS pictures");
            // build an SQL statement to create 'pictures' table (if not exists)
            String sql = "CREATE TABLE IF NOT EXISTS pictures (id integer primary key, img blob not null, name VARCHAR);";
            picturesDB.execSQL(sql);
        }
        catch (Exception e)
        {
            Log.d("debug", "Error Creating Database");
        }

        String sqlQuestion = "SELECT * FROM pictures";
        Cursor cursor = picturesDB.rawQuery(sqlQuestion, null);
        Log.e("DB", "database count is: " + cursor.getCount());
        if (cursor.getCount() == 0)
        {
            ByteArrayOutputStream baos;
            ContentValues initialValues;
            Bitmap bitmap;
            byte[] photo;

            // car
            baos = new ByteArrayOutputStream();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_car);
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
            photo = baos.toByteArray();

            initialValues = new ContentValues();
            initialValues.put("id", 1);
            initialValues.put("img", photo);
            initialValues.put("name", "car");
            picturesDB.insert("pictures", null, initialValues);

            // dog
            baos = new ByteArrayOutputStream();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_dog);
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
            photo = baos.toByteArray();

            initialValues = new ContentValues();
            initialValues.put("id", 2);
            initialValues.put("img", photo);
            initialValues.put("name", "dog");
            picturesDB.insert("pictures", null, initialValues);

            // ball
            baos = new ByteArrayOutputStream();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_ball);
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
            photo = baos.toByteArray();

            initialValues = new ContentValues();
            initialValues.put("id", 3);
            initialValues.put("img", photo);
            initialValues.put("name", "ball");
            picturesDB.insert("pictures", null, initialValues);

            // fish
            baos = new ByteArrayOutputStream();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_fish);
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
            photo = baos.toByteArray();

            initialValues = new ContentValues();
            initialValues.put("id", 4);
            initialValues.put("img", photo);
            initialValues.put("name", "fish");
            picturesDB.insert("pictures", null, initialValues);

            // penguin
            baos = new ByteArrayOutputStream();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_penguin);
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
            photo = baos.toByteArray();

            initialValues = new ContentValues();
            initialValues.put("id", 5);
            initialValues.put("img", photo);
            initialValues.put("name", "penguin");
            picturesDB.insert("pictures", null, initialValues);
        }
    }

    public Stage getStageDB(int id)
    {
        String sqlQuestion = "SELECT * FROM pictures";
        Cursor cursor = picturesDB.rawQuery(sqlQuestion, null);

        int idColumn = cursor.getColumnIndex("id");
        int nameColumn = cursor.getColumnIndex("name");
        int imgColumn = cursor.getColumnIndex("img");

        if (cursor.moveToFirst())
        {
            do {
                int tempId = cursor.getInt(idColumn);
                if (id == tempId)
                {
                    String name = cursor.getString(nameColumn);
                    byte[] img = cursor.getBlob(imgColumn);

                    Stage tempStage = new Stage(id, name, img);
                    return tempStage;
                }
            } while (cursor.moveToNext());
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void speech()
    {
        String textToSpeech = stage.getName();
        if (textToSpeech == null || textToSpeech.isEmpty())
            textToSpeech = "Please enter text to speech";

        if (tts != null && !tts.isSpeaking())
        {
            // Speech pitch. 1.0 is the normal pitch, lower values lower the tone of
            // the synthesized voice, greater values increase it.
            tts.setPitch(1.0f);

            // Speech rate. 1.0 is the normal speech rate, lower values slow down
            // the speech (0.5 is half the normal speech rate), greater values
            // accelerate it (2.0 is twice the normal speech rate).
            tts.setSpeechRate(1.0f);

            // speech the string text
            tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void randomLetter(String name)
    {
        for (int i = 0; i < 8; i++)
        {
            if (i < name.length())
            {
                letters[i] = name.charAt(i);
            }
            else
            {
                Random rnd = new Random();
                char randomChar = (char) (rnd.nextInt(26) + 'a');
                letters[i] = randomChar;
            }
        }

        shuffleArray(letters);

        // change the buttons text
        btn1.setText(letters, 0, 1);
        btn2.setText(letters, 1, 1);
        btn3.setText(letters, 2, 1);
        btn4.setText(letters, 3, 1);
        btn5.setText(letters, 4, 1);
        btn6.setText(letters, 5, 1);
        btn7.setText(letters, 6, 1);
        btn8.setText(letters, 7, 1);
    }

    static void shuffleArray(char[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            char a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public void isVictory(String answer)
    {
        if (answer.compareTo(stage.getName()) == 0)
        {
            k++;
            Toast.makeText(GameActivity.this, "Congratulations you WIN!\nyou move to next stage", Toast.LENGTH_LONG).show();
            if(k < 6)
            {
                this.timeIsOn = false;
                startStage(k);
            }

            else if (k == 6)
            {
                Toast.makeText(GameActivity.this, "Bravo!!!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else
        {
            Toast.makeText(GameActivity.this, "Wrong! please try again.", Toast.LENGTH_LONG).show();
            txvResult.setText(tempR);
            this.result = "";
        }
    }

    private void timerCountdown()
    {
        this.timeIsOn = true;
        Log.d("debug", "countDown()");
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = timercount; i >= 0 && timeIsOn; i--)
                {
                    final int t = i;
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            txvTimer.setText("Time: " + t);
                        }
                    });
                    Log.d("debug", "Time = " + i);
                    SystemClock.sleep(1000);    // sleep for 1000ms = 1sec

                }
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        timeIsOn = false;
                        k = 1;
                        startStage(k);
                    }
                });
            }
        }).start();
    }

    // for play music & sound files
    private void playMusic()
    {
        loadData();
        Uri uri = Uri.parse(path);
        Log.e("$$$Path", path);

        if (path.isEmpty())
        {
            mediaPlayer = MediaPlayer.create(this, R.raw.theme);
            mediaPlayer.setLooping(true);
        }

        else
        {
            mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.setLooping(true);
        }
    }

    //loading data from SharedPreferences
    public void loadData()
    {
        SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        this.musicIsOn = sp.getBoolean("switchMusic", false);
        this.path = sp.getString("musicPath", "");
        this.radioId = sp.getInt("radioButton", 2131165232);

        if (radioId == 2131165232)
        {
            timercount = 60;
        }
        else if (radioId == 2131165231)
        {
            timercount = 30;
        }
        else if (radioId == 2131165233)
        {
            timercount = 90;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("debug", "GameActivity onStart()");

        // start the music
        loadData();
        if (musicIsOn)
        {
            playMusic();
            mediaPlayer.start();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("debug", "GameActivity onStop()");

        if (musicIsOn)
        {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d("debug", "GameActivity onDestroy()");

        this.timeIsOn = false;
        if (tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
        finish();
    }
}
