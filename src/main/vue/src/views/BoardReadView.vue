<!--
파일이름 : BoardReadView.vue
작 성 자 : 정부용
작 성 일 : 2024.01.29
설 명 : 게시판 상세 페이지
-->
<template>
  <div align="center">
    <h2>게시물 상세보기</h2>
<!-- 컴포넌트 BoardRead.vue에 item 값을 렌더링-->
    <board-read v-if="item" :item="item"/>
    <p v-else>loading...</p>
    <router-link :to="{ name: 'BoardListView' }">목록|</router-link>
    <router-link :to="{ name: 'BoardModifyView', params: { no } }">편집|</router-link>
    <button @click="deletePost">삭제</button>
  </div>
</template>

<script>
import BoardRead from '../components/BoardRead.vue';
import client from '../modules/client.js';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

// Vue 컴포넌트를 정의.'BoardReadView'로 설정
export default {
  // BoardRead 컴포넌트를 BoardReadView 컴포넌트의 하위 컴포넌트로 등록
  components: { BoardRead },
  // props로 no를 정의(게시물 번호)
  props: {
    no: {
      type: String,
      required: true, // no가 필수 prop임을 설정
    },
  },
  // setup 함수를 사용하여 컴포넌트의 상태 및 기능을 설정
  setup(props) {
    // ref() 함수를 사용하여 반응형 변수 item을 생성. 초기값은 빈 객체({})
    const item = ref({});

    // useRouter() 함수를 사용하여 Vue Router의 기능을 사용할 수 있도록 함
    const router = useRouter();

    // 서버로부터 게시물 데이터를 가져오는 비동기 함수.
    client
        .get(`/board/${props.no}`) // 서버로 GET 요청
        .then((res) => { // 요청이 성공하면 실행될 콜백 함수
          item.value = res.data; // 응답 데이터를 item 변수에 할당
        })
        .catch((err) => { // 요청이 실패하면 실행될 콜백 함수
          alert(err.response.data.msg); // 오류 메시지를 얼럿 표시
        });

    // 게시물을 삭제하는 함수
    const deletePost = () => {
      const { no } = item.value; // item 객체에서 게시물 번호를 가져옴
      client.delete(`/board/${no}`) // 서버로 DELETE 요청
          .then(() => { // 요청이 성공하면 실행될 콜백 함수
            alert('삭제되었습니다.'); // 삭제가 성공했음을 알리는 메시지를 표시
            router.push({ name: 'BoardListView' }); // BoardListView 페이지로 이동
          })
          .catch(err => { // 요청이 실패하면 실행될 콜백 함수
            alert(err.response.data.message); // 오류 메시지를 얼럿 표시
          });
    };

    // item과 deletePost 함수를 반환합니다.
    return {
      item,
      deletePost,
    };
  },
};
</script>