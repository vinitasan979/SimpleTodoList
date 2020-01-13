package com.example.todolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT="item text";
    public static final String KEY_ITEM_POSITION="item position";
    public static final int EDIT_TEXT_CODE= 20;

    List<String> items;
    Button btnAdd;
    EditText etItems;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAdd= findViewById(R.id.btnAdd);
        etItems=findViewById(R.id.etItems);
        rvItems=findViewById(R.id.rvItems);

        loadItems();


        ItemsAdapter.OnLongClickListener onLongClickListener=new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                //delete item
                items.remove(position);
                //notify adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"Item was removed ",Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {

                Log.d("MainActivity","Single Click at position "+position);
                //create new activity
                Intent i= new Intent(MainActivity.this,EditActivity.class);
                //pass edited data
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                //display the modified activity
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };
        itemsAdapter=new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //etItems.setText("works fine");
                String todoItem=etItems.getText().toString();
                items.add(todoItem);
                itemsAdapter.notifyItemInserted(items.size()-1);
                etItems.setText("");
                Toast.makeText(getApplicationContext(),"Item was Added",Toast.LENGTH_SHORT).show();
                saveItems();


            }
        });

    }
    //handle results of activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK && requestCode==EDIT_TEXT_CODE){
            //get updated text
            String itemText=data.getStringExtra(KEY_ITEM_TEXT);

            //get original position of edited item
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //update model
            items.set(position,itemText);
            // notifiy the adapter
            itemsAdapter.notifyItemChanged(position);
            //save changes
            saveItems();
            Toast.makeText(getApplicationContext(),"Item Updated Successfully ",Toast.LENGTH_SHORT).show();



        }
        else{
            Log.w("Main Activity","Unknown call to onActivityResult");
        }

    }

    private File getDataFile(){
        return new File(getFilesDir(),"data.txt");
    }

    private void loadItems(){
        try {
            items= new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity","Error reading items");
            items=new ArrayList<>();

        }

    }
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity","Error reading items");
        }

    }
}
