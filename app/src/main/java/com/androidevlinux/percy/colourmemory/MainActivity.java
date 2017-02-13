package com.androidevlinux.percy.colourmemory;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;
import com.inqbarna.tablefixheaders.TableFixHeaders;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener{

    private ColorGameFragment mColorGameFragment;
    private static final int INTRO_DURATION = 600;
    private boolean mShowingHighScore = false;
    private boolean canSwitchView = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        // Log user details
        logUser();

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mColorGameFragment = new ColorGameFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.fragment_area, mColorGameFragment);
            fragmentTransaction.commit();
        } else {
            mShowingHighScore = (getFragmentManager().getBackStackEntryCount() > 0);
        }

        getFragmentManager().addOnBackStackChangedListener(this);
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(Build.SERIAL);
        Crashlytics.setUserName(Build.USER);
    }

    private void rotateScreenPermission(boolean allow) {
        if(allow)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void flipScreen() {
        if (mShowingHighScore) {
            getFragmentManager().popBackStack();
            return;
        }

        // Flip to the back.

        mShowingHighScore = true;

        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.

        Fragment highscoreFragment = new HighScoreFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(this, fragmentTransaction, mColorGameFragment, highscoreFragment, R.id.fragment_area);
        fragmentTransactionExtended.addTransition(FragmentTransactionExtended.SCALEX);
        fragmentTransactionExtended.commit();
    }




    @Override
    public void onBackStackChanged() {
        mShowingHighScore = (getFragmentManager().getBackStackEntryCount() > 0);
    }


    /**
     * A fragment representing the front of the Image card.
     */
    public static class ColorGameFragment extends Fragment implements ImageCardAdapter.GameEvents {

        private static final int GENERAL_DURATION = 1000;
        private static final int LONG_DURATION = 1500;
        private static final String GAME_PREFERENCE = "COLOUR_MEMORY_GAME_GAME_PREFERENCE";
        private TextView gameScore;
        private TextView currentScore;
        private long gameScoreCount;
        private ImageCardAdapter gameAdapter;
        private View rootView;
        private boolean gameOver;
        private boolean openDialog;

        public interface GameOverResult {
            public boolean onResult(final String username);
        }

        public ColorGameFragment() {
            rootView = null;
            openDialog = false;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            if(rootView == null) {
                gameOver = false;
                SharedPreferences preferences = getActivity().getSharedPreferences(GAME_PREFERENCE, Context.MODE_PRIVATE);
                rootView = inflater.inflate(R.layout.color_game, container, false);
                final Typeface game_font = Typeface.DEFAULT;
                gameScore = (TextView) rootView.findViewById(R.id.textViewGameScore);
                currentScore = (TextView) rootView.findViewById(R.id.textViewCurrentScore);
                gameAdapter = new ImageCardAdapter((AppCompatActivity) getActivity());
                gameScoreCount = preferences.getLong("CurrentGameScore", 0);
                gameAdapter.populateCards(false);
                gameAdapter.setGameEvents(this);
                currentScore.setVisibility(View.INVISIBLE);
                gameScore.setVisibility(View.INVISIBLE);
                gameScore.setTypeface(game_font);
                currentScore.setTypeface(game_font);
                gameScore.setText("SCORE: " + gameScoreCount);
                initGameField(rootView);
                initGameButtons(rootView);
            }

            ((MainActivity)getActivity()).rotateScreenPermission(false);
            return rootView;
        }

        protected void initGameField(View root_view) {
            final GridView cardsView = (GridView) root_view.findViewById(R.id.gridViewCards);

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    cardsView.setAdapter(gameAdapter);
                }
            }, INTRO_DURATION);


            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    gameScore.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.DropOut)
                            .duration(GENERAL_DURATION)
                            .playOn(gameScore);
                }
            }, LONG_DURATION);
        }

        protected void initGameButtons(View root_view) {
            final ImageButton high_score = (ImageButton) root_view.findViewById(R.id.imageButtonHighScore);
            final ImageButton restart = (ImageButton) root_view.findViewById(R.id.imageButtonRestart);
            final ImageButton quit = (ImageButton) root_view.findViewById(R.id.imageButtonQuit);

            high_score.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.Pulse)
                            .duration(INTRO_DURATION)
                            .playOn(v);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final MainActivity parent = (MainActivity)getActivity();
                            if(parent != null && parent.canSwitchView) {
                                parent.canSwitchView = false;
                                parent.flipScreen();
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        parent.canSwitchView = true;
                                    }
                                }, INTRO_DURATION);
                            }
                        }
                    }, INTRO_DURATION);
                }
            });

            restart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    YoYo.with(Techniques.Pulse)
                            .duration(INTRO_DURATION)
                            .playOn(v);
                    if(openDialog)
                        return;
                    openDialog = true;
                    (new Handler()).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        DialogMessageBox.Ask(getActivity(), "Restart the game", "Are you sure that you want to restart the game?", R.drawable.ic_restart,
                                                                "Yes", "No", new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        openDialog = false;
                                                                        resetGame();
                                                                    }
                                                                }, new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        openDialog = false;
                                                                    }
                                                                });
                                                    }
                                                }
                            , INTRO_DURATION);
                }
            });

            quit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.Pulse)
                            .duration(INTRO_DURATION)
                            .playOn(v);
                    if(openDialog)
                        return;
                    openDialog = true;
                    (new Handler()).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        DialogMessageBox.Ask(getActivity(), "Quit the game", "Do you want to quit the game?", R.drawable.ic_power,
                                                                "Yes", "No", new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        gameAdapter.populateCards(true);
                                                                        gameScoreCount = 0;
                                                                        saveScore();
                                                                        getActivity().finish();
                                                                        System.exit(0);
                                                                    }
                                                                }, new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        openDialog = false;
                                                                    }
                                                                });
                                                    }
                                                }
                            , INTRO_DURATION);
                }
            });
        }

        @Override
        public void gameOver() {
            gameOver = true;
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    showGameOverDialog(new GameOverResult() {
                        @Override
                        public boolean onResult(final String username) {
                            final RealmConfiguration realmConfig = new RealmConfiguration.Builder(getActivity()).build();
                            final Realm db = Realm.getInstance(realmConfig);
                            GameHighScore score = db.where(GameHighScore.class).equalTo("name", username).findFirst();
                            if (score == null) {
                                db.beginTransaction();
                                score = db.createObject(GameHighScore.class);
                                score.setName(username);
                                score.setScore(gameScoreCount);
                                score.setRank(GameHighScore.getRank(gameScoreCount));
                                db.commitTransaction();
                            } else
                                return false;
                            resetGame();
                            ((MainActivity) getActivity()).flipScreen();
                            return true;
                        }
                    });
                }
            }, INTRO_DURATION);
        }

        @Override
        public void onDestroy() {
            if(rootView != null && rootView.getParent() != null) {
                ((ViewGroup)rootView.getParent()).removeView(rootView);
            }
            super.onDestroy();
        }

        @Override
        public void onStart() {
            super.onStart();
            if(!gameOver && gameAdapter.gameEnded())
                gameOver();
        }

        @Override
        public void onPause() {
            gameAdapter.saveState();
            saveScore();
            super.onPause();
        }

        @Override
        public void result(final boolean matched) {
            currentScore.setVisibility(View.VISIBLE);
            if (matched) {
                currentScore.setText(R.string.plus2);
                gameScoreCount += 2;
            } else {
                currentScore.setText(R.string.minus1);
                gameScoreCount--;
            }

            YoYo.with(Techniques.FadeOutUp)
                    .duration(LONG_DURATION)
                    .playOn(currentScore);

            (new Handler()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    gameScore.setText("SCORE: " + gameScoreCount);
                    if (matched)
                        YoYo.with(Techniques.Tada)
                                .duration(GENERAL_DURATION)
                                .playOn(gameScore);
                    else
                        YoYo.with(Techniques.Flash)
                                .duration(GENERAL_DURATION)
                                .playOn(gameScore);
                }
            }, GENERAL_DURATION);

        }

        public void saveScore() {
            SharedPreferences preferences = getActivity().getSharedPreferences(GAME_PREFERENCE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("CurrentGameScore", gameScoreCount);
            editor.commit();
        }

        public void resetGame() {
            gameAdapter.populateCards(true);
            gameAdapter.notifyDataSetChanged();
            gameScoreCount = 0;
            gameScore.setText(R.string.score_0);
        }

        public void showGameOverDialog(final GameOverResult result) {

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float padding = ((float)size.x/100f)*6f;

            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.game_over);
            alert.setMessage("The game score is " + gameScoreCount + ". Please enter your username");
            final EditText username = new EditText(getActivity());
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(25);
            username.setFilters(filters);
            username.setFocusable(true);
            username.setHint(R.string.Username);
            username.setSingleLine();
            FrameLayout container = new FrameLayout(getActivity());
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(Math.round(padding), Math.round(padding), Math.round(padding), Math.round(padding));
            username.setLayoutParams(params);
            container.addView(username);
            username.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    username.setError(null);
                }
            });
            alert.setView(container);
            alert.setPositiveButton("Ok", null);
            alert.setNegativeButton(null, null);
            alert.setCancelable(false);
            final AlertDialog dialog = alert.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dlg) {

                    Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // TODO Do something
                            String input = username.getText().toString().trim();
                            if (input.length() == 0) {
                                username.setError("Please specify a username!");
                            }
                            else {
                                gameOver = false;
                                if (result != null){
                                    if(result.onResult(input))
                                        dialog.dismiss();
                                    else
                                        username.setError("This username already exist. Try another name.");
                                }
                                else
                                    dialog.dismiss();

                            }
                        }
                    });
                }
            });
            dialog.show();
        }
    }


    /**
     * A fragment representing the back of the Image card.
     */
    public static class HighScoreFragment extends Fragment {

        private View rootView;
        private HighScoreAdapter highscoreTableAdapter;

        public HighScoreFragment() {
            rootView = null;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            if(rootView == null) {
                rootView = inflater.inflate(R.layout.high_score_table, container, false);
                final Typeface game_font = Typeface.DEFAULT;
                final TextView header = (TextView) rootView.findViewById(R.id.textViewHeaderHighScore);
                final TextView message =  (TextView) rootView.findViewById(R.id.textViewEmtyTable);
                final TableFixHeaders highscore_table = (TableFixHeaders) rootView.findViewById(R.id.tableHighScore);
                highscoreTableAdapter = new HighScoreAdapter((AppCompatActivity)getActivity());
                if(highscoreTableAdapter.populateScore())
                    message.setVisibility(View.GONE);
                else
                    highscore_table.setVisibility(View.INVISIBLE);
                highscore_table.setAdapter(highscoreTableAdapter);
                header.setTypeface(game_font);
                message.setTypeface(game_font);
                initGameButtons(rootView);
            }
            ((MainActivity)getActivity()).rotateScreenPermission(true);
            return rootView;
        }

        @Override
        public void onDestroy() {
            if(rootView != null && rootView.getParent() != null) {
                ((ViewGroup)rootView.getParent()).removeView(rootView);
            }
            super.onDestroy();
        }

        protected void initGameButtons(View root_view) {
            final ImageButton home = (ImageButton)root_view.findViewById(R.id.imageButtonHome);
            final ImageButton close = (ImageButton)root_view.findViewById(R.id.imageButtonClose);

            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.Pulse)
                            .duration(INTRO_DURATION)
                            .playOn(v);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final MainActivity parent = (MainActivity)getActivity();
                            if(parent != null && parent.canSwitchView) {
                                parent.canSwitchView = false;
                                parent.flipScreen();
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        parent.canSwitchView = true;
                                    }
                                }, INTRO_DURATION);
                            }
                        }
                    }, INTRO_DURATION);
                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.Pulse)
                            .duration(INTRO_DURATION)
                            .playOn(v);

                    (new Handler()).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        DialogMessageBox.Ask(getActivity(), "Quit the game", "Do you want to quit the game?", R.drawable.ic_power,
                                                                "Yes", "No", new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        getActivity().finish();
                                                                        System.exit(0);
                                                                    }
                                                                }, null);
                                                    }
                                                }
                            , INTRO_DURATION);
                }
            });
        }
    }
}

