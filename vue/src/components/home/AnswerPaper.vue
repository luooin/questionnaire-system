<template>
<div class="paperViewContainer">
  <div class="paperBox"
       v-loading="isLoading"
       :element-loading-text="loadingMsg">
    <div class="paperTitle">
      <h2>{{paper.paperTitle}}</h2>
    </div>
    <div class="paperIntroduce">
      <span>&ensp;&ensp;&ensp; {{paper.paperIntroduce}}</span>
    </div>
    <!--问题列表-->
    <div class="questionList">
      <el-card class="question" v-for="(question,index) in paper.questionList" :key="index">
        <!--单选题-->
        <div v-if="question.questionType===1">
          <div slot="header" class="questionHead">
            <span v-text="question.isRequired===1?'* ':''" style="color: red"></span>
            <span>{{index+1}}、{{ question.questionTitle }}</span>
          </div>
          <div v-for="(option,opIndex) in question.questionOption" :key="opIndex">
            <el-radio v-model="question.answer[0]" :label="opIndex">{{option}}</el-radio>
          </div>
        </div>
        <!--多选题-->
        <div v-if="question.questionType===2">
          <div slot="header" class="questionHead">
            <span v-text="question.isRequired===1?'* ':'' + ' '" style="color: red"></span>
            <span>{{index+1}}、{{question.questionTitle}}</span>
          </div>
          <el-checkbox-group class="question-items" v-model="question.answer">
            <el-checkbox v-for="(option,opIndex) in question.questionOption"
                         :label="opIndex" :key="opIndex">
              <span>{{option}}</span>
            </el-checkbox>
          </el-checkbox-group>
        </div>
        <!--文本题-->
        <div v-if="question.questionType===3">
          <div slot="header" class="questionHead">
            <span v-text="question.isRequired===1?'* ':'' + ' '" style="color: red"></span>
            <span>{{index+1}}、{{question.questionTitle}}</span>
          </div>
          <el-input
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 4}"
              placeholder="请输入内容"
              v-model="question.answer[0]">
          </el-input>
        </div>
      </el-card>
    </div>
    <div class="footBtn">
      <el-button type="primary" @click="uploadAnswer()">提交</el-button>
    </div>
    <div style="height: 40px"></div> <!--占位,以后可以添加个人信息-->
  </div>
</div>
</template>

<script>
export default {
  name: "AnswerPaper",
  data(){
    return{
      paperId:"",
      paper:{},
      isLoading: false,
      loadingMsg: '正在获取问卷'
    }
  },
  methods:{
    // 通过问卷id获取问卷问题
    async getPaperById(){
      this.isLoading = true;  // 查询中状态
      // 查询用户状态
      let {data:re} = await this.$http.get(`/paper/queryPState/${this.paperId}`);;
      if (re.code!==200){
        this.isLoading = false;
        return this.$message.warning(re.msg);
      }
      // 通过paperId获取paper的信息
      let {data:res} = await this.$http.get(`/paper/getPaperById/${this.paperId}`);
      // console.log(res);
      if (res.code !== 200){  // 获取问卷失败
        return this.$message.error(res.msg);
      }
      this.isLoading = false;
      this.paper = res.data;
      console.log(this.paper);
    },
    // 上传问卷填写结果
    async uploadAnswer(){
      if (this.paper.status!==1){
        return this.$message.warning("问卷未发布");
      }
      // 循环答案列表，判断是否还有没有必填的答案没有填写
      for (let i in this.paper.questionList){
        if (this.paper.questionList[i].isRequired === 1){ // 如果是必填项
          if (this.paper.questionList[i].questionType===2 && this.paper.questionList[i].answer.length===1){ // 如果是多选题
            return this.$message.warning("请填写必填项");
          }else if (this.paper.questionList[i].questionType!==2 && this.paper.questionList[i].answer[0] === null){   // 说明是单选题或者文本题
            return this.$message.warning("请填写必填项");
          }
        }
      }
      // 到这里说明问卷没有问题
      this.loadingMsg = '正在上传答案，请稍等';
      this.isLoading = true;
      let upPaper = {
        paperId:this.paperId,
        answerList:this.paper.questionList
      }
      console.log(upPaper);
      let {data:res} = await this.$http.post("/answer/addAnswer",upPaper);
      if (res.code !== 200){
        this.isLoading = false;
        this.loadingMsg = '正在获取问卷';  // 恢复到初始值
        return this.$message.error(res.msg);
      }
      this.isLoading = false;
      this.loadingMsg = '正在获取问卷';  // 恢复到初始值
      this.$message.success(res.msg);
      // 跳转到回答问卷成功页面
      await this.$router.push("/answerSuccess");
    }
  },
  created() {
    this.paperId = this.$route.params.paperId;
    // console.log(this.paperId);
    this.getPaperById(this.paperId);
  }
}
</script>

<style scoped lang="less">

.paperViewContainer{
  width: 100%;
  height: 100%;
  .paperBox{
    position: absolute;
    width: 50%;
    height: 100%;
    left: 50%;
    transform: translate(-50%);   // 向左移动自身宽度一半的距离
    .paperTitle{
      text-align: center;
    }
    .paperIntroduce{
      border-bottom: 3px solid #409EFF;
      padding-bottom: 12px;
      margin-bottom: 12px;
    }
    // 问题样式
    .question{
      margin: 10px 0;
      .questionHead{
        margin-bottom: 16px;
      }
      // 单选题和多选题设置宽度和外边距
      .el-radio, .el-checkbox{
        width: 100%;
        margin-bottom: 8px;
      }
    }

    // 底部按钮
    .footBtn{
      display: flex;   // 弹性盒子布局
      flex-direction: row;
      justify-content: center;
      align-items: center;
      height: 40px;
    }

  }


}

</style>
