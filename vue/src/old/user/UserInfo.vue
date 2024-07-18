<template>
  <div class="infoContainer">
    <div class="userInfo">
      <h2>用户信息</h2>
      <div class="headImgContainer">
        <el-image :src="imgUrl" alt=""/>
        <div>
          <el-button type="primary"
                     round
                     size="small"
                     @click="changeHead">修改头像</el-button>
        </div>
        <myUpload
            v-model="showDialog"
            :headers="headers"
            :url="uploadUrl"
            @crop-upload-success="cropUploadSuccess"
        />
      </div>
      <el-descriptions>
        <el-descriptions-item :span="24" label="用户名">kooriookami</el-descriptions-item>
        <el-descriptions-item :span="24" label="手机号">18100000000</el-descriptions-item>
        <el-descriptions-item :span="24" label="居住地">苏州市</el-descriptions-item>
        <el-descriptions-item :span="24" label="备注">
          <el-tag size="small">学校</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="联系地址">江苏省苏州市吴中区吴中大道 1188 号</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script>
import myUpload from 'vue-image-crop-upload';  // 导入文件上传组件

export default {
  name: "UserInfo",
  components:{myUpload},
  data(){
    return{
      showDialog: false,
      imgUrl:"https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
      uploadUrl:'/user/uploadHeader',   // 上传头像接口
      headers:{
        token:window.sessionStorage.getItem("token")
      },
      userInfo:[]
    }
  },
  methods:{
    // 获取用户信息,赋值给userInfo
    getUserInfo(){
      let userInfoStr = window.sessionStorage.getItem("userInfo");
      this.userInfo = JSON.parse(userInfoStr);
      if (this.userInfo.userHeadPath){  // 如果用户设置了头像，将路径从默认路径更改成头像在服务器中的路径
        this.imgUrl = "http://localhost:8888"+this.userInfo.userHeadPath;
      }
      // console.log(this.userInfo.userHeadPath);
    },
    // 修改用户头像窗口打开
    changeHead(){
      this.showDialog = !this.showDialog
    },
    // 头像上传成功事件
    cropUploadSuccess(jsonData,field){  // 这两个参数是vue-image-crop-upload组件需要使用的,名字也是固定的
      this.$message.success("修改头像成功");
      // console.log(jsonData.data);
      // 更新用户头像的地址
      // console.log("http://localhost:8888" + jsonData.data.newHeadPath);
      this.imgUrl = "http://localhost:8888" + jsonData.data.newHeadPath;
      // 更新缓存中的用户信息
      this.userInfo.userHeadPath = jsonData.data.newHeadPath;
      // console.log(this.userInfo);
      // console.log(JSON.stringify(this.userInfo));
      window.sessionStorage.setItem("userInfo",JSON.stringify(this.userInfo));
    }
  },
  created() {
    // 获取用户信息
    this.getUserInfo();
  }
}
</script>

<style scoped lang="less">
.infoContainer{
  position: relative;
  .userInfo{
    width: 90%;
    margin: 0 auto;
    .headImgContainer{
      width: 130px;
      height: 180px;
      position: absolute;
      right: 130px;
      top: 20px;
      text-align: center; // 让div里面的行内元素水平居中
      .el-image{
        width: 130px;
        height: 130px;
        border-radius: 50%;
        box-shadow: 0 0 8px #5e5c5c;
        margin-bottom: 5px;
      }
    }
  }
}

</style>
