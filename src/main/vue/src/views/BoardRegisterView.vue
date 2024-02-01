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
  name: 'BoardRegisterView',
  components: { BoardRegisterForm },
  setup() {
    const router = useRouter();
    const addPost = (payload) => {
      const { title, content, file } = payload;

      const itemObject = {
        title: title,
        content: content,
      };

      const formData = new FormData();
      formData.append('file', file);
      formData.append('item', JSON.stringify(itemObject));

      client
          .post('/board', formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          })
          .then((res) => {
            console.log(res);
            alert('등록되었습니다.');
            router.push({
              name: 'BoardListView',
            });
          })
          .catch((error) => {
            console.error('에러 발생:', error);
            if (error.response) {
              console.error('서버 응답 상태 코드:', error.response.status);
              console.error('서버 응답 데이터:', error.response.data);
            }
          });
    };
    return {
      addPost,
    };
  },
};
</script>
