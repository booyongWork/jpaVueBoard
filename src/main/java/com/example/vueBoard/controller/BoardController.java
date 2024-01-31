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

    private final BoardService boardService;
    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

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

    @GetMapping({"/{no}"})
    public ResponseEntity<Board> read(@PathVariable("no") Long no) throws Exception {
        log.info("read");
        Board item = this.boardService.read(no);
        // Hibernate.initialize(item);
        Board itemDto = mapItemToDto(item);
        // cnt 값을 1 증가시킵니다.
        itemDto.setCnt(itemDto.getCnt() + 1);

        // 업데이트된 itemDto를 데이터베이스에 저장합니다.
        this.boardService.regist(itemDto);
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    private Board mapItemToDto(Board board) {
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

    @PostMapping
    public ResponseEntity<Board> register(@RequestPart("item") String itemString, @RequestPart("file") MultipartFile picture) throws Exception {
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

        item.setPicture(picture);
        item.setWriter("admin");
        item.setUseYn("Y");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(formatter);
        item.setRegDate(formattedDate);

        MultipartFile file = item.getPicture();
        log.info("originalName: " + file.getOriginalFilename());
        log.info("size: " + file.getSize());
        log.info("contentType: " + file.getContentType());
        String createdFileName = this.uploadFile(file.getOriginalFilename(), file.getBytes());
        item.setPictureUrl(createdFileName);
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

//    public ItemController(final ItemService itemService) {
//        this.itemService = itemService;
//    }
}

