package asusx.testtranslater;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView text;
    EditText ToTranslate;
    EditText TranslText;
    Spinner FirstSP;
    Spinner SecondSP;
    Map<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.history);
        ToTranslate = (EditText) findViewById(R.id.ToTranslate);
        TranslText = (EditText) findViewById(R.id.TranslateText);
        FirstSP = (Spinner) findViewById(R.id.FirstList);
        SecondSP = (Spinner) findViewById(R.id.SecondList);
        String[] languages = {"Русский", "Английский", "Немецкий"};
        map=new HashMap<String,String>();
        map.put("Русский","ru");
        map.put("Английский", "en");
        map.put("Немецкий", "de");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, languages);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        FirstSP.setAdapter(adapter);
        FirstSP.setSelection(0);
        SecondSP.setAdapter(adapter);
        SecondSP.setSelection(1);
    }

    public void StartTranslate(View view) {
        Context context = this;
        TranslatorBackgroundTask NewTask = new TranslatorBackgroundTask(context);
        String ToTranslateText, Pairs = GetPairs();
        ToTranslateText=ToTranslate.getText().toString();
        NewTask.execute(ToTranslateText,Pairs);
    }

    private String GetPairs()
    {
        String From,To,Result="";
        From=FirstSP.getSelectedItem().toString();
        To=SecondSP.getSelectedItem().toString();
        Result=map.get(From)+"-"+map.get(To);
        return Result;
    }


    public class TranslatorBackgroundTask extends AsyncTask<String, Void, String> {
        //Declare Context
        Context ctx;

        //Set Context
        TranslatorBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            //String variables
            String textToBeTranslated = params[0];
            String languagePair = params[1];

            String jsonString;

            try {
                //Set up the translation call URL
                String yandexKey = "trnsl.1.1.20171111T171718Z.0844c6810391f598.2f12753721434a4a5240f4d5081195ff1d563fdb";
                String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                        + "&text=" + textToBeTranslated + "&lang=" + languagePair;
                URL yandexTranslateURL = new URL(yandexUrl);

                //Set Http Conncection, Input Stream, and Buffered Reader
                HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
                InputStream inputStream = httpJsonConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Set string builder and insert retrieved JSON result into it
                StringBuilder jsonStringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine()) != null) {
                    jsonStringBuilder.append(jsonString + "\n");
                }

                //Close and disconnect
                bufferedReader.close();
                inputStream.close();
                httpJsonConnection.disconnect();

                //Making result human readable
                String resultString = jsonStringBuilder.toString().trim();
                //Getting the characters between [ and ]
                resultString = resultString.substring(resultString.indexOf('[') + 1);
                resultString = resultString.substring(0, resultString.indexOf("]"));
                //Getting the characters between " and "
                resultString = resultString.substring(resultString.indexOf("\"") + 1);
                resultString = resultString.substring(0, resultString.indexOf("\""));

                return resultString;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            TranslText.setText(result);
        }
    }
}
