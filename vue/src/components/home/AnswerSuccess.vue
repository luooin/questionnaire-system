<template>
<div class="successContainer">
  <div class="successBox">
    <el-card class="box-card">
      <div class="successInfo">
        <img src="../../assets/img/successImg.png" alt="回答成功">
        <h2>填写问卷成功</h2>
      </div>
    </el-card>
  </div>
</div>
</template>

<script>
export default {
  name: "AnswerSuccess"
}
</script>

<style scoped lang="less">

.successContainer{
  .successBox{
    width: 50%;
    height: 80%;
    margin: 20px auto;
    text-align: center;
    .successInfo{
      width: 50%;
      margin: 10px auto;
    }
  }
}

</style>
