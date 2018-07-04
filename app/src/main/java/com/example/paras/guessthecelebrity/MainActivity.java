package com.example.paras.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrlList=new ArrayList<String>();
    ArrayList<String> celebNameList=new ArrayList<String>();

    int choseCeleb=0;

    ImageView imageView;

    int correctAnswerLocation=0;

    String[] answers=new String[4];

    Button button0,button1,button2,button3;




    public class ImageDownload extends AsyncTask<String ,Void,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url=new URL(urls[0]);

                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();

                urlConnection.connect();

                InputStream in=urlConnection.getInputStream();

                Bitmap myBitmap=BitmapFactory.decodeStream(in);

                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public class DownloadContent extends AsyncTask<String,Void ,String >
    {

        @Override
        protected String doInBackground(String... urls) {
            String results="";
            URL url;
            HttpURLConnection urlConnection=null;


            try {

                url=new URL(urls[0]);

                urlConnection=(HttpURLConnection) url.openConnection();

                InputStream in =urlConnection.getInputStream();

                InputStreamReader reader=new InputStreamReader(in);

                int data=reader.read();

                while(data!=-1)
                {
                    char current=(char)data;

                    results+=current;

                    data=reader.read();

                }
                return results;

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }
    }


public void createNewQuestion()
{
    Random random=new Random();
    choseCeleb=random.nextInt(celebUrlList.size());


    ImageDownload imageTask=new ImageDownload();

    Bitmap celebImage;

    try {
        celebImage=imageTask.execute(celebUrlList.get(choseCeleb)).get();

        imageView.setImageBitmap(celebImage);

        correctAnswerLocation=random.nextInt(4);

        int incorrectAnswerLocation;


        for(int i=0;i<4;i++)
        {

            if(i==correctAnswerLocation)
            {

                answers[i]=celebNameList.get(choseCeleb);

            }
            else
            {

                incorrectAnswerLocation=random.nextInt(celebUrlList.size());

                while(incorrectAnswerLocation==choseCeleb) {

                    incorrectAnswerLocation=random.nextInt(celebUrlList.size());

                }
                answers[i] = celebNameList.get(incorrectAnswerLocation);

            }

        }

        button0.setText(answers[0]);
        button1.setText(answers[1]);
        button2.setText(answers[2]);
        button3.setText(answers[3]);
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    }


}

    public void celebChosen(View view)
    {

        if(view.getTag().toString().equals(Integer.toString(correctAnswerLocation)))
        {
            Toast.makeText(this,"Correct!",Toast.LENGTH_SHORT).show();
        }

        else
        {
            Toast.makeText(this,"Incorrect!  It was "+celebNameList.get(choseCeleb),Toast.LENGTH_SHORT).show();
        }

        createNewQuestion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView=(ImageView)findViewById(R.id.imageView);

        button0=(Button)findViewById(R.id.button0);
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);

        DownloadContent task=new DownloadContent();

        String result=null;

        try {

            result=task.execute("http://www.posh24.se/kandisar").get();

            String[] split=result.split("<div class=\"sidebarContainer\">");

            Pattern p=Pattern.compile("<img src=\"(.*?)\"");
            Matcher m=p.matcher(split[0]);

            while (m.find())
            {
                celebUrlList.add(m.group(1));
            }


             p=Pattern.compile("alt=\"(.*?)\"");
            m=p.matcher(split[0]);

            while (m.find())
            {
                celebNameList.add(m.group(1));
            }



            //Log.i("Contents of UrL: ",result);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        createNewQuestion();
    }
}
