<template>
  <div class="paper-container">
    <!--面包屑导航区域-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/welcome' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>问卷管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!--卡片视图区域-->
    <el-card class="box-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input placeholder="请输入内容"
                    v-model="queryInfo.query" clearable @clear="getPaperList">
            <el-button slot="append" icon="el-icon-search" @click="getPaperList"></el-button>
          </el-input>
        </el-col>
      </el-row>
      <el-table :data="paperList" border="border" stripe>
        <el-table-column label="#" type="index"></el-table-column>
        <el-table-column label="问卷标题" prop="title"></el-table-column>
        <el-table-column label="发布问卷用户" prop="userName"></el-table-column>
        <el-table-column label="创建时间" prop="createTime"></el-table-column>
        <el-table-column label="状态">
          <template slot-scope="scope">
            <span>{{scope.row.paperStatus===-1?'已封禁':'正常'}}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <!--修改按钮-->
            <!--scope.row可以获取这一行的用户的信息-->
            <el-tooltip effect="dark" content="详细问卷" placement="top" :enterable="false">
              <el-button type="primary" @click="toPaperView(scope.row.paperId)" icon="el-icon-view" circle></el-button>
            </el-tooltip>
            <!--封禁按钮-->
            <el-tooltip effect="dark" v-if="scope.row.paperStatus!==-1" content="封禁问卷" placement="top" :enterable="false">
              <el-button type="danger" @click="banPaper(scope.row.paperId)" icon="el-icon-minus" circle></el-button>
            </el-tooltip>
            <!--解除封禁按钮-->
            <el-tooltip effect="dark" v-if="scope.row.paperStatus===-1" content="解除封禁" placement="top" :enterable="false">
              <el-button type="info" @click="noBanPaper(scope.row.paperId)" icon="el-icon-s-help" circle></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
      <!--分页区域-->
      <div class="block">
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="queryInfo.pagenum"
            :page-sizes="[5,10]"
            :page-size="queryInfo.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "AdminPaper",
  data(){
    return{
      paperList:[],   // 问卷列表
      total:0,   // 总共有多少条数据
      queryInfo:{
        query: '',
        // 当前的页数
        pageNum: 1,
        // 一页显示的大小
        pageSize: 5
      }
    }
  },
  methods:{
    // 获取问卷列表
    async getPaperList(){
      let {data:res} = await this.$http.get("/admin/getPapers",{
        params:this.queryInfo
      });
      console.log(res);
      if (res.code!==200){
        return this.$message.error(res.msg);
      }
      this.paperList = res.data.paperList;
      this.total = res.data.total;
    },
    // 监听pageSize大小改变
    handleSizeChange (newSize) {
      // console.log(newSize)
      this.queryInfo.pageSize = newSize
      this.getPaperList(); // 重新获取数据
    },
    // 监听分页页码值改变事件
    handleCurrentChange (newPage) {
      console.log(newPage)
      this.queryInfo.pageNum = newPage
      this.getPaperList();
    },
    // 封禁一个问卷
    banPaper(paperId){
      this.$confirm('确定要封禁这个问卷吗', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        let {data:res} = await this.$http.post("/admin/banPaper", paperId);
        console.log(res);
        if (res.code!==200){
          return this.$message.error(res.msg);
        }
        this.$message.success(res.msg);
        // 封禁问卷之后重新获取问卷
        await this.getPaperList();
      }).catch(() => {
        this.$message.info("已取消封禁");
      });
    },
    // 解除封禁问卷
    noBanPaper(paperId){
      this.$confirm('确定要解除这个问卷的封禁吗', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        let {data:res} = await this.$http.post("/admin/noBanPaper", paperId);
        console.log(res);
        if (res.code!==200){
          return this.$message.error(res.msg);
        }
        this.$message.success(res.msg);
        // 解除封禁问卷之后重新获取问卷
        await this.getPaperList();
      }).catch(() => {
        this.$message.info("已取消封禁");
      });
    },
    // 跳转到回答问卷界面
    toPaperView(paperId){
      this.$router.push({
            name:"toAnswerPaper",
            params:{
              paperId: paperId,
            }
          }
      );
    },
  },
  created() {
    // 获取问卷列表
    this.getPaperList();
  }
}
</script>

<style scoped lang="less">
.paper-container{
  .el-table{
    margin-top: 20px;
  }
}
</style>
