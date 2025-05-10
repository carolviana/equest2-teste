package com.example.newequest.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.util.Pair;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.newequest.model.question.AnswerableQuestion;
import com.example.newequest.model.question.EditQuestion;
import com.example.newequest.model.question.GeoLocationQuestion;
import com.example.newequest.model.question.ListQuestion;
import com.example.newequest.model.question.MultipleChoiceQuestion;
import com.example.newequest.model.question.NotAnswerableQuestion;
import com.example.newequest.model.question.OnlyOneChoiceQuestion;
import com.example.newequest.model.question.PersonCreatorQuestion;
import com.example.newequest.model.question.Question;
import com.example.newequest.model.question.RowQuestion;
import com.example.newequest.model.question.SpinnerChoiceQuestion;
import com.example.newequest.model.question.Table;
import com.github.javafaker.Faker;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

@Entity
public class Questionnaire implements Parcelable {

    @Ignore
    public static final String LOG_TAG = Questionnaire.class.getName();
    @PrimaryKey(autoGenerate = true)
    private int primaryKey;
    private ArrayList<Block> blocks = new ArrayList<>();
    private ArrayList<Person> people = new ArrayList<>();
    private Person mainRespondent;
    private Question lastQuestion;
    private boolean isComplete = false;
    private String city;
    private String dateTime = getDateTimeInTimezone();
    private String endTime;
    @Ignore
    private long startTimeInMilliseconds = 0;
    private double elapsedTimeInSeconds = 0;
    private String interviewer;
    private String interviewerName;
    private String replicationQuestionID;
    private String location;
    private boolean isNew = false;

    private static final String MULTIPLE_QUESTION_TYPE = "MultipleChoiceQuestion";
    private static final String NOT_ANSWERABLE_QUESTION_TYPE = "NotAnswerableQuestion";
    private static final String MORE_THAN_QUESTION_TYPE = "MoreThanQuestion";

    private static final String CITY_CHECK_QUESTION_TYPE = "CityCheckQuestion";
    private static final String EDIT_QUESTION_TYPE = "EditQuestion";
    private static final String GEO_LOCATION_QUESTION_TYPE = "GeoLocationQuestion";
    private static final String ONLY_ONE_CHOICE_QUESTION_TYPE = "OnlyOneChoiceQuestion";
    private static final String PERSON_CREATOR_QUESTION_TYPE ="PersonCreatorQuestion";
    private static final String SPINNER_QUESTION_TYPE = "SpinnerChoiceQuestion";

    private static final String ROW_TYPE = "RowQuestion";
    private static final String TABLE_TYPE = "Table";
    private static final String LIST_TYPE = "ListQuestion";



    public Questionnaire() {
    }

