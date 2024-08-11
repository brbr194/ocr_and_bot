package com.example.entity;



import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;


@Getter
@Entity
@Table(name = "recognize")
@Data
public class Recognize {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "recognition_result")
    private String recognitionResult;

    @Column(name = "remarks", length = 255)
    private String remarks;

    // Constructors
    public Recognize() {
    }

    public Recognize(String imageUrl, String recognitionResult, String remarks) {
        this.imageUrl = imageUrl;
        this.recognitionResult = recognitionResult;
        this.remarks = remarks;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setRecognitionResult(String recognitionResult) {
        this.recognitionResult = recognitionResult;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


}
