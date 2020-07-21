package jakubw.pracainz.goalsexecutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class EditDoesActivity extends AppCompatActivity {

    EditText editDate;
    EditText editTitle;
    Button editTaskBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_does);

        editDate = findViewById(R.id.editDate);
        editTitle = findViewById(R.id.editTitle);
        editTaskBtn = findViewById(R.id.editTaskBtn);

        Intent intent = getIntent();
        editTitle.setText(intent.getStringExtra("title"));
    }
}
