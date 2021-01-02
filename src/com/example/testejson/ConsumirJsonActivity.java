package com.example.testejson;

public class ConsumirJsonActivity extends ListActivity {

	ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new DownloadJsonAsyncTask()
//		.execute("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.places%20where%20text%3D%22rio%20de%20janeiro%2C%20brazil%22&format=json&diagnostics=true&callback=");
		//.execute("https://api.twitter.com/1/trends/23424768.json");
		.execute("http://www.ticket.com.br/portal-web/consult-card/balance/json?chkProduto=TA&card=6033425628415009");
		//.execute("http://www.ticket.com.br/portal-web/consult-card/release/json?txtOperacao=lancamentos&token=dGlja2V0RWRlbnJlZDIyMTQzIDA2MTEtMzUwOS0zNzY0MzEgMTExOC0wOTQ1&card=6033425628415009&rows=10");
		// TR: 6033425628415009
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Trend trend = (Trend) l.getAdapter().getItem(position);

		Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(trend.url));
		startActivity(it);
	}

	private List<Trend> getTrends(String jsonString) {

		List<Trend> trends = new ArrayList<Trend>();
		
		try {
			JSONObject trendLists = new JSONObject(jsonString);
			JSONObject card = trendLists.getJSONObject("card");
			JSONObject balance = card.getJSONObject("balance"); 

			String saldoVal = balance.getString("value");
			String saldoString = "Saldo Ticket:";

			Trend objetoTrend1 = new Trend();
			objetoTrend1.name = saldoString;
			objetoTrend1.url = saldoVal;
			trends.add(objetoTrend1);
			
			Trend objetoTrend2 = new Trend();
			objetoTrend2.name = saldoVal;
			objetoTrend2.url = saldoString;
			trends.add(objetoTrend2);
			
//			JSONObject trend;
//			for (int i = 0; i < trendsArray.length(); i++) {
//				trend = new JSONObject(trendsArray.getString(i));
//
//				Log.i("DEVMEDIA", "nome=" + trend.getString("balance"));
//				
//				Trend objetoTrend = new Trend();
//				objetoTrend.name = trend.getString("name");
//				objetoTrend.url = trend.getString("url");
//				
//				trends.add(objetoTrend);
//			}
		} catch (JSONException e) {
			Log.e("DEVMEDIA", "Erro no parsing do JSON", e);
		}

		return trends;
	}
	
	class DownloadJsonAsyncTask extends AsyncTask<String, Void, List<Trend>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(ConsumirJsonActivity.this, "Aguarde", "Baixando JSON, por favor aguarde...");
		}
		
		@Override
		protected List<Trend> doInBackground(String... params) {
			String urlString = params[0];

			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(urlString);
			httpget.setHeader("Host", "www.ticket.com.br");
			httpget.setHeader("Referer", "http://www.ticket.com.br/portal/portalcorporativo/home/home.htm");
			httpget.setHeader("X-Requested-With", "XMLHttpRequest");
			httpget.setHeader("Accept", "application/json,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httpget.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.57 Safari/537.36");
			httpget.setHeader("Connection", "keep-alive");

			try {
				HttpResponse response = httpclient.execute(httpget);

				HttpEntity entity = response.getEntity();

				if (entity != null) {
					InputStream instream = entity.getContent();

					String json = toString(instream);
					
					instream.close();
					
					List<Trend> trends = getTrends(json);
					
					return trends;
				}
			} catch (Exception e) {
				Log.e("DEVMEDIA", "Falha ao acessar Web service", e);
			}
			return null;
		}
		
		private String toString(InputStream is) throws IOException {

			byte[] bytes = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int lidos;
			while ((lidos = is.read(bytes)) > 0) {
				baos.write(bytes, 0, lidos);
			}
			return new String(baos.toByteArray());
		}
		
		@Override
		protected void onPostExecute(List<Trend> result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result.size() > 0) {
				ArrayAdapter<Trend> adapter = new ArrayAdapter<Trend>(
						ConsumirJsonActivity.this,
						android.R.layout.simple_list_item_1, result);
				setListAdapter(adapter);

			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ConsumirJsonActivity.this).setTitle("Aten��o")
						.setMessage("N�o foi possivel acessar essas inform��es...")
						.setPositiveButton("OK", null);
				builder.create().show();
			}
		}
	}
}
