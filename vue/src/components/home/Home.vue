<template>
  <el-container class="home_container">
    <!--头部组件-->
    <el-header style="padding: 0">
      <Head></Head>
    </el-header>
    <el-main style="padding: 0">
      <div class="mainContainer">
        <el-row>
          <!--左侧视图-->
          <el-col :span="6" class="left">
            <!--问卷操作栏-->
            <div class="control">
              <el-tooltip class="item" effect="dark" content="添加问卷" placement="bottom">
                <el-button icon="el-icon-plus" type="text" @click="addPaperDialogClose(true)"></el-button>
              </el-tooltip>
            </div>
            <div class="paperList"
                 v-loading="paperListIsLoading"
                 element-loading-text="正在拼命获取问卷">
              <!--创建的问卷列表-->
              <el-menu
                  default-active="2"
                  class="el-menu-vertical-demo"
                  :default-active="saveIndex.toString()"
              >
                <el-menu-item
                    v-for="(paper,index) in myPaper" :key="index"
                    @click="changePaperView(index)"
                    :index="index.toString()"
                >
                  <i class="el-icon-document" style="margin-right: 5px"></i>
                  <span slot="title">
                    {{paper.paperTitle}}
                    <span v-if="paper.paperState===1" style="color: green;font-size: 10px">
                      <i class="el-icon-paperclip" style="font-size: 10px;width:10px;margin-right: 0"></i>
                      已发布
                    </span>
                    <span v-if="paper.paperState===0" style="color: blue;font-size: 10px;">
                      <i class="el-icon-paperclip" style="font-size: 13px;width:10px;margin-right: 0"></i>
                      未发布
                    </span>
                    <span v-if="paper.paperState===-1" style="color: red;font-size: 10px;">
                      <i class="el-icon-paperclip" style="font-size: 13px;width:10px;margin-right: 0"></i>
                      已封禁
                    </span>
                  </span>
                  <!--问卷操作-->
                  <div class="paperSet" v-show="saveIndex===index">
                    <el-dropdown @command="paperSet" trigger="click">
                      <span class="operationName">
                        操作<i class="el-icon-arrow-down el-icon--right"></i>
                      </span>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item icon="el-icon-video-play" v-if="paper.paperState===0" :command="paperSetParam(index,'issuePaper')">发布问卷</el-dropdown-item>
                        <el-dropdown-item icon="el-icon-video-pause" v-if="paper.paperState===1" :command="paperSetParam(index,'cancelIssuePaper')">取消发布</el-dropdown-item>
                        <el-dropdown-item icon="el-icon-view" :command="paperSetParam(index,'toPaperView')">模拟答卷</el-dropdown-item>
                        <el-dropdown-item icon="el-icon-edit" :command="paperSetParam(index,'updatePaper')">修改问卷</el-dropdown-item>
                        <el-dropdown-item icon="el-icon-s-promotion" :command="paperSetParam(index,'shareDialogSwitch')">分享问卷</el-dropdown-item>
                        <el-dropdown-item icon="el-icon-delete" :command="paperSetParam(index,'delPaper')">删除</el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>
                </el-menu-item>
              </el-menu>
            </div>
          </el-col>
          <!--右侧视图-->
          <el-col :span="18" class="right">
            <el-tabs type="border-card"
                     @tab-click="changeAnswerCensus"
                     v-model="tabName">  <!--绑定标签为tabName-->
              <!--问卷预览-->
              <el-tab-pane name="paperView" label="问卷预览"
                           v-loading="isLoading"
                           element-loading-text="查询中，请稍等">
                <!--如果没有选择左侧的问卷-->
                <div v-if="JSON.stringify(this.nowPaper)==='{}'">
                  <el-empty description="选择左侧问卷"></el-empty>
                </div>
                <!--如果选择了左侧的问卷-->
                <div class="paperTitle">
                  <h1>{{nowPaper.paperTitle}}</h1>
                </div>
                <div class="questionList">
                  <el-card class="question" v-for="(question,index) in nowPaper.questionList" :key="index">
                    <!--单选题-->
                    <div v-if="question.questionType===1">
                      <div slot="header" class="questionHead">
                        <span>{{index+1}}、{{question.questionTitle}}</span>
<!--                        <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>-->
                      </div>
                      <el-radio-group v-model="question.answer[0]" class="question-items">
                        <el-radio v-for="(option,opIndex) in question.questionOption"
                                  :label="opIndex" :key="opIndex">
                          <span>{{option}}</span>
                        </el-radio>
                      </el-radio-group>
                    </div>
                    <!--多选题-->
                    <div v-if="question.questionType===2">
                      <div slot="header" class="questionHead">
                        <span>{{index+1}}、{{question.questionTitle}}</span>
