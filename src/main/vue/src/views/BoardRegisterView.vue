<!--
파일이름 : BoardRegisterView.vue
작 성 자 : 정부용
작 성 일 : 2024.01.29
설 명 : 게시판 등록 페이지
-->
<template>
  <div align="center">
    <h2>게시물 등록</h2>
    <board-register-form @add-post="addPost"/>
  </div>
</template>

<script>
import BoardRegisterForm from '../components/BoardRegisterForm.vue';
import client from '../modules/client.js';
import { useRouter } from 'vue-router';

export default {
  name: 'BoardRegisterView', // 컴포넌트의 이름을 지정
  components: { BoardRegisterForm }, // BoardRegisterForm 컴포넌트를 등록
  setup() { // setup 함수 정의
    const router = useRouter(); // 라우터 객체 생성

    const addPost = (payload) => { // 새로운 게시물을 추가하는 함수
      const { title, content, file } = payload; // payload에서 제목, 내용, 파일을 추출

      const itemObject = { // 게시물 객체 생성
        title: title, // 제목 설정
        content: content, // 내용 설정
      };

      const formData = new FormData(); // FormData 객체 생성
      formData.append('file', file); // 파일 데이터 추가
      formData.append('item', JSON.stringify(itemObject)); // 게시물 객체를 문자열로 변환하여 추가

      client // 클라이언트로부터 POST 요청
          .post('/board', formData, { // '/board' 엔드포인트에 데이터 전송
            headers: { // 헤더 설정
              'Content-Type': 'multipart/form-data', // 멀티파트 폼 데이터로 설정
            },
          })
          .then((res) => { // 요청이 성공한 경우
            console.log(res); // 응답 로그 출력
            alert('등록되었습니다.'); // 성공 메시지 알림
            router.push({ // 게시판 목록 페이지로 이동
              name: 'BoardListView', // BoardListView 라우터로 이동
            });
          })
          .catch((error) => { // 요청이 실패한 경우
            console.error('에러 발생:', error); // 에러 로그 출력
            if (error.response) { // 서버 응답이 있는 경우
              console.error('서버 응답 상태 코드:', error.response.status); // 상태 코드 로그 출력
              console.error('서버 응답 데이터:', error.response.data); // 응답 데이터 로그 출력
            }
          });
    };

    return { // setup 함수의 반환 객체 정의
      addPost, // 새로운 게시물 추가 함수 반환
    };
  },
};

</script>
