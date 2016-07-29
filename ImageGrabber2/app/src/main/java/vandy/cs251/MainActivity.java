package vandy.cs251;

import android.os.Bundle;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText mUrlEditText;
    private ImageView mImageView;
    private String mDefaultUrl =
            "http://www.dre.vanderbilt.edu/~schmidt/ka.png";
    private ProgressDialog mProgressDialog;
    Handler mDownloadHandler = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the content view specified in the main.xml file.
        setContentView(R.layout.activity_main);

        // Caches references to the EditText and ImageView objects in
        // data members to optimize subsequent access.
        mUrlEditText = (EditText) findViewById(R.id.mUrlEditText);
        mImageView = (ImageView) findViewById(R.id.mImageView);

        // Initialize the downloadHandler.
        mDownloadHandler = new DownloadHandler(this);
    }

    void showErrorToast(String errorString) {
        Toast.makeText(this,
                errorString,
                Toast.LENGTH_LONG).show();
    }

    void displayImage(Bitmap image)
    {
        if (mImageView == null)
            showErrorToast("Problem with Application,"
                    + " please contact the Developer.");
        else if (image != null)
            mImageView.setImageBitmap(image);
        else
            showErrorToast("image is corrupted,"
                    + " please check the requested URL.");
    }

    public void downloadImage(View view) {
        // Obtain the requested URL from the user input.
        String url = getUrlString();

        Log.e(MainActivity.class.getSimpleName(),
                "Downloading " + url);

        hideKeyboard();

        Intent intent =
                DownloadService.makeIntent(this,
                        Uri.parse(url),
                        mDownloadHandler);

        // Start the DownloadService.
        startService(intent);
    }

    private static class DownloadHandler extends Handler {

        private WeakReference<MainActivity> mActivity;

        public DownloadHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        public void handleMessage(Message message) {
            MainActivity activity = mActivity.get();
            // Bail out if the DownloadActivity is gone.
            if (activity == null)
                return;

            // Try to extract the pathname from the message.
            String pathname = DownloadService.getPathname(message);


            activity.displayImage(BitmapFactory.decodeFile(pathname));
        }
    };

    private void hideKeyboard() {
            InputMethodManager mgr =
                    (InputMethodManager) getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(mUrlEditText.getWindowToken(),
                0);
    }

    /*
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }
    */
    String getUrlString() {
        String s = mUrlEditText.getText().toString();
        if (s.equals(""))
            s = mDefaultUrl;
        return s;
    }

}