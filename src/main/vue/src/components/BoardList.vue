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
  name: 'BoardList',
  props: {
    items: {
      type: Array
    }
  },
  methods: {
    // 사용자 정의 메서드로 라우터를 변경하는 함수
    goToItemReadView(no) {
      // 라우터를 변경하고 ItemReadView로 이동
      // console.log(`Navigating to ItemReadView with itemId: ${itemId}`);
      this.$router.push({ name: 'BoardReadView', params: { no: no } });
    },
    showAlert(item) {
      // 'O'를 클릭할 때 얼럿을 띄우는 로직 추가
      if (item.fileUrl) {
        alert('첨부파일 다운로드 구현 예정입니다.');
      }
    }
  }
}
</script>
