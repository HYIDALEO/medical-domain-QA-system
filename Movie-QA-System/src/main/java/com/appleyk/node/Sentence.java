package com.appleyk.node;

import java.util.List;

import com.appleyk.InitElements.QAWordSegmenter;


public class Sentence {
    public String text = null;
    public List<String> words = null;
    public boolean[] isMatch;

    public Sentence() {
    }

    public Sentence(String text, List<String> words) {
        this.text = text;
        this.words = words;
    }

    public void fill() {
        if (text == null && words != null) {
            StringBuilder sb = new StringBuilder();
            words.forEach(sb::append);
            text = sb.toString();
        }
    }

    public Sentence(String text) {
        this.text = text;
        this.words = QAWordSegmenter.getInstance().segment(text, true, true);
    }

    public Sentence(List<String> words) {
        this.words = words;
    }

    public String getText() {
        return text;
    }

    public List<String> getWords() {
        return words;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
}
