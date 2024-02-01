<!--
파일이름 : BoardList.vue
작 성 자 : 정부용
작 성 일 : 2024.01.29
설 명 : 게시판 등록 페이지 컨포넌트
-->
<template>
  <div>
    <table border="1">
      <tr>
        <th align="center" width="80">No</th>
        <th align="center" width="320">제목</th>
        <th align="center" width="100">작성일시</th>
        <th align="center" width="100">작성자</th>
        <th align="center" width="100">조회</th>
        <th align="center" width="100">첨부파일</th>
      </tr>

      <!-- 리스트 예외처리 -->
      <tr v-if="!items || (Array.isArray(items) && items.length === 0)">
        <td align="center" colspan="6">
          등록된 파일이 없습니다.
        </td>
      </tr>

      <tr v-else v-for="item in items" :key="item.no">
        <td align="center">{{ item.no }}</td>
        <td align="left">
          <span @click="goToItemReadView(item.no)" style="cursor: pointer;">{{ item.title }}</span>
        </td>
        <td align="center">{{ item.regDate }}</td>
        <td align="center">{{ item.writer }}</td>
        <td align="center">{{ item.cnt }}</td>
        <td align="center" :style="{ cursor: item.fileUrl ? 'pointer' : 'default' }"
            @click="showAlert(item)">
          {{ item.fileUrl ? 'O' : '-' }}
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
export default {
  name: 'BoardList', // 컴포넌트의 이름을 지정

  props: {
    items: { // items라는 prop을 정의
      type: Array // prop의 타입을 배열로 지정
    }
  },

  methods: {
    goToItemReadView(no) { // goToItemReadView 메서드 정의
      // 라우터를 통해 BoardReadView 페이지로 이동하는 메서드
      this.$router.push({ name: 'BoardReadView', params: { no: no } });
    },

    showAlert(item) { // showAlert 메서드 정의
      if (item.fileUrl) { // item 객체에 fileUrl 속성이 있는 경우
        alert('첨부파일 다운로드 구현 예정입니다.'); // 얼럿으로 메시지 표시
      }
    }
  }
}
</script>
