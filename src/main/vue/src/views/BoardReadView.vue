<!--
파일이름 : BoardReadView.vue
작 성 자 : 정부용
작 성 일 : 2024.01.29
설 명 : 게시판 상세 페이지
-->
<template>
  <div align="center">
    <h2>게시물 상세보기</h2>
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

export default {
  name: 'BoardReadView',
  components: { BoardRead },
  props: {
    no: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const item = ref({});
    const router = useRouter();

    client
        .get(`/board/${props.no}`)
        .then((res) => {
          item.value = res.data;
        })
        .catch((err) => {
          alert(err.response.data.msg);
        })
    const deletePost = () => {
      const { no } = item.value
      client.delete(`/board/${no}`)
          .then(() => {
            alert('삭제되었습니다.')
            router.push({name: 'BoardListView'})
          })
          .catch(err => {
            alert(err.response.data.message)
          })
    }

    return {
      item,
      deletePost
    };
  },
};
</script>