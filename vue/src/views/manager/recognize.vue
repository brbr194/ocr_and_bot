<template >
  <div>
    <div class="card" style="margin-bottom: 10px;">
      <el-input  v-model="data.licensePlate" :prefix-icon="Search" style="width: 250px; margin-right: 10px" placeholder="请输入车牌号"></el-input>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>
    <div class="card" style="margin-bottom: 10px">
      <div style="margin-bottom: 10px">
        <el-button type="primary" @click="handleAdd">登记入库</el-button>
        <el-button type="info" @click="handleExit">登记出库</el-button>
      </div>

      <el-table stripe :data="data.tableData" ref="tableRef" >
        <el-table-column label="车牌号" prop="licensePlate" ></el-table-column>
        <el-table-column prop="inImageUrl" label="进库图片">
          <template #default="scope">
            <el-image
                preview-teleported="true"
                hide-on-click-modal="true"
                v-if="scope.row.inImageUrl" :src="scope.row.inImageUrl" :preview-src-list="[scope.row.inImageUrl]"  style="width: 100%; height: auto; border-radius: 5px; object-fit: contain;"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="进库时间" prop="entryTime"></el-table-column>
        <el-table-column prop="outImageUrl" label="出库图片">
          <template #default="scope">
            <el-image
                preview-teleported="true"
                hide-on-click-modal="true"
                v-if="scope.row.outImageUrl" :src="scope.row.outImageUrl" :preview-src-list="[scope.row.outImageUrl]"  style="width: 100%; height: auto; border-radius: 5px; object-fit: contain;"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="出库时间" prop="exitTime"></el-table-column>
        <el-table-column label="操作" align="center" width="160">
          <template #default="scope">
<!--            <el-button type="danger" @click="handleDelete(scope.row.id)" >删除</el-button>-->
<!--            <el-button type="warning" @click="handelPay(scope.row)" v-if="scope.row.exitTime !== null && scope.row.outImageUrl !== null">计算收费</el-button>-->

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


    <el-dialog title="登记入库" width="50%" v-model="data.formVisible1" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="150px" style="padding-right: 50px" ref="formRef" >
        <el-form-item label="识别图像：" prop="imageUrl" required>
          <el-upload action="http://localhost:9090/upload" list-type="picture" :on-success="handleImgUploadSuccess">
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="data.formVisible1 = false">取 消</el-button>
        <el-button type="primary" @click="save1">确认识别</el-button>
      </span>
      </template>
    </el-dialog>

    <el-dialog title="登记出库" width="50%" v-model="data.formVisible2" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="150px" style="padding-right: 50px" ref="formRef" >
        <el-form-item label="识别图像：" prop="imageUrl" required>
          <el-upload action="http://localhost:9090/upload" list-type="picture" :on-success="handleImgUploadSuccess">
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="data.formVisible2 = false">取 消</el-button>
        <el-button type="primary" @click="save2">确认识别</el-button>
      </span>
      </template>
    </el-dialog>


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
  formVisible1: false,
  formVisible2: false,
  form: {},
  tableData: [],
  total:0,
  pageSize:5,//一页的条数
  remarks:'',
  licensePlate:'',

})

const handleAdd = () => {
  data.form = {}
  data.formVisible1 = true
}
const handleExit = () =>{
  data.form = {}
  data.formVisible2 = true
}


//分页查询加载
const load = () =>{
  request.get('/api/vehicles/selectPage',{
    params:{
      pageNum:data.pageNum,
      pageSize:data.pageSize,
      licensePlate:data.licensePlate,
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
  request.get('/api/vehicles/selectPage',{
    params:{
      pageNum:1,
      pageSize:data.pageSize,
      licensePlate:'',
    }
  }).then(res =>{
    if(res.code !== '200'){
      ElMessage.error(res.msg)
    }else{
      //console.log(res.data.list)
      data.licensePlate=''
      data.tableData = res.data?.list || []
      data.total = res.data?.total || 0
    }

  })
}


//把添加的信息保存到数据库
const save1 = () =>{
  const loading = ElLoading.service({
    lock: true,
    text: "正在识别，请稍后...",
    background: 'rgba(0, 0, 0, 0.7)'
  });
  console.log(data.form)
      request.get('/api/vehicles/entry',{
          params: {
            imageUrl: data.form.imageUrl
          }
      }).then(res =>{
        loading.close();
        if(res.code === '200'){
          load()
          ElMessage.success("操作成功")
          data.formVisible1 = false
        }else{
          loading.close(); //
          ElMessage.error(res.msg)
          data.formVisible1 = false
        }
      })
}

const save2 = () =>{
  let resLog = {}
  const loading = ElLoading.service({
    lock: true,
    text: "正在识别，请稍后...",
    background: 'rgba(0, 0, 0, 0.7)'
  });
  console.log(data.form)
  request.get('/api/vehicles/exit',{
    params: {
      imageUrl: data.form.imageUrl
    }
  }).then(res =>{
    loading.close();
    if(res.code === '200'){
      load()
      resLog = res.data
      console.log(res.data)
      console.log(resLog)
      request.post('/pay/leave',resLog).then(res =>{
            if(res.code === '200'){
              ElMessage.success("成功生成停车收费信息")
            }else{
              ElMessage.error(res.msg)
            }
          }
      )
      ElMessage.success("操作成功")
      data.formVisible2 = false
    }else{
      loading.close(); //
      ElMessage.error(res.msg)
      data.formVisible2 = false
    }
  })
  //存完出库后调用支付方法生成订单信息

}


const handleImgUploadSuccess =  (res) =>{
  if(res.code === '200'){
    data.form.imageUrl = res.data
    ElMessage.success("上传成功")
    console.log(res.data)
  }else{
    ElMessage.error(res.msg)
    data.formVisible1 = false
    data.formVisible2 = false
  }


}


</script>

<style>

</style>