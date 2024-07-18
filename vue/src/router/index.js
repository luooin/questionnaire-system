import Vue from 'vue'
import VueRouter from 'vue-router'

import adminRoute from "@/router/admin.js";
import homeRoute from "@/router/home.js";

Vue.use(VueRouter)

// 创建一个路由对象，将两个路由文件封装进去
const routes = new Set([...homeRoute,...adminRoute]);

// 将路由对象挂载到router对象中，之后再main.js会引用
const router = new VueRouter({
  mode:'hash',  // 哈希模式
  routes:routes,
});

// 挂载路由导航守卫

router.beforeEach((to, from, next) => {
    // 如果目标路由与当前路由相同，则不进行导航
    if (to.path === from.path) {
      return;
    }
    
    if (to.meta.title) { // 如果路由中设置了标题，将标题设置成网站标题
      document.title = to.meta.title;
    }
    
    // 放行不需要权限控制的路由
    const allowedRoutes = ['/login', '/toRegister', '/admin'];
    if (allowedRoutes.includes(to.path) || 
        to.path.includes("/user/bandEmail/") || 
        to.path.includes("/answerPaper/") || 
        to.path === '/answerSuccess') {
      return next();
    }
    
    // 获取token
    const token = window.sessionStorage.getItem("token");
    if (!token) {
      alert("请先登录再进行操作");
      return next("/login");
    }
    
    // 如果目标路由与当前路由不同，则进行导航
    next(); // 放行
  });
  

// router.beforeEach((to,from,next)=>{
//   // 如果目标路由与当前路由相同，则不进行导航
//   if (to.path === from.path) {
//     return;
//   }  
//   // console.log(to);
//   if (to.meta.title){  // 如果路由中设置了标题，将标题设置成网站标题
//     document.title = to.meta.title;
//   }
//   if (to.path === '/login'){  // 允许跳转到后台登录界面
//     return next();
//   }else if (to.path === '/toRegister'){
//     return next();  // 跳转到注册界面
//   }else if (to.path === '/admin'){  // 跳转到后台登录界面
//     return next();
//   }else if (to.path.includes("/user/bandEmail/")){  // 放行邮箱注册
//     return next();
//   }else if (to.path.includes("/answerPaper/")){ // 放行回答问卷界面
//     return next();
//   }else if (to.path === '/answerSuccess'){   // 放行填写问卷成功界面
//     return next();
//   }
//   // 获取token
//   const token = window.sessionStorage.getItem("token");
//   if (!token){
//     alert("请先登录再进行操作");
//     return next("/login");
//   }
//   next();  // 放行
// })

export default router
