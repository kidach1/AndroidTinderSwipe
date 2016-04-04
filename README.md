# AndroidTinderSwipe

<img src="/AndroidTinderSwipe1.gif" width="35%"> <img src="/AndroidTinderSwipe2.gif" width="35%">


## Download

```build.gradle
dependencies {
    compile 'com.kidach1:AndroidTinderSwipe:1.0.7'
}
```


## Usage

#### Set xml for card container

```xml
<com.kidach1.tinderswipe.view.CardContainer
    android:id="@+id/cardContainer"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1"/>
```

#### Init CardModel

```java
CardModel cardModel = new CardModel("TinderSwipe", "Description for card.", "http://example.com/example.png"); // title, desc, imgUrl ※
```

※ Extend CardModel for more fields if you want.

#### Init CardStackAdapter and add model

```java
SimpleCardStackAdapter cardAdapter = new SimpleCardStackAdapter(this);
cardAdapter.add(cardModel);
```

#### Init CardContainer and set adapter

```java
CardContainer cardContainer = (CardContainer) findViewById(R.id.cardContainer);
cardContainer.setAdapter(cardAdapter);
```

## Custom

#### Add SwipeListener to Container

```java
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
```

#### Add DissmissListener to CardModel

```java
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
                    Log.i(TAG, "cancel");
                    callback.unchoose();
                }
            })
            .show();
}
```



## Thanks

Inspired by `Swipeable-Cards` in [kikoso](https://github.com/kikoso).

License
-------

    Copyright 2015 kidach1

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.