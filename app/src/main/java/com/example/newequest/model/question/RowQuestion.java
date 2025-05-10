package com.example.newequest.model.question;

import com.example.newequest.model.Dependency;

import java.util.ArrayList;

public class RowQuestion extends Question {
    protected ArrayList<Question> questions;

    public RowQuestion() {
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    private Question getQuestionById(String id) {
        for (Question question : getQuestions()) {
            if (question.getId().equals(id)) {
                return question;
            }
        }
        return null;
    }

    public Question getNextQuestionWithLastQuestion(Question lastQuestion) {
        if (lastQuestion == null) {
            return getQuestions().get(0);
        }
        int index = getQuestions().indexOf(lastQuestion);
        try {
            Question nextPossibleQuestion = getQuestions().get(index + 1);
            if (nextPossibleQuestion.getDependencies() != null) {
                for (Dependency dependency : nextPossibleQuestion.getDependencies()) {
                    Question dependentQuestion = getQuestionById(dependency.getDependencyID());
                    AnswerableQuestion answerableQuestion = (AnswerableQuestion) dependentQuestion;
                    if (answerableQuestion != null) {
                        String answer = answerableQuestion.getAnswer();
                        if (dependency.hasValue() && !answer.equals(dependency.getDependencyValue())) {
                            //Caso a dependência não seja atendida, é retornada recursivamente a próxima pergunta
                            return getNextQuestionWithLastQuestion(nextPossibleQuestion);
                        }
                    }
                }
            }
            return nextPossibleQuestion;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Question getLastQuestionWithCurrentQuestion(Question currentQuestion) {
        if (currentQuestion == null) {
            return getQuestions().get(getQuestions().size() - 1);
        }
        int index = getQuestions().indexOf(currentQuestion);
        try {
            Question lastPossibleQuestion = getQuestions().get(index - 1);
            if (lastPossibleQuestion.getType().equals("MultipleChoiceQuestion")) {
                if (((MultipleChoiceQuestion) lastPossibleQuestion).getAnswers().size() > 0) {
                    return lastPossibleQuestion;
                }
            }else {
                if (!((AnswerableQuestion) lastPossibleQuestion).getAnswer().equals("")) {
                    return lastPossibleQuestion;
                }
            }
            return getLastQuestionWithCurrentQuestion(lastPossibleQuestion);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}