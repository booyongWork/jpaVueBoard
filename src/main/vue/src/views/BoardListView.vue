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
