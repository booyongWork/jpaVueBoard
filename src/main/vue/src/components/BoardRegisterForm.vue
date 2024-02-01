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
import { ref } from 'vue' // Vue에서 ref 함수를 가져옴

export default {
  name: 'BoardRegisterForm', // 컴포넌트의 이름을 지정
  emits: ['add-post'], // 부모 컴포넌트로 이벤트를 발생시키는 명시적인 이벤트 선언
  setup(props, context) { // setup 함수 정의
    const title = ref('') // 제목을 저장하는 ref 변수 생성, 초기값은 빈 문자열
    const content = ref('') // 내용을 저장하는 ref 변수 생성, 초기값은 빈 문자열
    const file = ref('') // 파일을 저장하는 ref 변수 생성, 초기값은 빈 문자열

    const fireAddPost = async () => { // 새로운 게시물을 추가하는 함수
      if (!title.value) { // 제목이 입력되지 않은 경우
        alert('제목은 필수 입력 항목입니다.'); // 경고 메시지 표시
        return;
      }
      if (!content.value) { // 내용이 입력되지 않은 경우
        alert('내용은 필수 입력 항목입니다.'); // 경고 메시지 표시
        return;
      }
      if (file.value && file.value.size > 5 * 1024 * 1024) { // 파일이 업로드되었고 파일 크기가 5MB를 초과하는 경우
        alert('파일 크기는 5MB 이하로 업로드해주세요.'); // 경고 메시지 표시
        return;
      }
      console.log('title:', title.value); // 제목을 콘솔에 출력
      console.log('content:', content.value); // 내용을 콘솔에 출력
      console.log('file:', file.value); // 파일을 콘솔에 출력

      await context.emit('add-post', { // add-post 이벤트 발생
        title: title.value, // 제목 전달
        content: content.value, // 내용 전달
        file: file.value // 파일 전달
      });
    }

    const changeFile = (evt) => { // 파일 입력 값이 변경될 때 실행되는 함수
      file.value = evt.target.files[0] // 파일 데이터 업데이트
    }

    return { // setup 함수의 반환 객체 정의
      title, // 제목 반환
      content, // 내용 반환
      file, // 파일 반환
      fireAddPost, // 새로운 게시물 추가 함수 반환
      changeFile, // 파일 입력 값 변경 함수 반환
    }
  },
}

</script>