<!--                        <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>-->
                      </div>
                      <el-checkbox-group v-model="question.answer" class="question-items">
                        <el-checkbox v-for="(option,opIndex) in question.questionOption"
                                     :label="opIndex" :key="opIndex">
                          <span>{{option}}</span>
                        </el-checkbox>
                      </el-checkbox-group>
                    </div>
                    <!--文本题-->
                    <div v-if="question.questionType===3">
                      <div slot="header" class="questionHead">
                        <span>{{index+1}}、{{question.questionTitle}}</span>
<!--                        <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>-->
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
              </el-tab-pane>
              <!--答卷管理-->
              <el-tab-pane name="answerManage" label="答卷管理"
                           v-loading="isLoading"
                           element-loading-text="查询中，请稍等">
                <!--如果没有选择左侧的问卷-->
                <div v-if="JSON.stringify(this.nowPaper)==='{}'">
                  <el-empty description="选择左侧问卷"></el-empty>
                </div>
                <div class="answerContainer">
                  <div v-if="JSON.stringify(this.nowPaper)!=='{}'">
                    <div class="answerExport">
                      <el-button size="small" @click="exportExcel"
                                 :loading="exportBtnIsLoading" type="primary" plain>{{exportBtnText}}</el-button>
                    </div>
                  </div>
                  <!--回答问卷的内容-->
                  <div class="optionAnswer" v-for="(answer,index) in answerList">
                    <!--单选题和多选题-->
                    <el-card class="answerCard" v-if="answer.questionType===1 || answer.questionType===2">
                      <div slot="header" class="clearfix">
                        <span>{{index+1}}、{{answer.questionTitle}}</span>
                      </div>
                      <el-table
                          :data="answer.answerCensusesList"
                          style="width: 100%">
                        <el-table-column prop="optionName" label="选项" width="180"></el-table-column>
                        <el-table-column prop="num" label="数量" width="180"></el-table-column>
                        <el-table-column prop="percentage" label="百分比">
                        </el-table-column>
                      </el-table>
                      <div class="censusBtns">
                        <el-button size="small" @click="changeCensus(index,1)" type="primary" plain>柱状图</el-button>
                        <el-button size="small" @click="changeCensus(index,2)" type="primary" plain>饼状图</el-button>
                        <el-button size="small" @click="changeCensus(index,3)" type="primary" plain>环形图</el-button>
                        <el-button size="small" @click="changeCensus(index,0)" type="primary" plain>隐藏图表</el-button>
                      </div>
                      <!--柱状图-->
                      <div :ref="`histogram${index}`"
                           class="answerCensus"
                           v-show="censusList[index]===1">
                      </div>
                      <!--饼状图-->
                      <div :ref="`pieChart${index}`"
                           class="answerCensus"
                           v-show="censusList[index]===2">
                      </div>
                      <!--环形图-->
                      <div :ref="`annular${index}`"
                           class="answerCensus"
                           v-show="censusList[index]===3">
                      </div>
                    </el-card>
                    <!--文本题-->
                    <el-card class="textAreaCard" v-if="answer.questionType===3">
                      <div slot="header" class="clearfix">
                        <span>{{index+1}}、{{answer.questionTitle}}</span>
                      </div>
                      <p>有{{answer.answerCensusesList[0].num}}条有效数据</p>
                      <div class="censusBtns">
                        <el-button size="small" @click="textAreaViewDetails(answer.questionId)" type="primary" plain>查看详情</el-button>
                        <el-button size="small" @click="exportTextArea(answer.questionId)" :loading="exportTextIsLoading" type="primary" plain>{{exportTextAreaText}}</el-button>
                      </div>
                    </el-card>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </el-col>
        </el-row>
      </div>
    </el-main>

    <!--添加/修改 问卷对话框-->
    <el-dialog
        :visible.sync="paperAddSwitch"
        width="50%"
        class="addPaperDialog"
    >
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
      <!--问卷简介-->
      <div class="paperIntroduce">
        <el-input
            type="textarea"
            :rows="2"
            placeholder="请输入问卷简介"
            v-model="paperIntroduce">
        </el-input>
      </div>
      <div v-for="(p,index) in questionList" :key="index">
        <!--单选题-->
        <div class="question" v-if="p.questionType===1">
          <!--问题标题-->
          <div>
            <span>{{index+1}}、</span>
            <el-input
                class="pTitle"
                v-model="p.questionTitle">
            </el-input>
            <el-tooltip content="是否必须填写" placement="top">
              <el-switch
                  class="isRequired"
                  v-model="p.isRequired"
                  active-color="#13ce66"
                  :active-value = 1
                  :inactive-value = 0
                  @change="test(p.isRequired)">
              </el-switch>
            </el-tooltip>
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
                      v-model="p.questionTitle">
            </el-input>
            <el-tooltip content="是否必须填写" placement="top">
              <el-switch
                  class="isRequired"
                  v-model="p.isRequired"
                  active-color="#13ce66"
                  :active-value = 1
                  :inactive-value = 0
                  @change="test(p.isRequired)">
              </el-switch>
            </el-tooltip>
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
        <div class="textArea" v-if="p.questionType===3">
          <div class="areaQuestionTitle">
            <span>{{index+1}}、文本题</span>
            <el-tooltip content="是否必须填写" placement="top">
              <el-switch
                  class="isRequired"
                  v-model="p.isRequired"
                  active-color="#13ce66"
                  :active-value = 1
                  :inactive-value = 0
                  @change="test(p.isRequired)">
              </el-switch>
            </el-tooltip>
          </div>
          <!--问题内容-->
          <div>
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
        <el-button @click="savePaper($event,0)">保存问卷</el-button>
        <el-button type="primary" @click="savePaper($event,1)">发布问卷</el-button>
      </span>
    </el-dialog>
    <!--查看文本题详细信息对话框-->
    <el-dialog
        class="textAreaDialog"
        title="答卷信息"
        :visible.sync="textAreaVisible"
        width="55%"
    >
      <el-table
          :data="textAreaDetailValue"
          style="width: 100%">
        <el-table-column
            prop="answer"
            label="答案">
        </el-table-column>
        <el-table-column
            prop="answerTime"
            label="填写时间"
            width="140">
        </el-table-column>
      </el-table>
      <el-pagination
          @size-change="pageSizeChange"
          @current-change="currentChange"
          :current-page.sync="queryInfo.pageNum"
          :page-sizes="[5,10,20]"
          :page-size="queryInfo.pageSize"
          layout="sizes, prev, pager, next"
          :total="10">
      </el-pagination>
    </el-dialog>
    <!--分享问卷对话框-->
    <el-dialog
        title="分享问卷"
        :visible.sync="shareLinkVisible"
        class="sharePaperDialog"
        width="45%"
        @open="openShareDialog"
        :before-close="closeShareDialog">
      <div class="shareBox">
        <span>链接：</span>
        <el-input v-model="shareLink" style="width: 60%" placeholder="请输入内容"></el-input>
        <el-button @click="copyLink" class="copyBtn">复制</el-button>
      </div>
      <div class="qrCodeBox">
        <span>二维码:</span>
        <div class="qrCodeUrl" ref="qrCodeUrl"></div>
      </div>
    </el-dialog>
  </el-container>

