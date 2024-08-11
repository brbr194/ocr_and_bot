<template>
  <div>

    <div class="card" style="line-height: 30px">
      <div>欢迎您，{{ user.name }} 祝您今天过得开心！</div>
    </div>
    <el-card style="width: 500px;margin-top: 20px">
      <div slot="header" class="clearfix">
        <span>2024</span>
      </div>
      <div>
        <div style="margin-top: 20px">
          <div style="margin: 10px 0"><strong>主题色</strong></div>
          <el-button type="primary">按钮</el-button>
          <el-button type="success">按钮</el-button>
          <el-button type="warning">按钮</el-button>
          <el-button type="danger">按钮</el-button>
          <el-button type="info">按钮</el-button>
        </div>
      </div>
    </el-card>

  </div>
</template>

<script setup>
  import request from "@/utils/request";
  const user = JSON.parse(localStorage.getItem('student-user') || '{}')
</script>