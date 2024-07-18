<template>
  <div>
    <!--面包屑导航区域-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/welcome' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!--卡片视图区域-->
    <el-card class="box-card">
      <!--搜索和添加区域-->
      <!--格栅布局系统-->
      <el-row :gutter="20"> <!--下面组件中每一个组件距离相差20px-->
        <el-col :span="8">
          <el-input placeholder="请输入内容"
                    v-model="queryInfo.query" clearable @clear="getUserList">
            <el-button slot="append" icon="el-icon-search" @click="getUserList"></el-button>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="addDialogVisible=true">添加用户</el-button>
        </el-col>
      </el-row>
      <el-table :data="userList" border="border"
                stripe
                v-loading="isLoading"
                element-loading-text="加载用户中"
      >
        <el-table-column label="#" type="index"></el-table-column>
        <el-table-column label="姓名" prop="userName"></el-table-column>
        <el-table-column label="邮箱" prop="userEmail"></el-table-column>
        <el-table-column label="电话" prop="phone"></el-table-column>
        <el-table-column label="角色" prop="name"></el-table-column>
        <el-table-column label="状态">
          <template slot-scope="scope">  <!--scope.row就相当于这一行的所有数据-->
            <span>{{scope.row.state === 1 ? '正常用户':'已封禁'}}</span>
            &ensp;&ensp;
            <el-switch v-model="scope.row.state"
                       @change="userStateChanged(scope.row)"
                       :active-value = 1
                       :inactive-value = 0
            >
            </el-switch>  <!--可以通过slot-scope中的属性中的row获取这一行的数据-->
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <!--scope.row可以获取这一行的用户的信息-->
            <!--删除按钮-->
            <el-tooltip effect="dark" content="删除用户" placement="top" :enterable="false">
              <el-button type="danger" icon="el-icon-delete" @click="removeUserById(scope.row.id)" circle></el-button>
            </el-tooltip>
            <!--分配角色按钮-->
            <el-tooltip effect="dark" content="分配角色" placement="top" :enterable="false">
              <el-button type="warning" icon="el-icon-setting" circle></el-button>
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
            :page-sizes="[1,2,5,10]"
            :page-size="queryInfo.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </div>
    </el-card>
    <!--添加用户对话框-->
    <el-dialog
        title="添加用户"
        :visible.sync="addDialogVisible"
        width="50%"
        @close="addDialogClosed">
      <!--内容主体区域-->
      <el-form :model="addForm" :rules="addFormRules" ref="addFormRef"
               label-width="80px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="addForm.userName"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="userPassword">
          <el-input v-model="addForm.userPassword"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="rePassword">
          <el-input v-model="addForm.rePassword"></el-input>
        </el-form-item>
      </el-form>
      <!--底部区域-->
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addUser">确 定</el-button>
      </span>
    </el-dialog>
    <!--修改用户对话框-->
    <el-dialog
        title="修改用户"
        :visible.sync="editDialogVisible"
        width="50%"
        @close="editDialogClosed">
      <!--内容主体区域-->
      <el-form :model="editForm" ref="editFormRef"
               :rules="editFormRules" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="editForm.mobile"></el-input>
        </el-form-item>
      </el-form>
      <!--底部区域-->
      <span slot="footer" class="dialog-footer">
    <el-button @click="editDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="editUserInfo">确 定</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'User',
  data () {
    // 验证用户名是否重复
    let checkUsername = async (rule, value, callback) =>
    {
      let {data:res} = await this.$http.get("/user/register/usernameIsExit/"+value);
      if (res.data === 1){
        return callback(new Error("该用户名已注册"));
      }
      callback();
    };
    // 自定义确认密码验证规则
    let checkPassword = (rule, value, callback) => {
      if (value !== this.addForm.userPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    }
    return {
      // 是否正在查找
      isLoading: false,
      // 获取用户列表的参数
      queryInfo: {
        query: '',
        pageNum: 1,        // 当前的页数
        pageSize: 5        // 一页显示的大小
      },
      userList: [],
      total: 0, // 总数据条数
      addDialogVisible: false, // 控制添加用户对话框的显示和隐藏
      addForm: {
        userName: '',
        userPassword: '',
        rePassword:''
      },
      // 添加表单的验证规则对象
      addFormRules: {
        userName: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 10, message: '用户名长度在3-10个字符之间', trigger: 'blur' },
          { validator: checkUsername, trigger: 'blur' }
        ],
        userPassword: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 16,message: '密码长度为6~16个字符', trigger: 'blur'}
        ],
        rePassword:[
          { required: true, message: '请再次输入密码', trigger: 'blur'},
          { validator: checkPassword, trigger: 'blur'}
        ]
      },
      // 控制修改用户对话框的显示与隐藏
      editDialogVisible: false,
      // 修改用户信息对象中的data,这里为空，之后通过查询到的数据复制给这个对象
      editForm: {},
      // 修改表单的验证规则对象
      editFormRules: {
        email: [
          { required: true, message: '请输入用户邮箱', trigger: 'blur' },
        ],
        mobile: [
          { required: true, message: '请输入用户手机', trigger: 'blur' },
        ]
      }
    }
  },
  methods: {
    // 获取用户列表
    async getUserList () {
      this.isLoading = true;
      const { data: res } = await this.$http.get('/admin/getUsers', { // 将获取到的属性中的data子属性命名为res
        params: this.queryInfo
      })
      console.log(res);
      this.isLoading = false;
      if (res.code !== 200) {
        return this.$message.error('数据获取失败')
      }
      this.userList = res.data.users;
      this.total = res.data.total;
      console.log(this.userList);
    },
    // 监听pagesize大小改变
    handleSizeChange (newSize) {
      // console.log(newSize)
      this.queryInfo.pageSize = newSize
      this.getUserList() // 重新获取数据
    },
    // 监听分页页码值改变事件
    handleCurrentChange (newPage) {
      // console.log(newPage)
      this.queryInfo.pageNum = newPage
      this.getUserList()
    },
    // 监听switch状态开关的改变
    userStateChanged (userInfo) {
      console.log(userInfo);
      this.$confirm(userInfo.state===1?'确定要解封用户吗':'确定要封禁用户吗', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const { data: res } = await this.$http.post(`/admin/updateState`,{
          userId:userInfo.userId,
          state:userInfo.state
        });
        console.log(res);
        if (res.code !== 200) {
          this.$message.error('更新用户状态失败')
          userInfo.state = userInfo.state===1?0:1;
          return
        }
        this.$message.success('更新用户状态成功');
      }).catch(() => {
        userInfo.state = userInfo.state===1?0:1;
        this.$message.info("已取消修改");
      });
    },
    // 监听添加用户对话框的关闭事件
    addDialogClosed () {
      // 重置表单
      this.$refs.addFormRef.resetFields()
    },
    // 点击按钮添加新用户
    addUser () {
      // ElementUI中的验证操作
      this.$refs.addFormRef.validate(async value => {
        if (!value) {
          return
        }
        // 向服务器发起请求
        const { data: res } = await this.$http.post('/user/register/registerUser', this.addForm)
        if (res.code !== 200) {
          return this.$message.error('添加用户失败')
        }
        this.$message.success('添加用户成功')
        // 隐藏对话框
        this.addDialogVisible = false
        // 刷新用户列表（重新获取用户列表）
        await this.getUserList()
      })
    },
    // 展示编辑用户对话框
    async showEditDialog (id) {
      // 从服务器中获取用户信息，并且传递给data中的参数
      const { data: res } = await this.$http.get('users/' + id)
      console.log(res)
      this.editForm = res.data
      console.log(this.editForm)
      this.editDialogVisible = true
    },
    // 监听修改用户信息窗口的关闭事件
    editDialogClosed () {
      // 重置表单
      this.$refs.editFormRef.resetFields()
    },
    // 修改用户信息并提交
    editUserInfo () {
      // 预验证
      this.$refs.editFormRef.validate(async valid => {
        if (valid) {
          // 发起修改用户信息的请求
          const { data: res } = await this.$http.put('users/' + this.editForm.id, {
            email: this.editForm.email,
            mobile: this.editForm.mobile
          })
          if (res.status !== 200) {
            this.$message.error('更新用户信息失败')
          }
          // 说明更新用户信息成功,隐藏对话框，重新获取用户列表
          this.editDialogVisible = false
          this.getUserList()
          this.$message.success('更新用户信息成功')
        }
      })
    },
    // 根据id删除对应的用户信息
    async removeUserById (userId) {
      // 询问用户是否删除数据
      const confirmResult = await this.$confirm('此操作将永久删除这个用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).catch(err => { // 捕获异常并且返回出去
        return err
      })
      // 如果用户确认删除，则返回值为字符串confirm
      // 如果用户取消了删除，则返回值为字符串cancel
      console.log(confirmResult)
      if (confirmResult !== 'confirm') {
        return ;
      }
      const { data: res } = await this.$http.delete('users/' + userId)
      if (res.meta.status !== 200) {
        return this.$message.error('删除用户失败')
      }
      this.$message.success('删除用户成功')
      this.getUserList()
    }
  },
  created () {
    this.getUserList()
  }
}
</script>

<style scoped>
.el-card{
  box-shadow: 0 1px 1px rgba(0,0,0,0.15) !important; /*!important 提高指定样式规则的应用优先权（优先级）*/
}
.el-table{
  margin-top: 15px;
  font-size: 12px;
}
.el-pagination{
  margin-top: 15px;
}
</style>
