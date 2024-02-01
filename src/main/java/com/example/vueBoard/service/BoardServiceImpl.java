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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    /**
     * (useYn='Y') 게시물 목록을 조회하는 메서드
     *
     * @return List<Board> 게시물 목록
     * @throws Exception 조회 중 발생한 예외
     */
    public List<Board> list() throws Exception {
        log.debug("################ BoardServiceImpl.list ################");
        return this.repository.findByUseYn("Y", Sort.by(Sort.Direction.DESC, "no"));
    }
    public Board read(Long no) throws Exception {
        log.debug("################ BoardServiceImpl.read ################");
        Optional<Board> optionalItemEntity = this.repository.findById(no);
        return (Board)this.repository.getOne(no);
    }

    @Transactional
    public void regist(Board item, MultipartFile picture) throws Exception {
        log.debug("################ BoardServiceImpl.regist ################");
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

            // 파일의 확장자 얻기
            String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

            // 파일 확장자에 따라 폴더 경로 만들기
            String folderPath = uploadPath + "/" + fileExtension;
            File folder = new File(folderPath);

            // 폴더가 존재하지 않으면 폴더 생성
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 파일을 폴더에 저장
            String createdFileName = uploadFile(file.getOriginalFilename(), file.getBytes(), folderPath);

            // 폴더 경로를 파일 경로에 추가하여 설정
            item.setFileUrl(createdFileName);
            item.setFileNm(file.getOriginalFilename());
        }

        item.setWriter("admin");
        item.setUseYn("Y");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(formatter);
        item.setRegDate(formattedDate);

        repository.save(item);
    }

    // 파일을 특정 폴더에 저장하는 메서드
    private String uploadFile(String originalFilename, byte[] bytes, String folderPath) throws IOException {
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        String fullPath = folderPath + "/";
        File folder = new File(fullPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String createdFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        Path path = Paths.get(fullPath + "/" + createdFileName);
        Files.write(path, bytes);

        return createdFileName;
    }

    public ResponseEntity<byte[]> displayFile(Long no) throws Exception {
        log.debug("################ BoardServiceImpl.displayFile ################");
        ResponseEntity<byte[]> entity = null;
        String fileName = this.getPicture(no);

        try {
            String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
            MediaType mediaType = this.getMediaType(formatName);
            HttpHeaders headers = new HttpHeaders();
            File file = new File(this.uploadPath + "/" + formatName+ File.separator + fileName);
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
        log.debug("################ BoardServiceImpl.getMediaType ################");
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
        log.debug("################ BoardServiceImpl.getMediaType ################");
        Board boardDto = new Board();
        boardDto.setNo(board.getNo());
        boardDto.setTitle(board.getTitle());
        boardDto.setContent(board.getContent());
        boardDto.setRegDate(board.getRegDate());
        boardDto.setCnt(board.getCnt());
        boardDto.setWriter(board.getWriter());
        boardDto.setUseYn(board.getUseYn());
        boardDto.setPicture(board.getPicture());
        boardDto.setFileUrl(board.getFileUrl());
        boardDto.setFileNm(board.getFileNm());
        return boardDto;
    }
    @Transactional
    public Board modify(Board item, MultipartFile picture) throws Exception {
        log.debug("################ BoardServiceImpl.modify ################");
        Optional<Board> optionalItemEntity = this.repository.findById(item.getNo());
        Board itemEntity = optionalItemEntity.orElseThrow(() -> new EntityNotFoundException("Entity with id " + item.getNo() + " not found"));

        itemEntity.setTitle(item.getTitle());
        itemEntity.setContent(item.getContent());

        if (picture != null && !picture.isEmpty()) {
            // 기존 파일 삭제 또는 처리
            if (itemEntity.getFileUrl() != null) {
                String filePath = uploadPath + "/" + itemEntity.getFileUrl();
                File existingFile = new File(filePath);
                if (existingFile.exists()) {
                    existingFile.delete();
                }
            }

            // 파일의 확장자 얻기
            String fileExtension = StringUtils.getFilenameExtension(picture.getOriginalFilename());

            // 파일 확장자에 따라 폴더 경로 만들기
            String folderPath = uploadPath + "/" + fileExtension;
            File folder = new File(folderPath);

            // 폴더가 존재하지 않으면 폴더 생성
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 파일을 폴더에 저장
            String createdFileName = uploadFile(picture.getOriginalFilename(), picture.getBytes(), folderPath);

            // 폴더 경로를 파일 경로에 추가하여 설정
            itemEntity.setFileUrl(createdFileName);
            itemEntity.setFileNm(picture.getOriginalFilename());
        }else{
            if(StringUtils.isEmpty(item.getFileNm())){
                // 첨부 파일이 없는 경우 fileNm과 fileUrl을 null로 설정하고 기존 파일 삭제
                itemEntity.setFileUrl(null);
                itemEntity.setFileNm(null);

                // 기존 파일 삭제 또는 처리
                if (itemEntity.getFileUrl() != null) {
                    String filePath = uploadPath + "/" + itemEntity.getFileUrl();
                    File existingFile = new File(filePath);
                    if (existingFile.exists()) {
                        existingFile.delete();
                    }
                }
            }
        }

        return repository.save(itemEntity);
    }

    @Transactional
    public void remove(Long no) throws Exception {
        log.debug("################ BoardServiceImpl.remove ################");
        Optional<Board> optionalBoard = this.repository.findById(no);

        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();
            board.setUseYn("N");
            this.repository.save(board);
        }
    }

    public String getPicture(Long no) throws Exception {
        log.debug("################ BoardServiceImpl.getPicture ################");
        Board item = (Board)this.repository.getOne(no);
        return item.getFileUrl();
    }

    public void updateCnt(Board item) throws Exception {
        log.debug("################ BoardServiceImpl.getPicture ################");
        repository.save(item);
    }
}
