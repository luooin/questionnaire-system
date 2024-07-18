<template>
  <el-container class="home_container">
    <el-header>
      <Head><title></title></Head>
    </el-header>

    <!--主页面窗口-->
    <el-container>
      <!--侧边栏-->
      <el-aside width="200px">
        <div class="addPaper">
          <el-button type="primary" @click="addPaperDialogClose(true)">新建问卷</el-button>
        </div>
        <el-menu
            background-color="rgba(255,137,28,0.5)"
            text-color="#fff"
            active-text-color="#ffd04b"
            :default-active="savePath"
            router>  <!--开启路由导航跳转-->
          <el-menu-item index="myPaper"
                        @click="changePath('/myPaper')">
            <i class="el-icon-document-checked"></i>
            <span slot="title">我的问卷</span>
          </el-menu-item>
          <el-menu-item index="recycleBin" @click="changePath('/recycleBin')">
            <i class="el-icon-delete"></i>
            <span slot="title">回收站</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!--主要显示内容-->
      <el-main>
        <el-card class="box-card">
          <router-view></router-view>
        </el-card>
      </el-main>

    </el-container>
    <!--添加问卷对话框-->
    <el-dialog
        :visible.sync="paperAddSwitch"
        width="50%"
        class="addPaperDialog">
      <!--通过插槽自定义对话框标题-->
      <template slot="title">
        <div>
          <el-input
              class="paperTitleStyle"
              v-model="paperTitle">
          </el-input>
          <el-divider></el-divider>
        </div>
      </template>
      <div v-for="(p,index) in questionList" :key="index">
        <!--单选题-->
        <div class="question" v-if="p.questionType===1">
          <!--问题标题-->
          <div>
            <span>{{index+1}}、</span>
            <el-input class="pTitle"
                      v-model="p.questionTitle"></el-input>
          </div>
          <!--问题内容-->
          <div>
            <ul class="optionUl">
              <li class="radioLi"
                  v-for="(option,opIndex) in p.questionOption"
                  :key="opIndex"
                  @mouseenter="showOBtn($event)"
                  @mouseleave="leverOBtn($event)">
                <span>{{numToChar(opIndex)}}、</span>
                <el-input
                    class="optionInput"
                    v-model="p.questionOption[opIndex]">
                </el-input>
                <p class="optionBtn">
                  <el-link type="success" @click="addOption(index,opIndex)">添加</el-link>
                  <el-link type="primary" @click="delOption(index,opIndex)">删除</el-link>
                </p>
              </li>
            </ul>
          </div>
          <div class="questionBtn">
            <el-link type="primary" @click="removeQuestion(index)">删除问题</el-link>
          </div>
        </div>
        <!--多选题-->
        <div class="question" v-if="p.questionType===2">
          <!--问题标题-->
          <div>
            <span>{{index+1}}、</span>
            <el-input class="pTitle"
                      v-model="p.questionTitle"></el-input>
          </div>
          <!--问题内容-->
          <div>
            <ul class="optionUl">
              <li class="radioLi"
                  v-for="(option,opIndex) in p.questionOption"
                  :key="opIndex"
                  @mouseenter="showOBtn($event)"
                  @mouseleave="leverOBtn($event)">
                <span>{{numToChar(opIndex)}}、</span>
                <el-input
                    class="optionInput"
                    v-model="p.questionOption[opIndex]">
                </el-input>
                <p class="optionBtn">
                  <el-link type="success" @click="addOption(index,opIndex)">添加</el-link>
                  <el-link type="primary" @click="delOption(index,opIndex)">删除</el-link>
                </p>
              </li>
            </ul>
          </div>
          <div class="questionBtn">
            <el-link type="primary" @click="removeQuestion(index)">删除问题</el-link>
          </div>
        </div>
        <!--文本题-->
        <div class="question" v-if="p.questionType===3">
          <!--问题内容-->
          <div>
            <span>{{index+1}}、文本题</span>
            <el-input
                type="textarea"
                placeholder="请输入问题"
                autosize
                v-model="p.questionTitle"
                rows="3">
            </el-input>
          </div>
          <div class="questionBtn">
            <el-link type="primary" @click="removeQuestion(index)">删除问题</el-link>
          </div>
        </div>
      </div>
      <div class="SelectOptions">
        <el-button type="primary" @click="addProblem(1)">单选题</el-button>
        <el-button type="primary" @click="addProblem(2)">多选题</el-button>
        <el-button type="primary" @click="addProblem(3)">文本题</el-button>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="addPaperDialogClose(false)">取消</el-button>
        <el-button @click="addPaperDialogClose(false); savePaper($event)">保存问卷</el-button>
        <el-button type="primary" @click="addPaperDialogClose(false)">发布问卷</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
