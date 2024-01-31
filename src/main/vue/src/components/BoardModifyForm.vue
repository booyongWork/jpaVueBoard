<template xmlns:v-bind="http://www.w3.org/1999/xhtml">
  <form @submit.prevent="fireModifyPost">
    <table border="1">
      <tr>
        <td>번호</td>
        <td><input :value="localItem.no" type="text" disabled></td>
      </tr>
      <tr>
        <td>제목</td>
        <td><input v-model="localItem.title"></td>
      </tr>
      <tr>
        <td>내용</td>
        <td><input v-model="localItem.content"></td>
      </tr>
      <tr>
        <td>파일</td>
        <td><input type="file" @change="changeFile($event)" /></td>
      </tr>
      <tr v-if="item.pictureUrl">
        <td>미리보기</td>
        <td><img v-bind:src="pictureUrl()" width="200"></td>
      </tr>
    </table>

    <div>
      <button type="submit">수정|</button>
      <router-link :to="{ name: 'BoardReadView', params: { no: no } }">취소</router-link>
    </div>
  </form>
</template>


<script>
import { ref, watch } from 'vue';

export default {
  name: 'BoardModifyForm',
  props: {
    item: {
      type: Object,
      required: true
    },
    no: {
      type: String,
      required: true
    },
  },
  emits: ['modify-post'],
  setup(props, context) {
    const file = ref('')
    const localItem = ref({ ...props.item });

    const fireModifyPost = () => {
      console.log('localItem:', localItem.value);
      context.emit('modify-post', {
        title: localItem.value.title,
        content: localItem.value.content,
        file: file.value,
      });
    };

    const changeFile = (evt) => {
      file.value = evt.target.files[0]
    }

    const pictureUrl = () => {
      if (props.item.pictureUrl) {
        return `http://localhost:8090/board/display?no=${props.item.no}&timestamp=${new Date().getTime()}`;
      }
    }

    watch(() => props.item, () => {
      localItem.value = { ...props.item };
    });

    return {
      localItem,
      fireModifyPost,
      changeFile,
      pictureUrl,
    }
  },
}
</script>
