package com.example.android.letsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ModulEditActivity extends AppCompatActivity {

    Modul currentModul;
    List<ModulElement> modulElements = new ArrayList<ModulElement>();
    List<Element> addElements;
    ImageButton mAddNewElement;
    EditText mTitleView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modul_edit);



        mTitleView = findViewById(R.id.et_modul_edit_title);
        mAddNewElement = findViewById(R.id.ib_modul_edit_add_element);
        fab = findViewById(R.id.fab_modul_edit);

        Intent receivedIntent = getIntent();
        if(receivedIntent != null){
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Element>>(){}.getType();
            addElements = gson.fromJson(receivedIntent.getStringExtra("selectedElements"), listType);
            Log.e("addElements: ", "" + addElements);
            if(addElements != null){
                modulElements = generateModulElementsFromElements(addElements);
            }

        }

        addOnClickListeners();

    }

    public List<ModulElement> generateModulElementsFromElements(List<Element> elements){
        for(Element element: addElements){
            // TODO Not sure if I can set other varaible when only using this simple Constructor
            ModulElement newModulElement = new ModulElement(element);
            modulElements.add(newModulElement);
        }
        return modulElements;
    }



    

    public void addOnClickListeners(){
        mAddNewElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(ModulEditActivity.this, ElementListActivity.class);

                if(currentModul != null){

                }else{
                    if(mTitleView.getText() != null){
                        //TODO Muss man hier wirklich das ganze Modul verschicken?
                        currentModul = new Modul(mTitleView.getText().toString(), modulElements);
                        addIntent.putExtra("newModul", currentModul);
                        startActivity(addIntent);

                    }else{
                        Toast.makeText(ModulEditActivity.this, "You have to define a Title first", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Save Modul to Database
            }
        });
    }
}
