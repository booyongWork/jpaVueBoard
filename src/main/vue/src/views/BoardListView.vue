<!--
파일이름 : BoardListView.vue
작 성 자 : 정부용
작 성 일 : 2024.01.29
설 명 : 게시판 목록 페이지
-->
<template>
  <div align="center">
    <h2>게시물 목록</h2>
    <a href="/register">|등록|</a>
    <board-list :items="items"/>
  </div>
</template>

<script>
import BoardList from '../components/BoardList.vue'
import client from '../modules/client.js'
import { ref, onMounted } from 'vue'

export default {
  name: 'BoardListView', // 컴포넌트의 이름을 지정

  components: { BoardList }, // BoardList 컴포넌트를 사용하기 위해 임포트

  setup() { // Composition API의 setup 함수를 사용하여 컴포넌트 로직을 정의
    const items = ref([]); // 게시물 목록을 담을 ref 변수 선언 //  반응형(reference) 데이터 // 데이터가 변경될 때 화면이 자동으로 업데이트

    onMounted (() => { // 컴포넌트가 화면에 표시 될 때 실행되는 부분 // Dom 이랑 같은 함수
      client.get('/board') // 서버로부터 게시물 목록을 가져오는 HTTP GET 요청
          .then(res => { // 요청이 성공한 경우
            items.value = res.data; // 서버로부터 받은 데이터를 items 변수에 할당
          })
          .catch(err => { // 요청이 실패한 경우
            alert(err.response.data.msg); // 에러 메시지를 알림창으로 표시
          });
    });

    return {
      items, // items 변수를 반환하여 컴포넌트에서 사용할 수 있도록 함
    };
  },
};

</script>
