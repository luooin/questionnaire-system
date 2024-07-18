<template>
  <div class="paper-container">
    <!--问卷列表头部操作-->
    <div class="title-container">
      <el-row :gutter="20">
        <!--标题位置-->
        <el-col :span="12">
          <div class="grid-content" style="">
            <span class="title">问卷列表</span>
          </div>
        </el-col>
        <!--右侧功能区域-->
        <el-col :span="12">
          <div class="grid-content function" style="">
            <!--下拉时间选择器-->
            <template>
              <el-select v-model="defaultSort" class="mySelect" size="small"> <!--默认选择第一个-->
                <el-option
                    v-for="item in sortByTime"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                </el-option>
              </el-select>
            </template>
            <!--下拉状态选择器-->
            <template>
              <el-select v-model="defaultState" class="mySelect" size="small"> <!--默认选择第一个-->
                <el-option
                    v-for="item in paperState"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                </el-option>
              </el-select>
            </template>
            <!--搜索输入框-->
            <el-input
                class="selectPaper"
                placeholder="请输入内容"
                prefix-icon="el-icon-search">
            </el-input>
          </div>
        </el-col>
      </el-row>
    </div>

    <!--问卷列表-->
    <div class="card">
      <el-card v-for="(paper,index) in $store.state.myPaper" class="cardBox" :key="index">
        <!--问卷头部-->
        <div class="cardHead">
            <span class="paperTitle">{{paper.title}}</span>
          <div class="cardInfo">
            <div class="isRelease">
              <span>{{paper.paperStatus===1?"已发布":"未发布"}}</span>
            </div>
            <div class="getQuestionnaire">
              <span>答卷数量:{{paper.fillNumber}}</span>
            </div>
            <div class="releaseTime">
              <span>创建时间{{}}</span>
            </div>
          </div>
        </div>
        <el-divider></el-divider>
        <!--问卷操作-->
        <div class="paperOperation">
          <div class="paperOperation_left">
            <el-button type="primary" icon="el-icon-edit" @click="updatePaper(index)" size="medium">修改问卷</el-button>
            <el-button type="primary" icon="el-icon-s-promotion" size="medium">发送问卷</el-button>
          </div>
          <div class="paperOperation_right">
            <el-button type="primary" size="medium">发布问卷<i class="el-icon-upload el-icon--right"></i></el-button>
            <el-button type="primary" size="medium" @click="delPaper(index)">删除问卷<i class="el-icon-delete-solid el-icon--right"></i></el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "MyPaper",
  data() {
    return {
      // 时间选择
      sortByTime: [{
        value: 'timeNormal',
        label: '时间正序'  // 时间正序
      }, {
        value: 'timeReverse', // 时间倒序
        label: '时间倒序'
      }],
      defaultSort: 'timeNormal',  // 默认值是 第一个选项
      // 状态选择
      paperState: [{
        value: 'AllPaper',
        label: '所有问卷'
      },{
        value: 'published',
        label: '已发布'
      }, {
        value: 'unpublished',
        label: '未发布'
      }],
      defaultState:'AllPaper',  // 默认值是 第一个选项
    }
  },
  computed:{
    paperAddSwitch:{  // 添加问卷对话框开关,获取vuex中的值绑定，下面一样
      get(){
        return this.$store.state.paperAddSwitch;
      },
      set(value){
        this.$store.state.paperAddSwitch = value;
      }
    },
    paperTitle:{   // 问卷标题
      get(){  // 当有人调用属性的时候自动调用get方法
        return this.$store.state.paperTitle;
      },
      set(value){   // 有人修改属性的时候自动调用set方法
        this.$store.state.paperTitle = value;  // 修改vuex中的值
      }
    },
    questionList:{  // 问题列表
      get() {
        return this.$store.state.questionList;
      },
      set(value) {
        this.$store.state.questionList = value;
      }
    }
  },
  methods:{
    // 删除某一个问卷
    delPaper(index) {
      this.$confirm('确定要删除问卷吗，删除后还可以在回收站找到它', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {  // 用户点击确定
        let paperId = this.$store.state.myPaper[index].paperId;
        let {data:res} = await axios.post("/paper/delPaperById", paperId);
        if (res.code !== 200){  // 说明删除失败
          return this.$message.error(res.msg);
        }
        this.$message.success(res.msg);
        // 重新获取问卷内容
        await this.$store.dispatch("getMyPaper");
      }).catch(() => {  // 用户点击取消
        this.$message.info("取消删除问卷")
      });
    },
    // 修改某个问卷
    async updatePaper(index){
      let paperId = this.$store.state.myPaper[index].paperId;
      // 通过问卷id获取问卷信息
      let {data : res} = await this.$http.get("/paper/getPaperById/"+paperId);  // 通过restful风格传递参数
      if (res.code !== 200){  // 说明删除失败
        return this.$message.error("发生错误");
      }
      let paper = res.data;
      // console.log(paper);
      this.paperTitle = paper.paperTitle;
      this.questionList = paper.questionList;
      this.paperAddSwitch = true; // 打开对话框，用来修改问卷
      this.$store.commit("UPDATE_PAPER_ID",paper.paperId);
      // console.log(this.$store.state.questionList);
    }
  },
  created(){
    // 从后台获取当前登录用户的问卷
    this.$store.dispatch("getMyPaper");  // 获取我的问卷内容
  }
}
</script>

