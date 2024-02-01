<!--
파일이름 : BoardModifyForm.vue
작 성 자 : 정부용
작 성 일 : 2024.01.29
설 명 : 게시판 수정 페이지 컨포넌트
-->
<template>
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
  name: 'BoardModifyForm', // 컴포넌트의 이름을 지정
  props: { // 컴포넌트에 전달되는 props를 정의
    item: { // item이라는 prop을 정의
      type: Object, // prop의 타입을 객체로 설정
      required: true // prop이 필수로 전달되어야 함
    },
    no: { // no라는 prop을 정의
      type: String, // prop의 타입을 문자열로 설정
      required: true // prop이 필수로 전달되어야 함
    },
  },
  emits: ['modify-post', 'delete-file'], // 부모 컴포넌트로 이벤트를 발생시키는 명시적인 이벤트 선언
  setup(props, context) { // setup 함수 정의
    const file = ref(null); // 파일 데이터를 저장하는 ref 변수 생성, 초기값은 null로 설정
    const localItem = ref({ ...props.item }); // props로 전달된 item을 복제하여 수정할 데이터를 관리하는 ref 변수 생성
    const showDeleteButton = ref(true); // 파일 삭제 버튼을 보여줄지 여부를 결정하는 ref 변수 생성, 초기값은 true로 설정
    const showFileInput = ref(true); // 파일 입력란을 보여줄지 여부를 결정하는 ref 변수 생성, 초기값은 true로 설정

    const submitForm = () => { // 폼 제출 시 실행되는 함수
      if (!localItem.value.title) { // 제목이 입력되지 않은 경우
        alert('제목은 필수 입력 항목입니다.'); // 경고 메시지 표시
        return;
      }

      if (!localItem.value.content) { // 내용이 입력되지 않은 경우
        alert('내용은 필수 입력 항목입니다.'); // 경고 메시지 표시
        return;
      }
      if (file.value && file.value.size > 5 * 1024 * 1024) { // 파일이 업로드되었고 파일 크기가 5MB를 초과하는 경우
        alert('파일 크기는 5MB 이하로 업로드해주세요.'); // 경고 메시지 표시
        return;
      }
      console.log('localItem:', localItem.value); // 수정된 데이터를 콘솔에 출력
      context.emit('modify-post', { // modify-post 이벤트 발생
        title: localItem.value.title, // 수정된 제목 전달
        content: localItem.value.content, // 수정된 내용 전달
        file: file.value, // 수정된 파일 전달
        fileNm: localItem.value.fileNm // 파일 이름 전달
      });
    };

    const changeFile = (evt) => { // 파일 입력 값이 변경될 때 실행되는 함수
      file.value = evt.target.files[0]; // 파일 데이터 업데이트
    }

    const fileUrl = () => { // 파일의 URL을 생성하는 함수
      if (props.item.fileUrl) { // props로 전달된 item에 fileUrl이 있는 경우
        return `http://localhost:8090/board/display?no=${props.item.no}&timestamp=${new Date().getTime()}`; // 파일 URL 반환
      }
    }

    const deleteFile = () => { // 파일 삭제 시 실행되는 함수
      if (confirm('파일을 삭제하시겠습니까?')) { // 사용자에게 확인 메시지 표시
        context.emit('delete-file'); // delete-file 이벤트 발생
        localItem.value.fileUrl = null; // 파일 URL 초기화
        localItem.value.fileNm = null; // 파일 이름 초기화
        showDeleteButton.value = false; // 파일 삭제 버튼 숨김
        showFileInput.value = false; // 파일 입력란 숨김
      }
    };

    watch(() => props.item, () => { // props.item이 변경될 때마다 실행되는 watch 함수 정의
      localItem.value = { ...props.item }; // localItem을 props.item으로 업데이트
    });

    return { // setup 함수의 반환 객체 정의
      localItem, // 수정할 데이터 반환
      showDeleteButton, // 파일 삭제 버튼 표시 여부 반환
      showFileInput, // 파일 입력란 표시 여부 반환
      submitForm, // 폼 제출 함수 반환
      changeFile, // 파일 입력 값 변경 함수 반환
      fileUrl, // 파일 URL 생성 함수 반환
      deleteFile, // 파일 삭제 함수 반환
    }
  },
}

</script>
