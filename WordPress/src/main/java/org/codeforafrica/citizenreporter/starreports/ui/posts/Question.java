package org.codeforafrica.citizenreporter.starreports.ui.posts;

/**
 * Created by john on 7/13/2015.
 */
public class Question {

    public String title="";
    public String answer="";

    public Question(String title, String answer) {
        super();
        this.title = title;
        this.answer = answer;
    }

    public void setAnswer(String _answer){
        this.answer = _answer;
    }
}
