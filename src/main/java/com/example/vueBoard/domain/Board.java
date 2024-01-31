package com.example.vueBoard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
@Getter
@Setter
@Entity
@Table(name = "vue")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;
    @Column(
            length = 50
    )
    private String title;
    @Column(
            length = 250
    )
    private String content;

    private int cnt;

    private String useYn;

    private String writer;
    private String regDate;
    @Transient
    private MultipartFile picture;
    @Column(
            length = 200
    )
    private String pictureUrl;
}
