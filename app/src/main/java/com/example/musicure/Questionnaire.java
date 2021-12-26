package com.example.musicure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class Questionnaire extends AppCompatActivity {

    private ArrayList<Question> questionlist;
    Random random;
    private TextView question;
    private Button option1, option2, option3, option4;
    int currentscore = 0, currentpos, questionattempted = 1;
    int randomNumber = (int) (Math.random()*(10-1)) + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        getWindow().setStatusBarColor(ContextCompat.getColor(Questionnaire.this,R.color.red));
        getWindow().setNavigationBarColor(ContextCompat.getColor(Questionnaire.this,R.color.darkred));



        question = findViewById(R.id.question);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        questionlist = new ArrayList<>();
        random = new Random();
        getQuizQuestion(questionlist);
        currentpos = random.nextInt(questionlist.size());
        setDataToViews(randomNumber);

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentscore += 1;
                questionattempted++;
                randomNumber++;
                if(randomNumber>=10)
                {
                    randomNumber=0;
                }
                currentpos = random.nextInt(questionlist.size());
                setDataToViews(randomNumber);
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentscore += 2;
                questionattempted++;
                randomNumber++;
                if(randomNumber>=10)
                {
                    randomNumber=0;
                }
                currentpos = random.nextInt(questionlist.size());
                setDataToViews(randomNumber);
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentscore += 3;
                questionattempted++;
                currentpos = random.nextInt(questionlist.size());
                randomNumber++;
                if(randomNumber>=10)
                {
                    randomNumber=0;
                }
                setDataToViews(randomNumber);
            }
        });
        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentscore += 4;
                questionattempted++;
                currentpos = random.nextInt(questionlist.size());
                randomNumber++;
                if(randomNumber>=10)
                {
                    randomNumber=0;
                }
                setDataToViews(randomNumber);
            }
        });

    }

    private void setDataToViews(int currentpos) {
        if (questionattempted > 10) {
            showmusicplayer();
        }
        question.setText(questionlist.get(currentpos).getQuestion());
        option1.setText(questionlist.get(currentpos).getOption1());
        option2.setText(questionlist.get(currentpos).getOption2());
        option3.setText(questionlist.get(currentpos).getOption3());
        option4.setText(questionlist.get(currentpos).getOption4());


    }

    private void showmusicplayer() {

        if (currentscore > 30) {
            startActivity(new Intent(getApplicationContext(), Music_player.class).putExtra("raga", "Hypertension"));
        } else if (currentscore > 20) {
            startActivity(new Intent(getApplicationContext(), Music_player.class).putExtra("raga", "Anxiety"));
        } else if (currentscore > 10) {
            startActivity(new Intent(getApplicationContext(), Music_player.class).putExtra("raga", "Headache"));
        } else {
            startActivity(new Intent(getApplicationContext(), Music_player.class).putExtra("raga", "Insomnia"));
        }

    }

    private void getQuizQuestion(ArrayList<Question> questionlist) {
        questionlist.add(new Question("How often have you been feeling anxious or nervous in the past few days ?", "Not at all", "Several times", "More than half the days", "Almost every day"));
        questionlist.add(new Question("How often have you been feeling Stressed out in the past few days ?", "Not at all", "Several times", "More than half the days", "Almost every day"));
        questionlist.add(new Question("How often have you been feeling drained in the past few days ?", "Not at all", "Several times", "More than half the days", "Almost every day"));
        questionlist.add(new Question("How often have you had troubles falling asleep in the past few days ?", "Not at all", "Several times", "More than half the days", "Almost every day"));
        questionlist.add(new Question("How often have you been bothered by worrying too much about different things in the past few days ?", "Not at all", "Several times", "More than half the days", "Almost every day"));
        questionlist.add(new Question("How often have you been feeling restless and agitated in the past few days ?", "Not at all", "Several times", "More than half the days", "Almost every day"));
        questionlist.add(new Question("How often have you been annoyed or irritated by the silliest things in the past few days ?", "Not at all", "Several times", "More than half the days", "Almost every day"));
        questionlist.add(new Question("How often have you had trouble concentrating at work/studies in the past few days ?", "Not at all", "Several times", "More than half the days", "Almost every day"));
        questionlist.add(new Question("How often have you been feeling tensed by workload in the past few days ?", "Not at all", "Several times", "More than half the days", "Almost every day"));
        questionlist.add(new Question("How often have you been feeling alienated in the past few days ?", "Not at all", "Several times", "More than half the days", "Almost every day"));
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please complete the test !", Toast.LENGTH_SHORT).show();
    }
}