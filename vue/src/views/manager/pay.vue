<template >
  <div>
    <div class="card" style="margin-bottom: 10px;">
      <el-input  v-model="data.transactionId" :prefix-icon="Search" style="width: 250px; margin-right: 10px" placeholder="请输入支付订单号"></el-input>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>
    <div class="card" style="margin-bottom: 10px">
      <div style="margin-bottom: 10px">

      </div>

      <el-table stripe :data="data.tableData" ref="tableRef" >
        <el-table-column fixed="left" label="用户姓名" prop="user.name" width="150" ></el-table-column>
        <el-table-column label="支付方式" prop="user.paymentDetails"  width="150"></el-table-column>
        <el-table-column label="停车费用" prop="price" ></el-table-column>

        <el-table-column label="支付状态" prop="status" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'WAITING'" type="warning" effect="dark">未支付</el-tag>
            <el-tag v-else-if="scope.row.status === 'SUCCESS'" type="success" effect="dark">已支付</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="车牌号" prop="vehicleLog.licensePlate"  width="90"></el-table-column>
        <el-table-column prop="vehicleLog.inImageUrl" label="进库图片">
          <template #default="scope">
            <el-image
                preview-teleported="true"
                hide-on-click-modal="true"
                v-if="scope.row.vehicleLog.inImageUrl" :src="scope.row.vehicleLog.inImageUrl" :preview-src-list="[scope.row.vehicleLog.inImageUrl]"  style="width: 100%; height: auto; border-radius: 5px; object-fit: contain;"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="进库时间" prop="vehicleLog.entryTime"  width="170"></el-table-column>
        <el-table-column prop="vehicleLog.outImageUrl" label="出库图片">
          <template #default="scope">
            <el-image
                preview-teleported="true"
                hide-on-click-modal="true"
                v-if="scope.row.vehicleLog.outImageUrl" :src="scope.row.vehicleLog.outImageUrl" :preview-src-list="[scope.row.vehicleLog.outImageUrl]"  style="width: 100%; height: auto; border-radius: 5px; object-fit: contain;"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="出库时间" prop="vehicleLog.exitTime" width="170"></el-table-column>
        <el-table-column label="支付订单号" prop="transactionId" width="180" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" align="center" width="130px" fixed="right">
          <template #default="scope">
            <!--            <el-button type="danger" @click="handleDelete(scope.row.id)" >删除</el-button>-->
            <el-button type="warning" @click="handelOkPay(scope.row.transactionId)" v-if="scope.row.status === 'WAITING'">确认收费</el-button>

          </template>
        </el-table-column>
      </el-table>
    </div>


    <div class="card">
      <div style="margin-top: 10px">
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="data.pageNum"
            :page-sizes="[3, 5, 10, 15, 20]"
            :page-size="data.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="data.total">
        </el-pagination>
      </div>
    </div>



  </div>
</template>

<script setup>
import request from "@/utils/request";
import {reactive, ref} from "vue";
import {ElLoading, ElMessage, ElMessageBox} from "element-plus";
import {Search} from "@element-plus/icons-vue";
import {column} from "element-plus/es/components/table-v2/src/common";


//数据定义
const data = reactive({
  pageNum: 1,//当前页码

  form: {},
  tableData: [],
  total:0,
  pageSize:5,//一页的条数
  remarks:'',
  transactionId:'',

})


//分页查询加载
const load = () =>{
  request.get('/pay/selectPage',{
    params:{
      pageNum:data.pageNum,
      pageSize:data.pageSize,
      transactionId:data.transactionId,
    }
  }).then(res =>{
    if(res.code !== '200'){
      ElMessage.error(res.msg)
    }else{
      data.tableData = res.data?.list || []
      data.total = res.data?.total || 0
    }

  })
}
load()
//处理当前页的变化
const handleCurrentChange = (pageNum)=>{
  data.pageNum = pageNum
  load()
}
//处理每一页条数的变化
const handleSizeChange=(pageSize)=> {
  data.pageSize = pageSize
  load()
}
//重置搜索框
const reset = () =>{
  request.get('/pay/selectPage',{
    params:{
      pageNum:1,
      pageSize:data.pageSize,
      transactionId:'',
    }
  }).then(res =>{
    if(res.code !== '200'){
      ElMessage.error(res.msg)
    }else{
      console.log(res.data.list)
      data.transactionId=''
      data.tableData = res.data?.list || []
      data.total = res.data?.total || 0
    }

  })
}


const handelOkPay = (transactionId) =>{
  ElMessageBox.confirm('您确认支付吗?', '确认支付', { type: 'warning' }).then(res => {
    // location.href("http://localhost:9090/alipay/pay?transactionId=" + transactionId)
    // window.open("http://localhost:9090/alipay/pay?transactionId=" + transactionId)

 window.location.href = "http://localhost:9090/alipay/pay?transactionId=" + transactionId;
    /*request.get('/pay/okPay', {
      params: {
        transactionId: transactionId
      }
    }).then(res =>{
      if(res.code === '200'){
        ElMessage.success("确认支付成功")
        load()
      }else{
        ElMessage.error(res.msg)
      }
    })*/
  }).catch(err => {
    console.error(err)
    ElMessage({
      type:'info',
      message:'取消确认支付操作'
    })
  })

}
</script>

<style>

</style>