// 问题模板
import Head from "@/components/home/Head";
const questionTemplate = {
  radio: {
    questionType: 1,
    questionTitle: "单选题",      // 问题
    questionOption: ["选项1","选项2"]      // 选项
  },
  checkbox: {
    questionType: 2,
    questionTitle: "多选题",
    questionOption: ["选项1","选项2"]
  },
  textarea: {
    questionType: 3,
    questionTitle: "",  // 文本题
    questionOption: []
  }
}
export default {
  name: "Home",
  components: {Head},
  data(){
    return{
      // 保存激活链接，用来显示菜单高亮
      savePath:"",
      oBtnIsShow:false,  // 问题选项列表后面的选项是否显示
    }
  },
  computed:{
    // 添加问卷对话框开关
    paperAddSwitch:{
      get(){
        return this.$store.state.paperAddSwitch;
      },
      set(value){
        this.$store.state.paperAddSwitch = value;
      }
    },
    // 问卷标题
    paperTitle:{
      get(){
        return this.$store.state.paperTitle;
      },
      set(value) {
        this.$store.state.paperTitle = value;
      }
    },
    // 问题列表
    questionList:{
      get(){
        return this.$store.state.questionList;
      },
      set(value){
        this.$store.state.questionList = value;
      }
    }
  },
  methods:{
    // 将数字转化为对应的字母
    numToChar(item){
      let num = ''
      num = String.fromCharCode(Number(item)+65)
      return num
    },
    // 保存左边菜单的激活状态
    changePath(path){
      window.sessionStorage.setItem("savePath",path);
    },
    // 添加问题
    addProblem(value){
      if (value===1){
        // console.log("添加单选题");
        let tatleTemplate = JSON.parse(JSON.stringify(questionTemplate.radio));  // 先将值转换一下，不然都指向的是同一个值
        this.questionList.push(tatleTemplate);
        // 弄一个单选题的模板出来
      }else if (value===2){
        // console.log("添加多选题");
        let template = JSON.parse(JSON.stringify(questionTemplate.checkbox));  // 先将值转换一下，不然都指向的是同一个值
        this.questionList.push(template);
      }else if (value===3){
        // console.log("添加文本题");
        let template = JSON.parse(JSON.stringify(questionTemplate.textarea));  // 先将值转换一下，不然都指向的是同一个值
        this.questionList.push(template);
      }
      // console.log(this.questionList);
    },
    // 控制对话框的显示或者关闭
    addPaperDialogClose(flag){
      this.paperAddSwitch = flag;
    },
    // 向后端提交问题列表,新增一个问卷
    async savePaper(){
      // 创建一个变量，封装想要传递的参数
      const paper = {
        paperTitle: this.paperTitle,
        questionList: this.questionList,
        paperId: this.$store.state.paperId
      }
      // 向后端发起请求，将用户创建的问题发送给后端
      let {data: res} = await this.$http.post('/paper/addPaper',paper);
      if (res.code !== 200){
        return this.$message.error(res.msg);
      }
      this.$message.success(res.msg);
      // 刷新一下问卷列表
      await this.$store.dispatch("getMyPaper");
    },

    // 删除某一个问题
    removeQuestion(index){
      // 删除问题列表中对应的下标的元素
      this.$delete(this.questionList,index);
    },
    // 隐藏选项后的按钮
    showOBtn(e){
      e.target.querySelector(".optionBtn").style.visibility = "visible"  // 获取当前子元素中对应的元素，取消隐藏设置
    },
    // 隐藏选项后的按钮
    leverOBtn(e){
      e.target.querySelector(".optionBtn").style.visibility = "hidden"  // 获取当前子元素中对应的元素，并设置为隐藏
    },
    // 删除选项
    delOption(index,opIndex){
      // 删除对应的选项
      this.$delete(this.questionList[index].questionOption,opIndex); // 删除对应的选项
    },
    // 添加一个新的选项
    addOption(index,opIndex){
      let optionList = this.questionList[index].questionOption;
      optionList.push("选项"+(optionList.length+1));  // 往选项列表中添加一个值
    }
  },
  // 创建完成页面执行的钩子函数
  created() {
    this.savePath = window.sessionStorage.getItem("savePath");
  }
}
</script>

<style scoped lang="less">
.home_container{
  width: 100%;
  height: 100%;
}
// 头部组件
.el-header{
  padding: 0;
}

// 侧边栏组件
.el-aside{
  background-color: #698797;
}
.el-menu{
  border-right:none;
  position: relative;
}
.addPaper{
  margin: 10px;
  text-align:center;
}

.el-main{
  overflow: hidden;
  // 用外部大卡片填满文职
  .box-card{
    width: 100%;
    height: 100%;
  }
}

// 弹出对话框样式
.addPaperDialog{
  padding: 20px 10px;
  // 样式穿透强行修改对话框中的内边距
  ::v-deep .el-dialog__body{
    padding: 10px 20px;
  }
  // 修改对话框标题输入框的样式
  .paperTitleStyle{
    font-size: 20px;
    font-weight: bold;
  }
  // 分割线样式,将上下边距缩小
  .el-divider{
    margin: 2px;
  }
  // 题目内容框
  .question{
    border: 1px solid black;
    margin-bottom: 10px;
    padding: 0 10px 0 10px;
    position: relative;
    // 题目标题
    .pTitle{
      width: 50%;
    }
    // 选项列表
    .optionUl{
      margin-top: 0;
      margin-bottom: 0;
      position: relative;
      // 单选题选项
      .radioLi{
        width: 100%;
        list-style: none;
        // 输入框宽度
        .optionInput{
          width: 85%;
        }
        // 选项后方的选项
        .optionBtn{
          display: inline-block;
          visibility: hidden;
          .el-link{
            margin-right: 5px;
          }
        }
      }
    }
    // 右下角按钮区域
    .questionBtn{
      display: flex;
      flex-direction: row;
      justify-content: right;
      align-items: center;
    }
  }
  // 题目选择框
  .SelectOptions{
    display: flex;  // 弹性盒子
    flex-direction: row;
    justify-content: center;
    align-items: center;
  }
  ::v-deep .el-input__inner{  // 样式穿透,将问题输入框的边框删除
    border: 0;
  }
}

</style>
