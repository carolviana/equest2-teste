package com.example.newequest.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.newequest.model.NewQuestionnaireEntry;
import com.example.newequest.model.Questionnaire;
import com.example.newequest.model.QuestionnaireEntry;

import java.util.List;

@Dao
public interface QuestionnaireDao {
    @Query("SELECT * FROM questionnaire")
    List<Questionnaire> loadAllQuestionnaires();

    @Query("SELECT primaryKey, mainRespondent, isComplete, dateTime FROM questionnaire WHERE NOT isComplete")
    List<QuestionnaireEntry> loadAllIncompleteQuestionnaireEntries();

    @Query("SELECT primaryKey, mainRespondent, isComplete, dateTime, lastQuestion FROM questionnaire")
    List<QuestionnaireEntry> loadAllQuestionnaireEntries();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertQuestionnaire(Questionnaire questionnaire);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateQuestionnaire(Questionnaire questionnaire);

    @Delete
    void deleteQuestionnaire(Questionnaire questionnaire);

    @Query("DELETE FROM questionnaire WHERE primaryKey = :primaryKey")
    void deleteByPrimaryKey(int primaryKey);

    @Query("SELECT * FROM questionnaire WHERE primaryKey = :primaryKey")
    Questionnaire loadQuestionnaireByPrimaryKey(int primaryKey);

    @Query("SELECT primaryKey, isNew FROM questionnaire WHERE isNew = :isNew")
    List<NewQuestionnaireEntry> loadNewQuestionnaire(boolean isNew);
}
