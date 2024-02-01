<template xmlns:v-bind="http://www.w3.org/1999/xhtml">
  <form @submit.prevent="submitForm">
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
<!--      <tr v-if="item.fileUrl">-->
<!--        <td>미리보기</td>-->
<!--        <td><img v-bind:src="fileUrl()" width="200"></td>-->
<!--      </tr>-->
      <tr v-if="item.fileNm">
        <td>파일 이름</td>
        <td>
          <input v-if="showFileInput" type="text" :value="localItem.fileNm" disabled style="width: 200px; font-size: 10px; height: 30px;">
          <button v-if="showDeleteButton" type="button" @click="deleteFile" style="color: #181818; margin: 10px;">x</button>
        </td>
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
  emits: ['modify-post', 'delete-file'],
  setup(props, context) {
    const file = ref(null); // 초기값을 null로 설정
    const localItem = ref({ ...props.item });
    const showDeleteButton = ref(true); // 초기값을 true로 설정
    const showFileInput = ref(true); // 초기값을 true로 설정

    const submitForm = () => {
      if (!localItem.value.title) {
        alert('제목은 필수 입력 항목입니다.');
        return;
      }

      if (!localItem.value.content) {
        alert('내용은 필수 입력 항목입니다.');
        return;
      }
      if (file.value && file.value.size > 5 * 1024 * 1024) {
        alert('파일 크기는 5MB 이하로 업로드해주세요.');
        return;
      }
      console.log('localItem:', localItem.value);
      context.emit('modify-post', {
        title: localItem.value.title,
        content: localItem.value.content,
        file: file.value,
        fileNm : localItem.value.fileNm
      });
    };

    const changeFile = (evt) => {
      file.value = evt.target.files[0]
    }

    const fileUrl = () => {
      if (props.item.fileUrl) {
        return `http://localhost:8090/board/display?no=${props.item.no}&timestamp=${new Date().getTime()}`;
      }
    }

    const deleteFile = () => {
      if (confirm('파일을 삭제하시겠습니까?')) {
        context.emit('delete-file');
        localItem.value.fileUrl = null;
        localItem.value.fileNm = null;
        showDeleteButton.value = false; // 버튼을 숨김
        showFileInput.value = false; // 파일 입력란을 숨김
      }
    };

    watch(() => props.item, () => {
      localItem.value = { ...props.item };
    });

    return {
      localItem,
      showDeleteButton, // 변수 반환
      showFileInput, // 변수 반환
      submitForm,
      changeFile,
      fileUrl,
      deleteFile,
    }
  },
}
</script>
