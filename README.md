dialogplus
==========

###Introduction
Simple,easy dialog for android. Instead of using dialog/fragments, normal view will be shown as dialog. It's customizable and have 3 different content holder.

###Add the dependency
<pre>
dependencies {
    compile 'com.github.nr4bt:dialogplus:1.0.0-SNAPSHOT@aar'
}
</pre>

###Usage
Use the builder to create the dialog.

Basic default usage
<pre>
ArrayAdapter<String> adapter = new ArrayAdapter<>(                                            
        this, R.layout.simple_list_item_1, new String[]{"Item 1", "Item 2","Item 3","Item 4"} 
);                                                                                            
final DialogPlus dialog = new DialogPlus.Builder(this)                                            
        .setHolder(new ListHolder())    // Optional, default:BasicHolder                    
        .setHeader(R.layout.header)     // Optional                                           
        .setFooter(R.layout.footer)     // Optional                                           
        .setCancelable(true)            // Optional default:true                              
        .setGravity(Gravity.BOTTOM)     // Optional default:true                              
        .setAdapter(adapter)            // This must be added                                 
        .setOnItemClickListener(new AdapterView.OnItemClickListener() {                       
            @Override                                                                         
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 //todo                                                                             
            }                                                                                 
        })                                                                                    
        .create();                                                                            
dialog.show();                                                                                                              </pre>

You can also select different holder for the dialog.
<ul>
<li>
</li>
<ul>