<style scoped lang="less">
// 我的问卷头部
.grid-content {
  height: 50px;
}
.el-row {
  margin-bottom: 20px;
}
// 问卷列表字体
.title{
  font-weight: bold;
  font-size: 25px;
  line-height: 50px;  // 上下居中
  position: absolute;
  left: 60px;
}
// 功能菜单
.function{
  position: relative;
  // 选择框
  .mySelect{
    width: 120px;
    margin: 0 10px;
    line-height: 50px;  // 上下居中
  }
  // 输入框
  .selectPaper{
    width: 50%;
    position: absolute;
    right: 5px;
    line-height: 50px;  // 上下居中
  }
}

// 问卷列表
.card{
  .cardBox{
    height: 120px;
    margin-bottom: 10px;
    padding: 0;
    // 问卷上半部分
    .cardHead{
      height: 30px;
      margin-bottom: 10px;
      display: block;
      .paperTitle{
        float: left;
        font-size: 20px;
      }
      .cardInfo{
        height: 100%;
        width: 40%;
        float: right;
        display: flex;  // 弹性布局盒子
        flex-direction: row;  // 水平分布
        justify-content: space-between;  // 子元素等间隔分布
        align-items: center;   // 上下居中
        // 是否发布
        .isRelease{
          font-size: 10px;
          line-height: 30px;
          height: 100%;
          width: 20%;
          text-align:center;  // div中的行内元素水平居中
        }
        // 答卷数量
        .getQuestionnaire{
          font-size: 10px;
          line-height: 30px;
          height: 100%;
          width: 40%;
          text-align:center;  // div中的行内元素水平居中
        }
        // 发布时间
        .releaseTime{
          font-size: 10px;
          line-height: 30px;
          height: 100%;
          width: 30%;
          text-align:center;  // div中的行内元素水平居中
        }
      }
    }
    // 分割线
    .el-divider{
      margin: 0 0 5px 0;
    }
    // 问卷操作
    .paperOperation{
      height: 50px;
      .paperOperation_left{
        float: left;
        height: 100%;
        width: 40%;
        margin-left: 20px;
        display: flex;  // 弹性盒子
        flex-direction: row;
        justify-content: left;
        align-items: center;
      }
      .paperOperation_right{
        float: right;
        height: 100%;
        width: 30%;
        margin-right: 20px;
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
      }
    }
  }
}


</style>
