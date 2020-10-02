package github.mjksabit.nasasa.accelometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private VrPanoramaView mVRPanoramaView;
    private ImageView satelliteView;

    private int index = 2;

    private Satellite satellite = new Satellite(293.20056f, 26.058495f);

    private String[] images = {
            "25654595177_7f4f9ad12a_k.jpg","31903386543_6e3a302f12_k.jpg","33817337141_d81d7acae9_k.jpg","34770759254_db8e47f6c8_k.jpg","50113651081_9135acc2bd_o.jpg","50131496382_234fea5e45_k.jpg","50132874773_c0c0096a18_o.jpg","50144016137_d269a5390b_o.jpg","50225566643_699dfb4155_o.jpg","50231114598_a57528d65d_o.jpg","50259899302_6394fb84d7_o.jpg"
    };

    private Rotation rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this;
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        mVRPanoramaView = findViewById(R.id.vrPanoramaView);
        satelliteView = findViewById(R.id.satellight);

        satelliteView.setImageResource(R.drawable.satellite_mono);

        loadPhotoSphere();
        setupRotation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        rotation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        rotation.stop();

        mVRPanoramaView.pauseRendering();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rotation.start();
        mVRPanoramaView.resumeRendering();
    }

    @Override
    protected void onDestroy() {
        mVRPanoramaView.shutdown();
        rotation.stop();
        super.onDestroy();
    }

    private void loadPhotoSphere() {
        VrPanoramaView.Options options = new VrPanoramaView.Options();
        InputStream inputStream = null;

        AssetManager assetManager = getAssets();
        try {
            inputStream = assetManager.open(images[index%images.length]);
            options.inputType = VrPanoramaView.Options.TYPE_MONO;
            mVRPanoramaView.loadImageFromBitmap(BitmapFactory.decodeStream(inputStream), options);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    private void setupRotation() {
        rotation = new Rotation(this);
        Rotation.RotationListener cl = getRotationListener();
        rotation.setListener(cl);
    }

    private Rotation.RotationListener getRotationListener() {
        return (azimuth, vAngle) -> runOnUiThread(() -> {
            Pair<Float, Float> pair = satellite.getPosition(azimuth, vAngle);
            if (pair != null) {
                satelliteView.setVisibility(View.VISIBLE);

                float HBias = 0.5f + pair.first;
                float VBias = 0.5f - pair.second;

                Log.d(TAG, "getRotationListener: _ " + HBias + " | " + VBias);
                Log.d(TAG, "getRotationListener: " + satelliteView.getDrawable());

                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) satelliteView.getLayoutParams();
                params.horizontalBias = HBias;
                params.verticalBias = VBias;
                satelliteView.setLayoutParams(params);

            } else {
                satelliteView.setVisibility(View.GONE);
            }
        });
    }
}