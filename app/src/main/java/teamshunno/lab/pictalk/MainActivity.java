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
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import teamshunno.lab.pictalk.adapters.ObjectAdapter;
import teamshunno.lab.pictalk.listeners.OnItemClickListener;

public class MainActivity extends AppCompatActivity implements OnItemClickListener, View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {

    final int VERB_DO = 0;
    final int VERB_DONT = 1;
    final int VERB_WANT = 2;
    final int VERB_DONT_WANT = 3;

    final String PREF_SPEECH_RATE = "speech_rate";

    RelativeLayout mContainer;
    Context mContext;
    SharedPreferences sharedPref;
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
                    {"vec_object_walking", "হাটতে যেতে"},
                    {"vec_object_toilet", "টয়লেটে যেতে"},
                    {"vec_object_study", "পড়াশুনা করতে"},
                    {"vec_object_cartoon", "কার্টুন দেখতে"}
            },
            {
                    {"vec_object_water", "পানি খেতে"},
                    {"vec_object_juice", "শরবত খেতে"},
                    {"vec_object_chocolate", "চকলেট"},
                    {"vec_object_fish", "মাছ খেতে"},
                    {"vec_object_meat", "মাংস খেতে"},
                    {"vec_object_chicken", "মুরগি খেতে"},
                    {"vec_object_apple", "ফল খেতে"}
            },
            {
                    {"vec_object_ball", "বল"},
                    {"vec_object_doll", "পুতুল"},
                    {"vec_object_car", "গাড়ি"},
                    {"vec_object_pencils", "রং পেন্সিল"}
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
    int speech_rate = 10;
    boolean isBackPressed = false;
    private TextToSpeech mTTS;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainer = findViewById(R.id.main_container);

        mContext = MainActivity.this;

        sharedPref = MainActivity.this.getSharedPreferences(getPackageName(), MODE_PRIVATE);

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

        speech_rate = sharedPref.getInt(PREF_SPEECH_RATE, 10);

        /**
         * Text to Speech
         */
        mTTS = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    int result = mTTS.setLanguage(new Locale("bn_BD"));

                    setSpeechRate();

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
        activeObjectTextView.setText(mDataset[active_type][position][1]);

        if (mDataset[active_type][position][0].startsWith("vec_")) {
            activeObjectImageView
                    .setImageDrawable(ContextCompat.getDrawable(mContext, mContext.getResources().getIdentifier(mDataset[active_type][position][0], "drawable", BuildConfig.APPLICATION_ID)));
        } else {
            Picasso.get()
                    .load(getResources().getIdentifier(mDataset[active_type][position][0], "drawable", BuildConfig.APPLICATION_ID))
                    .into(activeObjectImageView);

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonVerbPositive:
                activeVerbImageView.setImageDrawable(((ImageView) v).getDrawable());
                activeVerbImageView.setTag(VERB_WANT);

                speakNow();

                break;
            case R.id.buttonVerbNegative:
                activeVerbImageView.setImageDrawable(((ImageView) v).getDrawable());
                activeVerbImageView.setTag(VERB_DONT_WANT);

                speakNow();

                break;

            case R.id.buttonSpeak:

                speakNow();
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
                Toast.makeText(mContext, "Hold the Button to view Settings menu", Toast.LENGTH_LONG).show();
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

                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.setOnMenuItemClickListener(this);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.option_menu, popupMenu.getMenu());
                popupMenu.show();

                return true;
        }

        return false;
    }

//    boolean isPreviewCompleted() {
//        if (!activeObjectTextView.getText().toString().isEmpty()
//                && Integer.valueOf(activeVerbImageView.getTag().toString()) != -1) {
//            return true;
//        }
//
//        return false;
//    }

    void speakNow() {
        mTTS.speak(getPreviewText(), TextToSpeech.QUEUE_FLUSH, null);
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.speech_rate:

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final SeekBar seekBar = new SeekBar(mContext);
                seekBar.setMax(10);
                seekBar.setProgress(speech_rate);

                builder.setView(seekBar);
                builder.setTitle("Change Speech Rate");
                builder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        speech_rate = seekBar.getProgress();

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(PREF_SPEECH_RATE, speech_rate);
                        editor.apply();

                        setSpeechRate();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.show();

                return true;

            case R.id.about:

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

    void setSpeechRate() {
        mTTS.setSpeechRate((float) (speech_rate / 10.0));
    }
}