</template>

<script>
// 问题模板
import axios from "axios";
// 引入qrCode自动生成二维码组件
import QRCode from "qrcodejs2";
import Head from "@/components/home/Head";
// import echarts from "echarts";  // 这是4.几版本引用方式，现在更新了版本引用方式
import * as echarts from 'echarts'

// 问题模板
const questionTemplate = {
  radio: {
    questionType: 1,
    questionTitle: "单选题",      // 问题
    questionOption: ["选项1","选项2"],      // 选项
    isRequired:1   // 是否必填，默认是必填
  },
  checkbox: {
    questionType: 2,
    questionTitle: "多选题",
    questionOption: ["选项1","选项2"],
    isRequired:1
  },
  textarea: {
    questionType: 3,
    questionTitle: "",  // 文本题
    questionOption: [],   // 文本内容
    isRequired:1
  }
}
export default {
  name: "Home",
  components: {Head},
  data(){
    return{
      paperId:"",
      paperListIsLoading:false,  // 左侧获取问卷加载中
      myPaper: [],       // 我的问卷列表(左侧问卷)
      paperAddSwitch:false,  // 对话框开关
      paperTitle:"新建问卷", // 添加问卷/修改问卷标题
      paperIntroduce:"感谢填写问卷",     // 问卷简介
      questionList:[],  // 添加或者修改问卷对话框的问题列表
      nowPaper:{},   // 当前选择的问卷
      selectOption:[],  // 存放选择的答案
      testOption:"",  // 临时测试使用
      saveIndex:"",   // 页面激活菜单标记
      tabName:"paperView",     // 右侧标签页面激活name
      isLoading:false,    // 右侧是否是正在查找
      exportBtnIsLoading:false, // 导出excel表格是否正在加载
      exportBtnText: '导出Excel', // 导出按钮文字
      answerList:[],   // 右侧问卷答案列表
      censusList:[],   // 答案统计视图列表
      // 文本题详细信息
      textAreaVisible:false,  // 文本题详情对话框是否可见
      textAreaQuestionId: '',  // 当前详细信息的问题id
      textAreaDetailValue:[],   // 文本题详细信息
      exportTextIsLoading:false,  // 导出文本excel是否正在加载
      exportTextAreaText:'导出Excel表格',
      queryInfo: {  // 查询信息
        pageNum: 1,   // 当前的页数
        pageSize: 5   // 一页显示的大小
      },
      // 分享问卷对话框
      shareLinkVisible:false,
      shareLink:'',
      urlPaperId:''   // 用来临时保存二维码路径中的paperId
    }
  },
  methods:{
    // 左侧问卷列表操作
    paperSet(params){
      switch (params.value){
        case 'toPaperView':   // 跳转到回答问卷页面
          this.toPaperView(params.index);
          break;
        case 'updatePaper':   // 修改问卷
          this.updatePaper(params.index);
          break;
        case 'delPaper':   // 删除问卷
          this.delPaper(params.index);
          break;
        case 'issuePaper': // 发布问卷
          this.issuePaper(params.index);
          break;
        case 'cancelIssuePaper': // 取消发布问卷
          this.cancelIssuePaper(params.index);
          break;
        case 'shareDialogSwitch': // 分享问卷
          this.shareDialogSwitch(params.index);
          break;
      }
    },
    // 将左侧点击事件中的参数封装成一个对象
    paperSetParam(index,value){
      return{
        "index":index,   // 操作的问卷下标
        "value":value    // 操作的方法
      }
    },
    // 将数字转化为对应的字母
    numToChar(item){
      let num = ''
      num = String.fromCharCode(Number(item)+65)
      return num
    },
    // 发布问卷
    async issuePaper(index){
      let paperId = this.myPaper[index].paperId;
      let {data:res} = await this.$http.post("/paper/issuePaper",paperId);
      if (res.code !== 200){
        return this.$message.error(res.msg);
      }
      this.$message.success(res.msg);
      this.myPaper[index].paperId = res.data;
      // 重新获取问卷
      await this.getMyPaper();
    },
    // 取消发布问卷
    async cancelIssuePaper(index){
      let paperId = this.myPaper[index].paperId;
      let {data:res} = await this.$http.post("/paper/cancelIssuePaper",paperId);
      if (res.code !== 200){
        return this.$message.error(res.msg);
      }
      this.$message.success(res.msg);
      this.myPaper[index].paperId = res.data;
      // 重新获取问卷
      await this.getMyPaper();
    },
    // 删除某一个问卷
    delPaper(index) {
      this.$confirm('确定要删除问卷吗，删除后还可以在回收站找到它', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {  // 用户点击确定
        let paperId = this.myPaper[index].paperId;
        let {data:res} = await axios.post("/paper/delPaperById", paperId);
        if (res.code !== 200){  // 说明删除失败
          return this.$message.error(res.msg);
        }
        this.$message.success(res.msg);
        // 重新获取问卷内容
        await this.getMyPaper();
        this.nowPaper = {};
        this.answerList = [];
      }).catch(() => {  // 用户点击取消
        this.$message.info("取消删除问卷")
      });
    },
    // 问卷中添加问题
    addProblem(value){
      if (value===1){
        let tableTemplate = JSON.parse(JSON.stringify(questionTemplate.radio));  // 先将值转换一下，不然都指向的是同一个值
        this.questionList.push(tableTemplate);
        // console.log(this.questionList);
      }else if (value===2){
        // console.log("添加多选题");
        let template = JSON.parse(JSON.stringify(questionTemplate.checkbox));  // 先将值转换一下，不然都指向的是同一个值
        this.questionList.push(template);
      }else if (value===3){
        // console.log("添加文本题");
        let template = JSON.parse(JSON.stringify(questionTemplate.textarea));  // 先将值转换一下，不然都指向的是同一个值
        this.questionList.push(template);
      }
      console.log(this.questionList);
    },
    // 控制添加问卷对话框的显示或者关闭
    addPaperDialogClose(flag){
      // 如果当前登录用户没有绑定邮箱，提醒用户需要绑定邮箱才能添加问卷或者修改问卷
      let userInfo = JSON.parse(window.sessionStorage.getItem("userInfo"));
      let userEmail = userInfo.userEmail;
      if (userEmail === "null"){
        return this.$message.warning("请先到用户信息界面绑定邮箱再发布问卷");
      }
      this.paperAddSwitch = flag;
    },
    // 查询当前登录用户的所有问卷
    async getMyPaper() {
      this.paperListIsLoading = true;
      let {data:res} = await axios.get("/paper/getMyPaper");
      if (res.code !== 200){
        this.paperListIsLoading = false;
        return this.$message.error("获取问卷失败，请稍后再试");
      }
      this.myPaper = res.data;  // 调用mutations中的方法，对state中的数据进行更改
      this.paperListIsLoading = false;
      // console.log(this.myPaper);
    },
    // 向后端提交问题列表,新增或者保存一个问卷
    async savePaper(e,status){
      // 对问卷的问题进行验证
      if (this.paperTitle.length > 16) {
        return this.$message.warning("问卷标题不能超过16个字");
      }else if (this.paperTitle.length < 1){
        return this.$message.warning("请设置问卷标题");
      }
      if (this.questionList.length === 0){
        return this.$message.warning("请添加问题");
      }
      // 判断问卷中的问题有没有空的问题，如果有就提示音用户添加问题信息
      for (let i = 0; i < this.questionList.length; i++){
        // console.log(this.questionList[i]);
        if (this.questionList[i].questionTitle === ''){
          console.log("下标是:"+i);
          console.log(this.questionList[i].questionTitle);
          return this.$message.warning("请不要设置空白的问题");
        }
        // 如果不是文本题，检查里面有没有空白的选项
        if (this.questionList[i].questionType !== 3){
          for (let j = 0; j < this.questionList[i].questionOption.length; j++){
            if (this.questionList[i].questionOption[j].length<1){
              return this.$message.warning("请不要设置空白的选项");
            }
          }
        }
      }

      // 如果paperId为空，直接添加问卷
      if (this.paperId===""){
        return await this.addOrUpdatePaper(status)
      }
      // 向后端查询是否有人填写了这个问卷
      let {data: re} = await this.$http.get(`/paper/paperIsAnswered/${this.paperId}`);
      // console.log(re.data);
      // 说明这个问卷有人填写了
      if (re.data) {
        this.$confirm('这个问卷已经有人填写了，修改的话会删除所有填写的内容，确定要继续吗', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          await this.addOrUpdatePaper();
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '取消修改问卷'
          });
          this.paperAddSwitch = false;  // 关闭对话框
        });
      } else {
        // 说明还没有人填写这个问卷，可以直接修改问卷
        await this.addOrUpdatePaper(status);
      }
    },
    // 向后端发送一个请求添加或者修改一个问卷
    async addOrUpdatePaper(status){
      // 创建一个变量，封装想要传递的参数
      const paper = {
        paperTitle: this.paperTitle,
        paperIntroduce: this.paperIntroduce,
        questionList: this.questionList,
        paperId: this.paperId,
        status: status   // 问卷状态
      }
      // console.log(paper);
      // 向后端发起请求，将用户创建的问题发送给后端
      let {data: res} = await this.$http.post('/paper/addPaper',paper);
      if (res.code !== 200){
        return this.$message.error(res.msg);
      }
      this.$message.success(res.msg);
      this.paperAddSwitch = false;  // 关闭对话框
      // 刷新一下问卷列表
      await this.getMyPaper();
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
    delOption(index,opIndex){  // 参数，问题在问题列表中的下标，选择的选项的下标
      if (this.questionList[index].questionOption.length<=2){
        return this.$message.warning("最少需要两个选项");
      }
      // 删除对应的选项
      this.$delete(this.questionList[index].questionOption,opIndex); // 删除对应的选项
    },
    // 添加一个新的选项
    addOption(index,opIndex){
      let optionList = this.questionList[index].questionOption;
      optionList.push("选项"+(optionList.length+1));  // 往选项列表中添加一个值
    },
    // 修改newPaper，改变右侧的视图
    async changePaperView(index){
      // 判断当前点击的是否是和右侧相同的问卷，直接退出
      if (this.myPaper[index].paperId === this.nowPaper.paperId){
        return;
      }
      // 如果这个问卷已经封禁了，弹出提示框，不跳转到右侧界面
      if (this.myPaper[index].paperState===-1){
        return await this.$alert('此问卷已经被封禁，请联系管理员解封', '警告', {
          confirmButtonText: '确定'
        });
      }
      this.isLoading = true;
      let paperId = this.myPaper[index].paperId;
      let {data: res} = await this.$http.get("paper/getPaperById/"+paperId);
      if (res.code !== 200){
        return this.$message.error(res.msg);
      }
      this.nowPaper = res.data;  // 给newPaper赋值，修改右边的视图
      // 保存当前点击的路径到session中
      window.sessionStorage.setItem("saveIndex",index);
      this.saveIndex = index;
      // console.log(this.nowPaper);
      // 如果此时用户tab标签页是答卷管理，自动进行答卷查询
      this.censusList = [];  // 重置视图数组对象
      if (this.tabName==="answerManage"){
        await this.getAnswerByPId(paperId);  // 查询右侧答案统计视图
      }
      this.isLoading = false;
    },
    // 打开修改问卷对话框
    async updatePaper(index){
      let paperId = this.myPaper[index].paperId;
      // 通过问卷id获取问卷信息
      let {data : res} = await this.$http.get("/paper/getPaperById/"+paperId);  // 通过restful风格传递参数
      if (res.code !== 200){  // 说明删除失败
        return this.$message.error("发生错误");
      }
      let paper = res.data;
      // console.log(paper);
      this.paperId = paper.paperId;
      this.paperTitle = paper.paperTitle;  // 修改问题标题
      this.paperIntroduce = paper.paperIntroduce;  // 修改问卷简介
      this.questionList = paper.questionList;  // 修改问题列表
      this.addPaperDialogClose(true); // 打开对话框，用来修改问卷
      console.log(this.questionList);
    },
    // 跳转到预览问卷页面
    toPaperView(index){
      this.$router.push({
            name:"toAnswerPaper",
            params:{
              paperId: this.myPaper[index].paperId,
            }
          }
      );
    },
    // 切换右侧视图
    async changeAnswerCensus(tab,e){
      // 切换到答卷管理
      if (tab.index==="1"){ // 如果是查询问卷
        let paperId = this.nowPaper.paperId;
        if (paperId){
          await this.getAnswerByPId(paperId);
        }
        // console.log(this.tabName);
      }
    },
    // 查询一个问卷的答案情况
    async getAnswerByPId(paperId){
      this.isLoading = true;
      if (paperId){
        let {data:res} = await this.$http.get(`/answer/getAnswerByPId/${paperId}`);
        // console.log(res.data);
        this.answerList = res.data;
        console.log(this.answerList);
      }else {
        this.$message.warning("问卷信息错误");
      }
      this.isLoading = false;
    },
    // 导出问卷整体excel表格
    async exportExcel(){
      this.exportBtnIsLoading = true;  // 开启加载中遮罩
      this.exportBtnText = '正在导出';
      await this.$http.get("/answer/exportAnswerExcel",{
        params:{
          paperTitle:this.nowPaper.paperTitle,
          paperId: this.nowPaper.paperId
        },
        responseType:'blob'   // 指明返回格式为二进制格式
      }).then(res =>{
        this.exportBtnIsLoading = false;  // 关闭遮罩
        this.exportBtnText = '导出Excel';
        // console.log(res);
        // 文件下载
        const blob = new Blob([res.data], {
          type: "application/vnd.ms-excel"
        });
        let fileNames = res.headers['content-disposition'] //获取到Content-Disposition;filename
        let regFileNames = fileNames.match(/=(.*)$/)[1]; //文件名称  截取=后面的文件名称
        let link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.setAttribute('download', `${decodeURI(regFileNames)}`);
        link.click();
        link = null;
      })
    },
    // 点击修改对应的统计对象图像类型
    changeCensus(index,value){  // 参数：在视图数组中对应的下标，对应的值
      this.$set(this.censusList,index,value); // 设置censusList中下标为index的值为value
      if (value === 1){  // 柱状图
        this.histogram(index);  // 设置index为柱状图
      }else if (value === 2){
        this.pieChart(index);
      }else if (value === 3){
        this.annular(index);
      } else {
        this.$set(this.censusList,index,0);
      }
    },
    // 柱状图
    histogram(index){
      // console.log(this.$refs[`census${index}`][0]);
      let myChart = echarts.init(this.$refs[`histogram${index}`][0]);
      let option = {
        xAxis: {
          type: 'category',
          data: this.answerList[index].answerCensusesList.map(d => d.optionName)  // map映射对应数组中的某一个数组生成一个新的数组
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            // 数量
            data: this.answerList[index].answerCensusesList.map(d => d.num),
            type: 'bar'
          }
        ]
      }
      myChart.setOption(option);  // 设置视图
    },
    // 饼状图
    pieChart(index){
      // console.log(this.$refs[`census${index}`][0]);
      let myChart = echarts.init(this.$refs[`pieChart${index}`][0]);
      let option = {
        title: {
          text: '饼状图',
          subtext: 'Fake Data',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '百分比',
            type: 'pie',
            radius: '50%',
            data: this.answerList[index].answerCensusesList.map(item =>{
              return{
                value:item.num,
                name:item.optionName
              }
            }),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };
      myChart.setOption(option);
      console.log(this.answerList[index].answerCensusesList.map(item =>{
        return{
          value:item.num,
          name:item.optionName
        }
      }));
    },
    // 环形图
    annular(index){
      // console.log(this.$refs[`census${index}`][0]);
      let myChart = echarts.init(this.$refs[`annular${index}`][0]);
      let option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            name: '数量',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '10',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.answerList[index].answerCensusesList.map(item => {
              return{
                value:item.num,
                name:item.optionName
              }
            })
          }
        ]
      }
      myChart.setOption(option);  // 设置视图
    },
    // 查询某个文本题详情
    async textAreaViewDetails(questionId){
      let {data:res} = await this.$http.get(`/answer/getTextAreaDetails`,{
        params:{
          questionId: questionId,
          pageNum: this.queryInfo.pageNum,
          pageSize: this.queryInfo.pageSize
        }
      });
      // console.log(res);
      if (res.code !== 200){
        return this.$message.error(res.msg);
      }
      this.textAreaQuestionId = questionId;  // 用textAreaQuestionId保存当前的问题id
      this.textAreaDetailValue = res.data.areaDetails;
      this.textAreaVisible = true;
      console.log(this.textAreaDetailValue);
    },
    // 导出文本信息详细信息
    exportTextArea(questionId){
      this.exportTextIsLoading = true;  // 加载遮罩
      this.exportTextAreaText = '正在导出';
      this.$http.get("/answer/exportTextAreaExcel",{
        params:{
          questionId: questionId
        },
        responseType:'blob'   // 指明返回格式为二进制格式
      }).then(res => {
        this.exportTextIsLoading = false;  // 关闭遮罩
        this.exportTextAreaText = '导出Excel表格';
        // console.log(res);
        // 文件下载
        const blob = new Blob([res.data], {
          type: "application/vnd.ms-excel"
        });
        let fileNames = res.headers['content-disposition'] //获取到Content-Disposition;filename
        let regFileNames = fileNames.match(/=(.*)$/)[1]; //文件名称  截取=后面的文件名称
        let link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.setAttribute('download', `${decodeURI(regFileNames)}`);  // 使用js自带的decodeRUI进行解析
        link.click();
        link = null;
      })
    },
    // 修改分页大小
    pageSizeChange(value){
      this.queryInfo.pageSize = value;
      // 重新获取答案信息
      this.textAreaViewDetails(this.textAreaQuestionId);
    },
    // 修改当前分页
    currentChange(value){
      this.queryInfo.pageNum = value;
      // 重新获取答卷信息
      this.textAreaViewDetails(this.textAreaQuestionId);
    },
    // 打开分享问卷对话框
    shareDialogSwitch(index){
      if (this.myPaper[index].status!==1){
        return this.$message.warning("请先发布问卷");
      }
      this.urlPaperId = this.myPaper[index].paperId;
      this.shareLink = this.$global.localAddress+`/#/answerPaper/${this.myPaper[index].paperId}`;
      this.shareLinkVisible = true;
    },
    // 生成问卷二维码
    openShareDialog(){
      this.$nextTick(()=>{  // this.$nextTick()将回调延迟到下次 DOM 更新循环之后执行。不使用this.$nextTick无法获取到ref
        // console.log(this.$refs);
        // console.log(this.$refs.qrCodeUrl);
        // 生成问卷二维码
        console.log(this.urlPaperId);
        new QRCode(this.$refs.qrCodeUrl,{
          width:200,
          height:200,
          text: this.$global.localAddress+`/#/answerPaper/${this.urlPaperId}`
        });
      });
    },
    // 复制链接到剪切板
    async copyLink(){
      await navigator.clipboard.writeText(this.shareLink);
      this.$message.success("已经复制到剪切板");
    },
    // 关闭分享问卷对话框事件
    closeShareDialog(){
      this.shareLinkVisible = false;
    }
  },
  created() {
    this.getMyPaper();       // 查询当前用户的所有问卷
    if (window.sessionStorage.getItem("saveIndex")){
      this.saveIndex = window.sessionStorage.getItem("saveIndex");
    }
    // console.log(this.saveIndex);
    // console.log(this.nowPaper);
  },
  watch:{
    // 监听对话框的关闭
    paperAddSwitch(newValue,oldValue){
      // 如果是关闭对话框，重置对话框中的信息
      if (newValue === false){
        setTimeout(()=>{
          this.paperTitle = "新建问卷";
          this.questionList = [];
          this.paperIntroduce = "感谢填写问卷";
          // console.log("更新对话框中信息成功");
        },200);
      }
    }
  }
}
</script>