    public Questionnaire(boolean isNew){
        this.isNew = true;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<Block> blocks) {
        this.blocks = blocks;
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public String getPeopleString() {
        StringBuilder sb = new StringBuilder();
        if (people.size() > 0) {
            for (int i = 0; i < people.size(); i++) {
                //sb.append(i).append(people.get(i).getName()).append("\n");
                sb.append(i+1).append(": ").append(people.get(i).getName()).append("\n");
            }
            return sb.toString();
        }
        return "";
    }

    public void setPeople(ArrayList<Person> people) {
        this.people = people;
    }

    public void addPerson(Person person) {
        people.add(person);
    }

    public int getNextPersonId() {
        return people.size();
    }

    public Question getLastQuestion() {
        return lastQuestion;
    }

    public void setLastQuestion(Question lastQuestion) {
        this.lastQuestion = lastQuestion;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(String interviewer) {
        this.interviewer = interviewer;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    private String getDateTimeInTimezone() {
        return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.LONG)
                .format(Calendar.getInstance().getTime());
    }

    public void startTimer() {
        startTimeInMilliseconds = SystemClock.elapsedRealtime();
    }

    public void finishTimer() {
        long endTimeInMilliseconds = SystemClock.elapsedRealtime();
        long elapsedMilliSeconds = endTimeInMilliseconds - startTimeInMilliseconds;
        elapsedTimeInSeconds += elapsedMilliSeconds / 1000.0;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setQuestionnaireEndTime(){
        this.endTime = getDateTimeInTimezone();
    }

    //this.endTime = getDateTimeInTimezone();
    public double getElapsedTimeInSeconds() {
        return elapsedTimeInSeconds;
    }

    public void setElapsedTimeInSeconds(double elapsedTimeInSeconds) {
        this.elapsedTimeInSeconds = elapsedTimeInSeconds;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void addLocation(String location){
        StringBuilder sb = new StringBuilder();
        sb.append(this.location).append("|").append(location);
        this.location = sb.toString();
    }

    public Person getPersonById(int personId) {
        return people.get(personId);
    }

    public String getPersonNameById(int personId) {
        try {
            return people.get(personId).getName();
        } catch (IndexOutOfBoundsException e) {
            if (personId == 0) {
                return "Respondente Principal";
            } else {
                return "Próximo membro da família";
            }
        }
    }

    public Person getMainRespondent() {
        return mainRespondent;
    }

    public void setMainRespondent(Person mainRespondent) {
        this.mainRespondent = mainRespondent;
    }

    public String getReplicationQuestionID() {
        return replicationQuestionID;
    }

    public void setReplicationQuestionID(String replicationQuestionID) {
        this.replicationQuestionID = replicationQuestionID;
    }

    protected Questionnaire(Parcel in) {
        if (in.readByte() == 0x01) {
            blocks = new ArrayList<>();
            in.readList(blocks, Block.class.getClassLoader());
        } else {
            blocks = null;
        }
        if (in.readByte() == 0x01) {
            people = new ArrayList<>();
            in.readList(people, Person.class.getClassLoader());
        } else {
            people = null;
        }
        mainRespondent = (Person) in.readValue(Person.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (blocks == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(blocks);
        }
        if (people == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(people);
        }
        dest.writeValue(mainRespondent);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Questionnaire> CREATOR = new Parcelable.Creator<Questionnaire>() {
        @Override
        public Questionnaire createFromParcel(Parcel in) {
            return new Questionnaire(in);
        }

        @Override
        public Questionnaire[] newArray(int size) {
            return new Questionnaire[size];
        }
    };

    private Question getQuestionById(String id) {
        for (Block block : blocks) {
            for (Question question : block.getQuestions()) {
                if (question.getId().equals(id)) {
                    return question;
                }
            }
        }
        return null;
    }

    public Question getQuestionByIdAndRespondent(String id, int respondent) {
        for (Block block : blocks) {
            for (Question question : block.getQuestions()) {
                if ((question.getId().equals(id)) && (question.getRespondent() == respondent)) {
                    return question;
                }
            }
        }
        return null;
    }

    public Question getFirstQuestion(){
        return blocks.get(0).getQuestions().get(0);
    }

    public String getReference(String id, int respondent) { //getQuestionAnswerByIdAndRespondent
        for (Block block : blocks) {
            for (Question question : block.getQuestions()) {
                if ((question.getId().equals(id)) && (question.getRespondent() == respondent)) {
                    AnswerableQuestion answerableQuestion = (AnswerableQuestion) question;
                    return answerableQuestion.getAnswer();
                }
            }
        }
        return null;
    }

    public Pair<Integer, Integer> getBlockAndQuestionIndexes(Question question) {
        for (Block block : blocks) {
            for (Question e : block.getQuestions()) {
                if (e.getId().equals(question.getId()) && (e.getRespondent() == question.getRespondent())) {
                    int indexOfQuestion = block.getQuestions().indexOf(e);
                    int indexOfBlock = blocks.indexOf(block);
                    return new Pair<>(indexOfBlock, indexOfQuestion);
                }
            }
        }
        return null;
    }

    //Usar esta função quando quiser obter a próxima pergunta
    public Question getNextQuestionWithLastQuestion(Question lastQuestion, Integer respondent) {
        this.lastQuestion = lastQuestion;
        //TODO should return first not answered question when resume is clicked
        if (lastQuestion == null) {
            return blocks.get(0).getQuestions().get(0);
        }
        Pair<Integer, Integer> blockAndQuestionIndexes = getBlockAndQuestionIndexes(lastQuestion);
        if (blockAndQuestionIndexes != null &&
                blockAndQuestionIndexes.first != null &&
                blockAndQuestionIndexes.second != null) {
            Block block = blocks.get(blockAndQuestionIndexes.first);
            if (block != null) {
                Question nextPossibleQuestion;
                try {
                    nextPossibleQuestion = block.getQuestions()
                            .get(blockAndQuestionIndexes.second + 1);
                } catch (IndexOutOfBoundsException e) {
                    try {
                        block = blocks.get(blockAndQuestionIndexes.first + 1);
                        nextPossibleQuestion = block.getQuestions().get(0);
                    } catch (IndexOutOfBoundsException f) {
                        return null;
                    }
                }

                verifyReplication(nextPossibleQuestion);

                if(respondent != null){
                    if(nextPossibleQuestion.getRespondent() != respondent){
                        return getNextQuestionWithLastQuestion(nextPossibleQuestion, respondent);
                    }
                }

                if (nextPossibleQuestion.getDependencies() != null) {
                    if (!verifyDependencies(nextPossibleQuestion)) {
                        if (nextPossibleQuestion.getType().equals(MULTIPLE_QUESTION_TYPE)) {
                            ((MultipleChoiceQuestion) nextPossibleQuestion).setAnswers(new ArrayList<String>());
                            ((MultipleChoiceQuestion) nextPossibleQuestion).setAnswersValue(new ArrayList<String>());
                            ((MultipleChoiceQuestion) nextPossibleQuestion).setAnswerValue("");
                        } else
                            try {
                                if (!((AnswerableQuestion) nextPossibleQuestion).getAnswer().equals("")) {
                                    ((AnswerableQuestion) nextPossibleQuestion).setAnswer("");
                                    ((AnswerableQuestion) nextPossibleQuestion).setAnswerValue("");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        return getNextQuestionWithLastQuestion(nextPossibleQuestion, respondent);
                    }
                }

                if (nextPossibleQuestion.hasReference()) {
                    nextPossibleQuestion.setReference(getReference(nextPossibleQuestion.getReference(), nextPossibleQuestion.getRespondent()));
                }

                return nextPossibleQuestion;
            }
        }
        return null;
    }



    public Question getLastQuestionWithCurrentQuestion(Question currentQuestion, Integer respondent) {
        Question lastPossibleQuestion;

        //TODO: integrar com lastQuestion

        Pair<Integer, Integer> blockAndQuestionIndexes = getBlockAndQuestionIndexes(currentQuestion);

        if((blockAndQuestionIndexes.first == 0) && (blockAndQuestionIndexes.second == 0)){
            return null;
        }

        if (blockAndQuestionIndexes != null &&
                blockAndQuestionIndexes.first != null &&
                blockAndQuestionIndexes.second != null) {

            Block block = blocks.get(blockAndQuestionIndexes.first);
            if (block != null) { //esse if não ta fazendo nd - repetido
                try {
                    lastPossibleQuestion = block.getQuestions().get(blockAndQuestionIndexes.second - 1);
                } catch (IndexOutOfBoundsException e) {
                    try {
                        block = blocks.get(blockAndQuestionIndexes.first - 1);
                        lastPossibleQuestion = block.getQuestions().get(block.getLastQuestionIndex());
                    } catch (IndexOutOfBoundsException f) {
                        return null;
                    }
                }

                if(respondent != null){
                    if(lastPossibleQuestion.getRespondent() != respondent){
                        return getLastQuestionWithCurrentQuestion(lastPossibleQuestion, respondent);
                    }
                }

                switch (lastPossibleQuestion.getType()) {
                    case MULTIPLE_QUESTION_TYPE:
                    case GEO_LOCATION_QUESTION_TYPE:
                        if (((AnswerableQuestion) lastPossibleQuestion).getAnswers().size() > 0) {
                            return lastPossibleQuestion;
                        }
                        return getLastQuestionWithCurrentQuestion(lastPossibleQuestion, respondent);

                    case NOT_ANSWERABLE_QUESTION_TYPE:
                        return lastPossibleQuestion;

                    case MORE_THAN_QUESTION_TYPE:
                        ((AnswerableQuestion) currentQuestion).setAnswer("");
                        return getLastQuestionWithCurrentQuestion(currentQuestion, respondent);

                    case EDIT_QUESTION_TYPE:
                    case PERSON_CREATOR_QUESTION_TYPE:
                    case CITY_CHECK_QUESTION_TYPE:
                    case ONLY_ONE_CHOICE_QUESTION_TYPE:
                    case SPINNER_QUESTION_TYPE:
                        if (!((AnswerableQuestion) lastPossibleQuestion).getAnswer().equals("")) {
                            return lastPossibleQuestion;
                        }
                        return getLastQuestionWithCurrentQuestion(lastPossibleQuestion, respondent);

                    case ROW_TYPE: //Não está sendo utilizada.
                    case TABLE_TYPE: //Não está sendo utilizada.
                    case LIST_TYPE: //Não está sendo utilizada.
                        return lastPossibleQuestion;

                    default:
                        return getLastQuestionWithCurrentQuestion(lastPossibleQuestion, respondent);
                }
            }
        }
        return null;
    }

    private boolean verifyDependencies(Question question) {
        ArrayList<Dependency> dependencies = question.getDependencies();
        ArrayList<String> dependenciesList = new ArrayList<>();
        for (Dependency dependency : dependencies) {
            Question dependentQuestion = getQuestionByIdAndRespondent(dependency.getDependencyID(), question.getRespondent());

            if (dependentQuestion!= null && dependentQuestion.getType().equals(MULTIPLE_QUESTION_TYPE)) {
                if (dependentQuestion.getRespondent() == question.getRespondent()) {
                    ArrayList<String> answers = ((MultipleChoiceQuestion) dependentQuestion).getAnswers();
                    if (dependency.hasValue()) {
                        for (String answer : answers) {
                            if (answer.equals(dependency.getDependencyValue())) {
                                dependenciesList.add(dependency.getDependencyID());
                                break;
                            }
                        }
                    }
                }

            } else {
                AnswerableQuestion answerableQuestion = (AnswerableQuestion) dependentQuestion;
                String answer;
                if (answerableQuestion != null) {
                    if (dependentQuestion.getRespondent() == question.getRespondent()) {
                        answer = answerableQuestion.getAnswer();
                        if (dependency.hasValue()) {
                            //TODO tratar dependência para OpOther
                            if (answer.equals(dependency.getDependencyValue())) {
                                dependenciesList.add(dependency.getDependencyID());
                            }
                        }
                    }
                }
            }

        }
        for (Dependency dependency : dependencies) {
            if (!dependenciesList.contains(dependency.getDependencyID())) {
                return false;
            }
        }
        return true;
    }

    public Question getFirstNotAnsweredQuestion(Question question, Integer respondent) {

        //indice da pergunta atual
        Pair<Integer, Integer> blockAndQuestionIndexes = getBlockAndQuestionIndexes(question);
        if (blockAndQuestionIndexes != null &&
                blockAndQuestionIndexes.first != null &&
                blockAndQuestionIndexes.second != null) {

            //pega a próxima pergunta pra testar
            Block block = blocks.get(blockAndQuestionIndexes.first);
            if (block != null) {
                Question nextPossibleQuestion;
                try {
                    nextPossibleQuestion = block.getQuestions().get(blockAndQuestionIndexes.second + 1);
                } catch (IndexOutOfBoundsException e) {
                    try {
                        block = blocks.get(blockAndQuestionIndexes.first + 1);
                        nextPossibleQuestion = block.getQuestions().get(0);
                    } catch (IndexOutOfBoundsException f) {
                        return null;
                    }
                }

                //testa replicação
                verifyReplication(nextPossibleQuestion);

                //verifica se é do mesmo respondente (caso respondente não seja nulo)
                if(respondent != null){
                    if(nextPossibleQuestion.getRespondent() != respondent){
                        return getFirstNotAnsweredQuestion(nextPossibleQuestion, respondent);
                    }
                }

                //verifica dependências - apaga resposta se a pergunta tem resposta e está fora do fluxo
                if (nextPossibleQuestion.getDependencies() != null) {
                    if (!verifyDependencies(nextPossibleQuestion)) {
                        if (nextPossibleQuestion.getType().equals(MULTIPLE_QUESTION_TYPE)) {
                            //TODO:|| nextPossibleQuestion.getType().equals(GEO_LOCATION_QUESTION_TYPE)
                            ((MultipleChoiceQuestion) nextPossibleQuestion).setAnswers(new ArrayList<String>());
                            ((MultipleChoiceQuestion) nextPossibleQuestion).setAnswersValue(new ArrayList<String>());
                            ((MultipleChoiceQuestion) nextPossibleQuestion).setAnswerValue("");
                        } else
                            try {
                                if (!((AnswerableQuestion) nextPossibleQuestion).getAnswer().equals("")) {
                                    ((AnswerableQuestion) nextPossibleQuestion).setAnswer("");
                                    ((AnswerableQuestion) nextPossibleQuestion).setAnswerValue("");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        return getFirstNotAnsweredQuestion(nextPossibleQuestion, respondent);
                    }
                }

                //verifica se tem referências
                if(nextPossibleQuestion.hasReference()) {
                    //if(((AnswerableQuestion) nextPossibleQuestion).getAnswer().equals("")){
                    nextPossibleQuestion.setReference(getReference(nextPossibleQuestion.getReference(), nextPossibleQuestion.getRespondent()));
                    //}
                }

                //verifica se tem resposta
                switch (nextPossibleQuestion.getType()) {

                    case MULTIPLE_QUESTION_TYPE:
                        if (((MultipleChoiceQuestion) nextPossibleQuestion).getAnswers().size() > 0) {
                            return getFirstNotAnsweredQuestion(nextPossibleQuestion, respondent);
                        }
                        break;

                    case GEO_LOCATION_QUESTION_TYPE:
                        if (((GeoLocationQuestion) nextPossibleQuestion).getAnswers().size() > 0) {
                            return getFirstNotAnsweredQuestion(nextPossibleQuestion, respondent);
                        }
                        break;

                    case NOT_ANSWERABLE_QUESTION_TYPE:
                        return getFirstNotAnsweredQuestion(nextPossibleQuestion, respondent);

                    default:
                        try {
                            if (!((AnswerableQuestion) nextPossibleQuestion).getAnswer().equals("")){
                                return getFirstNotAnsweredQuestion(nextPossibleQuestion, respondent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
                return nextPossibleQuestion;
            }
        }
        return null;
    }

    public void verifyReplication(Question nextPossibleQuestion){
        Pair<Integer, Integer> blockAndQuestionIndexes = getBlockAndQuestionIndexes(nextPossibleQuestion);
        Block block = blocks.get(blockAndQuestionIndexes.first);

        if (nextPossibleQuestion.hasReplication()) { //&& nextPossibleQuestion.getReplication()
            AnswerableQuestion replicationQuestion = (AnswerableQuestion) getQuestionById(nextPossibleQuestion.getReplication());
            int replication;
            if (replicationQuestion != null) {
                setReplicationQuestionID(replicationQuestion.getId());
                replication = Integer.parseInt(replicationQuestion.getAnswer());//int replication = Integer.parseInt(answer);
                int index = block.getQuestions().indexOf(nextPossibleQuestion);
                nextPossibleQuestion.setReplication(null);
                nextPossibleQuestion.setReplicationStatus(true);
                for (int i = 1; i < replication; i++) { //replicação
                    try {
                        Question question;
                        if (nextPossibleQuestion.getType().equals(TABLE_TYPE)){
                            question = (Table) nextPossibleQuestion.clone();
                            for(Question internalQuestion: ((Table) question).getQuestions()){
                                internalQuestion.setRespondent(i);
                                internalQuestion.setReplicationStatus(true);
                            }
                        } else {
                            question = (Question) nextPossibleQuestion.clone();
                            if (!question.getType().equals(MORE_THAN_QUESTION_TYPE)) {
                                question.setReference(null);
                            }
                        }

                        question.setReplication(null);
                        question.setRespondent(i);
                        question.setReplicationStatus(true);

                        block.getQuestions().add(index + 1, question);
                        index = block.getQuestions().indexOf(question);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                //printAllQuestions();
            }
        }
    }

    //Faz update apagando tudo que vem depois
    public void updateQuestion(Question question) {
        Pair<Integer, Integer> blockAndQuestionIndexes = getBlockAndQuestionIndexes(question);
        if (blockAndQuestionIndexes != null &&
                blockAndQuestionIndexes.first != null &&
                blockAndQuestionIndexes.second != null) {
            Block block = blocks.get(blockAndQuestionIndexes.first);
            for (int i = blockAndQuestionIndexes.second + 1; i < block.getQuestions().size(); i++) {
                Question iQuestion = block.getQuestions().get(i);
                AnswerableQuestion answerableQuestion = (AnswerableQuestion) iQuestion;
                answerableQuestion.setAnswer("");
            }
            for (int j = blockAndQuestionIndexes.first + 1; j < blocks.size(); j++) {
                Block jBlock = blocks.get(j);
                for (Question blockQuestion : jBlock.getQuestions()) {
                    AnswerableQuestion answerableQuestion = (AnswerableQuestion) blockQuestion;
                    answerableQuestion.setAnswer("");
                }
            }
        }
    }

    public void fillWithDummyData() {
        Question question = getNextQuestionWithLastQuestion(null,null);
        while (question != null) {
            answerDummyQuestion(question);
            question = getNextQuestionWithLastQuestion(question, null);
        }
    }

    private void answerDummyQuestion(Question question) {
        if (!(question instanceof NotAnswerableQuestion)) {
            //question.setRespondent(mainRespondent);
            question.setRespondent(0);
            if (question.getId().equals("CF.1")) {
                AnswerableQuestion answerableQuestion = (AnswerableQuestion) question;
                answerableQuestion.setAnswer("2");
            } else if (question instanceof EditQuestion) {
                if (question instanceof PersonCreatorQuestion) {
                    PersonCreatorQuestion personCreatorQuestion = (PersonCreatorQuestion) question;
                    Person person = new Person();
                    Faker faker = new Faker(new Locale("pt-BR"));
                    String name = faker.name().firstName();
                    person.setId(getNextPersonId());
                    person.setName(name);
                    people.add(person);
                    personCreatorQuestion.setAnswer(person.getName());
                } else {
                    EditQuestion editQuestion = (EditQuestion) question;
                    editQuestion.setAnswer("Test");
                }
            } else if (question instanceof ListQuestion) {
                ListQuestion listQuestion = (ListQuestion) question;
                listQuestion.setAnswer("Test");
            } else if (question instanceof MultipleChoiceQuestion) {
                MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
                ArrayList<String> answers = new ArrayList<>();
                answers.add("OpA");
                multipleChoiceQuestion.setAnswers(answers);
            } else if (question instanceof OnlyOneChoiceQuestion) {
                OnlyOneChoiceQuestion onlyOneChoiceQuestion = (OnlyOneChoiceQuestion) question;
                onlyOneChoiceQuestion.setAnswer("OpA");
            } else if (question instanceof RowQuestion) {
                RowQuestion rowQuestion = (RowQuestion) question;
                if (rowQuestion.getQuestions() != null) {
                    for (Question iQuestion : rowQuestion.getQuestions()) {
                        answerDummyQuestion(iQuestion);
                    }
                }
            } else if (question instanceof SpinnerChoiceQuestion) {
                SpinnerChoiceQuestion spinnerChoiceQuestion = (SpinnerChoiceQuestion) question;
                spinnerChoiceQuestion.setAnswer("OpA");
            } else if (question instanceof Table) {
                Table table = (Table) question;
                if (table.getQuestions() != null) {
                    for (Question iQuestion : table.getQuestions()) {
                        answerDummyQuestion(iQuestion);
                    }
                }
            }
        }
    }

    public void startQuestionnaire(String name) {
        Person person = new Person();
        person.setId(getNextPersonId());
        if (name == null) {
            Faker faker = new Faker(new Locale("pt-BR"));
            person.setName(faker.name().firstName());
        } else {
            person.setName(name);
        }
        setMainRespondent(person);
        people.add(person);
    }

    // É só chamar essa função passando a resposta após chamar setAnswer de um PersonCreatorQuestion
    public void createPerson(String name, int personID, String option) {
        if(option.equals("create")){
            if(personID == 0 && people.size() == 1){
                people.get(personID).setName(name);
            } else {
                Person person = new Person();
                person.setId(getNextPersonId());
                person.setName(name);
                if (people.size() == 0) {
                    setMainRespondent(person);
                    person.setMainRespondent(true);
                } else {
                    person.setMainRespondent(false);
                }
                addPerson(person);
            }
        } else {
            people.get(personID).setName(name);
        }
    }

    public void deletePerson(int personID){
        //apagar perguntas do questionário
        Question question;
        for (int blockIndex = 0; blockIndex <= blocks.size() - 1; blockIndex++) {
            for (int questionIndex = 0 ; questionIndex <= blocks.get(blockIndex).getQuestions().size() - 1; questionIndex++) {
                question = blocks.get(blockIndex).getQuestions().get(questionIndex);
                if(question.getRespondent() == personID){
                    blocks.get(blockIndex).getQuestions().remove(question);
                }
            }
        }

        //Acertar Ordem se não for a ultima pessoa
        if(personID != people.size() - 1) {
            for (int i = personID + 1; i < people.size(); i++) {
                for (int blockIndex = 0; blockIndex <= blocks.size() - 1; blockIndex++) {
                    for (int questionIndex = 0; questionIndex <= blocks.get(blockIndex).getQuestions().size() - 1; questionIndex++) {
                        question = blocks.get(blockIndex).getQuestions().get(questionIndex);
                        if (question.getRespondent() == i) {
                            blocks.get(blockIndex).getQuestions().get(questionIndex).setRespondent(i - 1);
                        }
                    }
                }
            }
        }

        //apagar do vetor pessoa
        people.remove(personID);

        //atualizar número de pessoas na família
        AnswerableQuestion replicationQuestion = (AnswerableQuestion) getQuestionByIdAndRespondent(replicationQuestionID, 0);
        String newValue = String.valueOf(Integer.parseInt(replicationQuestion.getAnswer()) - 1);
        replicationQuestion.setAnswer(newValue);
    }

    public void printAllQuestions(){
        for (Block block : blocks) {
            for (Question question : block.getQuestions()) {
                try{
                    if(question.getType().equals(TABLE_TYPE)){
                        Log.i("carol", question.getId() + " - " + question.getRespondent() + " (Tabela)");
                        for (Question internalQuestion : ((Table) question).getQuestions()){
                            String teste = ((AnswerableQuestion) internalQuestion).getAnswer();
                            Log.i("carol", " -> " + internalQuestion.getId() + " - " + internalQuestion.getRespondent() + " - " + teste);
                        }
                    }else {
                        if(question.getType().equals(MULTIPLE_QUESTION_TYPE) || question.getType().equals(GEO_LOCATION_QUESTION_TYPE)){
                            Log.i("carol", "id: " + question.getId() + " - r: " + question.getRespondent() + " - a: " + ((AnswerableQuestion) question).getAnswers() + " - av: " + ((AnswerableQuestion) question).getAnswerValue());
                        }else {
                            Log.i("carol", "id: " + question.getId() + " - r: " + question.getRespondent() + " - a: " + ((AnswerableQuestion) question).getAnswer() + " - av: " + ((AnswerableQuestion) question).getAnswerValue());
                        }
                    }
                }catch (Exception e){
                    Log.i("carol", question.getId() + " - " + question.getRespondent());
                }
            }
        }
    }

    public Question addNewPerson(){
        //adicionar perguntas para o novo respondente
        Question personCreatorQuestion = null;
        Question question;
        for (int blockIndex = 0; blockIndex <= blocks.size() - 1; blockIndex++) {
            for (int questionIndex = 0 ; questionIndex <= blocks.get(blockIndex).getQuestions().size() - 1; questionIndex++) {
                question = blocks.get(blockIndex).getQuestions().get(questionIndex);
                if((question.isReplica()) && (question.getRespondent() == 0)){
                    try {
                        Question questionAux = (Question) question.clone();
                        questionAux.setReplication(null);
                        questionAux.setRespondent(people.size());
                        questionAux.setReplicationStatus(true);
                        if (!questionAux.getType().equals(MORE_THAN_QUESTION_TYPE)) {
                            questionAux.setReference(null);
                        }
                        if(questionAux.getType().equals(MULTIPLE_QUESTION_TYPE) || questionAux.getType().equals(GEO_LOCATION_QUESTION_TYPE)){
                            ((AnswerableQuestion) questionAux).setAnswers(new ArrayList<String>());
                        }else{
                            try{
                                ((AnswerableQuestion) questionAux).setAnswer("");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        if(questionAux.getType().equals(PERSON_CREATOR_QUESTION_TYPE)){
                            personCreatorQuestion = questionAux;
                        }
                        blocks.get(blockIndex).getQuestions().add(questionIndex + people.size(), questionAux);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                        //Erro ao adicionar pessoa
                        return null;
                    }
                }
            }
        }

        //printAllQuestions();

        //atualizar o número de pessoas na família
        AnswerableQuestion replicationQuestion = (AnswerableQuestion) getQuestionByIdAndRespondent(replicationQuestionID, 0);
        String newValue = String.valueOf(Integer.parseInt(replicationQuestion.getAnswer()) + 1);
        replicationQuestion.setAnswer(newValue);
        return personCreatorQuestion;
    }

    public ArrayList<String> getPeopleArray(){
        ArrayList<String> peopleArray = new ArrayList<>();
        for(Person person: people){
            peopleArray.add(person.getName());
        }
        return peopleArray;
    }

    public boolean verifyAddPeopleDependency(){
        if(replicationQuestionID != null){
            for(Block block: blocks){
                for(Question question: block.getQuestions()){
                    if (question.getType().equals(PERSON_CREATOR_QUESTION_TYPE)){
                        if( ((AnswerableQuestion) question).getAnswer().equals("")){
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public QuestionnaireData getQuestionnaireData() {
        QuestionnaireData questionnaireData = new QuestionnaireData();

        for (Block block : blocks) {
            for (Question question : block.getQuestions()) {
                if(!question.getType().equals(NOT_ANSWERABLE_QUESTION_TYPE)){
                    Answer answer;
                    if(question.getType().equals(MULTIPLE_QUESTION_TYPE) || question.getType().equals(GEO_LOCATION_QUESTION_TYPE)){
                        answer = new Answer(question.getId(),
                                question.getType(),
                                question.getRespondent(),
                                null,
                                ((AnswerableQuestion) question).getAnswers(),
                                ((AnswerableQuestion) question).getAnswerValue());
                    }else{
                        answer = new Answer(question.getId(),
                                question.getType(),
                                question.getRespondent(),
                                ((AnswerableQuestion) question).getAnswer(),
                                null,
                                ((AnswerableQuestion) question).getAnswerValue());
                    }
                    questionnaireData.getAnswers().add(answer);
                }
            }
        }

        questionnaireData.setPrimaryKey(this.primaryKey);
        questionnaireData.setComplete(true);
        questionnaireData.setDateTime(this.dateTime);
        questionnaireData.setEndTime(this.endTime);
        questionnaireData.setElapsedTimeInSeconds(this.elapsedTimeInSeconds);
        questionnaireData.setInterviewerName(this.interviewerName);
        questionnaireData.setPeopleString(getPeopleString());
        questionnaireData.setJsonVersion("20");
        if(this.interviewer != null){
            questionnaireData.setInterviewer(this.interviewer);
        }
        if(this.location != null){
            questionnaireData.setLocation(this.location);
        }
        return questionnaireData;
    }


}