<template>
  <div class="infoContainer">
    <el-header style="padding: 0">
      <Head></Head>
    </el-header>
    <div class="userInfoContainer">
      <h2>用户信息</h2>
      <div class="headImgContainer">
        <el-image :src="imgUrl" alt=""/>
        <div>
          <el-button type="primary"
                     round
                     size="small"
                     @click="changeHead">修改头像
          </el-button>
        </div>
        <myUpload
            v-model="showDialog"
            :headers="headers"
            :url="uploadUrl"
            @crop-upload-success="cropUploadSuccess"
        />
      </div>
      <div class="userInfoBox">
        <el-card class="box-card">
          <el-descriptions :column="1">
            <el-descriptions-item label="账号">
              {{userInfo.userId}}
            </el-descriptions-item>
            <el-descriptions-item label="用户昵称">
              <div
                  @mouseenter="showBtn($event)"
                  @mouseleave="leverBtn($event)">
                {{userInfo.userName}}
                <el-link type="primary" @click="changeNameDialog()" class="changeBtn" :underline="false">
                  修改昵称
                </el-link>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="密码">
              <div
                  @mouseenter="showBtn($event)"
                  @mouseleave="leverBtn($event)">
                *******
                <el-link type="primary" @click="changePasswordDialog()" class="changeBtn" :underline="false">
                  修改密码
                </el-link>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              <div
                  @mouseenter="showBtn($event)"
                  @mouseleave="leverBtn($event)">
                {{userInfo.userEmail?userInfo.userEmail:'请先登录'}}
                <el-link type="primary" @click="changeEmailDialog()" class="changeBtn" :underline="false">
                  修改/绑定邮箱
                </el-link>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="电话">
              <div
                  @mouseenter="showBtn($event)"
                  @mouseleave="leverBtn($event)">
                {{userInfo.phone?userInfo.phone:'请先登录'}}
                <el-link type="primary" class="changeBtn" :underline="false">
                  {{userInfo.phone!=='null'?'修改绑定':'绑定手机号'}}
                </el-link>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="简介">
              <div
                  @mouseenter="showBtn($event)"
                  @mouseleave="leverBtn($event)">
                {{userInfo.userIntroduce?userInfo.userIntroduce:'请先登录'}}
                <el-link type="primary" @click="changeIntroduceDialog()" class="changeBtn" :underline="false">
                  修改昵称
                </el-link>
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </div>
    </div>
    <!--修改用户昵称对话框-->
    <el-dialog title="修改昵称" :visible.sync="changeNameVisible">
      <div class="changeBox">
        <span>昵称：</span>
        <el-input v-model="changeNameValue" style="width: 70%"></el-input>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="changeNameVisible = false">取 消</el-button>
        <el-button type="primary" @click="doChangeUserName">确 定</el-button>
      </div>
    </el-dialog>
    <!--修改密码对话框-->
    <el-dialog title="修改密码" :visible.sync="changePasswordVisible">
      <el-form :model="passwordForm" ref="passwordRef" :rules="passwordFormRules">
        <el-form-item label="旧密码:" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" show-password autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="新密码:" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" show-password autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="确认密码:" prop="reNewPassword">
          <el-input v-model="passwordForm.reNewPassword" show-password autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="changePasswordVisible = false">取 消</el-button>
        <el-button type="primary" @click="doChangePassword">确 定</el-button>
      </div>
    </el-dialog>
    <!--绑定邮箱对话框-->
    <el-dialog title="修改邮箱"
               :visible.sync="changeEmailVisible"
               width="40%">
      <div
          v-loading="emailIsLoading"
          element-loading-text="发送邮件中，请稍后">
        <div class="emailClass">
          <span>邮箱地址：</span>
          <el-input v-model="emailValue" style="width: 70%;"></el-input>
        </div>
        <div class="emailDialogFooter">
          <el-button @click="changeEmailVisible = false">取 消</el-button>
          <el-button type="primary" @click="sendEmail()">确 定</el-button>
        </div>
      </div>
    </el-dialog>
    <!--修改用户简介对话框-->
    <el-dialog title="修改个人简介" :visible.sync="changeIntroduceVisible">
      <div class="changeBox">
        <span>简介：</span>
        <el-input type="textarea" v-model="changeIntroduceValue" :rows="1" autosize style="width: 70%"></el-input>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="changeIntroduceVisible = false">取 消</el-button>
        <el-button type="primary" @click="doChangeUserIntroduce">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import myUpload from 'vue-image-crop-upload';
import Head from "@/components/home/Head";  // 导入文件上传组件

