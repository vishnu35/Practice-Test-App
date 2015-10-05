package com.sktekki.agile;

import java.util.regex.Pattern;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class TestScreen extends Activity implements OnClickListener {

	Button SubmitQ;
	Button NextQ;
	TextView question;
	RadioButton optionA;
	RadioButton optionB;
	RadioButton optionC;
	RadioButton optionD;
	RadioGroup options;
	TextView TextQuestionNumber;
	TextView TextExplanation;
	LinearLayout TestScreenLayout;

	String CorrectAnswer;
	String Explanation;
	int QuestionNumber;
	int CorrectNumber;
	String QuestionId;

	Toast myToast;
	SoapObject tableRow;

	Dialog dialog;
	Boolean FirstQuestion;

	private AdView adView;
	private InterstitialAd mInterstitial;

	private static final String SOAP_ACTION_GETQUESTION = "http://tempuri.org/GetQuestion";
	private static final String METHOD_NAME_GETQUESTION = "GetQuestion";

	private static final String SOAP_ACTION_LOGUSER = "http://tempuri.org/LogUser";
	private static final String METHOD_NAME_LOGUSER = "LogUser";

	private static final String NAMESPACE = "http://tempuri.org/";
	private static final String URL = "http://agile4practice.com/AndroidDbAccess.asmx";

	@SuppressLint({ "ShowToast", "NewApi" })
	private void Initialise() {
		question = (TextView) findViewById(R.id.Question);
		optionA = (RadioButton) findViewById(R.id.OptionA);
		optionB = (RadioButton) findViewById(R.id.OptionB);
		optionC = (RadioButton) findViewById(R.id.OptionC);
		optionD = (RadioButton) findViewById(R.id.OptionD);
		options = (RadioGroup) findViewById(R.id.OptionGroup);
		SubmitQ = (Button) findViewById(R.id.SubmitAns_Btn);
		NextQ = (Button) findViewById(R.id.Next_btn);

		TextQuestionNumber = (TextView) findViewById(R.id.QuestionNumber);
		TextExplanation = (TextView) findViewById(R.id.Explanation);
		
		TestScreenLayout = (LinearLayout) findViewById(R.id.linearLay);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		myToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		SharedPreferences HigheshScore = getSharedPreferences("QUESTIONS", 0);
		QuestionNumber = Integer.parseInt(HigheshScore.getString("total", "0"));
		CorrectNumber = Integer
				.parseInt(HigheshScore.getString("correct", "0"));
		
		FirstQuestion= true;
		adView = (AdView)this.findViewById(R.id.adView);	
		AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	    
	    mInterstitial = new InterstitialAd(TestScreen.this);
		mInterstitial.setAdUnitId(getResources().getString(R.string.ad_unit_id2));    
		AdRequest adRequest2 = new AdRequest.Builder().build();
	    mInterstitial.loadAd(adRequest2);
		
	}

	private void ToastMsg(String Msg) {
		myToast.setText(Msg);
		myToast.setGravity(Gravity.CENTER, 0, 0);
		myToast.show();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private void NoInternetDailog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(TestScreen.this);
		builder.setMessage("Please Check your internet connectivity.");
		builder.setCancelable(false);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				TestScreen.this.finish();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void LogUser(String Message) {
		
			try {
				String possibleEmail = null;
				Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
				Account[] accounts = AccountManager.get(this).getAccounts();
				for (Account account : accounts) {
					if (emailPattern.matcher(account.name).matches()) {
						possibleEmail = account.name;

					}
				}

				SoapObject request = new SoapObject(NAMESPACE,
						METHOD_NAME_LOGUSER);
				request.addProperty("token", "xxxxxxxx");
				request.addProperty("userInfo", possibleEmail);
				request.addProperty("questionId", QuestionId);
				request.addProperty("message", Message);

				SoapSerializationEnvelope soapEnv = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				soapEnv.dotNet = true;
				soapEnv.setOutputSoapObject(request);

				HttpTransportSE HttpTransport = new HttpTransportSE(URL);

				HttpTransport.call(SOAP_ACTION_LOGUSER, soapEnv);
				// SoapPrimitive result = (SoapPrimitive)soapEnv.getResponse();

			} catch (Exception e) {
				 //Log.e("A4pError", e.getMessage());
			}
	}

	private boolean CheckAnswer() {
		int selectedId = options.getCheckedRadioButtonId();
		RadioButton selectedRadio = (RadioButton) findViewById(selectedId);
		if (CorrectAnswer.equals(selectedRadio.getText())) {
			return true;
		}
		return false;
	}

	private int GetCorrectAnsId() {

		if (CorrectAnswer.equals(optionA.getText())) {
			return R.id.OptionA;
		} else if (CorrectAnswer.equals(optionB.getText())) {
			return R.id.OptionB;
		} else if (CorrectAnswer.equals(optionC.getText())) {
			return R.id.OptionC;
		} else {
			return R.id.OptionD;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testscreen);
		Initialise();
		TestScreenLayout.setVisibility(View.GONE);
		new GenerateQuestionAsyn().execute();
		SubmitQ.setOnClickListener(this);
		NextQ.setOnClickListener(this);

		TextQuestionNumber.setText("Question Attempted:" + QuestionNumber
				+ " / Correct:" + CorrectNumber);
	}
	
	public void onClick(View v) {
		if (options.getCheckedRadioButtonId() != -1) {
			if (SubmitQ == v) {
				new SubmitQuestionAsyn().execute();

			} else if (NextQ == v) {
				new GenerateQuestionAsyn().execute();

				ScrollView sView = (ScrollView) findViewById(R.id.ScrollView1);
				sView.fullScroll(View.FOCUS_UP);				
			}
		} else {
			ToastMsg("Please Select One Option!");
		}
	}

	public class GenerateQuestionAsyn extends AsyncTask<Void, Void, SoapObject> {

		@Override
		protected void onPreExecute() {
			dialog = new Dialog(TestScreen.this);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.progressdailog);
			dialog.show();			
		}

		@Override
		protected SoapObject doInBackground(Void... params) {		
			
			if(isNetworkAvailable()){		        
			try {
				SoapObject request = new SoapObject(NAMESPACE,

				METHOD_NAME_GETQUESTION);
				request.addProperty("token", "xxxxxxxx");

				SoapSerializationEnvelope soapEnv = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				soapEnv.dotNet = true;
				soapEnv.setOutputSoapObject(request);

				HttpTransportSE HttpTransport = new HttpTransportSE(URL);

				HttpTransport.call(SOAP_ACTION_GETQUESTION, soapEnv);
				SoapObject resultset = (SoapObject) soapEnv.getResponse();
				SoapObject Dataset = (SoapObject) resultset.getProperty(1);
				SoapObject table = (SoapObject) Dataset.getProperty(0);
				tableRow = (SoapObject) table.getProperty(0);
				return tableRow;
			} catch (Exception e) {
				//Log.e("A4pError", e.getMessage());
				return null;
			}
			}else return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(SoapObject tableR) {
			
			if (tableR != null) {				
				optionA.setTextColor(Color.BLACK);
				optionB.setTextColor(Color.BLACK);
				optionC.setTextColor(Color.BLACK);
				optionD.setTextColor(Color.BLACK);
				options.clearCheck();

				question.setText("Q."
						+ tableR.getProperty("QuestionsDescription").toString());
				optionA.setText(tableR.getProperty("A").toString());
				optionB.setText(tableR.getProperty("B").toString());
				optionC.setText(tableR.getProperty("C").toString());
				optionD.setText(tableR.getProperty("D").toString());
				CorrectAnswer = tableR.getProperty(
						tableR.getProperty("Answer").toString()).toString();
				Explanation = tableR.getProperty("Explanation").toString();
				QuestionId = tableR.getProperty("QuestionNumber").toString();
				String exp = "<font color=#B40404>Explanation: </font>"
						+ Explanation;
				TextExplanation.setText(Html.fromHtml(exp));
				
				SubmitQ.setVisibility(View.VISIBLE);
				NextQ.setVisibility(View.GONE);
				TextExplanation.setVisibility(View.GONE);
				
				for (int i = 0; i < options.getChildCount(); i++) {
					((RadioButton) options.getChildAt(i)).setEnabled(true);
				}
				TestScreenLayout.setVisibility(View.VISIBLE);
				dialog.dismiss();	
				if (mInterstitial.isLoaded()) {
		            mInterstitial.show();
		        }						
			} else {
				dialog.dismiss();
				NoInternetDailog();
			}
		}

	}

	public class SubmitQuestionAsyn extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {			
			dialog = new Dialog(TestScreen.this);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.progressdailog);
			dialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			
			QuestionNumber = QuestionNumber + 1;
			if (CheckAnswer()) {
				CorrectNumber = CorrectNumber + 1;
				LogUser("Correct");
			} else {
				LogUser("Wrong");
			}
			return null;
			
		}
		@Override
		protected void onPostExecute(Void result) {
			SubmitQ.setVisibility(View.GONE);
			NextQ.setVisibility(View.VISIBLE);
			TextExplanation.setVisibility(View.VISIBLE);

			int selectedId = options.getCheckedRadioButtonId();
			RadioButton selectedRadio = (RadioButton) findViewById(selectedId);
			selectedRadio.setTextColor(Color.RED);

			RadioButton correctRadio = (RadioButton) findViewById(GetCorrectAnsId());
			correctRadio.setTextColor(Color.GREEN);

			for (int i = 0; i < options.getChildCount(); i++) {
				((RadioButton) options.getChildAt(i)).setEnabled(false);
			}
			
			
			TextQuestionNumber.setText("Question Attempted:"
					+ QuestionNumber + " / Correct:" + CorrectNumber);
			dialog.dismiss();
		}
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences QuestionsNumberSetting = getSharedPreferences(
				"QUESTIONS", 0);
		SharedPreferences.Editor editor = QuestionsNumberSetting.edit();
		editor.putString("total", String.valueOf(QuestionNumber));
		editor.putString("correct", String.valueOf(CorrectNumber));
		editor.commit();
		
		
		EasyTracker.getInstance(this).activityStop(this);  // Add this method
	}
	
	@Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }
	
	@Override
	protected void onPause() {
		adView.pause();
		super.onPause();
	}
	@Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }
	
	 @Override
	    protected void onDestroy() {
		 adView.destroy();
	        super.onDestroy();
	    }

}
