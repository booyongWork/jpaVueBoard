<!--
파일이름 : BoardRegisterForm.vue
작 성 자 : 정부용
작 성 일 : 2024.01.29
설 명 : 게시판 등록 페이지 컨포넌트
-->
<template>
  <form @submit.prevent="fireAddPost">
    <table border="1">
      <tr>
        <td>제목</td>
        <td><input type="text" v-model="title"></td>
      </tr>
      <tr>
        <td>내용</td>
        <td><input type="text" v-model="content"></td>
      </tr>
      <tr>
				<td>파일</td>
				<td><input type="file" @change="changeFile($event)"/></td>
			</tr>
		</table>

    <div>
      <button type="submit">등록|</button>
      <router-link :to="{ name: 'BoardListView' }">취소</router-link>
    </div>
  </form>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'BoardRegisterForm',
  emits: ['add-post'],
  setup(props, context) {
    const title = ref('')
    const content = ref('')
    const file = ref('')

    const fireAddPost = async () => {
      if (!title.value) {
        alert('제목은 필수 입력 항목입니다.');
        return;
      }
      if (!content.value) {
        alert('내용은 필수 입력 항목입니다.');
        return;
      }
      if (file.value && file.value.size > 5 * 1024 * 1024) {
        alert('파일 크기는 5MB 이하로 업로드해주세요.');
        return;
      }
      console.log('title:', title.value);
      console.log('content:', content.value);
      console.log('file:', file.value);

      await context.emit('add-post', {
        title: title.value,
        content: content.value,
        file: file.value
      });
    }

    const changeFile = (evt) => {
      file.value = evt.target.files[0]
    }

    return {
      title,
      content,
      file,
      fireAddPost,
      changeFile,
    }
  },
}
</script>
