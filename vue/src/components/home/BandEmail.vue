<template>
  <div class="msgContainer">
    <div class="msgBox">
      <el-card class="cardBox" v-if="flag===1">
        <div class="imgBox">
          <img src="../../assets/img/successImg.png" alt="成功图片">
        </div>
        <div class="infoBox">
          <h2>{{msg1}}</h2>
          <p>{{msg2}}</p>
          <el-button type="primary" @click="toHome" size="small" plain>进入主页</el-button>
        </div>
      </el-card>
      <el-card class="cardBox" v-if="flag===2">
        <div class="imgBox">
          <img src="../../assets/img/warningImg.png" alt="失败图片">
        </div>
        <div class="infoBox">
          <h2>{{msg1}}</h2>
          <p>{{msg2}}</p>
          <el-button type="primary" @click="toHome" size="small" plain>进入主页</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: "BandEmail",
  data(){
    return{
      flag: 0,
      msg1:'',  // 消息1
      msg2:''   // 消息2
    }
  },
  methods:{
    // 向后端验证邮箱
    async bandEmail(){
      let {data:res} = await this.$http.get("/user/bandEmail",{
        params:{
          email: this.$route.params.email,
          key: this.$route.params.key,
          userId:this.$route.params.userId
        }
      });
      console.log(res);
      if (res.code===200){
        this.msg1 = res.msg;
        this.msg2 = "你现在可以通过邮箱登录网站"
        this.flag = 1;
      }else {
        this.msg1 = '绑定该邮箱失败';
        this.msg2 = res.msg
        this.flag = 2;
      }
    },
    // 跳转到主页面
    toHome(){
      this.$router.push("/toHome");
    }
  },
  created() {
    this.bandEmail();  // 向后端发送异步请求
  }
}
</script>

<style scoped lang="less">

.msgContainer{
  width: 100%;
  height: 100%;
  background-color: #eeeff1;
  .msgBox{
    width: 280px;
    height: 300px;
    position: absolute;
    left: 50%;
    top: 45%;
    transform: translate(-55%,-55%);
    display: block;
    .cardBox{
      width: 100%;
      height: 100%;
      border-radius: 12px;
      .imgBox{
        height: 100px;
        width: 100px;
        margin-bottom: 30px;
        img{
          height: 70px;
          width: 70px;
          position: absolute;
          display: block;
          left: 52%;
          top: 30%;
          transform: translate(-55%,-55%);
        }
      }
      .infoBox{
        text-align: center;  // 提示消息左右居中
        h2{
          font-size: 16px;
          color: #4c4c4c;
        }
        p{
          font-size: 14px;
          //padding-top: 10px;
        }
        .el-button{
          margin-top: 15px;
        }
      }
    }
  }
}

</style>
