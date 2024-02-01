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
        log.debug("################ BoardController.list ################");
        List<Board> boardList;
        try {
            boardList = boardService.list();

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
        log.debug("################ BoardController.read ################");
        Board itemDto;
        try {
            Board item = boardService.read(no);
            itemDto = boardService.mapItemToDto(item);
            itemDto.setCnt(itemDto.getCnt() + 1);
            boardService.updateCnt(itemDto);
    } catch (Exception e) {
        throw new Exception(e.getMessage());
    }

        return new ResponseEntity<>(itemDto, HttpStatus.OK);
}

    /**
     * 게시글 등록
     *
     * @param boardInfo   등록할 게시믈 정보
     * @param picture 게시글에 첨부되는 이미지 파일
     * @throws Exception 등록 과정에서 발생한 예외
     */
    @PostMapping
    public ResponseEntity<Board> register(@RequestPart("item") String boardInfo, @RequestPart(name = "file", required = false) MultipartFile picture) throws Exception {
        log.debug("################ BoardController.register ################");
        log.info("boardInfo: " + boardInfo);
        Board item = (Board)(new ObjectMapper()).readValue(boardInfo, Board.class);
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


    /**
     * 첨부파일 미리보기
     *
     * @param no 게시믈 번호
     * @return ResponseEntity<byte[]> 응답 엔터티. 성공 시 파일 데이터를 포함한 응답, 실패 시 INTERNAL_SERVER_ERROR 상태 반환
     */
    @GetMapping("/display")
    public ResponseEntity<byte[]> displayFile(@RequestParam Long no) {
        log.debug("################ BoardController.displayFile ################");
        try {
            ResponseEntity<byte[]> responseEntity = boardService.displayFile(no);
            return responseEntity;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게시물 삭제
     *
     * @param no 게시믈 번호
     * @return ResponseEntity<Void> 성공 시 NO_CONTENT 상태 반환
     * @throws Exception 삭제 도중 예외가 발생할 경우 발생한 예외 메시지를 담아서 전달
     */
    @DeleteMapping({"/{no}"})
    public ResponseEntity<Void> remove(@PathVariable("no") Long no) throws Exception {
        log.debug("################ BoardController.remove ################");
        try {
            boardService.remove(no);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * 게시물 수정
     *
     * @param boardInfo 수정할 게시물 정보
     * @param picture     수정할 첨부 파일
     * @return ResponseEntity<Board> 성공 시 수정된 게시물 정보와 OK 상태 반환
     * @throws Exception 수정 도중 예외가 발생할 경우 발생한 예외 메시지를 담아서 전달
     */
    @PutMapping
    public ResponseEntity<Board> modify(@RequestPart("item") String boardInfo, @RequestPart(name = "file",required = false) MultipartFile picture) throws Exception {
        log.debug("################ BoardController.modify ################");
        log.info("boardInfo: " + boardInfo);
        Board item = (Board)(new ObjectMapper()).readValue(boardInfo, Board.class);
        Board modifiedItem = new Board();

        try {
            modifiedItem = boardService.modify(item, picture);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity(modifiedItem, HttpStatus.OK);
    }
}

