package com.jlzk.apps.covid19StatsPh;

import android.app.*;
import android.content.*;
import android.os.*;
import android.text.*;
import android.widget.*;
import com.squareup.okhttp.*;
import java.io.*;
import java.net.*;
import org.apache.http.client.*;
import org.json.*;

public class MainActivity extends Activity 
{

	private TextView data;
	
	private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		data = (TextView) findViewById(R.id.textView);

		new GeoLocation(this).execute();
		new GetHttpResponse(this).execute();
    }

	// JSON parse class started from here.
    private class GetHttpResponse extends AsyncTask<JSONObject, Void, JSONObject>
    {
        public Context context;

        private ProgressDialog pd;

        public GetHttpResponse(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
			pd = ProgressDialog.show(context, null, "Refreshing...");
        }

        @Override
        protected JSONObject doInBackground(JSONObject... arg0)
        {
            // Passing HTTP URL to HttpServicesClass Class.

            try
			{
				OkHttpClient client = new OkHttpClient();

				Request request = new Request.Builder()
					.url("https://covid-19-coronavirus-statistics.p.rapidapi.com/v1/stats?country="+country)
					.get()
					.addHeader("x-rapidapi-host", "covid-19-coronavirus-statistics.p.rapidapi.com")
					.addHeader("x-rapidapi-key", "0c5e3d1d1fmshfb36352d0654a77p17e786jsn10e4114391f3")
					.build();

				Response response = client.newCall(request).execute();


				ResponseBody body = response.body();

				return new JSONObject(body.string());
			}
			catch (JSONException e)
			{

			}
			catch (IOException e)
			{

			}

			return null;
		}

        @Override
        protected void onPostExecute(JSONObject result)
        {
            pd.dismiss();

			try
			{
				JSONObject dataOb = result.getJSONObject("data").getJSONArray("covid19Stats").getJSONObject(0);

				String format = "As of now %s deaths in %s,Total of %s confirmed cases of coronavirus reported in island nation,  %s recovered.";
				data.setText(Html.fromHtml(String.format(format,dataOb.getString("deaths"),country,dataOb.getString("confirmed"),dataOb.getString("recovered"))));
			}
			catch (JSONException e)
			{

			}			
        }
    }
	
	public  class GeoLocation extends AsyncTask<JSONObject,JSONObject,JSONObject>
	{

		Context context;

		public GeoLocation(Context context)
		{
			this.context = context;
		}


		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}


		@Override
		protected JSONObject doInBackground(JSONObject[] arg)
		{

			JSONObject obj = null;
			try
			{
				HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://ip-api.com/json").openConnection();
				httpURLConnection .setRequestMethod("GET");

				InputStream is = httpURLConnection.getInputStream();
				BufferedReader input = new BufferedReader(new InputStreamReader(is));

				StringBuilder stringBuilder = new StringBuilder();
				while (true)
				{
					String readLine = input.readLine();
					if (readLine == null)
					{
						break;
					}
					stringBuilder.append(readLine);
				}
				obj = new JSONObject(stringBuilder.toString());
			}
			catch (UnsupportedEncodingException e)
			{
				cancel(true);
			}
			catch (ClientProtocolException e)
			{
				cancel(true);
			}
			catch (JSONException e)
			{
				cancel(true);
			}
			catch (IOException e)
			{
				cancel(true);
			}

			return obj;
		}

		@Override
		protected void onPostExecute(JSONObject result)
		{
			super.onPostExecute(result);

			StringBuffer buff = new StringBuffer();
			buff.append("<b>Geo Location Info:</b><br>");
			try
			{
				buff.append("Country: ").append(result.getString("country")).append("<br>");
				buff.append("Country code: ").append(result.getString("countryCode")).append("<br>");
				buff.append("Region: ").append(result.getString("regionName")).append("<br>");
				buff.append("Region code: ").append(result.getString("region")).append("<br>");
				buff.append("City: ").append(result.getString("city")).append("<br>");
				buff.append("Zip Code: ").append(result.getString("zip")).append("<br>");
				buff.append("Latitude: ").append(result.getString("lat")).append("<br>");
				buff.append("Longitude: ").append(result.getString("lon")).append("<br>");
				buff.append("Time Zone: ").append(result.getString("timezone")).append("<br>");
				buff.append("ISP: ").append(result.getString("isp")).append("<br>");
				buff.append("Organization: ").append(result.getString("org")).append("<br>");
				buff.append("AS number/name: ").append(result.getString("as")).append("<br>");
				buff.append("Internal IP: ").append(result.getString("query"));

				country = result.getString("country");
			}
			catch (JSONException e)
			{
				buff.append("null");
			}

			
		}

	}
}

