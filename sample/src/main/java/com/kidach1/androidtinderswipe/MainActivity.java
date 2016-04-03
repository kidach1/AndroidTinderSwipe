package com.kidach1.androidtinderswipe;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.kidach1.tinderswipe.model.CardModel;
import com.kidach1.tinderswipe.view.CardContainer;
import com.kidach1.tinderswipe.view.SimpleCardStackAdapter;

public class MainActivity extends AppCompatActivity {

    private CardContainer mCardContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mainlayout);

        mCardContainer = (CardContainer) findViewById(R.id.layoutview);
        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);

        adapter.add(new CardModel("Title1", "Description goes here", "http://img.peco-japan.com/image/93127"));
        adapter.add(new CardModel("Title2", "Description goes here", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcRmZfrA31MKjExHzG83ycVshteNDg5hUAoGZ30HzTu9so_PjXnftQ"));
        adapter.add(new CardModel("Title3", "Description goes here", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTe8kAEiKraXeXsdtVi6WnfO2dl43niQ17JUChhtrezZUFFgRoYmQ"));
        adapter.add(new CardModel("Title1", "Description goes here", "http://img.peco-japan.com/image/93127"));
        adapter.add(new CardModel("Title2", "Description goes here", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcRmZfrA31MKjExHzG83ycVshteNDg5hUAoGZ30HzTu9so_PjXnftQ"));
        adapter.add(new CardModel("Title3", "Description goes here", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTe8kAEiKraXeXsdtVi6WnfO2dl43niQ17JUChhtrezZUFFgRoYmQ"));
        adapter.add(new CardModel("Title1", "Description goes here", "http://img.peco-japan.com/image/93127"));
        adapter.add(new CardModel("Title2", "Description goes here", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcRmZfrA31MKjExHzG83ycVshteNDg5hUAoGZ30HzTu9so_PjXnftQ"));
        adapter.add(new CardModel("Title3", "Description goes here", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTe8kAEiKraXeXsdtVi6WnfO2dl43niQ17JUChhtrezZUFFgRoYmQ"));

        CardModel cardModel = new CardModel("Title3", "Description goes here", "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTe8kAEiKraXeXsdtVi6WnfO2dl43niQ17JUChhtrezZUFFgRoYmQ");
        cardModel.setOnClickListener(new CardModel.OnClickListener() {
            @Override
            public void OnClickListener() {
                Log.i("Swipeable Cards", "I am pressing the card");
            }
        });

        cardModel.setOnCardDismissedListener(new CardModel.OnCardDismissedListener() {
            @Override
            public void onLike(final CardContainer.OnLikeListener cb) {
                Log.i("Swipeable Cards", "dissmiss the card");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("title")
                        .setMessage("message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cb.choose();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cb.unchoose();
                            }
                        })
                        .show();
            }

            @Override
            public void onDislike() {
                Log.i("Swipeable Cards", "I dislike the card");
            }
        });

        adapter.add(cardModel);

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
