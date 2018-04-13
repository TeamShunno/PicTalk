/**
 * Bismillahir Rahmanir Rahim
 * <p>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * For Future Information:
 *
 * I Used following library for image resize:
 * https://github.com/twaddington/android-asset-resizer
 */
package teamshunno.lab.pictalk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import teamshunno.lab.pictalk.adapters.ObjectAdapter;
import teamshunno.lab.pictalk.listeners.OnItemClickListener;

public class MainActivity extends AppCompatActivity implements OnItemClickListener, View.OnClickListener, View.OnLongClickListener {

    final int VERB_DO = 0;
    final int VERB_DONT = 1;
    final int VERB_WANT = 2;
    final int VERB_DONT_WANT = 3;

    RelativeLayout mContainer;
    Context mContext;
    /**
     * Types:
     * 1: General
     * 2: Food
     * 3: Game
     */
    String[][][] mDataset = {
            {
                    {"object_bed", "ঘুমাতে"},
                    {"object_bath", "গোসল করতে"},
                    {"object_home", "বাসায় যেতে"},
                    {"object_wash_hand", "হাত ধুঁতে"},
                    {"object_bed", "হাটতে যেতে"},
                    {"object_bath", "বাথরুমে যেতে"},
                    {"object_home", "পড়াশুনা করতে"},
                    {"object_wash_hand", "কার্টুন দেখতে"}
            },
            {
                    {"object_bed", "পানি খেতে"},
                    {"object_bed", "শরবত খেতে"},
                    {"object_bath", "চকলেট"},
                    {"object_home", "মাছ খেতে"},
                    {"object_wash_hand", "মাংস খেতে"},
                    {"object_bed", "মুরগি খেতে"},
                    {"object_bath", "ফল খেতে"}
            },
            {
                    {"object_bed", "ঘুমাতে"},
                    {"object_bath", "গোসল করতে"},
                    {"object_home", "বাসায় যেতে"},
                    {"object_wash_hand", "হাত ধুঁতে"}
            }
    };
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ImageView activeObjectImageView;
    TextView activeObjectTextView;
    ImageView activeVerbImageView;
    ImageView buttonVerbPositive;
    ImageView buttonVerbNegative;
    ImageButton buttonSpeak;
    ImageView button_emo_like;
    ImageView button_emo_dislike;
    ImageView button_emo_yes;
    ImageView button_emo_no;
    View button_type_general;
    View button_type_food;
    View button_type_game;
    ImageButton buttonBackSpace;
    ImageButton buttonAbout;
    int active_type = 0;
    boolean isBackPressed = false;
    private TextToSpeech mTTS;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainer = findViewById(R.id.main_container);

        mContext = MainActivity.this;

        activeObjectImageView = findViewById(R.id.preview_object_image);
        activeObjectTextView = findViewById(R.id.preview_object_name);
        activeVerbImageView = findViewById(R.id.preview_verb_image);

        buttonVerbPositive = findViewById(R.id.buttonVerbPositive);
        buttonVerbPositive.setOnClickListener(this);
        buttonVerbNegative = findViewById(R.id.buttonVerbNegative);
        buttonVerbNegative.setOnClickListener(this);

        buttonSpeak = findViewById(R.id.buttonSpeak);
        buttonSpeak.setOnClickListener(this);

        buttonBackSpace = findViewById(R.id.buttonBackSpace);
        buttonBackSpace.setOnClickListener(this);

        buttonAbout = findViewById(R.id.button_about);
        buttonAbout.setOnLongClickListener(this);
        buttonAbout.setOnClickListener(this);

        /**
         * Type Buttons
         */
        button_type_general = findViewById(R.id.button_type_general);
        button_type_general.setOnClickListener(this);
        button_type_food = findViewById(R.id.button_type_food);
        button_type_food.setOnClickListener(this);
        button_type_game = findViewById(R.id.button_type_game);
        button_type_game.setOnClickListener(this);

        /**
         * Emotion Buttons
         */
        button_emo_like = findViewById(R.id.button_emo_like);
        button_emo_like.setOnClickListener(this);
        button_emo_dislike = findViewById(R.id.button_emo_dislike);
        button_emo_dislike.setOnClickListener(this);
        button_emo_yes = findViewById(R.id.button_emo_yes);
        button_emo_yes.setOnClickListener(this);
        button_emo_no = findViewById(R.id.button_emo_no);
        button_emo_no.setOnClickListener(this);

        /**
         * For making the app full screen
         *
         * NOTE: I done it from styles.xml. It that cause any problem then below code will use.
         */
//        mContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        mRecyclerView = findViewById(R.id.object_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new ObjectAdapter(active_type, mDataset, mContext, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         * Text to Speech
         */
        mTTS = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    int result = mTTS.setLanguage(new Locale("bn_BD"));

//                    Log.e("aaa", mTTS.getAvailableLanguages().toString());


