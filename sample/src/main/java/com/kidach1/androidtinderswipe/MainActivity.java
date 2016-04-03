package com.kidach1.androidtinderswipe;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.kidach1.tinderswipe.model.CardModel;
import com.kidach1.tinderswipe.view.CardContainer;
import com.kidach1.tinderswipe.view.SimpleCardStackAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private CardContainer mCardContainer;
    private List<String> imgs = Arrays.asList(
            "http://img.peco-japan.com/image/93127",
            "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcRmZfrA31MKjExHzG83ycVshteNDg5hUAoGZ30HzTu9so_PjXnftQ",
            "https://pbs.twimg.com/profile_images/3129300560/9c13c196eaa4f1940641f2cf08878727.jpeg",
            "https://pbs.twimg.com/profile_images/581025665727655936/9CnwZZ6j.jpg"
    );

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCardContainer = (CardContainer) findViewById(R.id.layoutview);
        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);

        for (int i = 0; i < 8; i++) {
            CardModel cardModel = new CardModel("TinderSwipe", "Description for card.", imgs.get(i%4));
            cardModel.setOnClickListener(new CardModel.OnClickListener() {
                @Override
                public void OnClickListener() {
                    Log.i(TAG, "touch the card");
                }
            });

            cardModel.setOnCardDismissedListener(new CardModel.OnCardDismissedListener() {
                @Override
                public void onLike(final CardContainer.OnLikeListener cb) {
                    Log.i(TAG, "I like the card");
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("title")
                            .setMessage("message")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "I choose positive.");
                                    cb.choose();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "I choose negative.");
                                    cb.unchoose();
                                }
                            })
                            .show();
                }

                @Override
                public void onDislike() {
                    Log.i(TAG, "I dislike the card");
                }
            });

            adapter.add(cardModel);
        }

        mCardContainer.setAdapter(adapter);
        mCardContainer.setOnSwipeListener(new CardContainer.onSwipeListener() {
            @Override
            public void onSwipe(float scrollProgressPercent) {
                View view = mCardContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator)
                        .setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator)
                        .setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);

            }
        });
    }
}
