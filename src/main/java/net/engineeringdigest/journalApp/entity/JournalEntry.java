package net.engineeringdigest.journalApp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.engineeringdigest.journalApp.enums.Sentiment;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;




/*
@Document :
@Document is used with MongoDB in Spring Boot.
It marks a Java class as a MongoDB collection.
👉 Same idea as @Entity in JPA (MySQL), but for MongoDB.
Why we use @Document
To tell Spring Data MongoDB:
“This class should be stored in MongoDB”
“Each object = one document”
“This class maps to a MongoDB collection”
A MongoDB document is similar to a row, and we map a Java class to a MongoDB collection
using @Document(collection = "name"). There is no @Collection annotation in Spring Boot.
 */


@Document(collection = "journal_entries")
@Data
@NoArgsConstructor
public class JournalEntry {

    @Id
    private ObjectId id;
    @NonNull
    private String title;
    private String content;
    private LocalDateTime date;

    private Sentiment sentiment;


//    public LocalDateTime getDate() {
//        return date;
//    }
//
//    public void setDate(LocalDateTime date) {
//        this.date = date;
//    }
//
//    public ObjectId getId() {
//        return id;
//    }
//
//    public void setId(ObjectId id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }





}






/*
Why @NoArgsConstructor is needed:

Jackson (JSON library) requires it - When Spring receives your JSON request, Jackson:

First creates an empty object using no-args constructor

Then sets each field using setters

Without no-args constructor, Jackson can't create the object → 400 Bad Request

JPA/Hibernate requires it - For database entities, ORM frameworks need to instantiate objects when fetching from DB

Spring Framework uses it - For dependency injection and bean creation

Without @NoArgsConstructor + @NonNull:
 */
