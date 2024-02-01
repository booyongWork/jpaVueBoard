package com.example.vueBoard.controller;

import com.example.vueBoard.domain.Board;
import com.example.vueBoard.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(
        origins = {"http://localhost:8090", "http://localhost:3000"}
)
@RequestMapping({"/board"})
public class BoardController {
    private static final Logger log = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;

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
            this.boardService.updateCnt(itemDto);

    } catch (Exception e) {
        throw new Exception(e.getMessage());
    }

        return new ResponseEntity<>(itemDto, HttpStatus.OK);
}

    /**
     * 게시글 등록
     *
     * @param itemString   등록할 게시글 정보
     * @param picture 게시글에 첨부되는 이미지 파일
     * @throws Exception 등록 과정에서 발생한 예외
     */
    @PostMapping
    public ResponseEntity<Board> register(@RequestPart("item") String itemString, @RequestPart(name = "file", required = false) MultipartFile picture) throws Exception {
        log.info("itemString: " + itemString);
        Board item = (Board)(new ObjectMapper()).readValue(itemString, Board.class);
        Board registItem = new Board();
        try {
            boardService.regist(item, picture);
            log.info("register item.getNo() = " + item.getNo());
            registItem.setNo(item.getNo());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return new ResponseEntity<>(registItem, HttpStatus.OK);
    }



    @GetMapping("/display")
    public ResponseEntity<byte[]> displayFile(@RequestParam Long no) {
        try {
            ResponseEntity<byte[]> responseEntity = boardService.displayFile(no);
            return responseEntity;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        Board modifiedItem = new Board();

        try {
            modifiedItem = boardService.modify(item, picture);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity(modifiedItem, HttpStatus.OK);
    }
}

