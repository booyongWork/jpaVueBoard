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
  name: 'BoardModifyView',
  components: { BoardModifyForm },
  props: {
    no: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const router = useRouter();
    const item = ref({
      title: '',
      content: '',
    });

    client.get(`/board/${props.no}`)
        .then(res => {
          item.value = res.data;
        })
        .catch(err => {
          alert(err.response.data.message);
          router.back();
        });

    const modifyPost = (payload) => {
      const { title, content, file ,fileNm } = payload

      const itemObject = {
        no: props.no,
        title: title,
        content: content,
        fileNm: fileNm,
      }

      const formData = new FormData()
      formData.append("file", file)
      formData.append("item", JSON.stringify(itemObject))

      client.put('/board',
          formData,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }
      )
          .then(() => {
            alert('수정되었습니다.')
            router.push({
              name: 'BoardListView',
              // params: { no: res.data.no.toString() }
            })
          })
          .catch(err => {
            alert(err.response.data.msg)
          })
    }

    return {
      item,
      modifyPost,
    }
  },
}
</script>
