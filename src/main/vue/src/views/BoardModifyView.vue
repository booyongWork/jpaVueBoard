<!--
파일이름 : BoardModifyView.vue
작 성 자 : 정부용
작 성 일 : 2024.01.29
설 명 : 게시판 수정 페이지
-->
<template>
  <div align="center">
    <h2>게시물 수정</h2>
    <board-modify-form v-if="item"
                       :item="item"
                       :no="no"
                       @modify-post="modifyPost" />
    <p v-else>loading...</p>
  </div>
</template>

<script>
import BoardModifyForm from '../components/BoardModifyForm.vue'
import client from '../modules/client.js'
import { useRouter } from 'vue-router';
import { ref } from 'vue'

export default {
  name: 'BoardModifyView', // 컴포넌트의 이름을 지정

  components: { BoardModifyForm }, // BoardModifyForm 컴포넌트를 BoardModifyView 컴포넌트의 하위 컴포넌트로 등록

  props: { // 컴포넌트에 전달되는 props를 정의
    no: { // no라는 prop을 정의
      type: String, // prop의 타입을 문자열로 지정
      required: true // prop이 필수
    }
  },

  setup(props) { // setup 함수를 사용하여 컴포넌트 초기화 및 데이터 처리
    const router = useRouter(); // 라우터 객체 생성(이전 페이지 이동을 위해)
    const item = ref({ // ref 함수를 사용하여 반응형 데이터 생성 및 초기값 설정
      title: '', // title 속성 초기값 설정
      content: '', // content 속성 초기값 설정
    });

    client.get(`/board/${props.no}`) // 서버에서 게시물 정보를 가져오는 요청
        .then(res => {
          item.value = res.data; // 가져온 데이터를 item에 설정
        })
        .catch(err => {
          alert(err.response.data.message); // 오류 발생 시 메시지 표시
          router.back(); // 이전 페이지로 이동
        });

    const modifyPost = (payload) => { // modifyPost 함수 정의
      const { title, content, file, fileNm } = payload; // payload에서 필요한 데이터 추출

      const itemObject = { // 게시물 정보 객체 생성
        no: props.no, // 게시물 번호 설정
        title: title, // 제목 설정
        content: content, // 내용 설정
        fileNm: fileNm, // 파일 이름 설정
      };

      const formData = new FormData(); // FormData 객체 생성
      formData.append("file", file); // 파일 데이터 추가
      formData.append("item", JSON.stringify(itemObject)); // 게시물 정보 추가

      client.put('/board', // 게시물 수정 요청
          formData, // FormData 객체 전달
          {
            headers: {
              'Content-Type': 'multipart/form-data' // 헤더 설정
            }
          }
      )
          .then(() => {
            alert('수정되었습니다.'); // 성공 메시지 표시
            router.push({ // 게시물 목록 페이지로 이동
              name: 'BoardListView',
            });
          })
          .catch(err => {
            alert(err.response.data.msg); // 오류 메시지 표시
          });
    };

    return { // setup 함수의 반환 객체 정의
      item, // item 객체 반환
      modifyPost, // modifyPost 함수 반환
    };
  },
};
</script>
