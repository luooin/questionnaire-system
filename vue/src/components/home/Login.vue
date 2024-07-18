<template>
  <div class="login_container">
    <div class="login_box"
         v-loading="isLogging"
         element-loading-text="登陆中">
      <!--头像区域-->
      <div class="avater_box">
        <img :src="imgUrl" alt="默认头像">
      </div>

      <!--登录表单区域-->
      <el-form class="login_form" ref="loginFormRef" label-width="0px" :rules="loginFormRules" :model="loginForm">
        <!--用户名-->
        <el-form-item prop='userName'>
          <el-input prefix-icon="el-icon-user-solid" v-model="loginForm.userName"></el-input>
        </el-form-item>
        <!--密码-->
        <el-form-item prop='userPassword'>
          <el-input prefix-icon="el-icon-lock" v-model="loginForm.userPassword" type="password"></el-input>
        </el-form-item>
        <!--按钮区域-->
        <div class="formFoot">
          <el-form-item class="btns">
            <div class="tips">
              <span>还没有账号？</span>
              <el-link type="primary" @click="toRegister">去注册</el-link>
            </div>
<!--            <div class="forgetPassword">-->
<!--              <el-link type="primary" @click="toRegister">忘记密码</el-link>-->
<!--            </div>-->
            <el-button type="primary" @click="login">登录</el-button>
            <el-button type="info" @click="resetLoginForm">重置</el-button>
          </el-form-item>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data () {
    return {
      // 登录表单的数据绑定对象
      loginForm: {
        userName: '',
        userPassword: ''
      },
      loginFormRules: {
        // 验证用户名是否合法
        userName: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
        ],
        // 验证密码是否合法
        userPassword: [
          { required: true, message: '请输入登录密码', trigger: 'blur' },
          { min: 6, max: 11, message: '长度在 6 到 11 个字符', trigger: 'blur' }
        ]
      },
      imgUrl:'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      isLogging:false
    }
  },
  methods: {
    // 点击重置登录表单
    resetLoginForm () {
      this.$refs.loginFormRef.resetFields() // elementUI中内置的重置表单内容的方法
    },
    // 登录操作
    login () {
      this.$refs.loginFormRef.validate(async vaild => { // vaild是内置的验证对象，如果之前配置的验证没有问题就为true，否则为false
        if (vaild) {
          this.isLogging = true;  // 开启登陆中的遮罩
          // this.$router.push('/doLogin')
          // { data: res } 将返回值中的data子元素重命名为res  !!!
          let { data: res } = await this.$http.post('/login', this.loginForm) // 通过axios进行请求，传递表单参数
          if (res.code !== 200) {
            this.isLogging = false;
            return this.$message.error(res.msg)
          } else {
            this.$message.success('登录成功')
            // console.log(res.data.userInfo);
            // 将登录成功后后端传递的token保存在SessionStorage中
            window.sessionStorage.setItem('token', res.data.token)
            window.sessionStorage.setItem('userInfo',JSON.stringify(res.data.userInfo));
            // 通过路由跳转到home页面
            await this.$router.push('/toHome')
          }
          this.isLogging = false;
        }
      })
    },
    // 跳转到注册界面
    toRegister(){
      // 跳转到注册界面
      this.$router.push("/toRegister")
    },
    // 获取用户头像
    getImgUrl(){
      let userInfoStr = window.sessionStorage.getItem("userInfo");
      let userInfo = JSON.parse(userInfoStr);
      // console.log(userInfo);
      if (userInfo && userInfo.userHeadPath){
        this.imgUrl = userInfo.userHeadPath;
      }
      // console.log(this.imgUrl);
    }
  },
  created() {
    // 从会话中获取用户头像
    this.getImgUrl();
  }
}
</script>

<style scoped lang="less">
.login_container{
  background:url("/src/assets/img/admin.jpg");
  height: 100%;
  width: 100%;
  background-size: cover;  // 图片大小自适应调整
}
.login_box{
  width: 450px;
  height: 300px;
  background-color: #fff;
  border-radius: 45px;   // 圆角边框
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%,-50%);  // 相对位移
  // 语法嵌套
  // 头像区域
  .avater_box{
    width: 130px;
    height: 130px;
    border: 1px solid #eee;
    border-radius: 50%;
    background-color: #ffffff;
    padding: 10px;
    box-shadow: 0 0 15px #ddd;  // 阴影
    position: absolute;
    left: 50%;
    transform: translate(-50%,-50%);   // 向左移动自身宽度一半的距离
    z-index: 3000;  // 设置显示的图像在哪个层次，不被登陆中的遮罩挡住
    img{
      width: 100%;
      height: 100%;
      border-radius: 50%;
      background-color: #eeeeee;
    }
  }
  // 登录表单区域
  .login_form{
    position: absolute;
    bottom: 0;
    width: 100%;
    padding: 0 10px;
    box-sizing: border-box;
    .formFoot{
      .btns{
        display: flex;   // 弹性布局
        justify-content: flex-end;   // 居右对齐
        .tips{
          height: 40px;
          display: inline-block;
          margin-right: 10px;
          font-size: 10px;
          line-height: 40px;
        }
        // 忘记密码
        .forgetPassword{
          display: inline-block;
          margin-right: 10px;
          font-size: 10px;
          line-height: 40px;
        }
      }
    }
  }
  // 登录中状态
  ::v-deep .el-loading-mask{
    border-radius: 45px;   // 圆角边框
  }
}
</style>
