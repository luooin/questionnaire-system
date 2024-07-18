<template>
  <el-container class="home-container">
    <el-header>
      <div>
        <el-link class="headTitle" @click="toAdminHome"
          >问卷调查系统后台管理系统</el-link
        >
      </div>
      <div>
        <el-button type="primary" @click="toHome" size="small"
          >跳转到前台</el-button
        >
        <el-button type="primary" @click="logout" size="small"
          >注销登录</el-button
        >
      </div>
    </el-header>
    <!--页面主体-->
    <el-container>
      <!--侧边栏-->
      <el-aside :width="isCollapse ? '64px' : '200px'">
        <div class="toggle-button" @click="toggleSwitch">|||</div>
        <!--状态开关按钮-->
        <!--侧边栏菜单-->
        <el-menu
          default-active="2"
          class="el-menu-vertical-demo"
          background-color="rgba(255,137,28,0.5)"
          text-color="black"
          router
          :collapse="isCollapse"
          :collapse-transition="false"
          active-text-color="#ffffff"
        >
          <!--菜单列表-->
          <el-menu-item v-for="menu in menuList" :index="menu.id + ''">
            <i :class="iconsObj[menu.id]"></i>
            <span slot="title">{{ menu.menuName }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <!--主题路由占位符-->
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      menuList: [
        // 这里暂时将菜单页面写死
        {
          id: "welcome",
          menuName: "数据统计",
        },
        {
          id: "adminUsers",
          menuName: "用户管理",
        },
        {
          id: "paperList",
          menuName: "问卷管理",
        },
      ],
      iconsObj: {
        // 图标列表，每一个问题id对应一个图标
        welcome: "el-icon-s-data", // 数据统计图标
        adminUsers: "el-icon-user-solid", // 用户信息图标
        paperList: "el-icon-document", // 问卷信息图标
      },
      isCollapse: false, // 是否折叠，默认不折叠
      activePath: "", // 保存的页面的位置
      userId: "", // 用户id
    };
  },
  methods: {
    // 向后端发起请求，删除当前redis中的userid
    async delRedisKey() {
      // 向后端发送请求，删除redis中的key
      let { data: res } = await this.$http.post("/logout2", this.userId);
      console.log(res);
    },
    // 后台登出
    logout() {
      // 删除缓存中的token
      window.sessionStorage.removeItem("token");
      // 删除缓存中存的用户信息
      window.sessionStorage.removeItem("userInfo");
      // 删除保存的路径
      window.sessionStorage.removeItem("savePath");
      this.$router.push("/admin");
      this.$message.info("退出登录成功");
      this.delRedisKey(); // 往后端发起请求，删除redis中的登录信息
      return;
    },
    // 跳转到前台页面
    toHome() {
      this.$router.push("/toHome");
    },
    // 点击按钮，切换菜单的折叠与展开
    toggleSwitch() {
      this.isCollapse = !this.isCollapse;
    },
    // 保存当前页面的位置
    saveNavState(activePath) {
      // 将获取到的参数存到缓存中
      window.sessionStorage.setItem("savePath", activePath);
      // 刷新激活状态
      this.activePath = activePath;
    },
    // 跳转到后台主页
    toAdminHome() {
      if (this.$route.path !== "/adminHome" && this.$route.path !=="/welcome") {
        this.$router.push("/adminHome");
      }
    },

    // 获取用户信息
    getUserId() {
      let userInfo = JSON.parse(window.sessionStorage.getItem("userInfo"));
      if (userInfo.userId) {
        this.userId = userInfo.userId;
      }
    },
  },
  // 如果组件被创建了执行下面的钩子函数
  created() {
    this.activePath = window.sessionStorage.getItem("savePath");
    this.getUserId();
  },
};
</script>

<style scoped lang="less">
.el-header {
  background-color: rgba(255,137,28,0.8);
  /*将head定义为弹盒子*/
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #ffffff;
}
.el-aside {
  background-color: rgba(255,137,28,0.8);
}
.el-menu {
  border-right: none;
}
.home-container {
  height: 100%;
  width: 100%;
  overflow-y: hidden;
  // 标题
  .headTitle {
    font-size: 20px;
    color: black;
    height: 60px;
    line-height: 60px;
  }
}
.toggle-button {
  height: 30px;
  background-color: rgba(255,137,28,0.8);
  font-size: 15px;
  inline-size: 100%;
  line-height: 30px;
  color: #ffffff;
  text-align: center;
  letter-spacing: 0.2em; // 设置文本之间的间距
  cursor: pointer;
}
</style>
