package comkyli.dynamicalflowview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.kyli.dynamicalflowview.DynamicalFlowView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DynamicalFlowView dynamicalFlowView = findViewById(R.id.label);
        List<String>  strings=new ArrayList<>();
        strings.add("hellow");
        strings.add("world");
        strings.add("world");      strings.add("world");
        strings.add("world");
        strings.add("world");
        strings.add("world");

        dynamicalFlowView.setLabelData(strings);
    }
}