export default {
  name: "UserInfo",
  components:{Head, myUpload},
  data(){
    // 验证旧密码
    let checkOldPassword = async (rule, value, callback) => {
      // 通过用户id和value判断旧密码是否正确
      let {data:res} = await this.$http.get("/user/checkOldPassword", {
        params:{
          userId:this.userInfo.userId,
          oldPassword:value
        }
      });
      if (res.code!==200){
        return callback(new Error(res.msg));
      }
      callback();
    }
    // 验证确认密码
    let checkRePassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    }
    return{
      showDialog: false,
      uploadUrl:'/user/uploadHeader',   // 上传头像接口
      headers:{
        token:window.sessionStorage.getItem("token")
      },
      userInfo:[],   // 用户信息列表
      // 修改昵称对话框
      changeNameVisible:false,  // 修改名称对话框是否可见
      changeNameValue:'',    // 修改名称的值
      // 修改密码对话框
      changePasswordVisible:false,
      passwordForm: {
        oldPassword: '',  // 旧密码
        newPassword: '',  // 新密码
        reNewPassword: ''  // 确认新密码
      },
      passwordFormRules:{  // 修改密码验证规则
        oldPassword:[
          { required: true, message: '请输入旧密码', trigger: 'blur'},
          { validator: checkOldPassword, trigger: 'blur'}
        ],
        newPassword: [
          { required: true, message: '密码不能为空', trigger: 'blur'},
          { min: 6, max: 16,message: '密码长度为6~16个字符', trigger: 'blur'}
        ],
        reNewPassword:[
          { required: true, message: '请再次输入密码', trigger: 'blur'},
          { validator: checkRePassword, trigger: 'blur'}
        ]
      },
      // 修改简介对话框
      changeIntroduceVisible:false,  // 修改简介对话框是否可见
      changeIntroduceValue:'',  // 修改简介对话框是否可见
      // 修改绑定邮箱对话框
      changeEmailVisible:false,
      emailValue:'',
      emailIsLoading:false  // 加载框是否存在
    }
  },
  computed:{
    // 用户头像路径
    imgUrl:{
      get(){
        return this.$store.state.imgUrl;
      },
      set(value){
        this.$store.state.imgUrl = value; // 修改vuex中的值
      }
    }
  },
  methods:{
    // 获取用户信息,赋值给userInfo
    async getUserInfo(){
      // 通过userid重新获取用户信息
      let {data:res} = await this.$http.get(`/user/getUserInfo`);
      if (res.code!==200){
        return this.$message.error(res.msg);
      }
      this.userInfo = res.data;
      if (this.userInfo.userHeadPath){  // 如果用户设置了头像，将路径从默认路径更改成头像在服务器中的路径
        // this.imgUrl = this.$global.host + this.userInfo.userHeadPath;  // 不在主页面修改用户头像的url，不然后台mybatis有缓存，刷新不会更新头像
        // 修改vuex中的imgUrl
        console.log('刷新页面->'+this.imgUrl);
      }
      // console.log(this.userInfo);
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
      this.imgUrl = this.$global.host + jsonData.data.newHeadPath;
      // console.log('更新头像->'+this.imgUrl);
      // 更新缓存中的用户信息
      this.userInfo.userHeadPath = jsonData.data.newHeadPath;
      // console.log(this.userInfo);
      // console.log(JSON.stringify(this.userInfo));
      window.sessionStorage.setItem("userInfo",JSON.stringify(this.userInfo));
    },
    // 鼠标移入事件
    showBtn(e){
      e.target.querySelector(".changeBtn").style.visibility = "visible"  // 获取当前子元素中对应的元素，并设置为隐藏
    },
    // 鼠标移出事件
    leverBtn(e){
      e.target.querySelector(".changeBtn").style.visibility = "hidden"  // 获取当前子元素中对应的元素，并设置为隐藏
    },
    // 打开修改昵称对话框
    changeNameDialog(){
      // 将用户昵称赋值给
      this.changeNameValue = this.userInfo.userName;
      this.changeNameVisible = true;
    },
    // 修改用户昵称
    async doChangeUserName(){
      // 向后台发送数据，修改用户昵称
      let {data:res} = await this.$http.post("/user/updateUserName", {
        userId: this.userInfo.userId,
        userName: this.changeNameValue
      });
      // console.log(res);
      if (res.code !== 200){
        return this.$message.error(res.msg);
      }
      // 修改当前界面用户信息
      this.userInfo.userName = res.data;
      this.$message.success(res.msg);
      // 修改会话中的userInfo
      let userInfo = JSON.parse(window.sessionStorage.getItem("userInfo"));
      // console.log(userInfo);
      userInfo.userName = res.data;
      window.sessionStorage.setItem("userInfo",JSON.stringify(userInfo));
      this.changeNameVisible = false;  // 关闭对话框
    },
    // 打开修改简介对话框
    changeIntroduceDialog(){
      this.changeIntroduceValue = this.userInfo.userIntroduce;
      this.changeIntroduceVisible = true;
    },
    // 修改用户简介
    async doChangeUserIntroduce(){
      // 向后台发送数据，修改用户昵称
      let {data:res} = await this.$http.post("/user/updateUserIntroduce", {
        userId: this.userInfo.userId,
        userIntroduce: this.changeIntroduceValue
      });
      // console.log(res);
      if (res.code !== 200){
        return this.$message.error(res.msg);
      }
      // 修改当前界面用户信息
      this.userInfo.userIntroduce = res.data;
      this.$message.success(res.msg);
      // 修改会话中的userInfo
      let userInfo = JSON.parse(window.sessionStorage.getItem("userInfo"));
      // console.log(userInfo);
      userInfo.userIntroduce = res.data;
      window.sessionStorage.setItem("userInfo",JSON.stringify(userInfo));
      this.changeIntroduceVisible = false;  // 关闭对话框
    },
    // 修改密码对话框
    changePasswordDialog(){
      this.changePasswordVisible = true;
    },
    // 修改密码操作
    doChangePassword(){
      this.$refs.passwordRef.validate(async valid => {
        if (valid){
          // 向后端发起请求，修改用户密码
          let {data:res} = await this.$http.post("/user/doUpdatePassword", {
            userId: this.userInfo.userId,
            newPassword: this.passwordForm.newPassword
          });
          if (res.code!==200){
            this.$message.error(res.msg);
          }else {
            this.$message.success(res.msg);
          }
          this.changePasswordVisible = false;
        }
      });
    },
    // 打开修改绑定邮箱对话框
    changeEmailDialog(){
      if (this.userInfo.userEmail!=='null'){
        this.emailValue = this.userInfo.userEmail;
      }else {
        this.emailValue = '';
      }
      this.changeEmailVisible = true;
    },
    // 发送邮件到对应的邮箱
    async sendEmail(){
      this.emailIsLoading = true;  // 开启加载中遮罩
      // 对输入框进行验证
      if (!/^([A-z0-9]{6,18})(\w|\-)+@[A-z0-9]+\.([A-z]{2,3})$/.test(this.emailValue)){
        this.emailIsLoading = false;
        return this.$message.warning("请输入正确的邮箱格式");
      }
      let {data:res} = await this.$http.post("/user/sendEmail",this.emailValue);
      this.emailIsLoading = false;
      if (res.code!==200){
        return this.$message.error(res.msg);
      }else {
        // 将返回过来的数据设置成页面上的email
        this.$message.success(res.msg);
      }
      this.changeEmailVisible = false;
    }
  },
  watch:{
    // 监听改变密码框
    changePasswordVisible(newValue,oldValue){
      if (newValue===false){
        setTimeout(()=>{
          this.passwordForm.newPassword = '';
          this.passwordForm.reNewPassword = '';
          this.passwordForm.oldPassword = '';
        },200)
      }
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
  overflow-x: hidden;  // 关闭水平方向的滚动条
  // 头部组件
  .el-header{
    border-bottom: 3px solid #409EFF;
    background-color: #ffffff;
    display: block;
  }
  // 用户信息
  .userInfoContainer{
    width: 90%;
    margin: 0 auto;
    position: relative;  // 方便里面元素定位
    .headImgContainer{
      width: 130px;
      height: 180px;
      position: absolute;
      right: 130px;
      top: 60px;
      text-align: center; // 让div里面的行内元素水平居中
      .el-image{
        width: 130px;
        height: 130px;
        border-radius: 50%;
        box-shadow: 0 0 8px #5e5c5c;
        margin-bottom: 5px;
      }
    }
    // 用户信息盒子
    .userInfoBox{
      .el-card{
        width: 75%;
        // 用户信息后面的操作
        .changeBtn{
          font-size: 2px;
          margin-left: 10px;
          visibility: hidden;
        }
      }
    }
  }
  // 问卷样式
  .el-dialog{
    // 修改邮箱样式
    .emailClass{
      margin-bottom: 10px;
      .el-link{
        font-size: 5px;
        margin-left: 10px;
      }
    }
    .emailDialogFooter{
      margin-top: 20px;
      text-align:right;
    }
  }
}

</style>