                    if (result == TextToSpeech.LANG_MISSING_DATA) {

                        Toast.makeText(mContext, "Language data is Missing!", Toast.LENGTH_LONG).show();

                    } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {

                        Toast.makeText(mContext, "Language is not supported!", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (isBackPressed) {
            super.onBackPressed();
            return;
        }

        isBackPressed = true;

        Toast.makeText(MainActivity.this, "Press once again to exit", Toast.LENGTH_LONG)
                .show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                isBackPressed = false;
            }

        }, 2000);
    }

    @Override
    public void onClick(int position) {
        Log.e("aaa", String.valueOf(position));

        activeObjectTextView.setText(mDataset[active_type][position][1]);
        Picasso.get().load(getResources().getIdentifier(mDataset[active_type][position][0], "drawable", BuildConfig.APPLICATION_ID)).into(activeObjectImageView);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonVerbPositive:
                activeVerbImageView.setImageDrawable(((ImageView) v).getDrawable());
                activeVerbImageView.setTag(VERB_WANT);
                break;
            case R.id.buttonVerbNegative:
                activeVerbImageView.setImageDrawable(((ImageView) v).getDrawable());
                activeVerbImageView.setTag(VERB_DONT_WANT);
                break;

            case R.id.buttonSpeak:
                mTTS.speak(getPreviewText(), TextToSpeech.QUEUE_FLUSH, null);
                break;

            case R.id.buttonBackSpace:
                if (Integer.valueOf(activeVerbImageView.getTag().toString()) != -1) {
                    activeVerbImageView.setTag("-1");
                    activeVerbImageView.setImageDrawable(getResources().getDrawable(R.drawable.circle));
                } else if (!activeObjectTextView.getText().toString().isEmpty()) {
                    activeObjectTextView.setText("");
                    activeObjectImageView.setImageDrawable(null);
                }
                break;

            case R.id.button_about:
                Toast.makeText(mContext, "Hold the Button to view About Us", Toast.LENGTH_LONG).show();
                break;

            /**
             * Type Buttons
             */
            case R.id.button_type_general:
                active_type = 0;
                mAdapter = new ObjectAdapter(active_type, mDataset, mContext, this);
                mRecyclerView.swapAdapter(mAdapter, false);
                mRecyclerView.scrollToPosition(0);
                break;
            case R.id.button_type_food:
                active_type = 1;
                mAdapter = new ObjectAdapter(active_type, mDataset, mContext, this);
                mRecyclerView.swapAdapter(mAdapter, false);
                mRecyclerView.scrollToPosition(0);
                break;
            case R.id.button_type_game:
                active_type = 2;
                mAdapter = new ObjectAdapter(active_type, mDataset, mContext, this);
                mRecyclerView.swapAdapter(mAdapter, false);
                mRecyclerView.scrollToPosition(0);
                break;

            /**
             * Emotion Buttons
             */
            case R.id.button_emo_like:
                mTTS.speak("আমার এটি ভালো লেগেছে!", TextToSpeech.QUEUE_FLUSH, null);
                break;

            case R.id.button_emo_dislike:
                mTTS.speak("আমার এটি ভালো লাগেনি!", TextToSpeech.QUEUE_FLUSH, null);
                break;

            case R.id.button_emo_yes:
                mTTS.speak("হা", TextToSpeech.QUEUE_FLUSH, null);
                break;

            case R.id.button_emo_no:
                mTTS.speak("না", TextToSpeech.QUEUE_FLUSH, null);
                break;
        }
    }


    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {
            case R.id.button_about:

                AlertDialog.Builder aboutDialog = new AlertDialog.Builder(mContext);

                aboutDialog.setIcon(R.drawable.ic_info_outline_black_24dp);
                aboutDialog.setTitle(getString(R.string.app_name));
                aboutDialog.setMessage("Version " + BuildConfig.VERSION_NAME +
                        "\n" + "\n" +
                        "This Project is developed by—" +
                        "\n" +
                        getString(R.string.team_shunno) +
                        "\n" + "\n" +
                        "This is an Open-Source Project under MPL 2.0" +
                        "\n" +
                        "Copyright ©  " + getString(R.string.team_shunno));
                aboutDialog.show();

                return true;
        }

        return false;
    }

    /**
     * Generate the text to speak
     * <p>
     * eg., আমি ### চাই!
     *
     * @return The final text
     */
    String getPreviewText() {
        String str = "আমি ";

        if (!activeObjectTextView.getText().toString().isEmpty()) {
            str += activeObjectTextView.getText().toString();
        }

        int verb_tag = Integer.valueOf(activeVerbImageView.getTag().toString());

        if (verb_tag == VERB_WANT) {
            str += " চাই!";
        } else if (verb_tag == VERB_DONT_WANT) {
            str += " চাই না!";
        } else if (verb_tag == VERB_DO) {
            str += " করব!";
        } else if (verb_tag == VERB_DONT) {
            str += " করব না!";
        }

        return str;

//        return "কি করতে চাও?";
    }

}