<style scoped lang="less">
.home_container{
  width: 100%;
  height: 100%;
  overflow-x: hidden;  // 关闭水平方向的滚动条
  // 头部组件
  .el-header{
    border-bottom: 3px solid #409EFF;
    background-color: #ffffff;
  }
}
// 主页面容器
.mainContainer{
  height: 100%;
  overflow: hidden;
  .el-row{
    height: 100%;
    // 左侧问卷栏
    .left{
      height: 100%;
      // 问卷操作
      .control{
        display: flex;   // 弹性盒子
        flex-direction: row;
        justify-content: center;
        align-items: center;
        background-color: #f5f7fa;
        height: 35px;
        .el-button{

        }
      }
      // 问卷列表
      .paperList{
        height: 100%;
        overflow-y: scroll;
        overflow-x: hidden;  // 关闭水平方向的滚动条
        position: relative;
        // 问卷列表后方问卷设置
        .paperSet{
          display: inline-block;
          position: absolute;
          right: 2px;
          //visibility: hidden;
          .operationName{
            color: #409EFF;
            i{
              margin: 0;
            }
          }
        }
      }
    }
    .right{
      height: 100%;
      overflow-y: scroll;
      overflow-x: hidden;  // 关闭水平方向的滚动条
      .el-tabs{
        margin: 0 10px 10px 0;
        //height: 100%;
        .el-tab-pane{
          height: 100%;
        }
        .paperTitle{
          width: 100%;
          height: 40px;
          text-align: center;
          margin-bottom: 20px;
        }
        // 问题列表
        .questionList{
          width: 100%;
          .question{  // 问题卡片
            border: 0;
            box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
            margin:10px auto;  // 左右居中
            width: 80%;
            .questionHead{  // 问题内容
              font-size: 16px;
              margin-bottom: 20px;
            }
            // 问题选项
            .question-items{
              font-size: 14px;
              // 单选题和多选题
              .el-radio, .el-checkbox{
                width: 100%;
                margin-bottom: 8px;
              }
            }
          }
        }
        // 答卷管理
        .answerContainer{
          padding: 10px;
          // 导出表格操作
          .answerExport{
            width: 70%;
            margin:10px auto;  // 左右居中
          }
          .optionAnswer{
            width: 70%;
            margin:10px auto;  // 左右居中
            // 设置统计视图样式
            .censusBtns{
              display: flex;   // 弹性布局
              flex-direction: row;
              justify-content: left;
              align-items: center;
              margin: 10px 0 5px 0;
            }
            // 统计图样式
            .answerCensus{
              height: 300px;
              width: 500px;
            }
            // 文本题样式
            .textAreaCard{
              p{
                margin-top: -10px;
              }
            }
          }
        }
      }
    }
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
    margin: 0;
  }
  // 问卷简介
  .paperIntroduce{
    margin-bottom: 12px;
  }
  // 题目内容框
  .question{
    border: 1px solid black;
    margin-bottom: 10px;
    padding: 0 10px 0 10px;
    position: relative;
    // 题目标题
    .pTitle{
      width: 70%;
    }
    // 问题是否是必填的
    .isRequired{
      position: absolute;
      right: 10px;
      top: 10px;
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
  // 多选题文本域
  .textArea{
    margin-bottom: 10px;
    position: relative;
    border: 1px solid black;
    padding: 10px;
    .areaQuestionTitle{
      margin-bottom: 10px;
      // 文本题是否是必填的
      .isRequired{
        position: absolute;
        right: 10px;
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
// 文本题问卷详细信息对话框
.textAreaDialog{
  .el-pagination{
    margin-top: 12px;
  }
}
// 分享问卷对话框
.sharePaperDialog{
  .shareBox{
    margin: 10px 0;
    .copyBtn{
      margin-left: 10px;
    }
  }
  .qrCodeBox{
    .qrCodeUrl{
      margin-left: 70px;
    }
  }
}
</style>
