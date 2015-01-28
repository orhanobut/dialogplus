[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-dialogplus-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1413)

DialogPlus
==========

Simple, easy dialog for android. Instead of using dialog/fragments, normal view will be shown as dialog. It's
customizable and have 3 different content holder.

<img src='https://github.com/nr4bt/dialogplus/blob/master/images/s1.png' width='140' height='180'/>
<img src='https://github.com/nr4bt/dialogplus/blob/master/images/s2.png' width='140' height='180'/>
<img src='https://github.com/nr4bt/dialogplus/blob/master/images/s3.png' width='140' height='180'/>

###Gradle
```groovy
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/"}
}
dependencies {
    compile 'com.orhanobut:dialogplus:1.1-SNAPSHOT@aar'
}
```

###Usage
Use the builder to create the dialog.

Basic default usage
```java
ArrayAdapter<String> adapter = new ArrayAdapter<>(                                            
        this, R.layout.simple_list_item_1, new String[]{"Item 1", "Item 2","Item 3","Item 4"} 
);                                                                                            
DialogPlus dialog = new DialogPlus.Builder(this)                                            
    .setHolder(new ListHolder())    // Optional, default:BasicHolder                    
    .setHeader(R.layout.header)     // Optional                                           
    .setFooter(R.layout.footer)     // Optional                                           
    .setCancelable(true)            // Optional default:true                              
    .setGravity(Gravity.BOTTOM)     // Optional default:true                              
    .setAdapter(adapter)            // This must be called, Any adapter can be set.                              
    .setOnItemClickListener(new OnItemClickListener() {                       
        @Override                                                                         
        public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
            //todo                                                                             
        }                                                                                 
    })                                                                                    
    .create();                                                                            
dialog.show();
```

###Extras
You can also select different holder for the dialog.

- Use ListView as content holder
```java
setHolder(new ListHolder())
```

- Use BasicHolder as content holder, BasicHolder is basically a listview mockup implementation by using linearlayout, there is no scrollable feature in this view and this should be used if you have only a few items.
```java
setHolder(new BasicHolder())
```

- Use GridHolder if you want to use GridView for the dialog. You must set column number.
```java
setHolder(new GridHolder(COLUMN_NUMBER))
```


#### TODO 
Check wiki

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
