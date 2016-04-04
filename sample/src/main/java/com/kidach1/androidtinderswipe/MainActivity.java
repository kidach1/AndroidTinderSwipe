package com.kidach1.androidtinderswipe;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kidach1.tinderswipe.model.CardModel;
import com.kidach1.tinderswipe.view.CardContainer;
import com.kidach1.tinderswipe.view.SimpleCardStackAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private List<String> imgs = Arrays.asList(
            "http://img.peco-japan.com/image/93127",
            "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcRmZfrA31MKjExHzG83ycVshteNDg5hUAoGZ30HzTu9so_PjXnftQ",
            "https://pbs.twimg.com/profile_images/3129300560/9c13c196eaa4f1940641f2cf08878727.jpeg",
            "https://pbs.twimg.com/profile_images/581025665727655936/9CnwZZ6j.jpg"
    );

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleCardStackAdapter cardAdapter = new SimpleCardStackAdapter(this);

        for (int i = 0; i < 8; i++) {
            CardModel cardModel = new CardModel("TinderSwipe", "Description for card.", imgs.get(i%4));
            addClickListener(cardModel);
            addDissmissListener(cardModel);
            cardAdapter.add(cardModel);
        }

        CardContainer cardContainer = (CardContainer) findViewById(R.id.cardContainer);
        cardContainer.setAdapter(cardAdapter);

        addSwipeListener(cardContainer);
    }

    private void addClickListener(CardModel cardModel) {
        cardModel.setOnClickListener(new CardModel.OnClickListener() {
            @Override
            public void OnClickListener() {
                Log.i(TAG, "touch the card");
            }
        });
    }

    private void addDissmissListener(CardModel cardModel) {
        cardModel.setOnCardDismissedListener(new CardModel.OnCardDismissedListener() {
            @Override
            public void onLike(final CardContainer.OnLikeListener callback) {
                Log.i(TAG, "I like the card");
                openDialog(callback);
            }

            @Override
            public void onDislike() {
                Log.i(TAG, "I dislike the card");
            }
        });
    }

    private void openDialog(final CardContainer.OnLikeListener callback) {
        new MaterialDialog.Builder(MainActivity.this)
                .title(R.string.dialog_title)
                .content(R.string.dialog_content)
                .positiveText(R.string.dialog_positive_text)
                .negativeText(R.string.dialog_negative_text)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.i(TAG, "I choose positive.");
                        callback.choose();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.i(TAG, "I choose negative.");
                        callback.unchoose();
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.i(TAG, "cancel.");
                        callback.unchoose();
                    }
                })
                .show();

    }

    private void addSwipeListener(final CardContainer cardContainer) {
        cardContainer.setOnSwipeListener(new CardContainer.onSwipeListener() {
            @Override
            public void onSwipe(float scrollProgressPercent) {
                View view = cardContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator)
                        .setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator)
                        .setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);

            }
        });
    }
}
