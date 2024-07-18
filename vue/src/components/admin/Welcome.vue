<template>
  <div class="mainContainer">
    <!--面包屑导航区域-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/welcome' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>数据统计</el-breadcrumb-item>
    </el-breadcrumb>
    <!--视图区域-->
    <div class="dataBox"

         v-loading="isLoading"
         element-loading-text="正在获取统计图">
      <el-row class="rowTop">
        <el-col :span="11" class="paperCount">
          <el-card class="box-card">
            <div class="paperHistogram" ref="paperHistogram">
            </div>
          </el-card>
        </el-col>
        <el-col :span="11" class="answerCount">
          <el-card class="box-card">
            <div class="answerHistogram" ref="answerHistogram">
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div class="bottom">
        <el-card class="box-card">
          <div class="userCount" ref="userCount">
          </div>
        </el-card>
      </div>
    </div>

  </div>
</template>

<script>
// 引用echarts
import * as echarts from 'echarts';
export default {
  name: "Welcome",
  data(){
    return{
      isLoading: false,
      // 发布问卷信息数据
      paperCount:[],  // 数量
      paperDate:[],   // 下标时间
      // 填写问卷信息数据
      answerCount:[],
      answerDate:[],
      // 用户登录信息数据
      userCount:[],
      userDate:[]
    }
  },
  methods:{
    // 获取后端问卷发布情况数据
    async getPaperData(){
      // 像后端获取最近七天的用户发布问卷情况
      let {data:res} = await this.$http.get("/admin/getPaperCount");
      // console.log(res);
      this.paperCount = res.data.countList;
      this.paperDate = res.data.dateList;
      // console.log(this.paperCount);
      // console.log(this.paperDate);
    },
    // 向后端获取七天内问卷填写情况
    async getAnswerData(){
      // 像后端获取最近七天的用户发布问卷情况
      let {data:res} = await this.$http.get("/admin/getAnswerCount");
      // console.log(res);
      this.answerCount = res.data.countList;
      this.answerDate = res.data.dateList;  // 下标数据
    },
    // 向后端获取七天内用户登录情况
    async getUserData(){
      // 像后端获取最近七天的用户发布问卷情况
      let {data:res} = await this.$http.get("/admin/getUserCount");
      console.log(res);
      this.userCount = res.data.countList;
      this.userDate = res.data.dateList;  // 下标数据
    },
    // 绘制发布问卷数量统计图
    async paperHistogram(){
      let myChart = echarts.init(this.$refs.paperHistogram);
      // console.log(this.paperCount);
      // console.log(this.paperDate);
      // 柱状图
      let option = {
        title: {
          text: '近七天网站发布问卷情况',
          left: 'center'
        },
        xAxis: {
          type: 'category',
          data: this.paperDate
        },
        tooltip: {
          trigger: 'item'
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: this.paperCount,
            type: 'bar'
          }
        ]
      };
      myChart.setOption(option);// 设置条形图
    },
    // 绘制回答问卷数量统计图
    async answerHistogram(){
      let myChart = echarts.init(this.$refs.answerHistogram);
      // 折线图
      let option = {
        title: {
          text: '近七天网站填写问卷情况',
          left: 'center'
        },
        xAxis: {
          type: 'category',
          data: this.answerDate
        },
        yAxis: {
          type: 'value'
        },
        tooltip: {
          trigger: 'item'
        },
        series: [
          {
            data: this.answerCount,
            type: 'bar'
          }
        ]
      };
      myChart.setOption(option);// 设置条形图
    },
    // 绘制用户数量统计图
    async userHistogram(){
      let myChart = echarts.init(this.$refs.userCount);
      // 折线图
      let option = {
        title: {
          text: '近七天网站用户登录情况',
          left: 'center'
        },
        xAxis: {
          type: 'category',
          data: this.userDate
        },
        yAxis: {
          type: 'value'
        },
        tooltip: {
          trigger: 'item'
        },
        series: [
          {
            data: this.userCount,
            type: 'bar'
          }
        ]
      };
      myChart.setOption(option);// 设置折线图
    },
    // 绘制三个统计图
    async drawCharts(){
      this.isLoading = true;
      // 获取七天内问卷发布数量
      await this.getPaperData();
      // 获取七天内问卷填写数量
      await this.getAnswerData();
      // 获取七天内用户登录数量
      await this.getUserData();
      await this.paperHistogram();
      await this.answerHistogram();
      await this.userHistogram();
      this.isLoading = false;
    }
  },
  // 钩子函数，此时已经渲染完dom
  mounted() {
    // 绘制统计图
    this.drawCharts();
  }
}
</script>

<style scoped lang="less">
.mainContainer{
  width: 100%;
  height: 100%;
  // 面包屑导航
  .el-breadcrumb{
    margin: 0 0 20px 23px;
  }
  // 统计数据容器
  .dataBox{
    width: 100%;
    height: 100%;
    overflow-y: hidden;
    overflow-x: hidden;  // 关闭水平方向的滚动条
    // 上部分统计图
    .rowTop{
      height: 40%;
      margin-bottom: 20px;
      // 发布问卷数量容器
      .paperCount{
        height: 100%;
        margin: 0 20px;
        .paperHistogram{
          height: 100%;
        }
      }
      // 回答问卷数量容器
      .answerCount{
        height: 100%;
        margin: 0 20px;
        .answerHistogram{
          height: 100%;
        }
      }
    }
    // 下半部分统计图,用户登录数量统计
    .bottom{
      height: 52%;
      width: 90%;
      margin: 0 auto;
      .userCount{
        height: 100%;
      }
    }
  }
  // 卡片视图,样式穿透设置高度
  .box-card {
    height: 95%;
    box-shadow: 0 2px 12px 0 rgb(0 0 0 / 20%);
    ::v-deep .el-card__body {
      height: 100%;
      padding: 20px 0 0 0;
      margin: 0;
    }
  }

}

</style>
