package fi.jamk.vko_38_picasso_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GetFeed getFeed;
    private ListView listView;
    private ArrayList<String> imgPaths = new ArrayList<String>();
    private ArrayList<String> imgDescs = new ArrayList<>();
    private ArrayList<String> imgLinks = new ArrayList<>();
    private ProgressBar progressBar;
    private EditText editTags;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        inputMethodManager = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
        editTags = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // item listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String link = imgLinks.get(position);
                // create an explicit intent
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));

                // start a new activity
                startActivity(intent);
            }
        });

        editTags.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchButtonPressed(findViewById(R.id.searchButton));
                    editTags.clearFocus();
                    return true;
                }
                return false;
            }
        });
    }

    public void searchButtonPressed(View view){
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        imgPaths.clear();
        imgDescs.clear();
        imgLinks.clear();

        listView.setVisibility(View.INVISIBLE);

        getFeed = new GetFeed();

        getFeed.execute();
    }


    private class GetFeed extends AsyncTask<Void, Void, JSONObject>
    {
        String tags = editTags.getText().toString();

        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            String str = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&tags=" + tags;
            URLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL(str);
                urlConnection = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if(line.contains("jsonFlickrFeed({")) {
                        line = "{";
                    }
                    stringBuffer.append(line);
                }
                String jsonString = stringBuffer.toString();
                jsonString = jsonString.substring(0, jsonString.length() - 1);
                return new JSONObject(jsonString);

            } catch (Exception ex) {
                Log.e("App", "yourDataTask", ex);
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject response)
        {
            progressBar.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            if(response != null)
            {
                try {
                    JSONArray jsonArray = response.getJSONArray("items");
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                        String imgPath = jsonObject.get("media").toString();
                        imgPath = imgPath.substring(6, imgPath.length() - 2);
                        imgPaths.add(imgPath);
                        imgDescs.add(jsonObject.get("title").toString());
                        imgLinks.add(jsonObject.get("link").toString());
                    }

                    ImageFeedArrayAdapter adapter = new ImageFeedArrayAdapter(getApplicationContext(), imgPaths, imgDescs);

                    listView.setAdapter(adapter);

                } catch (JSONException ex) {
                    Log.e("App", "Failure", ex);
                }
            }
        }
    }
}
