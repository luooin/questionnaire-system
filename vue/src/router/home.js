import Login from "@/components/home/Login";
import Register from "@/components/home/Register";
import Home from "@/components/home/Home";
import UserInfo from "@/components/user/UserInfo";
import AnswerPaper from "@/components/home/AnswerPaper";
import AnswerSuccess from "@/components/home/AnswerSuccess";
import AdminLogin from "@/components/admin/AdminLogin";
import AdminHome from "@/components/admin/AdminHome"
import Welcome from "@/components/admin/Welcome";
import AdminUsers from "@/components/admin/user/AdminUsers"
import AdminPaper from "@/components/admin/AdminPaper";
import BandEmail from "@/components/home/BandEmail";

const homeRoute = [
    {
        path: '/',
        redirect: '/login',
    },
    {
        path:'/login',
        component:Login,
        meta:{
            title:'登录'
        }
    },
    // 跳转到注册界面
    {
        path: '/toRegister',
        component: Register,
        meta:{
            title:'注册'
        }
    },
    // 跳转到系统主页
    {
        path: '/toHome',
        component: Home,
        meta:{
            title:'我的问卷'
        }
    },
    // 登出
    {
        path: '/logout',
        redirect:'/login'
    },
    // 进入用户信息界面
    {
        path: '/userInfo',
        component: UserInfo,
        meta:{
            title:'个人信息'
        }
    },
    // 跳转到回答问卷界面
    {
        name: 'toAnswerPaper',
        path: "/answerPaper/:paperId",  // 占位符声明接收param参数
        component: AnswerPaper,
        meta:{
            title:'填写问卷'
        }
    },
    // 答题成功后跳转页面
    {
        path: "/answerSuccess",
        component: AnswerSuccess,
        meta:{
            title:'答题成功'
        }
    },
    // 验证邮箱地址
    {
        path: "/user/bandEmail/:email/:userId/:key",
        component: BandEmail,
        meta:{
            title:'验证邮箱'
        }
    },
    // ----------admin--------------
    // 后台登录界面
    {
        path: "/admin",
        component: AdminLogin,
        meta:{
            title:'后台登录'
        }
    },
    // 跳转到后台主页
    {
        path:"/adminHome",
        component: AdminHome,
        redirect: "/welcome",   // 每次进入后台主页面就重定向到欢迎界面
        children:[
            // 后台欢迎界面
            {
                path:"/welcome",
                component:Welcome,
                meta:{
                    title:'数据统计'
                }
            },
            // 后台用户管理界面
            {
                path: '/adminUsers',
                component: AdminUsers,
                meta:{
                    title:'用户管理'
                }
            },
            // 问卷管理
            {
                path: '/paperList',
                component: AdminPaper,
                meta:{
                    title:'问卷管理'
                }
            }
        ]
    },
]

export default homeRoute;
