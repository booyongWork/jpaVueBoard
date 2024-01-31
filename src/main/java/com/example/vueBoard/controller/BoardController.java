package com.example.vueBoard.controller;

import com.example.vueBoard.domain.Board;
import com.example.vueBoard.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(
        origins = {"http://localhost:8090", "http://localhost:3000"}
)
@RequestMapping({"/board"})
public class BoardController {
    private static final Logger log = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 게시판 목록 조회.
     *
     * @return 조회된 게시판 목록
     * @throws Exception 데이터베이스 조회 시 예외 발생
     */
    @GetMapping
    public ResponseEntity<List<Board>> list() throws Exception {
        List<Board> boardList;
        try {
            log.info("list");
            boardList = this.boardService.list();

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(boardList, HttpStatus.OK);
    }

    /**
     * 게시판 상세보기
     *
     * @param no 게시믈 번호
     * @return 조회된 게시믈 정보
     * @throws Exception 조회 과정에서 발생한 예외
     */
    @GetMapping("/{no}")
    public ResponseEntity<Board> read(@PathVariable("no") Long no) throws Exception {
        Board itemDto;

        try {
            log.info("read");
            Board item = this.boardService.read(no);
            itemDto = this.boardService.mapItemToDto(item);
            itemDto.setCnt(itemDto.getCnt() + 1);
            this.boardService.regist(itemDto);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    /**
     * 게시글 등록
     *
     * @param item   등록할 게시글 정보
     * @param picture 게시글에 첨부되는 이미지 파일
     * @throws Exception 등록 과정에서 발생한 예외
     */
    @PostMapping
    public ResponseEntity<Board> register(@RequestPart("item") String itemString, @RequestPart(name = "file",required = false) MultipartFile picture) throws Exception {
        log.info("itemString: " + itemString);
        Board item = (Board)(new ObjectMapper()).readValue(itemString, Board.class);
        String title = item.getTitle();
        String content = item.getContent();

        if (title != null) {
            log.info("item.getTitle(): " + title);
            item.setTitle(title);
        }

        if (content != null) {
            log.info("item.getContent(): " + content);
            item.setContent(content);
        }

        // 파일이 선택되었을 때만 설정
        if (picture != null) {
            item.setPicture(picture);
            MultipartFile file = item.getPicture();
            log.info("originalName: " + file.getOriginalFilename());
            log.info("size: " + file.getSize());
            log.info("contentType: " + file.getContentType());
            String createdFileName = this.uploadFile(file.getOriginalFilename(), file.getBytes());
            item.setPictureUrl(createdFileName);
        }

        item.setWriter("admin");
        item.setUseYn("Y");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(formatter);
        item.setRegDate(formattedDate);

        this.boardService.regist(item);
        log.info("register item.getNo() = " + item.getNo());
        Board createdItem = new Board();
        createdItem.setNo(item.getNo());
        return new ResponseEntity(createdItem, HttpStatus.OK);
    }

    private String uploadFile(String originalName, byte[] fileData) throws Exception {
        UUID uid = UUID.randomUUID();
        String createdFileName = uid.toString() + "_" + originalName;
        File target = new File(this.uploadPath, createdFileName);
        FileCopyUtils.copy(fileData, target);
        return createdFileName;
    }

    @GetMapping({"/display"})
    public ResponseEntity<byte[]> displayFile(Long no) throws Exception {
        ResponseEntity<byte[]> entity = null;
        String fileName = this.boardService.getPicture(no);
        log.info("FILE NAME: " + fileName);

        try {
            String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
            MediaType mediaType = this.getMediaType(formatName);
            HttpHeaders headers = new HttpHeaders();
            File file = new File(this.uploadPath + File.separator + fileName);
            if (mediaType != null) {
                headers.setContentType(mediaType);
            }

            entity = new ResponseEntity(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.CREATED);
        } catch (Exception var8) {
            var8.printStackTrace();
            entity = new ResponseEntity(HttpStatus.BAD_REQUEST);
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


    @DeleteMapping({"/{no}"})
    public ResponseEntity<Void> remove(@PathVariable("no") Long no) throws Exception {
        log.info("remove");
        this.boardService.remove(no);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Board> modify(@RequestPart("item") String itemString, @RequestPart(name = "file",required = false) MultipartFile picture) throws Exception {
        log.info("itemString: " + itemString);
        Board item = (Board)(new ObjectMapper()).readValue(itemString, Board.class);
        String title = item.getTitle();
        String content = item.getContent();
        if (title != null) {
            log.info("item.getTitle(): " + title);
            item.setTitle(title);
        }

        if (content != null) {
            log.info("item.getContent(): " + content);
            item.setContent(content);
        }

        Board modifiedItem;
        if (picture != null) {
            item.setPicture(picture);
            MultipartFile file = item.getPicture();
            log.info("originalName: " + file.getOriginalFilename());
            log.info("size: " + file.getSize());
            log.info("contentType: " + file.getContentType());
            String createdFileName = this.uploadFile(file.getOriginalFilename(), file.getBytes());
            item.setPictureUrl(createdFileName);
        } else {
            modifiedItem = this.boardService.read(item.getNo());
            item.setPictureUrl(modifiedItem.getPictureUrl());
        }

        this.boardService.modify(item);
        modifiedItem = new Board();
        modifiedItem.setNo(item.getNo());
        return new ResponseEntity(modifiedItem, HttpStatus.OK);
    }
}

