const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
    
    
    transpileDependencies: true,
    devServer:{
        //设置端口为80
        port:80,
        proxy:'http://localhost:8080'   // 配置代理服务器(本地运行)
        // proxy:'http://124.223.117.228:8001'   // 配置代理服务器(云端)
    },
    publicPath:"./"
})
