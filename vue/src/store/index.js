import Vue from 'vue'
import Vuex from 'vuex'
import axios from "axios";

Vue.use(Vuex)

export default new Vuex.Store({
  // 创建state-用于存储数据
  state: {
    imgUrl: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"
  },

  // action用于响应组件中的动作(业务逻辑),可以调用后端的组件
  actions: {

  },

  // mutations用于操作数据(state)，里面可以对state中的数据进行更改
  mutations: {
    // 更新头像url
    GET_HEADER(){

    }
  }
})
