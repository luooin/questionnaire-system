<template>
  <div class="loginbody">
    <div class="logindata"
         v-loading="isLoading"
         element-loading-text="登陆中">
      <div class="logintext">
        <h2>问卷后台管理</h2>
      </div>
      <div class="formdata"
      >
        <el-form ref="form" :model="form" :rules="rules">
          <el-form-item prop="userName">
            <el-input
                v-model="form.userName"
                clearable
                placeholder="请输入账号"
            ></el-input>
          </el-form-item>
          <el-form-item prop="userPassword">
            <el-input
                v-model="form.userPassword"
                clearable
                placeholder="请输入密码"
                show-password
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
<!--      <div class="tool">-->
<!--        <div>-->
<!--          <el-checkbox v-model="checked" @change="remenber"-->
<!--          >记住密码</el-checkbox-->
<!--          >-->
<!--        </div>-->
<!--        <div>-->
<!--          <span class="shou" @click="forgetpas">忘记密码？</span>-->
<!--        </div>-->
<!--      </div>-->
      <div class="butt">
        <div class="msg">
          <span>只有前台账号？</span>
          <el-link type="primary" @click="toLogin()">去前台</el-link>
        </div>
        <el-button type="primary" @click.native.prevent="login('form')">登录</el-button>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: "login",
  data() {
    return {
      form: {
        userName: "",
        userPassword: "",
      },
      rules: {
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
      isLoading:false
    };
  },
  methods: {
    login(form) {
      this.isLoading = true;
      this.$refs[form].validate(async (valid) => {
        if (valid) {
          // 向后台发送请求，登录
          let {data:res} = await this.$http.post("/admin/login",this.form);
          if (res.code!==200){  // 说明未经授权
            this.isLoading = false;
            return this.$message.error(res.msg);
          }
          this.$message.success('登录成功')
          console.log(res.data.userInfo);
          // 将登录成功后后端传递的token保存在SessionStorage中
          window.sessionStorage.setItem('token', res.data.token)
          window.sessionStorage.setItem('userInfo',JSON.stringify(res.data.userInfo));
          // 通过路由跳转到home页面
          await this.$router.push('/adminHome');
        }
        this.isLoading = false;
      });
    },
    // 跳转到前台登录界面
    toLogin(){
      this.$router.push("/");
    }
  },
};
</script>

<style scoped lang="less">
.loginbody {
  width: 100%;
  height: 100%;
  min-width: 1000px;
  background-image: url("../../assets/img/login.jpg");
  background-size: 100% 100%;
  background-position: center center;
  overflow: auto;
  background-repeat: no-repeat;  // 背景不平铺，只显示一张
  position: fixed;
  line-height: 100%;
}

// 登录标题
.logintext {
  margin-bottom: 20px;
  line-height: 50px;
  text-align: center;
  font-size: 30px;
  font-weight: bolder;
  color: white;
  text-shadow: 2px 2px 4px #000000;
}

.logindata {
  width: 400px;
  height: 300px;
  transform: translate(-50%,-50%);
  position: absolute;
  left: 50%;
  top: 50%;
  background-color: rgba(0,54,81,0.7);
  border-radius: 40px;
  padding: 10px;

}

.tool {
  display: flex;
  justify-content: space-between;
  color: #606266;
}

.butt {
  margin-top: 10px;
  padding-right: 20px;;
  text-align: right;
  .msg{
    display: inline-block;
    margin-right: 10px;
  }
}

.shou {
  cursor: pointer;
  color: #606266;
}

// 登录中状态遮罩
::v-deep .el-loading-mask{
  border-radius: 40px;   // 圆角边框
  background-color: rgba(45,50,59,0.5);
}
</style>

