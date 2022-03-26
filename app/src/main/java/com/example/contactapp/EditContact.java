package com.example.contactapp;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contactapp.databinding.ActivityEditContactBinding;


public class EditContact extends AppCompatActivity {
    private ActivityEditContactBinding binding;
    private static final int NEW_EDIT_CONTACT_ACTIVITY_REQUEST_CODE = 1;
    private boolean isEditMode = false;
    private Contact contact;
    private AppDatabase appDatabase;
    private ContactDAO contactDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditContactBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);

        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("is_edit_mode", false);

        appDatabase = AppDatabase.getInstance(this);
        contactDao = appDatabase.contactDao();

        ActionBar actionBar = getSupportActionBar();

        if (isEditMode) {
            actionBar.setTitle("Edit contact");

            Bundle bundle = getIntent().getExtras();
            if (bundle == null) {
                return;
            }
            contact = (Contact) bundle.get("object_contact");


            binding.etFirstName.setText(contact.getName().split(" ")[0]);
            binding.etLastName.setText(contact.getName().split(" ")[1]);
            binding.etMobile.setText(contact.getMobile());
            binding.etEmail.setText(contact.getEmail());

        } else {
            actionBar.setTitle("Create new contact");
        }

        Drawable drawable = getResources().getDrawable(R.drawable.ic_baseline_close_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(drawable);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_contact_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.btn_save:
                onClickBtnSave();
            default:break;
        }
        return true;
    }
    private void onClickBtnSave() {
        Intent intent = new Intent();
        String name = binding.etFirstName.getText().toString() + " " + binding.etLastName.getText().toString();
        String mobile = binding.etMobile.getText().toString();
        String email = binding.etEmail.getText().toString();

        if (isEditMode) {
            Contact c = new Contact(contact.getId(), name, mobile, email);
            contactDao.updateContact(c);
            Bundle bundle = new Bundle();
            bundle.putSerializable("edited_contact", c);
            intent.putExtras(bundle);
        } else {
            Contact c = new Contact( name, mobile, email);
            contactDao.insertAll(c);
        }

        setResult(NEW_EDIT_CONTACT_ACTIVITY_REQUEST_CODE, intent);
        finish();
    }
}