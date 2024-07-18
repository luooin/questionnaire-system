<template>
  <div class="header">
    <div>
      <el-link @click="toHome" class="headTitle">在线问卷调查系统</el-link>
    </div>
    <div class="head_userInfo">
      <!--用户头像-->
      <el-avatar :size="45" :src="imgUrl"></el-avatar>
      <!--用户下拉菜单-->
      <div class="dropDownMenu">
        <el-dropdown @command="DownMenuEvent">
          <!--给下拉菜单添加点击事件-->
          <span class="el-dropdown-link">
            {{ username }}<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-user-solid" command="userInfo"
              >用户信息</el-dropdown-item
            >
            <el-dropdown-item icon="el-icon-switch-button" command="logout"
              >退出登录</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Head",
  data() {
    return {
      username: "用户名",
      userId: "",
    };
  },
  computed: {
    imgUrl: {
      get() {
        return this.$store.state.imgUrl;
      },
      set(value) {
        this.$store.state.imgUrl = value; // 修改vuex中的值
      },
    },
  },
  methods: {
    // 下拉菜单事件
    DownMenuEvent: function (value) {
      if (value === "logout") {
        // 退出登录
        // 删除缓存中的token
        window.sessionStorage.removeItem("token");
        // 删除缓存中存的用户信息
        window.sessionStorage.removeItem("userInfo");
        // 删除保存的路径
        window.sessionStorage.removeItem("savePath");
        this.$router.push("/logout");
        this.$message.info("退出登录成功");
        this.delRedisKey();
        return;
      }
      if (value === "userInfo" && this.$route.path !== "/userInfo") {
        this.$router.push("userInfo");
        return;
      }
    },
    // 向后端发起请求，删除当前userid
    async delRedisKey() {
      // 向后端发送请求，删除redis中的key
      let { data: res } = await this.$http.post("/logout2", this.userId);
      // console.log(res);
    },
    // 跳转到系统主页
    toHome() {
      if (this.$route.path !== "/toHome") {
        this.$router.push("/toHome");
      }
      //this.$router.push("/toHome");
    },
    // 获取用户头像路径
    getUserSimpleInfo() {
      let userInfo = JSON.parse(window.sessionStorage.getItem("userInfo"));
      // console.log(userInfo);
      if (userInfo.userHeadPath) {
        // 如果用户设置了头像，将路径从默认路径更改成头像在服务器中的路径
        this.imgUrl = this.$global.host + userInfo.userHeadPath;
      }
      if (userInfo.userName) {
        this.username = userInfo.userName;
      }
      if (userInfo.userId) {
        this.userId = userInfo.userId;
      }
    },
  },
  created() {
    this.getUserSimpleInfo();
  },
};
</script>

<style scoped lang="less">
// 头部组件
.header {
  align-items: center;
  padding: 0 20px;
  width: 100%;
  height: 60px;
  .headTitle {
    font-size: 20px;
    color: black;
    height: 60px;
    line-height: 60px;
  }
  // 用户信息区域
  .head_userInfo {
    display: flex; // 弹性布局
    justify-content: flex-end; // 弹性布局，靠右对齐
    align-items: center; // 这个弹性盒子是水平分部，所以这里是上下居中

    position: absolute;
    right: 20px;
    top: 10px;
    width: 220px;
    // 下拉菜单div
    .dropDownMenu {
      height: 30px;
      background-color: #fff5f5;
      border-radius: 50px; // 圆角弧度
      margin: 0 10px;
      padding: 0 5px 0 10px;
      display: flex; // 弹性布局
      align-items: center; // 这个弹性盒子是水平分部，所以这里是上下居中
      cursor: pointer;
      // 下拉菜单
      .el-dropdown {
        font-size: 13px;
        color: #0a85b3;
      }
    }
  }
}
</style>
