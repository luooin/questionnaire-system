import Vue from 'vue'
import Vuex from 'vuex'
import axios from "axios";

Vue.use(Vuex)

export default new Vuex.Store({
  // 创建state-用于存储数据
  state: {
    paperId:"",   // 问卷id
    myPaper: [],       // 我的问卷列表
    paperAddSwitch:false,  // 对话框开关
    paperTitle:"新建问卷", // 问卷标题
    questionList:[],  // 问题列表
  },

  // action用于响应组件中的动作(业务逻辑),可以调用后端的组件
  actions: {
    // 获取我的问卷
    async getMyPaper(context) {
      let {data:res} = await axios.get("/paper/getMyPaper");
      if (res.code !== 200){
        return this.$message.error("获取问卷失败，请稍后再试");
      }
      context.commit("GET_PAPER",res.data);  // 调用mutations中的方法，对state中的数据进行更改
    },
  },

  // mutations用于操作数据(state)，里面可以对state中的数据进行更改
  mutations: {
    // 获取问卷
    GET_PAPER(state,paper){
      this.state.myPaper = paper;
    },
    // 更新问卷id的值
    UPDATE_PAPER_ID(state,paperId){
      state.paperId = paperId;
    },
    // 更新头像url
    GET_HEADER(){

    }
  },
  modules: {
  }
})
