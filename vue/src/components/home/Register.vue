<template>
  <div class="formContainer">
    <div class="formBox">
      <p>用户注册</p>
      <el-divider></el-divider>
      <el-form ref="registerFormRef" :model="form" :rules="registerFormRules" label-width="80px">
        <el-form-item label="用户名" prop='userName'>
          <el-input v-model="form.userName"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="userPassword">
          <el-input v-model="form.userPassword" type="password"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="rePassword">
          <el-input v-model="form.rePassword" type="password"></el-input>
        </el-form-item>
        <!--按钮区域-->
        <div class="formFoot">
          <el-form-item class="btns">
            <div class="tips">
              <span>已经有账号?</span>
              <el-link type="primary" @click="toLogin">去登录</el-link>
            </div>
            <el-button type="primary" @click="registerUser()">注册</el-button>
          </el-form-item>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Register",
  data(){
    // 验证用户名是否重复
    let checkUsername = async (rule, value, callback) =>
    {
      let {data:res} = await this.$http.get("/user/register/usernameIsExit/"+value);
      if (res.data === 1){
        return callback(new Error("该用户名已注册"));
      }
      callback();
    };
    // 确认密码
    let checkPassword = (rule, value, callback) => {
      if (value !== this.form.userPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    }
    return{
      form:{
        userName:'',
        userPassword:'',
        rePassword:''
      },
      // 登录验证规则
      registerFormRules:{
        // 验证用户名是否没问题
        userName: [
          { required: true, message: '请输入用户名', trigger: 'blur'},
          { min: 4, max: 10,message: '长度在6-10个字符', trigger: 'blur'},
          // { pattern: /^\d{6,10}$/,message: '请输入6-10位的数字', trigger: 'blur'  },  // 正则表达式验证法，userId为6-10位的数字
          { validator: checkUsername, trigger: 'blur' }
        ],
        userPassword: [
          { required: true, message: '密码不能为空', trigger: 'blur'},
          { min: 6, max: 16,message: '密码长度为6~16个字符', trigger: 'blur'}
        ],
        rePassword:[
          { required: true, message: '请再次输入密码', trigger: 'blur'},
          { validator: checkPassword, trigger: 'blur'}
        ]
      }
    }
  },
  methods:{
    // 跳转到登录界面
    toLogin(){
      this.$router.push("/login");
    },
    registerUser() {
      this.$refs.registerFormRef.validate(async valid => {
        if (valid) {
          // 向后台发送请求，添加一个用户
          let {data : res} = await this.$http.post("/user/register/registerUser",this.form);
          console.log(res);
          if (res.code !== 200){  // 说明注册失败
            return this.$message.error(res.msg);
          }
          this.$router.push('/login');
          this.$message.success(res.msg);
        }
      });
    },
  }
}
</script>

<style scoped lang="less">
// 注册容器，铺满界面
.formContainer{
  width: 100%;
  height: 100%;
  background: url("/src/assets/img/regirect_background.png");
  background-size: cover;
  // 注册表单盒子
  .formBox{
    width: 450px;
    height: 350px;
    position: absolute;
    left: 48%;
    top: 50%;
    transform: translate(-50%,-50%);  // 相对位移
    background-color: #fff;
    border-radius: 45px;   // 圆角边框
    padding: 10px 10px 10px 0;
    text-align: center;  // 左右居中
    p{
      font-size: 20px;
    }
    .el-form{
      padding: 0 10px;
      .formFoot{
        .btns{
          display: flex;   // 弹性布局
          justify-content: flex-end;   // 居右对齐
          .tips{
            height: 40px;
            display: inline-block;
            margin-right: 10px;
            line-height: 30px;
            span{
              font-size: 12px;
              margin: 5px;
            }
          }
        }
      }
    }
  }
}
</style>
