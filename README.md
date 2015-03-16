[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-dialogplus-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1413)    [![API](https://img.shields.io/badge/API-10%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=10) [![Join the chat at https://gitter.im/orhanobut/dialogplus](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/orhanobut/dialogplus?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

DialogPlus
==========
Simple, easy dialog solution for android.

<img src='https://github.com/orhanobut/dialogplus/blob/master/images/dialog-plus.png' width='128' height='128'/>
<hr>

<img src='https://github.com/nr4bt/dialogplus/blob/master/images/d1.png' width='140' height='200'/>
<img src='https://github.com/nr4bt/dialogplus/blob/master/images/d2.png' width='140' height='200'/>
<img src='https://github.com/nr4bt/dialogplus/blob/master/images/d3.png' width='140' height='200'/>
<img src='https://github.com/nr4bt/dialogplus/blob/master/images/d4.png' width='140' height='200'/>
<img src='https://github.com/nr4bt/dialogplus/blob/master/images/d5.png' width='140' height='200'/>
<img src='https://github.com/nr4bt/dialogplus/blob/master/images/d6.png' width='140' height='200'/>
<img src='https://github.com/nr4bt/dialogplus/blob/master/images/d7.png' width='140' height='200'/>
<img src='https://github.com/nr4bt/dialogplus/blob/master/images/d8.png' width='140' height='200'/>
<img src='https://github.com/nr4bt/dialogplus/blob/master/images/d9.png' width='140' height='200'/>
<img src='https://github.com/nr4bt/dialogplus/blob/master/images/d10.png' width='140' height='200'/>

##### DialogPlus provides 3 types:
- Top : Dialog will appear at top with animation
- Center : Dialog will appear in the center with animation
- Bottom : Dialog will appear at the bottom of the screen with animation

##### DialogPlus provides 3 content types:
- ListHolder : Items will be shown in a listview
- GridHolder : Items will be shown in a gridview
- ViewHolder : Your customized view will be shown in the content

### Gradle
```groovy
compile 'com.orhanobut:dialogplus:1.3@aar'
```

### Usage
Use the builder to create the dialog.

Basic usage
```java
DialogPlus dialog = new DialogPlus.Builder(this)
        .setAdapter(adapter)
        .setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
            }
        })
        .create();
dialog.show();
```

### More options
You can also select different holder for the dialog.

- Use ListView as content holder, note that this is default content type.
```java
setContentHolder(new ListHolder())
```
- Use ViewHolder as content holder if you want to use a custom view for your dialog. Pass resource id
```java
.setContentHolder(new ViewHolder(R.layout.content))
```
or pass view itself
```java
.setContentHolder(new ViewHolder(view))
```
- Use GridHolder if you want to use GridView for the dialog. You must set column number.
```java
.setContentHolder(new GridHolder(COLUMN_NUMBER))
```
- Set dialog position. BOTTOM (default), TOP or CENTER
```java
.setGravity(DialogPlus.Gravity.CENTER)
```
- Define if the dialog is cancelable and should be closed when back pressed or click outside is pressed
```java
.setCancelable(true)
```
- Set Adapter, this adapter will be used to fill the content for ListHolder and GridHolder. This is required if the content holder is ListHolder or GridHolder. It is not required if the content holder is ViewHolder.
```java
.setAdapter(adapter);
```
- Set an item click listener when list or grid holder is chosen. In that way you can have callbacks when one of your items is clicked
```java
.setOnItemClickListener(new OnItemClickListener() {
    @Override
    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

    }
})
```
- Set a global click listener to you dialog in order to handle all the possible click events. You can then identify the view by using its id and handle the correct behaviour Only views which has id will trigger this event.
```java
.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(DialogPlus dialog, View view) {

    }
})
```
- Add margins to your dialog. They are set to 0 except when gravity is center. In that case basic margins are applied
```java
.setMargins(left, top, right, bottom)
```
- Set the footer view using the id of the layout resource
```java
.setFooter(R.layout.footer)
```
or use view
```java
.setFooter(view)
```
- Set the header view using the id of the layout resource
```java
.setHeader(R.layout.header)
```
or use view
```java
.setHeader(view)
```
- Set animation resources
```java
.setInAnimation(R.anim.abc_fade_in)
.setOutAnimation(R.anim.abc_fade_out)
```
- Set screen type to either fill the screen or only half
```java
.setScreenType(DialogPlus.ScreenType.FULL)
```

#### You might also like
- [Hawk](https://github.com/orhanobut/hawk) Secure simple key-value storage
- [Wasp](https://github.com/orhanobut/wasp) All-in-one network solution
- [Bee](https://github.com/orhanobut/bee) QA/Debug tool
- [SimpleListView](https://github.com/orhanobut/simplelistview) Simple basic listview implementation with linearlayout

#### License
<pre>
Copyright 2014 Orhan Obut

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
