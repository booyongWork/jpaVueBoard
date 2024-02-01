<!--
파일이름 : BoardRead.vue
작 성 자 : 정부용
작 성 일 : 2024.01.29
설 명 : 게시판 상세 페이지 컨포넌트
-->
<template xmlns:v-bind="http://www.w3.org/1999/xhtml">
  <div>
    <table border="1">
      <tr>
				<td>No</td>
				<td><input type="text" :value="item.no" disabled></td>
			</tr>
			<tr>
				<td>제목</td>
				<td><input type="text" :value="item.title" disabled></td>
			</tr>
			<tr>
				<td>내용</td>
				<td><input type="text" :value="item.content" disabled></td>
			</tr>
      <tr v-if="item.fileUrl">
				<td>파일 미리보기</td>
				<td>
          <img v-bind:src="fileUrl()" width="200" disabled>
         </td>
			</tr>
      <tr v-if="item.fileNm">
        <td>파일 이름</td>
        <td>
          <input type="text" :value="item.fileNm" disabled style="width: 200px;font-size: 10px; height: 30px;">
        </td>
      </tr>
		</table>
  </div>
</template>

<script>
export default {
  name: 'BoardRead', // 컴포넌트의 이름을 지정

  props: { // 컴포넌트에 전달되는 props를 정의
    item: { // item이라는 prop을 정의
      type: Object, // prop의 타입을 객체
      required: true // prop이 필수
    }
  },

  setup(props) {
    console.log(props)
    const fileUrl = () => { // fileUrl이라는 함수를 선언
      if(props.item.fileUrl) { // 전달된 item 객체의 fileUrl 속성이 존재하는지 확인
        const url = `http://localhost:8090/board/display?no=${props.item.no}&timestamp=${new Date().getTime()}`; // 동적인 파일 URL을 생성
        console.log('File URL:', url);
        return url; // 파일 URL을 반환
      }
    }

    return { // setup 함수의 반환 객체를 정의
      fileUrl, // fileUrl 함수를 반환
    }
  },
}
</script>
