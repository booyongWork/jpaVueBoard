package com.example.vueBoard.service;

import com.example.vueBoard.controller.BoardController;
import com.example.vueBoard.domain.Board;
import com.example.vueBoard.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BoardServiceImpl implements BoardService {
    private static final Logger log = LoggerFactory.getLogger(BoardController.class);
    @Autowired
    private BoardRepository repository;
    @Value("${upload.path}")
    private String uploadPath;

    @Transactional(readOnly = true)
    public List<Board> list() throws Exception {
        return this.repository.findByUseYn("Y", Sort.by(Sort.Direction.DESC, "no"));
    }
    @Transactional(readOnly = true)
    public Board read(Long no) throws Exception {
        return (Board)this.repository.getOne(no);
    }

    @Transactional
    public void regist(Board item, MultipartFile picture) throws Exception {
        String title = item.getTitle();
        String content = item.getContent();

        if (title != null) {
            item.setTitle(title);
        }

        if (content != null) {
            item.setContent(content);
        }

        if (picture != null && !picture.isEmpty()) {
            item.setPicture(picture);
            MultipartFile file = item.getPicture();
            String createdFileName = uploadFile(file.getOriginalFilename(), file.getBytes());
            item.setPictureUrl(createdFileName);
        }

        item.setWriter("admin");
        item.setUseYn("Y");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(formatter);
        item.setRegDate(formattedDate);

        repository.save(item);
    }

    private String uploadFile(String originalName, byte[] fileData) throws Exception {
        UUID uid = UUID.randomUUID();
        String createdFileName = uid.toString() + "_" + originalName;
        File target = new File(this.uploadPath, createdFileName);
        FileCopyUtils.copy(fileData, target);
        return createdFileName;
    }

    public ResponseEntity<byte[]> displayFile(Long no) throws Exception {
        ResponseEntity<byte[]> entity = null;
        String fileName = this.getPicture(no);

        try {
            String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
            MediaType mediaType = this.getMediaType(formatName);
            HttpHeaders headers = new HttpHeaders();
            File file = new File(this.uploadPath + File.separator + fileName);
            if (mediaType != null) {
                headers.setContentType(mediaType);
            }

            entity = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            entity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return entity;
    }

    private MediaType getMediaType(String formatName) {
        if (formatName != null) {
            if (formatName.equals("JPG")) {
                return MediaType.IMAGE_JPEG;
            }

            if (formatName.equals("GIF")) {
                return MediaType.IMAGE_GIF;
            }

            if (formatName.equals("PNG")) {
                return MediaType.IMAGE_PNG;
            }
        }

        return null;
    }

    public Board mapItemToDto(Board board) {
        Board boardDto = new Board();
        boardDto.setNo(board.getNo());
        boardDto.setTitle(board.getTitle());
        boardDto.setContent(board.getContent());
        boardDto.setRegDate(board.getRegDate());
        boardDto.setCnt(board.getCnt());
        boardDto.setWriter(board.getWriter());
        boardDto.setUseYn(board.getUseYn());
        boardDto.setPicture(board.getPicture());
        boardDto.setPictureUrl(board.getPictureUrl());
        return boardDto;
    }
    @Transactional
    public Board modify(Board board, MultipartFile picture) throws Exception {
        Optional<Board> optionalItemEntity = this.repository.findById(board.getNo());
        Board itemEntity = optionalItemEntity.orElseThrow(() -> new EntityNotFoundException("Entity with id " + board.getNo() + " not found"));

        itemEntity.setTitle(board.getTitle());
        itemEntity.setContent(board.getContent());

        if (picture != null && !picture.isEmpty()) {
            // 기존의 파일은 삭제하거나 처리하고, 새로운 파일을 저장한 후에 경로를 엔터티에 설정
            String createdFileName = this.uploadFile(picture.getOriginalFilename(), picture.getBytes());
            itemEntity.setPictureUrl(createdFileName);
        }

        return itemEntity;
    }

    @Transactional
    public void remove(Long no) throws Exception {
        Optional<Board> optionalBoard = this.repository.findById(no);

        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();
            board.setUseYn("N");
            this.repository.save(board);
        }
    }

    public String getPicture(Long no) throws Exception {
        Board item = (Board)this.repository.getOne(no);
        return item.getPictureUrl();
    }

    public void updateCnt(Board item) throws Exception {
        repository.save(item);
    }
}
