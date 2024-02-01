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
  name: 'BoardListView',
  components: { BoardList },
  setup() {
    const items = ref([])

    onMounted (() => {
      client.get('/board')
          .then(res => {
            items.value = res.data
          })
          .catch(err => {
            alert(err.response.data.msg)
          })
    })

    return {
      items,
    }
  },
}
</script>
