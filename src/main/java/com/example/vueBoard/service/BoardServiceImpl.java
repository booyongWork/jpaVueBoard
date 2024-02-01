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
import org.springframework.transaction.annotation.Propagation;
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
    public List<Board> list() {
        log.debug("################ BoardServiceImpl.list ################");
        return repository.findByUseYn("Y", Sort.by(Sort.Direction.DESC, "no"));
    }

    /**
     * 게시물 상세 조회 메서드.
     *
     * @param no 조회할 게시물의 번호
     * @return 조회된 게시물
     * @throws EntityNotFoundException 게시물이 존재하지 않는 경우 발생하는 예외
     */
    public Board read(Long no) throws EntityNotFoundException{
        log.debug("################ BoardServiceImpl.read ################");
        try {
            return (Board) repository.getOne(no);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("No Board found with id: " + no);
        }
    }

    /**
     * 게시물 등록 메서드
     *
     * @param item    등록할 게시물 정보를 담은 Board 객체
     * @param picture 게시물에 첨부될 이미지 파일(MultipartFile)
     * @throws Exception 예외가 발생할 경우 전파됨
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void regist(Board item, MultipartFile picture) throws IOException  {
        log.debug("################ BoardServiceImpl.regist ################");

        try {
            // 제목과 내용 정보 설정
            String title = item.getTitle();
            String content = item.getContent();

            if (title != null) {
                item.setTitle(title);
            }

            if (content != null) {
                item.setContent(content);
            }

            // 이미지 파일이 존재하면 처리
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

            // 기타 정보 설정
            item.setWriter("admin");
            item.setUseYn("Y");
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = currentDate.format(formatter);
            item.setRegDate(formattedDate);

            // 게시물 저장
            repository.save(item);
        } catch (IOException e) {
            // 파일 업로드 및 저장 중 예외 발생 시 처리
            log.error("이미지 파일 업로드 및 저장에 실패했습니다.", e);
            throw new IOException ("이미지 파일 업로드 및 저장에 실패했습니다.", e);
        }
    }

    /**
     * 파일 업로드 메서드.
     *
     * @param originalFilename 업로드할 파일의 원본 이름
     * @param bytes            업로드할 파일의 바이트 배열
     * @param folderPath       파일을 저장할 폴더 경로
     * @return                 생성된 파일의 이름 (UUID를 활용하여 고유한 이름 생성)
     * @throws IOException     파일 업로드 중 발생한 입출력 예외
     */
    private String uploadFile(String originalFilename, byte[] bytes, String folderPath) throws IOException {
        try {
            // 파일의 확장자 얻기
//            String fileExtension = StringUtils.getFilenameExtension(originalFilename);

            // 파일이 저장될 전체 경로
            String fullPath = folderPath + "/";
            File folder = new File(fullPath);

            // 폴더가 존재하지 않으면 폴더 생성
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // UUID와 원본 파일 이름을 조합하여 고유한 파일 이름 생성
            String createdFileName = UUID.randomUUID().toString() + "_" + originalFilename;

            // 파일을 저장할 경로 설정
            Path path = Paths.get(fullPath + "/" + createdFileName);

            // 파일을 실제 경로에 저장
            Files.write(path, bytes);

            // 생성된 파일 이름 반환
            return createdFileName;
        } catch (IOException e) {
            // 파일 업로드 중 예외 발생 시 처리
            log.error("파일 업로드 중 예외 발생", e);
            throw new IOException("파일 업로드 중 예외 발생", e);
        }
    }


    /**
     * 이미지 파일을 표시하는 메서드.
     *
     * @param no 게시물 번호
     * @return ResponseEntity<byte[]> 이미지 파일을 포함한 HTTP 응답 엔터티
     * @throws Exception 이미지 파일 표시 중 발생한 예외
     */
    public ResponseEntity<byte[]> displayFile(Long no) throws Exception {
        log.debug("################ BoardServiceImpl.displayFile ################");
        ResponseEntity<byte[]> entity = null;

        // 게시물 번호로 이미지 파일명 조회
        Board item = repository.getOne(no);
        String fileName = item.getFileUrl();

        try {
            // 파일 확장자 추출
            String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);

            // 확장자에 따라 MediaType 설정
            MediaType mediaType = this.getMediaType(formatName);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            File file = new File(uploadPath + "/" + formatName + File.separator + fileName);

            // MediaType이 존재하면 ContentType 설정
            if (mediaType != null) {
                headers.setContentType(mediaType);
            }

            // 파일을 바이트 배열로 변환하여 ResponseEntity 생성
            entity = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            entity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return entity;
    }


    /**
     * 파일 확장자를 기반으로 MediaType을 반환하는 메서드.
     *
     * @param formatName 파일 확장자
     * @return MediaType 이미지 파일의 확장자에 해당하는 MediaType, 확장자가 일치하지 않는 경우 null 반환
     */
    private MediaType getMediaType(String formatName) {
        log.debug("################ BoardServiceImpl.getMediaType ################");

        // 파일 확장자가 존재하는 경우에만 처리
        if (formatName != null) {
            // 파일 확장자에 따라 MediaType 설정
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

        // 일치하는 확장자가 없는 경우 null 반환
        return null;
    }


    /**
     * Board 엔터티를 Board DTO로 변환하는 메서드.
     *
     * @param board 원본 Board 엔터티 객체
     * @return 변환된 Board DTO 객체
     */
    public Board mapItemToDto(Board board) {
        log.debug("################ BoardServiceImpl.mapItemToDto ################");

        // 새로운 Board DTO 객체 생성
        Board boardDto = new Board();

        // 필드 값 복사
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

        // 변환된 Board DTO 반환
        return boardDto;
    }

    /**
     * 게시물 수정 메서드
     *
     * @param item    수정할 게시물 정보를 담은 Board 객체
     * @param picture 게시물에 첨부될 이미지 파일(MultipartFile)
     * @return 수정된 게시물 정보
     * @throws IOException         파일 업로드 중 발생한 입출력 예외
     * @throws EntityNotFoundException 수정할 게시물이 존재하지 않을 때 발생하는 예외
     * @throws Exception            그 외 예외 발생 시 전파됨
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Board modify(Board item, MultipartFile picture) throws Exception {
        log.debug("################ BoardServiceImpl.modify ################");

        try {
            Optional<Board> optionalItemEntity = repository.findById(item.getNo());
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
            } else {
                if (StringUtils.isEmpty(item.getFileNm())) {
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
        } catch (IOException e) {
            // 파일 업로드 중 예외 발생 시 처리
            log.error("파일 업로드 중 예외 발생", e);
            throw new IOException("파일 업로드 중 예외 발생", e);
        } catch (Exception e) {
            // 기타 예외 처리
            log.error("게시물 수정 중 예외 발생", e);
            throw new Exception("게시물 수정 중 예외 발생", e);
        }
    }


    /**
     * 게시물 삭제 메서드.
     *
     * @param no 게시물 번호
     * @throws Exception 삭제 과정에서 발생한 예외
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void remove(Long no) throws Exception {
        log.debug("################ BoardServiceImpl.remove ################");

        try {
            // 주어진 번호로 게시물 조회
            Optional<Board> optionalBoard = repository.findById(no);

            // 게시물이 존재하는 경우에만 삭제 처리
            if (optionalBoard.isPresent()) {
                Board board = optionalBoard.get();

                // 게시물의 사용 여부를 'N'으로 설정하여 삭제 표시
                board.setUseYn("N");

                // 변경된 내용을 저장
                repository.save(board);
            }
        } catch (Exception e) {
            throw new Exception("게시물 삭제 중 오류 발생: " + e.getMessage(), e);
        }
    }


    /**
     * 게시물의 이미지 파일 경로를 반환하는 메서드.
     *
     * @param no 이미지 파일 경로를 얻을 게시물 번호
     * @return 게시물의 이미지 파일 경로
     * @throws Exception 이미지 파일 경로 획득 중 발생한 예외
     */
//    public String getPicture(Long no) throws Exception {
//        log.debug("################ BoardServiceImpl.getPicture ################");
//
//        // 주어진 번호로 게시물 조회
//        Board item = (Board) repository.getOne(no);
//
//        // 게시물의 이미지 파일 경로 반환
//        return item.getFileUrl();
//    }


    /**
     * 게시물 조회수를 업데이트하는 메서드.
     *
     * @param item 조회수를 업데이트할 게시물 객체
     * @throws Exception 조회수 업데이트 중 발생한 예외
     */
    public void updateCnt(Board item) throws Exception {
        log.debug("################ BoardServiceImpl.updateCnt ################");

        try {
            // 주어진 게시물 객체의 조회수 업데이트
            repository.save(item);
        } catch (Exception e) {
            throw new Exception("게시물 조회수 업데이트 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
