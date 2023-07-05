<template>
  <Transition name="left-to-right">
  <el-container v-show="loading">
    <el-header>

      <div class="head-title">
        <div class="logo"><img src="~@/assets/images/logo.png" width="79" height="45"/></div>
        <h2>客户分层</h2>
      </div>
      <el-form :inline="true" class="search-form">
        <el-form-item label="">
          <el-input
              placeholder="请输入客户名称"
              prefix-icon="el-icon-search"
               v-model="queryParams.name">
          </el-input>

        </el-form-item>
        <el-form-item label="">
          <el-input
              placeholder="请输入备注"
              prefix-icon="el-icon-search"
              v-model="queryParams.remark">
          </el-input>
        </el-form-item>
        <el-form-item label="">
          <el-input
              placeholder="请输入描述"
              prefix-icon="el-icon-search"
              v-model="queryParams.desc">
          </el-input>

        </el-form-item>
        <el-form-item label="">
          <el-cascader
              @change="tagChange"
              :options="tags"
              :props=" { multiple: true,value: 'id', label: 'label', children: 'children' }"
              collapse-tags
              style="width: 300px"
              placeholder="请选择标签"
              clearable></el-cascader>
        </el-form-item>
        <el-form-item label="">
          <el-button type="primary"  @click="getList()">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-header>
    <el-main>
      <div class="count">
        共{{ totalCount }}个客户
      </div>
      <el-table
          :data="tableData"
          border
          @row-click="handleClick"
          style="flex: 1">
        <!-- <el-table-column
          fixed="left"
          label="操作"
          width="100">
          <template slot-scope="scope">
            <el-button @click="handleClick(scope.row)" type="text" size="small">查看</el-button>
            <el-button type="text" size="small">编辑</el-button>
          </template>
        </el-table-column> -->
        <el-table-column
            prop="name"
            label="备注名称">
        </el-table-column>
        <el-table-column
            prop="gender"
            label="客户性别"
            width="120" :formatter="genderFormatter">
        </el-table-column>
        <el-table-column
            prop="tagName"
            label="客户标签"
           >
        </el-table-column>
        <el-table-column
            prop="description"
            label="客户描述">
        </el-table-column>
        <el-table-column
            prop="createDate"
            label="日期"
            width="150" :formatter="dateFormatter">
        </el-table-column>
        <el-table-column
            prop="userName"
            label="添加人"
            width="120">
        </el-table-column>
      </el-table>
      <pagination
          v-show="totalCount>0"
          :total="totalCount"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
    </el-main>
  </el-container>
  </Transition>
</template>

<script>
import Vue from "vue";
import {getList} from "../../../api/customer";
import Pagination from '../../../components/Pagination'
import {getTagList} from "../../../api/tag";
export default {
  name: "vector-table",
  components: {
    Pagination
  },
  data(){
    return{
        tableData: [],
        loading: false,
        queryParams:{
          name: '', remark: '', desc: '', gender: -1,
          pageNum:1,
          pageSize:8,
          tags:[]
        },
        totalCount:0,
        tags:[],
      }
    }
  ,methods: {
    tagChange(item){
      this.queryParams.tags = []
      for (let i = 0; i < item.length; i++) {
        this.queryParams.tags.push(item[i][1]);
      }
    },
    handleClick(row) {
      console.log(row);
      this.$router.push({path:'/vector/info?externalUserid='+row.externalUserid+'&labelType='+row.labelType})
    },
    getList: function () {
      getList(this.queryParams).then((response) => {
        this.tableData = response.data.list;
        this.totalCount = response.data.totalCount;

      })
    },
    getTags(){
      getTagList().then((response) => {
        const tags = response.data;
        const tree = [];
        for (let i = 0; i < tags.length; i++) {
          tree.push({
            id: tags[i].groupId,
            label: tags[i].groupName,
            children: []
          })
          tags[i].tagList.forEach((item) => {
            tree[i].children.push({
              label: item.name,
              id: item.tagId
            })
          })
        }
        console.log(tree)
        this.$set(this, 'tags', tree)
      })
    },
    dateFormatter: function (row, column) {
      var date = new Date(row.createDate); //时间戳为10位需*1000，时间戳为13位的话不需乘1000
      let Y = date.getFullYear() + '-';
      let M = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) + '-' : date.getMonth() + 1 + '-';
      let D = date.getDate() < 10 ? '0' + date.getDate() + ' ' : date.getDate() + ' ';
      let h = date.getHours() < 10 ? '0' + date.getHours() + ':' : date.getHours() + ':';
      let m = date.getMinutes() < 10 ? '0' + date.getMinutes() + ':' : date.getMinutes() + ':';
      let s = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds();
      return Y + M + D;
    },
    genderFormatter: function (row) {
      let gender = '未知';
      if (row.gender === 1) {
        gender = '男';
      } else if (row.gender === 2) {
        gender = '女';
      }
      return gender;
    },
    formatUser: function () {
      return '--';
    }
  },
  computed: {},
  created: function () {


  },
  activated() {
    console.log("activated")
    this.loading = true;
    this.getList();
    this.getTags();
  },
  deactivated() {
    console.log("deactivated")
    this.loading = false;

  }
}
</script>

<style scoped>
.el-container .is-vertical {
  padding: 0;
}



.el-header {
  position: relative;
  padding-left: 53px;
  height: 142.986px !important;
  margin-bottom: 15px;
  background: #FFFFFF;
  border-radius: 10px;
}

.el-header .logo {
  margin-right: 28px;

}

.head-title {
  position: absolute;

  left: 53px;
  top: 30px;
  width: 235px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.head-title h2{
  font-size: 32px;
 font-weight: 600;
}

.el-main {
  background: #FFFFFF;
  border-radius: 10px;
  padding: 10px 10px 30px 10px;
  display: flex;
  flex-flow: column;
}
.el-form-item{
  margin-bottom: 0px !important;
}
.el-pagination {
  margin-top: 15px;
}

.count {
  color: #eee;
  font-size: 12px;
  line-height: 35px;
}

.search-form {
  position: absolute;
  bottom: 10px;
  left: 18px;
}
.left-to-right-enter-active, .left-to-right-leave-active {
  transition: all 1s;
}
.left-to-right-enter, .left-to-right-leave-to /* .fade-leave-active below version 2.1.8 */ {
  transform: translateX(-100%);
  opacity: 0;
}

</style>