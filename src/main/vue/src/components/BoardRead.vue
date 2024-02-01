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
          <input type="text" :value="item.fileNm" disabled style="    width: 200px;font-size: 10px; height: 30px;">
        </td>
      </tr>
<!--			<tr>-->
<!--				<td>상품설명</td>-->
<!--				<td><textarea :value="item.description" disabled></textarea></td>-->
<!--			</tr>-->
		</table>
  </div>
</template>

<script>
export default {
  name: 'BoardRead',
  props: {
    item: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    const fileUrl = () => {
      if(props.item.fileUrl) {
        const url = `http://localhost:8090/board/display?no=${props.item.no}&timestamp=${new Date().getTime()}`;
        console.log('File URL:', url);
        return url;
      }
    }

    return {
      fileUrl,
    }
  },
}
</script>
